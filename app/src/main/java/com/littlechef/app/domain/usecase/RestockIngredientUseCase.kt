package com.littlechef.app.domain.usecase

import com.littlechef.app.domain.model.InventoryTransaction
import com.littlechef.app.domain.model.TransactionStatus
import com.littlechef.app.domain.repository.IngredientRepository
import com.littlechef.app.domain.repository.InventoryRepository
import java.util.UUID
import javax.inject.Inject

class RestockIngredientUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val ingredientRepository: IngredientRepository
) {
    suspend operator fun invoke(
        ingredientId: String,
        quantity: Double,
        reason: String = "Restock"
    ) {
        android.util.Log.d("RestockIngredientUseCase", "=== Restocking ingredient ===")
        android.util.Log.d("RestockIngredientUseCase", "Ingredient ID: $ingredientId")
        android.util.Log.d("RestockIngredientUseCase", "Quantity to add: $quantity")
        android.util.Log.d("RestockIngredientUseCase", "Reason: $reason")
        
        // Get current ingredient info for logging
        val ingredient = ingredientRepository.getIngredientById(ingredientId)
        if (ingredient != null) {
            android.util.Log.d("RestockIngredientUseCase", "Ingredient: ${ingredient.name} (${ingredient.unit})")
            
            // Get current available quantity
            val currentQuantity = inventoryRepository.getAvailableQuantity(ingredientId)
            android.util.Log.d("RestockIngredientUseCase", "Current pantry quantity: $currentQuantity ${ingredient.unit}")
            android.util.Log.d("RestockIngredientUseCase", "After restock will have: ${currentQuantity + quantity} ${ingredient.unit}")
        } else {
            android.util.Log.w("RestockIngredientUseCase", "Could not find ingredient with ID: $ingredientId")
        }
        
        val now = System.currentTimeMillis()
        val transaction = InventoryTransaction(
            id = UUID.randomUUID().toString(),
            ingredientId = ingredientId,
            quantityChange = quantity,
            status = TransactionStatus.COMMITTED,
            reason = reason,
            mealPlanId = null,
            createdAt = now,
            updatedAt = now
        )
        
        android.util.Log.d("RestockIngredientUseCase", "Creating inventory transaction: ${transaction.id}")
        
        inventoryRepository.createTransaction(transaction)
        
        android.util.Log.d("RestockIngredientUseCase", "Transaction created successfully")
        
        // Update ingredient's updatedAt timestamp when quantity changes
        if (ingredient != null) {
            val updatedIngredient = ingredient.copy(
                updatedAt = now
            )
            val allergenNames = ingredient.allergens.map { it.name }
            ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
            
            android.util.Log.d("RestockIngredientUseCase", "Updated ingredient timestamp")
        }
        
        android.util.Log.d("RestockIngredientUseCase", "=== Restock completed ===")
    }
}
