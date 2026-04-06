package com.familymealplanner.data.local

import android.content.Context
import com.familymealplanner.domain.model.Cuisine
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BundledRecipe(
    val id: String,
    val name: String,
    val instructions: String,
    val simpleInstructions: String,
    val prepTimeMinutes: Int?,
    val cookTimeMinutes: Int?,
    val servings: Int,
    val cuisine: String,
    val imageUrl: String? = null,
    val mealType: String? = null,
    val dishCategory: String? = null,
    val ingredients: List<BundledIngredient>
)

@Serializable
data class BundledIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val isStarIngredient: Boolean = false
)

@Singleton
class BundledRecipeLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    fun loadRecipesForCuisine(cuisine: Cuisine, languageCode: String = "en"): List<BundledRecipe> {
        val folderName = "recipes/${cuisine.displayName.lowercase()}"
        return try {
            val files = context.assets.list(folderName) ?: emptyArray()
            
            // Filter files based on language
            val relevantFiles = if (languageCode == "en") {
                // For English, load files without language suffix
                files.filter { it.endsWith(".json") && !it.contains("_ru.json") && !it.contains("_ro.json") }
            } else {
                // For other languages, try to load language-specific files first
                val translatedFiles = files.filter { it.endsWith("_$languageCode.json") }
                if (translatedFiles.isNotEmpty()) {
                    translatedFiles
                } else {
                    // Fallback to English if no translations available
                    files.filter { it.endsWith(".json") && !it.contains("_ru.json") && !it.contains("_ro.json") }
                }
            }
            
            relevantFiles.mapNotNull { fileName ->
                try {
                    val content = context.assets.open("$folderName/$fileName")
                        .bufferedReader()
                        .use { it.readText() }
                    json.decodeFromString<BundledRecipe>(content)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun loadAllBundledRecipes(languageCode: String = "en"): Map<Cuisine, List<BundledRecipe>> {
        return Cuisine.entries.associateWith { loadRecipesForCuisine(it, languageCode) }
    }
}
