package com.familymealplanner.ui.screens

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.data.local.ImageStorage
import com.familymealplanner.data.preferences.OnboardingPreferences
import com.familymealplanner.domain.model.CatalogIngredient
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.model.UnitConversion
import com.familymealplanner.domain.repository.IngredientRepository
import com.familymealplanner.domain.repository.MealRepository
import com.familymealplanner.domain.util.IngredientMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ManualRecipeIngredient(
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "g",
    val isStarIngredient: Boolean = false
)

data class ManualRecipeState(
    val name: String = "",
    val prepTimeMinutes: String = "",
    val cookTimeMinutes: String = "",
    val servings: String = "",
    val mealType: com.familymealplanner.domain.model.MealType? = null,
    val dishCategory: com.familymealplanner.domain.model.DishCategory? = null,
    val ingredients: List<ManualRecipeIngredient> = emptyList(),
    val instructions: String = "",
    val dishImage: Bitmap? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface ManualRecipeUiState {
    data object Editing : ManualRecipeUiState
    data object Saving : ManualRecipeUiState
    data object Saved : ManualRecipeUiState
    data class Error(val message: String) : ManualRecipeUiState
}

@HiltViewModel
class ManualRecipeViewModel @Inject constructor(
    private val mealRepository: MealRepository,
    val ingredientRepository: IngredientRepository,
    private val imageStorage: ImageStorage,
    val preferences: OnboardingPreferences,
    private val ingredientMatcher: IngredientMatcher
) : ViewModel() {

    private val _state = MutableStateFlow(ManualRecipeState())
    val state: StateFlow<ManualRecipeState> = _state.asStateFlow()
    
    private val _uiState = MutableStateFlow<ManualRecipeUiState>(ManualRecipeUiState.Editing)
    val uiState: StateFlow<ManualRecipeUiState> = _uiState.asStateFlow()
    
    // Use catalog ingredients for autocomplete
    private val _allIngredients = MutableStateFlow<List<CatalogIngredient>>(IngredientCatalog.allIngredients)
    val allIngredients: StateFlow<List<CatalogIngredient>> = _allIngredients.asStateFlow()
    
    // Track if we're editing an existing meal
    private var editingMealId: String? = null
    
    init {
        // Initialize servings from user preference
        viewModelScope.launch {
            val defaultServings = preferences.defaultServingSize.first()
            _state.value = _state.value.copy(servings = defaultServings.toString())
        }
    }
    
    // Load existing meal for editing
    fun loadMealForEditing(mealId: String) {
        viewModelScope.launch {
            val meal = mealRepository.getMealById(mealId)
            if (meal != null) {
                editingMealId = mealId
                
                // Load image if exists
                val bitmap = meal.imagePath?.let { path ->
                    try {
                        imageStorage.loadBitmap(path)
                    } catch (e: Exception) {
                        null
                    }
                }
                
                // Convert meal ingredients to manual recipe ingredients
                val ingredients = meal.ingredients.map { mealIngredient ->
                    ManualRecipeIngredient(
                        name = mealIngredient.ingredient.name,
                        quantity = mealIngredient.quantity,
                        unit = mealIngredient.ingredient.unit
                    )
                }
                
                _state.value = ManualRecipeState(
                    name = meal.name,
                    prepTimeMinutes = meal.prepTimeMinutes?.toString() ?: "",
                    cookTimeMinutes = meal.cookTimeMinutes?.toString() ?: "",
                    servings = meal.servings?.toString() ?: "2",
                    ingredients = ingredients,
                    instructions = meal.instructions ?: "",
                    dishImage = bitmap
                )
            }
        }
    }
    
    // Reset to create new recipe mode
    fun resetForNewRecipe() {
        editingMealId = null
        viewModelScope.launch {
            val defaultServings = preferences.defaultServingSize.first()
            _state.value = ManualRecipeState(servings = defaultServings.toString())
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    fun updatePrepTime(time: String) {
        _state.value = _state.value.copy(prepTimeMinutes = time)
    }

    fun updateCookTime(time: String) {
        _state.value = _state.value.copy(cookTimeMinutes = time)
    }

    fun updateServings(servings: String) {
        _state.value = _state.value.copy(servings = servings)
    }
    
    fun updateMealType(mealType: com.familymealplanner.domain.model.MealType?) {
        _state.value = _state.value.copy(mealType = mealType)
    }
    
    fun updateDishCategory(dishCategory: com.familymealplanner.domain.model.DishCategory?) {
        _state.value = _state.value.copy(dishCategory = dishCategory)
    }

    fun updateInstructions(instructions: String) {
        _state.value = _state.value.copy(instructions = instructions)
    }

    fun setDishImage(bitmap: Bitmap) {
        _state.value = _state.value.copy(dishImage = bitmap)
    }

    fun clearDishImage() {
        _state.value = _state.value.copy(dishImage = null)
    }

    fun addIngredient() {
        val currentIngredients = _state.value.ingredients.toMutableList()
        currentIngredients.add(ManualRecipeIngredient())
        _state.value = _state.value.copy(ingredients = currentIngredients)
    }

    fun updateIngredient(index: Int, ingredient: ManualRecipeIngredient) {
        val currentIngredients = _state.value.ingredients.toMutableList()
        if (index in currentIngredients.indices) {
            currentIngredients[index] = ingredient
            _state.value = _state.value.copy(ingredients = currentIngredients)
        }
    }

    fun removeIngredient(index: Int) {
        val currentIngredients = _state.value.ingredients.toMutableList()
        if (index in currentIngredients.indices) {
            currentIngredients.removeAt(index)
            _state.value = _state.value.copy(ingredients = currentIngredients)
        }
    }

    fun saveRecipe() {
        val currentState = _state.value
        
        // Validation
        if (currentState.name.isBlank()) {
            _uiState.value = ManualRecipeUiState.Error("Recipe name is required")
            return
        }
        
        if (currentState.ingredients.isEmpty()) {
            _uiState.value = ManualRecipeUiState.Error("Add at least one ingredient")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = ManualRecipeUiState.Saving
            
            try {
                val now = System.currentTimeMillis()
                
                // Use existing meal ID if editing, otherwise create new
                val mealId = editingMealId ?: UUID.randomUUID().toString()
                val isEditing = editingMealId != null
                
                val prepTime = currentState.prepTimeMinutes.toIntOrNull()
                val cookTime = currentState.cookTimeMinutes.toIntOrNull()
                val servings = currentState.servings.toIntOrNull() ?: 1
                
                // Save dish image if provided or keep existing
                val imagePath = if (currentState.dishImage != null) {
                    imageStorage.saveBitmap(currentState.dishImage, mealId)
                } else if (isEditing) {
                    // Keep existing image path
                    mealRepository.getMealById(mealId)?.imagePath
                } else {
                    null
                }
                
                // Create ingredients and collect ingredient IDs with quantities and star status
                val ingredientInputs = mutableListOf<com.familymealplanner.domain.repository.MealIngredientInput>()
                
                for (ingredient in currentState.ingredients) {
                    if (ingredient.name.isBlank() || ingredient.quantity <= 0) continue
                    
                    // Convert to storage unit (g or ml) if needed
                    val (storageQuantity, storageUnit) = convertToStorageUnit(
                        ingredient.quantity,
                        ingredient.unit
                    )
                    
                    // Try to find existing ingredient by name
                    val existingIngredient = ingredientRepository.getIngredientByName(ingredient.name)
                    
                    val ingredientId = if (existingIngredient != null) {
                        existingIngredient.id
                    } else {
                        // Use fuzzy matching to find catalog ingredient (handles variations and translations)
                        val matchResult = ingredientMatcher.findMatch(ingredient.name, threshold = 0.6)
                        val catalogIngredient = matchResult?.catalogIngredient
                        
                        val category = catalogIngredient?.category?.displayName ?: "Other"
                        val subcategory = catalogIngredient?.subcategory ?: "Other"
                        val allergenNames = catalogIngredient?.allergens?.map { it.displayName } ?: emptyList()
                        
                        // Create new ingredient with storage unit
                        val createResult = ingredientRepository.createIngredient(
                            name = ingredient.name,
                            unit = storageUnit,
                            category = category,
                            subcategory = subcategory
                        )
                        
                        if (createResult.isFailure) {
                            throw createResult.exceptionOrNull() ?: Exception("Failed to create ingredient")
                        }
                        
                        createResult.getOrThrow().id
                    }
                    
                    ingredientInputs.add(
                        com.familymealplanner.domain.repository.MealIngredientInput(
                            ingredientId = ingredientId,
                            quantity = storageQuantity,
                            isStarIngredient = ingredient.isStarIngredient
                        )
                    )
                }
                
                if (ingredientInputs.isEmpty()) {
                    _uiState.value = ManualRecipeUiState.Error("Add at least one valid ingredient")
                    return@launch
                }
                
                if (isEditing) {
                    // Update existing meal
                    val existingMeal = mealRepository.getMealById(mealId)
                    if (existingMeal != null) {
                        val updatedMeal = existingMeal.copy(
                            name = currentState.name,
                            instructions = currentState.instructions,
                            prepTimeMinutes = prepTime,
                            cookTimeMinutes = cookTime,
                            servings = servings,
                            mealType = currentState.mealType,
                            dishCategory = currentState.dishCategory,
                            imagePath = imagePath,
                            updatedAt = now
                        )
                        mealRepository.updateMeal(updatedMeal, ingredientInputs)
                        android.util.Log.d("ManualRecipe", "Recipe updated successfully: $mealId")
                    }
                } else {
                    // Create new meal
                    val meal = Meal(
                        id = mealId,
                        name = currentState.name,
                        instructions = currentState.instructions,
                        simpleInstructions = null,
                        prepTimeMinutes = prepTime,
                        cookTimeMinutes = cookTime,
                        servings = servings,
                        isScraped = false,
                        mealType = currentState.mealType,
                        dishCategory = currentState.dishCategory,
                        imagePath = imagePath,
                        createdAt = now,
                        updatedAt = now
                    )
                    
                    mealRepository.createMeal(meal, ingredientInputs)
                }
                
                _uiState.value = ManualRecipeUiState.Saved
            } catch (e: Exception) {
                android.util.Log.e("ManualRecipe", "Error saving recipe", e)
                _uiState.value = ManualRecipeUiState.Error(
                    e.message ?: "Failed to save recipe"
                )
            }
        }
    }
    
    private fun convertToStorageUnit(quantity: Double, unit: String): Pair<Double, String> {
        // Convert to storage unit using UnitConversion (handles normalization)
        return UnitConversion.toStorageUnit(quantity, unit) ?: (quantity to unit)
    }
    
    fun clearError() {
        _uiState.value = ManualRecipeUiState.Editing
    }
}
