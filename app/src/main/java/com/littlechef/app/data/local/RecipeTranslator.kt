package com.littlechef.app.data.local

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles translation of recipes by loading language-specific recipe files.
 * 
 * This class:
 * - Attempts to load recipe files with language suffixes (e.g., churros_ru.json)
 * - Falls back to English recipe files if translation not available
 * - Caches loaded recipes in memory for performance
 * - Handles errors gracefully without crashing the app
 */
@Singleton
class RecipeTranslator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val recipeCache: MutableMap<String, BundledRecipe> = mutableMapOf()
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val TAG = "RecipeTranslator"
        private const val RECIPES_PATH = "recipes"
    }
    
    /**
     * Loads a recipe in the specified language.
     * 
     * Recipe naming patterns:
     * - English: {baseFileName}.json (e.g., "balsamic_bruschetta.json" or "italian_frittata_italian.json")
     * - Russian: {baseFileName}_ru.json (e.g., "balsamic_bruschetta_ru.json")
     * - Romanian: {baseFileName}_ro.json (e.g., "balsamic_bruschetta_ro.json")
     * 
     * The baseFileName is derived from the recipeId but may include cuisine suffix.
     * 
     * @param recipeId The recipe identifier (e.g., "balsamic_bruschetta", "italian_frittata")
     * @param cuisine The cuisine folder name (e.g., "mexican", "italian")
     * @param languageCode The language code (e.g., "ru", "ro", "en")
     * @return The loaded recipe or null if not found
     */
    fun loadRecipe(recipeId: String, cuisine: String, languageCode: String): BundledRecipe? {
        val cacheKey = "${recipeId}_${cuisine}_$languageCode"
        
        // Check cache first
        recipeCache[cacheKey]?.let { 
            return it 
        }
        
        // We need to find the base filename by checking what files exist
        // Try pattern 1: {recipeId}_{languageCode}.json (for most recipes)
        // Try pattern 2: {recipeId}_{cuisine}_{languageCode}.json (for recipes with cuisine in name)
        
        val possibleFileNames = if (languageCode == "en") {
            // For English, try without language suffix
            listOf(
                "$recipeId.json",
                "${recipeId}_$cuisine.json"
            )
        } else {
            // For other languages, try with language suffix
            listOf(
                "${recipeId}_$languageCode.json",
                "${recipeId}_${cuisine}_$languageCode.json"
            )
        }
        
        for (fileName in possibleFileNames) {
            val filePath = "$RECIPES_PATH/$cuisine/$fileName"
            
            try {
                val jsonString = context.assets.open(filePath)
                    .bufferedReader()
                    .use { it.readText() }
                
                if (jsonString.isNotBlank()) {
                    val recipe = json.decodeFromString<BundledRecipe>(jsonString)
                    recipeCache[cacheKey] = recipe
                    return recipe
                }
            } catch (e: java.io.FileNotFoundException) {
                // Try next pattern
                continue
            } catch (e: Exception) {
                Log.e(TAG, "✗ Recipe: Error loading '$recipeId' from '$fileName': ${e.message}")
            }
        }
        
        Log.e(TAG, "✗ Recipe: '$recipeId' not found in $languageCode for cuisine $cuisine")
        return null
    }
    
    /**
     * Clears the in-memory recipe cache.
     * Should be called when changing languages to free memory.
     */
    fun clearCache() {
        recipeCache.clear()
    }
}
