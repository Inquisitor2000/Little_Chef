package com.familymealplanner.ui.screens

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.familymealplanner.R
import com.familymealplanner.domain.model.Cuisine
import com.familymealplanner.domain.model.Meal
import com.familymealplanner.ui.util.RecipeImage

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
    
    // Track initial composition
    LaunchedEffect(Unit) {
        // Composition complete
    }
    
    // Preload Italian cuisine (most popular) when screen is first displayed
    LaunchedEffect(Unit) {
        cuisineViewModel.preloadRecipes(Cuisine.ITALIAN)
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
                        onClick = onNavigateToAddMeal,
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
            contentPadding = PaddingValues(vertical = 16.dp)
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
                    onCuisineClick = onNavigateToCuisine
                )
            }
        }
    }
}

@Composable
private fun CuisinesSection(
    onCuisineClick: (Cuisine) -> Unit
) {
    val cuisines = Cuisine.entries
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.meals_cuisines_headline),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            cuisines.forEachIndexed { index, cuisine ->
                CuisineCard(
                    cuisine = cuisine,
                    onClick = { onCuisineClick(cuisine) }
                )
                
                // Add divider and "Meal Types" headline after French cuisine (index 4)
                if (index == 4) {
                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    
                    Text(
                        text = stringResource(R.string.meals_meal_types_headline),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
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
    var isExpanded by remember { mutableStateOf(recipes.size <= 2) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "arrow rotation"
    )
    
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = recipes.size > 2) { isExpanded = !isExpanded },
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
                    if (recipes.size > 2) {
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
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    
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
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                onClick()
            },
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
                    .padding(end = 16.dp),
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
    }
}
