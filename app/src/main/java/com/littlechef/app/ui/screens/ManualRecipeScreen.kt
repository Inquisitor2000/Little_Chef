package com.littlechef.app.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.R
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.domain.model.UnitOptions
import com.littlechef.app.ui.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualRecipeScreen(
    onNavigateBack: () -> Unit,
    onRecipeSaved: () -> Unit,
    onNavigateToCustomIngredient: (String) -> Unit,
    viewModel: ManualRecipeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()
    
    // Pantry ViewModel for ingredient repository and preferences
    val pantryViewModel: PantryViewModel = hiltViewModel()
    
    val validUnits = remember { UnitConversion.getAllUnits() }
    
    // State for showing add ingredient drawer
    var showAddIngredientDrawer by remember { mutableStateOf(false) }
    var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }
    var showEditIngredientDialog by remember { mutableStateOf(false) }
    
    // Image picker for dish photo
    val dishImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, it))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            }
            viewModel.setDishImage(bitmap)
        }
    }
    
    // Handle saved state
    LaunchedEffect(uiState) {
        if (uiState is ManualRecipeUiState.Saved) {
            onRecipeSaved()
        }
    }
    
    // Show error dialog
    if (uiState is ManualRecipeUiState.Error) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error", style = MaterialTheme.typography.titleMedium) },
            text = { 
                Text(
                    text = (uiState as ManualRecipeUiState.Error).message,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                ) 
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (showAddIngredientDrawer) {
                            stringResource(R.string.add_ingredient_title)
                        } else {
                            stringResource(R.string.add_recipe_title)
                        },
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (showAddIngredientDrawer) {
                                showAddIngredientDrawer = false
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Recipe Name
                OutlinedTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text(stringResource(R.string.add_recipe_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Dish Image Section
                ManualDishImageSection(
                    dishImage = state.dishImage,
                    onPickImage = { dishImagePicker.launch("image/*") },
                    onClearImage = { viewModel.clearDishImage() }
                )
                
                // Times and Servings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = state.prepTimeMinutes,
                        onValueChange = { viewModel.updatePrepTime(it.filter { char -> char.isDigit() }) },
                        label = { Text(stringResource(R.string.add_recipe_prep_min), style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                    )
                    
                    OutlinedTextField(
                        value = state.cookTimeMinutes,
                        onValueChange = { viewModel.updateCookTime(it.filter { char -> char.isDigit() }) },
                        label = { Text(stringResource(R.string.add_recipe_cook_min), style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                    )
                    
                    // Servings selector - cycles through 1 through 6
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val currentServings = state.servings.toIntOrNull() ?: 2
                                val nextServings = when (currentServings) {
                                    1 -> 2
                                    2 -> 3
                                    3 -> 4
                                    4 -> 5
                                    5 -> 6
                                    else -> 1
                                }
                                viewModel.updateServings(nextServings.toString())
                                haptic.performLight()
                            }
                    ) {
                        OutlinedTextField(
                            value = state.servings.ifBlank { "2" },
                            onValueChange = {},
                            label = { Text(stringResource(R.string.add_recipe_servings), style = MaterialTheme.typography.labelSmall) },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
                        )
                    }
                }
                
                // Category Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.add_recipe_meal_type),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.add_recipe_dish_type),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(top = 2.dp, bottom = 4.dp)
                    ) {
                        // Left column - Meal Type
                        MealTypePicker(
                            selectedMealType = state.mealType,
                            onMealTypeSelected = { viewModel.updateMealType(it) },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .padding(vertical = 12.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        
                        // Right column - Dish Category
                        DishCategoryPicker(
                            selectedDishCategory = state.dishCategory,
                            onDishCategorySelected = { viewModel.updateDishCategory(it) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                
                // Ingredients Section
                Text(
                    text = stringResource(R.string.add_recipe_ingredients_count, state.ingredients.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                if (state.ingredients.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.add_recipe_no_ingredients_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    state.ingredients.forEachIndexed { index, ingredient ->
                        ManualIngredientItem(
                            ingredient = ingredient,
                            translateIngredient = { pantryViewModel.translateIngredient(it) },
                            onEdit = {
                                editingIngredientIndex = index
                                showEditIngredientDialog = true
                            },
                            onRemove = {
                                viewModel.removeIngredient(index)
                            }
                        )
                    }
                }
                
                // Add Ingredient Button
                Button(
                    onClick = { showAddIngredientDrawer = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_recipe_add_ingredient))
                }
                
                // Instructions
                Text(
                    text = stringResource(R.string.add_recipe_instructions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                OutlinedTextField(
                    value = state.instructions,
                    onValueChange = { viewModel.updateInstructions(it) },
                    label = { Text(stringResource(R.string.add_recipe_instructions_label)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .heightIn(min = 200.dp),
                    minLines = 8,
                    placeholder = { Text(stringResource(R.string.add_recipe_instructions_placeholder)) }
                )
                
                // Save and Cancel buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    
                    val canSave = uiState !is ManualRecipeUiState.Saving &&
                                 state.name.isNotBlank() &&
                                 state.ingredients.isNotEmpty()
                    
                    Button(
                        onClick = { viewModel.saveRecipe() },
                        enabled = canSave,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (uiState is ManualRecipeUiState.Saving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.button_save))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // AddIngredientDrawer
            if (showAddIngredientDrawer) {
                Box(modifier = Modifier.padding(padding)) {
                    AddIngredientDrawer(
                        visible = showAddIngredientDrawer,
                        onDismiss = { 
                            showAddIngredientDrawer = false
                        },
                        onAddIngredient = { name, quantity, unit, _, _, _ ->
                            viewModel.addIngredient()
                            viewModel.updateIngredient(
                                state.ingredients.size,
                                ManualRecipeIngredient(
                                    name = name,
                                    quantity = quantity,
                                    unit = unit
                                )
                            )
                            showAddIngredientDrawer = false
                        },
                        preferences = pantryViewModel.preferences,
                        ingredientRepository = pantryViewModel.ingredientRepository,
                        onNavigateToCustomIngredient = { initialName ->
                            showAddIngredientDrawer = false
                            onNavigateToCustomIngredient(initialName)
                        },
                        translationSystem = pantryViewModel.getTranslationSystem()
                    )
                }
            }
            
            // Edit Ingredient Dialog
            editingIngredientIndex?.let { index ->
                if (showEditIngredientDialog) {
                    val ingredient = state.ingredients[index]
                    EditManualIngredientDialog(
                        ingredient = ingredient,
                        validUnits = validUnits,
                        translateIngredient = { pantryViewModel.translateIngredient(it) },
                        onDismiss = {
                            showEditIngredientDialog = false
                            editingIngredientIndex = null
                        },
                        onSave = { updatedIngredient ->
                            viewModel.updateIngredient(index, updatedIngredient)
                            showEditIngredientDialog = false
                            editingIngredientIndex = null
                        },
                        onDelete = {
                            viewModel.removeIngredient(index)
                            showEditIngredientDialog = false
                            editingIngredientIndex = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ManualIngredientItem(
    ingredient: ManualRecipeIngredient,
    translateIngredient: (String) -> String,
    onEdit: () -> Unit,
    onRemove: () -> Unit
) {
    // Check if ingredient exists in catalog
    val isInCatalog = remember(ingredient.name) {
        com.littlechef.app.domain.model.IngredientCatalog.allIngredients.any {
            it.nameKey.equals(ingredient.name, ignoreCase = true)
        }
    }
    
    // Only translate if ingredient is in catalog, otherwise use the name as-is
    val displayName = if (isInCatalog) {
        translateIngredient(ingredient.name)
    } else {
        ingredient.name
    }
    
    // Translate unit using string resources
    val translatedUnit = when (ingredient.unit.lowercase()) {
        "g" -> stringResource(R.string.unit_g)
        "ml" -> stringResource(R.string.unit_ml)
        "kg" -> stringResource(R.string.unit_kg)
        "l" -> stringResource(R.string.unit_l)
        "cup" -> stringResource(R.string.unit_cup)
        "tbsp" -> stringResource(R.string.unit_tbsp)
        "tsp" -> stringResource(R.string.unit_tsp)
        "piece", "pcs" -> stringResource(R.string.unit_piece)
        "oz" -> stringResource(R.string.unit_oz)
        "lb" -> stringResource(R.string.unit_lb)
        else -> ingredient.unit
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "${ingredient.quantity} $translatedUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = androidx.compose.foundation.shape.CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditManualIngredientDialog(
    ingredient: ManualRecipeIngredient,
    validUnits: List<String>,
    translateIngredient: (String) -> String,
    onDismiss: () -> Unit,
    onSave: (ManualRecipeIngredient) -> Unit,
    onDelete: () -> Unit
) {
    var quantity by remember { mutableStateOf(ingredient.quantity.toString()) }
    var selectedUnit by remember { mutableStateOf(ingredient.unit) }
    val haptic = rememberHapticFeedback()
    
    // Look up ingredient in catalog to get icon
    val catalogIngredient = remember(ingredient.name) {
        com.littlechef.app.domain.model.IngredientCatalog.allIngredients.find {
            it.nameKey.equals(ingredient.name, ignoreCase = true)
        }
    }
    
    // Determine allowed units based on current unit
    val allowedUnits = remember(selectedUnit) {
        UnitOptions.getAllowedUnitsForIngredient(selectedUnit)
    }
    val showUnitSelector = allowedUnits.size > 1
    
    // Unit translation helper
    val translateUnit: @Composable (String) -> String = { unit ->
        when (unit.lowercase()) {
            "g" -> stringResource(R.string.unit_g)
            "ml" -> stringResource(R.string.unit_ml)
            "kg" -> stringResource(R.string.unit_kg)
            "l" -> stringResource(R.string.unit_l)
            "pcs" -> stringResource(R.string.unit_piece)
            else -> unit
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            // Header with icon, name, and delete button (aligned with unit switch position)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side: icon and name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Ingredient icon
                    catalogIngredient?.let { catIng ->
                        Image(
                            painter = painterResource(
                                id = com.littlechef.app.domain.model.CategoryIcons.getIconForIngredient(
                                    category = catIng.category.displayName,
                                    subcategory = catIng.subcategory
                                )
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Text(
                        text = translateIngredient(ingredient.name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Normal
                    )
                }
                
                // Right side: spacing to align with unit switch + delete button
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Spacer to match: minus (48dp) + spacing (12dp) + input field + spacing (12dp) + plus (48dp) + spacing (12dp)
                    // This will position delete button where unit switch will be
                    Spacer(modifier = Modifier.width(0.dp)) // Will be aligned by the row structure
                    
                    // Delete button - same size as +/- buttons
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                onDelete()
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quantity input with tick counters and unit switches
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Minus button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                val current = quantity.toDoubleOrNull() ?: 0.0
                                if (current > 0) {
                                    quantity = (current - 1).coerceAtLeast(0.0).toString()
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
                    
                    // Quantity input
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { newValue ->
                            quantity = newValue.filter { it.isDigit() || it == '.' }
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                    )
                    
                    // Plus button
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                val current = quantity.toDoubleOrNull() ?: 0.0
                                quantity = (current + 1).toString()
                                haptic.performLight()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    // Unit selector (if multiple units available)
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
                                Surface(
                                    shape = CircleShape,
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
                                            text = translateUnit(selectedUnit),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    } else if (!showUnitSelector) {
                        Text(
                            text = translateUnit(selectedUnit),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                
                // Unit type switches (for 3 unit types)
                if (showUnitSelector && allowedUnits.size == 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allowedUnits.forEach { unit ->
                            val isSelected = selectedUnit == unit
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable {
                                        selectedUnit = unit
                                        haptic.performLight()
                                    }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text(
                                        text = translateUnit(unit),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSecondaryContainer
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Custom button row
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                            val finalQuantity = quantity.toDoubleOrNull() ?: 0.0
                            if (finalQuantity > 0) {
                                onSave(
                                    ManualRecipeIngredient(
                                        name = ingredient.name,
                                        quantity = finalQuantity,
                                        unit = selectedUnit
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = quantity.toDoubleOrNull() != null
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
