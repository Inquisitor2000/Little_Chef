package com.littlechef.app.domain.model

/**
 * Represents an ingredient in the meal planning system.
 *
 * @property id Unique identifier for the ingredient
 * @property name Display name of the ingredient
 * @property unit Default unit of measurement (e.g., "cup", "gram", "piece")
 * @property category Top-level category (e.g., "Vegetables", "Dairy & Eggs")
 * @property subcategory Subcategory within the category (e.g., "Leafy Greens", "Milk")
 * @property preferredDisplayUnit User's preferred unit for displaying this ingredient (e.g., "kg", "g", "L", "ml")
 * @property createdInLanguage Language code in which this ingredient was created (e.g., "en", "ru", "ro")
 * @property createdAt Timestamp when the ingredient was created
 * @property updatedAt Timestamp when the ingredient was last updated
 * @property allergens List of allergens associated with this ingredient
 * @property substitutes List of possible substitutes for this ingredient
 */
data class Ingredient(
    val id: String,
    val name: String,
    val unit: String,
    val category: String?,
    val subcategory: String?,
    val preferredDisplayUnit: String? = null,
    val createdInLanguage: String = "en",
    val createdAt: Long,
    val updatedAt: Long,
    val allergens: List<Allergen> = emptyList(),
    val substitutes: List<IngredientSubstitute> = emptyList()
)

data class IngredientSubstitute(
    val id: String,
    val substituteIngredient: Ingredient,
    val notes: String?,
    val createdAt: Long
)
