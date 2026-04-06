package com.familymealplanner.domain.usecase

import com.familymealplanner.data.local.BundledRecipeLoader
import com.familymealplanner.data.local.TranslationSystem
import com.familymealplanner.domain.model.Allergen
import com.familymealplanner.domain.model.Cuisine
import com.familymealplanner.domain.util.IngredientMatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Preloads allergen data for all cuisines in the background on app launch.
 * This prevents frame drops when users navigate to cuisine screens.
 */
@Singleton
class PreloadCuisineAllergensUseCase @Inject constructor(
    private val bundledRecipeLoader: BundledRecipeLoader,
    private val translationSystem: TranslationSystem,
    private val ingredientMatcher: IngredientMatcher
) {
    private val preloadScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    // Shared cache accessible by CuisineMealsViewModel
    companion object {
        val allergenCache = mutableMapOf<String, List<Allergen>>()
        @Volatile
        var isPreloaded = false
    }
    
    /**
     * Start preloading allergen data for all cuisines in the background.
     * This should be called once on app launch.
     */
    fun preload() {
        if (isPreloaded) return
        
        preloadScope.launch {
            try {
                val currentLanguage = translationSystem.getCurrentLanguage()
                
                // Load all recipes from all cuisines
                Cuisine.entries.forEach { cuisine ->
                    val bundledRecipes = bundledRecipeLoader.loadRecipesForCuisine(cuisine, currentLanguage)
                    
                    // Process each ingredient to build allergen cache
                    bundledRecipes.forEach { recipe ->
                        recipe.ingredients.forEach { ingredient ->
                            val ingredientName = ingredient.name.trim().lowercase()
                            
                            // Skip if already cached
                            if (allergenCache.containsKey(ingredientName)) {
                                return@forEach
                            }
                            
                            // Translate and match ingredient
                            val translatedName = translationSystem.translateIngredient(ingredient.name)
                            val matchResult = ingredientMatcher.findMatch(translatedName, threshold = 0.6)
                            val catalogIngredient = matchResult?.catalogIngredient
                            
                            // Cache allergens
                            val allergens = if (catalogIngredient != null && catalogIngredient.allergens.isNotEmpty()) {
                                catalogIngredient.allergens.map { commonAllergen ->
                                    Allergen(
                                        id = commonAllergen.name.lowercase(),
                                        name = commonAllergen.displayName,
                                        createdAt = 0,
                                        updatedAt = 0
                                    )
                                }
                            } else {
                                emptyList()
                            }
                            
                            allergenCache[ingredientName] = allergens
                        }
                    }
                }
                
                isPreloaded = true
            } catch (e: Exception) {
                // Silently fail - allergens will be loaded on demand
            }
        }
    }
}
