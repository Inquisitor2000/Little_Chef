package com.familymealplanner.domain.model

import com.familymealplanner.domain.util.ParsedIngredient

/**
 * Represents a parsed ingredient enriched with catalog information and category inference.
 * 
 * NOTE: This model is deprecated for voice input. Use VoiceIngredientItem instead.
 * This is kept for backward compatibility with other features.
 * 
 * @param parsedIngredient Original parsed data from VoiceInputParser (name only)
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
