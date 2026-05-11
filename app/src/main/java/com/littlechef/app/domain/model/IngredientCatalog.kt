package com.littlechef.app.domain.model

import android.content.Context
import com.littlechef.app.R

/**
 * Common food allergens based on FDA's major food allergens list.
 */
enum class CommonAllergen(val displayName: String) {
    GLUTEN("Gluten"),
    DAIRY("Dairy"),
    EGGS("Eggs"),
    TREE_NUTS("Tree Nuts"),
    PEANUTS("Peanuts"),
    SOY("Soy"),
    FISH("Fish"),
    SHELLFISH("Shellfish"),
    SESAME("Sesame");
    
    fun getLocalizedName(context: Context): String {
        return when (this) {
            GLUTEN -> context.getString(R.string.allergen_gluten)
            DAIRY -> context.getString(R.string.allergen_dairy)
            EGGS -> context.getString(R.string.allergen_eggs)
            TREE_NUTS -> context.getString(R.string.allergen_tree_nuts)
            PEANUTS -> context.getString(R.string.allergen_peanuts)
            SOY -> context.getString(R.string.allergen_soy)
            FISH -> context.getString(R.string.allergen_fish)
            SHELLFISH -> context.getString(R.string.allergen_shellfish)
            SESAME -> context.getString(R.string.allergen_sesame)
        }
    }
}

/**
 * Pre-defined ingredient catalog with translation support.
 */
data class CatalogIngredient(
    val id: String,
    val nameKey: String,
    val defaultUnit: String,
    val alternateUnit: String? = null, // kg for g, L for ml
    val category: IngredientCategory,
    val subcategory: String? = null,
    val allergens: List<CommonAllergen> = emptyList()
)

enum class IngredientCategory(val key: String, val displayName: String) {
    MEAT_POULTRY("meat_poultry", "Meat & Poultry"),
    SEAFOOD("seafood", "Seafood"),
    DAIRY_EGGS("dairy_eggs", "Dairy & Eggs"),
    VEGETABLES("vegetables", "Vegetables"),
    FRUITS("fruits", "Fruits"),
    GRAINS_BREAD("grains_bread", "Grains & Bread"),
    LEGUMES_BEANS("legumes_beans", "Legumes & Beans"),
    NUTS_SEEDS("nuts_seeds", "Nuts & Seeds"),
    OILS_FATS("oils_fats", "Oils & Fats"),
    SPICES_HERBS("spices_herbs", "Spices & Herbs"),
    CONDIMENTS_SAUCES("condiments_sauces", "Condiments & Sauces"),
    SWEETENERS_BAKING("sweeteners_baking", "Sweeteners & Baking"),
    CANNED_PRESERVED("canned_preserved", "Canned & Preserved"),
    BEVERAGES("beverages", "Beverages"),
    SNACKS("snacks", "Snacks & Misc");
    
    fun getLocalizedName(context: Context): String {
        return when (this) {
            MEAT_POULTRY -> context.getString(R.string.ingredient_category_meat_poultry)
            SEAFOOD -> context.getString(R.string.ingredient_category_seafood)
            DAIRY_EGGS -> context.getString(R.string.ingredient_category_dairy_eggs)
            VEGETABLES -> context.getString(R.string.ingredient_category_vegetables)
            FRUITS -> context.getString(R.string.ingredient_category_fruits)
            GRAINS_BREAD -> context.getString(R.string.ingredient_category_grains_bread)
            LEGUMES_BEANS -> context.getString(R.string.ingredient_category_legumes_beans)
            NUTS_SEEDS -> context.getString(R.string.ingredient_category_nuts_seeds)
            OILS_FATS -> context.getString(R.string.ingredient_category_oils_fats)
            SPICES_HERBS -> context.getString(R.string.ingredient_category_spices_herbs)
            CONDIMENTS_SAUCES -> context.getString(R.string.ingredient_category_condiments_sauces)
            SWEETENERS_BAKING -> context.getString(R.string.ingredient_category_sweeteners_baking)
            CANNED_PRESERVED -> context.getString(R.string.ingredient_category_canned_preserved)
            BEVERAGES -> context.getString(R.string.ingredient_category_beverages)
            SNACKS -> context.getString(R.string.ingredient_category_snacks)
        }
    }
}

// Helper function to get localized subcategory name
fun getLocalizedSubcategoryName(context: Context, subcategory: String): String {
    return when (subcategory.lowercase()) {
        "chicken" -> context.getString(R.string.ingredient_subcategory_chicken)
        "beef" -> context.getString(R.string.ingredient_subcategory_beef)
        "pork" -> context.getString(R.string.ingredient_subcategory_pork)
        "lamb" -> context.getString(R.string.ingredient_subcategory_lamb)
        "turkey" -> context.getString(R.string.ingredient_subcategory_turkey)
        "processed" -> context.getString(R.string.ingredient_subcategory_processed)
        "fish" -> context.getString(R.string.ingredient_subcategory_fish)
        "shellfish" -> context.getString(R.string.ingredient_subcategory_shellfish)
        "other" -> context.getString(R.string.ingredient_subcategory_other)
        else -> subcategory
    }
}

object IngredientCatalog {
    
    // ==================== MEAT & POULTRY ====================
    val meatPoultry = listOf(
        // Chicken
        CatalogIngredient("meat_001", "Chicken Breast", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_002", "Chicken Thigh", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_003", "Chicken Drumstick", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_004", "Chicken Wing", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_005", "Whole Chicken", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_006", "Ground Chicken", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        CatalogIngredient("meat_007", "Chicken Liver", "g", "kg", IngredientCategory.MEAT_POULTRY, "Chicken"),
        // Beef
        CatalogIngredient("meat_010", "Beef Steak", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_011", "Ground Beef", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_012", "Beef Ribs", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_013", "Beef Brisket", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_014", "Beef Roast", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_015", "Beef Liver", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_016", "Beef Shank", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_017", "Beef Tenderloin", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_018", "Beef Top Round", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        CatalogIngredient("meat_019", "Chuck Roast", "g", "kg", IngredientCategory.MEAT_POULTRY, "Beef"),
        // Pork
        CatalogIngredient("meat_020", "Pork Chop", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_021", "Ground Pork", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_022", "Pork Belly", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_023", "Pork Ribs", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_024", "Pork Loin", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_025", "Pork Shoulder", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_026", "Bacon", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_027", "Ham", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        // Lamb
        CatalogIngredient("meat_030", "Lamb Chop", "g", "kg", IngredientCategory.MEAT_POULTRY, "Lamb"),
        CatalogIngredient("meat_031", "Ground Lamb", "g", "kg", IngredientCategory.MEAT_POULTRY, "Lamb"),
        CatalogIngredient("meat_032", "Lamb Leg", "g", "kg", IngredientCategory.MEAT_POULTRY, "Lamb"),
        CatalogIngredient("meat_033", "Lamb Shank", "g", "kg", IngredientCategory.MEAT_POULTRY, "Lamb"),
        CatalogIngredient("meat_034", "Lamb", "g", "kg", IngredientCategory.MEAT_POULTRY, "Lamb"),
        // Turkey
        CatalogIngredient("meat_040", "Turkey Breast", "g", "kg", IngredientCategory.MEAT_POULTRY, "Turkey"),
        CatalogIngredient("meat_041", "Ground Turkey", "g", "kg", IngredientCategory.MEAT_POULTRY, "Turkey"),
        CatalogIngredient("meat_042", "Turkey Thigh", "g", "kg", IngredientCategory.MEAT_POULTRY, "Turkey"),
        CatalogIngredient("meat_043", "Whole Turkey", "g", "kg", IngredientCategory.MEAT_POULTRY, "Turkey"),
        // Duck
        CatalogIngredient("meat_050", "Duck Breast", "g", "kg", IngredientCategory.MEAT_POULTRY, "Duck"),
        CatalogIngredient("meat_051", "Duck Leg", "g", "kg", IngredientCategory.MEAT_POULTRY, "Duck"),
        CatalogIngredient("meat_052", "Whole Duck", "g", "kg", IngredientCategory.MEAT_POULTRY, "Duck"),
        // Processed
        CatalogIngredient("meat_060", "Sausage", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_061", "Hot Dogs", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_062", "Salami", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_063", "Pepperoni", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_064", "Italian Sausage", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_065", "Mixed Cured Meats", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_066", "Pancetta", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_067", "Ground Buffalo", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_068", "Bulk Pork Sausage", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_069", "Chorizo", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed"),
        CatalogIngredient("meat_070", "Pork Tenderloin", "g", "kg", IngredientCategory.MEAT_POULTRY, "Pork"),
        CatalogIngredient("meat_071", "Liver Paté", "g", "kg", IngredientCategory.MEAT_POULTRY, "Processed")
    )


    // ==================== SEAFOOD ====================
    val seafood = listOf(
        // Fish
        CatalogIngredient("sea_001", "Salmon", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_002", "Tuna", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_003", "Cod", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_004", "Tilapia", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_005", "Trout", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_006", "Sea Bass", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_007", "Mackerel", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_008", "Sardines", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_009", "Halibut", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_010", "Snapper", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("sea_011", "Anchovies", "g", "kg", IngredientCategory.SEAFOOD, "Fish", listOf(CommonAllergen.FISH)),
        // Shellfish
        CatalogIngredient("sea_020", "Shrimp", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_021", "Crab", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_022", "Lobster", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_023", "Clams", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_024", "Mussels", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_025", "Oysters", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_026", "Scallops", "g", "kg", IngredientCategory.SEAFOOD, "Shellfish", listOf(CommonAllergen.SHELLFISH)),
        // Cephalopods
        CatalogIngredient("sea_030", "Squid", "g", "kg", IngredientCategory.SEAFOOD, "Cephalopods", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("sea_031", "Octopus", "g", "kg", IngredientCategory.SEAFOOD, "Cephalopods", listOf(CommonAllergen.SHELLFISH))
    )

    // ==================== DAIRY & EGGS ====================
    val dairyEggs = listOf(
        // Milk
        CatalogIngredient("dairy_001", "Whole Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_002", "Skim Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_003", "2% Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_004", "Heavy Cream", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_005", "Half and Half", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_006", "Buttermilk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_007", "Evaporated Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_008", "Condensed Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        // Cheese
        CatalogIngredient("dairy_010", "Cheddar Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_011", "Mozzarella", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_012", "Parmesan", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_013", "Swiss Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_014", "Feta Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_015", "Gouda", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_016", "Brie", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_017", "Cream Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_018", "Cottage Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_019", "Ricotta", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_020", "Blue Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_021", "Goat Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_022", "Mascarpone", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_023", "Burrata", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_024", "Provolone", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_025", "Gruyère", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_026", "Pecorino Romano", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_027", "Parmigiano-Reggiano", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_027a", "Monterey Jack", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_028", "Milk", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_029", "Cream", "ml", "L", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY)),
        // Yogurt
        CatalogIngredient("dairy_030", "Plain Yogurt", "g", "kg", IngredientCategory.DAIRY_EGGS, "Yogurt", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_031", "Greek Yogurt", "g", "kg", IngredientCategory.DAIRY_EGGS, "Yogurt", listOf(CommonAllergen.DAIRY)),
        // Cultured Dairy
        CatalogIngredient("dairy_032", "Sour Cream", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cultured Dairy", listOf(CommonAllergen.DAIRY)),
        // Butter
        CatalogIngredient("dairy_040", "Butter", "g", "kg", IngredientCategory.DAIRY_EGGS, "Butter", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_041", "Unsalted Butter", "g", "kg", IngredientCategory.DAIRY_EGGS, "Butter", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_042", "Ghee", "g", "kg", IngredientCategory.DAIRY_EGGS, "Butter", listOf(CommonAllergen.DAIRY)),
        // Eggs
        CatalogIngredient("dairy_050", "Eggs", "pcs", null, IngredientCategory.DAIRY_EGGS, "Eggs", listOf(CommonAllergen.EGGS)),
        CatalogIngredient("dairy_051", "Egg Whites", "pcs", null, IngredientCategory.DAIRY_EGGS, "Eggs", listOf(CommonAllergen.EGGS)),
        CatalogIngredient("dairy_052", "Egg Yolks", "pcs", null, IngredientCategory.DAIRY_EGGS, "Eggs", listOf(CommonAllergen.EGGS)),
        CatalogIngredient("dairy_060", "American Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_061", "Monterey Jack Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_062", "Pepper Jack Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_063", "Mexican Cheese Blend", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_064", "Processed Cheese", "g", "kg", IngredientCategory.DAIRY_EGGS, "Cheese", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("dairy_065", "Instant Dry Milk", "g", "kg", IngredientCategory.DAIRY_EGGS, "Milk", listOf(CommonAllergen.DAIRY))
    )


    // ==================== VEGETABLES ====================
    val vegetables = listOf(
        // Leafy Greens
        CatalogIngredient("veg_001", "Lettuce", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_002", "Spinach", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_003", "Kale", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_004", "Arugula", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_005", "Cabbage", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_006", "Bok Choy", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_007", "Swiss Chard", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        // Root Vegetables
        CatalogIngredient("veg_010", "Potato", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_011", "Carrot", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_012", "Onion", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_013", "Garlic", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_014", "Beet", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_015", "Turnip", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_016", "Radish", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_017", "Sweet Potato", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_018", "Ginger", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        // Nightshades
        CatalogIngredient("veg_020", "Tomato", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_021", "Bell Pepper", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_021a", "Green Bell Pepper", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_021b", "Red Bell Pepper", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_022", "Eggplant", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_023", "Chili Pepper", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_023a", "Green Chili", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_024", "Jalapeño", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_024b", "Jalapeño Pepper", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        // Cruciferous
        CatalogIngredient("veg_030", "Broccoli", "g", "kg", IngredientCategory.VEGETABLES, "Cruciferous"),
        CatalogIngredient("veg_031", "Cauliflower", "g", "kg", IngredientCategory.VEGETABLES, "Cruciferous"),
        CatalogIngredient("veg_032", "Brussels Sprouts", "g", "kg", IngredientCategory.VEGETABLES, "Cruciferous"),
        // Squash
        CatalogIngredient("veg_040", "Zucchini", "g", "kg", IngredientCategory.VEGETABLES, "Squash"),
        CatalogIngredient("veg_041", "Yellow Squash", "g", "kg", IngredientCategory.VEGETABLES, "Squash"),
        CatalogIngredient("veg_042", "Butternut Squash", "g", "kg", IngredientCategory.VEGETABLES, "Squash"),
        CatalogIngredient("veg_043", "Pumpkin", "g", "kg", IngredientCategory.VEGETABLES, "Squash"),
        CatalogIngredient("veg_044", "Cucumber", "g", "kg", IngredientCategory.VEGETABLES, "Squash"),
        // Alliums
        CatalogIngredient("veg_050", "Shallot", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        CatalogIngredient("veg_051", "Leek", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        CatalogIngredient("veg_052", "Scallion", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        CatalogIngredient("veg_053", "Chives", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        // Mushrooms
        CatalogIngredient("veg_060", "Button Mushroom", "g", "kg", IngredientCategory.VEGETABLES, "Mushrooms"),
        CatalogIngredient("veg_061", "Cremini Mushroom", "g", "kg", IngredientCategory.VEGETABLES, "Mushrooms"),
        CatalogIngredient("veg_062", "Portobello", "g", "kg", IngredientCategory.VEGETABLES, "Mushrooms"),
        CatalogIngredient("veg_063", "Shiitake", "g", "kg", IngredientCategory.VEGETABLES, "Mushrooms"),
        // Other Vegetables
        CatalogIngredient("veg_070", "Corn", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_071", "Asparagus", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_072", "Celery", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_073", "Green Beans", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_074", "Snap Peas", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_075", "Artichoke", "g", "kg", IngredientCategory.VEGETABLES, "Other"),
        CatalogIngredient("veg_076", "Baby Spinach", "g", "kg", IngredientCategory.VEGETABLES, "Leafy Greens"),
        CatalogIngredient("veg_077", "Cherry Tomatoes", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_078", "Red Onion", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        CatalogIngredient("veg_079", "Yellow Onion", "g", "kg", IngredientCategory.VEGETABLES, "Alliums"),
        CatalogIngredient("veg_080", "Sliced Pepperoncini", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_081", "Green Chile Peppers", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_082", "Roasted Green Chile Peppers", "g", "kg", IngredientCategory.VEGETABLES, "Nightshades"),
        CatalogIngredient("veg_083", "Jicama", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables"),
        CatalogIngredient("veg_084", "Frozen Hash Browns", "g", "kg", IngredientCategory.VEGETABLES, "Root Vegetables")
    )

    // ==================== FRUITS ====================
    val fruits = listOf(
        // Citrus
        CatalogIngredient("fruit_001", "Orange", "g", "kg", IngredientCategory.FRUITS, "Citrus"),
        CatalogIngredient("fruit_002", "Lemon", "g", "kg", IngredientCategory.FRUITS, "Citrus"),
        CatalogIngredient("fruit_003", "Lime", "g", "kg", IngredientCategory.FRUITS, "Citrus"),
        CatalogIngredient("fruit_004", "Grapefruit", "g", "kg", IngredientCategory.FRUITS, "Citrus"),
        CatalogIngredient("fruit_005", "Tangerine", "g", "kg", IngredientCategory.FRUITS, "Citrus"),
        // Berries
        CatalogIngredient("fruit_010", "Strawberry", "g", "kg", IngredientCategory.FRUITS, "Berries"),
        CatalogIngredient("fruit_011", "Blueberry", "g", "kg", IngredientCategory.FRUITS, "Berries"),
        CatalogIngredient("fruit_012", "Raspberry", "g", "kg", IngredientCategory.FRUITS, "Berries"),
        CatalogIngredient("fruit_013", "Blackberry", "g", "kg", IngredientCategory.FRUITS, "Berries"),
        CatalogIngredient("fruit_014", "Cranberry", "g", "kg", IngredientCategory.FRUITS, "Berries"),
        // Stone Fruits
        CatalogIngredient("fruit_020", "Peach", "g", "kg", IngredientCategory.FRUITS, "Stone Fruits"),
        CatalogIngredient("fruit_021", "Plum", "g", "kg", IngredientCategory.FRUITS, "Stone Fruits"),
        CatalogIngredient("fruit_022", "Cherry", "g", "kg", IngredientCategory.FRUITS, "Stone Fruits"),
        CatalogIngredient("fruit_023", "Apricot", "g", "kg", IngredientCategory.FRUITS, "Stone Fruits"),
        CatalogIngredient("fruit_024", "Nectarine", "g", "kg", IngredientCategory.FRUITS, "Stone Fruits"),
        // Pome Fruits
        CatalogIngredient("fruit_030", "Apple", "g", "kg", IngredientCategory.FRUITS, "Pome Fruits"),
        CatalogIngredient("fruit_031", "Pear", "g", "kg", IngredientCategory.FRUITS, "Pome Fruits"),
        // Tropical
        CatalogIngredient("fruit_040", "Banana", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_041", "Mango", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_042", "Pineapple", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_043", "Papaya", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_044", "Kiwi", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_045", "Coconut", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        CatalogIngredient("fruit_046", "Passion Fruit", "g", "kg", IngredientCategory.FRUITS, "Tropical"),
        // Melons
        CatalogIngredient("fruit_050", "Watermelon", "g", "kg", IngredientCategory.FRUITS, "Melons"),
        CatalogIngredient("fruit_051", "Cantaloupe", "g", "kg", IngredientCategory.FRUITS, "Melons"),
        CatalogIngredient("fruit_052", "Honeydew", "g", "kg", IngredientCategory.FRUITS, "Melons"),
        // Other Fruits
        CatalogIngredient("fruit_060", "Grapes", "g", "kg", IngredientCategory.FRUITS, "Other"),
        CatalogIngredient("fruit_061", "Avocado", "g", "kg", IngredientCategory.FRUITS, "Other"),
        CatalogIngredient("fruit_062", "Pomegranate", "g", "kg", IngredientCategory.FRUITS, "Other"),
        CatalogIngredient("fruit_063", "Fig", "g", "kg", IngredientCategory.FRUITS, "Other")
    )


    // ==================== GRAINS & BREAD ====================
    val grainsBread = listOf(
        // Rice
        CatalogIngredient("grain_001", "White Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        CatalogIngredient("grain_002", "Brown Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        CatalogIngredient("grain_003", "Jasmine Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        CatalogIngredient("grain_004", "Basmati Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        CatalogIngredient("grain_005", "Arborio Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        CatalogIngredient("grain_006", "Wild Rice", "g", "kg", IngredientCategory.GRAINS_BREAD, "Rice"),
        // Pasta
        CatalogIngredient("grain_010", "Spaghetti", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_011", "Penne", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_012", "Fettuccine", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_013", "Macaroni", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_014", "Lasagna Sheets", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_015", "Egg Noodles", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN, CommonAllergen.EGGS)),
        CatalogIngredient("grain_016", "Rice Noodles", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta"),
        // Bread
        CatalogIngredient("grain_020", "White Bread", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_021", "Whole Wheat Bread", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_022", "Sourdough", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_023", "Baguette", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_024", "Pita Bread", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_025", "Tortillas", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_026", "Bagels", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_027", "English Muffins", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        // Cereals
        CatalogIngredient("grain_030", "Oats", "g", "kg", IngredientCategory.GRAINS_BREAD, "Cereals", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_031", "Cornflakes", "g", "kg", IngredientCategory.GRAINS_BREAD, "Cereals"),
        CatalogIngredient("grain_032", "Granola", "g", "kg", IngredientCategory.GRAINS_BREAD, "Cereals", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_033", "Muesli", "g", "kg", IngredientCategory.GRAINS_BREAD, "Cereals", listOf(CommonAllergen.GLUTEN)),
        // Flour
        CatalogIngredient("grain_040", "All-Purpose Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_041", "Whole Wheat Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_042", "Bread Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_043", "Almond Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("grain_044", "Coconut Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour"),
        CatalogIngredient("grain_045", "Cornmeal", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour"),
        CatalogIngredient("grain_046", "Rye Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_047", "Rice Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour"),
        CatalogIngredient("grain_048", "Flaxseed Meal", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour"),
        // Other Grains
        CatalogIngredient("grain_050", "Quinoa", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains"),
        CatalogIngredient("grain_051", "Couscous", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_052", "Bulgur", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_053", "Barley", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_054", "Polenta", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains"),
        CatalogIngredient("grain_055", "Breadcrumbs", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_056", "Biscuits", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN, CommonAllergen.DAIRY)),
        CatalogIngredient("grain_057", "Frozen Puff Pastry", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN, CommonAllergen.DAIRY)),
        CatalogIngredient("grain_058", "Italian Bread", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_059", "Ladyfinger Cookies", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN, CommonAllergen.EGGS, CommonAllergen.DAIRY)),
        CatalogIngredient("grain_060", "Medium Pasta Shells", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_061", "Orzo Pasta", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_062", "Rigatoni Pasta", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_063", "Refrigerated Pizza Dough", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_064", "Bolillo Rolls", "g", "kg", IngredientCategory.GRAINS_BREAD, "Bread", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_065", "Glutinous Rice Flour", "g", "kg", IngredientCategory.GRAINS_BREAD, "Flour"),
        CatalogIngredient("grain_066", "Farro", "g", "kg", IngredientCategory.GRAINS_BREAD, "Other Grains", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("grain_067", "Gnocchi", "g", "kg", IngredientCategory.GRAINS_BREAD, "Pasta", listOf(CommonAllergen.GLUTEN))
    )

    // ==================== LEGUMES & BEANS ====================
    val legumesBeans = listOf(
        // Beans
        CatalogIngredient("legume_001", "Black Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        CatalogIngredient("legume_002", "Kidney Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        CatalogIngredient("legume_003", "Pinto Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        CatalogIngredient("legume_004", "Navy Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        CatalogIngredient("legume_005", "Cannellini Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        CatalogIngredient("legume_006", "Lima Beans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Beans"),
        // Lentils
        CatalogIngredient("legume_010", "Green Lentils", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Lentils"),
        CatalogIngredient("legume_011", "Red Lentils", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Lentils"),
        CatalogIngredient("legume_012", "Brown Lentils", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Lentils"),
        CatalogIngredient("legume_013", "French Lentils", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Lentils"),
        // Peas
        CatalogIngredient("legume_020", "Chickpeas", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Peas"),
        CatalogIngredient("legume_021", "Split Peas", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Peas"),
        CatalogIngredient("legume_022", "Black-Eyed Peas", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Peas"),
        // Soy Products
        CatalogIngredient("legume_030", "Soybeans", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Soy Products", listOf(CommonAllergen.SOY)),
        CatalogIngredient("legume_031", "Tofu", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Soy Products", listOf(CommonAllergen.SOY)),
        CatalogIngredient("legume_031a", "Silken Tofu", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Soy Products", listOf(CommonAllergen.SOY)),
        CatalogIngredient("legume_032", "Tempeh", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Soy Products", listOf(CommonAllergen.SOY)),
        CatalogIngredient("legume_033", "Edamame", "g", "kg", IngredientCategory.LEGUMES_BEANS, "Soy Products", listOf(CommonAllergen.SOY))
    )


    // ==================== NUTS & SEEDS ====================
    val nutsSeeds = listOf(
        // Tree Nuts
        CatalogIngredient("nut_001", "Almonds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_002", "Walnuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_003", "Cashews", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_004", "Pecans", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_005", "Pistachios", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_006", "Hazelnuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_007", "Macadamia Nuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_008", "Brazil Nuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_009", "Pine Nuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Tree Nuts", listOf(CommonAllergen.TREE_NUTS)),
        // Peanuts
        CatalogIngredient("nut_020", "Peanuts", "g", "kg", IngredientCategory.NUTS_SEEDS, "Peanuts", listOf(CommonAllergen.PEANUTS)),
        CatalogIngredient("nut_021", "Peanut Butter", "g", "kg", IngredientCategory.NUTS_SEEDS, "Peanuts", listOf(CommonAllergen.PEANUTS)),
        // Seeds
        CatalogIngredient("nut_030", "Sunflower Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_031", "Pumpkin Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_032", "Chia Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_033", "Flax Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_034", "Sesame Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds", listOf(CommonAllergen.SESAME)),
        CatalogIngredient("nut_035", "Hemp Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_036", "Poppy Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        CatalogIngredient("nut_037", "Caraway Seeds", "g", "kg", IngredientCategory.NUTS_SEEDS, "Seeds"),
        // Nut Butters
        CatalogIngredient("nut_040", "Almond Butter", "g", "kg", IngredientCategory.NUTS_SEEDS, "Nut Butters", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_041", "Cashew Butter", "g", "kg", IngredientCategory.NUTS_SEEDS, "Nut Butters", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("nut_042", "Tahini", "g", "kg", IngredientCategory.NUTS_SEEDS, "Nut Butters", listOf(CommonAllergen.SESAME))
    )

    // ==================== OILS & FATS ====================
    val oilsFats = listOf(
        // Cooking Oils
        CatalogIngredient("oil_001", "Olive Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_033", "Cooking Spray", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_002", "Vegetable Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_003", "Canola Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_004", "Coconut Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_005", "Sesame Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils", listOf(CommonAllergen.SESAME)),
        CatalogIngredient("oil_006", "Avocado Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_007", "Peanut Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils", listOf(CommonAllergen.PEANUTS)),
        CatalogIngredient("oil_008", "Sunflower Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        CatalogIngredient("oil_009", "Grapeseed Oil", "ml", "L", IngredientCategory.OILS_FATS, "Cooking Oils"),
        // Animal Fats
        CatalogIngredient("oil_020", "Lard", "g", "kg", IngredientCategory.OILS_FATS, "Animal Fats"),
        CatalogIngredient("oil_021", "Duck Fat", "g", "kg", IngredientCategory.OILS_FATS, "Animal Fats"),
        CatalogIngredient("oil_022", "Bacon Grease", "g", "kg", IngredientCategory.OILS_FATS, "Animal Fats"),
        // Specialty Oils
        CatalogIngredient("oil_030", "Truffle Oil", "ml", "L", IngredientCategory.OILS_FATS, "Specialty Oils"),
        CatalogIngredient("oil_031", "Walnut Oil", "ml", "L", IngredientCategory.OILS_FATS, "Specialty Oils", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("oil_032", "Flaxseed Oil", "ml", "L", IngredientCategory.OILS_FATS, "Specialty Oils")
    )


    // ==================== SPICES & HERBS ====================
    val spicesHerbs = listOf(
        // Ground Spices
        CatalogIngredient("spice_001", "Black Pepper", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_002", "Paprika", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_002a", "Smoked Paprika", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_003", "Cumin", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_003a", "Ground Cumin", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_004", "Cinnamon", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_005", "Nutmeg", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_006", "Turmeric", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_007", "Chili Powder", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_008", "Cayenne Pepper", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_009", "Garlic Powder", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_010", "Onion Powder", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_011", "Ginger Powder", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_012", "Coriander", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_013", "Cardamom", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_014", "Allspice", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_015", "Cloves", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        // Whole Spices
        CatalogIngredient("spice_020", "Peppercorns", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_021", "Cinnamon Sticks", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_022", "Bay Leaves", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_023", "Star Anise", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_024", "Cumin Seeds", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_025", "Fennel Seeds", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_026", "Mustard Seeds", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        // Fresh Herbs
        CatalogIngredient("spice_030", "Fresh Basil", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_031", "Fresh Parsley", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_032", "Fresh Cilantro", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_032a", "Cilantro", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_033", "Fresh Mint", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_034", "Fresh Rosemary", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_035", "Fresh Thyme", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_036", "Fresh Dill", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_037", "Fresh Sage", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_038", "Curry Leaves", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        // Dried Herbs
        CatalogIngredient("spice_040", "Dried Oregano", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_040a", "Oregano", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_041", "Dried Basil", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_042", "Dried Thyme", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_043", "Dried Rosemary", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_044", "Dried Parsley", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_045", "Italian Seasoning", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        CatalogIngredient("spice_046", "Herbes de Provence", "g", "kg", IngredientCategory.SPICES_HERBS, "Dried Herbs"),
        // Salt & Basics
        CatalogIngredient("spice_050", "Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_051", "Table Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_052", "Sea Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_053", "Kosher Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_054", "Pink Himalayan Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_055", "Vanilla Extract", "ml", "L", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_056", "Red Pepper Flakes", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_057", "White Pepper", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_058", "Garlic Salt", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_059", "Dried Guajillo Chiles", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_060", "Dried Pasilla Chiles", "g", "kg", IngredientCategory.SPICES_HERBS, "Whole Spices"),
        CatalogIngredient("spice_061", "Chipotle Pepper", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_062", "Taco Seasoning", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_063", "Curry Powder", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_064", "Garam Masala", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_065", "Poultry Seasoning", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices"),
        CatalogIngredient("spice_066", "Dashi Granules", "g", "kg", IngredientCategory.SPICES_HERBS, "Salt & Basics"),
        CatalogIngredient("spice_067", "Fresh Chives", "g", "kg", IngredientCategory.SPICES_HERBS, "Fresh Herbs"),
        CatalogIngredient("spice_063", "Fajita Seasoning", "g", "kg", IngredientCategory.SPICES_HERBS, "Ground Spices")
    )


    // ==================== CONDIMENTS & SAUCES ====================
    val condimentsSauces = listOf(
        // Vinegars
        CatalogIngredient("cond_001", "White Vinegar", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        CatalogIngredient("cond_002", "Apple Cider Vinegar", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        CatalogIngredient("cond_003", "Balsamic Vinegar", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        CatalogIngredient("cond_004", "Red Wine Vinegar", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        CatalogIngredient("cond_005", "Rice Vinegar", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        // Asian Sauces
        CatalogIngredient("cond_010", "Soy Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces", listOf(CommonAllergen.SOY, CommonAllergen.GLUTEN)),
        CatalogIngredient("cond_011", "Fish Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces", listOf(CommonAllergen.FISH)),
        CatalogIngredient("cond_012", "Oyster Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces", listOf(CommonAllergen.SHELLFISH)),
        CatalogIngredient("cond_013", "Hoisin Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces", listOf(CommonAllergen.SOY)),
        CatalogIngredient("cond_014", "Teriyaki Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces", listOf(CommonAllergen.SOY, CommonAllergen.GLUTEN)),
        CatalogIngredient("cond_015", "Sriracha", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces"),
        // Fermented & Pastes
        CatalogIngredient("cond_016", "Miso Paste", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Fermented & Pastes", listOf(CommonAllergen.SOY)),
        CatalogIngredient("cond_017", "Wasabi Paste", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Fermented & Pastes"),
        CatalogIngredient("cond_018", "Red Bean Paste", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Asian Sauces"),
        // Western Sauces
        CatalogIngredient("cond_020", "Ketchup", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_021", "Mustard", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_022", "Mayonnaise", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces", listOf(CommonAllergen.EGGS)),
        CatalogIngredient("cond_023", "BBQ Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_024", "Hot Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_025", "Worcestershire Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces", listOf(CommonAllergen.FISH)),
        CatalogIngredient("cond_026", "Ranch Dressing", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces", listOf(CommonAllergen.DAIRY, CommonAllergen.EGGS)),
        CatalogIngredient("cond_027", "Italian Dressing", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_027a", "Italian Salad Dressing", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_028", "Marinara Sauce", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces"),
        CatalogIngredient("cond_029", "Pesto", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Western Sauces", listOf(CommonAllergen.DAIRY, CommonAllergen.TREE_NUTS)),
        // Cooking Liquids
        CatalogIngredient("cond_036", "Coconut Milk", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids"),
        CatalogIngredient("cond_037", "Coconut Cream", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids"),
        CatalogIngredient("cond_038", "Lemon Juice", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids"),
        CatalogIngredient("cond_039", "Lime Juice", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids"),
        // Spreads
        CatalogIngredient("cond_030", "Hummus", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads", listOf(CommonAllergen.SESAME)),
        CatalogIngredient("cond_031", "Guacamole", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads"),
        CatalogIngredient("cond_032", "Salsa", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads"),
        CatalogIngredient("cond_033", "Relish", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads"),
        CatalogIngredient("cond_034", "Jam", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads"),
        CatalogIngredient("cond_035", "Marmalade", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Spreads"),
        CatalogIngredient("cond_040", "Balsamic Glaze", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Vinegars"),
        CatalogIngredient("cond_041", "Lemon Zest", "g", "kg", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids"),
        CatalogIngredient("cond_042", "Pineapple Juice", "ml", "L", IngredientCategory.CONDIMENTS_SAUCES, "Cooking Liquids")
    )

    // ==================== SWEETENERS & BAKING ====================
    val sweetenersBaking = listOf(
        // Sugars
        CatalogIngredient("sweet_001", "White Sugar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Sugars"),
        CatalogIngredient("sweet_002", "Brown Sugar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Sugars"),
        CatalogIngredient("sweet_003", "Powdered Sugar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Sugars"),
        CatalogIngredient("sweet_004", "Raw Sugar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Sugars"),
        CatalogIngredient("sweet_005", "Coconut Sugar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Sugars"),
        // Liquid Sweeteners
        CatalogIngredient("sweet_010", "Honey", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        CatalogIngredient("sweet_011", "Maple Syrup", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        CatalogIngredient("sweet_012", "Agave Nectar", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        CatalogIngredient("sweet_013", "Corn Syrup", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        CatalogIngredient("sweet_014", "Molasses", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        // Baking & Decorating
        CatalogIngredient("sweet_020", "Baking Powder", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_021", "Baking Soda", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_022", "Active Dry Yeast", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_023", "Instant Yeast", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_024", "Cornstarch", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_025", "Cream of Tartar", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_026", "Gelatin", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_027", "Cocoa Powder", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_028", "Chocolate Chips", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("sweet_029", "Food Coloring", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_030", "Shortening", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_031", "Caramel Sauce", "ml", "L", IngredientCategory.SWEETENERS_BAKING, "Liquid Sweeteners"),
        CatalogIngredient("sweet_032", "Bread Machine Yeast", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating"),
        CatalogIngredient("sweet_033", "Sourdough Starter", "g", "kg", IngredientCategory.SWEETENERS_BAKING, "Baking & Decorating", listOf(CommonAllergen.GLUTEN))
    )


    // ==================== CANNED & PRESERVED ====================
    val cannedPreserved = listOf(
        // Canned Vegetables
        CatalogIngredient("can_001", "Canned Tomatoes", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_002", "Tomato Paste", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_003", "Tomato Sauce", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_004", "Canned Corn", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_005", "Canned Peas", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_006", "Canned Mushrooms", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_007", "Canned Artichokes", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_008", "Canned Pumpkin", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_009", "Sun-Dried Tomatoes", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_060", "Tomato Juice", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        // Canned Beans
        CatalogIngredient("can_010", "Canned Black Beans", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        CatalogIngredient("can_011", "Canned Kidney Beans", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        CatalogIngredient("can_012", "Canned Chickpeas", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        CatalogIngredient("can_013", "Canned Pinto Beans", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        CatalogIngredient("can_014", "Canned Baked Beans", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        CatalogIngredient("can_015", "Refried Beans", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Beans"),
        // Canned Fish
        CatalogIngredient("can_020", "Canned Tuna", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("can_021", "Canned Salmon", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("can_022", "Canned Sardines", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fish", listOf(CommonAllergen.FISH)),
        CatalogIngredient("can_023", "Canned Anchovies", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fish", listOf(CommonAllergen.FISH)),
        // Broths & Stocks
        CatalogIngredient("can_030", "Chicken Broth", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Broths & Stocks"),
        CatalogIngredient("can_031", "Beef Broth", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Broths & Stocks"),
        CatalogIngredient("can_032", "Vegetable Broth", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Broths & Stocks"),
        CatalogIngredient("can_033", "Bone Broth", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Broths & Stocks"),
        CatalogIngredient("can_034", "Turkey Broth", "ml", "L", IngredientCategory.CANNED_PRESERVED, "Broths & Stocks"),
        // Pickled
        CatalogIngredient("can_040", "Pickles", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_041", "Olives", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_042", "Capers", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_043", "Sauerkraut", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_044", "Kimchi", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_045", "Pickled Jalapeños", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        // Canned Fruits
        CatalogIngredient("can_050", "Canned Pineapple", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fruits"),
        CatalogIngredient("can_051", "Canned Peaches", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fruits"),
        CatalogIngredient("can_052", "Canned Mandarin Oranges", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Fruits"),
        CatalogIngredient("can_061", "Crushed Tomatoes", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Canned Vegetables"),
        CatalogIngredient("can_062", "Black Olives", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled"),
        CatalogIngredient("can_063", "Green Olives", "g", "kg", IngredientCategory.CANNED_PRESERVED, "Pickled")
    )


    // ==================== BEVERAGES ====================
    val beverages = listOf(
        // Water & Basic
        CatalogIngredient("bev_001", "Water", "ml", "L", IngredientCategory.BEVERAGES, "Water & Basic"),
        CatalogIngredient("bev_002", "Sparkling Water", "ml", "L", IngredientCategory.BEVERAGES, "Water & Basic"),
        CatalogIngredient("bev_003", "Tonic Water", "ml", "L", IngredientCategory.BEVERAGES, "Water & Basic"),
        CatalogIngredient("bev_004", "Club Soda", "ml", "L", IngredientCategory.BEVERAGES, "Water & Basic"),
        // Coffee & Tea
        CatalogIngredient("bev_010", "Coffee Beans", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_011", "Ground Coffee", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_012", "Instant Coffee", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_013", "Black Tea", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_014", "Green Tea", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_015", "Herbal Tea", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_016", "Matcha", "g", "kg", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        // Juices
        CatalogIngredient("bev_020", "Orange Juice", "ml", "L", IngredientCategory.BEVERAGES, "Juices"),
        CatalogIngredient("bev_021", "Apple Juice", "ml", "L", IngredientCategory.BEVERAGES, "Juices"),
        CatalogIngredient("bev_022", "Grape Juice", "ml", "L", IngredientCategory.BEVERAGES, "Juices"),
        CatalogIngredient("bev_023", "Cranberry Juice", "ml", "L", IngredientCategory.BEVERAGES, "Juices"),
        // Plant Milks
        CatalogIngredient("bev_030", "Almond Milk", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks", listOf(CommonAllergen.TREE_NUTS)),
        CatalogIngredient("bev_031", "Oat Milk", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("bev_032", "Soy Milk", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks", listOf(CommonAllergen.SOY)),
        CatalogIngredient("bev_033", "Coconut Milk Beverage", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks"),
        CatalogIngredient("bev_034", "Rice Milk", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks"),
        CatalogIngredient("bev_035", "Cashew Milk", "ml", "L", IngredientCategory.BEVERAGES, "Plant Milks", listOf(CommonAllergen.TREE_NUTS)),
        // Soft Drinks
        CatalogIngredient("bev_040", "Cola", "ml", "L", IngredientCategory.BEVERAGES, "Soft Drinks"),
        CatalogIngredient("bev_041", "Lemonade", "ml", "L", IngredientCategory.BEVERAGES, "Soft Drinks"),
        CatalogIngredient("bev_042", "Iced Tea", "ml", "L", IngredientCategory.BEVERAGES, "Soft Drinks"),
        CatalogIngredient("bev_043", "Energy Drink", "ml", "L", IngredientCategory.BEVERAGES, "Soft Drinks"),
        CatalogIngredient("bev_044", "Sports Drink", "ml", "L", IngredientCategory.BEVERAGES, "Soft Drinks"),
        CatalogIngredient("bev_045", "Brewed Coffee", "ml", "L", IngredientCategory.BEVERAGES, "Coffee & Tea"),
        CatalogIngredient("bev_046", "Rum", "ml", "L", IngredientCategory.BEVERAGES, "Alcoholic"),
        CatalogIngredient("bev_047", "Sherry", "ml", "L", IngredientCategory.BEVERAGES, "Alcoholic"),
        CatalogIngredient("bev_048", "White Wine", "ml", "L", IngredientCategory.BEVERAGES, "Alcoholic"),
        CatalogIngredient("bev_049", "Beer", "ml", "L", IngredientCategory.BEVERAGES, "Alcoholic"),
        CatalogIngredient("bev_050", "Red Wine", "ml", "L", IngredientCategory.BEVERAGES, "Alcoholic")
    )

    // ==================== SNACKS & MISC ====================
    val snacks = listOf(
        // Chips & Crackers
        CatalogIngredient("snack_001", "Potato Chips", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers"),
        CatalogIngredient("snack_002", "Tortilla Chips", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers"),
        CatalogIngredient("snack_003", "Pretzels", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("snack_004", "Crackers", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("snack_005", "Rice Cakes", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers"),
        CatalogIngredient("snack_006", "Popcorn", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers"),
        CatalogIngredient("snack_007", "Pretzel Crisps", "g", "kg", IngredientCategory.SNACKS, "Chips & Crackers"),
        // Chocolate & Candy
        CatalogIngredient("snack_010", "Dark Chocolate", "g", "kg", IngredientCategory.SNACKS, "Chocolate & Candy"),
        CatalogIngredient("snack_011", "Milk Chocolate", "g", "kg", IngredientCategory.SNACKS, "Chocolate & Candy", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("snack_012", "White Chocolate", "g", "kg", IngredientCategory.SNACKS, "Chocolate & Candy", listOf(CommonAllergen.DAIRY)),
        CatalogIngredient("snack_013", "Candy", "g", "kg", IngredientCategory.SNACKS, "Chocolate & Candy"),
        CatalogIngredient("snack_014", "Gummy Bears", "g", "kg", IngredientCategory.SNACKS, "Chocolate & Candy"),
        // Dried Snacks
        CatalogIngredient("snack_020", "Dried Fruit", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        CatalogIngredient("snack_021", "Raisins", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        CatalogIngredient("snack_022", "Dried Cranberries", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        CatalogIngredient("snack_023", "Dried Apricots", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        CatalogIngredient("snack_024", "Dates", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        CatalogIngredient("snack_025", "Beef Jerky", "g", "kg", IngredientCategory.SNACKS, "Dried Snacks"),
        // Bars & Cookies
        CatalogIngredient("snack_030", "Granola Bars", "g", "kg", IngredientCategory.SNACKS, "Bars & Cookies", listOf(CommonAllergen.GLUTEN)),
        CatalogIngredient("snack_031", "Protein Bars", "g", "kg", IngredientCategory.SNACKS, "Bars & Cookies"),
        CatalogIngredient("snack_032", "Cookies", "g", "kg", IngredientCategory.SNACKS, "Bars & Cookies", listOf(CommonAllergen.GLUTEN, CommonAllergen.DAIRY, CommonAllergen.EGGS)),
        CatalogIngredient("snack_033", "Trail Mix", "g", "kg", IngredientCategory.SNACKS, "Bars & Cookies", listOf(CommonAllergen.TREE_NUTS, CommonAllergen.PEANUTS))
    )


    // Get all ingredients
    val allIngredients: List<CatalogIngredient> by lazy {
        meatPoultry + seafood + dairyEggs + vegetables + fruits + grainsBread + 
        legumesBeans + nutsSeeds + oilsFats + spicesHerbs + condimentsSauces + 
        sweetenersBaking + cannedPreserved + beverages + snacks
    }

    // Get ingredients by category
    fun getByCategory(category: IngredientCategory): List<CatalogIngredient> {
        return when (category) {
            IngredientCategory.MEAT_POULTRY -> meatPoultry
            IngredientCategory.SEAFOOD -> seafood
            IngredientCategory.DAIRY_EGGS -> dairyEggs
            IngredientCategory.VEGETABLES -> vegetables
            IngredientCategory.FRUITS -> fruits
            IngredientCategory.GRAINS_BREAD -> grainsBread
            IngredientCategory.LEGUMES_BEANS -> legumesBeans
            IngredientCategory.NUTS_SEEDS -> nutsSeeds
            IngredientCategory.OILS_FATS -> oilsFats
            IngredientCategory.SPICES_HERBS -> spicesHerbs
            IngredientCategory.CONDIMENTS_SAUCES -> condimentsSauces
            IngredientCategory.SWEETENERS_BAKING -> sweetenersBaking
            IngredientCategory.CANNED_PRESERVED -> cannedPreserved
            IngredientCategory.BEVERAGES -> beverages
            IngredientCategory.SNACKS -> snacks
        }
    }

    // Get subcategories for a category
    fun getSubcategories(category: IngredientCategory): List<String> {
        return getByCategory(category)
            .mapNotNull { it.subcategory }
            .distinct()
    }

    // Get ingredients by category and subcategory
    fun getByCategoryAndSubcategory(category: IngredientCategory, subcategory: String): List<CatalogIngredient> {
        return getByCategory(category).filter { it.subcategory == subcategory }
    }

    // Search ingredients by name
    fun search(query: String): List<CatalogIngredient> {
        val lowerQuery = query.lowercase()
        return allIngredients.filter { it.nameKey.lowercase().contains(lowerQuery) }
    }

    // Get ingredient by ID
    fun getById(id: String): CatalogIngredient? {
        return allIngredients.find { it.id == id }
    }

    // Get all categories
    val categories: List<IngredientCategory> = IngredientCategory.entries
}





