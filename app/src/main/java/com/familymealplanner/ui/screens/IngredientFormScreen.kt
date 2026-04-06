package com.familymealplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.domain.model.Allergen
import com.familymealplanner.domain.model.Ingredient
import com.familymealplanner.domain.model.IngredientCatalog
import com.familymealplanner.domain.model.IngredientCategory
import com.familymealplanner.ui.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientFormScreen(
    ingredientId: String?,
    viewModel: IngredientsViewModel = hiltViewModel(),
    allergensViewModel: AllergensViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val ingredientsState by viewModel.uiState.collectAsState()
    val allergensState by allergensViewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var subcategory by remember { mutableStateOf("") }
    var selectedAllergenIds by remember { mutableStateOf(setOf<String>()) }
    var showAllergenPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSubcategoryPicker by remember { mutableStateOf(false) }
    var ingredient by remember { mutableStateOf<Ingredient?>(null) }

    // Load ingredient if editing
    LaunchedEffect(ingredientId, ingredientsState) {
        if (ingredientId != null && ingredientsState is IngredientsUiState.Success) {
            val ing = (ingredientsState as IngredientsUiState.Success).ingredients.find { it.id == ingredientId }
            ing?.let {
                ingredient = it
                name = it.name
                unit = it.unit
                category = it.category ?: ""
                subcategory = it.subcategory ?: ""
                selectedAllergenIds = it.allergens.map { a -> a.id }.toSet()
            }
        }
    }

    // Get available subcategories for selected category
    val availableSubcategories = remember(category) {
        if (category.isBlank()) {
            emptyList()
        } else {
            // Get all unique subcategories from catalog for this category
            IngredientCatalog.allIngredients
                .filter { it.category.displayName == category }
                .mapNotNull { it.subcategory }
                .distinct()
                .sorted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = if (ingredientId == null) "Add Ingredient" else "Edit Ingredient",
                    style = MaterialTheme.typography.headlineSmall
                ) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit (e.g., cups, grams)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category picker
            OutlinedButton(
                onClick = { showCategoryPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (category.isBlank()) "Select Category"
                    else "Category: $category"
                )
            }

            // Subcategory picker (only show if category is selected)
            if (category.isNotBlank()) {
                OutlinedButton(
                    onClick = { showSubcategoryPicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = availableSubcategories.isNotEmpty()
                ) {
                    Text(
                        if (subcategory.isBlank()) "Select Subcategory"
                        else "Subcategory: $subcategory"
                    )
                }
            }

            // Allergen selection
            OutlinedButton(
                onClick = { showAllergenPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (selectedAllergenIds.isEmpty()) "Select Allergens (optional)"
                    else "Allergens: ${selectedAllergenIds.size} selected"
                )
            }

            if (selectedAllergenIds.isNotEmpty() && allergensState is AllergensUiState.Success) {
                val allergens = (allergensState as AllergensUiState.Success).allergens
                val selectedAllergens = allergens.filter { it.id in selectedAllergenIds }
                Text(
                    text = selectedAllergens.joinToString { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    haptic.performSuccess()
                    if (ingredientId == null) {
                        viewModel.createIngredient(
                            name = name,
                            unit = unit,
                            category = category.ifBlank { null },
                            subcategory = subcategory.ifBlank { null },
                            allergenIds = selectedAllergenIds.toList()
                        )
                    } else {
                        ingredient?.let {
                            viewModel.updateIngredient(
                                ingredient = it,
                                name = name,
                                unit = unit,
                                category = category.ifBlank { null },
                                subcategory = subcategory.ifBlank { null },
                                allergenIds = selectedAllergenIds.toList()
                            )
                        }
                    }
                    onNavigateBack()
                },
                enabled = name.isNotBlank() && unit.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (ingredientId == null) "Create" else "Update")
            }
        }
    }

    // Category picker dialog
    if (showCategoryPicker) {
        CategoryPickerDialog(
            categories = IngredientCategory.values().map { it.displayName },
            selectedCategory = category,
            onDismiss = { showCategoryPicker = false },
            onConfirm = { selected ->
                category = selected
                // Reset subcategory when category changes
                subcategory = ""
                showCategoryPicker = false
            }
        )
    }

    // Subcategory picker dialog
    if (showSubcategoryPicker && availableSubcategories.isNotEmpty()) {
        SubcategoryPickerDialog(
            subcategories = availableSubcategories,
            selectedSubcategory = subcategory,
            onDismiss = { showSubcategoryPicker = false },
            onConfirm = { selected ->
                subcategory = selected
                showSubcategoryPicker = false
            }
        )
    }

    if (showAllergenPicker && allergensState is AllergensUiState.Success) {
        AllergenPickerDialog(
            allergens = (allergensState as AllergensUiState.Success).allergens,
            selectedIds = selectedAllergenIds,
            onDismiss = { showAllergenPicker = false },
            onConfirm = { selected ->
                selectedAllergenIds = selected
                showAllergenPicker = false
            }
        )
    }
}

@Composable
fun AllergenPickerDialog(
    allergens: List<Allergen>,
    selectedIds: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedIds) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Allergens") },
        text = {
            LazyColumn {
                items(allergens) { allergen ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = allergen.id in tempSelected,
                            onCheckedChange = { checked ->
                                tempSelected = if (checked) {
                                    tempSelected + allergen.id
                                } else {
                                    tempSelected - allergen.id
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = allergen.name,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempSelected) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CategoryPickerDialog(
    categories: List<String>,
    selectedCategory: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedCategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Category") },
        text = {
            LazyColumn {
                items(categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = category == tempSelected,
                            onClick = { tempSelected = category }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tempSelected) },
                enabled = tempSelected.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SubcategoryPickerDialog(
    subcategories: List<String>,
    selectedSubcategory: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedSubcategory) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Subcategory") },
        text = {
            LazyColumn {
                items(subcategories) { subcategory ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = subcategory == tempSelected,
                            onClick = { tempSelected = subcategory }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = subcategory,
                            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(tempSelected) },
                enabled = tempSelected.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
