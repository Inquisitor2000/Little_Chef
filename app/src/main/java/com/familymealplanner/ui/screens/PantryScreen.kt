package com.familymealplanner.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.R
import com.familymealplanner.domain.model.CategoryIcons
import com.familymealplanner.domain.model.CommonAllergen
import com.familymealplanner.domain.model.EnrichedIngredient
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.model.IngredientCategory
import com.familymealplanner.domain.model.PantryItem
import com.familymealplanner.domain.model.UnitConversion
import com.familymealplanner.domain.model.UnitOptions
import com.familymealplanner.ui.theme.md_theme_light_background
import com.familymealplanner.ui.util.rememberHapticFeedback

// Helper function to translate units in formatted display strings
@Composable
private fun translateFormattedUnit(formattedString: String): String {
    // Extract the unit from the formatted string (e.g., "100 g" -> "g")
    val parts = formattedString.trim().split(" ")
    if (parts.size < 2) return formattedString
    
    val quantity = parts[0]
    val unit = parts[1]
    
    val translatedUnit = when (unit.lowercase()) {
        "g" -> stringResource(R.string.unit_g)
        "kg" -> stringResource(R.string.unit_kg)
        "ml" -> stringResource(R.string.unit_ml)
        "l" -> stringResource(R.string.unit_l)
        "cup" -> stringResource(R.string.unit_cup)
        "tbsp" -> stringResource(R.string.unit_tbsp)
        "tsp" -> stringResource(R.string.unit_tsp)
        "pcs", "piece" -> stringResource(R.string.unit_piece)
        "oz" -> stringResource(R.string.unit_oz)
        "lb" -> stringResource(R.string.unit_lb)
        else -> unit
    }
    
    return "$quantity $translatedUnit"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = hiltViewModel(),
    onNavigateToAddCustomIngredient: ((String) -> Unit)? = null,
    onNavigateToSettings: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLanguage = viewModel.getCurrentLanguage()
    var selectedItem by remember { mutableStateOf<PantryItem?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddIngredientDrawer by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf<String?>(null) }
    var expandedSubcategory by remember { mutableStateOf<String?>(null) }
    val haptic = rememberHapticFeedback()

    // Reset dialog state when screen is recomposed (e.g., after navigation)
    DisposableEffect(Unit) {
        onDispose {
            // This runs when leaving the screen
        }
    }
    
    // Reset state when returning to this screen
    LaunchedEffect(Unit) {
        selectedItem = null
        showEditDialog = false
    }

    LaunchedEffect(Unit) {
        viewModel.loadPantryItems()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.pantry_title), style = MaterialTheme.typography.headlineSmall) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    actions = {
                        Button(
                            onClick = {
                                haptic.performLight()
                                showAddIngredientDrawer = true
                            },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(stringResource(R.string.pantry_add_ingredient_button))
                        }
                        Button(
                            onClick = {
                                haptic.performLight()
                                onNavigateToSettings?.invoke()
                            },
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = stringResource(R.string.nav_settings),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                )
            }
        ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is PantryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PantryUiState.Success -> {
                    val itemsWithInventory = state.pantryItems.filter { 
                        it.availableQuantity > 0 || it.reservedQuantity > 0 
                    }
                    
                    if (itemsWithInventory.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_empty_pantry),
                                    contentDescription = "Empty pantry",
                                    modifier = Modifier.size(120.dp),
                                    alpha = 0.6f
                                )
                                Text(
                                    text = stringResource(R.string.pantry_empty_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = stringResource(R.string.pantry_empty_message_full),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(0.dp)
                        ) {
                            // Group by category
                            val grouped = itemsWithInventory.groupBy { it.ingredient.category ?: "Other" }
                            
                            grouped.forEach { (category, categoryItems) ->
                                // Group by subcategory within each category
                                val subcategoryGroups = categoryItems.groupBy { item ->
                                    // Use subcategory from database (should always be populated)
                                    item.ingredient.subcategory ?: "Other"
                                }
                                
                                val isCategoryExpanded = expandedCategory == category
                                
                                item(key = "category_$category") {
                                    PantryCategoryHeader(
                                        category = category,
                                        isExpanded = isCategoryExpanded,
                                        itemCount = categoryItems.size,
                                        onClick = {
                                            if (isCategoryExpanded) {
                                                expandedCategory = null
                                                expandedSubcategory = null
                                            } else {
                                                expandedCategory = category
                                                expandedSubcategory = null
                                            }
                                        },
                                        translateCategory = viewModel::translateCategory
                                    )
                                    
                                    AnimatedVisibility(
                                        visible = isCategoryExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column {
                                            // Check if there are subcategories
                                            val hasSubcategories = subcategoryGroups.keys.any { it != null }
                                            
                                            if (hasSubcategories) {
                                                // Show subcategories
                                                subcategoryGroups.forEach { (subcategory, subcategoryItems) ->
                                                    if (subcategory != null) {
                                                        val isSubcategoryExpanded = expandedSubcategory == subcategory
                                                        
                                                        PantrySubcategoryHeader(
                                                            subcategory = subcategory,
                                                            isExpanded = isSubcategoryExpanded,
                                                            itemCount = subcategoryItems.size,
                                                            onClick = {
                                                                expandedSubcategory = if (isSubcategoryExpanded) null else subcategory
                                                            },
                                                            translateCategory = viewModel::translateCategory
                                                        )
                                                        
                                                        AnimatedVisibility(
                                                            visible = isSubcategoryExpanded,
                                                            enter = expandVertically() + fadeIn(),
                                                            exit = shrinkVertically() + fadeOut()
                                                        ) {
                                                            Column {
                                                                subcategoryItems.forEach { pantryItem ->
                                                                    val translatedName = viewModel.translateIngredient(pantryItem.ingredient.name)
                                                                    PantryItemCard(
                                                                        pantryItem = pantryItem,
                                                                        onClick = {
                                                                            selectedItem = pantryItem
                                                                            showEditDialog = true
                                                                        },
                                                                        modifier = Modifier.padding(start = 48.dp),
                                                                        currentLanguage = currentLanguage,
                                                                        translatedName = translatedName
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        // Items without subcategory
                                                        subcategoryItems.forEach { pantryItem ->
                                                            val translatedName = viewModel.translateIngredient(pantryItem.ingredient.name)
                                                            PantryItemCard(
                                                                pantryItem = pantryItem,
                                                                onClick = {
                                                                    selectedItem = pantryItem
                                                                    showEditDialog = true
                                                                },
                                                                modifier = Modifier.padding(start = 24.dp),
                                                                currentLanguage = currentLanguage,
                                                                translatedName = translatedName
                                                            )
                                                        }
                                                    }
                                                }
                                            } else {
                                                // No subcategories, show items directly
                                                categoryItems.forEach { pantryItem ->
                                                    val translatedName = viewModel.translateIngredient(pantryItem.ingredient.name)
                                                    PantryItemCard(
                                                        pantryItem = pantryItem,
                                                        onClick = {
                                                            selectedItem = pantryItem
                                                            showEditDialog = true
                                                        },
                                                        modifier = Modifier.padding(start = 24.dp),
                                                        currentLanguage = currentLanguage,
                                                        translatedName = translatedName
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is PantryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    } // End Scaffold

    // Edit/Delete dialog
    if (showEditDialog && selectedItem != null) {
        EditPantryItemDialog(
            pantryItem = selectedItem!!,
            onDismiss = {
                showEditDialog = false
                selectedItem = null
            },
            onUpdateQuantity = { newQuantity ->
                val item = selectedItem!!
                val diff = newQuantity - item.availableQuantity
                viewModel.adjustInventory(
                    ingredientId = item.ingredient.id,
                    quantityChange = diff,
                    reason = "Manual adjustment",
                    onSuccess = {
                        showEditDialog = false
                        selectedItem = null
                    },
                    onError = { /* Handle error */ }
                )
            },
            onDelete = {
                val item = selectedItem!!
                // Set quantity to 0 by subtracting current quantity
                viewModel.adjustInventory(
                    ingredientId = item.ingredient.id,
                    quantityChange = -item.availableQuantity,
                    reason = "Deleted from pantry",
                    onSuccess = {
                        showEditDialog = false
                        selectedItem = null
                    },
                    onError = { /* Handle error */ }
                )
            },
            onUpdateAllergens = { allergenNames ->
                val item = selectedItem!!
                viewModel.updateIngredientAllergens(
                    ingredientId = item.ingredient.id,
                    allergenNames = allergenNames,
                    onSuccess = { /* Allergens updated */ },
                    onError = { /* Handle error */ }
                )
            },
            onUpdateName = { newName ->
                val item = selectedItem!!
                viewModel.updateIngredientName(
                    ingredientId = item.ingredient.id,
                    newName = newName,
                    onSuccess = { /* Name updated */ },
                    onError = { /* Handle error */ }
                )
            },
            onNavigateToAddCustomIngredient = onNavigateToAddCustomIngredient,
            translateIngredient = { ingredientName -> viewModel.translateIngredient(ingredientName) }
        )
    }
    
    // Add Ingredient Drawer
    AddIngredientDrawer(
        visible = showAddIngredientDrawer,
        onDismiss = { showAddIngredientDrawer = false },
        onAddIngredient = { name, quantity, unit, category, subcategory, allergens ->
            viewModel.addIngredient(name, quantity, unit, category, subcategory, allergens)
        },
        preferences = viewModel.preferences,
        ingredientRepository = viewModel.ingredientRepository,
        onEditExistingIngredient = { ingredientId ->
            // Find the pantry item by ingredient ID and show edit dialog
            if (uiState is PantryUiState.Success) {
                val pantryItem = (uiState as PantryUiState.Success).pantryItems.find { 
                    it.ingredient.id == ingredientId 
                }
                if (pantryItem != null) {
                    selectedItem = pantryItem
                    showEditDialog = true
                }
            }
        },
        onNavigateToCustomIngredient = { initialName ->
            onNavigateToAddCustomIngredient?.invoke(initialName)
            showAddIngredientDrawer = false
        },
        translationSystem = viewModel.getTranslationSystem(),
        onVoiceInputClick = null
    )
    } // Close Box
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryItemCard(
    pantryItem: PantryItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentLanguage: String = "en",
    translatedName: String = pantryItem.ingredient.name
) {
    // Find matching catalog ingredient by name to get the subcategory and allergens
    val catalogIngredient = IngredientCatalog.allIngredients.find { 
        it.nameKey.equals(pantryItem.ingredient.name, ignoreCase = true) 
    }
    
    // Get allergens - from catalog for catalog ingredients, from database for custom
    val allergenNames = if (catalogIngredient != null) {
        catalogIngredient.allergens.map { it.displayName }
    } else {
        pantryItem.ingredient.allergens.map { it.name }
    }
    
    // Format quantities for display using UnitConversion
    val availableDisplay = translateFormattedUnit(
        UnitConversion.formatForDisplay(
            pantryItem.availableQuantity,
            pantryItem.ingredient.unit
        )
    )
    val reservedQty = pantryItem.reservedQuantity

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .height(IntrinsicSize.Min),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // No icon for individual ingredients - only categories/subcategories have icons
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = translatedName.replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            // Show language indicator if ingredient was created in a different language
                            val createdLanguage = pantryItem.ingredient.createdInLanguage
                            if (createdLanguage != null && createdLanguage != currentLanguage) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer
                                ) {
                                    Text(
                                        text = createdLanguage.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                    Text(
                        text = availableDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                if (reservedQty > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    val reservedDisplay = translateFormattedUnit(
                        UnitConversion.formatForDisplay(
                            reservedQty,
                            pantryItem.ingredient.unit
                        )
                    )
                    Text(
                        text = "Reserved: $reservedDisplay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    pantryItem.reservations.forEach { reservation ->
                        val resDisplay = translateFormattedUnit(
                            UnitConversion.formatForDisplay(
                                reservation.quantity,
                                pantryItem.ingredient.unit
                            )
                        )
                        Text(
                            text = "  • $resDisplay for ${reservation.mealName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun EditPantryItemDialog(
    pantryItem: PantryItem,
    onDismiss: () -> Unit,
    onUpdateQuantity: (Double) -> Unit,
    onDelete: () -> Unit,
    onUpdateAllergens: ((List<String>) -> Unit)? = null,
    onUpdateName: ((String) -> Unit)? = null,
    onNavigateToAddCustomIngredient: ((String) -> Unit)? = null,
    translateIngredient: (String) -> String = { it }
) {
    // Get allowed units for this ingredient
    val catalogIngredient = remember(pantryItem.ingredient.name) {
        IngredientCatalog.allIngredients.find { 
            it.nameKey.equals(pantryItem.ingredient.name, ignoreCase = true) 
        }
    }
    val isCustomIngredient = catalogIngredient == null
    
    val allowedUnits = remember(pantryItem.ingredient.unit, isCustomIngredient) {
        if (isCustomIngredient) {
            // For custom ingredients, get units of the same measurement type
            UnitOptions.getAllowedUnitsForIngredient(pantryItem.ingredient.unit)
        } else {
            UnitOptions.getAllowedUnitsForIngredient(pantryItem.ingredient.unit)
        }
    }
    
    // Use the user's preferred display unit if available, otherwise default to base unit (g, ml)
    val preferredUnit = remember(allowedUnits, pantryItem.ingredient.preferredDisplayUnit) {
        val preferred = pantryItem.ingredient.preferredDisplayUnit
        when {
            preferred != null && allowedUnits.contains(preferred) -> preferred
            allowedUnits.contains("g") -> "g"
            allowedUnits.contains("ml") -> "ml"
            allowedUnits.contains("kg") -> "kg"
            allowedUnits.contains("L") -> "L"
            else -> allowedUnits.firstOrNull() ?: pantryItem.ingredient.unit
        }
    }
    
    var selectedUnit by remember { mutableStateOf(preferredUnit) }
    
    // Convert quantity to display in selected unit (initial load only)
    val displayQuantity = remember(pantryItem.availableQuantity, pantryItem.ingredient.unit) {
        when {
            UnitConversion.isWeightUnit(pantryItem.ingredient.unit) && UnitConversion.isWeightUnit(preferredUnit) -> {
                val grams = UnitConversion.toGrams(pantryItem.availableQuantity, pantryItem.ingredient.unit) ?: pantryItem.availableQuantity
                UnitConversion.fromGrams(grams, preferredUnit) ?: pantryItem.availableQuantity
            }
            UnitConversion.isVolumeUnit(pantryItem.ingredient.unit) && UnitConversion.isVolumeUnit(preferredUnit) -> {
                val ml = UnitConversion.toMilliliters(pantryItem.availableQuantity, pantryItem.ingredient.unit) ?: pantryItem.availableQuantity
                UnitConversion.fromMilliliters(ml, preferredUnit) ?: pantryItem.availableQuantity
            }
            else -> pantryItem.availableQuantity
        }
    }
    
    var quantityText by remember(displayQuantity) { 
        mutableStateOf(
            if (displayQuantity == displayQuantity.toInt().toDouble()) {
                displayQuantity.toInt().toString()
            } else {
                // Use up to 3 decimal places to preserve precision (e.g., 2.995 kg)
                String.format("%.3f", displayQuantity).trimEnd('0').trimEnd('.')
            }
        ) 
    }
    
    // Track the unit that corresponds to the current quantityText value
    var quantityTextUnit by remember { mutableStateOf(preferredUnit) }
    
    // Update quantity text when unit changes - convert current input to new unit
    LaunchedEffect(selectedUnit) {
        // Only convert if the unit actually changed from what quantityText represents
        if (selectedUnit != quantityTextUnit) {
            val currentQty = quantityText.toDoubleOrNull() ?: 0.0
            if (currentQty > 0) {
                // Convert from the unit that quantityText is in to storage unit, then to new unit
                val inStorageUnit = when {
                    UnitConversion.isWeightUnit(quantityTextUnit) -> {
                        UnitConversion.toGrams(currentQty, quantityTextUnit) ?: currentQty
                    }
                    UnitConversion.isVolumeUnit(quantityTextUnit) -> {
                        UnitConversion.toMilliliters(currentQty, quantityTextUnit) ?: currentQty
                    }
                    else -> currentQty
                }
                
                // Then convert from storage unit to the new selected unit
                val converted = when {
                    UnitConversion.isWeightUnit(selectedUnit) -> {
                        UnitConversion.fromGrams(inStorageUnit, selectedUnit) ?: currentQty
                    }
                    UnitConversion.isVolumeUnit(selectedUnit) -> {
                        UnitConversion.fromMilliliters(inStorageUnit, selectedUnit) ?: currentQty
                    }
                    else -> currentQty
                }
                
                quantityText = if (converted == converted.toInt().toDouble()) {
                    converted.toInt().toString()
                } else {
                    // Use up to 3 decimal places to preserve precision
                    String.format("%.3f", converted).trimEnd('0').trimEnd('.')
                }
            }
            // Update quantityTextUnit to match the new selectedUnit
            quantityTextUnit = selectedUnit
        }
    }
    
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    val showUnitSelector = allowedUnits.size > 1
    val haptic = rememberHapticFeedback()

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(
                        R.string.pantry_delete_title,
                        translateIngredient(pantryItem.ingredient.name).replaceFirstChar { 
                            if (it.isLowerCase()) it.titlecase() else it.toString() 
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            text = { 
                Text(
                    text = stringResource(R.string.pantry_delete_message),
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
                            showDeleteConfirm = false
                            onDelete()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.pantry_delete_button))
                    }
                    Button(
                        onClick = { showDeleteConfirm = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.pantry_cancel_button))
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCustomIngredient) {
                        // Clickable name for custom ingredients - navigates to edit screen
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { 
                                    onNavigateToAddCustomIngredient?.invoke(pantryItem.ingredient.id)
                                    onDismiss()
                                }
                        ) {
                            Image(
                                painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                                    category = pantryItem.ingredient.category ?: "Other",
                                    subcategory = pantryItem.ingredient.subcategory
                                )),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = translateIngredient(pantryItem.ingredient.name)
                            )
                        }
                    } else {
                        // Static name for catalog ingredients
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Image(
                                painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                                    category = pantryItem.ingredient.category ?: "Other",
                                    subcategory = pantryItem.ingredient.subcategory
                                )),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = translateIngredient(pantryItem.ingredient.name).replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    // Delete/Discard icon button - aligned with unit switcher position
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color(0xFFE57373), // Softer red background
                            contentColor = Color(0xFF8B0000) // Dark red icon
                        ),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Discard"
                        )
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {
                    // Circular +/- buttons with centered input and iOS-style toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Minus button
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .clickable {
                                    val current = quantityText.toIntOrNull() ?: 0
                                    if (current > 0) {
                                        quantityText = (current - 1).coerceAtLeast(0).toString()
                                        haptic.performLight()
                                    }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = "−",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        
                        // Quantity input field
                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { newValue ->
                                // Allow digits and one decimal point
                                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    quantityText = newValue
                                }
                            },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                        
                        // Plus button
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .clickable {
                                    val current = quantityText.toIntOrNull() ?: 0
                                    quantityText = (current + 1).toString()
                                    haptic.performLight()
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        
                        // iOS-style vertical toggle switch (only if multiple units available)
                        if (showUnitSelector && allowedUnits.size == 2) {
                            val isFirstUnit = selectedUnit == allowedUnits[0]
                            
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier
                                    .width(44.dp)
                                    .height(80.dp)
                                    .clickable {
                                        val currentIndex = allowedUnits.indexOf(selectedUnit)
                                        val nextIndex = (currentIndex + 1) % allowedUnits.size
                                        selectedUnit = allowedUnits[nextIndex]
                                        haptic.performLight()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                ) {
                                    // Accent color sliding thumb with unit label
                                    Surface(
                                        shape = androidx.compose.foundation.shape.CircleShape,
                                        color = MaterialTheme.colorScheme.primary,
                                        shadowElevation = 2.dp,
                                        modifier = Modifier
                                            .size(38.dp)
                                            .align(if (isFirstUnit) Alignment.TopCenter else Alignment.BottomCenter)
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = when (selectedUnit.lowercase()) {
                                                    "g" -> stringResource(R.string.unit_g)
                                                    "kg" -> stringResource(R.string.unit_kg)
                                                    "ml" -> stringResource(R.string.unit_ml)
                                                    "l" -> stringResource(R.string.unit_l)
                                                    "cup" -> stringResource(R.string.unit_cup)
                                                    "tbsp" -> stringResource(R.string.unit_tbsp)
                                                    "tsp" -> stringResource(R.string.unit_tsp)
                                                    "pcs", "piece" -> stringResource(R.string.unit_piece)
                                                    "oz" -> stringResource(R.string.unit_oz)
                                                    "lb" -> stringResource(R.string.unit_lb)
                                                    else -> selectedUnit
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (!showUnitSelector) {
                            // Static unit label
                            Text(
                                text = when (selectedUnit.lowercase()) {
                                    "g" -> stringResource(R.string.unit_g)
                                    "kg" -> stringResource(R.string.unit_kg)
                                    "ml" -> stringResource(R.string.unit_ml)
                                    "l" -> stringResource(R.string.unit_l)
                                    "cup" -> stringResource(R.string.unit_cup)
                                    "tbsp" -> stringResource(R.string.unit_tbsp)
                                    "tsp" -> stringResource(R.string.unit_tsp)
                                    "pcs", "piece" -> stringResource(R.string.unit_piece)
                                    "oz" -> stringResource(R.string.unit_oz)
                                    "lb" -> stringResource(R.string.unit_lb)
                                    else -> selectedUnit
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    
                    // Allergen section for custom ingredients only
                    if (isCustomIngredient) {
                    }
                    
                    // Cancel and Save buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val newQty = quantityText.toDoubleOrNull()
                        
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        ) {
                            Text(stringResource(R.string.button_cancel))
                        }
                        
                        Button(
                            onClick = { 
                                newQty?.let { qty ->
                                    // Convert quantity from the unit that quantityText represents to storage unit (g or ml)
                                    // The database always stores weight in grams and volume in milliliters
                                    // Use quantityTextUnit (not selectedUnit) because that's what the current text value represents
                                    val quantityInStorageUnit = when {
                                        UnitConversion.isWeightUnit(quantityTextUnit) -> {
                                            // Convert to grams (storage unit for weight)
                                            UnitConversion.toGrams(qty, quantityTextUnit) ?: qty
                                        }
                                        UnitConversion.isVolumeUnit(quantityTextUnit) -> {
                                            // Convert to milliliters (storage unit for volume)
                                            UnitConversion.toMilliliters(qty, quantityTextUnit) ?: qty
                                        }
                                        else -> qty
                                    }
                                    
                                    // Note: We don't update the ingredient's unit because the database
                                    // always stores in base units (g or ml). The display automatically
                                    // converts to the appropriate unit (kg or L) when needed.
                                    
                                    onUpdateQuantity(quantityInStorageUnit) 
                                } 
                            },
                            enabled = newQty != null && newQty >= 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.button_save))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}


@Composable
private fun PantryCategoryHeader(
    category: String,
    isExpanded: Boolean,
    itemCount: Int,
    onClick: () -> Unit,
    translateCategory: (String) -> String = { it }
) {
    // Format category name: convert "MEAT_POULTRY" to "Meat & Poultry" if needed
    val displayCategory = when {
        category.contains("_") -> {
            // Try to match with enum display name
            IngredientCategory.entries.find { it.name == category }?.displayName ?: category
        }
        else -> category
    }
    
    // Translate the category name
    val translatedCategory = translateCategory(displayCategory)
    
    val iconResId = CategoryIcons.getIconForCategory(displayCategory)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = displayCategory,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = translatedCategory,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(if (isExpanded) 90f else 0f)
            )
        }
    }
}

@Composable
private fun PantrySubcategoryHeader(
    subcategory: String,
    isExpanded: Boolean,
    itemCount: Int,
    onClick: () -> Unit,
    translateCategory: (String) -> String = { it }
) {
    // Translate the subcategory name
    val translatedSubcategory = translateCategory(subcategory)
    
    val iconResId = CategoryIcons.getIconForSubcategory(subcategory)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 2.dp, bottom = 2.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = subcategory,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = translatedSubcategory,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$itemCount items",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(if (isExpanded) 90f else 0f)
            )
        }
    }
}
