package com.littlechef.app.ui.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.analytics.AnalyticsService
import com.littlechef.app.data.preferences.OnboardingPreferences
import com.littlechef.app.data.remote.OpenAiService
import com.littlechef.app.data.remote.ScrapedIngredient
import com.littlechef.app.data.remote.ScrapedRecipe
import com.littlechef.app.domain.usecase.CreateScrapedMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScrapeRecipeUiState {
    data object Initial : ScrapeRecipeUiState
    data object Loading : ScrapeRecipeUiState
    data class Success(
        val recipe: ScrapedRecipe,
        val editedName: String,
        val editedIngredients: List<ScrapedIngredient>,
        val mealType: com.littlechef.app.domain.model.MealType? = null,
        val dishCategory: com.littlechef.app.domain.model.DishCategory? = null,
        val dishImage: Bitmap? = null,
        val useDetailedInstructions: Boolean = false
    ) : ScrapeRecipeUiState
    data object Saved : ScrapeRecipeUiState
    data class Error(val message: String) : ScrapeRecipeUiState
}

@HiltViewModel
class ScrapeRecipeViewModel @Inject constructor(
    private val openAiService: OpenAiService,
    private val createScrapedMealUseCase: CreateScrapedMealUseCase,
    private val preferences: OnboardingPreferences,
    private val analyticsService: com.littlechef.app.data.analytics.AnalyticsService
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScrapeRecipeUiState>(ScrapeRecipeUiState.Initial)
    val uiState: StateFlow<ScrapeRecipeUiState> = _uiState.asStateFlow()

    private var currentRecipe: ScrapedRecipe? = null
    
    // Remember the last source for retry
    private var lastSourceUrl: String? = null
    
    // Flag to auto-retry when returning from settings
    private var shouldRetryOnReturn = false

    fun scrapeFromUrl(url: String) {
        lastSourceUrl = url
        
        viewModelScope.launch {
            _uiState.value = ScrapeRecipeUiState.Loading
            
            when (val result = openAiService.scrapeRecipeFromUrl(url)) {
                is OpenAiService.Result.Success -> {
                    currentRecipe = result.recipe
                    
                    analyticsService.trackRecipeScraped(
                        recipeName = result.recipe.name,
                        ingredientCount = result.recipe.ingredients.size,
                        tokenUsage = result.tokenUsage?.totalTokens,
                        success = true
                    )
                    
                    // Normalize ingredient units before displaying
                    val normalizedIngredients = result.recipe.ingredients.map { ingredient ->
                        normalizeIngredientUnit(ingredient)
                    }
                    
                    _uiState.value = ScrapeRecipeUiState.Success(
                        recipe = result.recipe.copy(ingredients = normalizedIngredients),
                        editedName = result.recipe.name,
                        editedIngredients = normalizedIngredients,
                        dishImage = null // User can add their own photo
                    )
                }
                is OpenAiService.Result.Error -> {
                    analyticsService.trackRecipeScraped(
                        recipeName = lastSourceUrl ?: "unknown",
                        ingredientCount = 0,
                        success = false,
                        errorMessage = result.message
                    )
                    _uiState.value = ScrapeRecipeUiState.Error(result.message)
                }
            }
        }
    }
    
    /**
     * Normalize ingredient units from API to app's standard units.
     * Converts units like "шт", "spray", "tsp" to "pcs", "ml", etc.
     */
    private fun normalizeIngredientUnit(ingredient: ScrapedIngredient): ScrapedIngredient {
        val normalized = com.littlechef.app.domain.model.UnitConversion.normalizeUnit(ingredient.unit)
        
        return if (normalized != null) {
            val (normalizedUnit, conversionFactor) = normalized
            ingredient.copy(
                quantity = ingredient.quantity * conversionFactor,
                unit = normalizedUnit
            )
        } else {
            // If unit is not recognized, keep as is
            ingredient
        }
    }
    
    fun retry() {
        lastSourceUrl?.let { scrapeFromUrl(it) } ?: reset()
    }

    fun updateName(name: String) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(editedName = name)
        }
    }
    
    fun updateMealType(mealType: com.littlechef.app.domain.model.MealType?) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(mealType = mealType)
        }
    }
    
    fun updateDishCategory(dishCategory: com.littlechef.app.domain.model.DishCategory?) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(dishCategory = dishCategory)
        }
    }

    fun updateIngredient(index: Int, ingredient: ScrapedIngredient) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            val updatedIngredients = currentState.editedIngredients.toMutableList()
            if (index in updatedIngredients.indices) {
                updatedIngredients[index] = ingredient
                _uiState.value = currentState.copy(editedIngredients = updatedIngredients)
            }
        }
    }
    
    fun setDishImage(bitmap: Bitmap) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(dishImage = bitmap)
        }
    }
    
    fun clearDishImage() {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(dishImage = null)
        }
    }
    
    fun setDetailedInstructions(detailed: Boolean) {
        val currentState = _uiState.value
        if (currentState is ScrapeRecipeUiState.Success) {
            _uiState.value = currentState.copy(useDetailedInstructions = detailed)
        }
    }

    fun saveRecipe() {
        val currentState = _uiState.value
        if (currentState !is ScrapeRecipeUiState.Success) return
        
        val recipe = currentRecipe ?: return
        
        viewModelScope.launch {
            _uiState.value = ScrapeRecipeUiState.Loading
            
            // Create updated recipe with edited ingredients
            // Keep BOTH instructions - don't replace them based on toggle
            val updatedRecipe = recipe.copy(
                ingredients = currentState.editedIngredients
            )
            
            val result = createScrapedMealUseCase(
                scrapedRecipe = updatedRecipe,
                finalName = currentState.editedName,
                mealType = currentState.mealType,
                dishCategory = currentState.dishCategory,
                dishImage = currentState.dishImage
            )
            
            if (result.isSuccess) {
                _uiState.value = ScrapeRecipeUiState.Saved
            } else {
                _uiState.value = ScrapeRecipeUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to save recipe"
                )
            }
        }
    }

    fun reset() {
        currentRecipe = null
        _uiState.value = ScrapeRecipeUiState.Initial
    }
    
    fun markForRetry() {
        shouldRetryOnReturn = true
    }
    
    fun shouldAutoRetry(): Boolean {
        val shouldRetry = shouldRetryOnReturn && lastSourceUrl != null
        if (shouldRetry) {
            shouldRetryOnReturn = false // Reset flag
        }
        return shouldRetry
    }
}
