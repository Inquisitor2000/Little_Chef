package com.familymealplanner.domain.model

data class GroceryItem(
    val id: String,
    val ingredientName: String,
    val ingredientId: String? = null, // ID of the ingredient in the database (null for custom items)
    val category: String? = null, // Category of the ingredient (from catalog or database)
    val quantity: Double,
    val unit: String,
    val mealName: String,
    val mealType: MealType? = null, // Type of meal (breakfast, lunch, dinner)
    val plannedDate: Long? = null, // When the meal is planned to be made
    val isChecked: Boolean = false,
    val checkedAt: Long? = null, // Timestamp when item was checked
    val createdAt: Long
)
