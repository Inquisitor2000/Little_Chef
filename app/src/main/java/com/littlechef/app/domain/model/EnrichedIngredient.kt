package com.littlechef.app.domain.model

/**
 * Represents a parsed ingredient enriched with catalog information and category inference.
 *
 * @param parsedIngredient Original parsed ingredient data (name only)
 * @param matchedCatalogIngredient Matched catalog entry (null if not found)
 * @param inferredCategory Category inferred from keywords (used if not matched)
 * @param allergens Allergens from catalog (empty if not matched)
 * @param isRecognized True if matched to catalog, false if custom ingredient
 * @param quantity Quantity value (default 1.0)
 * @param unit Unit of measurement (default from catalog or "pcs")
 */
data class EnrichedIngredient(
    val parsedIngredient: ParsedIngredient,
    val matchedCatalogIngredient: CatalogIngredient?,
    val inferredCategory: IngredientCategory,
    val allergens: List<CommonAllergen>,
    val isRecognized: Boolean,
    val quantity: Double = 1.0,
    val unit: String = "pcs"
) {
    /**
     * Convenience property for ingredient name.
     */
    val name: String get() = parsedIngredient.name
    
    /**
     * Convenience property for category.
     * Uses catalog category if matched, otherwise uses inferred category.
     */
    val category: IngredientCategory get() = 
        matchedCatalogIngredient?.category ?: inferredCategory
    
    /**
     * Convenience property for subcategory.
     * Uses catalog subcategory if matched, otherwise returns "Other".
     */
    val subcategory: String get() = 
        matchedCatalogIngredient?.subcategory ?: "Other"
}

/**
 * Represents a parsed ingredient from input.
 * Contains the raw ingredient name as parsed.
 *
 * @property name The ingredient name
 */
data class ParsedIngredient(
    val name: String
)
