package com.littlechef.app.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.littlechef.app.R
import com.littlechef.app.data.local.BundledIngredient
import com.littlechef.app.data.local.BundledRecipe
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.domain.model.roundEggQuantity
import com.littlechef.app.domain.usecase.CheckRecipeIngredientsUseCase
import com.littlechef.app.ui.components.formatNutritionValue
import com.littlechef.app.domain.model.NutritionInfo
import com.littlechef.app.ui.util.NutritionCalculator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BundledRecipeDetailScreen(
    cuisine: Cuisine,
    recipeId: String,
    onNavigateBack: () -> Unit,
    onNavigateToPlan: () -> Unit = {},
    onNavigateToGroceries: () -> Unit = {},
    viewModel: BundledRecipeDetailViewModel = hiltViewModel()
) {
    val recipe by viewModel.recipe.collectAsState()
    val allergens by viewModel.allergens.collectAsState()
    val useDetailedInstructions by viewModel.useDetailedInstructions.collectAsState()
    val showPlanDialog by viewModel.showPlanDialog.collectAsState()
    val planResult by viewModel.planResult.collectAsState()
    val showMadeMealDialog by viewModel.showMadeMealDialog.collectAsState()
    val madeMealResult by viewModel.madeMealResult.collectAsState()
    val hasAllIngredients by viewModel.hasAllIngredients.collectAsState()
    val selectedServings by viewModel.selectedServings.collectAsState()
    val ingredientSubstitutions by viewModel.ingredientSubstitutions.collectAsState()
    
    // Calculate multiplier reactively based on selectedServings
    val servingsMultiplier = remember(selectedServings, recipe) {
        recipe?.let { selectedServings.toDouble() / it.servings.toDouble() } ?: 1.0
    }
    
    LaunchedEffect(cuisine, recipeId) {
        viewModel.loadRecipe(cuisine, recipeId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = recipe?.name ?: "Recipe",
                        style = if ((recipe?.name?.length ?: 0) > 30) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.headlineSmall
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        recipe?.let { r ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image
                r.imageUrl?.let { url ->
                    BundledRecipeImage(
                        imagePath = url,
                        contentDescription = r.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    )
                }
                
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recipe info card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            val selectedServings by viewModel.selectedServings.collectAsState()

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val items = mutableListOf<@Composable () -> Unit>()
                            
                            // Calculate time adjustments based on servings multiplier
                            // Prep scales ~35% per doubling; cook scales ~5% (method-dependent)
                            val basePrepTime = r.prepTimeMinutes ?: 0
                            val baseCookTime = r.cookTimeMinutes ?: 0
                            val baseRecipeServings = r.servings
                            
                            val prepTimeAdjustment = if (basePrepTime > 0 && baseRecipeServings > 0) {
                                val ratio = selectedServings.toDouble() / baseRecipeServings.toDouble()
                                (basePrepTime * (ratio - 1.0) * 0.35).toInt().coerceAtLeast(0)
                            } else 0
                            
                            val cookTimeAdjustment = if (baseCookTime > 0 && baseRecipeServings > 0) {
                                val ratio = selectedServings.toDouble() / baseRecipeServings.toDouble()
                                (baseCookTime * (ratio - 1.0) * 0.05).toInt().coerceAtLeast(0)
                            } else 0
                            
                            r.prepTimeMinutes?.let { basePrepTime ->
                                val adjustedPrepTime = basePrepTime + prepTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedPrepTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_prep)) }
                            }
                            r.cookTimeMinutes?.let { cookTime ->
                                val adjustedCookTime = cookTime + cookTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedCookTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_cook)) }
                            }
                            items.add { 
                                InfoColumn(
                                    value = "$selectedServings", 
                                    label = stringResource(R.string.recipe_servings),
                                    clickable = true,
                                    onClick = { viewModel.cycleServings() }
                                ) 
                            }
                            
                            // Calculate total time dynamically with adjustments
                            val baseTotalTime = (r.prepTimeMinutes ?: 0) + (r.cookTimeMinutes ?: 0)
                            if (baseTotalTime > 0) {
                                val adjustedTotalTime = baseTotalTime + prepTimeAdjustment + cookTimeAdjustment
                                items.add { InfoColumn(value = "$adjustedTotalTime ${stringResource(R.string.recipe_min)}", label = stringResource(R.string.recipe_total)) }
                            }
                            
                            items.forEachIndexed { index, item ->
                                item()
                                if (index < items.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(40.dp)
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                    )
                                }
                            }
                        }

                        // Nutrition info per serving
                        val originalServings = r.servings
                        val nutritionInfo = remember(r.ingredients, selectedServings, originalServings) {
                            val portions = r.ingredients.map { ingredient ->
                                NutritionCalculator.IngredientPortion(
                                    name = ingredient.name,
                                    quantity = ingredient.quantity,
                                    unit = ingredient.unit ?: "g"
                                )
                            }
                            NutritionCalculator.calculate(portions, originalServings, viewModel.nutritionLoader)
                        }
                        if (nutritionInfo != NutritionInfo.EMPTY) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InfoColumn(
                                    value = formatNutritionValue(nutritionInfo.calories),
                                    label = stringResource(R.string.nutrition_calories_short)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                InfoColumn(
                                    value = formatNutritionValue(nutritionInfo.fatsG),
                                    label = stringResource(R.string.nutrition_fats_short)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                InfoColumn(
                                    value = formatNutritionValue(nutritionInfo.carbsG),
                                    label = stringResource(R.string.nutrition_carbs_short)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                InfoColumn(
                                    value = formatNutritionValue(nutritionInfo.proteinG),
                                    label = stringResource(R.string.nutrition_protein_short)
                                )
                            }
                        }
                        }
                    }

                    // Plan it and conditional button (Buy ingredients / Let's cook)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showPlanDialog() },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(stringResource(R.string.recipe_plan_it))
                        }
                        
                        if (hasAllIngredients) {
                            Button(
                                onClick = { viewModel.startCooking() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_lets_cook))
                            }
                        } else {
                            Button(
                                onClick = { viewModel.buyNecessaryIngredients() },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_buy_ingredients))
                            }
                        }
                    }

                    // Ingredients section with allergens
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.recipe_ingredients),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        // Allergen chips - display all with wrapping
                        if (allergens.isNotEmpty()) {
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.weight(1f).wrapContentHeight(),
                                maxItemsInEachRow = Int.MAX_VALUE
                            ) {
                                allergens.forEach { allergen ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = getAllergenTranslation(allergen.name),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            r.ingredients.sortedByDescending { it.isStarIngredient }.forEach { ingredient ->
                                val adjustedQuantity = roundEggQuantity(ingredient.quantity * servingsMultiplier, ingredient.name)
                                val substituteIngredient = ingredientSubstitutions[ingredient.name]
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (ingredient.isStarIngredient) {
                                                Icon(
                                                    imageVector = androidx.compose.material.icons.Icons.Filled.Star,
                                                    contentDescription = "Essential ingredient",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            // Translate ingredient name for display
                                            val translatedName = viewModel.translateIngredientName(ingredient.name)
                                            Text(
                                                text = translatedName.replaceFirstChar { 
                                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                textDecoration = if (substituteIngredient != null) TextDecoration.LineThrough else null,
                                                color = if (substituteIngredient != null) 
                                                    MaterialTheme.colorScheme.onSurfaceVariant 
                                                else 
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        
                                        // Show substitute if available
                                        if (substituteIngredient != null) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "→",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                // Translate substitute name for display
                                                val translatedSubstitute = viewModel.translateIngredientName(substituteIngredient)
                                                Text(
                                                    text = translatedSubstitute.replaceFirstChar { 
                                                        if (it.isLowerCase()) it.titlecase() else it.toString() 
                                                    },
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${formatQuantity(adjustedQuantity)} ${getUnitTranslation(ingredient.unit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (ingredient != r.ingredients.last()) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                                    )
                                }
                            }
                        }
                    }

                    // Instructions section with toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.recipe_instructions),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.setDetailedInstructions(true) },
                                colors = if (useDetailedInstructions) {
                                    ButtonDefaults.buttonColors()
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                },
                                border = if (!useDetailedInstructions) {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                } else null,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_detailed))
                            }
                            Button(
                                onClick = { viewModel.setDetailedInstructions(false) },
                                colors = if (!useDetailedInstructions) {
                                    ButtonDefaults.buttonColors()
                                } else {
                                    ButtonDefaults.outlinedButtonColors()
                                },
                                border = if (useDetailedInstructions) {
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                } else null,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(stringResource(R.string.recipe_simple))
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val instructions = if (useDetailedInstructions) {
                                r.instructions
                            } else {
                                r.simpleInstructions
                            }
                            
                            // Translate ingredient names within instructions
                            val translatedInstructions = viewModel.translateInstructions(instructions)
                            
                            val steps = translatedInstructions
                                .split(Regex("\n\n+"))
                                .map { it.trim() }
                                .filter { it.isNotBlank() }

                            steps.forEachIndexed { index, step ->
                                Text(
                                    text = step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    // Plan dialog
    if (showPlanDialog) {
        PlanMealDialog(
            mealName = recipe?.name ?: "",
            defaultMealType = recipe?.mealType,
            onDismiss = { viewModel.dismissPlanDialog() },
            onPlan = { mealType, date ->
                viewModel.planMeal(mealType, date)
            }
        )
    }

    // Show plan result dialog
    planResult?.let { message ->
        if (message == "ingredients_added_success" || message == "ingredients_added_planned" || message == "meal_planned_no_ingredients") {
            val titleRes = if (message == "ingredients_added_planned" || message == "meal_planned_no_ingredients") {
                R.string.recipe_success_planned
            } else {
                R.string.recipe_success
            }
            
            val bodyTextRes = if (message == "meal_planned_no_ingredients") {
                R.string.recipe_meal_planned
            } else {
                R.string.recipe_ingredients_added
            }
            
            AlertDialog(
                onDismissRequest = { viewModel.dismissPlanDialog() },
                title = {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = stringResource(bodyTextRes),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {},
                dismissButton = {},
                containerColor = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(28.dp)
            )
            
            // Handle navigation and dismissal
            LaunchedEffect(message) {
                // Navigate to appropriate screen in background
                kotlinx.coroutines.delay(1000)
                if (message == "ingredients_added_planned" || message == "meal_planned_no_ingredients") {
                    onNavigateToPlan()
                } else {
                    onNavigateToGroceries()
                }
                
                // Dismiss dialog after showing for 4 seconds total
                kotlinx.coroutines.delay(3000)
                viewModel.dismissPlanDialog()
            }
        } else if (message.startsWith("Error:")) {
            // Show error message
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(3000)
                viewModel.dismissPlanDialog()
            }
        }
    }

    // Made meal confirmation dialog
    if (showMadeMealDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMadeMealDialog() },
            title = { Text(stringResource(R.string.recipe_confirm_meal), style = MaterialTheme.typography.titleMedium) },
            text = { 
                Text(stringResource(R.string.recipe_confirm_made, recipe?.name ?: "", selectedServings))
            },
            confirmButton = {
                Button(onClick = { viewModel.confirmMadeMeal() }) {
                    Text(stringResource(R.string.recipe_yes_made_it))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissMadeMealDialog() }) {
                    Text(stringResource(R.string.recipe_cancel))
                }
            }
        )
    }

    // Show made meal result
    madeMealResult?.let { result ->
        LaunchedEffect(result) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearMadeMealResult()
        }
        
        AlertDialog(
            onDismissRequest = { viewModel.clearMadeMealResult() },
            text = { 
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (result.success) stringResource(R.string.recipe_success) else stringResource(R.string.recipe_notice),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (result.success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = result.message,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = { }
        )
    }
    
    // Missing ingredients dialog
    val showMissingDialog by viewModel.showMissingIngredientsDialog.collectAsState()
    val ingredientCheckResult by viewModel.ingredientCheckResult.collectAsState()
    
    if (showMissingDialog) {
        val missingResult = ingredientCheckResult as? CheckRecipeIngredientsUseCase.Result.MissingIngredients
        
        missingResult?.let { result ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissMissingIngredientsDialog() },
                title = { 
                    Text(
                        stringResource(R.string.recipe_missing_ingredients),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        result.missing.forEach { missing ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    // Translate ingredient name for display
                                    val translatedName = viewModel.translateIngredientName(missing.name)
                                    Text(
                                        text = translatedName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${stringResource(R.string.meal_plan_need)}: ${UnitConversion.formatQuantity(missing.required)} ${getUnitTranslation(missing.unit)}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${stringResource(R.string.meal_plan_have)}: ${UnitConversion.formatQuantity(missing.available)} ${getUnitTranslation(missing.unit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    
                                    // Show substitute if available
                                    missing.substitute?.let { sub ->
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                                        Text(
                                            text = "✓ Substitute available: ${sub.name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { 
                        viewModel.addMissingToGroceries()
                    }) {
                        Text(stringResource(R.string.recipe_add_to_groceries))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissMissingIngredientsDialog() }) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun InfoColumn(
    value: String, 
    label: String,
    clickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (clickable) {
            Modifier.clickable(onClick = onClick)
        } else {
            Modifier
        }
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (clickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatQuantity(quantity: Double): String {
    // Round to avoid floating-point precision issues
    val rounded = kotlin.math.round(quantity * 1000) / 1000.0
    
    return if (rounded == rounded.toLong().toDouble()) {
        rounded.toLong().toString()
    } else {
        // Show 1 decimal place with floor rounding
        val floored = kotlin.math.floor(rounded * 10) / 10
        String.format("%.1f", floored)
    }
}

@Composable
private fun getAllergenTranslation(allergenName: String): String {
    return when (allergenName.lowercase()) {
        "gluten" -> stringResource(R.string.allergen_gluten)
        "dairy" -> stringResource(R.string.allergen_dairy)
        "eggs" -> stringResource(R.string.allergen_eggs)
        "tree nuts" -> stringResource(R.string.allergen_tree_nuts)
        "peanuts" -> stringResource(R.string.allergen_peanuts)
        "soy" -> stringResource(R.string.allergen_soy)
        "fish" -> stringResource(R.string.allergen_fish)
        "shellfish" -> stringResource(R.string.allergen_shellfish)
        "sesame" -> stringResource(R.string.allergen_sesame)
        else -> allergenName
    }
}

@Composable
private fun getUnitTranslation(unit: String): String {
    return when (unit.lowercase()) {
        "g" -> stringResource(R.string.unit_g)
        "ml" -> stringResource(R.string.unit_ml)
        "kg" -> stringResource(R.string.unit_kg)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanMealDialog(
    mealName: String,
    defaultMealType: String?,
    onDismiss: () -> Unit,
    onPlan: (com.littlechef.app.domain.model.MealType, Long) -> Unit
) {
    // Parse the default meal type from string, fallback to DINNER
    val initialMealType = remember(defaultMealType) {
        defaultMealType?.let {
            try {
                com.littlechef.app.domain.model.MealType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                com.littlechef.app.domain.model.MealType.DINNER
            }
        } ?: com.littlechef.app.domain.model.MealType.DINNER
    }
    
    var selectedMealType by remember { mutableStateOf(initialMealType) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.background,
        title = { 
            Text(
                text = mealName, 
                style = MaterialTheme.typography.titleMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            ) 
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.recipe_when_plan),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Meal type selection - Main meals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    listOf(
                        com.littlechef.app.domain.model.MealType.BREAKFAST to R.string.plan_breakfast,
                        com.littlechef.app.domain.model.MealType.LUNCH to R.string.plan_lunch,
                        com.littlechef.app.domain.model.MealType.DINNER to R.string.plan_dinner
                    ).forEach { (mealType, stringRes) ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(stringResource(stringRes)) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // Meal type selection - Snacks and Desserts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    listOf(
                        com.littlechef.app.domain.model.MealType.SNACK to R.string.plan_snack,
                        com.littlechef.app.domain.model.MealType.DESSERT to R.string.plan_dessert
                    ).forEach { (mealType, stringRes) ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(stringResource(stringRes)) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
                
                // Date selection
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatDate(selectedDate))
                }
                
                // Full width buttons stacked vertically
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onPlan(selectedMealType, selectedDate) },
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Text(stringResource(R.string.recipe_plan))
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                }
            }
        },
        confirmButton = { }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 16.dp)
                        .offset(y = (-16).dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showDatePicker = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.recipe_cancel))
                    }
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate = it }
                            showDatePicker = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.recipe_ok))
                    }
                }
            },
            dismissButton = {},
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val calendar = java.util.Calendar.getInstance()
    calendar.timeInMillis = timestamp
    val dayOfWeek = calendar.getDisplayName(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.LONG, java.util.Locale.getDefault())
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val year = calendar.get(java.util.Calendar.YEAR)
    return "$dayOfWeek ${day.toString().padStart(2, '0')}/${month.toString().padStart(2, '0')}/$year"
}

@Composable
private fun BundledRecipeImage(
    imagePath: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(imagePath) {
        try {
            context.assets.open(imagePath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } ?: Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Image not available",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
