package com.familymealplanner.domain.model

/**
 * Helper object for ingredient deduction logic.
 * 
 * ALL INGREDIENTS ARE NOW DEDUCTIBLE from pantry inventory when cooking.
 * This includes salt, pepper, oils, spices, and all other ingredients.
 * 
 * All units (g, kg, ml, L, pcs) are tracked and deducted from inventory.
 */
object NonDeductibleIngredients {
    
    /**
     * Check if an ingredient should be deducted from inventory.
     * Always returns true - all ingredients are deductible.
     * 
     * @param ingredientName The name of the ingredient
     * @param unit The unit of measurement (optional)
     * @return true (all ingredients are deductible)
     */
    fun shouldDeduct(ingredientName: String, unit: String? = null): Boolean {
        return true // All ingredients are deductible
    }
    
    /**
     * Check if an ingredient is non-deductible by name only.
     * @param ingredientName The name of the ingredient
     * @return false (all ingredients are deductible)
     * @deprecated All ingredients are now deductible
     */
    @Deprecated("All ingredients are now deductible")
    fun isNonDeductibleByName(ingredientName: String): Boolean {
        return false
    }
}
