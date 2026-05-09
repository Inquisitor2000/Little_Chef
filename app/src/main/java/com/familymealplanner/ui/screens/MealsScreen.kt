package com.familymealplanner.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.R
import com.familymealplanner.domain.model.Cuisine
import com.familymealplanner.domain.model.Meal
import com.familymealplanner.ui.components.PremiumPackPreview
import com.familymealplanner.ui.components.PremiumPreviewDrawer
import com.familymealplanner.ui.util.RecipeImage
import com.familymealplanner.ui.util.rememberHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsScreen(
    onNavigateToCuisine: (Cuisine) -> Unit = {},
    onNavigateToAddMeal: () -> Unit = {},
    onNavigateToRecipe: (String) -> Unit = {},
    viewModel: MealsViewModel = hiltViewModel(),
    cuisineViewModel: CuisineMealsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    
    // Observe purchase state
    val purchaseState by viewModel.purchaseState.collectAsState()
    
    // State for premium preview drawer
    var showPremiumCuisine by remember { mutableStateOf<Cuisine?>(null) }
    val showPremiumPreview = showPremiumCuisine?.let { getPremiumPackPreview(it) }
    
    // Handle purchase state changes
    LaunchedEffect(purchaseState) {
        when (val state = purchaseState) {
            is com.familymealplanner.billing.PurchaseState.Success -> {
                // Purchase successful, close drawer and navigate
                android.widget.Toast.makeText(context, "Purchase successful! Recipes unlocked.", android.widget.Toast.LENGTH_SHORT).show()
                showPremiumPreview?.let { preview ->
                    showPremiumCuisine = null
                    onNavigateToCuisine(preview.cuisine)
                }
                viewModel.resetPurchaseState()
            }
            is com.familymealplanner.billing.PurchaseState.Cancelled -> {
                // User cancelled, just reset state
                android.widget.Toast.makeText(context, "Purchase cancelled", android.widget.Toast.LENGTH_SHORT).show()
                viewModel.resetPurchaseState()
            }
            is com.familymealplanner.billing.PurchaseState.Error -> {
                // Show error
                android.widget.Toast.makeText(context, "Error: ${state.message}", android.widget.Toast.LENGTH_LONG).show()
                viewModel.resetPurchaseState()
            }
            is com.familymealplanner.billing.PurchaseState.Loading -> {
                android.widget.Toast.makeText(context, "Opening Google Play...", android.widget.Toast.LENGTH_SHORT).show()
            }
            is com.familymealplanner.billing.PurchaseState.Downloading -> {
                android.widget.Toast.makeText(context, "Downloading recipes... ${state.progress}%", android.widget.Toast.LENGTH_SHORT).show()
            }
            else -> { /* Idle */ }
        }
    }
    
    // Track initial composition
    LaunchedEffect(Unit) {
        // Composition complete
    }
    
    // Preload Italian cuisine (most popular) when screen is first displayed
    LaunchedEffect(Unit) {
        cuisineViewModel.preloadRecipes(Cuisine.ITALIAN)
    }
    
    val haptic = rememberHapticFeedback()

    // Handle cuisine click with DLC check
    val handleCuisineClick: (Cuisine) -> Unit = { cuisine ->
        if (cuisine.isDLC) {
            showPremiumCuisine = cuisine
        } else {
            onNavigateToCuisine(cuisine)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.meals_title), style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    Button(
                        onClick = {
                            haptic.performLight()
                            onNavigateToAddMeal()
                        },
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.meals_add_recipe))
                    }
                }
            )
        }
    ) { padding ->
        val uiState by viewModel.uiState.collectAsState()
        
        val scrapedMeals = when (uiState) {
            is MealsUiState.Success -> (uiState as MealsUiState.Success).scrapedMeals
            else -> emptyList()
        }
        
        val isLoading = uiState is MealsUiState.Loading
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // My Recipes section - show placeholder while loading or if there are recipes
            if (isLoading || scrapedMeals.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    if (isLoading) {
                        MyRecipesPlaceholder()
                    } else {
                        MyRecipesSection(
                            recipes = scrapedMeals,
                            onRecipeClick = { meal -> onNavigateToRecipe(meal.id) }
                        )
                    }
                }
                
                item(span = { GridItemSpan(2) }) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Cuisines section
            item(span = { GridItemSpan(2) }) {
                CuisinesSection(
                    onCuisineClick = handleCuisineClick
                )
            }
        }
    }
    
    // Show premium preview drawer if needed
    showPremiumPreview?.let { preview ->
        PremiumPreviewDrawer(
            packPreview = preview,
            onDismiss = { showPremiumCuisine = null },
            onPurchase = {
                // Launch billing flow
                val act = activity
                val productId = preview.cuisine.assetPackName ?: ""
                
                android.util.Log.d("MealsScreen", "Purchase button clicked")
                android.util.Log.d("MealsScreen", "Activity: $act")
                android.util.Log.d("MealsScreen", "Product ID: $productId")
                
                if (act != null && productId.isNotEmpty()) {
                    android.util.Log.d("MealsScreen", "Launching billing flow for: $productId")
                    viewModel.purchaseDLC(act, productId)
                } else {
                    android.util.Log.e("MealsScreen", "Cannot launch billing - Activity: $act, ProductId: $productId")
                }
            }
        )
    }
}

@Composable
private fun CuisinesSection(
    onCuisineClick: (Cuisine) -> Unit
) {
    val allCuisines = Cuisine.entries
    
    // Separate DLC and regular cuisines
    val premiumCuisines = allCuisines.filter { it.isDLC }
    val regularCuisines = allCuisines.filter { !it.isDLC }
    
    // Split regular cuisines into country-based (first 5) and type-based (rest)
    val countryBasedCuisines = regularCuisines.take(5) // Italian, Mexican, Asian, Mediterranean, French
    val typeBasedCuisines = regularCuisines.drop(5)    // Bread & Bakery, Soups & Stews, etc.
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Premium Section (if any DLC cuisines exist)
        if (premiumCuisines.isNotEmpty()) {
            Text(
                text = stringResource(R.string.meals_premium_headline),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                premiumCuisines.forEach { cuisine ->
                    CuisineCard(
                        cuisine = cuisine,
                        onClick = { onCuisineClick(cuisine) }
                    )
                }
            }
            
            // Divider after Premium section
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
        
        // Cuisines by Country Section
        Text(
            text = stringResource(R.string.meals_cuisines_headline),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            countryBasedCuisines.forEach { cuisine ->
                CuisineCard(
                    cuisine = cuisine,
                    onClick = { onCuisineClick(cuisine) }
                )
            }
        }
        
        // Divider before Meal Types section
        if (typeBasedCuisines.isNotEmpty()) {
            Divider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            // Meal Types Section
            Text(
                text = stringResource(R.string.meals_meal_types_headline),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                typeBasedCuisines.forEach { cuisine ->
                    CuisineCard(
                        cuisine = cuisine,
                        onClick = { onCuisineClick(cuisine) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CuisineChip(
    cuisine: Cuisine,
    onClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Image(
                    painter = painterResource(id = cuisine.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
                Column {
                    Text(
                        text = cuisine.getLocalizedName(context),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = cuisine.getLocalizedDescription(context),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MyRecipesPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.my_recipes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
private fun MyRecipesSection(
    recipes: List<Meal>,
    onRecipeClick: (Meal) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) } // Always start collapsed
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "arrow rotation"
    )
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header card - matches cuisine card style
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.my_recipes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recipes_count, recipes.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Always show expand/collapse arrow
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(20.dp)
                                    .graphicsLayer {
                                        rotationZ = rotationAngle
                                    }
                            )
                        }
                    }
                }
            }
        }
        
        // Expanded content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = shrinkVertically(
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + fadeOut(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            // Display recipes in a vertical list
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                recipes.forEach { recipe ->
                    RecipeChip(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeChip(
    recipe: Meal,
    onClick: () -> Unit
) {
    // Collect unique allergens from all ingredients
    val allergens = remember(recipe.ingredients) {
        recipe.ingredients
            .flatMap { it.ingredient.allergens }
            .distinctBy { it.id }
    }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Recipe image
            RecipeImage(
                imagePath = recipe.imagePath,
                contentDescription = recipe.name,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            
            // Recipe info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
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
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Time info on separate lines
                val prepLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_prep)
                val cookLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_cook)
                val minLabel = androidx.compose.ui.res.stringResource(R.string.meal_plan_min)
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    recipe.prepTimeMinutes?.let {
                        Text(
                            text = "$prepLabel: $it $minLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    recipe.cookTimeMinutes?.let {
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

@Composable
private fun CuisineCard(
    cuisine: Cuisine,
    onClick: () -> Unit,
    viewModel: MealsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
    // Check if DLC is purchased
    val isPurchased by viewModel.isDLCPurchased(cuisine.assetPackName ?: "")
        .collectAsState(initial = false)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = cuisine.iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .padding(start = 16.dp, end = 12.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = cuisine.getLocalizedName(context),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cuisine.getLocalizedDescription(context),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Show lock/unlock icon for DLC cuisines in a circular badge
            if (cuisine.isDLC) {
                Surface(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(36.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isPurchased) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = if (isPurchased) "Unlocked" else "Locked",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getCuisineDescription(cuisine: Cuisine): String {
    return when (cuisine) {
        Cuisine.ITALIAN -> stringResource(R.string.cuisine_italian_desc)
        Cuisine.MEXICAN -> stringResource(R.string.cuisine_mexican_desc)
        Cuisine.ASIAN -> stringResource(R.string.cuisine_asian_desc)
        Cuisine.MEDITERRANEAN -> stringResource(R.string.cuisine_mediterranean_desc)
        Cuisine.FRENCH -> stringResource(R.string.cuisine_french_desc)
        Cuisine.BREAD_BAKERY -> stringResource(R.string.cuisine_bread_bakery_desc)
        Cuisine.SOUPS_STEWS -> stringResource(R.string.cuisine_soups_stews_desc)
        Cuisine.VEGETARIAN_VEGAN -> stringResource(R.string.cuisine_vegetarian_vegan_desc)
        Cuisine.MEAT_DISHES -> stringResource(R.string.cuisine_meat_dishes_desc)
        Cuisine.DESSERTS_SWEETS -> stringResource(R.string.cuisine_desserts_sweets_desc)
        Cuisine.TWO_FAST_TWO_HUNGRY -> cuisine.description // DLC cuisines use their description
        else -> cuisine.description
    }
}

/**
 * Creates a premium pack preview for a given DLC cuisine
 */
@Composable
private fun getPremiumPackPreview(cuisine: Cuisine): PremiumPackPreview {
    return when (cuisine) {
        Cuisine.TWO_FAST_TWO_HUNGRY -> PremiumPackPreview(
            cuisine = cuisine,
            recipeNames = listOf(
                stringResource(R.string.premium_2fast_cheese_omelette),
                stringResource(R.string.premium_2fast_chicken_stir_fry),
                stringResource(R.string.premium_2fast_pasta_aglio_e_olio),
                stringResource(R.string.premium_2fast_beef_tacos),
                stringResource(R.string.premium_2fast_egg_fried_rice),
                stringResource(R.string.premium_2fast_cheese_quesadilla),
                stringResource(R.string.premium_2fast_egg_ramen),
                stringResource(R.string.premium_2fast_grilled_cheese),
                stringResource(R.string.premium_2fast_chicken_wrap),
                stringResource(R.string.premium_2fast_coconut_chicken_curry),
                stringResource(R.string.premium_2fast_shrimp_noodles),
                stringResource(R.string.premium_2fast_toast_egg_scramble)
            ),
            price = "$1.99",
            recipeImageUrls = listOf(
                "recipes/images/2fast2hungry/5_minute_omelette.jpg",
                "recipes/images/2fast2hungry/speedy_stir_fry.jpg",
                "recipes/images/2fast2hungry/quick_pasta_aglio_e_olio.jpg",
                "recipes/images/2fast2hungry/10_minute_tacos.jpg",
                "recipes/images/2fast2hungry/fast_fried_rice.jpg",
                "recipes/images/2fast2hungry/express_quesadilla.webp",
                "recipes/images/2fast2hungry/rapid_ramen_bowl.jpg",
                "recipes/images/2fast2hungry/quick_grilled_cheese.jpg",
                "recipes/images/2fast2hungry/speedy_chicken_wrap.jpg",
                "recipes/images/2fast2hungry/15_minute_curry.jpg",
                "recipes/images/2fast2hungry/fast_noodle_bowl.jpg",
                "recipes/images/2fast2hungry/quick_toast_skillet.jpg"
            )
        )
        Cuisine.EASTERN_TRADITIONAL -> PremiumPackPreview(
            cuisine = cuisine,
            recipeNames = listOf(
                stringResource(R.string.premium_recipe_borscht),
                stringResource(R.string.premium_recipe_pierogi),
                stringResource(R.string.premium_recipe_golubtsy),
                stringResource(R.string.premium_recipe_stroganoff),
                stringResource(R.string.premium_recipe_pelmeni),
                stringResource(R.string.premium_recipe_kasha),
                stringResource(R.string.premium_recipe_shchi),
                stringResource(R.string.premium_recipe_kotleti),
                stringResource(R.string.premium_recipe_vareniki),
                stringResource(R.string.premium_recipe_olivier),
                stringResource(R.string.premium_recipe_blini),
                stringResource(R.string.premium_recipe_solyanka)
            ),
            price = "$1.99"
        )
        Cuisine.EXOTIC_TROPICS -> PremiumPackPreview(
            cuisine = cuisine,
            recipeNames = listOf(
                stringResource(R.string.premium_recipe_coconut_curry),
                stringResource(R.string.premium_recipe_mango_sticky_rice),
                stringResource(R.string.premium_recipe_pineapple_fried_rice),
                stringResource(R.string.premium_recipe_plantains),
                stringResource(R.string.premium_recipe_papaya_salad),
                stringResource(R.string.premium_recipe_coconut_rice),
                stringResource(R.string.premium_recipe_tuna_poke),
                stringResource(R.string.premium_recipe_mango_lassi),
                stringResource(R.string.premium_recipe_tropical_fruit_salad),
                stringResource(R.string.premium_recipe_coconut_shrimp),
                stringResource(R.string.premium_recipe_pineapple_salsa),
                stringResource(R.string.premium_recipe_banana_fritters)
            ),
            price = "$1.99"
        )
        else -> PremiumPackPreview(
            cuisine = cuisine,
            recipeNames = listOf(
                "Recipe 1", "Recipe 2", "Recipe 3",
                "Recipe 4", "Recipe 5", "Recipe 6",
                "Recipe 7", "Recipe 8", "Recipe 9",
                "Recipe 10", "Recipe 11", "Recipe 12"
            ),
            price = "$1.99"
        )
    }
}
