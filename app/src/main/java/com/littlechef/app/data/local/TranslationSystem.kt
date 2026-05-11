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
     * Initializes the translation system by loading all translations for the specified language.
     * 
     * This method should be called during app startup with the user's saved language preference.
     * 
     * @param languageCode The language code to load translations for (e.g., "en", "ru", "ro")
     */
    fun initialize(languageCode: String) {
        currentLanguage = languageCode
        
        try {
            ingredientTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load ingredient translations during initialization for $languageCode: ${e.message}. Continuing with English fallback.", e)
        }
        
        try {
            categoryTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load category translations during initialization for $languageCode: ${e.message}. Continuing with English fallback.", e)
        }
        
        // RecipeTranslator loads on-demand, no initialization needed
    }
    
    /**
     * Reloads all translations for a new language.
     * 
     * This method clears all translation caches and loads translations for the new language.
     * Should be called when the user changes their language preference.
     * 
     * @param languageCode The new language code to load translations for
     */
    fun reloadTranslations(languageCode: String) {
        currentLanguage = languageCode
        
        try {
            // Clear all caches
            ingredientTranslator.clearCache()
            recipeTranslator.clearCache()
            categoryTranslator.clearCache()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing translation caches: ${e.message}", e)
        }
        
        try {
            // Load new translations
            ingredientTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reload ingredient translations for $languageCode: ${e.message}. Continuing with English fallback.", e)
        }
        
        try {
            categoryTranslator.loadTranslations(languageCode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reload category translations for $languageCode: ${e.message}. Continuing with English fallback.", e)
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
