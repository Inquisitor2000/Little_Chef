package com.familymealplanner.domain.repository

import com.familymealplanner.domain.model.InventoryTransaction
import com.familymealplanner.domain.model.PantryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    suspend fun createTransaction(transaction: InventoryTransaction)
    suspend fun createTransactions(transactions: List<InventoryTransaction>)
    suspend fun updateTransaction(transaction: InventoryTransaction)
    suspend fun getTransactionsByIngredientId(ingredientId: String): List<InventoryTransaction>
    suspend fun getTransactionsByMealPlanId(mealPlanId: String): List<InventoryTransaction>
    suspend fun getAvailableQuantity(ingredientId: String): Double
    suspend fun getReservedQuantity(ingredientId: String): Double
    suspend fun getPantryItems(): List<PantryItem>
    fun observePantryItems(): Flow<List<PantryItem>>
}
