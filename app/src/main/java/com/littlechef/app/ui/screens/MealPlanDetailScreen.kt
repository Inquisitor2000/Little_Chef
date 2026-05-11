package com.littlechef.app.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.R
import com.littlechef.app.domain.model.MealPlan
import com.littlechef.app.domain.model.MealPlanStatus
import com.littlechef.app.domain.model.UnitConversion
import com.littlechef.app.domain.model.roundEggQuantity
import com.littlechef.app.domain.usecase.StartCookingUseCase
import com.littlechef.app.ui.components.formatNutritionValue
import com.littlechef.app.domain.model.NutritionInfo
import com.littlechef.app.ui.util.NutritionCalculator
import com.littlechef.app.ui.util.RecipeImage
import com.littlechef.app.ui.util.TimeAdjuster
import com.littlechef.app.ui.util.rememberHapticFeedback
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MealPlanDetailScreen(
    mealPlanId: String,
    viewModel: PlanViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToGroceries: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val startedByUserName by viewModel.startedByUserName.collectAsState()
    val insufficientIngredients by viewModel.insufficientIngredients.collectAsState()
    val useDetailedInstructions by viewModel.useDetailedInstructions.collectAsState()
    var showStartDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }
    var showAbortDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedServings by remember { mutableStateOf(2) }
    var remainingMinutes by remember { mutableStateOf<Int?>(null) }
    val haptic = rememberHapticFeedback()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var shouldScrollToInstructions by remember { mutableStateOf(false) }
    
    // Load meal plan by ID
    LaunchedEffect(mealPlanId) {
        viewModel.loadMealPlanById(mealPlanId)
    }

    val mealPlan = when (val state = uiState) {
        is PlanUiState.Success -> state.mealPlans.find { it.id == mealPlanId }
        else -> null
    }
    
    // Initialize servings from meal plan (use plannedServings if set, otherwise meal's default servings)
    LaunchedEffect(mealPlan) {
        mealPlan?.let { plan ->
            selectedServings = plan.plannedServings ?: plan.meal.servings ?: 2
        }
    }
    
    // Observe ingredient availability for PLANNED meals, reacting to servings changes
    LaunchedEffect(mealPlan?.status, selectedServings) {
        if (mealPlan != null && mealPlan.status == MealPlanStatus.PLANNED) {
            viewModel.observeIngredientAvailabilityWithServings(mealPlanId, selectedServings)
        } else {
            // Clear insufficient ingredients if not in PLANNED status
            viewModel.clearIngredientAvailability()
        }
    }
    
    // Clean up when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearIngredientAvailability()
        }
    }
    
    // Countdown timer for cooking meals
    LaunchedEffect(mealPlan?.status, mealPlan?.startedAt) {
        if (mealPlan?.status == MealPlanStatus.COOKING && mealPlan.startedAt != null) {
            val totalMinutes = (mealPlan.meal.prepTimeMinutes ?: 0) + (mealPlan.meal.cookTimeMinutes ?: 0)
            
            while (true) {
                val elapsedMillis = System.currentTimeMillis() - mealPlan.startedAt
                val elapsedMinutes = (elapsedMillis / 60000).toInt()
                val remaining = totalMinutes - elapsedMinutes
                
                remainingMinutes = if (remaining > 0) remaining else 0
                
                delay(60000) // Update every minute
            }
        } else {
            remainingMinutes = null
        }
    }
    
    // Scroll to instructions when needed
    LaunchedEffect(shouldScrollToInstructions) {
        if (shouldScrollToInstructions && mealPlan != null) {
            delay(300) // Wait for UI to update after status change
            
            // Calculate the index of instructions header
            var targetIndex = 0
            
            // Count items before instructions
            if (!mealPlan.meal.imagePath.isNullOrBlank()) targetIndex++ // Image
            targetIndex++ // Recipe Info Card
            targetIndex++ // Ingredients section header
            targetIndex++ // Ingredients card
            if (insufficientIngredients != null) targetIndex++ // Insufficient ingredients warning
            // Instructions header is next
            
            // Use animateScrollToItem with offset for smooth scroll
            listState.animateScrollToItem(
                index = targetIndex,
                scrollOffset = -30
            )
            
            shouldScrollToInstructions = false
        }
    }
    
    // Calculate servings multiplier
    val servingsMultiplier = mealPlan?.meal?.servings?.let { originalServings ->
        selectedServings.toDouble() / originalServings.toDouble()
    } ?: 1.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = mealPlan?.meal?.name ?: "Meal Plan Details",
                        style = if ((mealPlan?.meal?.name?.length ?: 0) > 25) {
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
        if (mealPlan == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Recipe Image (full width, no padding)
                if (!mealPlan.meal.imagePath.isNullOrBlank()) {
                    item {
                        RecipeImage(
                            imagePath = mealPlan.meal.imagePath,
                            contentDescription = mealPlan.meal.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        )
                    }
                }
                
                // Recipe Info Card (Times, Servings, Status)
                item {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val items = mutableListOf<@Composable () -> Unit>()
                                
                                val prepTimeAdjustment = TimeAdjuster.adjustPrepTime(mealPlan.meal.prepTimeMinutes, mealPlan.meal.servings, selectedServings)
                                val cookTimeAdjustment = TimeAdjuster.adjustCookTime(mealPlan.meal.cookTimeMinutes, mealPlan.meal.servings, selectedServings)
                                
                                // Prep time
                                mealPlan.meal.prepTimeMinutes?.let { prepTime ->
                                    if (prepTime > 0) {
                                        val adjustedPrepTime = prepTime + prepTimeAdjustment
                                        items.add { 
                                            InfoColumn(
                                                value = "$adjustedPrepTime ${stringResource(R.string.meal_plan_min)}",
                                                label = stringResource(R.string.meal_plan_prep)
                                            )
                                        }
                                    }
                                }
                                
                                // Cook time
                                mealPlan.meal.cookTimeMinutes?.let { cookTime ->
                                    val adjustedCookTime = cookTime + cookTimeAdjustment
                                    items.add { 
                                        InfoColumn(
                                            value = "$adjustedCookTime ${stringResource(R.string.meal_plan_min)}",
                                            label = stringResource(R.string.meal_plan_cook)
                                        )
                                    }
                                }
                                
                                // Servings selector
                                mealPlan.meal.servings?.let {
                                    items.add {
                                        val canChangeServings = mealPlan.status == MealPlanStatus.PLANNED
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.clickable(enabled = canChangeServings) {
                                                val newServings = when (selectedServings) {
                                                    1 -> 2
                                                    2 -> 3
                                                    3 -> 4
                                                    4 -> 5
                                                    5 -> 6
                                                    else -> 1
                                                }
                                                selectedServings = newServings
                                                // Save the servings change to the meal plan
                                                viewModel.updatePlannedServings(mealPlanId, newServings)
                                            }
                                        ) {
                                            Text(
                                                text = "$selectedServings",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (canChangeServings) 
                                                    MaterialTheme.colorScheme.primary 
                                                else 
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                            )
                                            Text(
                                                text = stringResource(R.string.meal_plan_servings),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = if (canChangeServings) 1f else 0.6f
                                                )
                                            )
                                        }
                                    }
                                }
                                
                                // Total time (only if at least one time exists and is > 0)
                                val prepTime = mealPlan.meal.prepTimeMinutes ?: 0
                                val cookTime = mealPlan.meal.cookTimeMinutes ?: 0
                                val totalTime = prepTime + cookTime
                                
                                if (totalTime > 0) {
                                    val adjustedTotal = totalTime + prepTimeAdjustment + cookTimeAdjustment
                                    
                                    items.add { 
                                        InfoColumn(
                                            value = if (mealPlan.status == MealPlanStatus.COOKING && remainingMinutes != null) {
                                                "$remainingMinutes ${stringResource(R.string.meal_plan_min)}"
                                            } else {
                                                "$adjustedTotal ${stringResource(R.string.meal_plan_min)}"
                                            },
                                            label = if (mealPlan.status == MealPlanStatus.COOKING) stringResource(R.string.meal_plan_remaining) else stringResource(R.string.meal_plan_total)
                                        )
                                    }
                                }
                                
                                items.forEachIndexed { index, item ->
                                    Box(
                                        modifier = Modifier.weight(1f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        item()
                                    }
                                    if (index < items.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(40.dp)
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Divider(
                                                modifier = Modifier.fillMaxHeight(),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                            )
                                        }
                                    }
                                }
                            }

                    // Nutrition info per serving
                    val originalServings = mealPlan.meal.servings ?: 2
                    val nutritionInfo = remember(mealPlan.meal.ingredients, selectedServings, originalServings) {
                        val portions = mealPlan.meal.ingredients.map { mealIngredient ->
                            NutritionCalculator.IngredientPortion(
                                name = mealIngredient.ingredient.name,
                                quantity = mealIngredient.quantity,
                                unit = mealIngredient.unit ?: "g"
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
                                    .padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    InfoColumn(
                                        value = formatNutritionValue(nutritionInfo.calories),
                                        label = stringResource(R.string.nutrition_calories_short)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    InfoColumn(
                                        value = formatNutritionValue(nutritionInfo.fatsG),
                                        label = stringResource(R.string.nutrition_fats_short)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    InfoColumn(
                                        value = formatNutritionValue(nutritionInfo.carbsG),
                                        label = stringResource(R.string.nutrition_carbs_short)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(28.dp)
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                )
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    InfoColumn(
                                        value = formatNutritionValue(nutritionInfo.proteinG),
                                        label = stringResource(R.string.nutrition_protein_short)
                                    )
                                }
                            }
                    }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = stringResource(R.string.meal_plan_meal_type),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = when (mealPlan.mealType) {
                                            com.littlechef.app.domain.model.MealType.BREAKFAST -> stringResource(R.string.meal_type_breakfast)
                                            com.littlechef.app.domain.model.MealType.LUNCH -> stringResource(R.string.meal_type_lunch)
                                            com.littlechef.app.domain.model.MealType.DINNER -> stringResource(R.string.meal_type_dinner)
                                            com.littlechef.app.domain.model.MealType.SNACK -> stringResource(R.string.meal_type_snack)
                                            com.littlechef.app.domain.model.MealType.DESSERT -> stringResource(R.string.meal_type_dessert)
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = stringResource(R.string.meal_plan_status),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = when (mealPlan.status) {
                                            MealPlanStatus.PLANNED -> stringResource(R.string.meal_status_planned)
                                            MealPlanStatus.COOKING -> stringResource(R.string.meal_status_cooking)
                                            MealPlanStatus.COMPLETED -> stringResource(R.string.meal_status_completed)
                                            MealPlanStatus.ABORTED -> stringResource(R.string.meal_status_aborted)
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (mealPlan.status) {
                                            MealPlanStatus.PLANNED -> MaterialTheme.colorScheme.primary
                                            MealPlanStatus.COOKING -> MaterialTheme.colorScheme.tertiary
                                            MealPlanStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
                                            MealPlanStatus.ABORTED -> MaterialTheme.colorScheme.error
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Ingredients section with allergens
                item {
                    val allergens = mealPlan.meal.ingredients.flatMap { it.ingredient.allergens }.distinctBy { it.id }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.meal_plan_ingredients),
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
                }
                
                item {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (mealPlan.meal.ingredients.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.meal_plan_no_ingredients),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                mealPlan.meal.ingredients.forEach { mealIngredient ->
                                    // Check if a substitute is applied for this ingredient
                                    val substituteId = mealPlan.ingredientSubstitutions[mealIngredient.ingredient.id]
                                    val substituteIngredient = substituteId?.let { subId ->
                                        mealIngredient.ingredient.substitutes.find { 
                                            it.substituteIngredient.id == subId 
                                        }?.substituteIngredient
                                    }
                                    
                                    // Use substitute if applied, otherwise use original
                                    val displayIngredient = substituteIngredient ?: mealIngredient.ingredient
                                    val isSubstituted = substituteIngredient != null
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                modifier = Modifier.weight(1f),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                // Show star icon for star ingredients
                                                if (mealIngredient.isStarIngredient) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Star,
                                                        contentDescription = "Essential ingredient",
                                                        tint = Color(0xFFFFD700),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = viewModel.translateIngredientName(displayIngredient.name).replaceFirstChar { 
                                                            if (it.isLowerCase()) it.titlecase() else it.toString() 
                                                        },
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    
                                                    // Show "Substituting for [original]" if this is a substitute
                                                    if (isSubstituted) {
                                                        Text(
                                                            text = stringResource(R.string.meal_plan_substituting_for, viewModel.translateIngredientName(mealIngredient.ingredient.name)),
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.padding(top = 2.dp)
                                                        )
                                                    }
                                                }
                                                
                                                // Show revert button if this is a substitute
                                                if (isSubstituted) {
                                                    Box(
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    ) {
                                                        Surface(
                                                            modifier = Modifier
                                                                .size(24.dp)
                                                                .clickable {
                                                                    // Revert to original ingredient
                                                                    viewModel.removeSubstitute(mealPlanId, mealIngredient.ingredient.id)
                                                                    haptic.performLight()
                                                                },
                                                            shape = CircleShape,
                                                            color = MaterialTheme.colorScheme.primary
                                                        ) {
                                                            Box(
                                                                contentAlignment = Alignment.Center,
                                                                modifier = Modifier.fillMaxSize()
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.ArrowBack,
                                                                    contentDescription = "Revert to original",
                                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                            Text(
                                            text = run {
                                                val adjustedQty = roundEggQuantity(mealIngredient.quantity * servingsMultiplier, displayIngredient.name)
                                                val formatted = UnitConversion.formatForDisplay(
                                                    adjustedQty,
                                                    displayIngredient.unit
                                                )
                                                // Extract quantity and unit, then translate unit
                                                val parts = formatted.split(" ", limit = 2)
                                                if (parts.size == 2) {
                                                    "${parts[0]} ${getUnitTranslation(parts[1])}"
                                                } else {
                                                    formatted
                                                }
                                            },
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Normal,
                                                color = if (isSubstituted) 
                                                    MaterialTheme.colorScheme.primary 
                                                else 
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                    
                                    if (mealIngredient != mealPlan.meal.ingredients.last()) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Insufficient ingredients warning - moved here right after ingredients
                if (insufficientIngredients != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                insufficientIngredients!!.forEach { shortage ->
                                    // The shortage.ingredientName might be the substitute name if one is applied
                                    // We need to find the original ingredient from the meal to check substitutions
                                    val originalMealIngredient = mealPlan.meal.ingredients.find { mealIng ->
                                        // Check if this is the original ingredient
                                        mealIng.ingredient.name == shortage.ingredientName ||
                                        // Or if a substitute is applied and this is the substitute name
                                        mealPlan.ingredientSubstitutions[mealIng.ingredient.id]?.let { subId ->
                                            mealIng.ingredient.substitutes.any { 
                                                it.substituteIngredient.id == subId && 
                                                it.substituteIngredient.name == shortage.ingredientName 
                                            }
                                        } == true
                                    }
                                    
                                    val originalIngredient = originalMealIngredient?.ingredient
                                    val isSubstituteCurrentlyShown = originalIngredient?.let { orig ->
                                        mealPlan.ingredientSubstitutions[orig.id] != null &&
                                        orig.name != shortage.ingredientName
                                    } ?: false
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        // Main ingredient row
                                        Column {
                                            Text(
                                                text = viewModel.translateIngredientName(shortage.ingredientName).replaceFirstChar { 
                                                    if (it.isLowerCase()) it.titlecase() else it.toString() 
                                                },
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            
                                            // Show "Substituting for [original]" if this is a substitute
                                            if (isSubstituteCurrentlyShown && originalIngredient != null) {
                                                Text(
                                                    text = stringResource(R.string.meal_plan_substituting_for, viewModel.translateIngredientName(originalIngredient.name)),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${stringResource(R.string.meal_plan_need)}: ${com.littlechef.app.domain.model.UnitConversion.formatQuantity(shortage.required)} ${getUnitTranslation(shortage.unit)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${stringResource(R.string.meal_plan_have)}: ${com.littlechef.app.domain.model.UnitConversion.formatQuantity(shortage.available)} ${getUnitTranslation(shortage.unit)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                        
                                        // Show substitute options or revert option
                                        if (isSubstituteCurrentlyShown && originalIngredient != null) {
                                            // Currently showing a substitute - offer to revert
                                            Spacer(modifier = Modifier.height(4.dp))
                                            
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        // Revert to original ingredient
                                                        viewModel.removeSubstitute(mealPlanId, originalIngredient.id)
                                                        haptic.performLight()
                                                    }
                                                    .padding(vertical = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.meal_plan_revert_to_original, viewModel.translateIngredientName(originalIngredient.name)),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    fontWeight = FontWeight.Normal,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                
                                                Surface(
                                                    modifier = Modifier.size(24.dp),
                                                    shape = CircleShape,
                                                    color = MaterialTheme.colorScheme.secondary
                                                ) {
                                                    Box(
                                                        contentAlignment = Alignment.Center,
                                                        modifier = Modifier.fillMaxSize()
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ArrowBack,
                                                            contentDescription = "Revert to original",
                                                            tint = MaterialTheme.colorScheme.onSecondary,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            // Show substitute if available - make it clickable
                                            shortage.substitute?.let { sub ->
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Divider(
                                                    modifier = Modifier.padding(vertical = 4.dp),
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                                )
                                                
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            originalIngredient?.let { orig ->
                                                                // Apply substitution
                                                                viewModel.applySubstitute(mealPlanId, orig.id, sub.id)
                                                                haptic.performLight()
                                                            }
                                                        }
                                                        .padding(vertical = 4.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.meal_plan_use_substitute, viewModel.translateIngredientName(sub.name)),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.secondary,
                                                        fontWeight = FontWeight.Normal,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    
                                                    Surface(
                                                        modifier = Modifier.size(24.dp),
                                                        shape = CircleShape,
                                                        color = MaterialTheme.colorScheme.secondary
                                                    ) {
                                                        Box(
                                                            contentAlignment = Alignment.Center,
                                                            modifier = Modifier.fillMaxSize()
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.ArrowForward,
                                                                contentDescription = "Apply substitute",
                                                                tint = MaterialTheme.colorScheme.onSecondary,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (shortage != insufficientIngredients!!.last()) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Instructions
                if (mealPlan.meal.instructions != null) {
                    item(key = "instructions_header") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.meal_plan_instructions),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Show toggle only if both instructions exist
                            if (mealPlan.meal.instructions != null && mealPlan.meal.simpleInstructions != null) {
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
                                        } else null
                                    ) {
                                        Text(stringResource(R.string.meal_plan_detailed))
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
                                        } else null
                                    ) {
                                        Text(stringResource(R.string.meal_plan_simple))
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val instructionsToShow = when {
                                    mealPlan.meal.simpleInstructions != null && !useDetailedInstructions -> mealPlan.meal.simpleInstructions
                                    else -> mealPlan.meal.instructions
                                }
                                
                                val steps = instructionsToShow
                                    .split(Regex("\n\n+"))
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

                                steps.forEachIndexed { index, step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.padding(bottom = if (index < steps.size - 1) 16.dp else 0.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Cooking Actions
                item {
                    Card(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.meal_plan_cooking_actions),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            when (mealPlan.status) {
                                MealPlanStatus.PLANNED -> {
                                    Button(
                                        onClick = { 
                                            if (insufficientIngredients != null && insufficientIngredients!!.isNotEmpty()) {
                                                // Navigate to groceries if ingredients are insufficient
                                                onNavigateToGroceries()
                                            } else {
                                                // Show start dialog if ingredients are sufficient
                                                showStartDialog = true
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(if (insufficientIngredients != null && insufficientIngredients!!.isNotEmpty()) 
                                            stringResource(R.string.meal_plan_insufficient_ingredients)
                                        else 
                                            stringResource(R.string.meal_plan_start_cooking))
                                    }
                                    Button(
                                        onClick = { showAbortDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Text(stringResource(R.string.meal_plan_cancel_meal))
                                    }
                                }
                                MealPlanStatus.COOKING -> {
                                    Button(
                                        onClick = { showCompleteDialog = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(stringResource(R.string.meal_plan_complete_meal))
                                    }
                                    Button(
                                        onClick = { showAbortDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Text(stringResource(R.string.meal_plan_abort_button))
                                    }
                                }
                                MealPlanStatus.COMPLETED -> {
                                    Text(
                                        text = stringResource(R.string.meal_plan_completed_message),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                MealPlanStatus.ABORTED -> {
                                    Text(
                                        text = stringResource(R.string.meal_plan_aborted_message),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }

                // Error message
                if (errorMessage != null) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = errorMessage!!,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(76.dp))
                }
            }
        }
    }

    // Start Cooking Dialog
    if (showStartDialog) {
        AlertDialog(
            onDismissRequest = { showStartDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(R.string.meal_plan_start_cooking_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = stringResource(
                        R.string.meal_plan_start_cooking_message,
                        mealPlan?.meal?.name ?: ""
                    ),
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
                            showStartDialog = false
                            viewModel.startCooking(
                                mealPlanId = mealPlanId,
                                onSuccess = {
                                    haptic.performSuccess()
                                    errorMessage = null
                                    // Trigger scroll to instructions
                                    shouldScrollToInstructions = true
                                },
                                onInsufficientIngredients = { _ ->
                                    haptic.performError()
                                    errorMessage = null
                                },
                                onError = { error ->
                                    haptic.performError()
                                    errorMessage = error
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.meal_plan_start_button))
                    }
                    OutlinedButton(
                        onClick = { showStartDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(stringResource(R.string.meal_plan_cancel_button))
                    }
                }
            },
            dismissButton = { }
        )
    }

    // Complete Cooking Dialog
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(R.string.meal_plan_complete_cooking_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = stringResource(
                        R.string.meal_plan_complete_cooking_message,
                        mealPlan?.meal?.name ?: ""
                    ),
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
                            showCompleteDialog = false
                            viewModel.completeCooking(
                                mealPlanId = mealPlanId,
                                onSuccess = {
                                    haptic.performSuccess()
                                    errorMessage = null
                                    // insufficientIngredients is now managed by ViewModel
                                },
                                onError = { error ->
                                    haptic.performError()
                                    errorMessage = error
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.meal_plan_complete_button))
                    }
                    OutlinedButton(
                        onClick = { showCompleteDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.secondary
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    ) {
                        Text(stringResource(R.string.meal_plan_cancel_button))
                    }
                }
            },
            dismissButton = { }
        )
    }

    // Abort Cooking Dialog
    if (showAbortDialog) {
        val isPlanned = mealPlan?.status == MealPlanStatus.PLANNED
        AlertDialog(
            onDismissRequest = { showAbortDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(
                        if (isPlanned) R.string.meal_plan_cancel_meal_title 
                        else R.string.meal_plan_abort_cooking_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = stringResource(
                        if (isPlanned) R.string.meal_plan_cancel_meal_message 
                        else R.string.meal_plan_abort_cooking_message,
                        mealPlan?.meal?.name ?: ""
                    ),
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
                            showAbortDialog = false
                            viewModel.abortCooking(
                                mealPlanId = mealPlanId,
                                onSuccess = {
                                    haptic.performDestructive()
                                    errorMessage = null
                                    // insufficientIngredients is now managed by ViewModel
                                    // Navigate back to Plan screen after successful cancellation
                                    onNavigateBack()
                                },
                                onError = { error ->
                                    haptic.performError()
                                    errorMessage = error
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(
                            if (isPlanned) R.string.meal_plan_remove_button 
                            else R.string.meal_plan_abort_button
                        ))
                    }
                    Button(
                        onClick = { showAbortDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Text(stringResource(R.string.meal_plan_cancel_button))
                    }
                }
            },
            dismissButton = { }
        )
    }
}


@Composable
private fun InfoColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        else -> allergenName // Fallback to original name if not found
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
        else -> unit // Fallback to original unit if not found
    }
}
