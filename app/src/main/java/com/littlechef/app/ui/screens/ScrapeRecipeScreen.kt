package com.littlechef.app.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import com.littlechef.app.ui.components.CupertinoPicker
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.data.remote.ScrapedIngredient
import com.littlechef.app.data.remote.ScrapedRecipe
import com.littlechef.app.domain.model.CatalogIngredient
import com.littlechef.app.domain.model.Ingredient
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.domain.model.UnitOptions
import com.littlechef.app.ui.util.rememberHapticFeedback
import com.littlechef.app.R

import kotlinx.coroutines.launch

@Composable
private fun translateMealType(mealType: com.littlechef.app.domain.model.MealType): String {
    return when (mealType) {
        com.littlechef.app.domain.model.MealType.BREAKFAST -> stringResource(R.string.meal_type_breakfast)
        com.littlechef.app.domain.model.MealType.LUNCH -> stringResource(R.string.meal_type_lunch)
        com.littlechef.app.domain.model.MealType.DINNER -> stringResource(R.string.meal_type_dinner)
        com.littlechef.app.domain.model.MealType.SNACK -> stringResource(R.string.meal_type_snack)
        com.littlechef.app.domain.model.MealType.DESSERT -> stringResource(R.string.meal_type_dessert)
    }
}

@Composable
private fun translateDishCategory(category: com.littlechef.app.domain.model.DishCategory): String {
    return when (category) {
        com.littlechef.app.domain.model.DishCategory.PASTA -> stringResource(R.string.dish_category_pasta)
        com.littlechef.app.domain.model.DishCategory.SALAD -> stringResource(R.string.dish_category_salad)
        com.littlechef.app.domain.model.DishCategory.SOUP -> stringResource(R.string.dish_category_soup)
        com.littlechef.app.domain.model.DishCategory.MAIN_COURSE -> stringResource(R.string.dish_category_main_course)
        com.littlechef.app.domain.model.DishCategory.APPETIZER -> stringResource(R.string.dish_category_appetizer)
        com.littlechef.app.domain.model.DishCategory.SIDE_DISH -> stringResource(R.string.dish_category_side_dish)
        com.littlechef.app.domain.model.DishCategory.BREAD -> stringResource(R.string.dish_category_bread)
        com.littlechef.app.domain.model.DishCategory.SEAFOOD -> stringResource(R.string.dish_category_seafood)
        com.littlechef.app.domain.model.DishCategory.CHICKEN -> stringResource(R.string.dish_category_chicken)
        com.littlechef.app.domain.model.DishCategory.BEEF -> stringResource(R.string.dish_category_beef)
        com.littlechef.app.domain.model.DishCategory.PORK -> stringResource(R.string.dish_category_pork)
        com.littlechef.app.domain.model.DishCategory.VEGETARIAN -> stringResource(R.string.dish_category_vegetarian)
        com.littlechef.app.domain.model.DishCategory.RICE_BOWL -> stringResource(R.string.dish_category_rice_bowl)
        com.littlechef.app.domain.model.DishCategory.SANDWICH -> stringResource(R.string.dish_category_sandwich)
        com.littlechef.app.domain.model.DishCategory.PIZZA -> stringResource(R.string.dish_category_pizza)
        com.littlechef.app.domain.model.DishCategory.DESSERT -> stringResource(R.string.dish_category_dessert)
        com.littlechef.app.domain.model.DishCategory.BEVERAGE -> stringResource(R.string.dish_category_beverage)
        com.littlechef.app.domain.model.DishCategory.BAKED_DISH -> stringResource(R.string.dish_category_baked_dish)
    }
}

@Composable
private fun translateUnit(unit: String): String {
    return when (unit.lowercase()) {
        "g" -> stringResource(R.string.unit_g)
        "kg" -> stringResource(R.string.unit_kg)
        "ml" -> stringResource(R.string.unit_ml)
        "l" -> stringResource(R.string.unit_l)
        "pcs", "piece" -> stringResource(R.string.unit_piece)
        else -> unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrapeRecipeScreen(
    onNavigateBack: () -> Unit,
    onRecipeSaved: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToManualRecipe: () -> Unit,
    viewModel: ScrapeRecipeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // For scraped recipes, only allow metric units (ml, L, g, kg) and pieces (pcs)
    val validUnits = remember { listOf("ml", "L", "g", "kg", "pcs") }
    
    var urlInput by remember { mutableStateOf("") }

    // Image picker for adding dish photo to recipe
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_recipe_title), style = MaterialTheme.typography.headlineSmall) },
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
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when (val state = uiState) {
                    is ScrapeRecipeUiState.Initial -> {
                        InitialContent(
                            urlInput = urlInput,
                            onUrlChange = { urlInput = it },
                            onScrapeUrl = {
                                if (urlInput.isNotBlank()) {
                                    viewModel.scrapeFromUrl(urlInput)
                                }
                            },
                            onOpenManualEntry = onNavigateToManualRecipe
                        )
                    }
                    is ScrapeRecipeUiState.Loading -> {
                        LoadingContent()
                    }
                    is ScrapeRecipeUiState.Success -> {
                        RecipePreviewContent(
                            recipe = state.recipe,
                            editedName = state.editedName,
                            editedIngredients = state.editedIngredients,
                            mealType = state.mealType,
                            dishCategory = state.dishCategory,
                            dishImage = state.dishImage,
                            useDetailedInstructions = state.useDetailedInstructions,
                            validUnits = validUnits,
                            onNameChange = { viewModel.updateName(it) },
                            onMealTypeChange = { viewModel.updateMealType(it) },
                            onDishCategoryChange = { viewModel.updateDishCategory(it) },
                            onIngredientChange = { index, ingredient -> 
                                viewModel.updateIngredient(index, ingredient) 
                            },
                            onPickDishImage = { dishImagePicker.launch("image/*") },
                            onClearDishImage = { viewModel.clearDishImage() },
                            onInstructionModeChange = { viewModel.setDetailedInstructions(it) },
                            onSave = { viewModel.saveRecipe() },
                            onRetry = { viewModel.retry() }
                        )
                    }
                    is ScrapeRecipeUiState.Saved -> {
                        LaunchedEffect(Unit) {
                            onRecipeSaved()
                        }
                    }
                    is ScrapeRecipeUiState.Error -> {
                        ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.retry() },
                            onNavigateToSettings = onNavigateToSettings,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun InitialContent(
    urlInput: String,
    onUrlChange: (String) -> Unit,
    onScrapeUrl: () -> Unit,
    onOpenManualEntry: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.add_recipe_url_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.add_recipe_url_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = onUrlChange,
                    label = { Text(stringResource(R.string.add_recipe_url_label)) },
                    placeholder = { Text(stringResource(R.string.add_recipe_url_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (focusState.isFocused) {
                                keyboardController?.hide()
                            }
                        },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    trailingIcon = {
                        if (isFocused && urlInput.isBlank()) {
                            TextButton(
                                onClick = {
                                    clipboardManager.getText()?.text?.let { pastedText ->
                                        onUrlChange(pastedText)
                                    }
                                    focusManager.clearFocus()
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.add_recipe_paste),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
                
                Button(
                    onClick = onScrapeUrl,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = urlInput.isNotBlank()
                ) {
                    Text(stringResource(R.string.add_recipe_import))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.add_recipe_or),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onOpenManualEntry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add_recipe_manual))
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.add_recipe_extracting),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.add_recipe_wait),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipePreviewContent(
    recipe: ScrapedRecipe,
    editedName: String,
    editedIngredients: List<ScrapedIngredient>,
    mealType: com.littlechef.app.domain.model.MealType?,
    dishCategory: com.littlechef.app.domain.model.DishCategory?,
    dishImage: Bitmap?,
    useDetailedInstructions: Boolean,
    validUnits: List<String>,
    onNameChange: (String) -> Unit,
    onMealTypeChange: (com.littlechef.app.domain.model.MealType?) -> Unit,
    onDishCategoryChange: (com.littlechef.app.domain.model.DishCategory?) -> Unit,
    onIngredientChange: (Int, ScrapedIngredient) -> Unit,
    onPickDishImage: () -> Unit,
    onClearDishImage: () -> Unit,
    onInstructionModeChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit
) {
    var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }
    
    // Helper function to check if ingredient is an egg
    fun isEgg(ingredientName: String): Boolean {
        val lowerName = ingredientName.lowercase()
        return lowerName.contains("egg") || lowerName.contains("eggs")
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = editedName,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.add_recipe_name_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Meal Type and Dish Category Selection
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
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                    }
                },
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
                    selectedMealType = mealType,
                    onMealTypeSelected = onMealTypeChange,
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
                    selectedDishCategory = dishCategory,
                    onDishCategorySelected = onDishCategoryChange,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Dish image section
        DishImageSection(
            dishImage = dishImage,
            onPickImage = onPickDishImage,
            onClearImage = onClearDishImage
        )
        
        // Recipe details card
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val items = mutableListOf<@Composable () -> Unit>()
                
                recipe.prepTimeMinutes?.let {
                    items.add { InfoColumn(value = "$it ${stringResource(R.string.add_recipe_min)}", label = stringResource(R.string.add_recipe_prep)) }
                }
                recipe.cookTimeMinutes?.let {
                    items.add { InfoColumn(value = "$it ${stringResource(R.string.add_recipe_min)}", label = stringResource(R.string.add_recipe_cook)) }
                }
                recipe.servings?.let {
                    items.add { InfoColumn(value = "$it", label = stringResource(R.string.add_recipe_servings)) }
                }
                
                items.forEachIndexed { index, item ->
                    item()
                    if (index < items.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }
            }
        }
        
        // Ingredients with edit capability
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.add_recipe_ingredients) + " (${editedIngredients.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.add_recipe_tap_to_edit),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                editedIngredients.forEachIndexed { index, ingredient ->
                    val hasInvalidUnit = ingredient.unit !in validUnits
                    val isPcsButNotEgg = ingredient.unit == "pcs" && !isEgg(ingredient.name)
                    val needsAttention = hasInvalidUnit || isPcsButNotEgg
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingIngredientIndex = index }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Star toggle button
                            IconButton(
                                onClick = { 
                                    onIngredientChange(
                                        index, 
                                        ingredient.copy(isStarIngredient = !ingredient.isStarIngredient)
                                    )
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (ingredient.isStarIngredient) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = if (ingredient.isStarIngredient) "Essential ingredient" else "Optional ingredient",
                                    tint = if (ingredient.isStarIngredient) {
                                        Color(0xFFFFD700)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Text(
                                text = ingredient.name.replaceFirstChar { 
                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${formatQuantity(ingredient.quantity)} ${translateUnit(ingredient.unit)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (needsAttention) MaterialTheme.colorScheme.error 
                                       else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Check for invalid units or pcs that are not eggs
        val hasInvalidUnits = editedIngredients.any { it.unit !in validUnits }
        val hasPcsNonEggs = editedIngredients.any { it.unit == "pcs" && !isEgg(it.name) }
        
        if (hasInvalidUnits) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.add_recipe_invalid_units),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        if (hasPcsNonEggs && !hasInvalidUnits) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = stringResource(R.string.add_recipe_pcs_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Instructions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.add_recipe_instructions),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onInstructionModeChange(true) },
                    colors = if (useDetailedInstructions) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
                    border = if (!useDetailedInstructions) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    } else null,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.add_recipe_detailed), style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = { onInstructionModeChange(false) },
                    colors = if (!useDetailedInstructions) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
                    border = if (useDetailedInstructions) {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    } else null,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(stringResource(R.string.add_recipe_simple), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val instructionsText = if (useDetailedInstructions) {
                    recipe.instructions
                } else {
                    recipe.simpleInstructions ?: recipe.instructions
                }
                val steps = instructionsText
                    .split(Regex("\n\n+"))
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                
                steps.forEachIndexed { index, step ->
                    Text(
                        text = step,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = if (index < steps.size - 1) 12.dp else 0.dp)
                    )
                }
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(stringResource(R.string.add_recipe_try_again))
            }
            Button(
                onClick = onSave,
                modifier = Modifier.weight(1f),
                enabled = editedName.isNotBlank() && !hasInvalidUnits
            ) {
                Text(stringResource(R.string.add_recipe_save))
            }
        }
    }
    
    // Edit ingredient dialog
    editingIngredientIndex?.let { index ->
        val ingredient = editedIngredients[index]
        EditIngredientDialog(
            ingredient = ingredient,
            validUnits = validUnits,
            onDismiss = { editingIngredientIndex = null },
            onSave = { updated ->
                onIngredientChange(index, updated)
                editingIngredientIndex = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishImageSection(
    dishImage: Bitmap?,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.add_recipe_dish_photo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (dishImage != null) {
                Button(
                    onClick = onClearImage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.add_recipe_remove))
                }
            }
        }
        
        if (dishImage != null) {
            Image(
                bitmap = dishImage.asImageBitmap(),
                contentDescription = "Dish photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPickImage() },
                contentScale = ContentScale.Crop
            )
            Text(
                text = stringResource(R.string.add_recipe_tap_image_to_change),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                onClick = onPickImage,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.add_recipe_add_photo),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditIngredientDialog(
    ingredient: ScrapedIngredient,
    validUnits: List<String>,
    onDismiss: () -> Unit,
    onSave: (ScrapedIngredient) -> Unit
) {
    var name by remember { mutableStateOf(ingredient.name) }
    var quantity by remember { mutableStateOf(formatQuantity(ingredient.quantity)) }
    var selectedUnit by remember { mutableStateOf(ingredient.unit) }
    val haptic = rememberHapticFeedback()
    
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
        title = { Text(stringResource(R.string.edit_ingredient_title), style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.add_recipe_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
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
                                contentDescription = stringResource(R.string.button_add),
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
                                            style = MaterialTheme.typography.bodyLarge,
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
                            val qty = quantity.toDoubleOrNull() ?: ingredient.quantity
                            onSave(ScrapedIngredient(name = name, quantity = qty, unit = selectedUnit))
                        },
                        modifier = Modifier.weight(1f),
                        enabled = name.isNotBlank() && quantity.toDoubleOrNull() != null
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

@Composable
internal fun MealTypePicker(
    selectedMealType: com.littlechef.app.domain.model.MealType?,
    onMealTypeSelected: (com.littlechef.app.domain.model.MealType?) -> Unit,
    modifier: Modifier = Modifier,
    includeNoneOption: Boolean = true
) {
    val items = if (includeNoneOption) {
        listOf<com.littlechef.app.domain.model.MealType?>(null) + com.littlechef.app.domain.model.MealType.entries
    } else {
        com.littlechef.app.domain.model.MealType.entries.toList()
    }

    CupertinoPicker(
        items = items,
        selectedItem = selectedMealType,
        onItemSelected = { onMealTypeSelected(it) },
        itemLabel = { item ->
            if (item == null) {
                stringResource(R.string.add_recipe_none)
            } else {
                "${item.emoji} ${translateMealType(item)}"
            }
        },
        modifier = modifier
    )
}

@Composable
internal fun DishCategoryPicker(
    selectedDishCategory: com.littlechef.app.domain.model.DishCategory?,
    onDishCategorySelected: (com.littlechef.app.domain.model.DishCategory?) -> Unit,
    modifier: Modifier = Modifier,
    includeNoneOption: Boolean = true
) {
    val items = if (includeNoneOption) {
        listOf<com.littlechef.app.domain.model.DishCategory?>(null) + com.littlechef.app.domain.model.DishCategory.entries
    } else {
        com.littlechef.app.domain.model.DishCategory.entries.toList()
    }

    CupertinoPicker(
        items = items,
        selectedItem = selectedDishCategory,
        onItemSelected = { onDishCategorySelected(it) },
        itemLabel = { item ->
            if (item == null) {
                stringResource(R.string.add_recipe_none)
            } else {
                "${item.emoji} ${translateDishCategory(item)}"
            }
        },
        modifier = modifier
    )
}

@Composable
private fun InfoColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    message: String, 
    onRetry: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: ScrapeRecipeViewModel
) {
    // Check if error is about missing API key
    val isApiKeyError = message.contains("API key not configured", ignoreCase = true) ||
                        message.contains("add it in Settings", ignoreCase = true)
    
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Text(
                text = if (isApiKeyError) {
                    stringResource(R.string.add_recipe_api_key_missing)
                } else {
                    stringResource(R.string.add_recipe_error_title)
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = if (isApiKeyError) {
                    stringResource(R.string.add_recipe_api_key_missing_message)
                } else {
                    message
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            if (isApiKeyError) {
                Button(onClick = { 
                    viewModel.markForRetry()
                    onNavigateToSettings()
                }) { 
                    Text(stringResource(R.string.add_recipe_add_api_key)) 
                }
            } else {
                Button(onClick = onRetry) { 
                    Text(stringResource(R.string.add_recipe_try_again)) 
                }
            }
        }
    }
}

private fun formatQuantity(quantity: Double): String {
    return if (quantity == quantity.toLong().toDouble()) {
        quantity.toLong().toString()
    } else {
        String.format("%.1f", quantity)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ManualDishImageSection(
    dishImage: android.graphics.Bitmap?,
    onPickImage: () -> Unit,
    onClearImage: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.add_recipe_dish_photo),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (dishImage != null) {
                Button(
                    onClick = onClearImage,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.add_recipe_remove))
                }
            }
        }
        
        if (dishImage != null) {
            Image(
                bitmap = dishImage.asImageBitmap(),
                contentDescription = "Dish photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onPickImage() },
                contentScale = ContentScale.Crop
            )
            Text(
                text = stringResource(R.string.add_recipe_tap_image_to_change),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                onClick = onPickImage,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.add_recipe_add_photo),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CompactIngredientRow(
    ingredient: ManualRecipeIngredient,
    validUnits: List<String>,
    allIngredients: List<CatalogIngredient>,
    onUpdate: (ManualRecipeIngredient) -> Unit,
    onRemove: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showEditDialog = true }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Star toggle button
            IconButton(
                onClick = { 
                    onUpdate(ingredient.copy(isStarIngredient = !ingredient.isStarIngredient))
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (ingredient.isStarIngredient) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (ingredient.isStarIngredient) "Essential ingredient" else "Optional ingredient",
                    tint = if (ingredient.isStarIngredient) {
                        Color(0xFFFFD700)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = ingredient.name.ifBlank { "New ingredient" }.replaceFirstChar { 
                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "${formatQuantity(ingredient.quantity)} ${ingredient.unit}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    if (showEditDialog) {
        EditManualIngredientDialog(
            ingredient = ingredient,
            validUnits = validUnits,
            allIngredients = allIngredients,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onUpdate(updated)
                showEditDialog = false
            },
            onRemove = {
                onRemove()
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditManualIngredientDialog(
    ingredient: ManualRecipeIngredient,
    validUnits: List<String>,
    allIngredients: List<CatalogIngredient>,
    onDismiss: () -> Unit,
    onSave: (ManualRecipeIngredient) -> Unit,
    onRemove: () -> Unit
) {
    var name by remember { mutableStateOf(ingredient.name) }
    var quantity by remember { mutableStateOf(if (ingredient.quantity == 0.0) "" else ingredient.quantity.toString()) }
    var selectedUnit by remember { mutableStateOf(ingredient.unit) }
    var isStarIngredient by remember { mutableStateOf(ingredient.isStarIngredient) }
    var expanded by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }
    var manuallyDismissed by remember { mutableStateOf(false) }
    
    // Filter ingredients based on input - case insensitive and partial match
    val suggestions = remember(name, allIngredients) {
        if (name.length < 2) {
            emptyList()
        } else {
            allIngredients
                .filter { it.nameKey.contains(name, ignoreCase = true) }
                .sortedBy { 
                    // Prioritize exact matches and starts-with matches
                    when {
                        it.nameKey.equals(name, ignoreCase = true) -> 0
                        it.nameKey.startsWith(name, ignoreCase = true) -> 1
                        else -> 2
                    }
                }
        }
    }
    
    // Check if current name matches an existing ingredient
    val matchingCatalogIngredient = remember(name, allIngredients) {
        allIngredients.find { it.nameKey.equals(name, ignoreCase = true) }
    }
    
    val isExistingIngredient = matchingCatalogIngredient != null
    
    // Filter units based on whether ingredient is in catalog
    val availableUnits = remember(matchingCatalogIngredient, validUnits) {
        if (matchingCatalogIngredient != null) {
            // For catalog ingredients, show default unit plus alternate unit if available
            listOfNotNull(
                matchingCatalogIngredient.defaultUnit,
                matchingCatalogIngredient.alternateUnit
            )
        } else {
            // For custom ingredients, show all units
            validUnits
        }
    }
    
    // Update selected unit if it's not in available units
    LaunchedEffect(availableUnits, selectedUnit) {
        if (selectedUnit !in availableUnits && availableUnits.isNotEmpty()) {
            selectedUnit = availableUnits.first()
        }
    }
    
    // Auto-show suggestions when typing (but respect manual dismissal)
    LaunchedEffect(name, suggestions) {
        if (name.length >= 2 && suggestions.isNotEmpty() && !manuallyDismissed) {
            showSuggestions = true
        } else if (name.length < 2 || suggestions.isEmpty()) {
            showSuggestions = false
            manuallyDismissed = false
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Edit Ingredient", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = onRemove,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove")
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Name field with autocomplete
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            manuallyDismissed = false // Reset when user types
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        supportingText = {
                            if (name.isNotBlank() && !isExistingIngredient && !showSuggestions) {
                                Text(
                                    text = "✨ New ingredient - will be added to your database",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                    
                    // Suggestions as horizontal chips - animated expansion
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showSuggestions && suggestions.isNotEmpty(),
                        enter = androidx.compose.animation.expandVertically(
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        ) + androidx.compose.animation.fadeIn(
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                        ),
                        exit = androidx.compose.animation.shrinkVertically(
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150, easing = androidx.compose.animation.core.FastOutSlowInEasing)
                        ) + androidx.compose.animation.fadeOut(
                            animationSpec = androidx.compose.animation.core.tween(durationMillis = 150)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            suggestions.forEach { catalogIngredient ->
                                SuggestionChip(
                                    onClick = {
                                        showSuggestions = false
                                        manuallyDismissed = true
                                        name = catalogIngredient.nameKey
                                        selectedUnit = catalogIngredient.defaultUnit
                                    },
                                    label = { 
                                        Text(
                                            catalogIngredient.nameKey.replaceFirstChar { 
                                                if (it.isLowerCase()) it.titlecase() else it.toString() 
                                            }
                                        ) 
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Star ingredient toggle - compact version
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isStarIngredient = !isStarIngredient }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isStarIngredient) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (isStarIngredient) "Essential ingredient" else "Optional ingredient",
                            tint = if (isStarIngredient) {
                                Color(0xFFFFD700)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = stringResource(R.string.add_recipe_mark_as_essential),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Switch(
                        checked = isStarIngredient,
                        onCheckedChange = { isStarIngredient = it }
                    )
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                // Unit selector - inline buttons for catalog ingredients, dropdown for custom
                if (isExistingIngredient && availableUnits.size <= 2) {
                    // Inline button grid for catalog ingredients (1-2 units)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.add_recipe_unit),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableUnits.forEach { unit ->
                                Button(
                                    onClick = { selectedUnit = unit },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = if (selectedUnit == unit) {
                                        ButtonDefaults.buttonColors()
                                    } else {
                                        ButtonDefaults.outlinedButtonColors()
                                    },
                                    border = if (selectedUnit != unit) {
                                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    } else null
                                ) {
                                    Text(
                                        text = unit,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Dropdown for custom ingredients (all units)
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedUnit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            // Display units in a 2-column grid
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                availableUnits.chunked(2).forEach { rowUnits ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowUnits.forEach { unit ->
                                            Button(
                                                onClick = {
                                                    selectedUnit = unit
                                                    expanded = false
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = unit,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                        // Add spacer if odd number of units in last row
                                        if (rowUnits.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 0.0
                    onSave(ManualRecipeIngredient(
                        name = name, 
                        quantity = qty, 
                        unit = selectedUnit,
                        isStarIngredient = isStarIngredient
                    ))
                },
                enabled = name.isNotBlank() && quantity.toDoubleOrNull() != null && selectedUnit in availableUnits,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        },
        dismissButton = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    mealId: String,
    onNavigateBack: () -> Unit,
    viewModel: ManualRecipeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val allIngredients by viewModel.allIngredients.collectAsState()
    val context = LocalContext.current
    
    val validUnits = remember { UnitConversion.getAllUnits() }
    
    // State for dialogs
    var showAddIngredientDialog by remember { mutableStateOf(false) }
    var showEditIngredientDialog by remember { mutableStateOf(false) }
    var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }
    
    // Load meal data for editing
    LaunchedEffect(mealId) {
        viewModel.loadMealForEditing(mealId)
    }
    
    // Handle saved state
    LaunchedEffect(uiState) {
        if (uiState is ManualRecipeUiState.Saved) {
            onNavigateBack()
        }
    }
    
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
                title = { Text(stringResource(R.string.edit_ingredient_title), style = MaterialTheme.typography.headlineSmall) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
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
            
            // Meal Type and Dish Category Selection
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
                modifier = Modifier.fillMaxWidth(),
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
                        translateIngredient = { it }, // No translation needed for custom recipes
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
            
            // Add ingredient button
            Button(
                onClick = { showAddIngredientDialog = true },
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
            
            // Show add ingredient dialog
            if (showAddIngredientDialog) {
                EditManualIngredientDialog(
                    ingredient = ManualRecipeIngredient(),
                    validUnits = validUnits,
                    allIngredients = allIngredients,
                    onDismiss = { showAddIngredientDialog = false },
                    onSave = { newIngredient ->
                        viewModel.addIngredient()
                        viewModel.updateIngredient(state.ingredients.size, newIngredient)
                        showAddIngredientDialog = false
                    },
                    onRemove = {
                        showAddIngredientDialog = false
                    }
                )
            }
            
            // Show edit ingredient dialog
            if (showEditIngredientDialog && editingIngredientIndex != null) {
                EditManualIngredientDialog(
                    ingredient = state.ingredients[editingIngredientIndex!!],
                    validUnits = validUnits,
                    allIngredients = allIngredients,
                    onDismiss = { 
                        showEditIngredientDialog = false
                        editingIngredientIndex = null
                    },
                    onSave = { updatedIngredient ->
                        viewModel.updateIngredient(editingIngredientIndex!!, updatedIngredient)
                        showEditIngredientDialog = false
                        editingIngredientIndex = null
                    },
                    onRemove = {
                        viewModel.removeIngredient(editingIngredientIndex!!)
                        showEditIngredientDialog = false
                        editingIngredientIndex = null
                    }
                )
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
        
        // Show add ingredient dialog
        if (showAddIngredientDialog) {
            EditManualIngredientDialog(
                ingredient = ManualRecipeIngredient(),
                validUnits = validUnits,
                allIngredients = allIngredients,
                onDismiss = { showAddIngredientDialog = false },
                onSave = { newIngredient ->
                    viewModel.addIngredient()
                    viewModel.updateIngredient(state.ingredients.size, newIngredient)
                    showAddIngredientDialog = false
                },
                onRemove = {
                    showAddIngredientDialog = false
                }
            )
        }
        
        // Show edit ingredient dialog
        if (showEditIngredientDialog && editingIngredientIndex != null) {
            EditManualIngredientDialog(
                ingredient = state.ingredients[editingIngredientIndex!!],
                validUnits = validUnits,
                allIngredients = allIngredients,
                onDismiss = { 
                    showEditIngredientDialog = false
                    editingIngredientIndex = null
                },
                onSave = { updatedIngredient ->
                    viewModel.updateIngredient(editingIngredientIndex!!, updatedIngredient)
                    showEditIngredientDialog = false
                    editingIngredientIndex = null
                },
                onRemove = {
                    viewModel.removeIngredient(editingIngredientIndex!!)
                    showEditIngredientDialog = false
                    editingIngredientIndex = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServingsSelector(
    servings: String,
    onServingsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use OutlinedTextField as a base but make it read-only and clickable
    OutlinedTextField(
        value = servings.ifBlank { "2" },
        onValueChange = {},
        label = { Text("Servings") },
        readOnly = true,
        modifier = modifier
            .height(30.dp)
            .clickable {
                val currentServings = servings.toIntOrNull() ?: 2
                val nextServings = when (currentServings) {
                    1 -> 2
                    2 -> 3
                    3 -> 4
                    4 -> 5
                    5 -> 6
                    else -> 1
                }
                onServingsChange(nextServings.toString())
            },
        singleLine = true,
        shape = RoundedCornerShape(2.dp),
        colors = OutlinedTextFieldDefaults.colors()
    )
}

