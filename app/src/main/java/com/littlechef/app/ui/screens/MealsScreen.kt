package com.littlechef.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.littlechef.app.R
import com.littlechef.app.domain.model.Cuisine
import com.littlechef.app.domain.model.Meal
import com.littlechef.app.ui.util.RecipeImage
import com.littlechef.app.ui.util.rememberHapticFeedback

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

    // Preload Italian cuisine (most popular) when screen is first displayed
    LaunchedEffect(Unit) {
        cuisineViewModel.preloadRecipes(Cuisine.ITALIAN)
    }

    val haptic = rememberHapticFeedback()

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
            // My Recipes section - show only when there are user recipes
            if (scrapedMeals.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    MyRecipesSection(
                        recipes = scrapedMeals,
                        onRecipeClick = { meal -> onNavigateToRecipe(meal.id) }
                    )
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
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Split cuisines: first 8 (former-DLC + country-based) and last 5 (meal types)
        val allCuisines = Cuisine.entries
        val topCuisines = allCuisines.take(8) // Two Fast, Eastern, Exotic, Italian, Mexican, Asian, Mediterranean, French
        val typeCuisines = allCuisines.drop(8) // Bread & Bakery, Soups, Veg, Meat, Desserts

        // Cuisines headline
        Text(
            text = stringResource(R.string.meals_cuisines_headline),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            topCuisines.forEach { cuisine ->
                CuisineCard(
                    cuisine = cuisine,
                    onClick = { onCuisineClick(cuisine) }
                )
            }
        }

        // Divider before Meal Types section
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
            typeCuisines.forEach { cuisine ->
                CuisineCard(
                    cuisine = cuisine,
                    onClick = { onCuisineClick(cuisine) }
                )
            }
        }
    }
}

@Composable
private fun CuisineCard(
    cuisine: Cuisine,
    onClick: () -> Unit
) {
    val context = LocalContext.current

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
                    fontWeight = FontWeight.Bold,
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
                .heightIn(min = 140.dp)
                .height(IntrinsicSize.Min)
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
                    overflow = TextOverflow.Ellipsis
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
