package com.littlechef.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littlechef.app.data.preferences.OnboardingPreferences
import com.littlechef.app.domain.model.GroceryItem
import com.littlechef.app.domain.model.IngredientCatalog
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.domain.repository.GroceryRepository
import com.littlechef.app.domain.usecase.AddGroceryItemToPantryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroceriesViewModel @Inject constructor(
    private val groceryRepository: GroceryRepository,
    private val addGroceryItemToPantryUseCase: AddGroceryItemToPantryUseCase,
    val preferences: OnboardingPreferences,
    val ingredientRepository: com.littlechef.app.domain.repository.IngredientRepository,
    private val inventoryRepository: com.littlechef.app.domain.repository.InventoryRepository,
    private val translationSystem: com.littlechef.app.data.local.TranslationSystem,
    val ingredientMatcher: com.littlechef.app.domain.util.IngredientMatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroceriesUiState>(GroceriesUiState.Loading)
    val uiState: StateFlow<GroceriesUiState> = _uiState.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _showQuantityDialog = MutableStateFlow<GroceryItem?>(null)
    val showQuantityDialog: StateFlow<GroceryItem?> = _showQuantityDialog.asStateFlow()
    
    private val _existingIngredients = MutableStateFlow<List<String>>(emptyList())
    val existingIngredients: StateFlow<List<String>> = _existingIngredients.asStateFlow()
    
    val customGroceryHeader: StateFlow<String?> = preferences.customGroceryHeader.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    fun loadGroceries() {
        viewModelScope.launch {
            try {
                // Clean up old checked items (older than 2 days)
                groceryRepository.cleanupOldItems()
                
                groceryRepository.observeGroceryItems().collect { items ->
                    // Update checked status based on current pantry availability
                    val updatedItems = items.map { item ->
                        // Find the ingredient in the database
                        val ingredient = ingredientRepository.getIngredientByName(item.ingredientName)
                        
                        if (ingredient != null) {
                            // Don't auto-check newly added items - let user manually check them
                            // Only auto-check/uncheck items that were already in the list
                            item
                        } else {
                            // Ingredient doesn't exist in database, keep as unchecked
                            if (item.isChecked) {
                                val updatedItem = item.copy(isChecked = false, checkedAt = null)
                                groceryRepository.updateGroceryItem(updatedItem)
                                updatedItem
                            } else {
                                item
                            }
                        }
                    }
                    
                    // Group items by meal name and planned date to handle same meal on same day
                    val grouped = updatedItems.groupBy { 
                        "${it.mealName}|${it.plannedDate ?: "no-date"}" 
                    }
                    val mealGroups = grouped.map { (_, mealItems) ->
                        // Get all unique meal types for this meal on this day
                        val mealTypes = mealItems.mapNotNull { it.mealType }.distinct().sortedBy { it.ordinal }
                        val firstItem = mealItems.firstOrNull()
                        
                        // Aggregate quantities for duplicate ingredients
                        val aggregatedItems = mealItems.groupBy { it.ingredientName }.map { (_, items) ->
                            // Sum quantities for same ingredient
                            val totalQuantity = items.sumOf { it.quantity }
                            val firstIngredient = items.first()
                            
                            // Use the first item but with aggregated quantity
                            firstIngredient.copy(quantity = totalQuantity)
                        }
                        
                        MealGroup(
                            mealName = firstItem?.mealName ?: "",
                            mealTypes = mealTypes,
                            plannedDate = firstItem?.plannedDate,
                            items = aggregatedItems
                        )
                    }.sortedWith(compareBy(
                        // Priority 1: Items with plannedDate come first (sorted by date, earliest first)
                        { group -> if (group.plannedDate != null) 0 else 1 },
                        { group -> group.plannedDate ?: Long.MAX_VALUE },
                        // Priority 2: Recipe meals (with mealTypes but no plannedDate) come second
                        { group -> 
                            val categoryNames = listOf(
                                "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
                                "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
                                "Snacks", "Spices & Seasonings", "Other"
                            )
                            val isCategory = categoryNames.any { it.equals(group.mealName, ignoreCase = true) }
                            val allItemsChecked = group.items.all { it.isChecked }
                            when {
                                group.plannedDate != null -> 0 // Planned meals first
                                !isCategory -> 1 // Regular recipes second
                                isCategory && !allItemsChecked -> 2 // Category items with unchecked items
                                else -> 3 // Category items with all items checked (move to bottom)
                            }
                        },
                        // Priority 3: Sort by meal name within each group
                        { group -> group.mealName }
                    ))
                    // Pre-compute category map for all unique ingredient names
                    val allIngredientNames = updatedItems.map { it.ingredientName }.distinct()
                    val categoryMap = mutableMapOf<String, String>()
                    for (name in allIngredientNames) {
                        val translatedName = translateIngredient(name)
                        categoryMap[translatedName] = getCategoryForIngredient(translatedName)
                    }
                    _uiState.value = GroceriesUiState.Success(
                        mealGroups = mealGroups,
                        ingredientCategories = categoryMap
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GroceriesUiState.Success(emptyList())
            }
        }
    }

    fun toggleItemChecked(item: GroceryItem) {
        viewModelScope.launch {
            if (!item.isChecked) {
                // Show quantity dialog before checking
                _showQuantityDialog.value = item
            } else {
                // Unchecking the item
                try {
                    groceryRepository.updateGroceryItem(
                        item.copy(
                            isChecked = false,
                            checkedAt = null
                        )
                    )
                } catch (e: Exception) {
                    android.util.Log.e("GroceriesViewModel", "Exception while unchecking item", e)
                }
            }
        }
    }

    fun dismissQuantityDialog() {
        _showQuantityDialog.value = null
    }

    fun confirmPurchase(item: GroceryItem, purchasedQuantity: Double, purchasedUnit: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            
            // Check if ingredient exists in database, if not create it
            try {
                // Parse ingredient name and category (format: "name|category" for custom ingredients)
                val (actualIngredientName, customCategory) = if (item.ingredientName.contains("|")) {
                    val parts = item.ingredientName.split("|", limit = 2)
                    parts[0] to parts.getOrNull(1)
                } else {
                    item.ingredientName to null
                }
                
                val existingIngredient = ingredientRepository.getIngredientByName(actualIngredientName)
                if (existingIngredient == null) {
                    // Look up in catalog to get default values
                    val catalogIngredient = com.littlechef.app.domain.model.IngredientCatalog.allIngredients.find { 
                        it.nameKey.equals(actualIngredientName, ignoreCase = true) 
                    }
                    
                    // Determine storage unit based on catalog
                    val storageUnit = if (catalogIngredient != null) {
                        // For countable items (pcs, etc.), use the catalog's default unit directly
                        if (!UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit) &&
                            !UnitConversion.isWeightUnit(catalogIngredient.defaultUnit)) {
                            catalogIngredient.defaultUnit
                        } else {
                            // Use catalog's default unit type, but convert to storage unit
                            if (UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit)) {
                                "ml"
                            } else {
                                "g"
                            }
                        }
                    } else {
                        // Fallback for custom ingredients
                        when {
                            UnitConversion.isVolumeUnit(item.unit) -> "ml"
                            UnitConversion.isWeightUnit(item.unit) -> "g"
                            else -> item.unit
                        }
                    }
                    
                    // Determine category: use custom category if provided, otherwise catalog, otherwise "Other"
                    val categoryName = customCategory ?: catalogIngredient?.category?.displayName ?: "Other"
                    val subcategoryName = catalogIngredient?.subcategory ?: "Other"
                    
                    // Create new ingredient
                    val createResult = ingredientRepository.createIngredient(
                        name = actualIngredientName,
                        unit = storageUnit,
                        category = categoryName,
                        subcategory = subcategoryName
                    )
                    
                    if (createResult.isFailure) {
                        android.util.Log.e("GroceriesViewModel", "Failed to create ingredient", createResult.exceptionOrNull())
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroceriesViewModel", "Error checking/creating ingredient", e)
            }
            
            // Add to pantry with the specified quantity in storage unit
            try {
                android.util.Log.d("GroceriesViewModel", "=== User adding grocery item to pantry ===")
                android.util.Log.d("GroceriesViewModel", "Original item: ${item.ingredientName}")
                android.util.Log.d("GroceriesViewModel", "Original quantity: ${item.quantity} ${item.unit}")
                android.util.Log.d("GroceriesViewModel", "User entered quantity: $purchasedQuantity $purchasedUnit")
                android.util.Log.d("GroceriesViewModel", "Meal context: ${item.mealName}")
                
                // Parse ingredient name (remove category suffix if present)
                val actualIngredientName = if (item.ingredientName.contains("|")) {
                    val parsed = item.ingredientName.split("|", limit = 2)[0]
                    android.util.Log.d("GroceriesViewModel", "Parsed ingredient name: '$parsed' (removed category suffix)")
                    parsed
                } else {
                    android.util.Log.d("GroceriesViewModel", "Using ingredient name as-is: '${item.ingredientName}'")
                    item.ingredientName
                }
                
                // Create a modified item with the purchased quantity and unit as entered by user
                // No conversion - keep the unit exactly as the user specified
                val itemForPantry = item.copy(
                    ingredientName = actualIngredientName,
                    quantity = purchasedQuantity,
                    unit = purchasedUnit
                )
                
                android.util.Log.d("GroceriesViewModel", "Calling AddGroceryItemToPantryUseCase with:")
                android.util.Log.d("GroceriesViewModel", "  Ingredient: ${itemForPantry.ingredientName}")
                android.util.Log.d("GroceriesViewModel", "  Quantity: ${itemForPantry.quantity} ${itemForPantry.unit}")
                
                val result = addGroceryItemToPantryUseCase(itemForPantry)
                
                if (result is AddGroceryItemToPantryUseCase.Result.Success) {
                    android.util.Log.d("GroceriesViewModel", "✅ Successfully added to pantry")
                    
                    // Mark as checked
                    val updatedItem = item.copy(
                        isChecked = true,
                        checkedAt = now
                    )
                    groceryRepository.updateGroceryItem(updatedItem)
                    android.util.Log.d("GroceriesViewModel", "Marked grocery item as checked")
                    
                    // Auto-check other grocery items with the same ingredient if pantry has enough
                    try {
                        android.util.Log.d("GroceriesViewModel", "Checking for other items with same ingredient to auto-check...")
                        
                        val allGroceryItems = groceryRepository.getGroceryItems()
                        val pantryIngredient = ingredientRepository.getIngredientByName(actualIngredientName)
                        
                        if (pantryIngredient != null) {
                            // Get available quantity from inventory
                            val availableQuantity = inventoryRepository.getAvailableQuantity(pantryIngredient.id)
                            android.util.Log.d("GroceriesViewModel", "Pantry now has: $availableQuantity ${pantryIngredient.unit} of ${pantryIngredient.name}")
                            
                            // Find all unchecked items with the same ingredient
                            val sameIngredientItems = allGroceryItems.filter { groceryItem ->
                                !groceryItem.isChecked &&
                                groceryItem.id != item.id &&
                                groceryItem.ingredientName.split("|")[0].equals(actualIngredientName, ignoreCase = true)
                            }
                            
                            android.util.Log.d("GroceriesViewModel", "Found ${sameIngredientItems.size} other unchecked items with same ingredient")
                            
                            // Check each item to see if pantry now has enough
                            sameIngredientItems.forEach { groceryItem ->
                                // Convert grocery item quantity to storage unit for comparison
                                val requiredInStorage = UnitConversion.toStorageUnit(
                                    groceryItem.quantity,
                                    groceryItem.unit
                                )
                                
                                if (requiredInStorage != null) {
                                    val (requiredQty, _) = requiredInStorage
                                    
                                    android.util.Log.d("GroceriesViewModel", "Item '${groceryItem.mealName}' needs $requiredQty ${pantryIngredient.unit}")
                                    
                                    // If pantry has enough, auto-check this item
                                    if (availableQuantity >= requiredQty) {
                                        val autoCheckedItem = groceryItem.copy(
                                            isChecked = true,
                                            checkedAt = now
                                        )
                                        groceryRepository.updateGroceryItem(autoCheckedItem)
                                        android.util.Log.d("GroceriesViewModel", "✅ Auto-checked item: ${groceryItem.mealName}")
                                    } else {
                                        android.util.Log.d("GroceriesViewModel", "❌ Not enough in pantry for: ${groceryItem.mealName}")
                                    }
                                } else {
                                    android.util.Log.w("GroceriesViewModel", "Could not convert units for: ${groceryItem.mealName}")
                                }
                            }
                        } else {
                            android.util.Log.w("GroceriesViewModel", "Could not find pantry ingredient: $actualIngredientName")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("GroceriesViewModel", "Error auto-checking other items", e)
                    }
                } else if (result is AddGroceryItemToPantryUseCase.Result.Error) {
                    android.util.Log.e("GroceriesViewModel", "❌ Failed to add to pantry: ${result.message}")
                }
            } catch (e: Exception) {
                android.util.Log.e("GroceriesViewModel", "Exception while adding to pantry", e)
            }
            
            _showQuantityDialog.value = null
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            groceryRepository.deleteGroceryItem(itemId)
        }
    }
    
    fun deleteItemsByMealName(mealName: String) {
        viewModelScope.launch {
            val items = groceryRepository.getGroceryItems()
            val itemsToDelete = items.filter { it.mealName == mealName }
            itemsToDelete.forEach { item ->
                groceryRepository.deleteGroceryItem(item.id)
            }
        }
    }

    fun clearCheckedItems() {
        viewModelScope.launch {
            groceryRepository.clearCheckedItems()
        }
    }

    fun showAddDialog() {
        viewModelScope.launch {
            // Load existing ingredients when showing the dialog
            try {
                val ingredients = ingredientRepository.getAllIngredients()
                _existingIngredients.value = ingredients.map { it.name }
            } catch (e: Exception) {
                _existingIngredients.value = emptyList()
            }
            _showAddDialog.value = true
        }
    }

    fun dismissAddDialog() {
        _showAddDialog.value = false
    }

    fun addManualItem(
        ingredientName: String,
        quantity: Double,
        unit: String
    ) {
        viewModelScope.launch {
            // Parse ingredient name and category (format: "name|category" for custom ingredients)
            val (actualIngredientName, customCategory) = if (ingredientName.contains("|")) {
                val parts = ingredientName.split("|", limit = 2)
                parts[0] to parts.getOrNull(1)
            } else {
                ingredientName to null
            }
            
            // Determine the category/meal name
            val mealName = if (customCategory != null) {
                // Use custom category if provided
                customCategory
            } else {
                // Look up in catalog to get category
                val catalogIngredient = com.littlechef.app.domain.model.IngredientCatalog.allIngredients.find { 
                    it.nameKey.equals(actualIngredientName, ignoreCase = true) 
                }
                catalogIngredient?.category?.displayName ?: "Other"
            }
            
            val item = GroceryItem(
                id = java.util.UUID.randomUUID().toString(),
                ingredientName = ingredientName,
                quantity = quantity,
                unit = unit,
                mealName = mealName,
                isChecked = false,
                checkedAt = null,
                createdAt = System.currentTimeMillis()
            )
            groceryRepository.addGroceryItem(item)
            _showAddDialog.value = false
        }
    }
    
    fun addCustomIngredientToGroceryList(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        subcategory: String,
        allergens: List<String>
    ) {
        viewModelScope.launch {
            // First, create the ingredient in the database so it's available for future use
            try {
                val createResult = ingredientRepository.createIngredient(
                    name = name,
                    unit = unit,
                    category = category,
                    subcategory = subcategory
                )
                
                if (createResult.isSuccess) {
                    // Add allergens if any
                    if (allergens.isNotEmpty()) {
                        val ingredient = ingredientRepository.getIngredientByName(name)
                        ingredient?.let {
                            ingredientRepository.updateIngredient(it, allergens)
                        }
                    }
                } else {
                    android.util.Log.e("GroceriesViewModel", "Failed to create ingredient", createResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                android.util.Log.e("GroceriesViewModel", "Error creating ingredient", e)
            }
            
            // Add to grocery list
            val item = GroceryItem(
                id = java.util.UUID.randomUUID().toString(),
                ingredientName = name,
                quantity = quantity,
                unit = unit,
                mealName = category, // Use category as meal name for grouping
                isChecked = false,
                checkedAt = null,
                createdAt = System.currentTimeMillis()
            )
            groceryRepository.addGroceryItem(item)
        }
    }
    
    /**
     * Add multiple ingredients to the grocery list in batch from voice input.
     * 
     * This method processes each ingredient sequentially, tracking success and failure
     * for each one. Items are added to the grocery list (not directly to pantry).
     * 
     * @param ingredients List of enriched ingredients to add
     * @return BatchAddResult containing success count, total count, and failed ingredients
     */
    suspend fun addIngredientsBatch(
        ingredients: List<com.littlechef.app.domain.model.EnrichedIngredient>
    ): BatchAddResult {
        val results = mutableListOf<Pair<com.littlechef.app.domain.model.EnrichedIngredient, Boolean>>()
        
        for (ingredient in ingredients) {
            try {
                // Get subcategory from matched catalog ingredient, or use "Other" as fallback
                val subcategory = ingredient.matchedCatalogIngredient?.subcategory ?: "Other"
                val category = ingredient.category.displayName
                
                // Create ingredient in database if it doesn't exist
                val existingIngredient = ingredientRepository.getIngredientByName(ingredient.name)
                if (existingIngredient == null) {
                    // Look up in catalog to get correct unit
                    val catalogIngredient = IngredientCatalog.allIngredients.find {
                        it.nameKey.equals(ingredient.name, ignoreCase = true)
                    }
                    
                    // Determine storage unit from catalog or fallback to conversion logic
                    val storageUnit = if (catalogIngredient != null) {
                        // For countable items (pcs, etc.), use the catalog's default unit directly
                        if (!UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit) &&
                            !UnitConversion.isWeightUnit(catalogIngredient.defaultUnit)) {
                            catalogIngredient.defaultUnit
                        } else {
                            // Use catalog's default unit type, but convert to storage unit
                            if (UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit)) {
                                "ml"
                            } else {
                                "g"
                            }
                        }
                    } else {
                        // Fallback for custom ingredients
                        when {
                            UnitConversion.isVolumeUnit(ingredient.unit) -> "ml"
                            UnitConversion.isWeightUnit(ingredient.unit) -> "g"
                            else -> ingredient.unit // pcs
                        }
                    }
                    
                    val createResult = ingredientRepository.createIngredient(
                        name = ingredient.name,
                        unit = storageUnit,
                        category = category,
                        subcategory = subcategory
                    )
                    
                    if (createResult.isSuccess && ingredient.allergens.isNotEmpty()) {
                        val createdIngredient = ingredientRepository.getIngredientByName(ingredient.name)
                        createdIngredient?.let {
                            ingredientRepository.updateIngredient(it, ingredient.allergens.map { allergen -> allergen.displayName })
                        }
                    }
                }
                
                // Add to grocery list
                val item = GroceryItem(
                    id = java.util.UUID.randomUUID().toString(),
                    ingredientName = ingredient.name,
                    quantity = ingredient.quantity,
                    unit = ingredient.unit,
                    mealName = category, // Use category as meal name for grouping
                    isChecked = false,
                    checkedAt = null,
                    createdAt = System.currentTimeMillis()
                )
                groceryRepository.addGroceryItem(item)
                results.add(ingredient to true)
            } catch (e: Exception) {
                android.util.Log.e("GroceriesViewModel", "Failed to add ingredient: ${ingredient.name}", e)
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
    
    // Translation helpers
    fun translateIngredient(ingredientName: String): String {
        return translationSystem.translateIngredient(ingredientName)
    }
    
    fun translateCategory(categoryName: String): String {
        return translationSystem.translateCategory(categoryName)
    }
    
    fun getTranslationSystem(): com.littlechef.app.data.local.TranslationSystem {
        return translationSystem
    }
    
    /**
     * Get category for an ingredient by name (translated name).
     * Uses database lookup first, then falls back to catalog fuzzy matching.
     */
    suspend fun getCategoryForIngredient(translatedIngredientName: String): String {
        // Try to look up ingredient in database to get its category
        val dbIngredient = ingredientRepository.getIngredientByName(translatedIngredientName)
        
        return if (dbIngredient != null && dbIngredient.category != null) {
            // Use category from database
            dbIngredient.category
        } else {
            // Fall back to catalog lookup with fuzzy matching
            val matchResult = ingredientMatcher.findMatch(translatedIngredientName, threshold = 0.6)
            matchResult?.catalogIngredient?.category?.displayName ?: "Other"
        }
    }
}

data class MealGroup(
    val mealName: String,
    val mealTypes: List<com.littlechef.app.domain.model.MealType>,
    val plannedDate: Long?,
    val items: List<GroceryItem>
)

sealed class GroceriesUiState {
    object Loading : GroceriesUiState()
    data class Success(
        val mealGroups: List<MealGroup>,
        val ingredientCategories: Map<String, String> = emptyMap()
    ) : GroceriesUiState()
}
