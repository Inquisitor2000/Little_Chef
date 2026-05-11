package com.littlechef.app.data.repository

import com.littlechef.app.data.local.dao.IngredientDao
import com.littlechef.app.data.local.dao.InventoryTransactionDao
import com.littlechef.app.data.local.dao.MealPlanDao
import com.littlechef.app.data.local.entity.InventoryTransactionEntity
import com.littlechef.app.domain.model.InventoryTransaction
import com.littlechef.app.domain.model.PantryItem
import com.littlechef.app.domain.model.Reservation
import com.littlechef.app.domain.model.TransactionStatus
import com.littlechef.app.domain.repository.IngredientRepository
import com.littlechef.app.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val inventoryTransactionDao: InventoryTransactionDao,
    private val ingredientDao: IngredientDao,
    private val mealPlanDao: MealPlanDao,
    private val ingredientRepository: IngredientRepository,
    private val mealDao: com.littlechef.app.data.local.dao.MealDao
) : InventoryRepository {

    override suspend fun createTransaction(transaction: InventoryTransaction) {
        inventoryTransactionDao.insert(transaction.toEntity())
    }

    override suspend fun createTransactions(transactions: List<InventoryTransaction>) {
        inventoryTransactionDao.insertAll(transactions.map { it.toEntity() })
    }

    override suspend fun updateTransaction(transaction: InventoryTransaction) {
        inventoryTransactionDao.update(transaction.toEntity())
    }

    override suspend fun getTransactionsByIngredientId(ingredientId: String): List<InventoryTransaction> {
        return inventoryTransactionDao.getByIngredientId(ingredientId).map { it.toDomain() }
    }

    override suspend fun getTransactionsByMealPlanId(mealPlanId: String): List<InventoryTransaction> {
        return inventoryTransactionDao.getByMealPlanId(mealPlanId).map { it.toDomain() }
    }

    override suspend fun getAvailableQuantity(ingredientId: String): Double {
        return inventoryTransactionDao.getCommittedQuantityByIngredientId(ingredientId) ?: 0.0
    }

    override suspend fun getReservedQuantity(ingredientId: String): Double {
        val reserved = inventoryTransactionDao.getReservedQuantityByIngredientId(ingredientId) ?: 0.0
        return kotlin.math.abs(reserved) // Reservations are negative, return absolute value
    }

    override suspend fun getPantryItems(): List<PantryItem> {
        val allIngredients = ingredientRepository.getAllIngredients()
        
        return allIngredients.map { ingredient ->
            val availableQty = getAvailableQuantity(ingredient.id)
            val reservedQty = getReservedQuantity(ingredient.id)
            
            // Get reservation details
            val reservedTransactions = inventoryTransactionDao.getByIngredientIdAndStatus(
                ingredient.id,
                TransactionStatus.RESERVED.name
            )
            
            val reservations = reservedTransactions.mapNotNull { transaction ->
                val mealPlanId = transaction.mealPlanId ?: return@mapNotNull null
                val mealPlan = mealPlanDao.getById(mealPlanId) ?: return@mapNotNull null
                val meal = mealDao.getById(mealPlan.mealId) ?: return@mapNotNull null
                
                Reservation(
                    mealPlanId = mealPlanId,
                    mealName = meal.name,
                    quantity = kotlin.math.abs(transaction.quantityChange) // Use absolute value
                )
            }
            
            PantryItem(
                ingredient = ingredient,
                availableQuantity = availableQty,
                reservedQuantity = reservedQty,
                reservations = reservations
            )
        }
    }

    override fun observePantryItems(): Flow<List<PantryItem>> {
        // Observe both transactions and ingredients to update when either changes
        return kotlinx.coroutines.flow.combine(
            ingredientDao.observeAll(),
            inventoryTransactionDao.observeAll()
        ) { _, _ ->
            getPantryItems()
        }
    }

    private fun InventoryTransactionEntity.toDomain(): InventoryTransaction {
        return InventoryTransaction(
            id = id,
            ingredientId = ingredientId,
            quantityChange = quantityChange,
            status = TransactionStatus.valueOf(status),
            reason = reason,
            mealPlanId = mealPlanId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun InventoryTransaction.toEntity(): InventoryTransactionEntity {
        return InventoryTransactionEntity(
            id = id,
            ingredientId = ingredientId,
            quantityChange = quantityChange,
            status = status.name,
            reason = reason,
            mealPlanId = mealPlanId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
