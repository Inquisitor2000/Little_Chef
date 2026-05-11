package com.littlechef.app.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.local.BundledRecipe
import com.littlechef.app.data.local.BundledRecipeLoader
import com.littlechef.app.data.preferences.OnboardingPreferences
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.model.PantryItem
import com.littlechef.app.domain.repository.IngredientRepository
import com.littlechef.app.domain.repository.InventoryRepository
import com.littlechef.app.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class representing a meal suggestion with match percentage
 */
sealed class MealSuggestion {
    abstract val matchPercentage: Int
    abstract val availableIngredients: Int
    abstract val totalIngredients: Int
    abstract val missingIngredientNames: List<String>
    
    /**
     * Get meal type as string for filtering
     */
    val mealTypeString: String?
        get() = when (this) {
            is UserMeal -> meal.mealType?.name
            is BundledMeal -> recipe.mealType
        }
    
    /**
     * Get dish category as string for filtering
     */
    val dishCategoryString: String?
        get() = when (this) {
            is UserMeal -> meal.dishCategory?.name
            is BundledMeal -> recipe.dishCategory
        }
    
    /**
     * User-created meal (manual or scraped)
     */
    data class UserMeal(
        val meal: Meal,
        override val matchPercentage: Int,
        override val availableIngredients: Int,
        override val totalIngredients: Int,
        override val missingIngredientNames: List<String>
    ) : MealSuggestion()
    
    /**
     * Bundled recipe from assets
     */
    data class BundledMeal(
        val recipe: BundledRecipe,
        val cuisine: Cuisine,
        override val matchPercentage: Int,
        override val availableIngredients: Int,
        override val totalIngredients: Int,
        override val missingIngredientNames: List<String>
    ) : MealSuggestion()
}

/**
 * UI state for the Suggestion screen
 */
sealed interface SuggestionUiState {
    data object Loading : SuggestionUiState
    data class Success(
        val allPerfectMatches: List<MealSuggestion>,
        val allGoodMatches: List<MealSuggestion>,
        val allPartialMatches: List<MealSuggestion>,
        val filteredPerfectMatches: List<MealSuggestion>,
        val filteredGoodMatches: List<MealSuggestion>,
        val filteredPartialMatches: List<MealSuggestion>,
        val selectedMealType: com.littlechef.app.domain.model.MealType? = null,
        val selectedDishCategory: com.littlechef.app.domain.model.DishCategory? = null
    ) : SuggestionUiState
    data class Error(val message: String) : SuggestionUiState
}

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val bundledRecipeLoader: BundledRecipeLoader,
    private val inventoryRepository: InventoryRepository,
    private val ingredientRepository: IngredientRepository,
    private val preferences: OnboardingPreferences,
    private val substituteInitializer: com.littlechef.app.data.local.SubstituteInitializer
) : ViewModel() {

    companion object {
        private const val TAG = "SuggestionViewModel"
    }

    private val _uiState = MutableStateFlow<SuggestionUiState>(SuggestionUiState.Loading)
    val uiState: StateFlow<SuggestionUiState> = _uiState.asStateFlow()

    /**
     * Load meal suggestions based on pantry availability
     */
    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                _uiState.value = SuggestionUiState.Loading
                
                // Initialize substitutes first to ensure they're in the database
                substituteInitializer.initialize(forceCheck = true)
                
                // Load pantry items
                val pantryItems = inventoryRepository.getPantryItems()
                
                // Load user-created meals (exclude bundled ones)
                val userMeals = mealRepository.getAllMeals().filter { !it.isBundled }
                
                // Get current language code
                val languageCode = java.util.Locale.getDefault().language
                
                // Load all bundled recipes with current language
                val bundledRecipesByCuisine = bundledRecipeLoader.loadAllBundledRecipes(languageCode)
                
                // Calculate matches for user meals (filter out nulls - meals without star ingredients)
                val userMealSuggestions = userMeals.mapNotNull { meal ->
                    calculateUserMealMatch(meal, pantryItems)
                }
                
                // Calculate matches for bundled recipes (filter out nulls - recipes without star ingredients)
                val bundledMealSuggestions = bundledRecipesByCuisine.flatMap { (cuisine, recipes) ->
                    recipes.mapNotNull { recipe ->
                        calculateBundledRecipeMatch(recipe, cuisine, pantryItems)
                    }
                }
                
                // Combine all suggestions
                val allSuggestions = userMealSuggestions + bundledMealSuggestions
                
                // Categorize by match percentage
                val perfectMatches = allSuggestions
                    .filter { it.matchPercentage == 100 }
                    .sortedByDescending { it.matchPercentage }
                
                val goodMatches = allSuggestions
                    .filter { it.matchPercentage in 80..99 }
                    .sortedByDescending { it.matchPercentage }
                
                val partialMatches = allSuggestions
                    .filter { it.matchPercentage in 50..79 }
                    .sortedByDescending { it.matchPercentage }
                
                _uiState.value = SuggestionUiState.Success(
                    allPerfectMatches = perfectMatches,
                    allGoodMatches = goodMatches,
                    allPartialMatches = partialMatches,
                    filteredPerfectMatches = perfectMatches,
                    filteredGoodMatches = goodMatches,
                    filteredPartialMatches = partialMatches,
                    selectedMealType = null,
                    selectedDishCategory = null
                )
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading suggestions", e)
                _uiState.value = SuggestionUiState.Error(
                    e.message ?: "Failed to load meal suggestions"
                )
            }
        }
    }

    /**
     * Calculate match percentage for a user-created meal
     * Uses ID-based matching with pantry ingredients
     * Checks star ingredients and their substitutes
     */
    private fun calculateUserMealMatch(
        meal: Meal,
        pantryItems: List<PantryItem>
    ): MealSuggestion.UserMeal? {
        val mealIngredients = meal.ingredients
        val totalIngredients = mealIngredients.size
        
        if (totalIngredients == 0) {
            return MealSuggestion.UserMeal(
                meal = meal,
                matchPercentage = 0,
                availableIngredients = 0,
                totalIngredients = 0,
                missingIngredientNames = emptyList()
            )
        }
        
        // Create set of available pantry ingredient IDs (with quantity > 0)
        val pantryIngredientIds = pantryItems
            .filter { it.availableQuantity > 0 }
            .map { it.ingredient.id }
            .toSet()
        
        // Separate star and regular ingredients
        val starIngredients = mealIngredients.filter { it.isStarIngredient }
        
        // Check if ALL star ingredients are available (directly or via substitutes)
        if (starIngredients.isNotEmpty()) {
            val allStarsAvailable = starIngredients.all { starIng ->
                // Check if ingredient itself is available
                val ingredientAvailable = pantryIngredientIds.contains(starIng.ingredient.id)
                
                // If not available, check if any substitute is available
                if (!ingredientAvailable) {
                    val hasAvailableSubstitute = starIng.ingredient.substitutes.any { substitute ->
                        pantryIngredientIds.contains(substitute.substituteIngredient.id)
                    }
                    hasAvailableSubstitute
                } else {
                    true
                }
            }
            
            if (!allStarsAvailable) {
                // Don't suggest this meal - missing essential star ingredients
                return null
            }
        }
        
        // Count matching ingredients by ID (DON'T count substitutes as available)
        val availableCount = mealIngredients.count { mealIngredient ->
            // Only count direct matches
            pantryIngredientIds.contains(mealIngredient.ingredient.id)
        }
        
        // Collect missing ingredient names (those without direct match)
        val missingIngredientNames = mealIngredients
            .filter { mealIng ->
                !pantryIngredientIds.contains(mealIng.ingredient.id)
            }
            .map { it.ingredient.name }
        
        // Calculate match percentage
        val matchPercentage = (availableCount * 100) / totalIngredients
        
        return MealSuggestion.UserMeal(
            meal = meal,
            matchPercentage = matchPercentage,
            availableIngredients = availableCount,
            totalIngredients = totalIngredients,
            missingIngredientNames = missingIngredientNames
        )
    }

    /**
     * Calculate match percentage for a bundled recipe
     * Uses name-based matching (case-insensitive) with pantry ingredients
     * Checks star ingredients and their substitutes
     */
    private suspend fun calculateBundledRecipeMatch(
        recipe: BundledRecipe,
        cuisine: Cuisine,
        pantryItems: List<PantryItem>
    ): MealSuggestion.BundledMeal? {
        val recipeIngredients = recipe.ingredients
        val totalIngredients = recipeIngredients.size
        
        if (totalIngredients == 0) {
            return MealSuggestion.BundledMeal(
                recipe = recipe,
                cuisine = cuisine,
                matchPercentage = 0,
                availableIngredients = 0,
                totalIngredients = 0,
                missingIngredientNames = emptyList()
            )
        }
        
        // Create map of ingredient names (case-insensitive) to pantry items
        val pantryByName = pantryItems
            .filter { it.availableQuantity > 0 }
            .associateBy { it.ingredient.name.lowercase() }
        
        // Separate star and regular ingredients
        val starIngredients = recipeIngredients.filter { it.isStarIngredient }
        
        // Check if ALL star ingredients are available (directly or via substitutes)
        if (starIngredients.isNotEmpty()) {
            val allStarsAvailable = starIngredients.all { starIng ->
                val ingredientName = starIng.name.lowercase()
                
                // Check if ingredient itself is available
                val ingredientAvailable = pantryByName.containsKey(ingredientName)
                
                // If not available, check if any substitute is available
                if (!ingredientAvailable) {
                    // Look up ingredient in database to get substitutes
                    val ingredient = ingredientRepository.getIngredientByName(starIng.name)
                    
                    val hasAvailableSubstitute = ingredient?.substitutes?.any { substitute ->
                        val subName = substitute.substituteIngredient.name.lowercase()
                        pantryByName.containsKey(subName)
                    } ?: false
                    
                    hasAvailableSubstitute
                } else {
                    true
                }
            }
            
            if (!allStarsAvailable) {
                // Don't suggest this meal - missing essential star ingredients
                return null
            }
        }
        
        var availableCount = 0
        val missingIngredientNames = mutableListOf<String>()
        
        // Check each recipe ingredient against pantry (DON'T count substitutes as available)
        recipeIngredients.forEach { bundledIngredient ->
            val ingredientName = bundledIngredient.name.lowercase()
            
            // Only check direct match - don't count substitutes as available
            val directMatch = pantryByName.containsKey(ingredientName)
            
            if (directMatch) {
                availableCount++
            } else {
                missingIngredientNames.add(bundledIngredient.name)
            }
        }
        
        // Calculate match percentage
        val matchPercentage = (availableCount * 100) / totalIngredients
        
        return MealSuggestion.BundledMeal(
            recipe = recipe,
            cuisine = cuisine,
            matchPercentage = matchPercentage,
            availableIngredients = availableCount,
            totalIngredients = totalIngredients,
            missingIngredientNames = missingIngredientNames
        )
    }

    /**
     * Retry loading suggestions after an error
     */
    fun retry() {
        loadSuggestions()
    }
    
    /**
     * Filter suggestions by meal type
     */
    fun filterByMealType(mealType: com.littlechef.app.domain.model.MealType?) {
        val currentState = _uiState.value
        if (currentState is SuggestionUiState.Success) {
            val filteredPerfect = if (mealType == null) {
                currentState.allPerfectMatches
            } else {
                currentState.allPerfectMatches.filter { it.mealTypeString == mealType.name }
            }
            
            val filteredGood = if (mealType == null) {
                currentState.allGoodMatches
            } else {
                currentState.allGoodMatches.filter { it.mealTypeString == mealType.name }
            }
            
            val filteredPartial = if (mealType == null) {
                currentState.allPartialMatches
            } else {
                currentState.allPartialMatches.filter { it.mealTypeString == mealType.name }
            }
            
            // Apply dish category filter if active
            val finalPerfect = if (currentState.selectedDishCategory != null) {
                filteredPerfect.filter { it.dishCategoryString == currentState.selectedDishCategory.name }
            } else {
                filteredPerfect
            }
            
            val finalGood = if (currentState.selectedDishCategory != null) {
                filteredGood.filter { it.dishCategoryString == currentState.selectedDishCategory.name }
            } else {
                filteredGood
            }
            
            val finalPartial = if (currentState.selectedDishCategory != null) {
                filteredPartial.filter { it.dishCategoryString == currentState.selectedDishCategory.name }
            } else {
                filteredPartial
            }
            
            _uiState.value = currentState.copy(
                selectedMealType = mealType,
                filteredPerfectMatches = finalPerfect,
                filteredGoodMatches = finalGood,
                filteredPartialMatches = finalPartial
            )
        }
    }
    
    /**
     * Filter suggestions by dish category
     */
    fun filterByDishCategory(dishCategory: com.littlechef.app.domain.model.DishCategory?) {
        val currentState = _uiState.value
        if (currentState is SuggestionUiState.Success) {
            val filteredPerfect = if (dishCategory == null) {
                currentState.allPerfectMatches
            } else {
                currentState.allPerfectMatches.filter { it.dishCategoryString == dishCategory.name }
            }
            
            val filteredGood = if (dishCategory == null) {
                currentState.allGoodMatches
            } else {
                currentState.allGoodMatches.filter { it.dishCategoryString == dishCategory.name }
            }
            
            val filteredPartial = if (dishCategory == null) {
                currentState.allPartialMatches
            } else {
                currentState.allPartialMatches.filter { it.dishCategoryString == dishCategory.name }
            }
            
            // Apply meal type filter if active
            val finalPerfect = if (currentState.selectedMealType != null) {
                filteredPerfect.filter { it.mealTypeString == currentState.selectedMealType.name }
            } else {
                filteredPerfect
            }
            
            val finalGood = if (currentState.selectedMealType != null) {
                filteredGood.filter { it.mealTypeString == currentState.selectedMealType.name }
            } else {
                filteredGood
            }
            
            val finalPartial = if (currentState.selectedMealType != null) {
                filteredPartial.filter { it.mealTypeString == currentState.selectedMealType.name }
            } else {
                filteredPartial
            }
            
            _uiState.value = currentState.copy(
                selectedDishCategory = dishCategory,
                filteredPerfectMatches = finalPerfect,
                filteredGoodMatches = finalGood,
                filteredPartialMatches = finalPartial
            )
        }
    }
    
    /**
     * Clear all filters
     */
    fun clearFilters() {
        val currentState = _uiState.value
        if (currentState is SuggestionUiState.Success) {
            _uiState.value = currentState.copy(
                selectedMealType = null,
                selectedDishCategory = null,
                filteredPerfectMatches = currentState.allPerfectMatches,
                filteredGoodMatches = currentState.allGoodMatches,
                filteredPartialMatches = currentState.allPartialMatches
            )
        }
    }
}
