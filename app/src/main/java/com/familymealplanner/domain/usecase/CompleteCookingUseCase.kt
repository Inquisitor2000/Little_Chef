package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.MealPlan
import com.familymealplanner.domain.model.MealPlanStatus
import com.familymealplanner.domain.model.TransactionStatus
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.repository.MealPlanRepository
import javax.inject.Inject

class CompleteCookingUseCase @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val inventoryRepository: InventoryRepository
) {
    sealed class Result {
        data class Success(val mealPlan: MealPlan) : Result()
        data class Error(val message: String) : Result()
    }

    suspend operator fun invoke(
        mealPlanId: String
    ): Result {
        val mealPlan = mealPlanRepository.getById(mealPlanId)
            ?: return Result.Error("Meal plan not found")

        // Check if meal plan is in correct state
        if (mealPlan.status != MealPlanStatus.COOKING) {
            return Result.Error("Meal plan must be in COOKING status to complete")
        }

        // Get all reservation transactions for this meal plan
        val reservationTransactions = inventoryRepository.getTransactionsByMealPlanId(mealPlanId)
            .filter { it.status == TransactionStatus.RESERVED }

        // Update all reservation transactions to COMMITTED
        val now = System.currentTimeMillis()
        for (transaction in reservationTransactions) {
            val updatedTransaction = transaction.copy(
                status = TransactionStatus.COMMITTED,
                updatedAt = now
            )
            inventoryRepository.updateTransaction(updatedTransaction)
        }

        // Update meal plan status to COMPLETED
        val updatedMealPlan = mealPlan.copy(
            status = MealPlanStatus.COMPLETED,
            completedAt = now,
            updatedAt = now
        )

        mealPlanRepository.update(updatedMealPlan)

        return Result.Success(updatedMealPlan)
    }
}
