package com.familymealplanner.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.familymealplanner.R
import com.familymealplanner.domain.model.CommonAllergen
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.model.IngredientCategory
import com.familymealplanner.ui.util.rememberHapticFeedback
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddCustomIngredientScreen(
    initialName: String = "",
    ingredientId: String = "",
    onNavigateBack: () -> Unit,
    onConfirm: (name: String, quantity: Double, unit: String, category: String, subcategory: String, allergens: List<String>) -> Unit,
    onUpdate: ((id: String, name: String, unit: String, category: String, subcategory: String, allergens: List<String>) -> Unit)? = null,
    preferences: com.familymealplanner.data.preferences.OnboardingPreferences,
    ingredientRepository: com.familymealplanner.domain.repository.IngredientRepository? = null,
    translationSystem: com.familymealplanner.data.local.TranslationSystem? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isEditMode = ingredientId.isNotEmpty()
    
    // Load existing ingredient if in edit mode
    var existingIngredient by remember { mutableStateOf<com.familymealplanner.domain.model.Ingredient?>(null) }
    
    LaunchedEffect(ingredientId) {
        if (isEditMode && ingredientRepository != null) {
            existingIngredient = ingredientRepository.getIngredientById(ingredientId)
        }
    }
    
    var name by remember { mutableStateOf(initialName) }
    var quantity by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<IngredientCategory?>(null) }
    var selectedSubcategory by remember { mutableStateOf("") }
    var selectedAllergens by remember { mutableStateOf(setOf<CommonAllergen>()) }
    var pendingUnit by remember { mutableStateOf<String?>(null) }
    var showPcsWarning by remember { mutableStateOf(false) }
    
    // Populate fields when existing ingredient is loaded
    LaunchedEffect(existingIngredient) {
        existingIngredient?.let { ingredient ->
            name = ingredient.name
            
            // Show storage unit as-is (g, ml, or pcs)
            // User can switch to kg or L if they prefer
            selectedUnit = ingredient.unit
            
            // Debug: Log the category from database
            android.util.Log.d("AddCustomIngredient", "Loading ingredient: ${ingredient.name}")
            android.util.Log.d("AddCustomIngredient", "Category from DB: '${ingredient.category}'")
            android.util.Log.d("AddCustomIngredient", "Subcategory from DB: '${ingredient.subcategory}'")
            android.util.Log.d("AddCustomIngredient", "Unit from DB: '${ingredient.unit}'")
            
            // Match category by displayName first, then try key
            selectedCategory = IngredientCategory.entries.find { 
                it.displayName.equals(ingredient.category, ignoreCase = true) 
            } ?: IngredientCategory.entries.find {
                it.key.equals(ingredient.category, ignoreCase = true)
            }
            
            android.util.Log.d("AddCustomIngredient", "Matched category: ${selectedCategory?.displayName}")
            android.util.Log.d("AddCustomIngredient", "Available categories: ${IngredientCategory.entries.map { it.displayName }}")
            
            selectedSubcategory = ingredient.subcategory ?: ""
            selectedAllergens = ingredient.allergens.mapNotNull { allergen ->
                CommonAllergen.entries.find { it.displayName == allergen.name }
            }.toSet()
        }
    }
    
    val coroutineScope = rememberCoroutineScope()
    val pcsWarningDismissed by preferences.pcsWarningDismissed.collectAsState(initial = false)
    val haptic = rememberHapticFeedback()

    val categories = remember { IngredientCategory.entries }
    val allergenList = remember { CommonAllergen.entries }
    
    val subcategories = remember(selectedCategory) {
        selectedCategory?.let { IngredientCatalog.getSubcategories(it) } ?: emptyList()
    }
    
    // Handle unit selection with warning check
    val handleUnitSelection: (String) -> Unit = { unit ->
        if (unit == "pcs" && !pcsWarningDismissed) {
            pendingUnit = unit
            showPcsWarning = true
        } else {
            selectedUnit = unit
        }
    }
    
    // Reset subcategory when category changes
    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            val newSubcategories = IngredientCatalog.getSubcategories(selectedCategory!!)
            selectedSubcategory = if (newSubcategories.isNotEmpty()) newSubcategories[0] else ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) stringResource(R.string.edit_ingredient_title) else stringResource(R.string.add_ingredient_title),
                        style = MaterialTheme.typography.headlineSmall
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            
            // Ingredient Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.add_ingredient_ingredient_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Quantity Section - show only unit switchers in edit mode
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (isEditMode) stringResource(R.string.add_recipe_unit) else stringResource(R.string.add_ingredient_quantity),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                if (isEditMode) {
                    // Edit mode: Show 3 rounded rectangle buttons for g, ml, pcs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // g button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (selectedUnit == "g") 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    selectedUnit = "g"
                                    haptic.performLight()
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(R.string.unit_g),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (selectedUnit == "g") FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selectedUnit == "g")
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        
                        // ml button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (selectedUnit == "ml") 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    selectedUnit = "ml"
                                    haptic.performLight()
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(R.string.unit_ml),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (selectedUnit == "ml") FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selectedUnit == "ml")
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        
                        // pcs button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (selectedUnit == "pcs") 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    handleUnitSelection("pcs")
                                    haptic.performLight()
                                }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = stringResource(R.string.unit_piece),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (selectedUnit == "pcs") FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (selectedUnit == "pcs")
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                } else {
                    // Add mode: Show quantity controls and unit switchers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quantity stepper
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val current = quantity.toIntOrNull() ?: 0
                                    if (current > 0) {
                                        quantity = (current - 1).coerceAtLeast(0).toString()
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
                        
                        OutlinedTextField(
                            value = quantity,
                            onValueChange = { newValue ->
                                quantity = newValue.filter { it.isDigit() }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .widthIn(min = 120.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                        
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val current = quantity.toIntOrNull() ?: 0
                                    quantity = (current + 1).toString()
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
                    
                    // g/kg vertical switcher
                    val isWeightSelected = selectedUnit == "g" || selectedUnit == "kg"
                    val isGSelected = selectedUnit == "g"
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .width(44.dp)
                            .height(80.dp)
                            .clickable {
                                if (isWeightSelected) {
                                    selectedUnit = if (selectedUnit == "g") "kg" else "g"
                                } else {
                                    selectedUnit = "kg"
                                }
                                haptic.performLight()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                        ) {
                            // Show circular background only when selected (behind text)
                            if (isWeightSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .align(if (isGSelected) Alignment.TopCenter else Alignment.BottomCenter)
                                ) {}
                            }
                            
                            // Always show both labels (on top)
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.unit_g),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isWeightSelected && isGSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isWeightSelected && isGSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                                Box(
                                    modifier = Modifier.size(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.unit_kg),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isWeightSelected && !isGSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isWeightSelected && !isGSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // ml/L vertical switcher
                    val isVolumeSelected = selectedUnit == "ml" || selectedUnit == "L"
                    val isMlSelected = selectedUnit == "ml"
                    
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .width(44.dp)
                            .height(80.dp)
                            .clickable {
                                if (isVolumeSelected) {
                                    selectedUnit = if (selectedUnit == "ml") "L" else "ml"
                                } else {
                                    selectedUnit = "L"
                                }
                                haptic.performLight()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                        ) {
                            // Show circular background only when selected (behind text)
                            if (isVolumeSelected) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .align(if (isMlSelected) Alignment.TopCenter else Alignment.BottomCenter)
                                ) {}
                            }
                            
                            // Always show both labels (on top)
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.unit_ml),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isVolumeSelected && isMlSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isVolumeSelected && isMlSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                                Box(
                                    modifier = Modifier.size(38.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.unit_l),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isVolumeSelected && !isMlSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isVolumeSelected && !isMlSelected)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // pcs circular button
                    Surface(
                        shape = CircleShape,
                        color = if (selectedUnit == "pcs") 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable {
                                handleUnitSelection("pcs")
                                haptic.performLight()
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = stringResource(R.string.unit_piece),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selectedUnit == "pcs") FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedUnit == "pcs")
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                    }
                }
            }


            // Category & Subcategory Selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_ingredient_category_subcategory),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        // Left column - Category
                        CupertinoPicker(
                            items = categories,
                            selectedItem = selectedCategory,
                            onItemSelected = { category ->
                                selectedCategory = category
                                haptic.performLight()
                            },
                            itemLabel = { it?.getLocalizedName(context) ?: "" },
                            showIcon = true,
                            getIcon = { it?.let { cat -> 
                                com.familymealplanner.domain.model.CategoryIcons.getIconForCategory(cat.displayName)
                            } ?: 0 },
                            modifier = Modifier.weight(1f)
                        )
                        
                        // Show subcategory picker only if category is selected and has subcategories
                        if (selectedCategory != null && subcategories.isNotEmpty()) {
                            // Divider
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .fillMaxHeight()
                                    .padding(vertical = 12.dp)
                                    .background(MaterialTheme.colorScheme.outlineVariant)
                            )
                            
                            // Right column - Subcategory
                            // Use key to force recomposition when category changes
                            key(selectedCategory) {
                                CupertinoPicker(
                                    items = subcategories,
                                    selectedItem = selectedSubcategory.takeIf { it.isNotEmpty() },
                                    onItemSelected = { subcategory ->
                                        selectedSubcategory = subcategory ?: ""
                                        haptic.performLight()
                                    },
                                    itemLabel = { it?.let { sub -> 
                                        translationSystem?.translateCategory(sub) ?: com.familymealplanner.domain.model.getLocalizedSubcategoryName(context, sub)
                                    } ?: "" },
                                    showIcon = true,
                                    getIcon = { it?.let { sub ->
                                        com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                                            category = selectedCategory!!.displayName,
                                            subcategory = sub
                                        )
                                    } ?: 0 },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
            
            // Allergens Selection
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_ingredient_allergens),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                
                // Display allergens in rows of 3
                val rows = allergenList.chunked(3)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    rows.forEach { rowAllergens ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowAllergens.forEach { allergen ->
                                val isSelected = allergen in selectedAllergens
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedAllergens = if (isSelected) {
                                            selectedAllergens - allergen
                                        } else {
                                            selectedAllergens + allergen
                                        }
                                        haptic.performLight()
                                    },
                                    label = { 
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                allergen.getLocalizedName(context),
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            // Add empty spaces if row has less than 3 items
                            repeat(3 - rowAllergens.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val qty = quantity.toDoubleOrNull()
                
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(stringResource(R.string.add_ingredient_cancel_button))
                }
                
                Button(
                    onClick = { 
                        if (isEditMode) {
                            // Convert display unit to storage unit for database
                            val storageUnit = when (selectedUnit) {
                                "kg", "g" -> "g"
                                "L", "ml" -> "ml"
                                else -> selectedUnit // pcs stays as pcs
                            }
                            onUpdate?.invoke(
                                ingredientId,
                                name,
                                storageUnit,
                                selectedCategory!!.displayName,
                                selectedSubcategory.ifEmpty { "Other" },
                                selectedAllergens.map { it.displayName }
                            )
                        } else {
                            onConfirm(
                                name, 
                                qty!!, 
                                selectedUnit, 
                                selectedCategory!!.displayName,
                                selectedSubcategory.ifEmpty { "Other" },
                                selectedAllergens.map { it.displayName }
                            )
                        }
                    },
                    enabled = name.isNotBlank() && 
                             (isEditMode || (qty != null && qty > 0)) &&
                             selectedUnit.isNotBlank() && selectedCategory != null,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isEditMode) stringResource(R.string.button_save) else stringResource(R.string.add_ingredient_add_button))
                }
            }
        }
    }
    
    // Pcs warning dialog
    if (showPcsWarning) {
        PcsWarningDialog(
            onDismiss = {
                showPcsWarning = false
                pendingUnit?.let { selectedUnit = it }
                pendingUnit = null
            },
            onDontShowAgain = {
                coroutineScope.launch {
                    preferences.setPcsWarningDismissed()
                }
                showPcsWarning = false
                pendingUnit?.let { selectedUnit = it }
                pendingUnit = null
            }
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun <T> CupertinoPicker(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    itemLabel: (T?) -> String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    getIcon: ((T?) -> Int)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val itemHeightDp = 40.dp
    val itemHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { itemHeightDp.toPx() }
    val visibleItems = 3
    
    // Calculate proper padding to center the middle item
    // Using 36dp for better visual centering
    val verticalPaddingDp = 36.dp
    
    // Create infinite list by repeating items
    val repeatCount = 1000 // Large enough to feel infinite
    val totalItems = items.size * repeatCount
    val middleStart = (repeatCount / 2) * items.size
    
    // Find selected index in the middle section
    val selectedIndex = items.indexOf(selectedItem).takeIf { it >= 0 } ?: 0
    val initialScrollIndex = middleStart + selectedIndex
    
    // Scroll to selected item when it changes
    LaunchedEffect(selectedItem) {
        val targetIndex = middleStart + (items.indexOf(selectedItem).takeIf { it >= 0 } ?: 0)
        listState.scrollToItem(targetIndex)
    }
    
    // Auto-select based on scroll position - item in center of highlight
    // Use debouncing and distinctUntilChanged to reduce callback frequency
    LaunchedEffect(listState) {
        snapshotFlow { 
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset 
        }
        .debounce(100) // Increased debounce to reduce update frequency
        .collect { (firstIndex, offset) ->
            // Calculate which item is in the center
            val scrollPosition = offset.toFloat()
            
            // The centered item is the one whose center aligns with the highlight center
            val centeredIndex = if (scrollPosition < itemHeightPx / 2) {
                firstIndex
            } else {
                firstIndex + 1
            }
            
            // Map back to original items using modulo
            val actualIndex = centeredIndex % items.size
            val actualItem = items[actualIndex]
            
            // Only update if different to avoid infinite loops
            if (actualItem != selectedItem) {
                onItemSelected(actualItem)
            }
        }
    }
    
    // Snap to center when scrolling stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // Calculate the closest item to center
            val firstIndex = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            
            // Determine which item should be centered
            val targetIndex = if (offset < itemHeightPx / 2) {
                firstIndex
            } else {
                firstIndex + 1
            }
            
            // Animate to center the target item
            coroutineScope.launch {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }
    
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        // Selection highlight in the center
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(itemHeightDp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        ) {}
        
        // Fade gradients at top and bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .align(Alignment.TopCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f)
                        )
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp)
                .align(Alignment.BottomCenter)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
        
        // Scrollable list with infinite items
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = verticalPaddingDp),
            horizontalAlignment = Alignment.CenterHorizontally,
            userScrollEnabled = true
        ) {
            items(totalItems) { index ->
                val actualIndex = index % items.size
                val item = items[actualIndex]
                val isSelected = item == selectedItem
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp)
                        .clickable { 
                            onItemSelected(item)
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp)
                    ) {
                        // Show icon if enabled
                        if (showIcon && getIcon != null) {
                            val iconRes = getIcon(item)
                            if (iconRes != 0) {
                                Image(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                        }
                        
                        Text(
                            text = itemLabel(item),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) 
                                MaterialTheme.colorScheme.onPrimaryContainer 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PcsWarningDialog(
    onDismiss: () -> Unit,
    onDontShowAgain: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                stringResource(R.string.pcs_warning_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                stringResource(R.string.pcs_warning_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.pcs_warning_understand))
                }
                Button(
                    onClick = onDontShowAgain,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(stringResource(R.string.pcs_warning_dont_show))
                }
            }
        },
        dismissButton = {}
    )
}
