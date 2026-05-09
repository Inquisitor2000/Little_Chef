package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.model.InventoryTransaction
import com.familymealplanner.domain.model.MealPlan
import com.familymealplanner.domain.model.MealPlanStatus
import com.familymealplanner.domain.model.NonDeductibleIngredients
import com.familymealplanner.domain.model.TransactionStatus
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.repository.MealPlanRepository
import com.familymealplanner.domain.model.roundEggQuantity
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

        // Calculate servings multiplier based on planned servings
        val servingsMultiplier = mealPlan.meal.servings?.let { originalServings ->
            val plannedServings = mealPlan.plannedServings ?: originalServings
            plannedServings.toDouble() / originalServings.toDouble()
        } ?: 1.0

        // Check ingredient availability (excluding other reservations) - only for deductible ingredients
        // Use substituted ingredients if they've been applied
        val shortages = mutableListOf<InsufficientIngredient>()
        for (mealIngredient in deductibleIngredients) {
            // Check if a substitute has been applied for this ingredient
            val ingredientToCheck = mealPlan.ingredientSubstitutions[mealIngredient.ingredient.id]?.let { substituteId ->
                // Find the substitute ingredient
                mealIngredient.ingredient.substitutes.find { it.substituteIngredient.id == substituteId }?.substituteIngredient
            } ?: mealIngredient.ingredient
            
            // Adjust quantity based on servings
            val requiredQuantity = roundEggQuantity(mealIngredient.quantity * servingsMultiplier, mealIngredient.ingredient.name)
            
            val available = inventoryRepository.getAvailableQuantity(ingredientToCheck.id)
            if (available < requiredQuantity) {
                // Check if there's a substitute available (only if not already using one)
                val substitute = if (ingredientToCheck.id == mealIngredient.ingredient.id) {
                    mealIngredient.ingredient.substitutes.firstOrNull { sub ->
                        val subAvailable = inventoryRepository.getAvailableQuantity(sub.substituteIngredient.id)
                        subAvailable >= requiredQuantity
                    }?.substituteIngredient
                } else {
                    null // Already using a substitute
                }
                
                shortages.add(
                    InsufficientIngredient(
                        ingredientName = mealIngredient.ingredient.name,
                        required = requiredQuantity,
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
            
            // Adjust quantity based on servings
            val adjustedQuantity = roundEggQuantity(mealIngredient.quantity * servingsMultiplier, mealIngredient.ingredient.name)
            
            InventoryTransaction(
                id = UUID.randomUUID().toString(),
                ingredientId = ingredientToDeduct.id,
                quantityChange = -adjustedQuantity,
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
