package com.littlechef.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.domain.model.Allergen
import com.littlechef.app.domain.repository.AllergenRepository
import com.littlechef.app.domain.usecase.CreateAllergenUseCase
import com.littlechef.app.domain.usecase.DeleteAllergenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllergensViewModel @Inject constructor(
    private val allergenRepository: AllergenRepository,
    private val createAllergenUseCase: CreateAllergenUseCase,
    private val deleteAllergenUseCase: DeleteAllergenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AllergensUiState>(AllergensUiState.Loading)
    val uiState: StateFlow<AllergensUiState> = _uiState.asStateFlow()

    init {
        loadAllergens()
    }

    private fun loadAllergens() {
        viewModelScope.launch {
            allergenRepository.observeAllAllergens().collect { allergens ->
                _uiState.value = AllergensUiState.Success(allergens)
            }
        }
    }

    fun createAllergen(name: String) {
        viewModelScope.launch {
            val result = createAllergenUseCase(name)
            if (result.isFailure) {
                _uiState.value = AllergensUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to create allergen"
                )
                loadAllergens()
            }
        }
    }

    fun deleteAllergen(allergen: Allergen) {
        viewModelScope.launch {
            val result = deleteAllergenUseCase(allergen)
            if (result.isFailure) {
                _uiState.value = AllergensUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to delete allergen"
                )
                loadAllergens()
            }
        }
    }

    fun clearError() {
        if (_uiState.value is AllergensUiState.Error) {
            loadAllergens()
        }
    }
}

sealed interface AllergensUiState {
    object Loading : AllergensUiState
    data class Success(val allergens: List<Allergen>) : AllergensUiState
    data class Error(val message: String) : AllergensUiState
}
