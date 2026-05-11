package com.littlechef.app.domain.util

import com.littlechef.app.domain.model.IngredientCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Infers ingredient category from name using keyword matching.
 * 
 * This class provides category inference for custom ingredients that are not
 * found in the catalog. It uses a keyword-based approach to map ingredient
 * names to appropriate categories.
 */
@Singleton
class CategoryInferencer @Inject constructor() {
    
    /**
     * Keyword mapping for category inference.
     * Each category is associated with a list of keywords that commonly appear
     * in ingredient names belonging to that category.
     */
    private val categoryKeywords = mapOf(
        IngredientCategory.MEAT_POULTRY to listOf(
            "chicken", "beef", "pork", "lamb", "turkey", "duck",
            "meat", "steak", "chop", "ribs", "sausage", "bacon", "ham",
            "veal", "goat", "rabbit", "venison", "quail"
        ),
        IngredientCategory.SEAFOOD to listOf(
            "fish", "salmon", "tuna", "cod", "tilapia", "trout",
            "shrimp", "prawn", "crab", "lobster", "clam", "mussel",
            "oyster", "scallop", "squid", "octopus", "anchovy",
            "sardine", "mackerel", "halibut", "sea", "seafood"
        ),
        IngredientCategory.DAIRY_EGGS to listOf(
            "milk", "cheese", "yogurt", "butter", "cream", "egg",
            "dairy", "cheddar", "mozzarella", "parmesan", "feta",
            "ricotta", "cottage", "sour cream", "whey", "buttermilk"
        ),
        IngredientCategory.VEGETABLES to listOf(
            "tomato", "onion", "carrot", "lettuce", "potato", "pepper",
            "cucumber", "broccoli", "spinach", "cabbage", "celery",
            "zucchini", "eggplant", "cauliflower", "kale", "radish",
            "beet", "turnip", "squash", "pumpkin", "mushroom",
            "asparagus", "artichoke", "leek", "chard", "arugula"
        ),
        IngredientCategory.FRUITS to listOf(
            "apple", "banana", "orange", "grape", "strawberry", "lemon",
            "lime", "peach", "pear", "berry", "melon", "mango",
            "pineapple", "watermelon", "cantaloupe", "kiwi", "plum",
            "cherry", "apricot", "fig", "date", "papaya", "guava",
            "pomegranate", "blueberry", "raspberry", "blackberry"
        ),
        IngredientCategory.GRAINS_BREAD to listOf(
            "flour", "rice", "pasta", "bread", "oats", "cereal",
            "wheat", "barley", "quinoa", "noodle", "couscous",
            "bulgur", "rye", "millet", "sorghum", "spelt",
            "tortilla", "pita", "bagel", "roll", "bun", "cracker"
        ),
        IngredientCategory.LEGUMES_BEANS to listOf(
            "bean", "lentil", "chickpea", "pea", "soy", "tofu",
            "kidney", "black bean", "pinto", "navy", "lima",
            "garbanzo", "edamame", "tempeh", "legume"
        ),
        IngredientCategory.NUTS_SEEDS to listOf(
            "nut", "almond", "walnut", "cashew", "peanut", "pecan",
            "pistachio", "hazelnut", "macadamia", "seed", "sunflower",
            "pumpkin seed", "sesame", "chia", "flax", "hemp"
        ),
        IngredientCategory.OILS_FATS to listOf(
            "oil", "olive", "vegetable", "canola", "coconut", "lard",
            "shortening", "ghee", "avocado oil", "sesame oil",
            "peanut oil", "sunflower oil", "grapeseed", "fat"
        ),
        IngredientCategory.SPICES_HERBS to listOf(
            "salt", "pepper", "garlic", "ginger", "basil", "oregano",
            "thyme", "rosemary", "cumin", "paprika", "cinnamon",
            "nutmeg", "clove", "cardamom", "coriander", "turmeric",
            "sage", "parsley", "cilantro", "dill", "mint", "bay",
            "chili", "cayenne", "curry", "spice", "herb", "seasoning"
        ),
        IngredientCategory.CONDIMENTS_SAUCES to listOf(
            "sauce", "ketchup", "mustard", "mayo", "mayonnaise",
            "vinegar", "soy", "worcestershire", "hot sauce", "salsa",
            "relish", "chutney", "pesto", "aioli", "dressing",
            "gravy", "marinade", "glaze", "condiment"
        ),
        IngredientCategory.SWEETENERS_BAKING to listOf(
            "sugar", "honey", "syrup", "chocolate", "cocoa", "vanilla",
            "molasses", "agave", "stevia", "maple", "brown sugar",
            "powdered", "confectioner", "baking powder", "baking soda",
            "yeast", "extract", "gelatin", "cornstarch", "sweetener"
        ),
        IngredientCategory.CANNED_PRESERVED to listOf(
            "canned", "can", "jar", "jarred", "preserved", "pickled",
            "pickle", "jam", "jelly", "preserve", "marmalade"
        ),
        IngredientCategory.BEVERAGES to listOf(
            "water", "juice", "soda", "tea", "coffee", "wine",
            "beer", "liquor", "vodka", "rum", "whiskey", "gin",
            "brandy", "beverage", "drink", "cola", "lemonade",
            "smoothie", "shake", "broth", "stock"
        )
    )
    
    /**
     * Infers the category for a given ingredient name.
     * 
     * The method normalizes the ingredient name (lowercase, trim) and checks
     * if any keyword from the categoryKeywords map appears in the name.
     * Returns the first matching category, or SNACKS as the default if no match is found.
     * 
     * @param name The ingredient name to categorize (case-insensitive)
     * @return The inferred IngredientCategory (defaults to SNACKS if no match)
     */
    fun inferCategory(name: String): IngredientCategory {
        val normalizedName = name.lowercase().trim()
        
        if (normalizedName.isEmpty()) {
            return IngredientCategory.SNACKS
        }
        
        // Check each category's keywords
        for ((category, keywords) in categoryKeywords) {
            for (keyword in keywords) {
                if (normalizedName.contains(keyword)) {
                    return category
                }
            }
        }
        
        // Default to SNACKS if no match found
        return IngredientCategory.SNACKS
    }
}
