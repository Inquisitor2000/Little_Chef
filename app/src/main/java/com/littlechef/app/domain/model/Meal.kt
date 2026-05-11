package com.littlechef.app.domain.model

data class Meal(
    val id: String,
    val name: String,
    val instructions: String?,
    val simpleInstructions: String? = null,
    val prepTimeMinutes: Int?,
    val cookTimeMinutes: Int?,
    val servings: Int?,
    val isScraped: Boolean = false,
    val isBundled: Boolean = false,
    val imagePath: String? = null,
    val mealType: MealType? = null,
    val dishCategory: DishCategory? = null,
    val createdInLanguage: String = "en",
    val createdAt: Long,
    val updatedAt: Long,
    val ingredients: List<MealIngredient> = emptyList()
)

data class MealIngredient(
    val id: String,
    val ingredient: Ingredient,
    val quantity: Double,
    val unit: String?,
    val isStarIngredient: Boolean = false
)
