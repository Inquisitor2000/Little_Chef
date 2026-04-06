package com.familymealplanner.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.data.preferences.OnboardingPreferences
import com.familymealplanner.domain.model.EnrichedIngredient
import com.familymealplanner.domain.model.PantryItem
import com.familymealplanner.domain.repository.InventoryRepository
import com.familymealplanner.domain.usecase.AdjustInventoryUseCase
import com.familymealplanner.domain.usecase.RestockIngredientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository,
    private val translationSystem: com.familymealplanner.data.local.TranslationSystem,
    private val restockIngredientUseCase: RestockIngredientUseCase,
    private val adjustInventoryUseCase: AdjustInventoryUseCase,
    val ingredientRepository: com.familymealplanner.domain.repository.IngredientRepository,
    val preferences: OnboardingPreferences
) : ViewModel() {

    companion object {
        private const val TAG = "PantryViewModel"
    }

    private val _uiState = MutableStateFlow<PantryUiState>(PantryUiState.Loading)
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    fun loadPantryItems() {
        viewModelScope.launch {
            try {
                inventoryRepository.observePantryItems().collect { items ->
                    _uiState.value = PantryUiState.Success(items)
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                // Job was cancelled, this is expected when navigating away
                throw e // Re-throw to properly cancel the coroutine
            } catch (e: Exception) {
                Log.e(TAG, "Error loading pantry items", e)
                _uiState.value = PantryUiState.Error(e.message ?: "Failed to load pantry items")
            }
        }
    }

    fun restockIngredient(
        ingredientId: String,
        quantity: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                restockIngredientUseCase(ingredientId, quantity)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to restock ingredient")
            }
        }
    }

    fun adjustInventory(
        ingredientId: String,
        quantityChange: Double,
        reason: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                adjustInventoryUseCase(ingredientId, quantityChange, reason)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to adjust inventory")
            }
        }
    }

    fun addIngredient(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        subcategory: String,
        allergenNames: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                // Determine storage unit (always use base units: g or ml)
                val storageUnit = when {
                    com.familymealplanner.domain.model.UnitConversion.isVolumeUnit(unit) -> "ml"
                    com.familymealplanner.domain.model.UnitConversion.isWeightUnit(unit) -> "g"
                    else -> unit // pcs stays as pcs
                }
                
                // Convert quantity to base units for storage (g or ml)
                val storageResult = com.familymealplanner.domain.model.UnitConversion.toStorageUnit(quantity, unit)
                val storageQuantity = if (storageResult != null) {
                    storageResult.first
                } else {
                    quantity
                }
                
                // Check if ingredient already exists
                val existingIngredient = ingredientRepository.getIngredientByName(name)
                
                if (existingIngredient != null) {
                    // Ingredient exists, update preferred display unit and restock
                    val updatedIngredient = existingIngredient.copy(preferredDisplayUnit = unit)
                    ingredientRepository.updateIngredient(updatedIngredient, existingIngredient.allergens.map { it.name })
                    restockIngredientUseCase(existingIngredient.id, storageQuantity)
                } else {
                    // Create new ingredient - save with storage unit (g, ml, or pcs) and preferred display unit
                    val createResult = ingredientRepository.createIngredient(
                        name = name,
                        unit = storageUnit, // Always save as base unit (g, ml, or pcs)
                        category = category,
                        subcategory = subcategory,
                        preferredDisplayUnit = unit // Save user's preferred unit (kg, g, L, ml, pcs)
                    )
                    
                    if (createResult.isSuccess) {
                        val ingredient = createResult.getOrThrow()
                        
                        // Update ingredient with allergens if provided
                        if (allergenNames.isNotEmpty()) {
                            ingredientRepository.updateIngredient(ingredient, allergenNames)
                        }
                        
                        // Add to pantry
                        restockIngredientUseCase(ingredient.id, storageQuantity)
                    } else {
                        throw createResult.exceptionOrNull() ?: Exception("Failed to create ingredient")
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error adding ingredient", e)
                _uiState.value = PantryUiState.Error(e.message ?: "Failed to add ingredient")
            }
        }
    }

    /**
     * Add multiple ingredients to the pantry in batch.
     * 
     * This method processes each ingredient sequentially, tracking success and failure
     * for each one. If an ingredient fails to add, the process continues with the
     * remaining ingredients to ensure maximum success.
     * 
     * @param ingredients List of enriched ingredients to add
     * @return BatchAddResult containing success count, total count, and failed ingredients
     */
    suspend fun addIngredientsBatch(
        ingredients: List<EnrichedIngredient>
    ): BatchAddResult {
        val results = mutableListOf<Pair<EnrichedIngredient, Boolean>>()
        
        for (ingredient in ingredients) {
            try {
                // Get subcategory from matched catalog ingredient, or use "Other" as fallback
                val subcategory = ingredient.matchedCatalogIngredient?.subcategory ?: "Other"
                
                addIngredient(
                    name = ingredient.name,
                    quantity = ingredient.quantity,
                    unit = ingredient.unit,
                    category = ingredient.category.key,
                    subcategory = subcategory,
                    allergenNames = ingredient.allergens.map { it.displayName }
                )
                results.add(ingredient to true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add ingredient: ${ingredient.name}", e)
                results.add(ingredient to false)
            }
        }
        
        val successCount = results.count { it.second }
        val totalCount = results.size
        
        return BatchAddResult(
            successCount = successCount,
            totalCount = totalCount,
            failedIngredients = results.filter { !it.second }.map { it.first }
        )
    }

    fun updateIngredientUnit(
        ingredientId: String,
        newUnit: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Updating ingredient unit: $ingredientId to $newUnit")
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    val updatedIngredient = ingredient.copy(
                        unit = newUnit,
                        updatedAt = System.currentTimeMillis()
                    )
                    // Keep existing allergens when updating unit
                    val allergenNames = ingredient.allergens.map { it.name }
                    ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
                    onSuccess()
                } else {
                    onError("Ingredient not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating ingredient unit", e)
                onError(e.message ?: "Failed to update ingredient unit")
            }
        }
    }

    fun updateIngredientAllergens(
        ingredientId: String,
        allergenNames: List<String>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    val updatedIngredient = ingredient.copy(
                        updatedAt = System.currentTimeMillis()
                    )
                    ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
                    onSuccess()
                } else {
                    onError("Ingredient not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating ingredient allergens", e)
                onError(e.message ?: "Failed to update ingredient allergens")
            }
        }
    }

    fun updateIngredientName(
        ingredientId: String,
        newName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    val updatedIngredient = ingredient.copy(
                        name = newName,
                        updatedAt = System.currentTimeMillis()
                    )
                    // Keep existing allergens when updating name
                    val allergenNames = ingredient.allergens.map { it.name }
                    ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
                    onSuccess()
                } else {
                    onError("Ingredient not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating ingredient name", e)
                onError(e.message ?: "Failed to update ingredient name")
            }
        }
    }
    
    fun updateCustomIngredient(
        ingredientId: String,
        name: String,
        unit: String,
        category: String,
        subcategory: String,
        allergenNames: List<String>
    ) {
        viewModelScope.launch {
            try {
                val ingredient = ingredientRepository.getIngredientById(ingredientId)
                if (ingredient != null) {
                    val updatedIngredient = ingredient.copy(
                        name = name,
                        unit = unit,
                        category = category,
                        subcategory = subcategory,
                        updatedAt = System.currentTimeMillis()
                    )
                    ingredientRepository.updateIngredient(updatedIngredient, allergenNames)
                } else {
                    Log.e(TAG, "Ingredient not found: $ingredientId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating custom ingredient", e)
            }
        }
    }
    
    /**
     * Checks if a language indicator should be shown for content.
     * Returns true if the content was created in a different language than the current app language.
     */
    fun shouldShowLanguageIndicator(createdInLanguage: String): Boolean {
        val currentLanguage = translationSystem.getCurrentLanguage()
        return createdInLanguage != currentLanguage
    }
    
    /**
     * Gets the current language code.
     */
    fun getCurrentLanguage(): String {
        return translationSystem.getCurrentLanguage()
    }
    
    /**
     * Gets the language code for display (e.g., "RU", "RO", "EN").
     */
    fun getLanguageDisplayCode(languageCode: String): String {
        return languageCode.uppercase()
    }
    
    // Translation helpers
    fun translateIngredient(ingredientName: String): String {
        return translationSystem.translateIngredient(ingredientName)
    }
    
    fun translateCategory(categoryName: String): String {
        return translationSystem.translateCategory(categoryName)
    }
    
    fun getTranslationSystem(): com.familymealplanner.data.local.TranslationSystem {
        return translationSystem
    }
}

sealed class PantryUiState {
    data object Loading : PantryUiState()
    data class Success(val pantryItems: List<PantryItem>) : PantryUiState()
    data class Error(val message: String) : PantryUiState()
}

/**
 * Result of a batch ingredient addition operation.
 * 
 * @param successCount Number of ingredients successfully added
 * @param totalCount Total number of ingredients attempted
 * @param failedIngredients List of ingredients that failed to add
 */
data class BatchAddResult(
    val successCount: Int,
    val totalCount: Int,
    val failedIngredients: List<com.familymealplanner.domain.model.EnrichedIngredient>
)
