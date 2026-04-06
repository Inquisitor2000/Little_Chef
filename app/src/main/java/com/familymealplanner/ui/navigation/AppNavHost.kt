package com.familymealplanner.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.familymealplanner.data.preferences.OnboardingPreferences
import com.familymealplanner.domain.model.Cuisine
import com.familymealplanner.ui.screens.BundledRecipeDetailScreen
import com.familymealplanner.ui.screens.CuisineMealsScreen
import com.familymealplanner.ui.screens.GroceriesScreen
import com.familymealplanner.ui.screens.ManualRecipeScreen
import com.familymealplanner.ui.screens.MealPlanDetailScreen
import com.familymealplanner.ui.screens.MealsScreen
import com.familymealplanner.ui.screens.PantryScreen
import com.familymealplanner.ui.screens.PlanScreen
import com.familymealplanner.ui.screens.RecipeDetailScreen
import com.familymealplanner.ui.screens.ScrapeRecipeScreen
import com.familymealplanner.ui.screens.SettingsScreen
import com.familymealplanner.ui.screens.SuggestionScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    val onboardingPreferences: OnboardingPreferences
) : ViewModel() {
    val isFirstLaunchAfterOnboarding: StateFlow<Boolean?> = onboardingPreferences.isFirstLaunchAfterOnboarding
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null // null means we're still loading
        )
    
    suspend fun clearFirstLaunchFlag() {
        onboardingPreferences.clearFirstLaunchFlag()
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navViewModel: NavViewModel = hiltViewModel()
) {
    val isFirstLaunchAfterOnboarding by navViewModel.isFirstLaunchAfterOnboarding.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    Log.d("AppNavHost", "Recomposition - isFirstLaunchAfterOnboarding: $isFirstLaunchAfterOnboarding")
    
    // Wait for the preference to load before determining start destination
    if (isFirstLaunchAfterOnboarding == null) {
        Log.d("AppNavHost", "Waiting for preference to load...")
        // Show empty box while loading the preference
        Box(modifier = modifier)
        return
    }
    
    // Remember the initial start destination - use Unit as key so it NEVER changes after first composition
    val startDestination = remember(Unit) {
        val destination = if (isFirstLaunchAfterOnboarding == true) {
            NavDestination.Meals.route
        } else {
            NavDestination.Plan.route
        }
        Log.d("AppNavHost", "Start destination determined (ONCE): $destination (firstLaunch: $isFirstLaunchAfterOnboarding)")
        destination
    }
    
    // Clear the flag when user navigates to any screen (but skip the initial navigation)
    LaunchedEffect(navController) {
        Log.d("AppNavHost", "Setting up navigation listener")
        var isInitialNavigation = true
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("AppNavHost", "Navigation changed to: ${destination.route}, firstLaunchFlag: $isFirstLaunchAfterOnboarding, isInitial: $isInitialNavigation")
            if (isInitialNavigation) {
                Log.d("AppNavHost", "Skipping initial navigation event")
                isInitialNavigation = false
                return@addOnDestinationChangedListener
            }
            if (isFirstLaunchAfterOnboarding == true) {
                Log.d("AppNavHost", "User navigated - clearing first launch flag")
                // User has interacted with navigation, clear the flag
                coroutineScope.launch {
                    navViewModel.clearFirstLaunchFlag()
                    Log.d("AppNavHost", "First launch flag cleared")
                }
            }
        }
    }
    
    Log.d("AppNavHost", "Creating NavHost with startDestination: $startDestination")
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavDestination.Plan.route) {
            PlanScreen(
                onNavigateToMealPlanDetail = { mealPlanId ->
                    navController.navigate("meal_plan_detail/$mealPlanId")
                },
                onNavigateToCuisine = { cuisine ->
                    navController.navigate(NavDestination.CuisineMeals.createRoute(cuisine.name))
                },
                onNavigateToSuggestion = {
                    navController.navigate(NavDestination.Suggestion.route)
                }
            )
        }
        composable(NavDestination.Meals.route) {
            MealsScreen(
                onNavigateToCuisine = { cuisine ->
                    navController.navigate(NavDestination.CuisineMeals.createRoute(cuisine.name))
                },
                onNavigateToAddMeal = {
                    navController.navigate(NavDestination.ScrapeRecipe.route)
                },
                onNavigateToRecipe = { mealId ->
                    navController.navigate(NavDestination.RecipeDetail.createRoute(mealId))
                }
            )
        }
        composable(NavDestination.ScrapeRecipe.route) {
            ScrapeRecipeScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecipeSaved = { navController.popBackStack() },
                onNavigateToSettings = { 
                    navController.navigate(NavDestination.Settings.route)
                },
                onNavigateToManualRecipe = {
                    navController.navigate(NavDestination.ManualRecipe.route)
                }
            )
        }
        composable(NavDestination.ManualRecipe.route) { backStackEntry ->
            val viewModel: com.familymealplanner.ui.screens.ManualRecipeViewModel = hiltViewModel()
            
            // Listen for custom ingredient data from AddCustomIngredientForRecipe
            val customIngredientName = backStackEntry.savedStateHandle.get<String>("custom_ingredient_name")
            val customIngredientQuantity = backStackEntry.savedStateHandle.get<Double>("custom_ingredient_quantity")
            val customIngredientUnit = backStackEntry.savedStateHandle.get<String>("custom_ingredient_unit")
            
            LaunchedEffect(customIngredientName, customIngredientQuantity, customIngredientUnit) {
                if (customIngredientName != null && customIngredientQuantity != null && customIngredientUnit != null) {
                    val currentIngredientCount = viewModel.state.value.ingredients.size
                    viewModel.addIngredient()
                    viewModel.updateIngredient(
                        currentIngredientCount,
                        com.familymealplanner.ui.screens.ManualRecipeIngredient(
                            name = customIngredientName,
                            quantity = customIngredientQuantity,
                            unit = customIngredientUnit
                        )
                    )
                    // Clear the saved state
                    backStackEntry.savedStateHandle.remove<String>("custom_ingredient_name")
                    backStackEntry.savedStateHandle.remove<Double>("custom_ingredient_quantity")
                    backStackEntry.savedStateHandle.remove<String>("custom_ingredient_unit")
                }
            }
            
            // Clean up saved state when leaving this screen
            androidx.compose.runtime.DisposableEffect(Unit) {
                onDispose {
                    backStackEntry.savedStateHandle.remove<String>("custom_ingredient_name")
                    backStackEntry.savedStateHandle.remove<Double>("custom_ingredient_quantity")
                    backStackEntry.savedStateHandle.remove<String>("custom_ingredient_unit")
                }
            }
            
            ManualRecipeScreen(
                onNavigateBack = { navController.popBackStack() },
                onRecipeSaved = { 
                    // Navigate to Meals screen and clear back stack
                    navController.navigate(NavDestination.Meals.route) {
                        popUpTo(NavDestination.Meals.route) { inclusive = true }
                    }
                },
                onNavigateToCustomIngredient = { initialName ->
                    navController.navigate(NavDestination.AddCustomIngredientForRecipe.createRoute(initialName))
                },
                viewModel = viewModel
            )
        }
        composable(
            route = NavDestination.RecipeDetail.route,
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            RecipeDetailScreen(
                mealId = mealId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { editMealId ->
                    navController.navigate("edit_recipe/$editMealId")
                },
                onNavigateToPlan = {
                    navController.navigate(NavDestination.Plan.route) {
                        popUpTo(NavDestination.Meals.route)
                    }
                },
                onNavigateToGroceries = {
                    navController.navigate(NavDestination.Groceries.route) {
                        popUpTo(NavDestination.Meals.route)
                    }
                }
            )
        }
        composable(
            route = "edit_recipe/{mealId}",
            arguments = listOf(navArgument("mealId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: return@composable
            com.familymealplanner.ui.screens.EditRecipeScreen(
                mealId = mealId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavDestination.CuisineMeals.route,
            arguments = listOf(navArgument("cuisineName") { type = NavType.StringType })
        ) { backStackEntry ->
            val cuisineName = backStackEntry.arguments?.getString("cuisineName") ?: return@composable
            val cuisine = Cuisine.valueOf(cuisineName)
            CuisineMealsScreen(
                cuisine = cuisine,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRecipe = { recipeId ->
                    navController.navigate(NavDestination.BundledRecipeDetail.createRoute(cuisineName, recipeId))
                }
            )
        }
        composable(
            route = NavDestination.BundledRecipeDetail.route,
            arguments = listOf(
                navArgument("cuisineName") { type = NavType.StringType },
                navArgument("recipeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cuisineName = backStackEntry.arguments?.getString("cuisineName") ?: return@composable
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: return@composable
            val cuisine = Cuisine.valueOf(cuisineName)
            BundledRecipeDetailScreen(
                cuisine = cuisine,
                recipeId = recipeId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPlan = { 
                    navController.navigate(NavDestination.Plan.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToGroceries = {
                    navController.navigate(NavDestination.Groceries.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(NavDestination.Groceries.route) {
            GroceriesScreen(
                onNavigateToAddCustomIngredient = { initialName ->
                    navController.navigate(NavDestination.AddCustomIngredientForGrocery.createRoute(initialName))
                }
            )
        }
        composable(NavDestination.Pantry.route) {
            PantryScreen(
                onNavigateToAddCustomIngredient = { ingredientIdOrName ->
                    // If it looks like an ID (not empty and not a simple name), pass it as ingredientId
                    // Otherwise pass it as initialName
                    if (ingredientIdOrName.isNotEmpty() && ingredientIdOrName.length > 10) {
                        // Likely an ingredient ID for editing
                        navController.navigate(NavDestination.AddCustomIngredient.createRoute("", ingredientIdOrName))
                    } else {
                        // Initial name for creating new ingredient
                        navController.navigate(NavDestination.AddCustomIngredient.createRoute(ingredientIdOrName))
                    }
                }
            )
        }
        composable(
            route = NavDestination.AddCustomIngredient.route,
            arguments = listOf(
                navArgument("initialName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("ingredientId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialName = backStackEntry.arguments?.getString("initialName") ?: ""
            val ingredientId = backStackEntry.arguments?.getString("ingredientId") ?: ""
            val pantryViewModel: com.familymealplanner.ui.screens.PantryViewModel = hiltViewModel()
            val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
            
            com.familymealplanner.ui.screens.AddCustomIngredientScreen(
                initialName = initialName,
                ingredientId = ingredientId,
                onNavigateBack = { navController.popBackStack() },
                onConfirm = { name, quantity, unit, category, subcategory, allergens ->
                    // Launch coroutine to wait for ingredient to be added before navigating back
                    coroutineScope.launch {
                        pantryViewModel.addIngredient(name, quantity, unit, category, subcategory, allergens)
                        // Small delay to ensure database transaction completes and Flow emits
                        kotlinx.coroutines.delay(100)
                        navController.popBackStack()
                    }
                },
                onUpdate = { id, name, unit, category, subcategory, allergens ->
                    coroutineScope.launch {
                        pantryViewModel.updateCustomIngredient(id, name, unit, category, subcategory, allergens)
                        kotlinx.coroutines.delay(100)
                        navController.popBackStack()
                    }
                },
                preferences = navViewModel.onboardingPreferences,
                ingredientRepository = pantryViewModel.ingredientRepository,
                translationSystem = pantryViewModel.getTranslationSystem()
            )
        }
        composable(
            route = NavDestination.AddCustomIngredientForGrocery.route,
            arguments = listOf(
                navArgument("initialName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialName = backStackEntry.arguments?.getString("initialName") ?: ""
            val groceriesViewModel: com.familymealplanner.ui.screens.GroceriesViewModel = hiltViewModel()
            
            com.familymealplanner.ui.screens.AddCustomIngredientScreen(
                initialName = initialName,
                ingredientId = "",
                onNavigateBack = { navController.popBackStack() },
                onConfirm = { name, quantity, unit, category, subcategory, allergens ->
                    // Add to grocery list instead of pantry
                    groceriesViewModel.addCustomIngredientToGroceryList(name, quantity, unit, category, subcategory, allergens)
                    navController.popBackStack()
                },
                onUpdate = null, // No update for grocery list items
                preferences = navViewModel.onboardingPreferences,
                ingredientRepository = groceriesViewModel.ingredientRepository,
                translationSystem = groceriesViewModel.getTranslationSystem()
            )
        }
        composable(
            route = NavDestination.AddCustomIngredientForRecipe.route,
            arguments = listOf(
                navArgument("initialName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val initialName = backStackEntry.arguments?.getString("initialName") ?: ""
            val pantryViewModel: com.familymealplanner.ui.screens.PantryViewModel = hiltViewModel()
            
            com.familymealplanner.ui.screens.AddCustomIngredientScreen(
                initialName = initialName,
                ingredientId = "",
                onNavigateBack = { navController.popBackStack() },
                onConfirm = { name, quantity, unit, category, subcategory, allergens ->
                    // Save custom ingredient to database first (with quantity 0 since it's for recipe, not pantry)
                    pantryViewModel.addIngredient(name, 0.0, unit, category, subcategory, allergens)
                    
                    // Then save ingredient data to previous back stack entry for recipe
                    navController.previousBackStackEntry?.savedStateHandle?.set("custom_ingredient_name", name)
                    navController.previousBackStackEntry?.savedStateHandle?.set("custom_ingredient_quantity", quantity)
                    navController.previousBackStackEntry?.savedStateHandle?.set("custom_ingredient_unit", unit)
                    navController.popBackStack()
                },
                onUpdate = null,
                preferences = navViewModel.onboardingPreferences,
                ingredientRepository = pantryViewModel.ingredientRepository,
                translationSystem = pantryViewModel.getTranslationSystem()
            )
        }
        composable(
            route = "meal_plan_detail/{mealPlanId}",
            arguments = listOf(navArgument("mealPlanId") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealPlanId = backStackEntry.arguments?.getString("mealPlanId") ?: return@composable
            MealPlanDetailScreen(
                mealPlanId = mealPlanId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToGroceries = {
                    navController.navigate(NavDestination.Groceries.route) {
                        popUpTo(NavDestination.Plan.route)
                    }
                }
            )
        }
        composable(NavDestination.Suggestion.route) {
            SuggestionScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToUserMeal = { mealId ->
                    navController.navigate(NavDestination.RecipeDetail.createRoute(mealId))
                },
                onNavigateToBundledRecipe = { cuisine, recipeId ->
                    navController.navigate(NavDestination.BundledRecipeDetail.createRoute(cuisine.name, recipeId))
                }
            )
        }
        composable(NavDestination.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
