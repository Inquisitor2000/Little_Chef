package com.familymealplanner.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.R
import com.familymealplanner.data.local.BundledRecipe
import com.familymealplanner.data.local.BundledRecipeLoader
import com.familymealplanner.domain.model.Cuisine
import com.familymealplanner.domain.usecase.CheckRecipeIngredientsUseCase
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase
import com.familymealplanner.domain.usecase.CreateMealUseCase
import com.familymealplanner.domain.usecase.CreateScrapedMealUseCase
import com.familymealplanner.domain.util.IngredientMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class BundledRecipeDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localeManager: com.familymealplanner.data.preferences.LocaleManager,
    private val bundledRecipeLoader: BundledRecipeLoader,
    private val translationSystem: com.familymealplanner.data.local.TranslationSystem,
    private val checkRecipeIngredientsUseCase: CheckRecipeIngredientsUseCase,
    private val groceryRepository: com.familymealplanner.domain.repository.GroceryRepository,
    private val preferences: com.familymealplanner.data.preferences.OnboardingPreferences,
    private val createScrapedMealUseCase: CreateScrapedMealUseCase,
    private val createMealPlanUseCase: CreateMealPlanUseCase,
    private val startCookingUseCase: com.familymealplanner.domain.usecase.StartCookingUseCase,
    private val mealRepository: com.familymealplanner.domain.repository.MealRepository,
    private val mealPlanRepository: com.familymealplanner.domain.repository.MealPlanRepository,
    private val inventoryRepository: com.familymealplanner.domain.repository.InventoryRepository,
    private val ingredientRepository: com.familymealplanner.domain.repository.IngredientRepository,
    private val ingredientMatcher: IngredientMatcher
) : ViewModel() {
    
    private val localeContext: Context by lazy {
        localeManager.applyLocale(context)
    }
    
    private val _recipe = MutableStateFlow<BundledRecipe?>(null)
    val recipe: StateFlow<BundledRecipe?> = _recipe
    
    private val _allergens = MutableStateFlow<List<com.familymealplanner.domain.model.Allergen>>(emptyList())
    val allergens: StateFlow<List<com.familymealplanner.domain.model.Allergen>> = _allergens
    
    private val _useDetailedInstructions = MutableStateFlow(true)
    val useDetailedInstructions: StateFlow<Boolean> = _useDetailedInstructions

    private val _ingredientCheckResult = MutableStateFlow<CheckRecipeIngredientsUseCase.Result?>(null)
    val ingredientCheckResult: StateFlow<CheckRecipeIngredientsUseCase.Result?> = _ingredientCheckResult

    private val _showMissingIngredientsDialog = MutableStateFlow(false)
    val showMissingIngredientsDialog: StateFlow<Boolean> = _showMissingIngredientsDialog

    private val _showPlanDialog = MutableStateFlow(false)
    val showPlanDialog: StateFlow<Boolean> = _showPlanDialog

    private val _planResult = MutableStateFlow<String?>(null)
    val planResult: StateFlow<String?> = _planResult

    private val _showMadeMealDialog = MutableStateFlow(false)
    val showMadeMealDialog: StateFlow<Boolean> = _showMadeMealDialog

    data class MadeMealResult(val success: Boolean, val message: String)
    private val _madeMealResult = MutableStateFlow<MadeMealResult?>(null)
    val madeMealResult: StateFlow<MadeMealResult?> = _madeMealResult

    private val _hasAllIngredients = MutableStateFlow(true)
    val hasAllIngredients: StateFlow<Boolean> = _hasAllIngredients
    
    private val _selectedServings = MutableStateFlow(2)
    val selectedServings: StateFlow<Int> = _selectedServings
    
    // Helper function to look up ingredient by English name with translation and fuzzy matching
    private suspend fun lookupIngredientByEnglishName(englishName: String): com.familymealplanner.domain.model.Ingredient? {
        // Bundled recipe ingredients are always in English and always exist in the catalog
        // Skip database lookup and go straight to catalog for efficiency
        val translatedName = translationSystem.translateIngredient(englishName)
        
        android.util.Log.d("BundledRecipeViewModel", "Looking up ingredient in catalog - English: $englishName, Translated: $translatedName")
        
        val matchResult = ingredientMatcher.findMatch(translatedName, threshold = 0.6)
        
        if (matchResult != null) {
            android.util.Log.d("BundledRecipeViewModel", "Catalog match found: ${matchResult.catalogIngredient.nameKey}, confidence: ${matchResult.confidence}")
            
            // Create a temporary ingredient object with catalog information
            // This won't be saved to database, just used for category lookup
            val matchedTranslatedName = translationSystem.translateIngredient(matchResult.catalogIngredient.nameKey)
            val categoryDisplayName = matchResult.catalogIngredient.category.displayName
            
            val ingredient = com.familymealplanner.domain.model.Ingredient(
                id = "", // Empty ID indicates this is from catalog, not database
                name = matchResult.catalogIngredient.nameKey, // Use English name from catalog
                unit = matchResult.catalogIngredient.defaultUnit, // Use catalog's default unit
                category = categoryDisplayName,
                subcategory = matchResult.catalogIngredient.subcategory,
                preferredDisplayUnit = null,
                createdInLanguage = localeManager.getLanguage(),
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            android.util.Log.d("BundledRecipeViewModel", "Created ingredient from catalog: ${matchResult.catalogIngredient.nameKey}, category: ${ingredient.category}")
            return ingredient
        } else {
            android.util.Log.d("BundledRecipeViewModel", "No catalog match found")
            return null
        }
    }
    
    // Map of ingredient name to substitute name (for display purposes)
    private val _ingredientSubstitutions = MutableStateFlow<Map<String, String>>(emptyMap())
    val ingredientSubstitutions: StateFlow<Map<String, String>> = _ingredientSubstitutions
    
    fun loadRecipe(cuisine: Cuisine, recipeId: String) {
        viewModelScope.launch {
            // Use TranslationSystem to load translated recipe
            val translatedRecipe = translationSystem.translateRecipe(recipeId, cuisine.displayName.lowercase())
            _recipe.value = translatedRecipe
            
            // Load allergens from ingredients using catalog-first approach
            _recipe.value?.let { recipe ->
                val allergensSet = mutableSetOf<com.familymealplanner.domain.model.Allergen>()
                
                recipe.ingredients.forEach { bundledIngredient ->
                    val ingredientName = bundledIngredient.name.trim()
                    
                    // Try to find allergens from catalog first (more reliable)
                    val catalogAllergens = findAllergensFromCatalog(ingredientName)
                    if (catalogAllergens.isNotEmpty()) {
                        allergensSet.addAll(catalogAllergens)
                    } else {
                        // Fallback to database lookup
                        val ingredient = ingredientRepository.getIngredientByName(ingredientName)
                        ingredient?.allergens?.let { allergensSet.addAll(it) }
                    }
                }
                
                _allergens.value = allergensSet.toList()
            }
            
            // Load default serving size from preferences
            val defaultServingSize = preferences.defaultServingSize.first()
            _selectedServings.value = defaultServingSize
            
            // Check ingredient availability when recipe loads
            checkIngredientsAvailability()
        }
    }
    
    /**
     * Find allergens from the ingredient catalog using fuzzy matching.
     * Uses fuzzy matching to handle ingredient name variations and typos.
     * Translates ingredient names to current language before matching.
     * Returns a list of Allergen objects if found, empty list otherwise.
     */
    private fun findAllergensFromCatalog(ingredientName: String): List<com.familymealplanner.domain.model.Allergen> {
        // Translate the ingredient name to current language before matching
        // This handles bundled recipes which have English names
        val translatedName = translationSystem.translateIngredient(ingredientName)
        
        // Use fuzzy matching to handle variations in ingredient names
        val matchResult = ingredientMatcher.findMatch(translatedName, threshold = 0.6)
        val catalogIngredient = matchResult?.catalogIngredient
        
        // Convert catalog allergens to Allergen objects
        return if (catalogIngredient != null && catalogIngredient.allergens.isNotEmpty()) {
            catalogIngredient.allergens.map { commonAllergen ->
                com.familymealplanner.domain.model.Allergen(
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
    
    fun cycleServings() {
        _selectedServings.value = when (_selectedServings.value) {
            1 -> 2
            2 -> 4
            4 -> 6
            else -> 1
        }
        // Recheck ingredients when servings change
        checkIngredientsAvailability()
    }
    
    /**
     * Translates an ingredient name to the current language.
     * Used for displaying ingredient names in the UI.
     */
    fun translateIngredientName(ingredientName: String): String {
        return translationSystem.translateIngredient(ingredientName)
    }
    
    /**
     * Translates ingredient names within recipe instructions.
     * This helps translate ingredient names that appear in instruction text,
     * even when the instructions themselves are already translated but contain
     * English ingredient names.
     */
    fun translateInstructions(instructions: String): String {
        return com.familymealplanner.domain.util.InstructionTranslator.translateInstructions(
            instructions,
            translationSystem
        )
    }
    
    fun getServingsMultiplier(): Double {
        val recipe = _recipe.value ?: return 1.0
        return _selectedServings.value.toDouble() / recipe.servings.toDouble()
    }
    
    private fun checkIngredientsAvailability() {
        viewModelScope.launch {
            _recipe.value?.let { recipe ->
                val pantryItems = inventoryRepository.getPantryItems()
                val pantryByName = pantryItems
                    .filter { it.availableQuantity > 0 }
                    .associateBy { it.ingredient.name.lowercase() }
                
                // Track which ingredients are being substituted
                val substitutions = mutableMapOf<String, String>()
                
                // Check each recipe ingredient for substitutions
                recipe.ingredients.forEach recipeLoop@{ bundledIngredient ->
                    val ingredientName = bundledIngredient.name.lowercase()
                    
                    // If ingredient is not in pantry, check for substitutes
                    if (!pantryByName.containsKey(ingredientName)) {
                        val ingredient = ingredientRepository.getIngredientByName(bundledIngredient.name)
                        ingredient?.substitutes?.forEach { substitute ->
                            val substituteName = substitute.substituteIngredient.name.lowercase()
                            if (pantryByName.containsKey(substituteName)) {
                                // Found a substitute that's in the pantry
                                substitutions[bundledIngredient.name] = substitute.substituteIngredient.name
                                return@recipeLoop // Use first available substitute
                            }
                        }
                    }
                }
                
                _ingredientSubstitutions.value = substitutions
                
                val multiplier = getServingsMultiplier()
                val recipeIngredients = recipe.ingredients.map { bundledIngredient ->
                    CheckRecipeIngredientsUseCase.RecipeIngredient(
                        name = bundledIngredient.name,
                        quantity = bundledIngredient.quantity * multiplier,
                        unit = bundledIngredient.unit
                    )
                }
                val result = checkRecipeIngredientsUseCase(recipeIngredients)
                _ingredientCheckResult.value = result
                _hasAllIngredients.value = result is CheckRecipeIngredientsUseCase.Result.AllAvailable
            }
        }
    }
    
    fun setDetailedInstructions(detailed: Boolean) {
        _useDetailedInstructions.value = detailed
    }

    fun buyNecessaryIngredients() {
        viewModelScope.launch {
            val result = _ingredientCheckResult.value
            val recipe = _recipe.value
            
            android.util.Log.d("BundledRecipeViewModel", "buyNecessaryIngredients called")
            
            if (result is CheckRecipeIngredientsUseCase.Result.MissingIngredients && 
                recipe != null) {
                
                android.util.Log.d("BundledRecipeViewModel", "Missing ingredients count: ${result.missing.size}")
                
                // Add all missing ingredients as separate items (no merging)
                result.missing.forEach { missing ->
                    // Look up ingredient using helper function
                    val ingredient = lookupIngredientByEnglishName(missing.name)
                    
                    android.util.Log.d("BundledRecipeViewModel", "buyNecessaryIngredients - Creating grocery item - Original: ${missing.name}, IngredientId: ${ingredient?.id}, Category: ${ingredient?.category}")
                    
                    val newItem = com.familymealplanner.domain.model.GroceryItem(
                        id = java.util.UUID.randomUUID().toString(),
                        ingredientName = missing.name, // Store English name for translation
                        ingredientId = ingredient?.id, // Store ingredient ID for category lookup
                        category = ingredient?.category, // Store category from catalog
                        quantity = missing.required - missing.available,
                        unit = missing.unit,
                        mealName = "${recipe.name} (${context.getString(R.string.groceries_servings, _selectedServings.value.toString())})",
                        isChecked = false,
                        checkedAt = null,
                        createdAt = System.currentTimeMillis()
                    )
                    groceryRepository.addGroceryItem(newItem)
                }
                
                // Show success message and trigger navigation
                _planResult.value = "ingredients_added_success"
            }
        }
    }

    fun dismissMissingIngredientsDialog() {
        _showMissingIngredientsDialog.value = false
    }

    fun addMissingToGroceries() {
        viewModelScope.launch {
            val result = _ingredientCheckResult.value
            val recipe = _recipe.value
            
            android.util.Log.d("BundledRecipeViewModel", "addMissingToGroceries called, result: $result, recipe: ${recipe?.name}")
            
            if (result is CheckRecipeIngredientsUseCase.Result.MissingIngredients && 
                recipe != null) {
                val groceryItems = result.missing.map { missing ->
                    // Look up ingredient using helper function
                    val ingredient = lookupIngredientByEnglishName(missing.name)
                    
                    android.util.Log.d("BundledRecipeViewModel", "Creating grocery item - Original: ${missing.name}, IngredientId: ${ingredient?.id}, Category: ${ingredient?.category}")
                    
                    com.familymealplanner.domain.model.GroceryItem(
                        id = java.util.UUID.randomUUID().toString(),
                        ingredientName = missing.name, // Store English name for translation
                        ingredientId = ingredient?.id, // Store ingredient ID for category lookup
                        quantity = missing.required - missing.available,
                        unit = missing.unit,
                        mealName = recipe.name,
                        isChecked = false,
                        checkedAt = null,
                        createdAt = System.currentTimeMillis()
                    )
                }
                groceryRepository.addGroceryItems(groceryItems)
                _showMissingIngredientsDialog.value = false
                android.util.Log.d("BundledRecipeViewModel", "Setting planResult to ingredients_added_success")
                _planResult.value = "ingredients_added_success" // Show success message
            } else {
                android.util.Log.d("BundledRecipeViewModel", "Condition not met for adding groceries")
            }
        }
    }

    fun showPlanDialog() {
        _showPlanDialog.value = true
    }

    fun dismissPlanDialog() {
        _showPlanDialog.value = false
        _planResult.value = null
    }

    fun planMeal(mealType: com.familymealplanner.domain.model.MealType, plannedDate: Long) {
        viewModelScope.launch {
            val recipe = _recipe.value
            
            if (recipe != null) {
                // Check which ingredients are missing
                val multiplier = getServingsMultiplier()
                val recipeIngredients = recipe.ingredients.map { bundledIngredient ->
                    CheckRecipeIngredientsUseCase.RecipeIngredient(
                        name = bundledIngredient.name,
                        quantity = bundledIngredient.quantity * multiplier,
                        unit = bundledIngredient.unit
                    )
                }
                val ingredientCheck = checkRecipeIngredientsUseCase(recipeIngredients)
                
                // Add missing ingredients to grocery list
                if (ingredientCheck is CheckRecipeIngredientsUseCase.Result.MissingIngredients) {
                    val groceryItems = ingredientCheck.missing.map { missing ->
                        // Look up ingredient using helper function
                        val ingredient = lookupIngredientByEnglishName(missing.name)
                        
                        android.util.Log.d("BundledRecipeViewModel", "planMeal - Creating grocery item - Original: ${missing.name}, IngredientId: ${ingredient?.id}, Category: ${ingredient?.category}")
                        
                        com.familymealplanner.domain.model.GroceryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            ingredientName = missing.name, // Store English name for translation
                            ingredientId = ingredient?.id, // Store ingredient ID for category lookup
                            quantity = missing.required - missing.available,
                            unit = missing.unit,
                            mealName = "${recipe.name} (${context.getString(R.string.groceries_servings, _selectedServings.value.toString())})",
                            mealType = mealType,
                            plannedDate = plannedDate,
                            isChecked = false,
                            checkedAt = null,
                            createdAt = System.currentTimeMillis()
                        )
                    }
                    if (groceryItems.isNotEmpty()) {
                        groceryRepository.addGroceryItems(groceryItems)
                    }
                }
                
                // First, save the bundled recipe as a meal
                val scrapedRecipe = com.familymealplanner.data.remote.ScrapedRecipe(
                    name = recipe.name,
                    ingredients = recipe.ingredients.map { 
                        com.familymealplanner.data.remote.ScrapedIngredient(
                            name = it.name,
                            quantity = it.quantity * multiplier,
                            unit = it.unit,
                            isStarIngredient = it.isStarIngredient
                        )
                    },
                    instructions = recipe.instructions,
                    simpleInstructions = recipe.simpleInstructions,
                    prepTimeMinutes = recipe.prepTimeMinutes,
                    cookTimeMinutes = recipe.cookTimeMinutes,
                    servings = _selectedServings.value
                )
                
                val mealResult = createScrapedMealUseCase(
                    scrapedRecipe = scrapedRecipe,
                    finalName = recipe.name,
                    dishImage = null
                )
                
                mealResult.fold(
                    onSuccess = { meal ->
                        // Reload the meal to get it with ingredients populated
                        val fullMeal = mealRepository.getMealById(meal.id)
                        if (fullMeal == null) {
                            _planResult.value = "Error: Failed to load created meal"
                            return@fold
                        }
                        
                        // Mark the meal as bundled and set the image path
                        val bundledMeal = fullMeal.copy(
                            isBundled = true,
                            imagePath = recipe.imageUrl // Save the bundled recipe image path
                        )
                        mealRepository.updateMeal(
                            bundledMeal, 
                            fullMeal.ingredients.map { 
                                com.familymealplanner.domain.repository.MealIngredientInput(
                                    it.ingredient.id, 
                                    it.quantity,
                                    it.isStarIngredient
                                )
                            }
                        )
                        
                        // Now create the meal plan
                        val planResult = createMealPlanUseCase(
                            mealId = fullMeal.id,
                            plannedDate = plannedDate,
                            mealType = mealType
                        )
                        
                        when (planResult) {
                            is CreateMealPlanUseCase.Result.Success -> {
                                _planResult.value = "meal_planned_no_ingredients" // No missing ingredients
                                _showPlanDialog.value = false
                            }
                            is CreateMealPlanUseCase.Result.InsufficientIngredients -> {
                                _planResult.value = "ingredients_added_planned" // Show success message with ingredients
                                _showPlanDialog.value = false
                            }
                            is CreateMealPlanUseCase.Result.Error -> {
                                _planResult.value = "Error: ${planResult.message}"
                            }
                        }
                    },
                    onFailure = { error ->
                        _planResult.value = "Error: ${error.message}"
                    }
                )
            }
        }
    }

    fun showMadeMealDialog() {
        _showMadeMealDialog.value = true
    }

    fun dismissMadeMealDialog() {
        _showMadeMealDialog.value = false
    }
    
    fun startCooking() {
        viewModelScope.launch {
            val recipe = _recipe.value
            
            if (recipe != null) {
                try {
                    val multiplier = getServingsMultiplier()
                    
                    // Create a ScrapedRecipe from the BundledRecipe
                    val scrapedRecipe = com.familymealplanner.data.remote.ScrapedRecipe(
                        name = recipe.name,
                        ingredients = recipe.ingredients.map { ingredient ->
                            com.familymealplanner.data.remote.ScrapedIngredient(
                                name = ingredient.name,
                                quantity = ingredient.quantity * multiplier,
                                unit = ingredient.unit,
                                isStarIngredient = ingredient.isStarIngredient
                            )
                        },
                        instructions = recipe.instructions,
                        simpleInstructions = recipe.simpleInstructions,
                        prepTimeMinutes = recipe.prepTimeMinutes,
                        cookTimeMinutes = recipe.cookTimeMinutes,
                        servings = _selectedServings.value
                    )
                    
                    // Create a meal from the recipe
                    val mealResult = createScrapedMealUseCase(
                        scrapedRecipe = scrapedRecipe,
                        finalName = recipe.name,
                        dishImage = null
                    )
                    
                    mealResult.fold(
                        onSuccess = { meal ->
                            // Reload the meal to get it with ingredients populated
                            val fullMeal = mealRepository.getMealById(meal.id)
                            if (fullMeal == null) {
                                _madeMealResult.value = MadeMealResult(
                                    success = false,
                                    message = localeContext.getString(R.string.recipe_failed_load_meal)
                                )
                                return@fold
                            }
                            
                            // Mark the meal as bundled and set the image path
                            val bundledMeal = fullMeal.copy(
                                isBundled = true,
                                imagePath = recipe.imageUrl
                            )
                            mealRepository.updateMeal(
                                bundledMeal, 
                                fullMeal.ingredients.map { 
                                    com.familymealplanner.domain.repository.MealIngredientInput(
                                        it.ingredient.id, 
                                        it.quantity,
                                        it.isStarIngredient
                                    )
                                }
                            )
                            
                            // Create a meal plan with PLANNED status
                            val mealPlanResult = createMealPlanUseCase(
                                mealId = fullMeal.id,
                                plannedDate = System.currentTimeMillis(),
                                mealType = com.familymealplanner.domain.model.MealType.DINNER
                            )
                            
                            when (mealPlanResult) {
                                is CreateMealPlanUseCase.Result.Success -> {
                                    // Now start cooking
                                    val cookingResult = startCookingUseCase(mealPlanResult.mealPlan.id)
                                    
                                    when (cookingResult) {
                                        is com.familymealplanner.domain.usecase.StartCookingUseCase.Result.Success -> {
                                            _madeMealResult.value = MadeMealResult(
                                                success = true,
                                                message = localeContext.getString(R.string.recipe_started_cooking)
                                            )
                                        }
                                        is com.familymealplanner.domain.usecase.StartCookingUseCase.Result.InsufficientIngredients -> {
                                            _madeMealResult.value = MadeMealResult(
                                                success = false,
                                                message = localeContext.getString(R.string.recipe_missing_ingredients_list, cookingResult.shortages.joinToString { it.ingredientName })
                                            )
                                        }
                                        is com.familymealplanner.domain.usecase.StartCookingUseCase.Result.Error -> {
                                            _madeMealResult.value = MadeMealResult(
                                                success = false,
                                                message = cookingResult.message
                                            )
                                        }
                                    }
                                }
                                is CreateMealPlanUseCase.Result.InsufficientIngredients -> {
                                    _madeMealResult.value = MadeMealResult(
                                        success = false,
                                        message = localeContext.getString(R.string.recipe_missing_ingredients_list, mealPlanResult.shortages.joinToString { it.ingredientName })
                                    )
                                }
                                is CreateMealPlanUseCase.Result.Error -> {
                                    _madeMealResult.value = MadeMealResult(
                                        success = false,
                                        message = mealPlanResult.message
                                    )
                                }
                            }
                        },
                        onFailure = { error ->
                            _madeMealResult.value = MadeMealResult(
                                success = false,
                                message = localeContext.getString(R.string.recipe_failed_create_meal, error.message)
                            )
                        }
                    )
                } catch (e: Exception) {
                    _madeMealResult.value = MadeMealResult(
                        success = false,
                        message = localeContext.getString(R.string.recipe_failed_start_cooking, e.message ?: "")
                    )
                }
            }
        }
    }

    fun confirmMadeMeal() {
        viewModelScope.launch {
            val recipe = _recipe.value
            
            if (recipe != null) {
                try {
                    val multiplier = getServingsMultiplier()
                    
                    // Filter out non-deductible ingredients
                    val deductibleIngredients = recipe.ingredients.filter { ingredient ->
                        com.familymealplanner.domain.model.NonDeductibleIngredients.shouldDeduct(
                            ingredient.name,
                            ingredient.unit
                        )
                    }

                    if (deductibleIngredients.isEmpty()) {
                        _madeMealResult.value = MadeMealResult(
                            success = true,
                            message = localeContext.getString(R.string.recipe_meal_marked_made)
                        )
                        _showMadeMealDialog.value = false
                        return@launch
                    }

                    // Check ingredient availability and create transactions
                    val now = System.currentTimeMillis()
                    val transactions = mutableListOf<com.familymealplanner.domain.model.InventoryTransaction>()
                    val missingIngredients = mutableListOf<String>()

                    for (ingredient in deductibleIngredients) {
                        // Apply servings multiplier to quantity
                        val adjustedQuantity = ingredient.quantity * multiplier
                        
                        // Find the ingredient in the database by name
                        val ingredientEntity = findIngredientByName(ingredient.name)
                        
                        if (ingredientEntity != null) {
                            val available = inventoryRepository.getAvailableQuantity(ingredientEntity.id)
                            
                            if (available >= adjustedQuantity) {
                                // Create committed transaction to deduct from pantry
                                val transaction = com.familymealplanner.domain.model.InventoryTransaction(
                                    id = java.util.UUID.randomUUID().toString(),
                                    ingredientId = ingredientEntity.id,
                                    quantityChange = -adjustedQuantity,
                                    status = com.familymealplanner.domain.model.TransactionStatus.COMMITTED,
                                    reason = "Made: ${recipe.name} (${context.getString(R.string.groceries_servings, _selectedServings.value.toString())})",
                                    mealPlanId = null,
                                    createdAt = now,
                                    updatedAt = now
                                )
                                transactions.add(transaction)
                            } else {
                                missingIngredients.add("${ingredient.name} (need ${adjustedQuantity} ${ingredient.unit}, have $available ${ingredient.unit})")
                            }
                        } else {
                            missingIngredients.add("${ingredient.name} (not in pantry)")
                        }
                    }

                    // Create all transactions
                    if (transactions.isNotEmpty()) {
                        inventoryRepository.createTransactions(transactions)
                    }

                    // Show result
                    _madeMealResult.value = if (missingIngredients.isEmpty()) {
                        MadeMealResult(
                            success = true,
                            message = localeContext.getString(R.string.recipe_ingredients_deducted)
                        )
                    } else {
                        MadeMealResult(
                            success = false,
                            message = localeContext.getString(R.string.recipe_some_missing)
                        )
                    }
                    _showMadeMealDialog.value = false
                } catch (e: Exception) {
                    _madeMealResult.value = MadeMealResult(
                        success = false,
                        message = localeContext.getString(R.string.recipe_error, e.message ?: "")
                    )
                    _showMadeMealDialog.value = false
                }
            }
        }
    }

    fun clearMadeMealResult() {
        _madeMealResult.value = null
    }

    private suspend fun findIngredientByName(name: String): com.familymealplanner.domain.model.Ingredient? {
        // Get all pantry items and find matching ingredient
        val pantryItems = inventoryRepository.getPantryItems()
        return pantryItems.find { 
            it.ingredient.name.equals(name, ignoreCase = true) 
        }?.ingredient
    }
    
    /**
     * Updates meal name with count multiplier
     * Examples:
     * - "Recipe" + "Recipe" = "Recipe x2"
     * - "Recipe x2" + "Recipe" = "Recipe x3"
     * - "Recipe A, Recipe B" + "Recipe A" = "Recipe A x2, Recipe B"
     */
    private fun updateMealNameWithCount(existingMealName: String?, newMealName: String): String {
        if (existingMealName.isNullOrBlank()) {
            return newMealName
        }
        
        // Split by comma to handle multiple recipes
        val meals = existingMealName.split(",").map { it.trim() }
        val mealCounts = mutableMapOf<String, Int>()
        
        // Parse existing meal names and their counts
        meals.forEach { meal ->
            val multiplierMatch = Regex("""(.+?)\s+x(\d+)$""").find(meal)
            if (multiplierMatch != null) {
                val (name, count) = multiplierMatch.destructured
                mealCounts[name.trim()] = count.toInt()
            } else {
                mealCounts[meal] = mealCounts.getOrDefault(meal, 0) + 1
            }
        }
        
        // Add the new meal
        mealCounts[newMealName] = mealCounts.getOrDefault(newMealName, 0) + 1
        
        // Format the result
        return mealCounts.entries.joinToString(", ") { (name, count) ->
            if (count > 1) "$name x$count" else name
        }
    }
}


