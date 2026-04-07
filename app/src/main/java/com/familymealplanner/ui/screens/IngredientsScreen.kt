package com.familymealplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.ui.util.SwipeToDeleteContainer
import com.familymealplanner.ui.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    viewModel: IngredientsViewModel = hiltViewModel(),
    onNavigateToForm: (String?) -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val haptic = rememberHapticFeedback()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredients", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    IconButton(onClick = { onNavigateToForm(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
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
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search ingredients...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            when (val state = uiState) {
                is IngredientsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is IngredientsUiState.Success -> {
                    if (state.filteredIngredients.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "No ingredients yet" else "No matching ingredients",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Group by category first, then by subcategory within each category
                        val groupedByCategory = state.filteredIngredients.groupBy { it.category ?: "Uncategorized" }
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            groupedByCategory.forEach { (category, categoryIngredients) ->
                                // Category header
                                item {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                
                                // Group by subcategory within this category
                                val groupedBySubcategory = categoryIngredients.groupBy { 
                                    it.subcategory ?: "Other" 
                                }
                                
                                groupedBySubcategory.forEach { (subcategory, ingredients) ->
                                    // Subcategory header
                                    item {
                                        Text(
                                            text = subcategory,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)
                                        )
                                    }
                                    
                                    // Ingredients under this subcategory
                                    items(ingredients) { ingredient ->
                                        SwipeToDeleteContainer(
                                            item = ingredient,
                                            onDelete = {
                                                haptic.performDestructive()
                                                viewModel.deleteIngredient(ingredient)
                                            },
                                            confirmationTitle = "Delete Ingredient",
                                            confirmationMessage = "Are you sure you want to delete ${ingredient.name.replaceFirstChar { 
                                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                                            }}? This cannot be undone."
                                        ) {
                                            Box(modifier = Modifier.padding(start = 32.dp)) {
                                                IngredientCard(
                                                    ingredient = ingredient,
                                                    onClick = { onNavigateToDetail(ingredient.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is IngredientsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: com.familymealplanner.domain.model.Ingredient,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = ingredient.name.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Unit: ${ingredient.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (ingredient.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Allergens: ${ingredient.allergens.joinToString { it.name }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
