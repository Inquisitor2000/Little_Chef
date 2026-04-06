package com.familymealplanner.data.local.dao

import androidx.room.*
import com.familymealplanner.data.local.entity.GroceryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GroceryItemEntity>)

    @Update
    suspend fun update(item: GroceryItemEntity)

    @Query("DELETE FROM grocery_items WHERE id = :itemId")
    suspend fun deleteById(itemId: String)

    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, createdAt DESC")
    suspend fun getAll(): List<GroceryItemEntity>

    @Query("SELECT * FROM grocery_items ORDER BY isChecked ASC, createdAt DESC")
    fun observeAll(): Flow<List<GroceryItemEntity>>

    @Query("DELETE FROM grocery_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()

    @Query("DELETE FROM grocery_items WHERE isChecked = 1 AND checkedAt < :cutoffTime")
    suspend fun deleteOldCheckedItems(cutoffTime: Long)
}
