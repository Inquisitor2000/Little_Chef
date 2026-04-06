package com.familymealplanner.domain.usecase

import com.familymealplanner.domain.model.InventoryTransaction
import com.familymealplanner.domain.model.TransactionStatus
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.repository.InventoryRepository
import java.util.UUID
import javax.inject.Inject

class AdjustInventoryUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        ingredientId: String,
        quantityChange: Double,
        reason: String
    ) {
        val now = System.currentTimeMillis()
        val transaction = InventoryTransaction(
            id = UUID.randomUUID().toString(),
            ingredientId = ingredientId,
            quantityChange = quantityChange,
            status = TransactionStatus.COMMITTED,
            reason = reason,
            mealPlanId = null,
            createdAt = now,
            updatedAt = now
        )
        
        inventoryRepository.createTransaction(transaction)
        
        // Update ingredient's updatedAt timestamp when quantity changes
        val ingredient = ingredientRepository.getIngredientById(ingredientId)
        if (ingredient != null) {
            val updatedIngredient = ingredient.copy(
                updatedAt = now
            )
            val allergenNames = ingredient.allergens.map { it.name }
            ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
        }
    }
}
