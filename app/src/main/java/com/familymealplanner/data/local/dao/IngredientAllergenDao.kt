package com.familymealplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.familymealplanner.data.local.entity.IngredientAllergenEntity

@Dao
interface IngredientAllergenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredientAllergen: IngredientAllergenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredientAllergens: List<IngredientAllergenEntity>)

    @Delete
    suspend fun delete(ingredientAllergen: IngredientAllergenEntity)

    @Query("DELETE FROM ingredient_allergens WHERE ingredient_id = :ingredientId")
    suspend fun deleteByIngredientId(ingredientId: String)

    @Query("SELECT * FROM ingredient_allergens WHERE ingredient_id = :ingredientId")
    suspend fun getByIngredientId(ingredientId: String): List<IngredientAllergenEntity>

    @Query("SELECT * FROM ingredient_allergens WHERE allergen_id = :allergenId")
    suspend fun getByAllergenId(allergenId: String): List<IngredientAllergenEntity>

    @Query("SELECT * FROM ingredient_allergens")
    suspend fun getAll(): List<IngredientAllergenEntity>

    @Query("SELECT * FROM ingredient_allergens WHERE id > :timestamp")
    suspend fun getChangedSince(timestamp: Long): List<IngredientAllergenEntity>
}
