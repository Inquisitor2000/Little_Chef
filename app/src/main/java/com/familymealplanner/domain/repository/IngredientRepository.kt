package com.familymealplanner.domain.repository

import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.model.IngredientSubstitute
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    /**
     * Creates a new ingredient with the specified properties.
     *
     * @param name The name of the ingredient
     * @param unit The unit of measurement for the ingredient
     * @param category The category of the ingredient (e.g., "Vegetables", "Dairy & Eggs")
     * @param subcategory The subcategory of the ingredient (e.g., "Leafy Greens", "Milk")
     * @param userId The ID of the user creating the ingredient
     * @param preferredDisplayUnit The user's preferred unit for displaying this ingredient (e.g., "kg", "g", "L", "ml")
     * @return Result containing the created Ingredient on success, or an error on failure
     */
    suspend fun createIngredient(
        name: String,
        unit: String,
        category: String?,
        subcategory: String?,
        preferredDisplayUnit: String? = null
    ): Result<Ingredient>
    
    suspend fun getIngredientById(id: String): Ingredient?
    suspend fun getIngredientByName(name: String): Ingredient?
    suspend fun getAllIngredients(): List<Ingredient>
    fun observeAllIngredients(): Flow<List<Ingredient>>
    suspend fun getIngredientsByCategory(category: String): List<Ingredient>
    suspend fun searchIngredients(query: String): List<Ingredient>
    suspend fun updateIngredient(ingredient: Ingredient, allergenNames: List<String>)
    suspend fun deleteIngredient(ingredient: Ingredient): Result<Unit>
    suspend fun addSubstitute(ingredientId: String, substituteId: String, notes: String?)
    suspend fun removeSubstitute(ingredientId: String, substituteId: String)
    suspend fun getSubstitutesForIngredient(ingredientId: String): List<IngredientSubstitute>
}
