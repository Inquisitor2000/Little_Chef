package com.littlechef.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.domain.model.Ingredient
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.ui.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealFormScreen(
    mealId: String?,
    viewModel: MealsViewModel = hiltViewModel(),
    ingredientsViewModel: IngredientsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val mealsState by viewModel.uiState.collectAsState()
    val ingredientsState by ingredientsViewModel.uiState.collectAsState()
    val haptic = rememberHapticFeedback()

    var name by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var prepTimeMinutes by remember { mutableStateOf("") }
    var cookTimeMinutes by remember { mutableStateOf("") }
    var servings by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf<com.littlechef.app.domain.model.MealType?>(null) }
    var selectedDishCategory by remember { mutableStateOf<com.littlechef.app.domain.model.DishCategory?>(null) }
    var selectedIngredients by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var showIngredientPicker by remember { mutableStateOf(false) }
    var meal by remember { mutableStateOf<Meal?>(null) }

    // Load meal if editing
    LaunchedEffect(mealId, mealsState) {
        if (mealId != null && mealsState is MealsUiState.Success) {
            val m = (mealsState as MealsUiState.Success).meals.find { it.id == mealId }
            m?.let {
                meal = it
                name = it.name
                instructions = it.instructions ?: ""
                prepTimeMinutes = it.prepTimeMinutes?.toString() ?: ""
                cookTimeMinutes = it.cookTimeMinutes?.toString() ?: ""
                servings = it.servings?.toString() ?: ""
                selectedMealType = it.mealType
                selectedDishCategory = it.dishCategory
                selectedIngredients = it.ingredients.map { ing -> 
                    ing.ingredient.id to ing.quantity.toString()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    text = if (mealId == null) "Add Meal" else "Edit Meal",
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
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = prepTimeMinutes,
                    onValueChange = { prepTimeMinutes = it.filter { c -> c.isDigit() } },
                    label = { Text("Prep (min)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cookTimeMinutes,
                    onValueChange = { cookTimeMinutes = it.filter { c -> c.isDigit() } },
                    label = { Text("Cook (min)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = servings,
                    onValueChange = { servings = it.filter { c -> c.isDigit() } },
                    label = { Text("Servings") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Category Selection
            var mealTypeExpanded by remember { mutableStateOf(false) }
            var dishCategoryExpanded by remember { mutableStateOf(false) }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Meal Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = mealTypeExpanded,
                    onExpandedChange = { mealTypeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedMealType?.let { "${it.emoji} ${it.displayName}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = mealTypeExpanded,
                        onDismissRequest = { mealTypeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedMealType = null
                                mealTypeExpanded = false
                            }
                        )
                        com.littlechef.app.domain.model.MealType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text("${type.emoji} ${type.displayName}") },
                                onClick = {
                                    selectedMealType = type
                                    mealTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Dish Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = dishCategoryExpanded,
                    onExpandedChange = { dishCategoryExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedDishCategory?.let { "${it.emoji} ${it.displayName}" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dish Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dishCategoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = dishCategoryExpanded,
                        onDismissRequest = { dishCategoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedDishCategory = null
                                dishCategoryExpanded = false
                            }
                        )
                        com.littlechef.app.domain.model.DishCategory.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text("${category.emoji} ${category.displayName}") },
                                onClick = {
                                    selectedDishCategory = category
                                    dishCategoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Ingredients section
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium
            )

            if (selectedIngredients.isNotEmpty() && ingredientsState is IngredientsUiState.Success) {
                val allIngredients = (ingredientsState as IngredientsUiState.Success).ingredients
                selectedIngredients.forEach { (ingredientId, quantity) ->
                    val ingredient = allIngredients.find { it.id == ingredientId }
                    ingredient?.let {
                        IngredientQuantityRow(
                            ingredient = it,
                            quantity = quantity,
                            onQuantityChange = { newQuantity ->
                                selectedIngredients = selectedIngredients.map { pair ->
                                    if (pair.first == ingredientId) ingredientId to newQuantity else pair
                                }
                            },
                            onRemove = {
                                selectedIngredients = selectedIngredients.filter { it.first != ingredientId }
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { showIngredientPicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Ingredient")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    haptic.performSuccess()
                    val ingredientPairs = selectedIngredients.mapNotNull { (id, qty) ->
                        qty.toDoubleOrNull()?.let { id to it }
                    }
                    
                    if (mealId == null) {
                        viewModel.createMeal(
                            name = name,
                            instructions = instructions.ifBlank { null },
                            prepTimeMinutes = prepTimeMinutes.toIntOrNull(),
                            cookTimeMinutes = cookTimeMinutes.toIntOrNull(),
                            servings = servings.toIntOrNull(),
                            mealType = selectedMealType,
                            dishCategory = selectedDishCategory,
                            ingredients = ingredientPairs
                        )
                    } else {
                        meal?.let {
                            viewModel.updateMeal(
                                meal = it,
                                name = name,
                                instructions = instructions.ifBlank { null },
                                prepTimeMinutes = prepTimeMinutes.toIntOrNull(),
                                cookTimeMinutes = cookTimeMinutes.toIntOrNull(),
                                servings = servings.toIntOrNull(),
                                mealType = selectedMealType,
                                dishCategory = selectedDishCategory,
                                ingredients = ingredientPairs
                            )
                        }
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && selectedIngredients.isNotEmpty()
            ) {
                Text(if (mealId == null) "Create Meal" else "Update Meal")
            }
        }

        // Ingredient picker dialog
        if (showIngredientPicker && ingredientsState is IngredientsUiState.Success) {
            val availableIngredients = (ingredientsState as IngredientsUiState.Success).ingredients
                .filter { ingredient -> selectedIngredients.none { it.first == ingredient.id } }
            
            AlertDialog(
                onDismissRequest = { showIngredientPicker = false },
                title = { Text("Select Ingredient") },
                text = {
                    LazyColumn {
                        items(availableIngredients) { ingredient ->
                            TextButton(
                                onClick = {
                                    selectedIngredients = selectedIngredients + (ingredient.id to "1.0")
                                    showIngredientPicker = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(ingredient.name, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showIngredientPicker = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun IngredientQuantityRow(
    ingredient: Ingredient,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = ingredient.name,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        
        OutlinedTextField(
            value = quantity,
            onValueChange = { onQuantityChange(it.filter { c -> c.isDigit() || c == '.' }) },
            modifier = Modifier.width(100.dp),
            singleLine = true,
            suffix = { Text(ingredient.unit) }
        )
        
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}
