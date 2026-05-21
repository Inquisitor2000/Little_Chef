package com.littlechef.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import com.littlechef.app.ui.util.rememberHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.littlechef.app.R
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.ui.util.RecipeImage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUserMeal: (String) -> Unit,
    onNavigateToBundledRecipe: (Cuisine, String) -> Unit,
    viewModel: SuggestionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Reload suggestions every time the screen is displayed
    LaunchedEffect(Unit) {
        viewModel.loadSuggestions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(androidx.compose.ui.res.stringResource(R.string.suggestions_title), style = MaterialTheme.typography.headlineSmall) },
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
        when (val state = uiState) {
            is SuggestionUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is SuggestionUiState.Error -> {
                ErrorContent(
                    message = state.message,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.padding(padding)
                )
            }
            is SuggestionUiState.Success -> {
                SuccessContent(
                    perfectMatches = state.allPerfectMatches,
                    goodMatches = state.allGoodMatches,
                    partialMatches = state.allPartialMatches,
                    filteredPerfectMatches = state.filteredPerfectMatches,
                    filteredGoodMatches = state.filteredGoodMatches,
                    filteredPartialMatches = state.filteredPartialMatches,
                    selectedDishCategory = state.selectedDishCategory,
                    hasActiveFilters = state.selectedDishCategory != null,
                    onDishCategorySelected = { viewModel.filterByDishCategory(it) },
                    onUserMealClick = onNavigateToUserMeal,
                    onBundledRecipeClick = onNavigateToBundledRecipe,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.suggestions_loading),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.suggestions_error_oops),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(androidx.compose.ui.res.stringResource(R.string.suggestions_retry))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessContent(
    perfectMatches: List<MealSuggestion>,
    goodMatches: List<MealSuggestion>,
    partialMatches: List<MealSuggestion>,
    filteredPerfectMatches: List<MealSuggestion>,
    filteredGoodMatches: List<MealSuggestion>,
    filteredPartialMatches: List<MealSuggestion>,
    selectedDishCategory: com.littlechef.app.domain.model.DishCategory?,
    hasActiveFilters: Boolean,
    onDishCategorySelected: (com.littlechef.app.domain.model.DishCategory?) -> Unit,
    onUserMealClick: (String) -> Unit,
    onBundledRecipeClick: (Cuisine, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // If no pantry-based meals, show centered empty state
    if (perfectMatches.isEmpty() && goodMatches.isEmpty() && partialMatches.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_sub_whole_spices),
                    contentDescription = "No suggestions",
                    modifier = Modifier.size(100.dp),
                    alpha = 0.6f
                )
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.suggestions_empty_pantry_title),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = androidx.compose.ui.res.stringResource(R.string.suggestions_empty_pantry_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 56.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filter carousel - always visible
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Dish Category stepper (only filter)
                DishCategoryStepper(
                    selectedDishCategory = selectedDishCategory,
                    onDishCategorySelected = onDishCategorySelected,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Divider after filters
        item {
            Divider()
        }

        // When NO filters are active, show all meals directly
        if (!hasActiveFilters) {
            // Perfect Matches
            if (perfectMatches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = androidx.compose.ui.res.stringResource(R.string.suggestions_perfect_matches),
                        count = perfectMatches.size,
                        emoji = "✨"
                    )
                }
                items(perfectMatches.size) { index ->
                    val suggestion = perfectMatches[index]
                    AnimatedMealCard(
                        suggestion = suggestion,
                        onClick = {
                            when (suggestion) {
                                is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                            }
                        }
                    )
                }
            }

            // Good Matches
            if (goodMatches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = androidx.compose.ui.res.stringResource(R.string.suggestions_good_matches),
                        count = goodMatches.size,
                        emoji = "👍"
                    )
                }
                items(goodMatches.size) { index ->
                    val suggestion = goodMatches[index]
                    AnimatedMealCard(
                        suggestion = suggestion,
                        onClick = {
                            when (suggestion) {
                                is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                            }
                        }
                    )
                }
            }

            // Partial Matches
            if (partialMatches.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = androidx.compose.ui.res.stringResource(R.string.suggestions_partial_matches),
                        count = partialMatches.size,
                        emoji = "🤔"
                    )
                }
                items(partialMatches.size) { index ->
                    val suggestion = partialMatches[index]
                    AnimatedMealCard(
                        suggestion = suggestion,
                        onClick = {
                            when (suggestion) {
                                is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                            }
                        }
                    )
                }
            }
        } else {
            // When filters ARE active, show filtered results and remaining meals
            
            // Check if filter returned any results
            if (filteredPerfectMatches.isEmpty() && filteredGoodMatches.isEmpty() && filteredPartialMatches.isEmpty()) {
                // Show simple text for no filter matches
                item {
                    var isVisible by remember { mutableStateOf(false) }
                    
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(200L)
                        isVisible = true
                    }
                    
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isVisible,
                        enter = androidx.compose.animation.fadeIn(
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 300,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        ),
                        exit = androidx.compose.animation.fadeOut(
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 200,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = androidx.compose.ui.res.stringResource(R.string.suggestions_no_filter_results),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                // Filtered Perfect Matches
                if (filteredPerfectMatches.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = androidx.compose.ui.res.stringResource(R.string.suggestions_perfect_matches),
                            count = filteredPerfectMatches.size,
                            emoji = "✨"
                        )
                    }
                    items(filteredPerfectMatches.size) { index ->
                        val suggestion = filteredPerfectMatches[index]
                        AnimatedMealCard(
                            suggestion = suggestion,
                            onClick = {
                                when (suggestion) {
                                    is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                    is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                                }
                            }
                        )
                    }
                }

                // Filtered Good Matches
                if (filteredGoodMatches.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = androidx.compose.ui.res.stringResource(R.string.suggestions_good_matches),
                            count = filteredGoodMatches.size,
                            emoji = "👍"
                        )
                    }
                    items(filteredGoodMatches.size) { index ->
                        val suggestion = filteredGoodMatches[index]
                        AnimatedMealCard(
                            suggestion = suggestion,
                            onClick = {
                                when (suggestion) {
                                    is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                    is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                                }
                            }
                        )
                    }
                }

                // Filtered Partial Matches
                if (filteredPartialMatches.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = androidx.compose.ui.res.stringResource(R.string.suggestions_partial_matches),
                            count = filteredPartialMatches.size,
                            emoji = "🤔"
                        )
                    }
                    items(filteredPartialMatches.size) { index ->
                        val suggestion = filteredPartialMatches[index]
                        AnimatedMealCard(
                            suggestion = suggestion,
                            onClick = {
                                when (suggestion) {
                                    is MealSuggestion.UserMeal -> onUserMealClick(suggestion.meal.id)
                                    is MealSuggestion.BundledMeal -> onBundledRecipeClick(suggestion.cuisine, suggestion.recipe.id)
                                }
                            }
                        )
                    }
                }
            }  // Close the if statement for filtered results check
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AnimatedMealCard(
    suggestion: MealSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Simply render the card without animation to prevent jumping during scroll
    MealSuggestionCard(
        suggestion = suggestion,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun EmptyStateContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_empty_suggestions),
                contentDescription = "No suggestions",
                modifier = Modifier.size(120.dp),
                alpha = 0.6f
            )
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.suggestions_empty_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = androidx.compose.ui.res.stringResource(R.string.suggestions_empty_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun FilteredResultsHeader(
    selectedDishCategory: com.littlechef.app.domain.model.DishCategory?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Text(
                text = "Filtered Results",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            // Filter badge
            selectedDishCategory?.let {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiary,
                    shadowElevation = 1.dp
                ) {
                    Text(
                        text = "${it.emoji} ${it.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    count: Int,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "$count ${if (count == 1) androidx.compose.ui.res.stringResource(R.string.suggestions_meal) else androidx.compose.ui.res.stringResource(R.string.suggestions_meals)}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealSuggestionCard(
    suggestion: MealSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showMatchPercentage: Boolean = true
) {
    val context = LocalContext.current
    
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                // Meal Image
                Box {
                    val imagePath = when (suggestion) {
                        is MealSuggestion.UserMeal -> suggestion.meal.imagePath
                        is MealSuggestion.BundledMeal -> suggestion.recipe.imageUrl
                    }

                    RecipeImage(
                        imagePath = imagePath,
                        contentDescription = when (suggestion) {
                            is MealSuggestion.UserMeal -> suggestion.meal.name
                            is MealSuggestion.BundledMeal -> suggestion.recipe.name
                        },
                        modifier = Modifier
                            .width(120.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    )

                    // Match Percentage Badge (only show if showMatchPercentage is true)
                    if (showMatchPercentage) {
                        MatchPercentageBadge(
                            percentage = suggestion.matchPercentage,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        )
                    }
                }

                // Meal Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                // Meal Name
                Text(
                    text = when (suggestion) {
                        is MealSuggestion.UserMeal -> suggestion.meal.name
                        is MealSuggestion.BundledMeal -> suggestion.recipe.name
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Meal Type Indicator and Category Badges (only cuisine + allergen count)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    // Source badge (My Recipe or Cuisine)
                    when (suggestion) {
                        is MealSuggestion.UserMeal -> {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "My Recipe",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            
                            // Count allergens from meal ingredients
                            val allergenCount = suggestion.meal.ingredients
                                .flatMap { it.ingredient.allergens }
                                .distinctBy { it.id }
                                .size
                            
                            if (allergenCount > 0) {
                                val resources = context.resources
                                val allergenCountText = resources.getQuantityString(R.plurals.allergen_count, allergenCount, allergenCount)
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
                        is MealSuggestion.BundledMeal -> {
                            // Cuisine name (without icon)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Text(
                                    text = suggestion.cuisine.getLocalizedName(context),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            
                            // For bundled recipes, check allergens from the allergen cache
                            // Count unique allergens from all ingredients
                            val allergenCount = try {
                                // Debug: Log cache status and ingredients
                                android.util.Log.d("SuggestionScreen", "Cache size: ${com.littlechef.app.domain.usecase.PreloadCuisineAllergensUseCase.allergenCache.size}")
                                android.util.Log.d("SuggestionScreen", "Recipe ingredients: ${suggestion.recipe.ingredients.map { it.name }}")
                                
                                suggestion.recipe.ingredients
                                    .mapNotNull { ingredient ->
                                        // Match the cache key format: trim and lowercase
                                        val cacheKey = ingredient.name.trim().lowercase()
                                        val allergens = com.littlechef.app.domain.usecase.PreloadCuisineAllergensUseCase.allergenCache[cacheKey]
                                        android.util.Log.d("SuggestionScreen", "Ingredient '${ingredient.name}' (key: '$cacheKey') -> allergens: ${allergens?.size ?: 0}")
                                        allergens
                                    }
                                    .flatten()
                                    .distinctBy { it.id }
                                    .size
                            } catch (e: Exception) {
                                android.util.Log.e("SuggestionScreen", "Error counting allergens", e)
                                0
                            }
                            
                            // Debug: Log allergen info
                            android.util.Log.d("SuggestionScreen", "Recipe: ${suggestion.recipe.name}, Allergen count: $allergenCount")
                            
                            if (allergenCount > 0) {
                                val resources = context.resources
                                val allergenCountText = resources.getQuantityString(R.plurals.allergen_count, allergenCount, allergenCount)
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
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Ingredient Info (only show if showMatchPercentage is true AND there are missing ingredients)
                if (showMatchPercentage && suggestion.missingIngredientNames.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${suggestion.availableIngredients}/${suggestion.totalIngredients} ${androidx.compose.ui.res.stringResource(R.string.suggestions_ingredients)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${suggestion.missingIngredientNames.size} ${androidx.compose.ui.res.stringResource(R.string.suggestions_missing)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else if (!showMatchPercentage) {
                    // For vibe recommendations, show total ingredients count
                    Text(
                        text = "${suggestion.totalIngredients} ${androidx.compose.ui.res.stringResource(R.string.suggestions_ingredients)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Prep/Cook Time on separate lines
                val prepLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_prep)
                val cookLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_cook)
                val minLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_min)
                
                val prepTime = when (suggestion) {
                    is MealSuggestion.UserMeal -> suggestion.meal.prepTimeMinutes
                    is MealSuggestion.BundledMeal -> suggestion.recipe.prepTimeMinutes
                }
                val cookTime = when (suggestion) {
                    is MealSuggestion.UserMeal -> suggestion.meal.cookTimeMinutes
                    is MealSuggestion.BundledMeal -> suggestion.recipe.cookTimeMinutes
                }
                
                if (prepTime != null || cookTime != null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        prepTime?.let {
                            Text(
                                text = "$prepLabel: $it $minLabel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        cookTime?.let {
                            Text(
                                text = "$cookLabel: $it $minLabel",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchPercentageBadge(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        percentage == 100 -> MaterialTheme.colorScheme.primary
        percentage >= 80 -> MaterialTheme.colorScheme.tertiary
        percentage >= 50 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val textColor = when {
        percentage == 100 -> MaterialTheme.colorScheme.onPrimary
        percentage >= 80 -> MaterialTheme.colorScheme.onTertiary
        percentage >= 50 -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = backgroundColor
    ) {
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun DishCategoryStepper(
    selectedDishCategory: com.littlechef.app.domain.model.DishCategory?,
    onDishCategorySelected: (com.littlechef.app.domain.model.DishCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val baseItems = remember {
        listOf<com.littlechef.app.domain.model.DishCategory?>(null) + com.littlechef.app.domain.model.DishCategory.entries
    }
    val repeatCount = 1000
    val totalItems = baseItems.size * repeatCount
    val middleStart = (repeatCount / 2) * baseItems.size

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val haptic = rememberHapticFeedback()
    val context = LocalContext.current
    val density = LocalDensity.current
    val itemWidthPx = with(density) { 100.dp.toPx() }.toInt()

    // Map total index → base item
    fun itemAt(idx: Int): com.littlechef.app.domain.model.DishCategory? = baseItems[idx % baseItems.size]

    // Find the total index nearest the viewport center.
    // Uses scroll position directly (always fresh) instead of layoutInfo.visibleItemsInfo
    // which can be stale when settle fires.
    fun findCenterItem(): Int {
        val firstIdx = listState.firstVisibleItemIndex
        val scrollOff = listState.firstVisibleItemScrollOffset
        return if (scrollOff > itemWidthPx / 2f) firstIdx + 1 else firstIdx
    }

    // The unique item currently centered — used for visual highlighting during scroll
    var centerItem by remember {
        mutableStateOf(itemAt(middleStart + (baseItems.indexOf(selectedDishCategory).coerceAtLeast(0))))
    }
    // Haptic guards: only fire once per unique item per gesture phase
    var lastLightItem by remember { mutableStateOf<Any?>(null) }
    var lastSuccessItem by remember { mutableStateOf<Any?>(null) }

    // Scroll to initial selection (in the middle of the infinite range)
    LaunchedEffect(Unit) {
        val target = middleStart + (baseItems.indexOf(selectedDishCategory).coerceAtLeast(0))
        listState.scrollToItem(target)
    }

    // During scroll: track center for visual + light haptic when center swaps
    // Does NOT call onDishCategorySelected — selection only commits on settle
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .debounce(40)
            .collect { _ ->
                val idx = findCenterItem()
                val item = itemAt(idx)
                centerItem = item
                if (item != lastLightItem) {
                    haptic.performLight()
                    lastLightItem = item
                }
            }
    }

    // On scroll settle: commit selection + success haptic + snap to center
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val idx = findCenterItem()
            val settledItem = itemAt(idx)
            centerItem = settledItem

            if (settledItem != selectedDishCategory) {
                onDishCategorySelected(settledItem)
            }
            if (settledItem != lastSuccessItem) {
                haptic.performSuccess()
                lastSuccessItem = settledItem
            }

            // Snap nearest item to the centered slot
            if (listState.firstVisibleItemScrollOffset != 0) {
                coroutineScope.launch {
                    listState.animateScrollToItem(idx)
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val itemWidth = 100.dp
        val sidePad = (maxWidth - itemWidth) / 2

        // Subtle bar rail
        Surface(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) {}

        // Center selection highlight
        Surface(
            modifier = Modifier
                .width(itemWidth)
                .height(80.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {}

        // Scrollable items (infinite via repeated list)
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth().height(80.dp),
            contentPadding = PaddingValues(horizontal = sidePad)
        ) {
            items(totalItems) { index ->
                val item = itemAt(index)
                val isSelected = item == centerItem
                Box(
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                        .clickable {
                            onDishCategorySelected(item)
                            centerItem = item
                            lastSuccessItem = item
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (item == null) "🍽️" else item.emoji,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isSelected) 1f else 0.4f)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (item == null)
                                androidx.compose.ui.res.stringResource(R.string.add_recipe_none)
                            else
                                item.getLocalizedName(context),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
