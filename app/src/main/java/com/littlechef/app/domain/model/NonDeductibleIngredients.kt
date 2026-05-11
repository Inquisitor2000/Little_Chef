package com.littlechef.app.domain.model

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
}
