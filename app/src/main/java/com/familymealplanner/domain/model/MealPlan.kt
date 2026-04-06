package com.familymealplanner.domain.model

data class MealPlan(
    val id: String,
    val meal: Meal,
    val plannedDate: Long,
    val mealType: MealType,
    val status: MealPlanStatus,
    val startedAt: Long?,
    val completedAt: Long?,
    val createdAt: Long,
    val updatedAt: Long,
    // Map of original ingredient ID to substitute ingredient ID
    val ingredientSubstitutions: Map<String, String> = emptyMap(),
    // Number of servings planned (defaults to recipe's original servings)
    val plannedServings: Int? = null,
    // Adjusted times based on servings (stored when meal plan is created)
    val adjustedPrepTimeMinutes: Int? = null,
    val adjustedCookTimeMinutes: Int? = null
)

enum class MealType(val displayName: String, val emoji: String) {
    BREAKFAST("Breakfast", "🍳"),
    LUNCH("Lunch", "🥗"),
    DINNER("Dinner", "🍽️"),
    SNACK("Snack", "🍿"),
    DESSERT("Dessert", "🍰");
    
    fun getLocalizedName(context: android.content.Context): String {
        return when (this) {
            BREAKFAST -> context.getString(com.familymealplanner.R.string.meal_type_breakfast)
            LUNCH -> context.getString(com.familymealplanner.R.string.meal_type_lunch)
            DINNER -> context.getString(com.familymealplanner.R.string.meal_type_dinner)
            SNACK -> context.getString(com.familymealplanner.R.string.meal_type_snack)
            DESSERT -> context.getString(com.familymealplanner.R.string.meal_type_dessert)
        }
    }
}

enum class MealPlanStatus {
    PLANNED,
    COOKING,
    COMPLETED,
    ABORTED
}
