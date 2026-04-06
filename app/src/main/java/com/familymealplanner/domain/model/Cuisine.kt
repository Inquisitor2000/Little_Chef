package com.familymealplanner.domain.model

import androidx.annotation.DrawableRes
import com.familymealplanner.R

/**
 * Represents different cuisine types for meal categorization.
 */
enum class Cuisine(
    val displayName: String,
    @DrawableRes val iconRes: Int,
    val description: String
) {
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
            ITALIAN -> context.getString(com.familymealplanner.R.string.cuisine_italian)
            MEXICAN -> context.getString(com.familymealplanner.R.string.cuisine_mexican)
            ASIAN -> context.getString(com.familymealplanner.R.string.cuisine_asian)
            MEDITERRANEAN -> context.getString(com.familymealplanner.R.string.cuisine_mediterranean)
            FRENCH -> context.getString(com.familymealplanner.R.string.cuisine_french)
            BREAD_BAKERY -> context.getString(com.familymealplanner.R.string.cuisine_bread_bakery)
            SOUPS_STEWS -> context.getString(com.familymealplanner.R.string.cuisine_soups_stews)
            VEGETARIAN_VEGAN -> context.getString(com.familymealplanner.R.string.cuisine_vegetarian_vegan)
            MEAT_DISHES -> context.getString(com.familymealplanner.R.string.cuisine_meat_dishes)
            DESSERTS_SWEETS -> context.getString(com.familymealplanner.R.string.cuisine_desserts_sweets)
        }
    }

    fun getLocalizedDescription(context: android.content.Context): String {
        return when (this) {
            ITALIAN -> context.getString(com.familymealplanner.R.string.cuisine_italian_desc)
            MEXICAN -> context.getString(com.familymealplanner.R.string.cuisine_mexican_desc)
            ASIAN -> context.getString(com.familymealplanner.R.string.cuisine_asian_desc)
            MEDITERRANEAN -> context.getString(com.familymealplanner.R.string.cuisine_mediterranean_desc)
            FRENCH -> context.getString(com.familymealplanner.R.string.cuisine_french_desc)
            BREAD_BAKERY -> context.getString(com.familymealplanner.R.string.cuisine_bread_bakery_desc)
            SOUPS_STEWS -> context.getString(com.familymealplanner.R.string.cuisine_soups_stews_desc)
            VEGETARIAN_VEGAN -> context.getString(com.familymealplanner.R.string.cuisine_vegetarian_vegan_desc)
            MEAT_DISHES -> context.getString(com.familymealplanner.R.string.cuisine_meat_dishes_desc)
            DESSERTS_SWEETS -> context.getString(com.familymealplanner.R.string.cuisine_desserts_sweets_desc)
        }
    }

    companion object {
        fun fromDisplayName(name: String): Cuisine? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
