package com.familymealplanner.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.usecase.AddIngredientSubstituteUseCase
import com.familymealplanner.domain.usecase.CreateIngredientUseCase
import com.familymealplanner.domain.usecase.DeleteIngredientUseCase
import com.familymealplanner.domain.usecase.UpdateIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val ingredientRepository: IngredientRepository,
    private val createIngredientUseCase: CreateIngredientUseCase,
    private val updateIngredientUseCase: UpdateIngredientUseCase,
    private val deleteIngredientUseCase: DeleteIngredientUseCase,
    private val addIngredientSubstituteUseCase: AddIngredientSubstituteUseCase,
    private val fixIngredientCategoriesUseCase: com.familymealplanner.domain.usecase.FixIngredientCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<IngredientsUiState>(IngredientsUiState.Loading)
    val uiState: StateFlow<IngredientsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadIngredients()
    }

    private fun loadIngredients() {
        viewModelScope.launch {
            ingredientRepository.observeAllIngredients().collect { ingredients ->
                _uiState.value = IngredientsUiState.Success(
                    ingredients = ingredients,
                    filteredIngredients = filterIngredients(ingredients, _searchQuery.value)
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        val currentState = _uiState.value
        if (currentState is IngredientsUiState.Success) {
            _uiState.value = currentState.copy(
                filteredIngredients = filterIngredients(currentState.ingredients, query)
            )
        }
    }

    private fun filterIngredients(ingredients: List<Ingredient>, query: String): List<Ingredient> {
        if (query.isBlank()) return ingredients
        return ingredients.filter { 
            it.name.contains(query, ignoreCase = true) ||
            it.category?.contains(query, ignoreCase = true) == true ||
            it.subcategory?.contains(query, ignoreCase = true) == true
        }
    }

    fun createIngredient(
        name: String,
        unit: String,
        category: String?,
        subcategory: String?,
        allergenIds: List<String>
    ) {
        viewModelScope.launch {
            val result = createIngredientUseCase(name, unit, category, subcategory, allergenIds)
            if (result.isFailure) {
                _uiState.value = IngredientsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to create ingredient"
                )
                loadIngredients()
            }
        }
    }

    fun updateIngredient(
        ingredient: Ingredient,
        name: String,
        unit: String,
        category: String?,
        subcategory: String?,
        allergenIds: List<String>
    ) {
        viewModelScope.launch {
            val result = updateIngredientUseCase(ingredient, name, unit, category, subcategory, allergenIds)
            if (result.isFailure) {
                _uiState.value = IngredientsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to update ingredient"
                )
                loadIngredients()
            }
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            val result = deleteIngredientUseCase(ingredient)
            if (result.isFailure) {
                _uiState.value = IngredientsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to delete ingredient"
                )
                loadIngredients()
            }
        }
    }

    fun addSubstitute(ingredientId: String, substituteId: String, notes: String?) {
        viewModelScope.launch {
            val result = addIngredientSubstituteUseCase(ingredientId, substituteId, notes)
            if (result.isFailure) {
                _uiState.value = IngredientsUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to add substitute"
                )
                loadIngredients()
            }
        }
    }

    fun clearError() {
        if (_uiState.value is IngredientsUiState.Error) {
            loadIngredients()
        }
    }

    fun fixIngredientCategories() {
        viewModelScope.launch {
            android.util.Log.d("IngredientsViewModel", "Starting ingredient category correction...")
            val result = fixIngredientCategoriesUseCase()
            if (result.isSuccess) {
                val correctedCount = result.getOrNull() ?: 0
                android.util.Log.d("IngredientsViewModel", "✅ Successfully corrected $correctedCount ingredient categories")
                // Reload ingredients to reflect the changes
                loadIngredients()
            } else {
                val errorMsg = "Failed to fix ingredient categories: ${result.exceptionOrNull()?.message}"
                android.util.Log.e("IngredientsViewModel", errorMsg)
                _uiState.value = IngredientsUiState.Error(errorMsg)
            }
        }
    }
}

sealed interface IngredientsUiState {
    object Loading : IngredientsUiState
    data class Success(
        val ingredients: List<Ingredient>,
        val filteredIngredients: List<Ingredient>
    ) : IngredientsUiState
    data class Error(val message: String) : IngredientsUiState
}
