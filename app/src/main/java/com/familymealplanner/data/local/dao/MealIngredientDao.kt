package com.familymealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.familymealplanner.data.local.entity.MealIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealIngredientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mealIngredient: MealIngredientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mealIngredients: List<MealIngredientEntity>)

    @Delete
    suspend fun delete(mealIngredient: MealIngredientEntity)

    @Query("DELETE FROM meal_ingredients WHERE meal_id = :mealId")
    suspend fun deleteByMealId(mealId: String)

    @Query("SELECT * FROM meal_ingredients WHERE meal_id = :mealId")
    suspend fun getByMealId(mealId: String): List<MealIngredientEntity>

    @Query("SELECT * FROM meal_ingredients WHERE meal_id = :mealId")
    fun observeByMealId(mealId: String): Flow<List<MealIngredientEntity>>

    @Query("SELECT * FROM meal_ingredients WHERE ingredient_id = :ingredientId")
    suspend fun getByIngredientId(ingredientId: String): List<MealIngredientEntity>

    @Query("SELECT COUNT(*) FROM meal_ingredients WHERE ingredient_id = :ingredientId")
    suspend fun countByIngredientId(ingredientId: String): Int

    @Query("SELECT * FROM meal_ingredients")
    suspend fun getAll(): List<MealIngredientEntity>

    @Query("SELECT * FROM meal_ingredients WHERE id > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<MealIngredientEntity>
}
