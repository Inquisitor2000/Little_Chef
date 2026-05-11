package com.littlechef.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.littlechef.app.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: MealEntity)

    @Update
    suspend fun update(meal: MealEntity)

    @Delete
    suspend fun delete(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getById(id: String): MealEntity?

    @Query("SELECT * FROM meals WHERE id = :id")
    fun observeById(id: String): Flow<MealEntity?>

    @Query("SELECT * FROM meals")
    suspend fun getAll(): List<MealEntity>

    @Query("SELECT * FROM meals")
    fun observeAll(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE is_scraped = 1 ORDER BY created_at DESC")
    fun observeScrapedMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE is_scraped = 0")
    fun observeNonScrapedMeals(): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<MealEntity>

    @Query("SELECT * FROM meals WHERE updated_at > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<MealEntity>

    @Query("UPDATE meals SET image_path = :imagePath, updated_at = :updatedAt WHERE id = :mealId")
    suspend fun updateImagePath(mealId: String, imagePath: String?, updatedAt: Long)
}
