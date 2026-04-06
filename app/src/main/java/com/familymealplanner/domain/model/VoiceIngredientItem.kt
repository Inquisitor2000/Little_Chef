package com.familymealplanner.domain.model

/**
 * Represents an ingredient item from voice input with quantity selection.
 * Used in the new simplified voice input flow where users:
 * 1. Speak ingredient names only
 * 2. See matched ingredients with default units
 * 3. Select quantity for each using a counter
 * 
 * @param parsedName The ingredient name from voice input
 * @param matchedIngredient The matched catalog ingredient (null if not found)
 * @param quantity The selected quantity (default 1)
 * @param isRecognized True if matched to catalog, false if unrecognized
 */
data class VoiceIngredientItem(
    val parsedName: String,
    val matchedIngredient: CatalogIngredient?,
    val quantity: Int = 1,
    val isRecognized: Boolean
) {
    /**
     * Display name - uses catalog name if matched, otherwise parsed name
     */
    val displayName: String get() = matchedIngredient?.nameKey ?: parsedName
    
    /**
     * Default unit from catalog, or "pcs" if not matched
     */
    val defaultUnit: String get() = matchedIngredient?.defaultUnit ?: "pcs"
    
    /**
     * Category from catalog, or SNACKS if not matched
     */
    val category: IngredientCategory get() = 
        matchedIngredient?.category ?: IngredientCategory.SNACKS
    
    /**
     * Subcategory from catalog, or "Other" if not matched
     */
    val subcategory: String get() = matchedIngredient?.subcategory ?: "Other"
    
    /**
     * Allergens from catalog, or empty if not matched
     */
    val allergens: List<CommonAllergen> get() = 
        matchedIngredient?.allergens ?: emptyList()
}
