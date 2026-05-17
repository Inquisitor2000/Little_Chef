package com.littlechef.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.R
import com.littlechef.app.domain.model.GroceryItem
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.ui.export.GroceriesTextExport
import com.littlechef.app.ui.theme.error_color
import com.littlechef.app.ui.theme.md_theme_light_background
import com.littlechef.app.ui.util.rememberHapticFeedback

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
fun GroceriesScreen(
    viewModel: GroceriesViewModel = hiltViewModel(),
    onNavigateToAddCustomIngredient: ((String) -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val ingredientCategories = (uiState as? GroceriesUiState.Success)?.ingredientCategories ?: emptyMap()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val showQuantityDialog by viewModel.showQuantityDialog.collectAsState()
    val customGroceryHeader by viewModel.customGroceryHeader.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    val expandedMeals = remember { mutableStateMapOf<String, Boolean>() }
    val context = LocalContext.current
    val haptic = rememberHapticFeedback()

    LaunchedEffect(Unit) {
        viewModel.loadGroceries()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(R.string.groceries_title), style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    when (val state = uiState) {
                        is GroceriesUiState.Success -> {
                            val hasCheckedItems = state.mealGroups.flatMap { it.items }.any { it.isChecked }
                            if (hasCheckedItems) {
                                // Clear checked circular button - matches height of Add Item button
                                Surface(
                                    onClick = { showClearDialog = true },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .height(40.dp)
                                        .aspectRatio(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = androidx.compose.ui.res.stringResource(R.string.groceries_clear_checked),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            val hasUncheckedItems = state.mealGroups.flatMap { it.items }.any { !it.isChecked }
                            if (hasUncheckedItems) {
                                // Export/share button (plain text)
                                Surface(
                                    onClick = {
                                        // Generate plain text grocery list
                                        val plainText = GroceriesTextExport.generatePlainText(
                                            mealGroups = state.mealGroups,
                                            translateIngredient = viewModel::translateIngredient,
                                            translateCategory = viewModel::translateCategory,
                                            translateUnit = { unit ->
                                                when (unit.lowercase()) {
                                                    "g" -> context.getString(R.string.unit_g)
                                                    "kg" -> context.getString(R.string.unit_kg)
                                                    "ml" -> context.getString(R.string.unit_ml)
                                                    "l" -> context.getString(R.string.unit_l)
                                                    "cup" -> context.getString(R.string.unit_cup)
                                                    "tbsp" -> context.getString(R.string.unit_tbsp)
                                                    "tsp" -> context.getString(R.string.unit_tsp)
                                                    "pcs", "piece" -> context.getString(R.string.unit_piece)
                                                    "oz" -> context.getString(R.string.unit_oz)
                                                    "lb" -> context.getString(R.string.unit_lb)
                                                    else -> unit
                                                }
                                            },
                                            getCategoryForIngredient = { translatedName ->
                                                ingredientCategories[translatedName] ?: "Other"
                                            },
                                            customHeader = customGroceryHeader,
                                            defaultHeader = context.getString(R.string.groceries_export_header),
                                            servingsText = { servings -> 
                                                context.getString(R.string.groceries_export_servings, servings)
                                            },
                                            taskFromText = context.getString(R.string.groceries_export_task_from),
                                            allCheckedText = context.getString(R.string.groceries_export_all_checked),
                                            consolidateIngredients = true
                                        )

                                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(android.content.Intent.EXTRA_TEXT, plainText)
                                        }
                                        context.startActivity(
                                            android.content.Intent.createChooser(
                                                shareIntent,
                                                context.getString(R.string.groceries_share_title)
                                            )
                                        )
                                    },
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .height(40.dp)
                                        .aspectRatio(1f)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = context.getString(R.string.groceries_share_button),
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                    Button(
                        onClick = {
                            haptic.performLight()
                            viewModel.showAddDialog()
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
                        Text(androidx.compose.ui.res.stringResource(R.string.groceries_add_item))
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is GroceriesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is GroceriesUiState.Success -> {
                if (state.mealGroups.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_empty_groceries),
                                contentDescription = "Empty groceries",
                                modifier = Modifier.size(100.dp),
                                alpha = 0.6f
                            )
                            Text(
                                text = stringResource(R.string.groceries_no_items),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.groceries_empty_hint),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Separate meal groups into planned/recipe groups and category groups
                        val categoryNames = listOf(
                            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
                            "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
                            "Snacks", "Spices & Seasonings", "Other"
                        )
                        val (categoryGroups, mealRecipeGroups) = state.mealGroups.partition { group ->
                            categoryNames.any { it.equals(group.mealName, ignoreCase = true) }
                        }
                        
                        // Display meal/recipe groups first
                        mealRecipeGroups.forEach { mealGroup ->
                            val groupKey = "${mealGroup.mealName}_${mealGroup.plannedDate ?: "no-date"}"
                            item(key = "header_$groupKey") {
                                val isExpanded = expandedMeals.getOrPut(groupKey) { false }
                                
                                SwipeToDeleteMealGroup(
                                    mealName = mealGroup.mealName,
                                    mealTypes = mealGroup.mealTypes,
                                    plannedDate = mealGroup.plannedDate,
                                    itemCount = mealGroup.items.size,
                                    checkedCount = mealGroup.items.count { it.isChecked },
                                    isExpanded = isExpanded,
                                    onToggleExpand = { 
                                        expandedMeals[groupKey] = !(expandedMeals[groupKey] ?: false)
                                    },
                                    onDelete = {
                                        viewModel.deleteItemsByMealName(mealGroup.mealName)
                                        expandedMeals.remove(groupKey)
                                    },
                                    translateCategory = viewModel::translateCategory
                                )
                                
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        // Check if this is a category group or a meal group
                                        val knownCategoryNames = listOf(
                                            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
                                            "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
                                            "Snacks", "Spices & Seasonings", "Other"
                                        )
                                        val isCategoryGroup = knownCategoryNames.any { it.equals(mealGroup.mealName, ignoreCase = true) }
                                        
                                        // Group items by category
                                        val itemsByCategory = mealGroup.items.groupBy { item ->
                                            val translatedName = viewModel.translateIngredient(item.ingredientName)
                                            ingredientCategories[translatedName] ?: "Other"
                                        }
                                        
                                        // Define category display order: Meat, Seafood, Dairy, Vegetables, then rest
                                        val categoryOrder = listOf(
                                            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables",
                                            "Fruits", "Grains & Bread", "Canned Goods",
                                            "Beverages", "Snacks", "Spices & Seasonings", "Other"
                                        )
                                        
                                        // Sort categories: first by checked status, then by predefined order
                                        val sortedCategories = itemsByCategory.entries.sortedWith(
                                            compareBy<Map.Entry<String, List<GroceryItem>>>(
                                                // Priority 1: Unchecked categories first, fully checked last
                                                { (_, categoryItems) -> if (categoryItems.all { it.isChecked }) 1 else 0 },
                                                // Priority 2: Predefined category order
                                                { (categoryName, _) -> 
                                                    val index = categoryOrder.indexOfFirst { it.equals(categoryName, ignoreCase = true) }
                                                    if (index >= 0) index else categoryOrder.size
                                                }
                                            )
                                        )
                                        
                                        // Display each category with its items
                                        sortedCategories.forEach { (categoryName, categoryItems) ->
                                            // Only show category header for meal groups, not for category groups
                                            if (!isCategoryGroup) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = com.littlechef.app.domain.model.CategoryIcons.getIconForCategory(categoryName)
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Text(
                                                        text = viewModel.translateCategory(categoryName),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            
                                            categoryItems.forEach { item ->
                                                val translatedName = viewModel.translateIngredient(item.ingredientName)
                                                SwipeToDeleteGroceryItem(
                                                    item = item,
                                                    onCheckedChange = { viewModel.toggleItemChecked(item) },
                                                    onDelete = { viewModel.deleteItem(item.id) },
                                                    translatedName = translatedName
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Add divider between meal/recipe groups and category groups
                        if (mealRecipeGroups.isNotEmpty() && categoryGroups.isNotEmpty()) {
                            item(key = "divider") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        
                        // Display category groups
                        categoryGroups.forEach { mealGroup ->
                            val groupKey = "${mealGroup.mealName}_${mealGroup.plannedDate ?: "no-date"}"
                            item(key = "header_$groupKey") {
                                val isExpanded = expandedMeals.getOrPut(groupKey) { false }
                                
                                SwipeToDeleteMealGroup(
                                    mealName = mealGroup.mealName,
                                    mealTypes = mealGroup.mealTypes,
                                    plannedDate = mealGroup.plannedDate,
                                    itemCount = mealGroup.items.size,
                                    checkedCount = mealGroup.items.count { it.isChecked },
                                    isExpanded = isExpanded,
                                    onToggleExpand = { 
                                        expandedMeals[groupKey] = !(expandedMeals[groupKey] ?: false)
                                    },
                                    onDelete = {
                                        viewModel.deleteItemsByMealName(mealGroup.mealName)
                                        expandedMeals.remove(groupKey)
                                    },
                                    translateCategory = viewModel::translateCategory
                                )
                                
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column {
                                        // Check if this is a category group or a meal group
                                        val knownCategoryNames = listOf(
                                            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
                                            "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
                                            "Snacks", "Spices & Seasonings", "Other"
                                        )
                                        val isCategoryGroup = knownCategoryNames.any { it.equals(mealGroup.mealName, ignoreCase = true) }
                                        
                                        // Group items by category
                                        val itemsByCategory = mealGroup.items.groupBy { item ->
                                            val translatedName = viewModel.translateIngredient(item.ingredientName)
                                            ingredientCategories[translatedName] ?: "Other"
                                        }
                                        
                                        // Define category display order: Meat, Seafood, Dairy, Vegetables, then rest
                                        val categoryOrder = listOf(
                                            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables",
                                            "Fruits", "Grains & Bread", "Canned Goods",
                                            "Beverages", "Snacks", "Spices & Seasonings", "Other"
                                        )
                                        
                                        // Sort categories: first by checked status, then by predefined order
                                        val sortedCategories = itemsByCategory.entries.sortedWith(
                                            compareBy<Map.Entry<String, List<GroceryItem>>>(
                                                // Priority 1: Unchecked categories first, fully checked last
                                                { (_, categoryItems) -> if (categoryItems.all { it.isChecked }) 1 else 0 },
                                                // Priority 2: Predefined category order
                                                { (categoryName, _) -> 
                                                    val index = categoryOrder.indexOfFirst { it.equals(categoryName, ignoreCase = true) }
                                                    if (index >= 0) index else categoryOrder.size
                                                }
                                            )
                                        )
                                        
                                        // Display each category with its items
                                        sortedCategories.forEach { (categoryName, categoryItems) ->
                                            // Only show category header for meal groups, not for category groups
                                            if (!isCategoryGroup) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Image(
                                                        painter = painterResource(
                                                            id = com.littlechef.app.domain.model.CategoryIcons.getIconForCategory(categoryName)
                                                        ),
                                                        contentDescription = null,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                    Text(
                                                        text = viewModel.translateCategory(categoryName),
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                            
                                            categoryItems.forEach { item ->
                                                val translatedName = viewModel.translateIngredient(item.ingredientName)
                                                SwipeToDeleteGroceryItem(
                                                    item = item,
                                                    onCheckedChange = { viewModel.toggleItemChecked(item) },
                                                    onDelete = { viewModel.deleteItem(item.id) },
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
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.groceries_clear_checked_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.groceries_clear_checked_message),
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
                            viewModel.clearCheckedItems()
                            showClearDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(androidx.compose.ui.res.stringResource(R.string.groceries_clear_button))
                    }
                    Button(
                        onClick = { showClearDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(androidx.compose.ui.res.stringResource(R.string.groceries_cancel_button))
                    }
                }
            },
            dismissButton = { }
        )
    }

    AddIngredientDrawer(
        visible = showAddDialog,
        onDismiss = { viewModel.dismissAddDialog() },
        onAddIngredient = { name, quantity, unit, category, subcategory, allergens ->
            viewModel.addManualItem(name, quantity, unit)
        },
        preferences = viewModel.preferences,
        ingredientRepository = viewModel.ingredientRepository,
        onEditExistingIngredient = null,
        onNavigateToCustomIngredient = { initialName ->
            onNavigateToAddCustomIngredient?.invoke(initialName)
            viewModel.dismissAddDialog()
        },
        translationSystem = viewModel.getTranslationSystem()
    )
    showQuantityDialog?.let { item ->
        
        val translatedName = viewModel.translateIngredient(item.ingredientName)
        PurchaseQuantityDialog(
            item = item,
            onDismiss = { viewModel.dismissQuantityDialog() },
            onConfirm = { quantity, unit ->
                viewModel.confirmPurchase(item, quantity, unit)
            },
            translatedName = translatedName
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteMealGroup(
    mealName: String,
    mealTypes: List<com.littlechef.app.domain.model.MealType>,
    plannedDate: Long?,
    itemCount: Int,
    checkedCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onDelete: () -> Unit,
    translateCategory: (String) -> String = { it }
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val dismissState = rememberDismissState(
        confirmValueChange = {
            if (it == DismissValue.DismissedToStart) {
                showDeleteDialog = true
                false // Don't dismiss yet, wait for confirmation
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.25f }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            val color = MaterialTheme.colorScheme.errorContainer
            val scale by animateFloatAsState(
                targetValue = if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "scale"
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    color = color,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = error_color,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .scale(scale)
                                )
                            }
                        }
                    }
                }
            }
        },
        dismissContent = {
            // Check if this is a category group
            val categoryNames = listOf(
                "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
                "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
                "Snacks", "Spices & Seasonings", "Other"
            )
            val isCategory = categoryNames.any { it.equals(mealName, ignoreCase = true) }
            
            MealGroupHeader(
                mealName = mealName,
                mealTypes = mealTypes,
                plannedDate = plannedDate,
                itemCount = itemCount,
                checkedCount = checkedCount,
                isExpanded = isExpanded,
                onToggleExpand = onToggleExpand,
                showIcon = isCategory,
                translateCategory = translateCategory
            )
        },
        directions = setOf(DismissDirection.EndToStart)
    )
    
    if (showDeleteDialog) {
        // Determine if this is a recipe or ingredient category group
        val categoryNames = listOf(
            "Meat & Poultry", "Seafood", "Dairy & Eggs", "Vegetables", 
            "Fruits", "Grains & Bread", "Canned Goods", "Beverages", 
            "Snacks", "Spices & Seasonings", "Other"
        )
        val isCategory = categoryNames.any { it.equals(mealName, ignoreCase = true) }
        
        // Parse recipe name and servings for recipes
        val (recipeName, servings) = if (!isCategory) {
            val servingsPattern = """\((\d+)\s+servings?\)""".toRegex()
            val match = servingsPattern.find(mealName)
            if (match != null) {
                val name = mealName.replace(servingsPattern, "").trim()
                val servingCount = match.groupValues[1]
                name to servingCount
            } else {
                mealName to null
            }
        } else {
            mealName to null
        }
        
        // Build meal types text
        val mealTypesText = if (mealTypes.isNotEmpty()) {
            mealTypes.joinToString(", ") { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } }
        } else {
            null
        }
        
        val message = if (isCategory) {
            context.getString(R.string.groceries_remove_category_message, itemCount, translateCategory(mealName))
        } else {
            val recipeInfo = buildString {
                // Prioritize servings over meal types
                if (servings != null) {
                    append("(")
                    append(context.getString(R.string.groceries_servings, servings))
                    append(")")
                } else if (mealTypesText != null) {
                    append("($mealTypesText)")
                }
            }
            context.getString(R.string.groceries_remove_recipe_message, itemCount, recipeName, recipeInfo)
        }
        
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.groceries_remove_items_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = message,
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
                            onDelete()
                            showDeleteDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(androidx.compose.ui.res.stringResource(R.string.groceries_remove_button))
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
                        Text(androidx.compose.ui.res.stringResource(R.string.groceries_cancel_button))
                    }
                }
            },
            dismissButton = { }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealGroupHeader(
    mealName: String,
    mealTypes: List<com.littlechef.app.domain.model.MealType>,
    plannedDate: Long?,
    itemCount: Int,
    checkedCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    showIcon: Boolean = false,
    translateCategory: (String) -> String = { it }
) {
    // Parse meal name to extract recipe name and servings
    val (recipeName, servings) = remember(mealName) {
        val servingsPattern = """\((\d+)\s+servings?\)""".toRegex()
        val match = servingsPattern.find(mealName)
        if (match != null) {
            val name = mealName.replace(servingsPattern, "").trim()
            val servingCount = match.groupValues[1]
            name to servingCount
        } else {
            mealName to null
        }
    }
    
    // Get icon if this is a category
    val iconResId = if (showIcon) {
        com.littlechef.app.domain.model.CategoryIcons.getIconForCategory(recipeName)
    } else null
    
    // Animated rotation for chevron
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "chevron_rotation"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggleExpand
            ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Show icon if available
                if (iconResId != null) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = iconResId),
                        contentDescription = recipeName,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column {
                    Text(
                        text = translateCategory(recipeName),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Show meal types and planned date if available
                    if (mealTypes.isNotEmpty() || plannedDate != null) {
                        // Translate meal types first
                        val translatedMealTypes = mealTypes.map { mealType ->
                            when (mealType) {
                                com.littlechef.app.domain.model.MealType.BREAKFAST -> stringResource(R.string.meal_type_breakfast)
                                com.littlechef.app.domain.model.MealType.LUNCH -> stringResource(R.string.meal_type_lunch)
                                com.littlechef.app.domain.model.MealType.DINNER -> stringResource(R.string.meal_type_dinner)
                                com.littlechef.app.domain.model.MealType.SNACK -> stringResource(R.string.meal_type_snack)
                                com.littlechef.app.domain.model.MealType.DESSERT -> stringResource(R.string.meal_type_dessert)
                            }
                        }
                        
                        val infoText = buildString {
                            if (translatedMealTypes.isNotEmpty()) {
                                append(translatedMealTypes.joinToString(", "))
                            }
                            if (plannedDate != null) {
                                if (translatedMealTypes.isNotEmpty()) append(" • ")
                                val date = java.time.Instant.ofEpochMilli(plannedDate)
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDate()
                                val formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd")
                                append(date.format(formatter))
                            }
                        }
                        Text(
                            text = infoText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (servings != null) {
                        Text(
                            text = stringResource(R.string.groceries_servings, servings),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "$itemCount items",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "$checkedCount/$itemCount",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteGroceryItem(
    item: GroceryItem,
    onCheckedChange: () -> Unit,
    onDelete: () -> Unit,
    translatedName: String = item.ingredientName.split("|")[0]
) {
    // Disable swipe for checked items
    if (item.isChecked) {
        CompactGroceryItemCard(
            item = item,
            onCheckedChange = onCheckedChange,
            translatedName = translatedName
        )
    } else {
        val dismissState = rememberDismissState(
            confirmValueChange = {
                if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                    onDelete()
                    true
                } else {
                    false
                }
            },
            positionalThreshold = { distance -> distance * 0.25f }
        )

        SwipeToDismiss(
            state = dismissState,
            background = {
                val color = MaterialTheme.colorScheme.errorContainer
                val scale by animateFloatAsState(
                    targetValue = if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        color = color,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.background,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = error_color,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .scale(scale)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            dismissContent = {
                CompactGroceryItemCard(
                    item = item,
                    onCheckedChange = onCheckedChange,
                    translatedName = translatedName
                )
            },
            directions = setOf(DismissDirection.EndToStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactGroceryItemCard(
    item: GroceryItem,
    onCheckedChange: () -> Unit,
    translatedName: String = item.ingredientName.split("|")[0]
) {
    val haptic = rememberHapticFeedback()
    
    // Smooth fade animation when checking/unchecking
    val animatedAlpha by animateFloatAsState(
        targetValue = if (item.isChecked) 0.9f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 30.dp)
            .padding(vertical = 1.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = androidx.compose.material.ripple.rememberRipple(
                    color = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    haptic.performLight()
                    onCheckedChange()
                }
            )
            .graphicsLayer {
                alpha = animatedAlpha
            },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onCheckedChange() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            
            Text(
                text = translatedName.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (item.isChecked) FontWeight.Normal else FontWeight.Bold,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                color = if (item.isChecked) 
                    MaterialTheme.colorScheme.onSurfaceVariant 
                else 
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (item.isChecked)
                    MaterialTheme.colorScheme.background
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(
                    text = translateFormattedUnit(UnitConversion.formatForDisplay(item.quantity, item.unit)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isChecked)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseQuantityDialog(
    item: GroceryItem,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit,
    translatedName: String = item.ingredientName
) {
    // Translate unit function
    @Composable
    fun translateUnit(unit: String): String {
        return when (unit.lowercase()) {
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
    }
    
    // Look up the ingredient in the catalog to determine its proper unit type
    val catalogIngredient = remember(item.ingredientName) {
        com.littlechef.app.domain.model.IngredientCatalog.allIngredients.find { 
            it.nameKey.equals(item.ingredientName, ignoreCase = true) 
        }
    }
    
    // Determine default unit and compatible units based on catalog ingredient
    val (defaultUnit, compatibleUnits) = remember(item.ingredientName, item.unit) {
        when {
            // If catalog ingredient exists, use its default unit type
            catalogIngredient != null && UnitConversion.isVolumeUnit(catalogIngredient.defaultUnit) -> {
                "ml" to UnitConversion.getVolumeUnits()
            }
            catalogIngredient != null && UnitConversion.isWeightUnit(catalogIngredient.defaultUnit) -> {
                "g" to UnitConversion.getWeightUnits()
            }
            // Fallback to item's unit type
            UnitConversion.isWeightUnit(item.unit) -> {
                item.unit to UnitConversion.getWeightUnits()
            }
            UnitConversion.isVolumeUnit(item.unit) -> {
                item.unit to UnitConversion.getVolumeUnits()
            }
            // For pieces (eggs), keep as pieces - no conversion
            UnitConversion.isPieceUnit(item.unit) -> {
                "pcs" to listOf("pcs")
            }
            // Default to weight for unknown types
            else -> {
                "g" to UnitConversion.getWeightUnits()
            }
        }
    }
    
    // Prefer larger units: kg over g, L over ml
    val preferredUnit = when (defaultUnit) {
        "g" -> if (compatibleUnits.contains("kg")) "kg" else "g"
        "ml" -> if (compatibleUnits.contains("L")) "L" else "ml"
        else -> defaultUnit
    }
    
    var quantity by remember { mutableStateOf("1") }
    var selectedUnit by remember { mutableStateOf(preferredUnit) }
    val showUnitSelector = compatibleUnits.size > 1
    val haptic = rememberHapticFeedback()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Show ingredient icon if available
                catalogIngredient?.let { ingredient ->
                    Image(
                        painter = painterResource(id = com.littlechef.app.domain.model.CategoryIcons.getIconForIngredient(
                            category = ingredient.category.displayName,
                            subcategory = ingredient.subcategory
                        )),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = translatedName.replaceFirstChar { 
                        if (it.isLowerCase()) it.titlecase() else it.toString() 
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.groceries_how_much_buy),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
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
                    
                    // Quantity input field
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { newValue ->
                            quantity = newValue.filter { it.isDigit() }
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    
                    // iOS-style vertical toggle switch (only if multiple units available)
                    if (showUnitSelector && compatibleUnits.size == 2) {
                        val isFirstUnit = selectedUnit == compatibleUnits[0]
                        
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .width(44.dp)
                                .height(80.dp)
                                .clickable {
                                    val currentIndex = compatibleUnits.indexOf(selectedUnit)
                                    val nextIndex = (currentIndex + 1) % compatibleUnits.size
                                    selectedUnit = compatibleUnits[nextIndex]
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
                        // Static unit label
                        Text(
                            text = translateUnit(selectedUnit),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
                
                // Determine if item is from a recipe or manually added
                // Manually added items have mealName set to a category (e.g., "Seafood", "Dairy & Eggs")
                // Recipe items have mealName set to the recipe name
                val categoryNames = com.littlechef.app.domain.model.IngredientCategory.entries.map { it.displayName }
                val isManuallyAdded = categoryNames.any { it.equals(item.mealName, ignoreCase = true) }
                
                Text(
                    text = if (isManuallyAdded) {
                        stringResource(R.string.groceries_we_decided, translateFormattedUnit(UnitConversion.formatForDisplay(item.quantity, item.unit)))
                    } else {
                        stringResource(R.string.groceries_recipe_needs, translateFormattedUnit(UnitConversion.formatForDisplay(item.quantity, item.unit)))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Buttons row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val qty = quantity.toDoubleOrNull()
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    
                    Button(
                        onClick = {
                            if (qty != null && qty > 0) {
                                onConfirm(qty, selectedUnit)
                            }
                        },
                        enabled = qty != null && qty > 0,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(stringResource(R.string.groceries_add_to_pantry))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
