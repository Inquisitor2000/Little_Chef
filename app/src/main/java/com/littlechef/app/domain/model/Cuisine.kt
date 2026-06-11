package com.littlechef.app.domain.model

import androidx.annotation.DrawableRes
import com.littlechef.app.R

/**
 * Represents different cuisine types for meal categorization.
 *
 * @param displayName The display name of the cuisine
 * @param iconRes The drawable resource for the cuisine icon
 * @param description A brief description of the cuisine
 */
enum class Cuisine(
    val displayName: String,
    @DrawableRes val iconRes: Int,
    val description: String
) {
    // Premium cuisines — always at top
    TWO_FAST_TWO_HUNGRY(
        displayName = "Two Fast Two Hungry",
        iconRes = R.drawable.ic_sub_cereals,
        description = "Quick meals when you are in a rush"
    ),
    EASTERN_TRADITIONAL(
        displayName = "Eastern Traditional",
        iconRes = R.drawable.ic_sub_squash,
        description = "Traditional Eastern European dishes"
    ),
    EXOTIC_TROPICS(
        displayName = "Exotic Tropics",
        iconRes = R.drawable.ic_sub_tropical,
        description = "Vibrant tropical flavors"
    ),

    // Country-based cuisines
    ITALIAN(
        displayName = "Italian",
        iconRes = R.drawable.ic_sub_pasta,
        description = "Pasta, pizza, risotto & more"
    ),
    MEXICAN(
        displayName = "Mexican",
        iconRes = R.drawable.ic_sub_beans,
        description = "Tacos, burritos, enchiladas"
    ),
    ASIAN(
        displayName = "Asian",
        iconRes = R.drawable.ic_sub_rice,
        description = "Chinese, Japanese, Thai & more"
    ),
    MEDITERRANEAN(
        displayName = "Mediterranean",
        iconRes = R.drawable.ic_cat_oils_fats,
        description = "Greek, Turkish, Middle Eastern"
    ),
    FRENCH(
        displayName = "French",
        iconRes = R.drawable.ic_sub_bars_cookies,
        description = "Classic & refined dishes"
    ),

    // Meal type cuisines
    BREAD_BAKERY(
        displayName = "Bread & Bakery",
        iconRes = R.drawable.ic_sub_bread,
        description = "Breads, pastries & baked goods"
    ),
    SOUPS_STEWS(
        displayName = "Soups & Stews",
        iconRes = R.drawable.ic_sub_broths_stocks,
        description = "Hearty soups & comforting stews"
    ),
    VEGETARIAN_VEGAN(
        displayName = "Vegetarian & Vegan",
        iconRes = R.drawable.ic_cat_vegetables,
        description = "Plant-based dishes"
    ),
    MEAT_DISHES(
        displayName = "Meat Dishes",
        iconRes = R.drawable.ic_cat_meat_poultry,
        description = "Beef, pork, lamb & more"
    ),
    DESSERTS_SWEETS(
        displayName = "Desserts & Sweets",
        iconRes = R.drawable.ic_sub_chocolate_candy,
        description = "Cakes, cookies & sweet treats"
    );

    fun getLocalizedName(context: android.content.Context): String {
        return when (this) {
            TWO_FAST_TWO_HUNGRY -> context.getString(com.littlechef.app.R.string.cuisine_2fast_2hungry)
            EASTERN_TRADITIONAL -> context.getString(com.littlechef.app.R.string.cuisine_eastern_traditional)
            EXOTIC_TROPICS -> context.getString(com.littlechef.app.R.string.cuisine_exotic_tropics)
            ITALIAN -> context.getString(com.littlechef.app.R.string.cuisine_italian)
            MEXICAN -> context.getString(com.littlechef.app.R.string.cuisine_mexican)
            ASIAN -> context.getString(com.littlechef.app.R.string.cuisine_asian)
            MEDITERRANEAN -> context.getString(com.littlechef.app.R.string.cuisine_mediterranean)
            FRENCH -> context.getString(com.littlechef.app.R.string.cuisine_french)
            BREAD_BAKERY -> context.getString(com.littlechef.app.R.string.cuisine_bread_bakery)
            SOUPS_STEWS -> context.getString(com.littlechef.app.R.string.cuisine_soups_stews)
            VEGETARIAN_VEGAN -> context.getString(com.littlechef.app.R.string.cuisine_vegetarian_vegan)
            MEAT_DISHES -> context.getString(com.littlechef.app.R.string.cuisine_meat_dishes)
            DESSERTS_SWEETS -> context.getString(com.littlechef.app.R.string.cuisine_desserts_sweets)
        }
    }

    fun getLocalizedDescription(context: android.content.Context): String {
        return when (this) {
            TWO_FAST_TWO_HUNGRY -> context.getString(com.littlechef.app.R.string.cuisine_2fast_2hungry_desc)
            EASTERN_TRADITIONAL -> context.getString(com.littlechef.app.R.string.cuisine_eastern_traditional_desc)
            EXOTIC_TROPICS -> context.getString(com.littlechef.app.R.string.cuisine_exotic_tropics_desc)
            ITALIAN -> context.getString(com.littlechef.app.R.string.cuisine_italian_desc)
            MEXICAN -> context.getString(com.littlechef.app.R.string.cuisine_mexican_desc)
            ASIAN -> context.getString(com.littlechef.app.R.string.cuisine_asian_desc)
            MEDITERRANEAN -> context.getString(com.littlechef.app.R.string.cuisine_mediterranean_desc)
            FRENCH -> context.getString(com.littlechef.app.R.string.cuisine_french_desc)
            BREAD_BAKERY -> context.getString(com.littlechef.app.R.string.cuisine_bread_bakery_desc)
            SOUPS_STEWS -> context.getString(com.littlechef.app.R.string.cuisine_soups_stews_desc)
            VEGETARIAN_VEGAN -> context.getString(com.littlechef.app.R.string.cuisine_vegetarian_vegan_desc)
            MEAT_DISHES -> context.getString(com.littlechef.app.R.string.cuisine_meat_dishes_desc)
            DESSERTS_SWEETS -> context.getString(com.littlechef.app.R.string.cuisine_desserts_sweets_desc)
        }
    }

    companion object {
        fun fromDisplayName(name: String): Cuisine? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
