package com.familymealplanner.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familymealplanner.R
import com.familymealplanner.data.local.ImageStorage
import com.familymealplanner.domain.model.Meal
import com.familymealplanner.domain.model.UnitConversion
import com.familymealplanner.domain.repository.MealRepository
import com.familymealplanner.domain.usecase.CheckRecipeIngredientsUseCase
import com.familymealplanner.domain.usecase.CreateMealPlanUseCase
import com.familymealplanner.domain.usecase.DeleteMealUseCase
import com.familymealplanner.ui.util.RecipeImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localeManager: com.familymealplanner.data.preferences.LocaleManager,
    private val mealRepository: MealRepository,
    private val deleteMealUseCase: DeleteMealUseCase,
    private val imageStorage: ImageStorage,
    private val checkRecipeIngredientsUseCase: CheckRecipeIngredientsUseCase,
    private val groceryRepository: com.familymealplanner.domain.repository.GroceryRepository,
    private val createMealPlanUseCase: CreateMealPlanUseCase,
    private val preferences: com.familymealplanner.data.preferences.OnboardingPreferences,
    private val mealPlanRepository: com.familymealplanner.domain.repository.MealPlanRepository,
    private val inventoryRepository: com.familymealplanner.domain.repository.InventoryRepository,
    private val translationSystem: com.familymealplanner.data.local.TranslationSystem
) : ViewModel() {

    private val localeContext: Context by lazy {
        localeManager.applyLocale(context)
    }

    private val _meal = MutableStateFlow<Meal?>(null)
    val meal: StateFlow<Meal?> = _meal.asStateFlow()

    private val _isDeleted = MutableStateFlow(false)
    val isDeleted: StateFlow<Boolean> = _isDeleted.asStateFlow()
    
    private val _useDetailedInstructions = MutableStateFlow(true)
    val useDetailedInstructions: StateFlow<Boolean> = _useDetailedInstructions.asStateFlow()

    private val _ingredientCheckResult = MutableStateFlow<CheckRecipeIngredientsUseCase.Result?>(null)
    val ingredientCheckResult: StateFlow<CheckRecipeIngredientsUseCase.Result?> = _ingredientCheckResult.asStateFlow()

    private val _showMissingIngredientsDialog = MutableStateFlow(false)
    val showMissingIngredientsDialog: StateFlow<Boolean> = _showMissingIngredientsDialog.asStateFlow()

    private val _showPlanDialog = MutableStateFlow(false)
    val showPlanDialog: StateFlow<Boolean> = _showPlanDialog.asStateFlow()

    private val _planResult = MutableStateFlow<String?>(null)
    val planResult: StateFlow<String?> = _planResult.asStateFlow()

    private val _showMadeMealDialog = MutableStateFlow(false)
    val showMadeMealDialog: StateFlow<Boolean> = _showMadeMealDialog.asStateFlow()

    data class MadeMealResult(val success: Boolean, val message: String)
    private val _madeMealResult = MutableStateFlow<MadeMealResult?>(null)
    val madeMealResult: StateFlow<MadeMealResult?> = _madeMealResult.asStateFlow()

    private val _hasAllIngredients = MutableStateFlow(true)
    val hasAllIngredients: StateFlow<Boolean> = _hasAllIngredients

    private val _selectedServings = MutableStateFlow(2)
    val selectedServings: StateFlow<Int> = _selectedServings

    fun loadMeal(mealId: String) {
        viewModelScope.launch {
            _meal.value = mealRepository.getMealById(mealId)
            
            // For scraped/manual recipes, set selected servings to the recipe's original servings
            // Only use default for bundled recipes or if servings is null
            val meal = _meal.value
            if (meal != null) {
                val originalServings = meal.servings ?: 2
                // If recipe has more than 4 servings, keep it as-is (no cycling)
                // Otherwise, set to original servings
                _selectedServings.value = originalServings
            }
            
            // Check ingredient availability when meal loads
            checkIngredientsAvailability()
        }
    }
    
    fun cycleServings() {
        val meal = _meal.value ?: return
        val originalServings = meal.servings ?: 2
        
        // Don't allow cycling for recipes with more than 6 servings
        if (originalServings > 6) {
            return
        }
        
        _selectedServings.value = when (_selectedServings.value) {
            1 -> 2
            2 -> 4
            4 -> 6
            else -> 1
        }
        // Recheck ingredients when servings change
        checkIngredientsAvailability()
    }
    
    fun getServingsMultiplier(): Double {
        val meal = _meal.value ?: return 1.0
        val originalServings = meal.servings ?: 2
        return _selectedServings.value.toDouble() / originalServings.toDouble()
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
    
    /**
     * Translates an ingredient name to the current locale.
     */
    fun translateIngredientName(ingredientName: String): String {
        return translationSystem.translateIngredient(ingredientName)
    }
    
    private fun checkIngredientsAvailability() {
        viewModelScope.launch {
            _meal.value?.let { meal ->
                val multiplier = getServingsMultiplier()
                val recipeIngredients = meal.ingredients.map { mealIngredient ->
                    CheckRecipeIngredientsUseCase.RecipeIngredient(
                        name = mealIngredient.ingredient.name,
                        quantity = mealIngredient.quantity * multiplier,
                        unit = mealIngredient.ingredient.unit
                    )
                }
                val result = checkRecipeIngredientsUseCase(recipeIngredients)
                _ingredientCheckResult.value = result
                _hasAllIngredients.value = result is CheckRecipeIngredientsUseCase.Result.AllAvailable
            }
        }
    }

    fun deleteMeal() {
        viewModelScope.launch {
            _meal.value?.let { meal ->
                deleteMealUseCase(meal)
                _isDeleted.value = true
            }
        }
    }

    fun updateImage(uri: Uri, onComplete: () -> Unit) {
        viewModelScope.launch {
            _meal.value?.let { meal ->
                val imagePath = imageStorage.saveFromUri(uri, meal.id)
                if (imagePath != null) {
                    mealRepository.updateMealImage(meal.id, imagePath)
                    // Reload meal to get updated image path
                    _meal.value = mealRepository.getMealById(meal.id)
                }
                onComplete()
            }
        }
    }
    
    fun setDetailedInstructions(detailed: Boolean) {
        _useDetailedInstructions.value = detailed
    }

    fun checkIngredients() {
        viewModelScope.launch {
            _meal.value?.let { meal ->
                val multiplier = getServingsMultiplier()
                val recipeIngredients = meal.ingredients.map { mealIngredient ->
                    CheckRecipeIngredientsUseCase.RecipeIngredient(
                        name = mealIngredient.ingredient.name,
                        quantity = mealIngredient.quantity * multiplier,
                        unit = mealIngredient.ingredient.unit
                    )
                }
                val result = checkRecipeIngredientsUseCase(recipeIngredients)
                _ingredientCheckResult.value = result
                
                when (result) {
                    is CheckRecipeIngredientsUseCase.Result.MissingIngredients -> {
                        _showMissingIngredientsDialog.value = true
                    }
                    is CheckRecipeIngredientsUseCase.Result.AllAvailable -> {
                        // All ingredients available, could show success message or navigate to cooking
                    }
                }
            }
        }
    }

    fun dismissMissingIngredientsDialog() {
        _showMissingIngredientsDialog.value = false
    }

    fun addMissingToGroceries() {
        viewModelScope.launch {
            val result = _ingredientCheckResult.value
            val meal = _meal.value
            
            android.util.Log.d("RecipeDetail", "=== addMissingToGroceries called ===")
            android.util.Log.d("RecipeDetail", "Meal: ${meal?.name}")
            
            if (result is CheckRecipeIngredientsUseCase.Result.MissingIngredients && 
                meal != null) {
                
                android.util.Log.d("RecipeDetail", "Missing ingredients count: ${result.missing.size}")
                
                val groceryItems = result.missing.map { missing ->
                    android.util.Log.d("RecipeDetail", "Processing missing ingredient: ${missing.name}")
                    
                    // Find the ingredient in the meal to get its ID
                    val mealIngredient = meal.ingredients.find { 
                        it.ingredient.name == missing.name 
                    }
                    
                    android.util.Log.d("RecipeDetail", "Found meal ingredient: ${mealIngredient?.ingredient?.name}, ID: ${mealIngredient?.ingredient?.id}, Category: ${mealIngredient?.ingredient?.category}")
                    
                    com.familymealplanner.domain.model.GroceryItem(
                        id = java.util.UUID.randomUUID().toString(),
                        ingredientName = missing.name,
                        ingredientId = mealIngredient?.ingredient?.id, // Store ingredient ID directly
                        quantity = missing.required - missing.available,
                        unit = missing.unit,
                        mealName = meal.name,
                        isChecked = false,
                        checkedAt = null,
                        createdAt = System.currentTimeMillis()
                    )
                }
                groceryRepository.addGroceryItems(groceryItems)
                _showMissingIngredientsDialog.value = false
            }
        }
    }

    fun buyNecessaryIngredients() {
        viewModelScope.launch {
            val result = _ingredientCheckResult.value
            val meal = _meal.value
            
            android.util.Log.d("RecipeDetail", "=== buyNecessaryIngredients called ===")
            android.util.Log.d("RecipeDetail", "Meal: ${meal?.name}")
            
            if (result is CheckRecipeIngredientsUseCase.Result.MissingIngredients && 
                meal != null) {
                
                android.util.Log.d("RecipeDetail", "Missing ingredients count: ${result.missing.size}")
                
                // Add all missing ingredients as separate items (no merging)
                result.missing.forEach { missing ->
                    android.util.Log.d("RecipeDetail", "Processing missing ingredient: ${missing.name}")
                    
                    // Find the ingredient in the meal to get its ID
                    val mealIngredient = meal.ingredients.find { 
                        it.ingredient.name == missing.name 
                    }
                    
                    android.util.Log.d("RecipeDetail", "Found meal ingredient: ${mealIngredient?.ingredient?.name}, ID: ${mealIngredient?.ingredient?.id}, Category: ${mealIngredient?.ingredient?.category}")
                    
                    val newItem = com.familymealplanner.domain.model.GroceryItem(
                        id = java.util.UUID.randomUUID().toString(),
                        ingredientName = missing.name,
                        ingredientId = mealIngredient?.ingredient?.id, // Store ingredient ID directly
                        quantity = missing.required - missing.available,
                        unit = missing.unit,
                        mealName = meal.name,
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

    fun showPlanDialog() {
        _showPlanDialog.value = true
    }

    fun dismissPlanDialog() {
        _showPlanDialog.value = false
        _planResult.value = null
    }

    fun planMeal(mealType: com.familymealplanner.domain.model.MealType, plannedDate: Long) {
        viewModelScope.launch {
            val meal = _meal.value
            
            android.util.Log.d("RecipeDetail", "=== planMeal called ===")
            android.util.Log.d("RecipeDetail", "Meal: ${meal?.name}, Type: $mealType")
            
            if (meal != null) {
                // Check which ingredients are missing
                val multiplier = getServingsMultiplier()
                val recipeIngredients = meal.ingredients.map { mealIngredient ->
                    CheckRecipeIngredientsUseCase.RecipeIngredient(
                        name = mealIngredient.ingredient.name,
                        quantity = mealIngredient.quantity * multiplier,
                        unit = mealIngredient.ingredient.unit
                    )
                }
                val ingredientCheck = checkRecipeIngredientsUseCase(recipeIngredients)
                
                // Add missing ingredients to grocery list
                if (ingredientCheck is CheckRecipeIngredientsUseCase.Result.MissingIngredients) {
                    val groceryItems = ingredientCheck.missing.map { missing ->
                        android.util.Log.d("RecipeDetail", "Processing missing ingredient: ${missing.name}")
                        
                        // Find the ingredient in the meal to get its ID
                        val mealIngredient = meal.ingredients.find { 
                            it.ingredient.name == missing.name 
                        }
                        
                        android.util.Log.d("RecipeDetail", "Found meal ingredient: ${mealIngredient?.ingredient?.name}, ID: ${mealIngredient?.ingredient?.id}, Category: ${mealIngredient?.ingredient?.category}")
                        
                        com.familymealplanner.domain.model.GroceryItem(
                            id = java.util.UUID.randomUUID().toString(),
                            ingredientName = missing.name,
                            ingredientId = mealIngredient?.ingredient?.id, // Store ingredient ID directly
                            quantity = missing.required - missing.available,
                            unit = missing.unit,
                            mealName = meal.name,
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
                
                // Create the meal plan
                val result = createMealPlanUseCase(
                    mealId = meal.id,
                    plannedDate = plannedDate,
                    mealType = mealType
                )
                
                when (result) {
                    is CreateMealPlanUseCase.Result.Success -> {
                        _planResult.value = "ingredients_added_planned"
                        _showPlanDialog.value = false
                    }
                    is CreateMealPlanUseCase.Result.InsufficientIngredients -> {
                        _planResult.value = "ingredients_added_planned"
                        _showPlanDialog.value = false
                    }
                    is CreateMealPlanUseCase.Result.Error -> {
                        _planResult.value = "Error: ${result.message}"
                    }
                }
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
            val meal = _meal.value
            
            if (meal != null) {
                try {
                    // Create a meal plan with PLANNED status
                    val mealPlanResult = createMealPlanUseCase(
                        mealId = meal.id,
                        plannedDate = System.currentTimeMillis(),
                        mealType = com.familymealplanner.domain.model.MealType.DINNER
                    )
                    
                    when (mealPlanResult) {
                        is CreateMealPlanUseCase.Result.Success -> {
                            // Now start cooking
                            val startCookingUseCase = com.familymealplanner.domain.usecase.StartCookingUseCase(
                                mealPlanRepository = mealPlanRepository,
                                inventoryRepository = inventoryRepository
                            )
                            
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
            val meal = _meal.value
            
            if (meal != null) {
                try {
                    // Filter out non-deductible ingredients
                    val deductibleIngredients = meal.ingredients.filter { mealIngredient ->
                        com.familymealplanner.domain.model.NonDeductibleIngredients.shouldDeduct(
                            mealIngredient.ingredient.name,
                            mealIngredient.ingredient.unit
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

                    for (mealIngredient in deductibleIngredients) {
                        val available = inventoryRepository.getAvailableQuantity(mealIngredient.ingredient.id)
                        
                        if (available >= mealIngredient.quantity) {
                            // Create committed transaction to deduct from pantry
                            val transaction = com.familymealplanner.domain.model.InventoryTransaction(
                                id = java.util.UUID.randomUUID().toString(),
                                ingredientId = mealIngredient.ingredient.id,
                                quantityChange = -mealIngredient.quantity,
                                status = com.familymealplanner.domain.model.TransactionStatus.COMMITTED,
                                reason = "Made: ${meal.name}",
                                mealPlanId = null,
                                createdAt = now,
                                updatedAt = now
                            )
                            transactions.add(transaction)
                        } else {
                            missingIngredients.add("${mealIngredient.ingredient.name} (need ${mealIngredient.quantity} ${mealIngredient.ingredient.unit}, have $available ${mealIngredient.ingredient.unit})")
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailScreen(
    mealId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToPlan: () -> Unit = {},
    onNavigateToGroceries: () -> Unit = {},
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val meal by viewModel.meal.collectAsState()
    val isDeleted by viewModel.isDeleted.collectAsState()
    val useDetailedInstructions by viewModel.useDetailedInstructions.collectAsState()
    val showPlanDialog by viewModel.showPlanDialog.collectAsState()
    val planResult by viewModel.planResult.collectAsState()
    val showMadeMealDialog by viewModel.showMadeMealDialog.collectAsState()
    val madeMealResult by viewModel.madeMealResult.collectAsState()
    val hasAllIngredients by viewModel.hasAllIngredients.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isUploadingImage = true
            viewModel.updateImage(it) {
                isUploadingImage = false
            }
        }
    }

    LaunchedEffect(mealId) {
        viewModel.loadMeal(mealId)
    }

    LaunchedEffect(isDeleted) {
        if (isDeleted) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = meal?.name ?: "Recipe",
                        style = if ((meal?.name?.length ?: 0) > 30) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.headlineSmall
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.error,
                                    shape = androidx.compose.foundation.shape.CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        meal?.let { recipe ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recipe image or add image button
                if (!recipe.imagePath.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    ) {
                        RecipeImage(
                            imagePath = recipe.imagePath,
                            contentDescription = recipe.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        )
                        if (isUploadingImage) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                } else {
                    // Add photo button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator()
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add photo",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add a photo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recipe info card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val items = mutableListOf<@Composable () -> Unit>()
                            
                            // Calculate time adjustments based on servings and ingredient count
                            val selectedServings by viewModel.selectedServings.collectAsState()
                            
                            val prepTimeAdjustment = when (selectedServings) {
                                4 -> if (recipe.ingredients.size < 10) 5 else 10
                                6 -> if (recipe.ingredients.size < 10) 15 else 20
                                else -> 0
                            }
                            
                            val cookTimeAdjustment = if ((recipe.cookTimeMinutes ?: 0) > 0) {
                                when (selectedServings) {
                                    4 -> if (recipe.ingredients.size < 8) 5 else 10
                                    6 -> if (recipe.ingredients.size < 8) 10 else 15
                                    else -> 0
                                }
                            } else {
                                0
                            }
                            
                            recipe.prepTimeMinutes?.let { basePrepTime ->
                                val adjustedPrepTime = basePrepTime + prepTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedPrepTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_prep)) }
                            }
                            recipe.cookTimeMinutes?.let { baseCookTime ->
                                val adjustedCookTime = baseCookTime + cookTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedCookTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_cook)) }
                            }
                            recipe.servings?.let { originalServings ->
                                // Only allow clicking if servings <= 6
                                val canCycle = originalServings <= 6
                                items.add { 
                                    InfoColumn(
                                        value = "$selectedServings", 
                                        label = stringResource(R.string.recipe_servings),
                                        clickable = canCycle,
                                        onClick = { viewModel.cycleServings() }
                                    ) 
                                }
                            }
                            
                            // Calculate total time dynamically with adjustments
                            val baseTotalTime = (recipe.prepTimeMinutes ?: 0) + (recipe.cookTimeMinutes ?: 0)
                            if (baseTotalTime > 0) {
                                val adjustedTotalTime = baseTotalTime + prepTimeAdjustment + cookTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedTotalTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_total)) }
                            }
                            
                            items.forEachIndexed { index, item ->
                                item()
                                if (index < items.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(MaterialTheme.colorScheme.outlineVariant)
                                    )
                                }
                            }
                        }
                    }

                    // Category Badges
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Source badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (recipe.isScraped) {
                                        stringResource(R.string.recipe_type_scraped)
                                    } else {
                                        stringResource(R.string.recipe_type_manual)
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Meal Type badge
                        recipe.mealType?.let { mealType ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mealType.emoji,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = mealType.getLocalizedName(context),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        // Dish Category badge
                        recipe.dishCategory?.let { category ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.emoji,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = category.getLocalizedName(context),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Plan it and conditional button (Buy ingredients / Let's cook)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showPlanDialog() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(stringResource(R.string.recipe_plan_it))
                        }
                        
                        if (hasAllIngredients) {
                            Button(
                                onClick = { viewModel.startCooking() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_lets_cook))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.buyNecessaryIngredients() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_buy_ingredients))
                            }
                        }
                    }

                    // Ingredients section with allergens
                    val allergens = remember(recipe.ingredients) {
                        recipe.ingredients
                            .flatMap { it.ingredient.allergens }
                            .distinctBy { it.id }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.recipe_ingredients),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        // Allergen chips - display all with wrapping
                        if (allergens.isNotEmpty()) {
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f).wrapContentHeight(),
                                maxItemsInEachRow = Int.MAX_VALUE
                            ) {
                                allergens.forEach { allergen ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = getAllergenTranslation(allergen.name),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Observe selectedServings outside the Card to ensure recomposition
                    val currentSelectedServings by viewModel.selectedServings.collectAsState()
                    
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (recipe.ingredients.isEmpty()) {
                                Text(
                                    text = "No ingredients listed",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                // Calculate multiplier based on current servings
                                val originalServings = recipe.servings ?: 2
                                val servingsMultiplier = currentSelectedServings.toDouble() / originalServings.toDouble()
                                
                                recipe.ingredients.forEach { ingredient ->
                                    val adjustedQuantity = ingredient.quantity * servingsMultiplier
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (ingredient.isStarIngredient) {
                                                    Icon(
                                                        imageVector = androidx.compose.material.icons.Icons.Filled.Star,
                                                        contentDescription = "Essential ingredient",
                                                        tint = Color(0xFFFFD700),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                // Translate ingredient name for display
                                                val translatedName = viewModel.translateIngredientName(ingredient.ingredient.name)
                                                Text(
                                                    text = translatedName.replaceFirstChar { 
                                                        if (it.isLowerCase()) it.titlecase() else it.toString() 
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = run {
                                                    val formatted = UnitConversion.formatForDisplay(
                                                        adjustedQuantity,
                                                        ingredient.ingredient.unit
                                                    )
                                                    // Extract quantity and unit, then translate unit
                                                    val parts = formatted.split(" ", limit = 2)
                                                    if (parts.size == 2) {
                                                        "${parts[0]} ${getUnitTranslation(parts[1])}"
                                                    } else {
                                                        formatted
                                                    }
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Instructions section with toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.recipe_instructions),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        // Show toggle only if both instructions exist
                        if (recipe.instructions != null && recipe.simpleInstructions != null) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.setDetailedInstructions(true) },
                                    colors = if (useDetailedInstructions) {
                                        ButtonDefaults.buttonColors()
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    border = if (!useDetailedInstructions) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    } else null,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(stringResource(R.string.recipe_detailed))
                                }
                                Button(
                                    onClick = { viewModel.setDetailedInstructions(false) },
                                    colors = if (!useDetailedInstructions) {
                                        ButtonDefaults.buttonColors()
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    border = if (useDetailedInstructions) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    } else null,
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(stringResource(R.string.recipe_simple))
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val instructionsToShow = when {
                                recipe.simpleInstructions != null && !useDetailedInstructions -> recipe.simpleInstructions
                                else -> recipe.instructions
                            }
                            
                            instructionsToShow?.let { instructions ->
                                // Translate ingredient names within instructions
                                val translatedInstructions = viewModel.translateInstructions(instructions)
                                
                                val steps = translatedInstructions
                                    .split(Regex("\n\n+"))
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                steps.forEachIndexed { index, step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)
                                    )
                                }
                            } ?: Text(
                                text = "No instructions available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(R.string.recipe_delete_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = stringResource(R.string.recipe_delete_message, meal?.name ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteMeal()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.recipe_delete))
                    }
                    Button(
                        onClick = { showDeleteDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                }
            },
            dismissButton = { }
        )
    }

    // Plan meal dialog
    if (showPlanDialog) {
        PlanMealDialog(
            mealName = meal?.name ?: "",
            defaultMealType = meal?.mealType,
            onDismiss = { viewModel.dismissPlanDialog() },
            onPlan = { mealType, date ->
                viewModel.planMeal(mealType, date)
            }
        )
    }

    // Show plan result dialog
    planResult?.let { message ->
        if (message == "ingredients_added_success" || message == "ingredients_added_planned") {
            val titleRes = if (message == "ingredients_added_planned") {
                R.string.recipe_success_planned
            } else {
                R.string.recipe_success
            }
            
            AlertDialog(
                onDismissRequest = { viewModel.dismissPlanDialog() },
                title = {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = stringResource(R.string.recipe_ingredients_added),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {},
                dismissButton = {},
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(28.dp)
            )
            
            // Handle navigation and dismissal
            LaunchedEffect(message) {
                // Navigate to appropriate screen in background
                kotlinx.coroutines.delay(1000)
                if (message == "ingredients_added_planned") {
                    onNavigateToPlan()
                } else {
                    onNavigateToGroceries()
                }
                
                // Dismiss dialog after showing for 4 seconds total
                kotlinx.coroutines.delay(3000)
                viewModel.dismissPlanDialog()
            }
        } else if (message.startsWith("Error:")) {
            // Show error message
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                viewModel.dismissPlanDialog()
            }
        }
    }

    // Made meal confirmation dialog
    if (showMadeMealDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMadeMealDialog() },
            title = { Text(stringResource(R.string.recipe_confirm_meal), style = MaterialTheme.typography.titleMedium) },
            text = { 
                Text(stringResource(R.string.recipe_confirm_made_no_servings, meal?.name ?: ""))
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmMadeMeal() }) {
                    Text(stringResource(R.string.recipe_yes_made_it))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissMadeMealDialog() }) {
                    Text(stringResource(R.string.recipe_cancel))
                }
            }
        )
    }

    // Show made meal result
    madeMealResult?.let { result ->
        LaunchedEffect(result) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMadeMealResult()
        }
        
        AlertDialog(
            onDismissRequest = { viewModel.clearMadeMealResult() },
            containerColor = MaterialTheme.colorScheme.background,
            text = { 
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (result.success) stringResource(R.string.recipe_success) else stringResource(R.string.recipe_notice),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (result.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = { }
        )
    }
    
    // Missing ingredients dialog
    val showMissingDialog by viewModel.showMissingIngredientsDialog.collectAsState()
    val ingredientCheckResult by viewModel.ingredientCheckResult.collectAsState()
    
    if (showMissingDialog) {
        val missingResult = ingredientCheckResult as? CheckRecipeIngredientsUseCase.Result.MissingIngredients
        
        missingResult?.let { result ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissMissingIngredientsDialog() },
                title = { 
                    Text(
                        stringResource(R.string.recipe_missing_ingredients),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        result.missing.forEach { missing ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Translate ingredient name for display
                                    val translatedName = viewModel.translateIngredientName(missing.name)
                                    Text(
                                        text = translatedName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stringResource(R.string.meal_plan_need)}: ${UnitConversion.formatQuantity(missing.required)} ${getUnitTranslation(missing.unit)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${stringResource(R.string.meal_plan_have)}: ${UnitConversion.formatQuantity(missing.available)} ${getUnitTranslation(missing.unit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    
                                    // Show substitute if available
                                    missing.substitute?.let { sub ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                        Text(
                                            text = "✓ Substitute available: ${sub.name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { 
                        viewModel.addMissingToGroceries()
                    }) {
                        Text(stringResource(R.string.recipe_add_to_groceries))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissMissingIngredientsDialog() }) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanMealDialog(
    mealName: String,
    defaultMealType: com.familymealplanner.domain.model.MealType?,
    onDismiss: () -> Unit,
    onPlan: (com.familymealplanner.domain.model.MealType, Long) -> Unit
) {
    // Use the meal's existing mealType or fallback to DINNER
    val initialMealType = defaultMealType ?: com.familymealplanner.domain.model.MealType.DINNER
    
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = { 
            Text(
                text = mealName, 
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.recipe_when_plan),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Meal type selection - Main meals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    listOf(
                        com.familymealplanner.domain.model.MealType.BREAKFAST to R.string.plan_breakfast,
                        com.familymealplanner.domain.model.MealType.LUNCH to R.string.plan_lunch,
                        com.familymealplanner.domain.model.MealType.DINNER to R.string.plan_dinner
                    ).forEach { (mealType, stringRes) ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(stringResource(stringRes)) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // Meal type selection - Snacks and Desserts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    listOf(
                        com.familymealplanner.domain.model.MealType.SNACK to R.string.plan_snack,
                        com.familymealplanner.domain.model.MealType.DESSERT to R.string.plan_dessert
                    ).forEach { (mealType, stringRes) ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(stringResource(stringRes)) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // Date selection
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatDate(selectedDate))
                }
                
                // Full width buttons stacked vertically
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPlan(selectedMealType, selectedDate) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(stringResource(R.string.recipe_plan))
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                        .offset(y = (-16).dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showDatePicker = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate = it }
                            showDatePicker = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.recipe_ok))
                    }
                }
            },
            dismissButton = {},
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val year = calendar.get(java.util.Calendar.YEAR)
    return "$day/$month/$year"
}

@Composable
private fun InfoColumn(
    value: String, 
    label: String,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (clickable) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        }
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (clickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun getAllergenTranslation(allergenName: String): String {
    return when (allergenName.lowercase()) {
        "gluten" -> stringResource(R.string.allergen_gluten)
        "dairy" -> stringResource(R.string.allergen_dairy)
        "eggs" -> stringResource(R.string.allergen_eggs)
        "tree nuts" -> stringResource(R.string.allergen_tree_nuts)
        "peanuts" -> stringResource(R.string.allergen_peanuts)
        "soy" -> stringResource(R.string.allergen_soy)
        "fish" -> stringResource(R.string.allergen_fish)
        "shellfish" -> stringResource(R.string.allergen_shellfish)
        "sesame" -> stringResource(R.string.allergen_sesame)
        else -> allergenName
    }
}

@Composable
private fun getUnitTranslation(unit: String): String {
    return when (unit.lowercase()) {
        "g" -> stringResource(R.string.unit_g)
        "ml" -> stringResource(R.string.unit_ml)
        "kg" -> stringResource(R.string.unit_kg)
        "l" -> stringResource(R.string.unit_l)
        "cup" -> stringResource(R.string.unit_cup)
        "tbsp" -> stringResource(R.string.unit_tbsp)
        "tsp" -> stringResource(R.string.unit_tsp)
        "pcs", "piece" -> stringResource(R.string.unit_piece)
        "oz" -> stringResource(R.string.unit_oz)
        "lb" -> stringResource(R.string.unit_lb)
        else -> unit
    }
}


