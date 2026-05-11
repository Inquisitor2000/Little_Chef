package com.littlechef.app.domain.model

import com.littlechef.app.R

/**
 * Maps categories and subcategories to their drawable resource icons.
 */
object CategoryIcons {
    
    /**
     * Get the drawable resource ID for a category.
     */
    fun getIconForCategory(category: String?): Int {
        return categoryIconMap[category] ?: R.drawable.ic_cat_snacks
    }
    
    /**
     * Get the drawable resource ID for a subcategory.
     */
    fun getIconForSubcategory(subcategory: String?): Int {
        return subcategoryIconMap[subcategory] ?: R.drawable.ic_sub_other
    }
    
    /**
     * Get icon for ingredient - tries subcategory first, then category.
     * Used for displaying ingredient icons in lists.
     */
    fun getIconForIngredient(category: String?, subcategory: String?): Int {
        // Try subcategory first if provided
        if (subcategory != null) {
            subcategoryIconMap[subcategory]?.let { return it }
        }
        
        // Fall back to category
        if (category != null) {
            categoryIconMap[category]?.let { return it }
        }
        
        // Default to generic icon
        return R.drawable.ic_sub_other
    }
    
    // ==================== CATEGORY ICONS ====================
    private val categoryIconMap = mapOf(
        "Meat & Poultry" to R.drawable.ic_cat_meat_poultry,
        "Seafood" to R.drawable.ic_cat_seafood,
        "Dairy & Eggs" to R.drawable.ic_cat_dairy_eggs,
        "Vegetables" to R.drawable.ic_cat_vegetables,
        "Fruits" to R.drawable.ic_cat_fruits,
        "Grains & Bread" to R.drawable.ic_cat_grains_bread,
        "Legumes & Beans" to R.drawable.ic_cat_legumes_beans,
        "Nuts & Seeds" to R.drawable.ic_cat_nuts_seeds,
        "Oils & Fats" to R.drawable.ic_cat_oils_fats,
        "Spices & Herbs" to R.drawable.ic_cat_spices_herbs,
        "Condiments & Sauces" to R.drawable.ic_cat_condiments_sauces,
        "Sweeteners & Baking" to R.drawable.ic_cat_sweeteners_baking,
        "Canned & Preserved" to R.drawable.ic_cat_canned_preserved,
        "Beverages" to R.drawable.ic_cat_beverages,
        "Snacks & Misc" to R.drawable.ic_cat_snacks
    )
    
    // ==================== SUBCATEGORY ICONS ====================
    private val subcategoryIconMap = mapOf(
        // Meat & Poultry subcategories
        "Chicken" to R.drawable.ic_sub_chicken,
        "Beef" to R.drawable.ic_sub_beef,
        "Pork" to R.drawable.ic_sub_pork,
        "Lamb" to R.drawable.ic_sub_lamb,
        "Turkey" to R.drawable.ic_sub_turkey,
        "Duck" to R.drawable.ic_sub_duck,
        "Processed" to R.drawable.ic_sub_processed,
        
        // Seafood subcategories
        "Fish" to R.drawable.ic_sub_fish,
        "Shellfish" to R.drawable.ic_sub_shellfish,
        "Cephalopods" to R.drawable.ic_sub_cephalopods,
        
        // Dairy & Eggs subcategories
        "Milk" to R.drawable.ic_sub_milk,
        "Cheese" to R.drawable.ic_sub_cheese,
        "Yogurt" to R.drawable.ic_sub_yogurt,
        "Cultured Dairy" to R.drawable.ic_sub_cultured_dairy,
        "Butter" to R.drawable.ic_sub_butter,
        "Eggs" to R.drawable.ic_sub_eggs,
        
        // Vegetables subcategories
        "Leafy Greens" to R.drawable.ic_sub_leafy_greens,
        "Root Vegetables" to R.drawable.ic_sub_root_vegetables,
        "Nightshades" to R.drawable.ic_sub_nightshades,
        "Cruciferous" to R.drawable.ic_sub_cruciferous,
        "Squash" to R.drawable.ic_sub_squash,
        "Alliums" to R.drawable.ic_sub_alliums,
        "Mushrooms" to R.drawable.ic_sub_mushrooms,
        "Other" to R.drawable.ic_sub_other,
        
        // Fruits subcategories
        "Citrus" to R.drawable.ic_sub_citrus,
        "Berries" to R.drawable.ic_sub_berries,
        "Stone Fruits" to R.drawable.ic_sub_stone_fruits,
        "Pome Fruits" to R.drawable.ic_sub_pome_fruits,
        "Tropical" to R.drawable.ic_sub_tropical,
        "Melons" to R.drawable.ic_sub_melons,
        
        // Grains & Bread subcategories
        "Rice" to R.drawable.ic_sub_rice,
        "Pasta" to R.drawable.ic_sub_pasta,
        "Bread" to R.drawable.ic_sub_bread,
        "Cereals" to R.drawable.ic_sub_cereals,
        "Flour" to R.drawable.ic_sub_flour,
        "Other Grains" to R.drawable.ic_sub_other_grains,
        
        // Legumes & Beans subcategories
        "Beans" to R.drawable.ic_sub_beans,
        "Lentils" to R.drawable.ic_sub_lentils,
        "Peas" to R.drawable.ic_sub_peas,
        "Soy Products" to R.drawable.ic_sub_soy_products,
        
        // Nuts & Seeds subcategories
        "Tree Nuts" to R.drawable.ic_sub_tree_nuts,
        "Peanuts" to R.drawable.ic_sub_peanuts,
        "Seeds" to R.drawable.ic_sub_seeds,
        "Nut Butters" to R.drawable.ic_sub_nut_butters,
        
        // Oils & Fats subcategories
        "Cooking Oils" to R.drawable.ic_sub_cooking_oils,
        "Animal Fats" to R.drawable.ic_sub_animal_fats,
        "Specialty Oils" to R.drawable.ic_sub_specialty_oils,
        
        // Spices & Herbs subcategories
        "Ground Spices" to R.drawable.ic_sub_ground_spices,
        "Whole Spices" to R.drawable.ic_sub_whole_spices,
        "Fresh Herbs" to R.drawable.ic_sub_fresh_herbs,
        "Dried Herbs" to R.drawable.ic_sub_dried_herbs,
        "Salt & Basics" to R.drawable.ic_sub_salt_basics,
        
        // Condiments & Sauces subcategories
        "Vinegars" to R.drawable.ic_sub_vinegars,
        "Asian Sauces" to R.drawable.ic_sub_asian_sauces,
        "Fermented & Pastes" to R.drawable.ic_sub_fermented_pastes,
        "Western Sauces" to R.drawable.ic_sub_western_sauces,
        "Cooking Liquids" to R.drawable.ic_sub_cooking_liquids,
        "Spreads" to R.drawable.ic_sub_spreads,
        
        // Sweeteners & Baking subcategories
        "Sugars" to R.drawable.ic_sub_sugars,
        "Liquid Sweeteners" to R.drawable.ic_sub_liquid_sweeteners,
        "Baking Essentials" to R.drawable.ic_sub_baking_essentials,
        "Baking & Decorating" to R.drawable.ic_sub_baking_decorating,
        
        // Canned & Preserved subcategories
        "Canned Vegetables" to R.drawable.ic_sub_canned_vegetables,
        "Canned Beans" to R.drawable.ic_sub_canned_beans,
        "Canned Fish" to R.drawable.ic_sub_canned_fish,
        "Broths & Stocks" to R.drawable.ic_sub_broths_stocks,
        "Pickled" to R.drawable.ic_sub_pickled,
        "Canned Fruits" to R.drawable.ic_sub_canned_fruits,
        
        // Beverages subcategories
        "Water & Basic" to R.drawable.ic_sub_water_basic,
        "Coffee & Tea" to R.drawable.ic_sub_coffee_tea,
        "Juices" to R.drawable.ic_sub_juices,
        "Plant Milks" to R.drawable.ic_sub_plant_milks,
        "Soft Drinks" to R.drawable.ic_sub_soft_drinks,
        
        // Snacks & Misc subcategories
        "Chips & Crackers" to R.drawable.ic_sub_chips_crackers,
        "Chocolate & Candy" to R.drawable.ic_sub_chocolate_candy,
        "Dried Snacks" to R.drawable.ic_sub_dried_snacks,
        "Bars & Cookies" to R.drawable.ic_sub_bars_cookies
    )
}
