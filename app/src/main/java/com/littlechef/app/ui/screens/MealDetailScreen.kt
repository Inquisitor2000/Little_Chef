package com.littlechef.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.R
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealDetailScreen(
    mealId: String,
    viewModel: MealsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val meal = remember(uiState) {
        if (uiState is MealsUiState.Success) {
            (uiState as MealsUiState.Success).meals.find { it.id == mealId }
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meal?.name ?: "Recipe Details", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(mealId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        if (meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Meal info
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = meal.name,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            if (meal.servings != null || meal.prepTimeMinutes != null || meal.cookTimeMinutes != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    meal.servings?.let {
                                        Text(
                                            text = "${androidx.compose.ui.res.stringResource(R.string.meal_plan_servings)}: $it",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    meal.prepTimeMinutes?.let {
                                        Text(
                                            text = "${androidx.compose.ui.res.stringResource(R.string.meal_plan_prep)}: $it ${androidx.compose.ui.res.stringResource(R.string.meal_plan_min)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    meal.cookTimeMinutes?.let {
                                        Text(
                                            text = "${androidx.compose.ui.res.stringResource(R.string.meal_plan_cook)}: $it ${androidx.compose.ui.res.stringResource(R.string.meal_plan_min)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                            
                            meal.instructions?.let {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Instructions",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                
                // Category Badges
                item {
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Source badge
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (meal.isScraped) {
                                        androidx.compose.ui.res.stringResource(R.string.recipe_type_scraped)
                                    } else {
                                        androidx.compose.ui.res.stringResource(R.string.recipe_type_manual)
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                                )
                            }
                        }
                        
                        // Meal Type badge
                        meal.mealType?.let { mealType ->
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = mealType.emoji,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = mealType.getLocalizedName(context),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                }
                            }
                        }
                        
                        // Dish Category badge
                        meal.dishCategory?.let { category ->
                            Surface(
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = category.emoji,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = category.getLocalizedName(context),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                // Allergen warnings
                item {
                    val allergens = meal.ingredients.flatMap { it.ingredient.allergens }.distinctBy { it.id }
                    if (allergens.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⚠ Allergen Warning",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Contains: ${allergens.joinToString { it.name }}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                // Ingredients
                item {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                items(meal.ingredients) { mealIngredient ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mealIngredient.ingredient.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                if (mealIngredient.ingredient.allergens.isNotEmpty()) {
                                    Text(
                                        text = "Allergens: ${mealIngredient.ingredient.allergens.joinToString { it.name }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Text(
                                text = translateFormattedUnit(
                                    UnitConversion.formatForDisplay(
                                        mealIngredient.quantity,
                                        mealIngredient.ingredient.unit
                                    )
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Recipe", style = MaterialTheme.typography.titleMedium) },
                text = { Text("Are you sure you want to delete this recipe?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            meal?.let { viewModel.deleteMeal(it) }
                            showDeleteDialog = false
                            onNavigateBack()
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
