package com.littlechef.app.ui.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.isUnspecified
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.littlechef.app.data.local.BundledRecipe
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CuisineMealsScreen(
    cuisine: Cuisine,
    onNavigateBack: () -> Unit,
    onNavigateToRecipe: (String) -> Unit = {},
    viewModel: CuisineMealsViewModel = hiltViewModel()
) {
    val recipes by viewModel.recipes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val defaultServingSize by viewModel.defaultServingSize.collectAsState()
    val favoriteRecipeIds by viewModel.favoriteRecipeIds.collectAsState()
    val favoriteRecipeIdsOrdered by viewModel.favoriteRecipeIdsOrdered.collectAsState()
    val context = LocalContext.current
    
    // Translate cuisine name
    val translatedCuisineName = cuisine.getLocalizedName(context)
    
    // Track screen composition
    LaunchedEffect(cuisine) {
        // Composition tracked
    }
    
    // Calculate target meal type fresh each time the screen is opened
    val targetMealType = remember(cuisine) { 
        viewModel.getCurrentMealType()
    }
    
    // Track if this is the initial load to only auto-scroll once per cuisine selection
    // Use rememberSaveable to persist across navigation to recipe and back
    var hasScrolledToTarget by rememberSaveable(cuisine) { mutableStateOf(false) }
    
    // Track the last loaded cuisine to avoid reloading when coming back from recipe
    // Use rememberSaveable to persist this across navigation
    var lastLoadedCuisine by rememberSaveable { mutableStateOf<Cuisine?>(null) }
    
    // State to control fade-in animation
    var showContent by remember(cuisine) { mutableStateOf(false) }
    
    // Clean up when leaving the screen entirely (not just going to recipe detail)
    DisposableEffect(Unit) {
        onDispose {
            // Don't reset lastLoadedCuisine here - let it persist
        }
    }
    
    LaunchedEffect(cuisine) {
        showContent = false
        // Only load recipes if this is a different cuisine or first load
        if (lastLoadedCuisine != cuisine) {
            viewModel.loadRecipes(cuisine)
            lastLoadedCuisine = cuisine
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = cuisine.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(translatedCuisineName, style = MaterialTheme.typography.headlineSmall)
                    }
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (recipes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = cuisine.iconRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = "No ${cuisine.displayName} recipes yet",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${cuisine.displayName} recipes coming soon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Group recipes by meal type and sort by ingredient count within each group
            val recipesByMealType = remember(recipes, favoriteRecipeIdsOrdered) {
                recipes.groupBy { recipeWithAllergens ->
                        recipeWithAllergens.recipe.mealType?.let { mealTypeString ->
                            try {
                                com.littlechef.app.domain.model.MealType.valueOf(mealTypeString)
                            } catch (e: IllegalArgumentException) {
                                null
                            }
                        }
                    }.mapValues { (_, recipesInGroup) ->
                        // Sort recipes: favorites first (in order they were liked), then by ingredient count (ascending)
                        val favorites = recipesInGroup.filter { favoriteRecipeIds.contains(it.recipe.id) }
                            .sortedBy { recipe -> favoriteRecipeIdsOrdered.indexOf(recipe.recipe.id) }
                        val nonFavorites = recipesInGroup.filter { !favoriteRecipeIds.contains(it.recipe.id) }
                            .sortedBy { it.recipe.ingredients.size }
                        favorites + nonFavorites
                    }
                }
            
            // Define the order of meal types
            val mealTypeOrder = listOf(
                com.littlechef.app.domain.model.MealType.BREAKFAST,
                com.littlechef.app.domain.model.MealType.LUNCH,
                com.littlechef.app.domain.model.MealType.DINNER,
                com.littlechef.app.domain.model.MealType.SNACK,
                com.littlechef.app.domain.model.MealType.DESSERT
            )
            
            // Create list state - it will automatically preserve scroll position across navigation
            // as long as the composable stays in the back stack
            val listState = rememberLazyListState()
            
            // Remember the first visible item index to maintain scroll position during reordering
            val firstVisibleItemIndex by rememberSaveable(stateSaver = androidx.compose.runtime.saveable.Saver(
                save = { listState.firstVisibleItemIndex },
                restore = { it }
            )) {
                mutableStateOf(0)
            }
            
            // Maintain scroll position when favorites change
            LaunchedEffect(favoriteRecipeIds) {
                if (listState.firstVisibleItemIndex > 0) {
                    // Preserve the current scroll position
                    val currentIndex = listState.firstVisibleItemIndex
                    val currentOffset = listState.firstVisibleItemScrollOffset
                    listState.scrollToItem(currentIndex, currentOffset)
                }
            }
            
            // Animated alpha for fade-in effect (reduced duration for snappier feel)
            val contentAlpha by animateFloatAsState(
                targetValue = if (showContent) 1f else 0f,
                animationSpec = tween(durationMillis = 200),
                label = "contentFadeIn"
            )
            
            // Auto-scroll to target meal type section when recipes are loaded (only once)
            LaunchedEffect(recipes, targetMealType) {
                if (recipes.isNotEmpty() && targetMealType != null && !hasScrolledToTarget) {
                    // Find the index of the target meal type header
                    var targetIndex = 0
                    var foundTarget = false
                    
                    for (mealType in mealTypeOrder) {
                        val recipesForType = recipesByMealType[mealType] ?: emptyList()
                        if (recipesForType.isNotEmpty()) {
                            if (mealType == targetMealType) {
                                foundTarget = true
                                
                                // Scroll to this section with positive offset to position header at top
                                if (targetIndex > 0) {
                                    listState.scrollToItem(targetIndex, scrollOffset = 50)
                                } else {
                                    listState.scrollToItem(0)
                                }
                                hasScrolledToTarget = true
                                break
                            }
                            // Count header + recipes for this section
                            targetIndex += 1 + recipesForType.size
                        }
                    }
                    
                    if (!foundTarget) {
                        hasScrolledToTarget = true
                    }
                    
                    // Wait a frame for scroll to complete, then fade in
                    delay(25)
                    showContent = true
                } else if (recipes.isNotEmpty() && !showContent) {
                    // No target meal type or already scrolled, just fade in immediately
                    delay(25)
                    showContent = true
                }
            }
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .alpha(contentAlpha),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Show recipes grouped by meal type
                mealTypeOrder.forEach { mealType ->
                    val recipesForType = recipesByMealType[mealType] ?: emptyList()
                    
                    if (recipesForType.isNotEmpty()) {
                        // Meal type header with separator (only for traditional cuisines)
                        val shouldShowMealTypeLabel = cuisine !in listOf(
                            Cuisine.BREAD_BAKERY,
                            Cuisine.SOUPS_STEWS,
                            Cuisine.VEGETARIAN_VEGAN,
                            Cuisine.MEAT_DISHES,
                            Cuisine.DESSERTS_SWEETS
                        )
                        
                        if (shouldShowMealTypeLabel) {
                            item(key = "header_${mealType.name}") {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = translateMealType(mealType),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Divider(
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Recipes for this meal type
                        items(
                            items = recipesForType,
                            key = { it.recipe.id },
                            contentType = { "recipe_card" }
                        ) { recipeWithAllergens ->
                            BundledRecipeCard(
                                recipe = recipeWithAllergens.recipe,
                                allergens = recipeWithAllergens.allergens,
                                defaultServingSize = defaultServingSize,
                                isFavorite = favoriteRecipeIds.contains(recipeWithAllergens.recipe.id),
                                onFavoriteClick = { viewModel.toggleFavorite(recipeWithAllergens.recipe.id) },
                                onClick = { onNavigateToRecipe(recipeWithAllergens.recipe.id) },
                                modifier = Modifier.animateItemPlacement(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            )
                        }
                    }
                }
                
                // Show recipes without meal type at the end
                val recipesWithoutType = recipesByMealType[null] ?: emptyList()
                if (recipesWithoutType.isNotEmpty()) {
                    // Only show "Other" header for traditional cuisines
                    val shouldShowMealTypeLabel = cuisine !in listOf(
                        Cuisine.BREAD_BAKERY,
                        Cuisine.SOUPS_STEWS,
                        Cuisine.VEGETARIAN_VEGAN,
                        Cuisine.MEAT_DISHES,
                        Cuisine.DESSERTS_SWEETS
                    )
                    
                    if (shouldShowMealTypeLabel) {
                        item(key = "header_other") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Other",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Divider(
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    items(
                        items = recipesWithoutType,
                        key = { it.recipe.id },
                        contentType = { "recipe_card" }
                    ) { recipeWithAllergens ->
                        BundledRecipeCard(
                            recipe = recipeWithAllergens.recipe,
                            allergens = recipeWithAllergens.allergens,
                            defaultServingSize = defaultServingSize,
                            isFavorite = favoriteRecipeIds.contains(recipeWithAllergens.recipe.id),
                            onFavoriteClick = { viewModel.toggleFavorite(recipeWithAllergens.recipe.id) },
                            onClick = { onNavigateToRecipe(recipeWithAllergens.recipe.id) },
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

private const val TAG = "CuisineMealsScreen"

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
private fun translateAllergen(allergenName: String): String {
    return when (allergenName.lowercase()) {
        "gluten" -> stringResource(R.string.allergen_gluten)
        "dairy" -> stringResource(R.string.allergen_dairy)
        "eggs" -> stringResource(R.string.allergen_eggs)
        "tree nuts", "tree_nuts" -> stringResource(R.string.allergen_tree_nuts)
        "peanuts" -> stringResource(R.string.allergen_peanuts)
        "soy" -> stringResource(R.string.allergen_soy)
        "fish" -> stringResource(R.string.allergen_fish)
        "shellfish" -> stringResource(R.string.allergen_shellfish)
        "sesame" -> stringResource(R.string.allergen_sesame)
        else -> allergenName
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BundledRecipeCard(
    recipe: BundledRecipe,
    allergens: List<com.littlechef.app.domain.model.Allergen>,
    defaultServingSize: Int,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val prepTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustPrepTime(recipe.prepTimeMinutes, recipe.servings, defaultServingSize)
    val cookTimeAdjustment = com.littlechef.app.ui.util.TimeAdjuster.adjustCookTime(recipe.cookTimeMinutes, recipe.servings, defaultServingSize)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 140.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .height(IntrinsicSize.Min)
            ) {
                // Recipe image - wrapped in Box to prevent driving height
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .fillMaxHeight()
                ) {
                if (recipe.imageUrl != null) {
                    // Use Coil for async image loading with size optimization
                    AsyncImage(
                        model = coil.request.ImageRequest.Builder(context)
                            .data("file:///android_asset/${recipe.imageUrl}")
                            .size(200, 280) // 2x the display size for quality on high-DPI screens
                            .crossfade(true)
                            .memoryCacheKey(recipe.imageUrl)
                            .diskCacheKey(recipe.imageUrl)
                            .build(),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            )
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_recipe_placeholder),
                        error = painterResource(R.drawable.ic_recipe_placeholder)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            )
                            .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🍽️",
                            fontSize = 32.sp
                        )
                    }
                }
            }
            
            // Recipe info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AutoSizeText(
                    text = recipe.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 2
                )
                
                // Allergen count badge
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
                
                // Push time to bottom so long names don't clip it
                Spacer(modifier = Modifier.weight(1f))
                
                val prepLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_prep)
                val cookLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_cook)
                val minLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_min)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    recipe.prepTimeMinutes?.let { basePrepTime ->
                        val adjustedPrepTime = basePrepTime + prepTimeAdjustment
                        Text(
                            text = "$prepLabel: $adjustedPrepTime $minLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    recipe.cookTimeMinutes?.let { baseCookTime ->
                        val adjustedCookTime = baseCookTime + cookTimeAdjustment
                        Text(
                            text = "$cookLabel: $adjustedCookTime $minLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
            
        // Heart icon in bottom-right corner
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .size(40.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        }
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
