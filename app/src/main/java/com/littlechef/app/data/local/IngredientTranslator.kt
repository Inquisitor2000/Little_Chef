package com.littlechef.app.data.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles translation of ingredient names from English to other supported languages.
 * 
 * This class:
 * - Loads ingredient translations from JSON files in assets/translations/
 * - Provides translation lookup with English fallback
 * - Caches translations in memory for performance
 * - Handles errors gracefully without crashing the app
 */
@Singleton
class IngredientTranslator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val translationCache: MutableMap<String, String> = mutableMapOf()
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val TAG = "IngredientTranslator"
        private const val TRANSLATIONS_PATH = "translations"
    }
    
    /**
     * Loads ingredient translations from assets/translations/ingredients_{languageCode}.json
     * 
     * @param languageCode The language code (e.g., "ru", "ro")
     */
    fun loadTranslations(languageCode: String) {
        if (languageCode == "en") {
            return
        }
        
        val fileName = "ingredients_$languageCode.json"
        val filePath = "$TRANSLATIONS_PATH/$fileName"
        
        try {
            val jsonString = context.assets.open(filePath)
                .bufferedReader()
                .use { it.readText() }
            
            if (jsonString.isBlank()) {
                Log.w(TAG, "✗ Ingredients: Translation file empty: $filePath")
                translationCache.clear()
                return
            }
            
            val translations = json.decodeFromString<Map<String, String>>(jsonString)
            
            translationCache.clear()
            translationCache.putAll(translations)
            
        } catch (e: java.io.FileNotFoundException) {
            Log.e(TAG, "✗ Ingredients: Translation file not found: $filePath")
            translationCache.clear()
        } catch (e: Exception) {
            Log.e(TAG, "✗ Ingredients: Error loading translations: ${e.message}")
            translationCache.clear()
        }
    }
    
    /**
     * Translates an ingredient name from English to the current language.
     * 
     * @param englishName The English name of the ingredient
     * @return The translated name if available, otherwise the English name
     */
    fun translate(englishName: String): String {
        val translated = translationCache[englishName]
        
        if (translated == null) {
            // Try case-insensitive lookup
            val caseInsensitiveMatch = translationCache.entries.firstOrNull { 
                it.key.equals(englishName, ignoreCase = true) 
            }
            
            if (caseInsensitiveMatch != null) {
                return caseInsensitiveMatch.value
            }
            
            return englishName
        }
        
        return translated
    }
    
    /**
     * Clears the in-memory translation cache.
     * Should be called when changing languages to free memory.
     */
    fun clearCache() {
        translationCache.clear()
    }
}
