package com.littlechef.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.repository.MealRepository
import com.littlechef.app.domain.usecase.CreateMealUseCase
import com.littlechef.app.domain.usecase.DeleteMealUseCase
import com.littlechef.app.domain.usecase.UpdateMealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    private val translationSystem: com.littlechef.app.data.local.TranslationSystem,
    private val createMealUseCase: CreateMealUseCase,
    private val updateMealUseCase: UpdateMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealsUiState>(MealsUiState.Loading)
    val uiState: StateFlow<MealsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadMeals()
    }

    private fun loadMeals() {
        viewModelScope.launch {
            // Load all meals but filter out bundled recipes from "My Recipes"
            mealRepository.observeAllMeals().collect { allMeals ->
                val userMeals = allMeals.filter { !it.isBundled }
                _uiState.value = MealsUiState.Success(
                    meals = userMeals,
                    filteredMeals = filterMeals(userMeals, _searchQuery.value),
                    scrapedMeals = userMeals
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        val currentState = _uiState.value
        if (currentState is MealsUiState.Success) {
            _uiState.value = currentState.copy(
                filteredMeals = filterMeals(currentState.meals, query)
            )
        }
    }

    private fun filterMeals(meals: List<Meal>, query: String): List<Meal> {
        if (query.isBlank()) return meals
        return meals.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    fun createMeal(
        name: String,
        instructions: String?,
        prepTimeMinutes: Int?,
        cookTimeMinutes: Int?,
        servings: Int?,
        mealType: com.littlechef.app.domain.model.MealType?,
        dishCategory: com.littlechef.app.domain.model.DishCategory?,
        ingredients: List<Pair<String, Double>>
    ) {
        viewModelScope.launch {
            val result = createMealUseCase(
                name, instructions, prepTimeMinutes, cookTimeMinutes, servings, mealType, dishCategory, ingredients
            )
            if (result.isFailure) {
                _uiState.value = MealsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to create meal"
                )
                loadMeals()
            }
        }
    }

    fun updateMeal(
        meal: Meal,
        name: String,
        instructions: String?,
        prepTimeMinutes: Int?,
        cookTimeMinutes: Int?,
        servings: Int?,
        mealType: com.littlechef.app.domain.model.MealType?,
        dishCategory: com.littlechef.app.domain.model.DishCategory?,
        ingredients: List<Pair<String, Double>>
    ) {
        viewModelScope.launch {
            val result = updateMealUseCase(
                meal, name, instructions, prepTimeMinutes, cookTimeMinutes, servings, mealType, dishCategory, ingredients
            )
            if (result.isFailure) {
                _uiState.value = MealsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update meal"
                )
                loadMeals()
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            deleteMealUseCase(meal)
        }
    }

    fun clearError() {
        if (_uiState.value is MealsUiState.Error) {
            loadMeals()
        }
    }

    /**
     * Translates a category name to the current language.
     * Used for displaying category names in the UI.
     */
    fun translateCategoryName(categoryName: String): String {
        return translationSystem.translateCategory(categoryName)
    }
}

sealed interface MealsUiState {
    object Loading : MealsUiState
    data class Success(
        val meals: List<Meal>,
        val filteredMeals: List<Meal>,
        val scrapedMeals: List<Meal> = emptyList()
    ) : MealsUiState
    data class Error(val message: String) : MealsUiState
}
