package com.littlechef.app.domain.model

/**
 * Nutritional information per 100g (or per piece for pcs-unit ingredients).
 */
data class NutritionInfo(
    val calories: Double,
    val fatsG: Double,
    val carbsG: Double,
    val proteinG: Double,
    /** Grams per piece — only for ingredients typically measured in "pcs" */
    val pieceG: Double? = null
) {
    companion object {
        val EMPTY = NutritionInfo(0.0, 0.0, 0.0, 0.0)
    }
}
