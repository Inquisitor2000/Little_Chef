package com.littlechef.app.domain.model

/**
 * Represents the category or style of dish
 */
enum class DishCategory(val displayName: String, val emoji: String) {
    PASTA("Pasta", "🍝"),
    SALAD("Salad", "🥗"),
    SOUP("Soup", "🍲"),
    MAIN_COURSE("Main Course", "🍖"),
    APPETIZER("Appetizer", "🥟"),
    SIDE_DISH("Side Dish", "🥔"),
    BREAD("Bread", "🥖"),
    SEAFOOD("Seafood", "🦐"),
    CHICKEN("Chicken", "🍗"),
    BEEF("Beef", "🥩"),
    PORK("Pork", "🥓"),
    VEGETARIAN("Vegetarian", "🥬"),
    RICE_BOWL("Rice Bowl", "🍚"),
    SANDWICH("Sandwich", "🥪"),
    PIZZA("Pizza", "🍕"),
    DESSERT("Dessert", "🍰"),
    BEVERAGE("Beverage", "🥤"),
    BAKED_DISH("Baked Dish", "🥘");
    
    fun getLocalizedName(context: android.content.Context): String {
        return when (this) {
            PASTA -> context.getString(com.littlechef.app.R.string.dish_category_pasta)
            SALAD -> context.getString(com.littlechef.app.R.string.dish_category_salad)
            SOUP -> context.getString(com.littlechef.app.R.string.dish_category_soup)
            MAIN_COURSE -> context.getString(com.littlechef.app.R.string.dish_category_main_course)
            APPETIZER -> context.getString(com.littlechef.app.R.string.dish_category_appetizer)
            SIDE_DISH -> context.getString(com.littlechef.app.R.string.dish_category_side_dish)
            BREAD -> context.getString(com.littlechef.app.R.string.dish_category_bread)
            SEAFOOD -> context.getString(com.littlechef.app.R.string.dish_category_seafood)
            CHICKEN -> context.getString(com.littlechef.app.R.string.dish_category_chicken)
            BEEF -> context.getString(com.littlechef.app.R.string.dish_category_beef)
            PORK -> context.getString(com.littlechef.app.R.string.dish_category_pork)
            VEGETARIAN -> context.getString(com.littlechef.app.R.string.dish_category_vegetarian)
            RICE_BOWL -> context.getString(com.littlechef.app.R.string.dish_category_rice_bowl)
            SANDWICH -> context.getString(com.littlechef.app.R.string.dish_category_sandwich)
            PIZZA -> context.getString(com.littlechef.app.R.string.dish_category_pizza)
            DESSERT -> context.getString(com.littlechef.app.R.string.dish_category_dessert)
            BEVERAGE -> context.getString(com.littlechef.app.R.string.dish_category_beverage)
            BAKED_DISH -> context.getString(com.littlechef.app.R.string.dish_category_baked_dish)
        }
    }
}
