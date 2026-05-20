package com.littlechef.app.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.local.BundledRecipe
import com.littlechef.app.data.local.BundledRecipeLoader
import com.littlechef.app.domain.model.Allergen
import com.littlechef.app.domain.model.CatalogIngredient
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.domain.model.MealType
import com.littlechef.app.domain.repository.IngredientRepository
import com.littlechef.app.domain.usecase.PreloadCuisineAllergensUseCase
import com.littlechef.app.domain.util.IngredientMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

/**
 * Singleton object for catalog lookup map to avoid rebuilding it multiple times.
 * Built once and shared across all ViewModel instances.
 */
object CatalogLookupCache {
    val map: Map<String, CatalogIngredient> by lazy {
        val result = mutableMapOf<String, CatalogIngredient>()
        
        // Build exact match map
        com.littlechef.app.domain.model.IngredientCatalog.allIngredients.forEach { ingredient ->
            val normalizedName = ingredient.nameKey.trim().lowercase()
            result[normalizedName] = ingredient
        }
        
        result
    }
}

data class RecipeWithAllergens(
    val recipe: BundledRecipe,
    val allergens: List<Allergen>
)

/**
 * ViewModel for cuisine meals screen with performance optimizations:
 * 1. Shared catalog lookup map for O(1) ingredient lookups
 * 2. Ingredient allergen caching to avoid repeated queries
 * 3. Recipe preloading capability for instant navigation
 */
@HiltViewModel
class CuisineMealsViewModel @Inject constructor(
    private val bundledRecipeLoader: BundledRecipeLoader,
    private val translationSystem: com.littlechef.app.data.local.TranslationSystem,
    private val ingredientRepository: IngredientRepository,
    private val onboardingPreferences: com.littlechef.app.data.preferences.OnboardingPreferences,
    private val ingredientMatcher: IngredientMatcher,
    private val preloadCuisineAllergensUseCase: PreloadCuisineAllergensUseCase,
    private val favoriteRecipesPreferences: com.littlechef.app.data.preferences.FavoriteRecipesPreferences,
    private val analyticsService: com.littlechef.app.data.analytics.AnalyticsService
) : ViewModel() {
    
    private val _recipes = MutableStateFlow<List<RecipeWithAllergens>>(emptyList())
    val recipes: StateFlow<List<RecipeWithAllergens>> = _recipes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _defaultServingSize = MutableStateFlow(2)
    val defaultServingSize: StateFlow<Int> = _defaultServingSize
    
    private val _favoriteRecipeIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteRecipeIds: StateFlow<Set<String>> = _favoriteRecipeIds
    
    private val _favoriteRecipeIdsOrdered = MutableStateFlow<List<String>>(emptyList())
    val favoriteRecipeIdsOrdered: StateFlow<List<String>> = _favoriteRecipeIdsOrdered
    
    // Use shared allergen cache from preloader
    private val allergenCache get() = PreloadCuisineAllergensUseCase.allergenCache
    
    // Cache for preloaded recipes by cuisine (shared across instances via companion object)
    companion object {
        private const val TAG = "CuisineMealsViewModel"
        private val preloadedRecipes = mutableMapOf<Cuisine, List<RecipeWithAllergens>>()
        private val preloadingInProgress = mutableSetOf<Cuisine>()
    }
    
    init {
        // Load default serving size from preferences
        viewModelScope.launch {
            onboardingPreferences.defaultServingSize.collect { servingSize ->
                _defaultServingSize.value = servingSize
            }
        }
        
        // Load favorite recipe IDs
        viewModelScope.launch {
            favoriteRecipesPreferences.favoriteRecipeIds.collect { favorites ->
                _favoriteRecipeIds.value = favorites
            }
        }
        
        // Load ordered favorite recipe IDs
        viewModelScope.launch {
            favoriteRecipesPreferences.favoriteRecipeIdsOrdered.collect { orderedFavorites ->
                _favoriteRecipeIdsOrdered.value = orderedFavorites
            }
        }
        
        // Preload all cuisines in background on app launch
        preloadAllCuisinesInBackground()
    }
    
    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            favoriteRecipesPreferences.toggleFavorite(recipeId)
        }
    }
    
    /**
     * Preload all cuisines in the background on app launch.
     * This runs on a background dispatcher to avoid blocking the main thread.
     */
    private fun preloadAllCuisinesInBackground() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentLanguage = translationSystem.getCurrentLanguage()
            
            // Preload all cuisines
            Cuisine.entries.forEach { cuisine ->
                // Skip if already loaded or loading
                if (preloadedRecipes.containsKey(cuisine) || preloadingInProgress.contains(cuisine)) {
                    return@forEach
                }
                
                preloadingInProgress.add(cuisine)
                
                try {
                    val bundledRecipes = bundledRecipeLoader.loadRecipesForCuisine(cuisine, currentLanguage)
                    val recipesWithAllergens = processRecipesWithAllergens(bundledRecipes)
                    preloadedRecipes[cuisine] = recipesWithAllergens
                } catch (e: Exception) {
                    // Silently fail - will load on demand if preload fails
                } finally {
                    preloadingInProgress.remove(cuisine)
                }
            }
        }
    }
    
    /**
     * Preload recipes for a cuisine in the background without showing loading state.
     * This is called when user hovers or is likely to navigate to a cuisine.
     */
    fun preloadRecipes(cuisine: Cuisine) {
        // Don't preload if already loaded or currently loading
        if (preloadedRecipes.containsKey(cuisine) || preloadingInProgress.contains(cuisine)) {
            return
        }
        
        preloadingInProgress.add(cuisine)
        
        viewModelScope.launch {
            // Load recipes in the current language
            val currentLanguage = translationSystem.getCurrentLanguage()
            val bundledRecipes = bundledRecipeLoader.loadRecipesForCuisine(cuisine, currentLanguage)
            val recipesWithAllergens = processRecipesWithAllergens(bundledRecipes)
            
            preloadedRecipes[cuisine] = recipesWithAllergens
            preloadingInProgress.remove(cuisine)
        }
    }
    
    fun loadRecipes(cuisine: Cuisine) {
        analyticsService.trackCuisineBrowsed(cuisineName = cuisine.name)
        viewModelScope.launch {
            val perfStart = System.currentTimeMillis()
            
            // Check if recipes are already preloaded
            val preloaded = preloadedRecipes[cuisine]
            if (preloaded != null) {
                _recipes.value = preloaded
                return@launch
            }
            
            _isLoading.value = true
            
            // Load and process recipes in background dispatcher
            val recipesWithAllergens = withContext(Dispatchers.Default) {
                // Load recipes in the current language
                val currentLanguage = translationSystem.getCurrentLanguage()
                val bundledRecipes = bundledRecipeLoader.loadRecipesForCuisine(cuisine, currentLanguage)
                
                // Process allergens in background (uses shared cache)
                processRecipesWithAllergens(bundledRecipes)
            }
            
            // Update UI on main thread
            _recipes.value = recipesWithAllergens
            _isLoading.value = false
        }
    }
    
    /**
     * Process recipes and add allergen information.
     * Extracted to be reusable by both loadRecipes and preloadRecipes.
     */
    private suspend fun processRecipesWithAllergens(bundledRecipes: List<BundledRecipe>): List<RecipeWithAllergens> {
        var catalogLookups = 0
        var cacheHits = 0
        
        val recipesWithAllergensResult = bundledRecipes.map { recipe ->
            val allergens = mutableSetOf<Allergen>()
            
            // Look up each ingredient and collect its allergens
            recipe.ingredients.forEach { bundledIngredient ->
                val ingredientName = bundledIngredient.name.trim().lowercase()
                
                // Check cache first
                val cachedAllergens = allergenCache[ingredientName]
                if (cachedAllergens != null) {
                    cacheHits++
                    allergens.addAll(cachedAllergens)
                } else {
                    catalogLookups++
                    // Only use catalog lookup (fast, no database queries)
                    val catalogAllergens = findAllergensFromCatalog(bundledIngredient.name.trim())
                    allergenCache[ingredientName] = catalogAllergens
                    allergens.addAll(catalogAllergens)
                }
            }
            
            RecipeWithAllergens(recipe, allergens.toList())
        }
        
        return recipesWithAllergensResult
    }
    
    /**
     * Find allergens from the ingredient catalog using fuzzy matching.
     * Uses fuzzy matching to handle ingredient name variations and typos.
     * Translates ingredient names to current language before matching.
     * Returns a list of Allergen objects if found, empty list otherwise.
     */
    private fun findAllergensFromCatalog(ingredientName: String): List<Allergen> {
        // Translate the ingredient name to current language before matching
        // This handles bundled recipes which have English names
        val translatedName = translationSystem.translateIngredient(ingredientName)
        
        // Use fuzzy matching to handle variations in ingredient names
        val matchResult = ingredientMatcher.findMatch(translatedName, threshold = 0.6)
        val catalogIngredient = matchResult?.catalogIngredient
        
        // Convert catalog allergens to Allergen objects
        return if (catalogIngredient != null && catalogIngredient.allergens.isNotEmpty()) {
            catalogIngredient.allergens.map { commonAllergen: com.littlechef.app.domain.model.CommonAllergen ->
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
    }
    
    /**
     * Determines the appropriate meal type based on current time:
     * - Breakfast: 06:00 - 10:00
     * - Lunch: 10:00 - 14:00
     * - Snack: 14:00 - 16:00
     * - Dessert: 16:00 - 18:00
     * - Dinner: 18:00 - 21:00
     * - Outside these ranges: null (no auto-scroll)
     */
    fun getCurrentMealType(): MealType? {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        
        val result = when (hourOfDay) {
            in 6..9 -> MealType.BREAKFAST
            in 10..13 -> MealType.LUNCH
            in 14..15 -> MealType.SNACK
            in 16..17 -> MealType.DESSERT
            in 18..20 -> MealType.DINNER
            else -> null
        }
        
        return result
    }
}
