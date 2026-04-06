package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.MealPlan
import com.familymealplanner.domain.model.MealPlanStatus
import com.familymealplanner.domain.model.MealType
import com.familymealplanner.domain.model.NonDeductibleIngredients
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.repository.MealPlanRepository
import com.familymealplanner.domain.repository.MealRepository
import java.util.UUID
import javax.inject.Inject

class CreateMealPlanUseCase @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val mealRepository: MealRepository,
    private val inventoryRepository: InventoryRepository
) {
    data class InsufficientIngredient(
        val ingredientName: String,
        val required: Double,
        val available: Double
    )

    sealed class Result {
        data class Success(val mealPlan: MealPlan) : Result()
        data class InsufficientIngredients(val shortages: List<InsufficientIngredient>) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(
        mealId: String,
        plannedDate: Long,
        mealType: MealType
    ): Result {
        val meal = mealRepository.getMealById(mealId)
            ?: return Result.Error("Meal not found")

        // Filter out non-deductible ingredients (oils, cooking spray, non-deductible units like pcs, tsp, etc.)
        val deductibleIngredients = meal.ingredients.filter {
            NonDeductibleIngredients.shouldDeduct(it.ingredient.name, it.ingredient.unit)
        }

        // Check ingredient availability - only for deductible ingredients
        val shortages = mutableListOf<InsufficientIngredient>()
        for (mealIngredient in deductibleIngredients) {
            val available = inventoryRepository.getAvailableQuantity(mealIngredient.ingredient.id)
            if (available < mealIngredient.quantity) {
                shortages.add(
                    InsufficientIngredient(
                        ingredientName = mealIngredient.ingredient.name,
                        required = mealIngredient.quantity,
                        available = available
                    )
                )
            }
        }

        // Create the meal plan regardless of ingredient availability
        val now = System.currentTimeMillis()
        val mealPlan = MealPlan(
            id = UUID.randomUUID().toString(),
            meal = meal,
            plannedDate = plannedDate,
            mealType = mealType,
            status = MealPlanStatus.PLANNED,
            startedAt = null,
            completedAt = null,
            createdAt = now,
            updatedAt = now
        )

        mealPlanRepository.create(mealPlan)
        
        // Return success with shortage info if there are missing ingredients
        return if (shortages.isNotEmpty()) {
            Result.InsufficientIngredients(shortages)
        } else {
            Result.Success(mealPlan)
        }
    }
}
