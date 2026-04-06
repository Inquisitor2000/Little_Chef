package com.familymealplanner.domain.model

/**
 * Ingredient substitution mappings for when users don't have all required ingredients.
 * Maps an ingredient to its substitute.
 */
object IngredientSubstitutions {

    /**
     * Map of ingredient name to its substitute
     */
    private val substitutions: Map<String, String> = mapOf(
        // Meat & Poultry
        "Chicken Breast" to "Chicken Thigh",
        "Chicken Thigh" to "Chicken Drumstick",
        "Chicken Wing" to "Chicken Drumstick",
        "Whole Chicken" to "Chicken Breast",
        "Ground Chicken" to "Ground Turkey",
        "Chicken Liver" to "Beef Liver",
        "Beef Steak" to "Beef Roast",
        "Ground Beef" to "Ground Pork",
        "Beef Ribs" to "Pork Ribs",
        "Beef Brisket" to "Beef Roast",
        "Beef Shank" to "Lamb Shank",
        "Pork Chop" to "Pork Loin",
        "Pork Belly" to "Bacon",
        "Pork Shoulder" to "Pork Loin",
        "Pork Tenderloin" to "Pork Chop",
        "Ham" to "Bacon",
        "Bacon" to "Ham",
        "Lamb Chop" to "Lamb Leg",
        "Ground Lamb" to "Ground Beef",
        "Turkey Breast" to "Chicken Breast",
        "Turkey Thigh" to "Chicken Thigh",
        "Whole Turkey" to "Whole Chicken",
        "Duck Breast" to "Duck Leg",
        "Whole Duck" to "Whole Chicken",
        "Sausage" to "Hot Dogs",
        "Bulk Pork Sausage" to "Ground Pork",
        "Chorizo" to "Bulk Pork Sausage",
        "Ground Buffalo" to "Ground Beef",
        
        // Seafood
        "Salmon" to "Trout",
        "Tuna" to "Mackerel",
        "Cod" to "Halibut",
        "Tilapia" to "Snapper",
        "Sea Bass" to "Snapper",
        "Sardines" to "Mackerel",
        "Shrimp" to "Scallops",
        "Crab" to "Lobster",
        "Clams" to "Mussels",
        "Oysters" to "Mussels",
        "Squid" to "Octopus",

        // Dairy & Eggs - Milk & Cream
        "Milk" to "Skim Milk",
        "Whole Milk" to "2% Milk",
        "2% Milk" to "Skim Milk",
        "Skim Milk" to "Milk",
        "Almond Milk" to "Milk",
        "Heavy Cream" to "Whole Milk",
        "Buttermilk" to "Plain Yogurt",
        "Evaporated Milk" to "Whole Milk",
        "Condensed Milk" to "Whole Milk",
        "Instant Dry Milk" to "Whole Milk",
        
        // Dairy & Eggs - Cheese
        "Cheddar Cheese" to "Gouda",
        "Mozzarella" to "Swiss Cheese",
        "Monterey Jack Cheese" to "Mozzarella",
        "Feta Cheese" to "Goat Cheese",
        "Brie" to "Cream Cheese",
        "Ricotta" to "Cottage Cheese",
        "Cream Cheese" to "Ricotta",
        "Blue Cheese" to "Goat Cheese",
        "American Cheese" to "Cheddar Cheese",
        "Mexican Cheese Blend" to "Cheddar Cheese",
        "Pepper Jack Cheese" to "Monterey Jack Cheese",
        "Processed Cheese" to "Cheddar Cheese",
        
        // Dairy & Eggs - Yogurt & Creams
        "Greek Yogurt" to "Plain Yogurt",
        "Sour Cream" to "Greek Yogurt",
        "Mayonnaise" to "Sour Cream",
        
        // Dairy & Eggs - Butter & Fats
        "Butter" to "Unsalted Butter",
        "Unsalted Butter" to "Ghee",
        "Shortening" to "Butter",
        
        // Dairy & Eggs - Eggs
        "Egg Whites" to "Eggs",
        "Egg Yolks" to "Eggs",
        
        // Vegetables - Leafy Greens
        "Lettuce" to "Spinach",
        "Kale" to "Swiss Chard",
        "Arugula" to "Spinach",
        "Bok Choy" to "Cabbage",
        
        // Vegetables - Root Vegetables
        "Potato" to "Sweet Potato",
        "Sweet Potato" to "Potato",
        "Onion" to "Shallot",
        "Red Onion" to "Onion",
        "Scallion" to "Onion",
        "Garlic" to "Shallot",
        "Beet" to "Turnip",
        "Ginger" to "Garlic",
        "Carrot" to "Celery",
        "Celery" to "Carrot",
        "Frozen Hash Browns" to "Potato",
        
        // Vegetables - Nightshades
        "Tomato" to "Bell Pepper",
        "Cherry Tomatoes" to "Tomato",
        "Canned Tomatoes" to "Tomato",
        "Chili Pepper" to "Jalapeño",
        "Jalapeño" to "Chili Pepper",
        "Eggplant" to "Zucchini",
        "Bell Pepper" to "Tomato",
        
        // Vegetables - Cruciferous
        "Broccoli" to "Cauliflower",
        "Brussels Sprouts" to "Cabbage",
        
        // Vegetables - Squash
        "Zucchini" to "Yellow Squash",
        "Yellow Squash" to "Zucchini",
        "Butternut Squash" to "Pumpkin",
        "Cucumber" to "Zucchini",
        "Jicama" to "Cucumber",
        
        // Vegetables - Alliums
        "Leek" to "Scallion",
        "Chives" to "Scallion",
        
        // Vegetables - Mushrooms
        "Button Mushroom" to "Cremini Mushroom",
        "Portobello" to "Shiitake",
        
        // Vegetables - Other
        "Green Beans" to "Snap Peas",
        "Celery" to "Asparagus",
        "Cabbage" to "Lettuce",
        "Spinach" to "Lettuce",
        "Avocado" to "Olive Oil",
        "Guacamole" to "Avocado",

        // Fruits - Citrus
        "Lemon" to "Lime",
        "Lemon Juice" to "Lime",
        "Orange" to "Tangerine",
        "Orange Juice" to "Orange",
        
        // Fruits - Berries
        "Strawberry" to "Raspberry",
        "Blueberry" to "Blackberry",
        "Cranberry" to "Dried Cranberries",
        
        // Fruits - Stone Fruits
        "Peach" to "Nectarine",
        "Plum" to "Apricot",
        
        // Fruits - Pome Fruits
        "Apple" to "Pear",
        
        // Fruits - Tropical
        "Banana" to "Mango",
        "Pineapple" to "Papaya",
        "Kiwi" to "Passion Fruit",
        
        // Fruits - Melons
        "Watermelon" to "Cantaloupe",
        
        // Fruits - Other
        "Grapes" to "Pomegranate",
        "Fig" to "Dates",
        
        // Grains & Bread - Rice
        "White Rice" to "Jasmine Rice",
        "Brown Rice" to "White Rice",
        "Basmati Rice" to "Jasmine Rice",
        "Arborio Rice" to "White Rice",
        
        // Grains & Bread - Pasta
        "Spaghetti" to "Fettuccine",
        "Penne" to "Macaroni",
        "Lasagna Sheets" to "Fettuccine",
        
        // Grains & Bread - Bread
        "White Bread" to "Whole Wheat Bread",
        "Baguette" to "Sourdough",
        "Pita Bread" to "Tortillas",
        "Tortillas" to "Pita Bread",
        "Bagels" to "English Muffins",
        "Bolillo Rolls" to "White Bread",
        "Refrigerated Pizza Dough" to "White Bread",
        
        // Grains & Bread - Cereals
        "Oats" to "Muesli",
        "Granola" to "Muesli",
        
        // Grains & Bread - Flour
        "All-Purpose Flour" to "Bread Flour",
        "Whole Wheat Flour" to "Bread Flour",
        "Almond Flour" to "Coconut Flour",
        
        // Grains & Bread - Other Grains
        "Quinoa" to "Bulgur",
        "Couscous" to "Bulgur",
        "Polenta" to "Cornmeal",
        "Breadcrumbs" to "Crushed Crackers",

        // Legumes & Beans
        "Black Beans" to "Kidney Beans",
        "Canned Black Beans" to "Black Beans",
        "Pinto Beans" to "Navy Beans",
        "Kidney Beans" to "Pinto Beans",
        "Refried Beans" to "Pinto Beans",
        "Cannellini Beans" to "Navy Beans",
        "Green Lentils" to "Brown Lentils",
        "Red Lentils" to "Brown Lentils",
        "Chickpeas" to "Black-Eyed Peas",
        "Split Peas" to "Lentils",
        "Tofu" to "Tempeh",
        "Edamame" to "Soybeans",
        
        // Nuts & Seeds - Tree Nuts
        "Almonds" to "Cashews",
        "Walnuts" to "Pecans",
        "Pecans" to "Walnuts",
        "Pistachios" to "Cashews",
        "Pine Nuts" to "Cashews",
        
        // Nuts & Seeds - Seeds
        "Sunflower Seeds" to "Pumpkin Seeds",
        "Chia Seeds" to "Flax Seeds",
        "Sesame Seeds" to "Poppy Seeds",
        
        // Nuts & Seeds - Nut Butters
        "Almond Butter" to "Cashew Butter",
        "Tahini" to "Sesame Seeds",
        
        // Oils & Fats
        "Olive Oil" to "Avocado Oil",
        "Vegetable Oil" to "Canola Oil",
        "Canola Oil" to "Vegetable Oil",
        "Sunflower Oil" to "Grapeseed Oil",
        "Grapeseed Oil" to "Sunflower Oil",
        "Cooking Spray" to "Vegetable Oil",
        "Lard" to "Bacon Grease",
        "Duck Fat" to "Bacon Grease",
        "Walnut Oil" to "Flaxseed Oil",
        
        // Spices & Herbs - Ground Spices
        "Paprika" to "Chili Powder",
        "Cumin" to "Coriander",
        "Cinnamon" to "Nutmeg",
        "Nutmeg" to "Cinnamon",
        "Garlic Powder" to "Garlic",
        "Cayenne Pepper" to "Chili Powder",
        "Chipotle Pepper" to "Cayenne Pepper",
        
        // Spices & Herbs - Whole Spices
        "Peppercorns" to "Ground Black Pepper",
        "Cinnamon Sticks" to "Ground Cinnamon",
        
        // Spices & Herbs - Fresh Herbs
        "Fresh Basil" to "Fresh Parsley",
        "Fresh Cilantro" to "Fresh Parsley",
        "Fresh Parsley" to "Fresh Basil",
        "Fresh Rosemary" to "Fresh Thyme",
        
        // Spices & Herbs - Dried Herbs
        "Dried Basil" to "Dried Oregano",
        "Dried Oregano" to "Italian Seasoning",
        "Italian Seasoning" to "Herbes de Provence",
        "Dried Guajillo Chiles" to "Chili Powder",
        "Dried Pasilla Chiles" to "Chili Powder",
        
        // Spices & Herbs - Salt
        "Sea Salt" to "Kosher Salt",
        "Pink Himalayan Salt" to "Sea Salt",
        "Garlic Salt" to "Salt",

        // Condiments & Sauces - Vinegar
        "Apple Cider Vinegar" to "White Vinegar",
        "Red Wine Vinegar" to "Balsamic Vinegar",
        "White Vinegar" to "Red Wine Vinegar",
        
        // Condiments & Sauces - Asian Sauces
        "Soy Sauce" to "Miso Paste",
        "Oyster Sauce" to "Fish Sauce",
        "Hoisin Sauce" to "Teriyaki Sauce",
        
        // Condiments & Sauces - Western Sauces
        "Ketchup" to "BBQ Sauce",
        "Tomato Paste" to "Ketchup",
        "Mustard" to "Worcestershire Sauce",
        "Marinara Sauce" to "Tomato Sauce",
        "Pesto" to "Olive Oil",
        "Hot Sauce" to "Chili Powder",
        
        // Condiments & Sauces - Spreads
        "Hummus" to "Guacamole",
        "Salsa" to "Ketchup",
        "Jam" to "Marmalade",
        
        // Condiments & Sauces - Seasonings
        "Taco Seasoning" to "Chili Powder",
        "Fajita Seasoning" to "Taco Seasoning",
        "Green Chile Peppers" to "Jalapeño",
        "Roasted Green Chile Peppers" to "Green Chile Peppers",
        
        // Sweeteners & Baking - Sugars
        "White Sugar" to "Brown Sugar",
        "Brown Sugar" to "White Sugar",
        "Powdered Sugar" to "White Sugar",
        "Coconut Sugar" to "Raw Sugar",
        
        // Sweeteners & Baking - Liquid Sweeteners
        "Honey" to "Maple Syrup",
        "Caramel Sauce" to "Honey",
        "Agave Nectar" to "Corn Syrup",
        "Vanilla Extract" to "Honey",
        
        // Sweeteners & Baking - Baking Essentials
        "Baking Powder" to "Baking Soda",
        "Baking Soda" to "Baking Powder",
        "Active Dry Yeast" to "Instant Yeast",
        "Bread Machine Yeast" to "Active Dry Yeast",
        "Cornstarch" to "All-Purpose Flour",
        "Cocoa Powder" to "Chocolate Chips",
        "Chocolate Chips" to "Cocoa Powder",
        
        // Beverages - Water & Basic
        "Sparkling Water" to "Club Soda",
        
        // Beverages - Coffee & Tea
        "Coffee Beans" to "Ground Coffee",
        "Instant Coffee" to "Ground Coffee",
        "Green Tea" to "Herbal Tea",
        
        // Beverages - Juices
        "Lemon Juice" to "Lime",
        "Orange Juice" to "Orange",
        "Pineapple Juice" to "Orange",
        "Chicken Broth" to "Water",
        
        // Additional Substitutions
        "Wild Rice" to "Brown Rice",
        "Curry Powder" to "Garam Masala",
        "Garam Masala" to "Curry Powder",
        "Poultry Seasoning" to "Italian Seasoning",
        "Lamb" to "Ground Lamb",
        "Silken Tofu" to "Tofu",
        "Turkey Broth" to "Chicken Broth",
        "Brewed Coffee" to "Water",
        
        // Beverages - Plant Milks
        "Almond Milk" to "Oat Milk",
        "Soy Milk" to "Cashew Milk",
        
        // Beverages - Soft Drinks
        "Cola" to "Lemonade",
        "Energy Drink" to "Sports Drink",
        
        // Snacks - Chips & Crackers
        "Potato Chips" to "Tortilla Chips",
        "Crackers" to "Pretzels",
        
        // Snacks - Chocolate & Candy
        "Dark Chocolate" to "Milk Chocolate",
        "White Chocolate" to "Milk Chocolate",
        
        // Snacks - Dried Snacks
        "Raisins" to "Dried Cranberries",
        "Dates" to "Dried Apricots",
        
        // Snacks - Bars & Cookies
        "Granola Bars" to "Protein Bars",
        "Trail Mix" to "Nuts"
    )

    /**
     * Get the substitute for an ingredient by name.
     * @param ingredientName The name of the ingredient to find a substitute for
     * @return The substitute ingredient name, or null if none found
     */
    fun getSubstitute(ingredientName: String): String? {
        return substitutions[ingredientName]
    }

    /**
     * Check if an ingredient has a substitute.
     * @param ingredientName The name of the ingredient to check
     * @return True if a substitute exists, false otherwise
     */
    fun hasSubstitute(ingredientName: String): Boolean {
        return substitutions.containsKey(ingredientName)
    }

    /**
     * Find all ingredients that can be substituted with the given ingredient.
     * @param ingredientName The substitute ingredient name
     * @return List of ingredient names that can use this as a substitute
     */
    fun findIngredientsSubstitutableWith(ingredientName: String): List<String> {
        return substitutions.filter { (_, sub) -> sub == ingredientName }.keys.toList()
    }

    /**
     * Get all substitution mappings.
     * @return Map of ingredient name to its substitute
     */
    fun getAllSubstitutions(): Map<String, String> = substitutions
}
