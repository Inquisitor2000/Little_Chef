package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.model.InventoryTransaction
import com.familymealplanner.domain.model.MealPlan
import com.familymealplanner.domain.model.MealPlanStatus
import com.familymealplanner.domain.model.NonDeductibleIngredients
import com.familymealplanner.domain.model.TransactionStatus
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.repository.MealPlanRepository
import java.util.UUID
import javax.inject.Inject

class StartCookingUseCase @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val inventoryRepository: InventoryRepository
) {
    data class InsufficientIngredient(
        val ingredientName: String,
        val required: Double,
        val available: Double,
        val unit: String,
        val substitute: Ingredient? = null
    )

    sealed class Result {
        data class Success(val mealPlan: MealPlan) : Result()
        data class InsufficientIngredients(val shortages: List<InsufficientIngredient>) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(
        mealPlanId: String
    ): Result {
        val mealPlan = mealPlanRepository.getById(mealPlanId)
            ?: return Result.Error("Meal plan not found")

        // Check if meal plan is in correct state
        if (mealPlan.status != MealPlanStatus.PLANNED) {
            return Result.Error("Meal plan must be in PLANNED status to start cooking")
        }

        // Check if the meal is planned for today
        val today = java.time.LocalDate.now()
        val plannedDate = java.time.Instant.ofEpochMilli(mealPlan.plannedDate)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        
        if (plannedDate != today) {
            return Result.Error("You can only start cooking meals planned for today")
        }

        // Filter out non-deductible ingredients (oils, cooking spray, non-deductible units like pcs, tsp, etc.)
        val deductibleIngredients = mealPlan.meal.ingredients.filter { 
            NonDeductibleIngredients.shouldDeduct(it.ingredient.name, it.ingredient.unit)
        }

        // Check ingredient availability (excluding other reservations) - only for deductible ingredients
        // Use substituted ingredients if they've been applied
        val shortages = mutableListOf<InsufficientIngredient>()
        for (mealIngredient in deductibleIngredients) {
            // Check if a substitute has been applied for this ingredient
            val ingredientToCheck = mealPlan.ingredientSubstitutions[mealIngredient.ingredient.id]?.let { substituteId ->
                // Find the substitute ingredient
                mealIngredient.ingredient.substitutes.find { it.substituteIngredient.id == substituteId }?.substituteIngredient
            } ?: mealIngredient.ingredient
            
            val available = inventoryRepository.getAvailableQuantity(ingredientToCheck.id)
            if (available < mealIngredient.quantity) {
                // Check if there's a substitute available (only if not already using one)
                val substitute = if (ingredientToCheck.id == mealIngredient.ingredient.id) {
                    mealIngredient.ingredient.substitutes.firstOrNull { sub ->
                        val subAvailable = inventoryRepository.getAvailableQuantity(sub.substituteIngredient.id)
                        subAvailable >= mealIngredient.quantity
                    }?.substituteIngredient
                } else {
                    null // Already using a substitute
                }
                
                shortages.add(
                    InsufficientIngredient(
                        ingredientName = mealIngredient.ingredient.name,
                        required = mealIngredient.quantity,
                        available = available,
                        unit = mealIngredient.ingredient.unit,
                        substitute = substitute
                    )
                )
            }
        }

        if (shortages.isNotEmpty()) {
            return Result.InsufficientIngredients(shortages)
        }

        // Create reservation transactions only for deductible ingredients
        // Use substituted ingredients if they've been applied
        val now = System.currentTimeMillis()
        val reservationTransactions = deductibleIngredients.map { mealIngredient ->
            // Check if a substitute has been applied for this ingredient
            val ingredientToDeduct = mealPlan.ingredientSubstitutions[mealIngredient.ingredient.id]?.let { substituteId ->
                // Find the substitute ingredient
                mealIngredient.ingredient.substitutes.find { it.substituteIngredient.id == substituteId }?.substituteIngredient
            } ?: mealIngredient.ingredient
            
            InventoryTransaction(
                id = UUID.randomUUID().toString(),
                ingredientId = ingredientToDeduct.id,
                quantityChange = -mealIngredient.quantity,
                status = TransactionStatus.RESERVED,
                reason = "Cooking: ${mealPlan.meal.name}",
                mealPlanId = mealPlanId,
                createdAt = now,
                updatedAt = now
            )
        }

        inventoryRepository.createTransactions(reservationTransactions)

        // Update meal plan status to COOKING
        val updatedMealPlan = mealPlan.copy(
            status = MealPlanStatus.COOKING,
            startedAt = now,
            updatedAt = now
        )

        mealPlanRepository.update(updatedMealPlan)

        return Result.Success(updatedMealPlan)
    }
}
