package com.littlechef.app.data.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates all translation operations for the app.
 * 
 * This class:
 * - Manages IngredientTranslator, RecipeTranslator, and CategoryTranslator
 * - Loads all translations at app startup
 * - Provides a unified API for translation lookups
 * - Handles language changes by reloading translations
 * - Ensures consistent translation behavior across the app
 */
@Singleton
class TranslationSystem @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ingredientTranslator: IngredientTranslator,
    private val recipeTranslator: RecipeTranslator,
    private val categoryTranslator: CategoryTranslator
) {
    private var currentLanguage: String = "en"
    
    companion object {
        private const val TAG = "TranslationSystem"
    }
    
    /**
     * Sets the current language without loading any translation data.
     * Must be called synchronously at startup so that [getCurrentLanguage] 
     * returns the correct value immediately for recipe loading decisions.
     */
    fun setLanguage(languageCode: String) {
        currentLanguage = languageCode
    }
    
    /**
     * Loads translation data for the specified language into memory.
     * This performs I/O (reads and parses JSON files from assets) and should
     * be called off the main thread. The translation caches are not needed
     * for startup — all callers fall back to English gracefully if the cache
     * is empty. Only ingredient and category name lookups depend on this data;
     * recipe language selection uses [getCurrentLanguage] instead.
     *
     * @param languageCode The language code to load translations for (e.g., "en", "ru", "ro")
     */
    fun loadTranslationData(languageCode: String) {
        if (languageCode == "en") return
        
        try {
            ingredientTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load ingredient translations for $languageCode: ${e.message}. Continuing with English fallback.", e)
        }
        
        try {
            categoryTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load category translations for $languageCode: ${e.message}. Continuing with English fallback.", e)
        }
        
        // RecipeTranslator loads on-demand, no initialization needed
    }
    
    /**
     * Translates an ingredient name from English to the current language.
     * 
     * @param englishName The English name of the ingredient
     * @return The translated name if available, otherwise the English name
     */
    fun translateIngredient(englishName: String): String {
        return ingredientTranslator.translate(englishName)
    }
    
    /**
     * Loads a recipe in the current language.
     * 
     * Attempts to load the translated version first, falls back to English if not available.
     * 
     * @param recipeId The recipe identifier (e.g., "churros")
     * @param cuisine The cuisine folder name (e.g., "mexican", "italian")
     * @return The loaded recipe or null if not found
     */
    fun translateRecipe(recipeId: String, cuisine: String): BundledRecipe? {
        return recipeTranslator.loadRecipe(recipeId, cuisine, currentLanguage)
    }
    
    /**
     * Translates a category name from English to the current language.
     * 
     * @param englishName The English name of the category
     * @return The translated name if available, otherwise the English name
     */
    fun translateCategory(englishName: String): String {
        return categoryTranslator.translate(englishName)
    }
    
    /**
     * Gets the current language code.
     * 
     * @return The current language code (e.g., "en", "ru", "ro")
     */
    fun getCurrentLanguage(): String {
        return currentLanguage
    }
}
