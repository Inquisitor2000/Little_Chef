package com.littlechef.app.domain.util

import com.littlechef.app.data.local.TranslationSystem

/**
 * Utility class for translating ingredient names within recipe instructions.
 * 
 * This helps translate ingredient names that appear in instruction text,
 * even when the instructions themselves are already translated but contain
 * English ingredient names.
 */
object InstructionTranslator {
    
    /**
     * Translates ingredient names found within instruction text.
     * 
     * This function attempts to identify and translate common ingredient names
     * that appear in recipe instructions. It uses word boundaries to avoid
     * translating parts of words.
     * 
     * @param instructions The instruction text (may contain English ingredient names)
     * @param translationSystem The translation system to use for lookups
     * @return The instructions with ingredient names translated
     */
    fun translateInstructions(
        instructions: String,
        translationSystem: TranslationSystem
    ): String {
        var result = instructions
        
        // Common ingredient patterns to translate
        // These are ingredients that commonly appear in instructions
        val commonIngredients = listOf(
            // Proteins
            "chicken", "beef", "pork", "fish", "shrimp", "salmon", "tuna",
            "bacon", "sausage", "ham", "turkey", "lamb", "pepperoni",
            
            // Dairy
            "milk", "butter", "cheese", "cream", "yogurt", "mozzarella",
            "parmesan", "cheddar", "feta", "ricotta",
            
            // Vegetables
            "onion", "garlic", "tomato", "potato", "carrot", "celery",
            "pepper", "mushroom", "spinach", "broccoli", "lettuce",
            "cucumber", "zucchini", "eggplant", "cabbage",
            
            // Herbs & Spices
            "oregano", "basil", "thyme", "rosemary", "parsley", "cilantro",
            "salt", "pepper", "paprika", "cumin", "cinnamon",
            
            // Grains & Bread
            "rice", "pasta", "bread", "flour", "noodles", "biscuit", "biscuits",
            
            // Sauces & Condiments
            "sauce", "oil", "vinegar", "soy sauce", "tomato sauce",
            "ketchup", "mustard", "mayonnaise",
            
            // Other
            "egg", "eggs", "sugar", "honey", "lemon", "lime", "orange",
            "seasoning"
        )
        
        // Translate compound phrases first (before individual words)
        // This prevents "tomato" and "sauce" from being translated separately
        val compoundIngredients = listOf(
            "refrigerated biscuit", "refrigerated biscuits",
            "tomato sauce", "soy sauce",
            "dried oregano", "dried basil", "dried thyme", "dried rosemary", "dried parsley",
            "italian seasoning", "italian sausage", "italian bread",
            "ground beef", "ground pork", "ground chicken", "ground turkey", "ground lamb",
            "chicken breast", "chicken thigh", "pork chop", "beef steak"
        )
        
        // Translate compound phrases first (case-insensitive)
        compoundIngredients.forEach { compound ->
            val translated = translationSystem.translateIngredient(compound)
            
            if (translated != compound) {
                // Case-insensitive replacement with case preservation
                val pattern = "\\b${Regex.escape(compound)}\\b".toRegex(RegexOption.IGNORE_CASE)
                result = pattern.replace(result) { matchResult ->
                    when {
                        // If first char is uppercase, capitalize translation
                        matchResult.value[0].isUpperCase() -> translated.replaceFirstChar { it.uppercase() }
                        else -> translated.lowercase()
                    }
                }
            }
        }
        
        // Then translate individual ingredients
        commonIngredients.forEach { ingredient ->
            val translated = translationSystem.translateIngredient(ingredient)
            
            // Only replace if translation is different (to avoid unnecessary replacements)
            if (translated != ingredient) {
                // Use word boundaries to avoid replacing parts of words
                // Case-insensitive replacement
                val pattern = "\\b${Regex.escape(ingredient)}\\b".toRegex(RegexOption.IGNORE_CASE)
                result = pattern.replace(result) { matchResult ->
                    // Preserve the original case pattern
                    when {
                        matchResult.value[0].isUpperCase() -> translated.replaceFirstChar { it.uppercase() }
                        else -> translated.lowercase()
                    }
                }
            }
        }
        
        return result
    }
}
