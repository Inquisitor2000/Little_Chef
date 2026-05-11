package com.littlechef.app.ui.util

import com.littlechef.app.data.local.NutritionLoader
import com.littlechef.app.domain.model.NutritionInfo

/**
 * Calculates per-serving nutritional totals for a recipe.
 *
 * For each ingredient with known nutrition data:
 * - "g" / "ml" units → contribution = qty × (per100g / 100)
 * - "pcs" units → contribution = qty × pieceG × (per100g / 100)
 *
 * All contributions are summed, then divided by servings.
 */
object NutritionCalculator {

    /**
     * Calculates per-serving nutrition for a list of ingredients.
     *
     * @param ingredients List of (name, quantity, unit) tuples for the recipe.
     * @param servings Number of servings to divide by.
     * @param loader NutritionLoader instance (must be loaded).
     * @return Per-serving [NutritionInfo], or [NutritionInfo.EMPTY] if no data available.
     */
    fun calculate(
        ingredients: List<IngredientPortion>,
        servings: Int,
        loader: NutritionLoader
    ): NutritionInfo {
        if (!loader.isLoaded || servings <= 0) return NutritionInfo.EMPTY

        var totalCalories = 0.0
        var totalFats = 0.0
        var totalCarbs = 0.0
        var totalProtein = 0.0
        var hasData = false

        for (ingredient in ingredients) {
            val nutrition = loader.getNutrition(ingredient.name) ?: continue
            hasData = true

            val grams = when (ingredient.unit.lowercase()) {
                "g" -> ingredient.quantity
                "ml" -> ingredient.quantity // 1ml ≈ 1g for nutrition purposes
                "pcs" -> {
                    val pieceG = nutrition.pieceG ?: continue
                    ingredient.quantity * pieceG
                }
                else -> continue
            }

            if (grams <= 0) continue

            val factor = grams / 100.0
            totalCalories += nutrition.calories * factor
            totalFats += nutrition.fatsG * factor
            totalCarbs += nutrition.carbsG * factor
            totalProtein += nutrition.proteinG * factor
        }

        if (!hasData) return NutritionInfo.EMPTY

        val s = servings.toDouble()
        return NutritionInfo(
            calories = totalCalories / s,
            fatsG = totalFats / s,
            carbsG = totalCarbs / s,
            proteinG = totalProtein / s
        )
    }

    data class IngredientPortion(
        val name: String,
        val quantity: Double,
        val unit: String
    )
}
