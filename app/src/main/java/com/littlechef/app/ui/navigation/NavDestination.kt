package com.littlechef.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.littlechef.app.R

sealed class NavDestination(
    val route: String,
    @StringRes val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val shortTitleRes: Int? = null // Optional short title for bottom nav
) {
    data object Plan : NavDestination(
        route = "plan",
        titleRes = R.string.nav_plan,
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Outlined.DateRange
    )

    data object Meals : NavDestination(
        route = "meals",
        titleRes = R.string.nav_meals,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    )

    data object Groceries : NavDestination(
        route = "groceries",
        titleRes = R.string.nav_groceries,
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        shortTitleRes = R.string.nav_groceries_short
    )

    data object Pantry : NavDestination(
        route = "pantry",
        titleRes = R.string.nav_pantry,
        selectedIcon = Icons.Filled.Kitchen,
        unselectedIcon = Icons.Outlined.Kitchen
    )

    // Non-bottom nav destinations
    data object ScrapeRecipe : NavDestination(
        route = "scrape_recipe",
        titleRes = R.string.screen_add_recipe,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    )

    data object ManualRecipe : NavDestination(
        route = "manual_recipe",
        titleRes = R.string.screen_add_recipe,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    )

    data object RecipeDetail : NavDestination(
        route = "recipe_detail/{mealId}",
        titleRes = R.string.screen_recipe,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(mealId: String) = "recipe_detail/$mealId"
    }

    data object CuisineMeals : NavDestination(
        route = "cuisine_meals/{cuisineName}",
        titleRes = R.string.screen_cuisine_meals,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(cuisineName: String) = "cuisine_meals/$cuisineName"
    }

    data object BundledRecipeDetail : NavDestination(
        route = "bundled_recipe/{cuisineName}/{recipeId}",
        titleRes = R.string.screen_recipe,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(cuisineName: String, recipeId: String) = "bundled_recipe/$cuisineName/$recipeId"
    }

    data object Suggestion : NavDestination(
        route = "suggestion",
        titleRes = R.string.screen_meal_suggestions,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    )

    data object AddCustomIngredient : NavDestination(
        route = "add_custom_ingredient?initialName={initialName}&ingredientId={ingredientId}",
        titleRes = R.string.screen_add_custom_ingredient,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(initialName: String = "", ingredientId: String = "") = 
            "add_custom_ingredient?initialName=$initialName&ingredientId=$ingredientId"
    }

    data object AddCustomIngredientForGrocery : NavDestination(
        route = "add_custom_ingredient_grocery?initialName={initialName}",
        titleRes = R.string.screen_add_custom_ingredient,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(initialName: String = "") = 
            "add_custom_ingredient_grocery?initialName=$initialName"
    }

    data object AddCustomIngredientForRecipe : NavDestination(
        route = "add_custom_ingredient_recipe?initialName={initialName}",
        titleRes = R.string.screen_add_custom_ingredient,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    ) {
        fun createRoute(initialName: String = "") = 
            "add_custom_ingredient_recipe?initialName=$initialName"
    }

    data object Settings : NavDestination(
        route = "settings",
        titleRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    companion object {
        val bottomNavItems = listOf(Plan, Meals, Groceries, Pantry)
    }
}
