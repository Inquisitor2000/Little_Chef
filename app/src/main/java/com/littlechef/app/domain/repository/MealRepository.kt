package com.littlechef.app.domain.repository

import com.littlechef.app.domain.model.Meal
import kotlinx.coroutines.flow.Flow

data class MealIngredientInput(
    val ingredientId: String,
    val quantity: Double,
    val isStarIngredient: Boolean = false
)

interface MealRepository {
    suspend fun createMeal(meal: Meal, ingredients: List<MealIngredientInput>): Result<Unit>
    suspend fun getMealById(id: String): Meal?
    suspend fun getAllMeals(): List<Meal>
    fun observeAllMeals(): Flow<List<Meal>>
    fun observeScrapedMeals(): Flow<List<Meal>>
    suspend fun searchMeals(query: String): List<Meal>
    suspend fun updateMeal(meal: Meal, ingredients: List<MealIngredientInput>): Result<Unit>
    suspend fun updateMealImage(mealId: String, imagePath: String?): Result<Unit>
    suspend fun deleteMeal(meal: Meal)
}
