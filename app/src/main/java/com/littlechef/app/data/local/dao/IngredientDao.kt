package com.littlechef.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.littlechef.app.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: IngredientEntity)

    @Update
    suspend fun update(ingredient: IngredientEntity)

    @Delete
    suspend fun delete(ingredient: IngredientEntity)

    @Query("SELECT * FROM ingredients WHERE id = :id")
    suspend fun getById(id: String): IngredientEntity?

    @Query("SELECT * FROM ingredients WHERE id = :id")
    fun observeById(id: String): Flow<IngredientEntity?>

    @Query("SELECT * FROM ingredients")
    suspend fun getAll(): List<IngredientEntity>

    @Query("SELECT * FROM ingredients")
    fun observeAll(): Flow<List<IngredientEntity>>

    @Query("SELECT * FROM ingredients WHERE category = :category")
    suspend fun getByCategory(category: String): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<IngredientEntity>

    @Query("SELECT * FROM ingredients WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getByName(name: String): IngredientEntity?

    @Query("SELECT * FROM ingredients WHERE updated_at > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<IngredientEntity>
}
