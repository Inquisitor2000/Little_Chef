package com.littlechef.app.domain.repository

import com.littlechef.app.domain.model.GroceryItem
import kotlinx.coroutines.flow.Flow

interface GroceryRepository {
    suspend fun addGroceryItem(item: GroceryItem)
    suspend fun addGroceryItems(items: List<GroceryItem>)
    suspend fun updateGroceryItem(item: GroceryItem)
    suspend fun deleteGroceryItem(itemId: String)
    suspend fun getGroceryItems(): List<GroceryItem>
    fun observeGroceryItems(): Flow<List<GroceryItem>>
    suspend fun clearCheckedItems()
    suspend fun cleanupOldItems()
}
