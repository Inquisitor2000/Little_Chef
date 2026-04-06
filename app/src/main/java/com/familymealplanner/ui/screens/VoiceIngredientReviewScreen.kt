package com.familymealplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.familymealplanner.R
import com.familymealplanner.domain.model.VoiceIngredientItem
import com.familymealplanner.ui.util.rememberHapticFeedback
import kotlinx.coroutines.launch

/**
 * New simplified voice ingredient review screen.
 * 
 * Shows a list of matched ingredients with:
 * - Ingredient name (from catalog or parsed)
 * - Default unit (from catalog)
 * - Quantity counter (stepper to select how many)
 * - Warning icon for unrecognized ingredients
 * 
 * User selects quantity for each ingredient, then saves all at once.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceIngredientReviewScreen(
    ingredients: List<VoiceIngredientItem>,
    viewModel: PantryViewModel,
    onNavigateBack: () -> Unit
) {
    // Mutable list to track ingredient quantities
    var ingredientList by remember { mutableStateOf(ingredients) }
    var isLoading by remember { mutableStateOf(false) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = rememberHapticFeedback()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.ingredient_review_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel button
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.ingredient_review_cancel))
                    }
                    
                    // Save button
                    Button(
                        onClick = {
                            isLoading = true
                            haptic.performTextHandleMove()
                            
                            coroutineScope.launch {
                                var successCount = 0
                                var failedCount = 0
                                
                                // Only add ingredients with quantity > 0
                                ingredientList.filter { it.quantity > 0 }.forEach { item ->
                                    try {
                                        viewModel.addIngredient(
                                            name = item.displayName,
                                            quantity = item.quantity.toDouble(),
                                            unit = item.defaultUnit,
                                            category = item.category.key,
                                            subcategory = item.subcategory,
                                            allergenNames = item.allergens.map { it.displayName }
                                        )
                                        successCount++
                                    } catch (e: Exception) {
                                        failedCount++
                                    }
                                }
                                
                                isLoading = false
                                
                                // Show result message
                                val totalCount = successCount + failedCount
                                val message = if (failedCount == 0) {
                                    context.getString(R.string.recipe_ingredients_added_count, successCount, totalCount)
                                } else {
                                    context.getString(R.string.recipe_ingredients_added_with_failures, successCount, totalCount, failedCount)
                                }
                                
                                snackbarHostState.showSnackbar(message)
                                
                                // Navigate back after showing message
                                kotlinx.coroutines.delay(1000)
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && ingredientList.any { it.quantity > 0 }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(R.string.voice_button_save))
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header showing ingredient count
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = context.getString(R.string.ingredient_review_count, ingredientList.size),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            // Ingredient list
            if (ingredientList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.voice_no_ingredients),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = ingredientList,
                        key = { it.parsedName + it.displayName }
                    ) { item ->
                        VoiceIngredientCard(
                            item = item,
                            onQuantityChanged = { newQuantity ->
                                ingredientList = ingredientList.map {
                                    if (it == item) it.copy(quantity = newQuantity) else it
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a single voice ingredient with quantity counter.
 */
@Composable
private fun VoiceIngredientCard(
    item: VoiceIngredientItem,
    onQuantityChanged: (Int) -> Unit
) {
    val haptic = rememberHapticFeedback()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Name and unit
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    
                    // Warning icon for unrecognized ingredients
                    if (!item.isRecognized) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Unrecognized",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Text(
                    text = item.defaultUnit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Right side: Quantity counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Minus button
                Surface(
                    shape = CircleShape,
                    color = if (item.quantity > 0) 
                        MaterialTheme.colorScheme.secondaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable(enabled = item.quantity > 0) {
                            onQuantityChanged((item.quantity - 1).coerceAtLeast(0))
                            haptic.performLight()
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (item.quantity > 0)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
                
                // Quantity display
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.width(48.dp).height(36.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                
                // Plus button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable {
                            onQuantityChanged(item.quantity + 1)
                            haptic.performLight()
                        }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
