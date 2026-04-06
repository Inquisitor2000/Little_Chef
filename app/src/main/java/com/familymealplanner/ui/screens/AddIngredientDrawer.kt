package com.familymealplanner.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.familymealplanner.R
import com.familymealplanner.domain.model.CatalogIngredient
import com.familymealplanner.domain.model.CommonAllergen
import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.model.IngredientCategory
import com.familymealplanner.domain.model.UnitOptions
import com.familymealplanner.ui.theme.md_theme_light_background
import com.familymealplanner.ui.util.BottomDrawer
import com.familymealplanner.ui.util.rememberHapticFeedback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddIngredientDrawer(
    visible: Boolean,
    onDismiss: () -> Unit,
    onAddIngredient: (name: String, quantity: Double, unit: String, category: String, subcategory: String, allergens: List<String>) -> Unit,
    preferences: com.familymealplanner.data.preferences.OnboardingPreferences,
    ingredientRepository: com.familymealplanner.domain.repository.IngredientRepository,
    onEditExistingIngredient: ((String) -> Unit)? = null,
    onNavigateToCustomIngredient: ((String) -> Unit)? = null,
    translationSystem: com.familymealplanner.data.local.TranslationSystem? = null,
    onVoiceInputClick: (() -> Unit)? = null
) {
    // Translation helper
    val translateCategory: (String) -> String = { categoryName ->
        translationSystem?.translateCategory(categoryName) ?: categoryName
    }
    val translateIngredient: (String) -> String = { ingredientName ->
        translationSystem?.translateIngredient(ingredientName) ?: ingredientName
    }
    
    // Unit translation helper using string resources
    val translateUnit: @Composable (String) -> String = { unit ->
        when (unit.lowercase()) {
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
            else -> unit
        }
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearchFocused by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<CatalogIngredient?>(null) }
    var selectedCustomIngredient by remember { mutableStateOf<com.familymealplanner.domain.model.Ingredient?>(null) }
    var showQuantityDialog by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf<IngredientCategory?>(null) }
    var expandedSubcategory by remember { mutableStateOf<String?>(null) }
    
    // Search results from database
    var databaseIngredients by remember { mutableStateOf<List<com.familymealplanner.domain.model.Ingredient>>(emptyList()) }
    
    // All custom ingredients from database (for category browsing)
    var allCustomIngredients by remember { mutableStateOf<List<com.familymealplanner.domain.model.Ingredient>>(emptyList()) }
    
    val listState = rememberLazyListState()
    val searchListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Reset state when drawer closes
    LaunchedEffect(visible) {
        if (!visible) {
            searchQuery = ""
            selectedIngredient = null
            selectedCustomIngredient = null
            showQuantityDialog = false
            expandedCategory = null
            expandedSubcategory = null
            databaseIngredients = emptyList()
        }
    }
    
    // Load all custom ingredients when drawer opens
    LaunchedEffect(visible) {
        if (visible) {
            allCustomIngredients = ingredientRepository.getAllIngredients()
        }
    }
    
    // Search database when query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            databaseIngredients = ingredientRepository.searchIngredients(searchQuery)
        } else {
            databaseIngredients = emptyList()
        }
    }
    
    // Dismiss keyboard when scrolling
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }
    
    LaunchedEffect(searchListState.isScrollInProgress) {
        if (searchListState.isScrollInProgress) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    }

    val filteredIngredients = remember(searchQuery) {
        if (searchQuery.isBlank()) null
        else {
            val lowerQuery = searchQuery.lowercase()
            // Search in both English names and translated names
            IngredientCatalog.allIngredients.filter { ingredient ->
                // Search in English name
                val englishMatch = ingredient.nameKey.lowercase().contains(lowerQuery)
                // Search in translated name
                val translatedName = translateIngredient(ingredient.nameKey).lowercase()
                val translatedMatch = translatedName.contains(lowerQuery)
                englishMatch || translatedMatch
            }
        }
    }
    
    // Combine catalog and database results
    val allSearchResults = remember(filteredIngredients, databaseIngredients) {
        if (searchQuery.isBlank()) null
        else {
            val catalog = filteredIngredients ?: emptyList()
            val database = databaseIngredients
            catalog to database
        }
    }
    
    val showCategories = !isSearchFocused && searchQuery.isBlank()

    BottomDrawer(
        visible = visible,
        onDismiss = onDismiss,
        title = stringResource(R.string.add_ingredient_title)
    ) {
        // Search bar with voice button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { isSearchFocused = it.isFocused },
                placeholder = { Text(stringResource(R.string.add_ingredient_search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
            
            // Voice input button
            if (onVoiceInputClick != null) {
                Surface(
                    onClick = onVoiceInputClick,
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = stringResource(R.string.voice_input),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        if (allSearchResults != null) {
            val (catalogResults, dbResults) = allSearchResults
            
            // Merge catalog and database ingredients into a single unified list
            // Convert both to a common type for unified display
            data class UnifiedIngredient(
                val name: String,
                val category: String,
                val subcategory: String?,
                val isCustom: Boolean,
                val catalogIngredient: CatalogIngredient? = null,
                val dbIngredient: Ingredient? = null
            )
            
            val unifiedResults = buildList {
                // Add database (custom) ingredients
                addAll(dbResults.map { ingredient ->
                    UnifiedIngredient(
                        name = ingredient.name,
                        category = ingredient.category ?: "Other",
                        subcategory = ingredient.subcategory,
                        isCustom = true,
                        dbIngredient = ingredient
                    )
                })
                // Add catalog ingredients
                addAll(catalogResults.map { ingredient ->
                    UnifiedIngredient(
                        name = ingredient.nameKey,
                        category = translateCategory(ingredient.category.displayName),
                        subcategory = ingredient.subcategory,
                        isCustom = false,
                        catalogIngredient = ingredient
                    )
                })
            }.sortedBy { it.name.lowercase() } // Sort alphabetically
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = searchListState
            ) {
                // Show unified list of all ingredients
                items(unifiedResults) { unifiedIngredient ->
                    if (unifiedIngredient.isCustom) {
                        DrawerCustomIngredientItem(
                            ingredient = unifiedIngredient.dbIngredient!!,
                            onClick = {
                                // If callback is provided, navigate to edit screen
                                // Otherwise show quantity dialog to add more
                                if (onEditExistingIngredient != null) {
                                    onEditExistingIngredient(unifiedIngredient.dbIngredient.id)
                                    onDismiss()
                                } else {
                                    selectedCustomIngredient = unifiedIngredient.dbIngredient
                                    showQuantityDialog = true
                                }
                            },
                            translateIngredient = translateIngredient
                        )
                    } else {
                        DrawerIngredientItem(
                            ingredient = unifiedIngredient.catalogIngredient!!,
                            onClick = {
                                selectedIngredient = unifiedIngredient.catalogIngredient
                                selectedCustomIngredient = null
                                showQuantityDialog = true
                            },
                            translateIngredient = translateIngredient
                        )
                    }
                }
                
                if (unifiedResults.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.no_ingredients_found),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Button(
                                    onClick = { 
                                        onNavigateToCustomIngredient?.invoke(searchQuery)
                                        onDismiss()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(stringResource(R.string.screen_add_custom_ingredient))
                                }
                            }
                        }
                    }
                }
            }
        } else if (showCategories) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                IngredientCategory.entries.forEachIndexed { index, category ->
                    val isCategoryExpanded = expandedCategory == category
                    val catalogIngredients = IngredientCatalog.getByCategory(category)
                    
                    // Get custom ingredients for this category, excluding ones that exist in catalog
                    val catalogIngredientNames = catalogIngredients.map { it.nameKey.lowercase() }.toSet()
                    val customIngredientsForCategory = allCustomIngredients.filter { 
                        (it.category?.equals(category.key, ignoreCase = true) == true ||
                        it.category?.equals(category.displayName, ignoreCase = true) == true) &&
                        !catalogIngredientNames.contains(it.name.lowercase()) // Exclude if already in catalog
                    }
                    
                    val totalItemCount = catalogIngredients.size + customIngredientsForCategory.size
                    val subcategories = IngredientCatalog.getSubcategories(category)
                    
                    // Get unique subcategories from both catalog and custom ingredients
                    val customSubcategories = customIngredientsForCategory
                        .mapNotNull { it.subcategory }
                        .distinct()
                    val allSubcategories = (subcategories + customSubcategories).distinct()
                    
                    item(key = category.key) {
                        DrawerCategoryHeader(
                            category = category,
                            isExpanded = isCategoryExpanded,
                            itemCount = totalItemCount,
                            onClick = {
                                if (isCategoryExpanded) {
                                    expandedCategory = null
                                    expandedSubcategory = null
                                } else {
                                    expandedCategory = category
                                    expandedSubcategory = null
                                    coroutineScope.launch {
                                        delay(50)
                                        listState.animateScrollToItem(index)
                                    }
                                }
                            },
                            translateCategory = translateCategory
                        )
                        
                        AnimatedVisibility(
                            visible = isCategoryExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(modifier = Modifier.padding(start = 24.dp)) {
                                allSubcategories.forEach { subcategory ->
                                    val isSubcategoryExpanded = expandedSubcategory == subcategory
                                    val catalogSubcategoryIngredients = IngredientCatalog.getByCategoryAndSubcategory(category, subcategory)
                                    
                                    // Get custom ingredients for this subcategory, excluding ones that exist in catalog
                                    val catalogIngredientNames = catalogSubcategoryIngredients.map { it.nameKey.lowercase() }.toSet()
                                    val customSubcategoryIngredients = customIngredientsForCategory.filter {
                                        it.subcategory?.equals(subcategory, ignoreCase = true) == true &&
                                        !catalogIngredientNames.contains(it.name.lowercase()) // Exclude if already in catalog
                                    }
                                    
                                    val subcategoryItemCount = catalogSubcategoryIngredients.size + customSubcategoryIngredients.size
                                    
                                    DrawerSubcategoryHeader(
                                        category = category.displayName,
                                        subcategory = subcategory,
                                        isExpanded = isSubcategoryExpanded,
                                        itemCount = subcategoryItemCount,
                                        onClick = {
                                            expandedSubcategory = if (isSubcategoryExpanded) null else subcategory
                                        },
                                        translateCategory = translateCategory
                                    )
                                    
                                    AnimatedVisibility(
                                        visible = isSubcategoryExpanded,
                                        enter = expandVertically() + fadeIn(),
                                        exit = shrinkVertically() + fadeOut()
                                    ) {
                                        Column(modifier = Modifier.padding(start = 24.dp)) {
                                            // Show catalog ingredients first
                                            catalogSubcategoryIngredients.forEach { ingredient ->
                                                DrawerIngredientItem(
                                                    ingredient = ingredient,
                                                    onClick = {
                                                        selectedIngredient = ingredient
                                                        showQuantityDialog = true
                                                    },
                                                    translateIngredient = translateIngredient
                                                )
                                            }
                                            // Then show custom ingredients
                                            customSubcategoryIngredients.forEach { ingredient ->
                                                DrawerCustomIngredientItem(
                                                    ingredient = ingredient,
                                                    onClick = {
                                                        if (onEditExistingIngredient != null) {
                                                            onEditExistingIngredient(ingredient.id)
                                                            onDismiss()
                                                        } else {
                                                            selectedCustomIngredient = ingredient
                                                            showQuantityDialog = true
                                                        }
                                                    },
                                                    translateIngredient = translateIngredient
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
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.add_ingredient_empty_search),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }


    // Quantity dialog for catalog ingredients
    if (showQuantityDialog && selectedIngredient != null) {
        DrawerQuantityDialog(
            ingredient = selectedIngredient!!,
            translateIngredient = translateIngredient,
            translateUnit = translateUnit,
            onDismiss = { 
                showQuantityDialog = false
                selectedIngredient = null
            },
            onConfirm = { quantity, unit ->
                onAddIngredient(
                    selectedIngredient!!.nameKey,
                    quantity,
                    unit,
                    selectedIngredient!!.category.displayName,
                    selectedIngredient!!.subcategory ?: "Other",
                    selectedIngredient!!.allergens.map { it.displayName }
                )
                showQuantityDialog = false
                selectedIngredient = null
                onDismiss()
            }
        )
    }
    
    // Quantity dialog for custom ingredients from database
    if (showQuantityDialog && selectedCustomIngredient != null) {
        DrawerCustomQuantityDialog(
            ingredient = selectedCustomIngredient!!,
            translateIngredient = translateIngredient,
            translateUnit = translateUnit,
            onDismiss = { 
                showQuantityDialog = false
                selectedCustomIngredient = null
            },
            onConfirm = { quantity, unit ->
                onAddIngredient(
                    selectedCustomIngredient!!.name,
                    quantity,
                    unit,
                    selectedCustomIngredient!!.category ?: "Other",
                    selectedCustomIngredient!!.subcategory ?: "Other",
                    selectedCustomIngredient!!.allergens.map { it.name }
                )
                showQuantityDialog = false
                selectedCustomIngredient = null
                onDismiss()
            }
        )
    }

}


@Composable
private fun DrawerCategoryHeader(
    category: IngredientCategory,
    isExpanded: Boolean,
    itemCount: Int,
    onClick: () -> Unit,
    translateCategory: (String) -> String = { it }
) {
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
                onClick = onClick
            ),
        color = if (isExpanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
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
                Image(
                    painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForCategory(category.displayName)),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = translateCategory(category.displayName),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExpanded) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = stringResource(R.string.add_ingredient_items, itemCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = if (isExpanded) stringResource(R.string.add_ingredient_collapse) else stringResource(R.string.add_ingredient_expand),
                modifier = Modifier.size(24.dp).rotate(rotationAngle),
                tint = if (isExpanded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}


@Composable
private fun DrawerSubcategoryHeader(
    category: String,
    subcategory: String,
    isExpanded: Boolean,
    itemCount: Int,
    onClick: () -> Unit,
    translateCategory: (String) -> String = { it }
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "subcategory_chevron_rotation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        color = if (isExpanded) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
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
                Image(
                    painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                        category = category,
                        subcategory = subcategory
                    )),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = translateCategory(subcategory),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isExpanded) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = stringResource(R.string.add_ingredient_items, itemCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = if (isExpanded) stringResource(R.string.add_ingredient_collapse) else stringResource(R.string.add_ingredient_expand),
                modifier = Modifier.size(24.dp).rotate(rotationAngle),
                tint = if (isExpanded) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}


@Composable
private fun DrawerIngredientItem(
    ingredient: CatalogIngredient,
    onClick: () -> Unit,
    translateIngredient: (String) -> String = { it }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                onClick = onClick
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // No icon for individual ingredients - only categories/subcategories have icons
            Text(
                text = translateIngredient(ingredient.nameKey),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DrawerCustomIngredientItem(
    ingredient: com.familymealplanner.domain.model.Ingredient,
    onClick: () -> Unit,
    translateIngredient: (String) -> String = { it }
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                onClick = onClick
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // No icon for individual ingredients - only categories/subcategories have icons
            Text(
                text = translateIngredient(ingredient.name),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
private fun DrawerQuantityDialog(
    ingredient: CatalogIngredient,
    translateIngredient: (String) -> String,
    translateUnit: @Composable (String) -> String,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, unit: String) -> Unit
) {
    var quantity by remember { mutableStateOf("1") }
    val defaultUnit = ingredient.defaultUnit
    val allowedUnits = remember(defaultUnit) {
        UnitOptions.getAllowedUnitsForIngredient(defaultUnit)
    }
    val preferredUnit = when (defaultUnit) {
        "g" -> if (allowedUnits.contains("kg")) "kg" else "g"
        "ml" -> if (allowedUnits.contains("L")) "L" else "ml"
        else -> defaultUnit
    }
    var selectedUnit by remember { mutableStateOf(preferredUnit) }
    val showUnitSelector = allowedUnits.size > 1
    val haptic = rememberHapticFeedback()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                        category = ingredient.category.displayName,
                        subcategory = ingredient.subcategory
                    )),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(translateIngredient(ingredient.nameKey))
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                        modifier = Modifier.weight(1f),
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
                                            fontWeight = FontWeight.SemiBold,
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
                        )
                    ) {
                        Text(stringResource(R.string.button_cancel))
                    }
                    
                    Button(
                        onClick = { onConfirm(qty!!, selectedUnit) },
                        enabled = qty != null && qty > 0,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.button_add))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerCustomQuantityDialog(
    ingredient: com.familymealplanner.domain.model.Ingredient,
    translateIngredient: (String) -> String,
    translateUnit: @Composable (String) -> String,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Double, unit: String) -> Unit
) {
    var quantity by remember { mutableStateOf("1") }
    val defaultUnit = ingredient.unit
    val allowedUnits = remember(defaultUnit) {
        UnitOptions.getAllowedUnitsForIngredient(defaultUnit)
    }
    var selectedUnit by remember { mutableStateOf(defaultUnit) }
    val showUnitSelector = allowedUnits.size > 1
    val haptic = rememberHapticFeedback()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = com.familymealplanner.domain.model.CategoryIcons.getIconForIngredient(
                        category = ingredient.category ?: "Other",
                        subcategory = ingredient.subcategory
                    )),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(translateIngredient(ingredient.name))
            }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                
                if (showUnitSelector) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.width(100.dp)
                    ) {
                        OutlinedTextField(
                            value = translateUnit(selectedUnit),
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor(),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            allowedUnits.forEach { unit ->
                                DropdownMenuItem(
                                    text = { Text(translateUnit(unit)) },
                                    onClick = {
                                        selectedUnit = unit
                                        expanded = false
                                        haptic.performLight()
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = translateUnit(selectedUnit),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val qty = quantity.toDoubleOrNull()
                    if (qty != null && qty > 0) {
                        onConfirm(qty, selectedUnit)
                    }
                },
                enabled = quantity.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text(stringResource(R.string.button_add))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(stringResource(R.string.button_cancel))
            }
        }
    )
}


