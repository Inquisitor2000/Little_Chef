package com.littlechef.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.littlechef.app.ui.theme.md_theme_light_background
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.R
import com.littlechef.app.ui.util.rememberHapticFeedback
import com.littlechef.app.domain.model.MealPlan
import com.littlechef.app.domain.model.MealPlanStatus
import com.littlechef.app.domain.model.MealType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    viewModel: PlanViewModel = hiltViewModel(),
    onNavigateToMealPlanDetail: (String) -> Unit = {},
    onNavigateToCuisine: (com.littlechef.app.domain.model.Cuisine) -> Unit = {},
    onNavigateToSuggestion: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAddMealDialog by viewModel.showAddMealDialog.collectAsState()
    val userMeals by viewModel.userMeals.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    val haptic = rememberHapticFeedback()

    LaunchedEffect(Unit) {
        // Small delay to let UI render first
        kotlinx.coroutines.delay(50)
        viewModel.loadMealPlans()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(R.string.plan_title), style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    // Show clear button if there are completed/aborted meals
                    when (val state = uiState) {
                        is PlanUiState.Success -> {
                            val hasCompletedMeals = state.mealPlans.any { 
                                it.status == MealPlanStatus.COMPLETED || it.status == MealPlanStatus.ABORTED
                            }
                            if (hasCompletedMeals) {
                                Surface(
                                    onClick = { haptic.performLight(); showClearDialog = true },
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
                                            contentDescription = stringResource(R.string.plan_clear_completed),
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
                            onNavigateToSuggestion()
                        },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(androidx.compose.ui.res.stringResource(R.string.plan_suggest_button))
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
            when (val state = uiState) {
                is PlanUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is PlanUiState.Success -> {
                    val groupedPlans = viewModel.getGroupedMealPlans()
                    
                    if (groupedPlans.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.padding(32.dp)
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_empty_plan),
                                    contentDescription = "No plans",
                                    modifier = Modifier.size(100.dp),
                                    alpha = 0.6f
                                )
                                Text(
                                    text = androidx.compose.ui.res.stringResource(R.string.plan_empty_title),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = androidx.compose.ui.res.stringResource(R.string.plan_empty_subtitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            groupedPlans.forEach { (date, plans) ->
                                item {
                                    Text(
                                        text = formatDateHeader(date),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                
                                // Group plans by meal type
                                val plansByMealType = plans.groupBy { it.mealType }
                                
                                // Define meal type order
                                val mealTypeOrder = listOf(
                                    MealType.BREAKFAST,
                                    MealType.LUNCH,
                                    MealType.DINNER,
                                    MealType.SNACK,
                                    MealType.DESSERT
                                )
                                
                                // Show plans grouped by meal type
                                mealTypeOrder.forEach { mealType ->
                                    val plansForType = plansByMealType[mealType] ?: emptyList()
                                    
                                    if (plansForType.isNotEmpty()) {
                                        // Meal type header with divider
                                        item(key = "header_${date}_${mealType.name}") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                val context = LocalContext.current
                                                Text(
                                                    text = mealType.getLocalizedName(context),
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Divider(
                                                    modifier = Modifier.weight(1f),
                                                    color = MaterialTheme.colorScheme.outlineVariant
                                                )
                                            }
                                        }
                                        
                                        // Plans for this meal type
                                        items(plansForType, key = { it.id }) { mealPlan ->
                                            // Only allow swipe for PLANNED meals
                                            val canSwipe = mealPlan.status == MealPlanStatus.PLANNED
                                            com.littlechef.app.ui.util.SwipeToDeleteContainer(
                                                item = mealPlan,
                                                enabled = canSwipe,
                                                onDelete = { viewModel.deleteMealPlan(mealPlan.id) },
                                                confirmationTitle = stringResource(R.string.meal_plan_delete_title),
                                                confirmationMessage = stringResource(R.string.meal_plan_delete_message, mealPlan.meal.name),
                                                deleteButtonText = stringResource(R.string.meal_plan_delete_button),
                                                cancelButtonText = stringResource(R.string.meal_plan_cancel_button)
                                            ) {
                                                MealPlanCard(
                                                    mealPlan = mealPlan,
                                                    onClick = { onNavigateToMealPlanDetail(mealPlan.id) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is PlanUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    
    // Clear completed meals dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = MaterialTheme.colorScheme.background,
            title = { 
                Text(
                    text = stringResource(R.string.plan_clear_completed_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = { 
                Text(
                    text = stringResource(R.string.plan_clear_completed_message),
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
                            viewModel.clearCompletedMeals()
                            showClearDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.plan_clear_button))
                    }
                    OutlinedButton(
                        onClick = { showClearDialog = false },
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealPlanCard(
    mealPlan: MealPlan,
    onClick: () -> Unit
) {
    // Calculate remaining time for cooking meals
    var remainingMinutes by remember { mutableStateOf<Int?>(null) }
    
    LaunchedEffect(mealPlan.status, mealPlan.startedAt) {
        if (mealPlan.status == MealPlanStatus.COOKING && mealPlan.startedAt != null) {
            val servings = mealPlan.meal.servings ?: 2
            val plannedServings = mealPlan.plannedServings ?: servings
            
            val prepTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustPrepTime(mealPlan.meal.prepTimeMinutes, servings, plannedServings)
            val cookTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustCookTime(mealPlan.meal.cookTimeMinutes, servings, plannedServings)
            
            val basePrepTime = mealPlan.meal.prepTimeMinutes ?: 0
            val baseCookTime = mealPlan.meal.cookTimeMinutes ?: 0
            val totalMinutes = basePrepTime + baseCookTime + prepTimeAdjustment + cookTimeAdjustment
            
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
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
            .clickable(onClick = onClick),
        border = if (mealPlan.status != MealPlanStatus.COMPLETED) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 140.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Recipe image - wrapped in Box to scale with card height
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                com.littlechef.app.ui.util.RecipeImage(
                    imagePath = mealPlan.meal.imagePath,
                    contentDescription = mealPlan.meal.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                )
            }
            
            // Meal info
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        // Meal name
                        AutoSizeText(
                            text = mealPlan.meal.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(0.9f),
                                color = if (mealPlan.status == MealPlanStatus.COMPLETED) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            ),
                            maxLines = 2
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Allergen count badge
                        val allergens = mealPlan.meal.ingredients.flatMap { it.ingredient.allergens }.distinctBy { it.id }
                        if (allergens.isNotEmpty()) {
                            val resources = LocalContext.current.resources
                            val allergenCountText = resources.getQuantityString(R.plurals.allergen_count, allergens.size, allergens.size)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = allergenCountText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    
                    // Bottom row: time info (stays at bottom left)
                    Column {
                        // Show countdown if cooking, otherwise show total time
                        if (mealPlan.status == MealPlanStatus.COOKING && remainingMinutes != null) {
                            Text(
                                text = "${stringResource(R.string.plan_time_remaining)}: $remainingMinutes ${stringResource(R.string.meal_plan_min)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (mealPlan.status != MealPlanStatus.COMPLETED) {
                            val servings = mealPlan.meal.servings ?: 2
                            val plannedServings = mealPlan.plannedServings ?: servings
                            
                            val prepTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustPrepTime(mealPlan.meal.prepTimeMinutes, servings, plannedServings)
                            val cookTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustCookTime(mealPlan.meal.cookTimeMinutes, servings, plannedServings)
                            
                            val basePrepTime = mealPlan.meal.prepTimeMinutes ?: 0
                            val baseCookTime = mealPlan.meal.cookTimeMinutes ?: 0
                            val totalTime = basePrepTime + baseCookTime + prepTimeAdjustment + cookTimeAdjustment
                            
                            if (totalTime > 0) {
                                Text(
                                    text = "${stringResource(R.string.plan_total_time)}: $totalTime ${stringResource(R.string.meal_plan_min)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Status badge positioned at bottom right (overlays the card)
                if (mealPlan.status == MealPlanStatus.COOKING || mealPlan.status == MealPlanStatus.COMPLETED) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (mealPlan.status == MealPlanStatus.COOKING) {
                            Color(0xFF4CAF50) // Green for cooking
                        } else {
                            MaterialTheme.colorScheme.secondary // Secondary for done
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = if (mealPlan.status == MealPlanStatus.COOKING) {
                                stringResource(R.string.meal_status_cooking)
                            } else {
                                stringResource(R.string.meal_plan_done_tag)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = if (mealPlan.status == MealPlanStatus.COOKING) {
                                Color.White
                            } else {
                                MaterialTheme.colorScheme.onSecondary
                            },
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun formatDateHeader(date: LocalDate): String {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val todayStr = stringResource(R.string.plan_today)
    val tomorrowStr = stringResource(R.string.plan_tomorrow)
    
    return when (date) {
        today -> "$todayStr, ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}"
        tomorrow -> "$tomorrowStr, ${date.format(DateTimeFormatter.ofPattern("MMMM dd"))}"
        else -> date.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMealPlanDialog(
    userMeals: List<com.littlechef.app.domain.model.Meal>,
    onDismiss: () -> Unit,
    onAddMealPlan: (String, MealType, Long) -> Unit,
    onNavigateToCuisine: (com.littlechef.app.domain.model.Cuisine) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedMealType by remember { mutableStateOf(MealType.DINNER) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var selectedMealId by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meal to Plan", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Meal Type Selection - Main meals
                Text(
                    text = "Meal Type",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER).forEach { mealType ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(mealType.name.lowercase().replaceFirstChar { it.uppercase() }) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp
                            )
                        )
                    }
                }
                
                // Meal Type Selection - Snacks and Desserts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(MealType.SNACK, MealType.DESSERT).forEach { mealType ->
                        FilterChip(
                            selected = selectedMealType == mealType,
                            onClick = { selectedMealType = mealType },
                            label = { 
                                Text(mealType.name.lowercase().replaceFirstChar { it.uppercase() }) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = MaterialTheme.colorScheme.outline,
                                selectedBorderColor = MaterialTheme.colorScheme.primary,
                                borderWidth = 1.dp,
                                selectedBorderWidth = 1.dp
                            )
                        )
                    }
                }
                
                // Date Selection
                Text(
                    text = "Date",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(formatDate(selectedDate))
                }
                
                Divider()
                
                // Recipe Selection Tabs
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("My Recipes") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Browse by Cuisine") }
                    )
                }
                
                // Tab Content
                when (selectedTab) {
                    0 -> {
                        // My Recipes
                        if (userMeals.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No saved recipes yet",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(userMeals) { meal ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedMealId = meal.id },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (selectedMealId == meal.id)
                                                MaterialTheme.colorScheme.primaryContainer
                                            else
                                                MaterialTheme.colorScheme.surface
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = meal.name,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Normal
                                                )
                                                val totalTime = (meal.prepTimeMinutes ?: 0) + (meal.cookTimeMinutes ?: 0)
                                                if (totalTime > 0) {
                                                    Text(
                                                        text = "$totalTime min",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            if (selectedMealId == meal.id) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        // Browse by Cuisine
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(com.littlechef.app.domain.model.Cuisine.values().toList()) { cuisine ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onDismiss()
                                            onNavigateToCuisine(cuisine)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = cuisine.iconRes),
                                                contentDescription = null,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Text(
                                                text = cuisine.displayName,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowRight,
                                            contentDescription = "Browse"
                                        )
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
                    selectedMealId?.let { mealId ->
                        onAddMealPlan(mealId, selectedMealType, selectedDate)
                    }
                },
                enabled = selectedMealId != null && selectedTab == 0
            ) {
                Text("Add to Plan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
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
                        Text("Cancel")
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
                        Text("OK")
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
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
    val month = calendar.get(java.util.Calendar.MONTH) + 1
    val year = calendar.get(java.util.Calendar.YEAR)
    return "$day/$month/$year"
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
private fun AutoSizeText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    maxLines: Int,
    modifier: Modifier = Modifier
) {
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    
    Text(
        text = text,
        style = textStyle,
        maxLines = maxLines,
        softWrap = true,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                val currentSize = textStyle.fontSize
                if (!currentSize.isUnspecified) {
                    textStyle = textStyle.copy(fontSize = currentSize * 0.9)
                } else {
                    readyToDraw = true
                }
            } else {
                readyToDraw = true
            }
        }
    )
}
