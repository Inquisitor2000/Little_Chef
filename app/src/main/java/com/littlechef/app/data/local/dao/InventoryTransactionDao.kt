package com.littlechef.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.littlechef.app.data.local.entity.InventoryTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryTransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: InventoryTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<InventoryTransactionEntity>)

    @Update
    suspend fun update(transaction: InventoryTransactionEntity)

    @Delete
    suspend fun delete(transaction: InventoryTransactionEntity)

    @Query("SELECT * FROM inventory_transactions WHERE id = :id")
    suspend fun getById(id: String): InventoryTransactionEntity?

    @Query("SELECT * FROM inventory_transactions WHERE ingredient_id = :ingredientId")
    suspend fun getByIngredientId(ingredientId: String): List<InventoryTransactionEntity>

    @Query("SELECT * FROM inventory_transactions WHERE ingredient_id = :ingredientId")
    fun observeByIngredientId(ingredientId: String): Flow<List<InventoryTransactionEntity>>

    @Query("SELECT * FROM inventory_transactions WHERE meal_plan_id = :mealPlanId")
    suspend fun getByMealPlanId(mealPlanId: String): List<InventoryTransactionEntity>

    @Query("SELECT * FROM inventory_transactions WHERE ingredient_id = :ingredientId AND status = :status")
    suspend fun getByIngredientIdAndStatus(ingredientId: String, status: String): List<InventoryTransactionEntity>

    @Query("SELECT SUM(quantity_change) FROM inventory_transactions WHERE ingredient_id = :ingredientId AND status = 'COMMITTED'")
    suspend fun getCommittedQuantityByIngredientId(ingredientId: String): Double?

    @Query("SELECT SUM(quantity_change) FROM inventory_transactions WHERE ingredient_id = :ingredientId AND status = 'RESERVED'")
    suspend fun getReservedQuantityByIngredientId(ingredientId: String): Double?

    @Query("SELECT * FROM inventory_transactions WHERE created_at > :timestamp")
    suspend fun getCreatedSince(timestamp: Long): List<InventoryTransactionEntity>

    @Query("SELECT * FROM inventory_transactions")
    suspend fun getAll(): List<InventoryTransactionEntity>

    @Query("SELECT * FROM inventory_transactions")
    fun observeAll(): Flow<List<InventoryTransactionEntity>>
}
