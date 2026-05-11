package com.littlechef.app.data.repository

import com.littlechef.app.data.local.dao.GroceryItemDao
import com.littlechef.app.data.local.entity.toEntity
import com.littlechef.app.domain.model.GroceryItem
import com.littlechef.app.domain.repository.GroceryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroceryRepositoryImpl @Inject constructor(
    private val groceryItemDao: GroceryItemDao
) : GroceryRepository {

    override suspend fun addGroceryItem(item: GroceryItem) {
        groceryItemDao.insert(item.toEntity())
    }

    override suspend fun addGroceryItems(items: List<GroceryItem>) {
        groceryItemDao.insertAll(items.map { it.toEntity() })
    }

    override suspend fun updateGroceryItem(item: GroceryItem) {
        groceryItemDao.update(item.toEntity())
    }

    override suspend fun deleteGroceryItem(itemId: String) {
        groceryItemDao.deleteById(itemId)
    }

    override suspend fun getGroceryItems(): List<GroceryItem> {
        return groceryItemDao.getAll().map { it.toDomain() }
    }

    override fun observeGroceryItems(): Flow<List<GroceryItem>> {
        return groceryItemDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun clearCheckedItems() {
        groceryItemDao.deleteCheckedItems()
    }

    override suspend fun cleanupOldItems() {
        val twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L)
        groceryItemDao.deleteOldCheckedItems(twoDaysAgo)
    }
}
