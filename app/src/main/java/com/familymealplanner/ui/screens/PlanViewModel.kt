package com.familymealplanner.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.domain.model.MealPlan
import com.familymealplanner.domain.model.MealPlanStatus
import com.familymealplanner.domain.model.MealType
import com.familymealplanner.domain.model.roundEggQuantity
import com.familymealplanner.domain.repository.MealPlanRepository
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase
import com.familymealplanner.domain.usecase.StartCookingUseCase
import com.familymealplanner.domain.usecase.CompleteCookingUseCase
import com.familymealplanner.domain.usecase.AbortCookingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val mealRepository: com.familymealplanner.domain.repository.MealRepository,
    private val inventoryRepository: com.familymealplanner.domain.repository.InventoryRepository,
    private val groceryRepository: com.familymealplanner.domain.repository.GroceryRepository,
    private val createMealPlanUseCase: CreateMealPlanUseCase,
    private val startCookingUseCase: StartCookingUseCase,
    private val completeCookingUseCase: CompleteCookingUseCase,
    private val abortCookingUseCase: AbortCookingUseCase,
    private val preferences: com.familymealplanner.data.preferences.OnboardingPreferences,
    private val substituteInitializer: com.familymealplanner.data.local.SubstituteInitializer,
    private val translationSystem: com.familymealplanner.data.local.TranslationSystem,
    val nutritionLoader: com.familymealplanner.data.local.NutritionLoader
) : ViewModel() {

    init {
        viewModelScope.launch {
            nutritionLoader.load()
        }
    }

    private val _uiState = MutableStateFlow<PlanUiState>(PlanUiState.Loading)
    val uiState: StateFlow<PlanUiState> = _uiState.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _showAddMealDialog = MutableStateFlow(false)
    val showAddMealDialog: StateFlow<Boolean> = _showAddMealDialog.asStateFlow()

    private val _userMeals = MutableStateFlow<List<com.familymealplanner.domain.model.Meal>>(emptyList())
    val userMeals: StateFlow<List<com.familymealplanner.domain.model.Meal>> = _userMeals.asStateFlow()

    private val _startedByUserName = MutableStateFlow<String?>(null)
    val startedByUserName: StateFlow<String?> = _startedByUserName.asStateFlow()
    
    private val _insufficientIngredients = MutableStateFlow<List<StartCookingUseCase.InsufficientIngredient>?>(null)
    val insufficientIngredients: StateFlow<List<StartCookingUseCase.InsufficientIngredient>?> = _insufficientIngredients.asStateFlow()
    
    private val _useDetailedInstructions = MutableStateFlow(true)
    val useDetailedInstructions: StateFlow<Boolean> = _useDetailedInstructions.asStateFlow()
    
    // Job for observing ingredient availability - can be cancelled and restarted
    private var availabilityObserverJob: kotlinx.coroutines.Job? = null

    fun loadMealPlans() {
        viewModelScope.launch {
            try {
                mealPlanRepository.observeAll().collect { plans ->
                    // Filter out aborted meals and completed meals from previous days
                    val today = LocalDate.now()
                    val filteredPlans = plans.filter { mealPlan ->
                        when (mealPlan.status) {
                            MealPlanStatus.ABORTED -> false // Never show aborted meals
                            MealPlanStatus.COMPLETED -> {
                                // Keep completed meals only if they're from today
                                val plannedDate = Instant.ofEpochMilli(mealPlan.plannedDate)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                plannedDate == today
                            }
                            else -> true // Keep planned and cooking meals
                        }
                    }
                    _uiState.value = PlanUiState.Success(filteredPlans)
                }
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error(e.message ?: "Failed to load meal plans")
            }
        }
    }
    
    fun loadMealPlanById(mealPlanId: String) {
        viewModelScope.launch {
            try {
                // Initialize substitutes first to ensure they're in the database
                substituteInitializer.initialize(forceCheck = true)
                
                val mealPlan = mealPlanRepository.getById(mealPlanId)
                if (mealPlan != null) {
                    // If we already have plans loaded, add this one if it's not there
                    val currentState = _uiState.value
                    if (currentState is PlanUiState.Success) {
                        val existingPlan = currentState.mealPlans.find { it.id == mealPlanId }
                        if (existingPlan == null) {
                            _uiState.value = PlanUiState.Success(currentState.mealPlans + mealPlan)
                        } else {
                            // Update the existing plan with the newly loaded one (to get fresh substitutes)
                            val updatedPlans = currentState.mealPlans.map { 
                                if (it.id == mealPlanId) mealPlan else it 
                            }
                            _uiState.value = PlanUiState.Success(updatedPlans)
                        }
                    } else {
                        // Load all plans
                        loadMealPlans()
                    }
                    
                    // Reset the user name since we no longer track it
                    _startedByUserName.value = null
                } else {
                    _uiState.value = PlanUiState.Error("Meal plan not found")
                }
            } catch (e: Exception) {
                _uiState.value = PlanUiState.Error(e.message ?: "Failed to load meal plan")
            }
        }
    }
    
    fun loadUserMeals() {
        viewModelScope.launch {
            try {
                val meals = mealRepository.getAllMeals()
                // Filter out bundled recipes - only show user-created and scraped recipes
                _userMeals.value = meals.filter { !it.isBundled }
            } catch (e: Exception) {
                // Handle error silently or show message
            }
        }
    }

    fun showAddMealDialog() {
        _showAddMealDialog.value = true
        loadUserMeals()
    }

    fun dismissAddMealDialog() {
        _showAddMealDialog.value = false
    }

    fun addMealPlan(
        mealId: String,
        mealType: MealType,
        plannedDate: Long
    ) {
        viewModelScope.launch {
            when (createMealPlanUseCase(mealId, plannedDate, mealType)) {
                is CreateMealPlanUseCase.Result.Success -> {
                    _showAddMealDialog.value = false
                }
                is CreateMealPlanUseCase.Result.InsufficientIngredients -> {
                    // Still create the plan, just show warning
                    _showAddMealDialog.value = false
                }
                is CreateMealPlanUseCase.Result.Error -> {
                    // Show error
                }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun createMealPlan(
        mealId: String,
        plannedDate: Long,
        mealType: MealType,
        onSuccess: () -> Unit,
        onInsufficientIngredients: (List<CreateMealPlanUseCase.InsufficientIngredient>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = createMealPlanUseCase(mealId, plannedDate, mealType)) {
                is CreateMealPlanUseCase.Result.Success -> onSuccess()
                is CreateMealPlanUseCase.Result.InsufficientIngredients -> onInsufficientIngredients(result.shortages)
                is CreateMealPlanUseCase.Result.Error -> onError(result.message)
            }
        }
    }

    fun getGroupedMealPlans(): Map<LocalDate, List<MealPlan>> {
        val state = _uiState.value
        if (state !is PlanUiState.Success) return emptyMap()

        val twoDaysAgo = LocalDate.now().minusDays(2).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        
        // Filter out aborted meals and completed meals older than 2 days
        val activePlans = state.mealPlans.filter { plan ->
            plan.status != com.familymealplanner.domain.model.MealPlanStatus.ABORTED &&
            (plan.status != com.familymealplanner.domain.model.MealPlanStatus.COMPLETED || 
            plan.completedAt == null || 
            plan.completedAt >= twoDaysAgo)
        }

        return activePlans
            .groupBy { plan ->
                Instant.ofEpochMilli(plan.plannedDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            .toSortedMap()
    }

    fun completeMealPlan(mealPlanId: String) {
        viewModelScope.launch {
            when (completeCookingUseCase(mealPlanId)) {
                is CompleteCookingUseCase.Result.Success -> {
                    // Success - the meal plan will be updated via the flow
                }
                is CompleteCookingUseCase.Result.Error -> {
                    // Handle error if needed
                }
            }
        }
    }

    fun startCooking(
        mealPlanId: String,
        onSuccess: () -> Unit,
        onInsufficientIngredients: (List<StartCookingUseCase.InsufficientIngredient>) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = startCookingUseCase(mealPlanId)) {
                is StartCookingUseCase.Result.Success -> {
                    onSuccess()
                }
                is StartCookingUseCase.Result.InsufficientIngredients -> onInsufficientIngredients(result.shortages)
                is StartCookingUseCase.Result.Error -> onError(result.message)
            }
        }
    }

    fun completeCooking(
        mealPlanId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = completeCookingUseCase(mealPlanId)) {
                is CompleteCookingUseCase.Result.Success -> onSuccess()
                is CompleteCookingUseCase.Result.Error -> onError(result.message)
            }
        }
    }

    fun abortCooking(
        mealPlanId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            // Get the meal plan details before aborting
            val mealPlan = (_uiState.value as? PlanUiState.Success)?.mealPlans?.find { it.id == mealPlanId }
            
            when (val result = abortCookingUseCase(mealPlanId)) {
                is AbortCookingUseCase.Result.Success -> {
                    // Delete grocery items for this specific meal plan
                    if (mealPlan != null) {
                        deleteGroceryItemsForMealPlan(mealPlan)
                    }
                    onSuccess()
                }
                is AbortCookingUseCase.Result.Error -> onError(result.message)
            }
        }
    }
    
    private suspend fun deleteGroceryItemsForMealPlan(mealPlan: MealPlan) {
        try {
            // Get all grocery items
            val allGroceryItems = groceryRepository.getGroceryItems()
            
            // Filter items that match this specific meal plan:
            // - Meal name matches (with or without servings suffix)
            // - Same planned date
            // - Same meal type
            val itemsToDelete = allGroceryItems.filter { item ->
                val mealNameMatches = item.mealName == mealPlan.meal.name || 
                                     item.mealName.startsWith("${mealPlan.meal.name} (")
                
                mealNameMatches &&
                item.plannedDate == mealPlan.plannedDate &&
                item.mealType == mealPlan.mealType
            }
            
            // Delete each matching item
            itemsToDelete.forEach { item ->
                groceryRepository.deleteGroceryItem(item.id)
            }
        } catch (e: Exception) {
            // Log error but don't fail the abort operation
            android.util.Log.e("PlanViewModel", "Error deleting grocery items", e)
        }
    }
    
    suspend fun checkIngredientAvailability(mealPlan: MealPlan): List<StartCookingUseCase.InsufficientIngredient> {
        // Trigger substitute initialization to ensure we have up-to-date substitutes
        // This will add any new substitutes for ingredients that now exist in the database
        substituteInitializer.initialize(forceCheck = true)
        
        // Filter out non-deductible ingredients
        val deductibleIngredients = mealPlan.meal.ingredients.filter { 
            com.familymealplanner.domain.model.NonDeductibleIngredients.shouldDeduct(it.ingredient.name, it.ingredient.unit)
        }

        // Check ingredient availability
        val shortages = mutableListOf<StartCookingUseCase.InsufficientIngredient>()
        for (mealIngredient in deductibleIngredients) {
            val available = inventoryRepository.getAvailableQuantity(mealIngredient.ingredient.id)
            if (available < mealIngredient.quantity) {
                // Check if there's a substitute available
                val substitute = mealIngredient.ingredient.substitutes.firstOrNull { sub ->
                    val subAvailable = inventoryRepository.getAvailableQuantity(sub.substituteIngredient.id)
                    subAvailable >= mealIngredient.quantity
                }?.substituteIngredient
                
                shortages.add(
                    StartCookingUseCase.InsufficientIngredient(
                        ingredientName = mealIngredient.ingredient.name,
                        required = mealIngredient.quantity,
                        available = available,
                        unit = mealIngredient.ingredient.unit,
                        substitute = substitute
                    )
                )
            }
        }
        
        return shortages
    }
    
    /**
     * Observe pantry changes and continuously check ingredient availability for a meal plan.
     * This will automatically update when ingredients are added/removed from the pantry.
     * Takes into account any applied substitutions.
     */
    /**
     * Observe ingredient availability with servings support.
     * Cancels any previous observation and starts a new one with the updated servings.
     */
    fun observeIngredientAvailabilityWithServings(mealPlanId: String, servings: Int) {
        // Cancel previous observation job
        availabilityObserverJob?.cancel()
        
        // Start new observation
        availabilityObserverJob = viewModelScope.launch {
            // Observe the meal plan itself for changes (including substitution updates)
            launch {
                mealPlanRepository.observeById(mealPlanId).collect { mealPlan ->
                    if (mealPlan == null) {
                        _insufficientIngredients.value = null
                        return@collect
                    }
                    
                    // Check ingredient availability with current substitutions and servings
                    val shortages = checkIngredientAvailabilityWithSubstitutions(mealPlan, servings)
                    _insufficientIngredients.value = if (shortages.isNotEmpty()) shortages else null
                }
            }
            
            // Also observe pantry changes
            launch {
                inventoryRepository.observePantryItems().collect { _ ->
                    // Trigger a re-check by getting the latest meal plan
                    val mealPlan = mealPlanRepository.getById(mealPlanId) ?: return@collect
                    val shortages = checkIngredientAvailabilityWithSubstitutions(mealPlan, servings)
                    _insufficientIngredients.value = if (shortages.isNotEmpty()) shortages else null
                }
            }
        }
    }
    
    fun observeIngredientAvailability(mealPlanId: String, servings: Int) {
        observeIngredientAvailabilityWithServings(mealPlanId, servings)
    }
    
    /**
     * Check ingredient availability for a specific serving size without setting up observers.
     * Used when servings are changed in the UI.
     */
    fun checkIngredientAvailabilityForServings(mealPlanId: String, servings: Int) {
        viewModelScope.launch {
            val mealPlan = mealPlanRepository.getById(mealPlanId) ?: return@launch
            val shortages = checkIngredientAvailabilityWithSubstitutions(mealPlan, servings)
            _insufficientIngredients.value = if (shortages.isNotEmpty()) shortages else null
        }
    }
    
    /**
     * Check ingredient availability, taking into account applied substitutions and serving size.
     * If a substitute has been applied, check the substitute ingredient instead of the original.
     */
    private suspend fun checkIngredientAvailabilityWithSubstitutions(
        mealPlan: MealPlan, 
        servings: Int
    ): List<StartCookingUseCase.InsufficientIngredient> {
        // Trigger substitute initialization to ensure we have up-to-date substitutes
        substituteInitializer.initialize(forceCheck = true)
        
        // Calculate servings multiplier
        val servingsMultiplier = mealPlan.meal.servings?.let { originalServings ->
            servings.toDouble() / originalServings.toDouble()
        } ?: 1.0
        
        // Filter out non-deductible ingredients
        val deductibleIngredients = mealPlan.meal.ingredients.filter { 
            com.familymealplanner.domain.model.NonDeductibleIngredients.shouldDeduct(it.ingredient.name, it.ingredient.unit)
        }

        // Check ingredient availability
        val shortages = mutableListOf<StartCookingUseCase.InsufficientIngredient>()
        for (mealIngredient in deductibleIngredients) {
            // Check if a substitute has been applied for this ingredient
            val ingredientToCheck = mealPlan.ingredientSubstitutions[mealIngredient.ingredient.id]?.let { substituteId ->
                // Find the substitute ingredient
                mealIngredient.ingredient.substitutes.find { it.substituteIngredient.id == substituteId }?.substituteIngredient
            } ?: mealIngredient.ingredient
            
            // Adjust quantity based on servings
            val requiredQuantity = roundEggQuantity(mealIngredient.quantity * servingsMultiplier, mealIngredient.ingredient.name)
            
            val available = inventoryRepository.getAvailableQuantity(ingredientToCheck.id)
            if (available < requiredQuantity) {
                // Only show substitutes if we're not already using one
                val substitute = if (ingredientToCheck.id == mealIngredient.ingredient.id) {
                    mealIngredient.ingredient.substitutes.firstOrNull { sub ->
                        val subAvailable = inventoryRepository.getAvailableQuantity(sub.substituteIngredient.id)
                        subAvailable >= requiredQuantity
                    }?.substituteIngredient
                } else {
                    null // Already using a substitute
                }
                
                shortages.add(
                    StartCookingUseCase.InsufficientIngredient(
                        ingredientName = ingredientToCheck.name, // Use substitute name if applied
                        required = requiredQuantity,
                        available = available,
                        unit = ingredientToCheck.unit,
                        substitute = substitute
                    )
                )
            }
        }
        
        return shortages
    }
    
    /**
     * Update the planned servings for a meal plan
     */
    fun updatePlannedServings(mealPlanId: String, servings: Int) {
        viewModelScope.launch {
            val mealPlan = mealPlanRepository.getById(mealPlanId) ?: return@launch
            val updatedMealPlan = mealPlan.copy(
                plannedServings = servings,
                updatedAt = System.currentTimeMillis()
            )
            mealPlanRepository.update(updatedMealPlan)
        }
    }
    
    /**
     * Stop observing ingredient availability (clear the state)
     */
    fun clearIngredientAvailability() {
        availabilityObserverJob?.cancel()
        availabilityObserverJob = null
        _insufficientIngredients.value = null
    }
    
    fun setDetailedInstructions(detailed: Boolean) {
        _useDetailedInstructions.value = detailed
    }
    
    /**
     * Delete a meal plan
     */
    fun deleteMealPlan(mealPlanId: String) {
        viewModelScope.launch {
            try {
                // Get the meal plan before deleting to clean up grocery items
                val mealPlan = mealPlanRepository.getById(mealPlanId)
                
                if (mealPlan != null) {
                    // Delete associated grocery items first
                    deleteGroceryItemsForMealPlan(mealPlan)
                    
                    // Delete the meal plan
                    mealPlanRepository.delete(mealPlan)
                }
            } catch (e: Exception) {
                android.util.Log.e("PlanViewModel", "Error deleting meal plan", e)
            }
        }
    }
    
    /**
     * Clear all completed and aborted meals from the plan
     */
    fun clearCompletedMeals() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                if (state is PlanUiState.Success) {
                    // Find all completed and aborted meals
                    val mealsToDelete = state.mealPlans.filter { 
                        it.status == MealPlanStatus.COMPLETED || it.status == MealPlanStatus.ABORTED
                    }
                    
                    // Delete each meal
                    mealsToDelete.forEach { mealPlan ->
                        deleteGroceryItemsForMealPlan(mealPlan)
                        mealPlanRepository.delete(mealPlan)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("PlanViewModel", "Error clearing completed meals", e)
            }
        }
    }
    
    /**
     * Apply a substitute ingredient for a specific ingredient in this meal plan instance.
     * This only affects this meal plan, not the original recipe.
     * Also updates grocery items to reflect the substitution.
     */
    fun applySubstitute(mealPlanId: String, originalIngredientId: String, substituteIngredientId: String) {
        viewModelScope.launch {
            val mealPlan = mealPlanRepository.getById(mealPlanId) ?: return@launch
            
            // Find the original and substitute ingredients
            val originalIngredient = mealPlan.meal.ingredients.find { it.ingredient.id == originalIngredientId }?.ingredient
            val substituteIngredient = originalIngredient?.substitutes?.find { 
                it.substituteIngredient.id == substituteIngredientId 
            }?.substituteIngredient
            
            if (originalIngredient == null || substituteIngredient == null) return@launch
            
            // Add the substitution to the map
            val updatedSubstitutions = mealPlan.ingredientSubstitutions.toMutableMap()
            updatedSubstitutions[originalIngredientId] = substituteIngredientId
            
            // Update the meal plan
            val updatedMealPlan = mealPlan.copy(
                ingredientSubstitutions = updatedSubstitutions,
                updatedAt = System.currentTimeMillis()
            )
            
            mealPlanRepository.update(updatedMealPlan)
            
            // Update grocery items: replace original ingredient with substitute
            updateGroceryItemsForSubstitution(
                mealPlan = mealPlan,
                originalIngredientName = originalIngredient.name,
                substituteIngredientName = substituteIngredient.name
            )
            
            // Reload the meal plan to update UI
            loadMealPlanById(mealPlanId)
        }
    }
    
    /**
     * Remove a substitution and revert to the original ingredient
     * Also updates grocery items to reflect the change.
     */
    fun removeSubstitute(mealPlanId: String, originalIngredientId: String) {
        viewModelScope.launch {
            val mealPlan = mealPlanRepository.getById(mealPlanId) ?: return@launch
            
            // Find the original and current substitute ingredients
            val originalIngredient = mealPlan.meal.ingredients.find { it.ingredient.id == originalIngredientId }?.ingredient
            val substituteId = mealPlan.ingredientSubstitutions[originalIngredientId]
            val substituteIngredient = originalIngredient?.substitutes?.find { 
                it.substituteIngredient.id == substituteId 
            }?.substituteIngredient
            
            if (originalIngredient == null || substituteIngredient == null) return@launch
            
            // Remove the substitution from the map
            val updatedSubstitutions = mealPlan.ingredientSubstitutions.toMutableMap()
            updatedSubstitutions.remove(originalIngredientId)
            
            // Update the meal plan
            val updatedMealPlan = mealPlan.copy(
                ingredientSubstitutions = updatedSubstitutions,
                updatedAt = System.currentTimeMillis()
            )
            
            mealPlanRepository.update(updatedMealPlan)
            
            // Update grocery items: replace substitute with original ingredient
            updateGroceryItemsForSubstitution(
                mealPlan = mealPlan,
                originalIngredientName = substituteIngredient.name,
                substituteIngredientName = originalIngredient.name
            )
            
            // Reload the meal plan to update UI
            loadMealPlanById(mealPlanId)
        }
    }
    
    /**
     * Update grocery items when a substitution is applied or removed
     */
    private suspend fun updateGroceryItemsForSubstitution(
        mealPlan: MealPlan,
        originalIngredientName: String,
        substituteIngredientName: String
    ) {
        try {
            // Get all grocery items
            val allGroceryItems = groceryRepository.getGroceryItems()
            
            // Find items that match this meal plan and the original ingredient
            val itemsToDelete = allGroceryItems.filter { item ->
                item.mealName == mealPlan.meal.name &&
                item.plannedDate == mealPlan.plannedDate &&
                item.mealType == mealPlan.mealType &&
                item.ingredientName.equals(originalIngredientName, ignoreCase = true)
            }
            
            // Delete the original ingredient items
            itemsToDelete.forEach { item ->
                groceryRepository.deleteGroceryItem(item.id)
            }
            
            // Note: We don't create new grocery items for the substitute here
            // The user should add the substitute to their grocery list if they need it
            // The insufficient ingredients check will now show the substitute instead
        } catch (e: Exception) {
            // Log error but don't fail the substitution operation
            android.util.Log.e("PlanViewModel", "Error updating grocery items", e)
        }
    }
    
    /**
     * Translates an ingredient name to the current language.
     */
    fun translateIngredientName(ingredientName: String): String {
        return translationSystem.translateIngredient(ingredientName)
    }
}

sealed class PlanUiState {
    data object Loading : PlanUiState()
    data class Success(val mealPlans: List<MealPlan>) : PlanUiState()
    data class Error(val message: String) : PlanUiState()
}
