package com.familymealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.familymealplanner.data.local.entity.IngredientSubstituteEntity

@Dao
interface IngredientSubstituteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(substitute: IngredientSubstituteEntity)

    @Delete
    suspend fun delete(substitute: IngredientSubstituteEntity)

    @Query("SELECT * FROM ingredient_substitutes WHERE ingredient_id = :ingredientId")
    suspend fun getByIngredientId(ingredientId: String): List<IngredientSubstituteEntity>

    @Query("SELECT * FROM ingredient_substitutes WHERE substitute_id = :substituteId")
    suspend fun getBySubstituteId(substituteId: String): List<IngredientSubstituteEntity>

    @Query("DELETE FROM ingredient_substitutes WHERE ingredient_id = :ingredientId")
    suspend fun deleteByIngredientId(ingredientId: String)

    @Query("SELECT * FROM ingredient_substitutes")
    suspend fun getAll(): List<IngredientSubstituteEntity>

    @Query("SELECT * FROM ingredient_substitutes WHERE created_at > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<IngredientSubstituteEntity>
}
