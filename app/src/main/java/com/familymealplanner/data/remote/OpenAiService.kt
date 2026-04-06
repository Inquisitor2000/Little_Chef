package com.familymealplanner.data.remote

import com.familymealplanner.data.preferences.OnboardingPreferences
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class ScrapedRecipe(
    val name: String,
    val instructions: String,
    val simpleInstructions: String? = null,
    val prepTimeMinutes: Int? = null,
    val cookTimeMinutes: Int? = null,
    val servings: Int? = null,
    val ingredients: List<ScrapedIngredient>
)

@Serializable
data class ScrapedIngredient(
    val name: String,
    @Serializable(with = QuantitySerializer::class)
    val quantity: Double,
    val unit: String,
    val isStarIngredient: Boolean = false
)

// Custom serializer to handle non-numeric quantities like "по вкусу" (to taste)
object QuantitySerializer : KSerializer<Double> {
    override val descriptor = PrimitiveSerialDescriptor("Quantity", PrimitiveKind.DOUBLE)
    
    override fun serialize(encoder: Encoder, value: Double) {
        encoder.encodeDouble(value)
    }
    
    override fun deserialize(decoder: Decoder): Double {
        return try {
            // Try to decode as double first
            decoder.decodeDouble()
        } catch (e: Exception) {
            try {
                // If that fails, try to decode as string and parse
                val stringValue = decoder.decodeString()
                // Try to extract a number from the string
                val numberMatch = Regex("""(\d+\.?\d*)""").find(stringValue)
                numberMatch?.value?.toDoubleOrNull() ?: 0.0 // Default to 0.0 for "to taste" etc.
            } catch (e: Exception) {
                0.0 // Default to 0.0 if all parsing fails
            }
        }
    }
}

@Serializable
private data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    @kotlinx.serialization.SerialName("max_tokens")
    val maxTokens: Int = 3000,
    @kotlinx.serialization.SerialName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat("json_object")
)

@Serializable
private data class ResponseFormat(val type: String)

@Serializable
private data class OpenAiMessage(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
private data class ContentPart(
    val type: String,
    val text: String? = null,
    @kotlinx.serialization.SerialName("image_url")
    val imageUrl: ImageUrl? = null
)

@Serializable
private data class ImageUrl(
    val url: String,
    val detail: String = "auto"
)

@Serializable
private data class OpenAiResponse(
    val choices: List<Choice>? = null,
    val error: OpenAiError? = null,
    val usage: TokenUsage? = null
)

@Serializable
private data class TokenUsage(
    @kotlinx.serialization.SerialName("prompt_tokens")
    val promptTokens: Int,
    @kotlinx.serialization.SerialName("completion_tokens")
    val completionTokens: Int,
    @kotlinx.serialization.SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
private data class OpenAiError(
    val message: String,
    val type: String? = null,
    val code: String? = null
)

@Serializable
private data class Choice(
    val message: MessageContent
)

@Serializable
private data class MessageContent(
    val content: String
)

@Singleton
class OpenAiService @Inject constructor(
    private val httpClient: HttpClient,
    private val preferences: OnboardingPreferences,
    private val localeManager: com.familymealplanner.data.preferences.LocaleManager,
    private val json: Json
) {
    companion object {
        private const val OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"
        private const val REQUEST_TIMEOUT_MS = 60_000L // 60 seconds
        
        private const val RECIPE_EXTRACTION_PROMPT = """
You are an expert recipe extraction assistant. Your task is to extract recipe information EXACTLY as written in the source, preserving the original serving size and ingredient quantities.

CRITICAL INSTRUCTIONS:
1. Extract the complete recipe from the provided content (image or URL)
2. DO NOT SCALE OR MODIFY ingredient quantities - copy them EXACTLY as shown
3. PRESERVE the original serving size from the recipe
4. Convert all measurements to ONLY these valid units

VALID UNITS (use ONLY these - NO OTHER UNITS ALLOWED):
- Weight: g (grams), kg (kilograms) - for SOLID ingredients
- Volume: ml (milliliters), L (liters) - for LIQUID ingredients ONLY
- Count: pcs (pieces) - ONLY for eggs, egg yolks, and egg whites

CRITICAL: You MUST convert ALL measurements to g, kg, ml, L, or pcs!
NO tsp, Tbsp, pinch, spray, oz, lb, cups, or any other units in the final output!

FRACTION AND DECIMAL HANDLING:
CRITICAL: You MUST convert ALL fractions and decimals to proper numeric values:
- 1/4 = 0.25, 1/3 = 0.33, 1/2 = 0.5, 2/3 = 0.67, 3/4 = 0.75

CRITICAL UNIT SELECTION RULES:
1. SOLID INGREDIENTS → ALWAYS use grams (g), even if recipe says "cups":
   - Flour, sugar, rice, pasta, vegetables, fruits, cheese, meat, frozen items
   - 1 cup flour → 120 g, 1 cup rice → 185 g, 1 cup shredded cheese → 115 g
   
2. LIQUID INGREDIENTS → use milliliters (ml):
   - Water, milk, cream, oil, broth, wine, juice, sauces
   - 1 cup milk → 240 ml, 1 cup water → 240 ml, 1/2 cup olive oil → 120 ml

3. EGGS ONLY → use pieces (pcs):
   - ONLY for: eggs, egg yolks, egg whites
   - 2 eggs → 2 pcs, 3 egg whites → 3 pcs, 1 egg yolk → 1 pcs
   - ALL OTHER ITEMS use grams (g) including: chicken legs, chicken thighs, chicken wings, drumsticks, etc.

4. SPICES/SEASONINGS/SMALL QUANTITIES → convert to grams (g) or milliliters (ml):
   CRITICAL: NO MORE tsp, Tbsp, or pinch units! Convert ALL to g or ml:
   
   SPOON TO GRAM CONVERSIONS (for DRY spices/seasonings):
   - 1 pinch = 0.5 g, 1/4 tsp = 1 g, 1/2 tsp = 2 g, 1 tsp = 4 g, 1 Tbsp = 12 g
   
   SPOON TO ML CONVERSIONS (for LIQUID seasonings like oils, sauces):
   - 1/4 tsp = 1 ml, 1/2 tsp = 2 ml, 1 tsp = 5 ml, 1 Tbsp = 15 ml

WEIGHT CONVERSIONS (to grams):
- 1 kg = 1000 g, 1 oz = 28 g, 1 lb = 454 g, 1 stick butter = 113 g

VOLUME TO WEIGHT CONVERSIONS (for SOLIDS measured in cups):
- 1 cup flour = 120 g, 1 cup sugar = 200 g, 1 cup rice = 185 g
- 1 cup pasta = 100 g, 1 cup frozen peas = 150 g, 1 cup shredded cheese = 115 g

VOLUME CONVERSIONS (for LIQUIDS only):
- 1 L = 1000 ml, 1 cup = 240 ml, 1 fl oz = 30 ml

INGREDIENT RULES:
- DO NOT include plain water as an ingredient (water is assumed to be available)
- quantity MUST ALWAYS be a numeric value (5, 2.5, 0.25), NEVER text like "to taste"
- For "to taste" ingredients, use reasonable defaults: salt→5g, pepper→2g, spices→3g
- For "optional" ingredients: EITHER include with specific amounts OR exclude completely (no "optional" text)
- unit MUST be one of: g, kg, ml, L, pcs (NO OTHER UNITS ALLOWED!)
- pcs is ONLY for eggs, egg yolks, and egg whites - ALL other items use grams (g)
- ALWAYS convert fractions to decimals first: 1/4→0.25, 1/2→0.5, 3/4→0.75
- Capitalize ingredient names properly (e.g., "Spaghetti" not "spaghetti")

CRITICAL - INGREDIENT AGGREGATION:
- If the same ingredient appears multiple times in a recipe, COMBINE the quantities into ONE entry
- Examples:
  * "1 tsp salt" + "1/2 tsp salt" → "Salt": 6g (4g + 2g = 6g)
  * "2 Tbsp olive oil" + "1 Tbsp olive oil" → "Olive Oil": 45ml (30ml + 15ml = 45ml)
  * "1 onion, chopped" + "1/2 onion, diced" → "Onion": 225g (150g + 75g = 225g)
- Always check for duplicate ingredients and sum their quantities before finalizing the ingredient list

CRITICAL - STAR INGREDIENTS (isStarIngredient):
- Automatically identify 3-4 MAIN ingredients that define the recipe's character
- Star ingredients should be:
  * Primary protein (chicken, beef, fish, tofu, etc.)
  * Main carbohydrate (pasta, rice, bread, potatoes, etc.)
  * Key vegetables or fruits that are central to the dish
  * Signature sauces or flavor bases (pesto, curry paste, etc.)
- DO NOT star: basic seasonings (salt, pepper), cooking oils, water, or minor garnishes
- Set isStarIngredient: true for 3-4 main ingredients, false for all others
- Examples of star ingredients:
  * Chicken Parmesan: Chicken Breast, Mozzarella, Tomato Sauce, Spaghetti
  * Beef Stir Fry: Ground Beef, Bell Pepper, Soy Sauce, White Rice
  * Caesar Salad: Lettuce, Parmesan, Chicken Breast (if included)

CRITICAL - INGREDIENT CATALOG MAPPING:
- MUST ALWAYS map ingredients to existing catalog names - DO NOT create new ingredient names
- Use EXACT catalog ingredient names from the list below
- If recipe ingredient is slightly different, use closest catalog match and specify variation in instructions
- ONLY add as new ingredient if absolutely no catalog match exists
- ALWAYS list the WHOLE ingredient, NOT specific parts or preparations
- SMART PLURAL HANDLING: Automatically map plural forms to singular catalog names:
  * "carrots" → "Carrot", "potatoes" → "Potato", "tomatoes" → "Tomato"
  * "onions" → "Onion", "mushrooms" → "Button Mushroom", "leeks" → "Leek"
  * "eggs" → "Eggs" (keep plural for eggs as it's the catalog form)
  * Apply this logic to ALL ingredients - use singular catalog form unless catalog uses plural

COMPLETE STANDARDIZED CATALOG INGREDIENTS (MUST use these exact names):

MEAT & POULTRY:
Chicken Breast, Chicken Thigh, Chicken Drumstick, Chicken Wing, Whole Chicken, Ground Chicken, Chicken Liver, Beef Steak, Ground Beef, Beef Ribs, Beef Brisket, Beef Roast, Beef Liver, Beef Shank, Beef Tenderloin, Beef Top Round, Chuck Roast, Pork Chop, Ground Pork, Pork Belly, Pork Ribs, Pork Loin, Pork Shoulder, Bacon, Ham, Lamb Chop, Ground Lamb, Lamb Leg, Lamb Shank, Lamb, Turkey Breast, Ground Turkey, Turkey Thigh, Whole Turkey, Duck Breast, Duck Leg, Whole Duck, Sausage, Hot Dogs, Salami, Pepperoni, Italian Sausage, Mixed Cured Meats, Pancetta, Ground Buffalo, Bulk Pork Sausage, Chorizo, Pork Tenderloin

SEAFOOD:
Salmon, Tuna, Cod, Tilapia, Trout, Sea Bass, Mackerel, Sardines, Halibut, Snapper, Anchovies, Shrimp, Crab, Lobster, Clams, Mussels, Oysters, Scallops, Squid, Octopus

DAIRY & EGGS:
Whole Milk, Skim Milk, 2% Milk, Heavy Cream, Half and Half, Buttermilk, Evaporated Milk, Condensed Milk, Cheddar Cheese, Mozzarella, Parmesan, Swiss Cheese, Feta Cheese, Gouda, Brie, Cream Cheese, Cottage Cheese, Ricotta, Blue Cheese, Goat Cheese, Mascarpone, Burrata, Provolone, Gruyère, Pecorino Romano, Parmigiano-Reggiano, Monterey Jack, Milk, Cream, Plain Yogurt, Greek Yogurt, Sour Cream, Butter, Unsalted Butter, Ghee, Eggs, Egg Whites, Egg Yolks, American Cheese, Monterey Jack Cheese, Pepper Jack Cheese, Mexican Cheese Blend, Processed Cheese, Instant Dry Milk

VEGETABLES:
Lettuce, Spinach, Kale, Arugula, Cabbage, Bok Choy, Swiss Chard, Potato, Carrot, Onion, Garlic, Beet, Turnip, Radish, Sweet Potato, Ginger, Tomato, Bell Pepper, Eggplant, Chili Pepper, Green Chili, Jalapeño, Broccoli, Cauliflower, Brussels Sprouts, Zucchini, Yellow Squash, Butternut Squash, Pumpkin, Cucumber, Shallot, Leek, Scallion, Chives, Button Mushroom, Cremini Mushroom, Portobello, Shiitake, Corn, Asparagus, Celery, Green Beans, Snap Peas, Artichoke, Baby Spinach, Cherry Tomatoes, Red Onion, Yellow Onion, Sliced Pepperoncini, Green Chile Peppers, Roasted Green Chile Peppers, Jicama, Frozen Hash Browns

FRUITS:
Orange, Lemon, Lime, Grapefruit, Tangerine, Strawberry, Blueberry, Raspberry, Blackberry, Cranberry, Peach, Plum, Cherry, Apricot, Nectarine, Apple, Pear, Banana, Mango, Pineapple, Papaya, Kiwi, Coconut, Passion Fruit, Watermelon, Cantaloupe, Honeydew, Grapes, Avocado, Pomegranate, Fig

GRAINS & BREAD:
White Rice, Brown Rice, Jasmine Rice, Basmati Rice, Arborio Rice, Wild Rice, Spaghetti, Penne, Fettuccine, Macaroni, Lasagna Sheets, Egg Noodles, Rice Noodles, White Bread, Whole Wheat Bread, Sourdough, Baguette, Pita Bread, Tortillas, Bagels, English Muffins, Oats, Cornflakes, Granola, Muesli, All-Purpose Flour, Whole Wheat Flour, Bread Flour, Almond Flour, Coconut Flour, Cornmeal, Rye Flour, Rice Flour, Flaxseed Meal, Quinoa, Couscous, Bulgur, Barley, Polenta, Breadcrumbs, Biscuits, Frozen Puff Pastry, Italian Bread, Ladyfinger Cookies, Medium Pasta Shells, Orzo Pasta, Rigatoni Pasta, Refrigerated Pizza Dough, Bolillo Rolls, Glutinous Rice Flour

LEGUMES & BEANS:
Black Beans, Kidney Beans, Pinto Beans, Navy Beans, Cannellini Beans, Lima Beans, Green Lentils, Red Lentils, Brown Lentils, French Lentils, Chickpeas, Split Peas, Black-Eyed Peas, Soybeans, Tofu, Silken Tofu, Tempeh, Edamame

NUTS & SEEDS:
Almonds, Walnuts, Cashews, Pecans, Pistachios, Hazelnuts, Macadamia Nuts, Brazil Nuts, Pine Nuts, Peanuts, Peanut Butter, Sunflower Seeds, Pumpkin Seeds, Chia Seeds, Flax Seeds, Sesame Seeds, Hemp Seeds, Poppy Seeds, Caraway Seeds, Almond Butter, Cashew Butter, Tahini

OILS & FATS:
Olive Oil, Cooking Spray, Vegetable Oil, Canola Oil, Coconut Oil, Sesame Oil, Avocado Oil, Peanut Oil, Sunflower Oil, Grapeseed Oil, Lard, Duck Fat, Bacon Grease, Truffle Oil, Walnut Oil, Flaxseed Oil

SPICES & HERBS:
Black Pepper, Paprika, Cumin, Ground Cumin, Cinnamon, Nutmeg, Turmeric, Chili Powder, Cayenne Pepper, Garlic Powder, Onion Powder, Ginger Powder, Coriander, Cardamom, Allspice, Cloves, Peppercorns, Cinnamon Sticks, Bay Leaves, Star Anise, Cumin Seeds, Fennel Seeds, Mustard Seeds, Fresh Basil, Fresh Parsley, Fresh Cilantro, Cilantro, Fresh Mint, Fresh Rosemary, Fresh Thyme, Fresh Dill, Fresh Sage, Curry Leaves, Dried Oregano, Oregano, Dried Basil, Dried Thyme, Dried Rosemary, Dried Parsley, Italian Seasoning, Herbes de Provence, Salt, Table Salt, Sea Salt, Kosher Salt, Pink Himalayan Salt, Vanilla Extract, Red Pepper Flakes, White Pepper, Garlic Salt, Dried Guajillo Chiles, Dried Pasilla Chiles, Chipotle Pepper, Taco Seasoning, Curry Powder, Garam Masala, Poultry Seasoning, Dashi Granules, Fresh Chives, Fajita Seasoning

CONDIMENTS & SAUCES:
White Vinegar, Apple Cider Vinegar, Balsamic Vinegar, Red Wine Vinegar, Rice Vinegar, Soy Sauce, Fish Sauce, Oyster Sauce, Hoisin Sauce, Teriyaki Sauce, Sriracha, Miso Paste, Wasabi Paste, Red Bean Paste, Ketchup, Mustard, Mayonnaise, BBQ Sauce, Hot Sauce, Worcestershire Sauce, Ranch Dressing, Italian Dressing, Marinara Sauce, Pesto, Coconut Milk, Coconut Cream, Lemon Juice, Lime Juice, Hummus, Guacamole, Salsa, Relish, Jam, Marmalade, Balsamic Glaze, Lemon Zest, Pineapple Juice

SWEETENERS & BAKING:
White Sugar, Brown Sugar, Powdered Sugar, Raw Sugar, Coconut Sugar, Honey, Maple Syrup, Agave Nectar, Corn Syrup, Molasses, Baking Powder, Baking Soda, Active Dry Yeast, Instant Yeast, Cornstarch, Cream of Tartar, Gelatin, Cocoa Powder, Chocolate Chips, Food Coloring, Shortening, Caramel Sauce, Bread Machine Yeast, Sourdough Starter

CANNED & PRESERVED:
Canned Tomatoes, Tomato Paste, Tomato Sauce, Canned Corn, Canned Peas, Canned Mushrooms, Canned Artichokes, Canned Pumpkin, Sun-Dried Tomatoes, Tomato Juice, Canned Black Beans, Canned Kidney Beans, Canned Chickpeas, Canned Pinto Beans, Canned Navy Beans, Canned Cannellini Beans, Canned Lima Beans, Canned Lentils, Canned Salmon, Canned Tuna, Canned Sardines, Canned Anchovies, Canned Crab, Chicken Broth, Beef Broth, Vegetable Broth, Fish Stock, Bone Broth

BEVERAGES:
Water, Sparkling Water, Coffee, Black Tea, Green Tea, Herbal Tea, Fruit Juice, Orange Juice, Apple Juice, Cranberry Juice, Grape Juice, Coconut Water, Almond Milk, Soy Milk, Oat Milk, Rice Milk, Wine, Beer, Vodka, Rum, Whiskey

SNACKS:
Potato Chips, Tortilla Chips, Pretzels, Crackers, Rice Cakes, Popcorn, Pretzel Crisps, Dark Chocolate, Milk Chocolate, White Chocolate, Candy, Gummy Bears, Dried Fruit, Raisins, Dried Cranberries, Dried Apricots, Dates, Beef Jerky, Granola Bars, Protein Bars, Cookies, Trail Mix

MAPPING PRIORITY RULES:
1. EXACT MATCH: Use exact catalog name if available (e.g., "chicken breast" → "Chicken Breast")
2. CLOSE MATCH: Use closest catalog equivalent (e.g., "chicken quarters" → "Chicken Drumstick", "fresh herbs" → "Fresh Basil" or specific herb from catalog)
3. GENERIC TO SPECIFIC: Map generic terms to specific catalog items (e.g., "flour" → "All-Purpose Flour", "cheese" → specific cheese type)
4. SINGULAR/PLURAL: Always use singular catalog form (e.g., "carrots" → "Carrot", "potatoes" → "Potato", "tomatoes" → "Tomato")
5. PREPARATION VARIATIONS: Use base ingredient (e.g., "diced tomatoes" → "Tomato", "minced garlic" → "Garlic")
6. ONLY CREATE NEW if absolutely no catalog match exists (very rare)

CRITICAL MAPPING CORRECTIONS:
- "carrots" → "Carrot" (use singular form)
- "potatoes" → "Potato" (use singular form)  
- "tomatoes" → "Tomato" (use singular form)
- "sweet potatoes" → "Sweet Potato" (use singular form)
- "mushrooms" → "Button Mushroom" (use specific type)
- "sugar" → "White Sugar" (use specific type)
- "green onions" → "Scallion" (use proper catalog name)
- "green bell pepper" → "Bell Pepper" (use generic catalog name, specify color in instructions)
- "red bell pepper" → "Bell Pepper" (use generic catalog name, specify color in instructions)
- "corn tortillas" → "Tortillas" (use generic catalog name, specify type in instructions)
- "smoked paprika" → "Paprika" (use base ingredient, specify "smoked" in instructions)
- "beef tenderloin" → "Beef Tenderloin" (exact match)
- "chuck roast" → "Chuck Roast" (exact match)
- "beef top round" → "Beef Top Round" (exact match)
- "italian salad dressing" → "Italian Dressing" (close match)
- "liver pate" → "Liver Paté" (exact match with accent)
- "jalapeño pepper" → "Jalapeño" (use base ingredient)
- "egg" → "Eggs" (use plural catalog form)
- "egg white" → "Egg Whites" (use plural catalog form)
- SMART PLURAL MAPPING: Always check if plural form should map to singular catalog name

MAPPING EXAMPLES:
- "chicken quarters" → "Chicken Drumstick" (closest match)
- "flour" → "All-Purpose Flour" (most common type)
- "fresh herbs" → "Fresh Basil" (or specify which herb in instructions)
- "cheese" → "Mozzarella" or "Cheddar Cheese" (use most appropriate for recipe)
- "oil" → "Olive Oil" or "Vegetable Oil" (use most appropriate)
- "salt and pepper" → separate into "Salt" and "Black Pepper"
- "2 large carrots" → "Carrot": 200g (use singular, estimate weight)
- "3 medium potatoes" → "Potato": 450g (use singular, estimate weight)
- "1 cup diced tomatoes" → "Tomato": 150g (use singular, whole ingredient weight)

INGREDIENT MAPPING RULES:
1. Use EXACT catalog names when the ingredient matches (e.g., "garlic" → "Garlic", "olive oil" → "Olive Oil")
2. For variations, use the closest catalog match:
   * "fresh mozzarella" → "Mozzarella" (specify "fresh" in instructions)
   * "baby spinach" → "Spinach" (specify "baby" in instructions)
   * "ground black pepper" → "Black Pepper" (specify "ground" in instructions)
   * "extra virgin olive oil" → "Olive Oil" (specify "extra virgin" in instructions)
   * "kosher salt" → "Salt" (specify "kosher" in instructions)
3. For ingredient parts, use the whole ingredient:
   * "lemon zest" → "Lemon" (instructions specify "zest the lemon")
   * "lemon juice" → "Lemon" (instructions specify "juice the lemon")
   * "grated parmesan" → "Parmesan" (instructions specify "grate the parmesan")
   * "chopped onion" → "Onion" (instructions specify "chop the onion")
   * "minced garlic" → "Garlic" (instructions specify "mince the garlic")
4. For quantities, use the amount of the WHOLE ingredient needed:
   * "1 tsp lemon zest" → "Lemon": 60 g (one medium lemon weighs ~60g)
   * "2 Tbsp lemon juice" → "Lemon": 60 g (one lemon provides ~2-3 Tbsp juice)
   * "1/4 cup lemon juice" → "Lemon": 120 g (need 2 lemons for 1/4 cup juice)
   * "1 cup grated cheese" → "Parmesan": 115 g (use specific cheese type from catalog)
   * "1/2 cup chopped onion" → "Onion": 80 g (the amount needed before chopping)
   * "2 chicken legs" → "Chicken Drumstick": 300 g (estimate ~150g per drumstick, use grams not pcs)
   * "4 chicken thighs" → "Chicken Thigh": 600 g (estimate ~150g per thigh, use grams not pcs)

INSTRUCTIONS FORMAT - CRITICAL CHANGES:
- Provide TWO versions of instructions:
  1. "instructions" - DETAILED version with specific temperatures, times, techniques, and visual cues
  2. "simpleInstructions" - SIMPLE version with concise, essential steps only

CRITICAL INSTRUCTION RULES:
1. DO NOT include specific quantities in the instruction text
2. Reference ingredients by name only (e.g., "Add the flour" not "Add 120g flour")
3. The app will dynamically insert quantities from the recipe quantity table based on serving size
4. ALWAYS mention ALL ingredients from the ingredient list somewhere in the instructions
5. Include preparation methods (zest, juice, grate, chop, mince, dice) but without quantities
6. Convert ALL imperial measurements to metric in BOTH instruction versions:
   * Temperatures: Fahrenheit → Celsius (e.g., "350°F" → "175°C", "400°F" → "200°C")
   * Lengths: inches → centimeters (e.g., "9x13 inch pan" → "23x33 cm pan")

INSTRUCTION EXAMPLES:
WRONG: "Add 120g flour and mix with 240ml milk"
RIGHT: "Add the flour and mix with the milk"

WRONG: "Zest 1 lemon and juice it"
RIGHT: "Zest the lemon and juice it"

CRITICAL FORMATTING RULES:
- Each step MUST be a COMPLETE sentence or paragraph - NEVER split mid-sentence
- Each step number and its full text must be on ONE continuous line (no line breaks within a step)
- Format: "1. Complete instruction text here.\n\n2. Next complete instruction here."
- Use "\n\n" (double newline) ONLY between steps, NOT within a step
- Each step should be self-contained and readable
- PRESERVE all preparation methods from original recipe (if recipe says "lemon zest", instructions must mention "zest")

QUANTITY ROUNDING RULES:
- For g and ml UNDER 10: round to nearest 1 (3.2→3, 4.7→5, 8.3→8)
- For g and ml 10 OR MORE: round to nearest 10 (14→10, 25→30, 227→230)
- For pcs (eggs only): always use whole numbers

DETAILED instructions should include:
- Specific temperatures and cooking times
- Visual cues (golden brown, shimmering, fragrant)
- Technique tips (stirring occasionally, breaking up with spoon)
- ALL preparation methods from original recipe (zest, juice, grate, chop, mince, dice)
- Reference to ALL ingredients by name (without quantities)

SIMPLE instructions should be:
- Concise but complete sentences
- Essential steps only
- Still readable and clear
- MUST include preparation methods (zest, juice, grate, chop, etc.)
- Reference to ALL ingredients by name (without quantities)

SERVING SIZE:
- Extract the EXACT serving size from the recipe
- If recipe says "Serves 4" → servings: 4
- If recipe says "Makes 6 servings" → servings: 6
- If recipe says "Serves 2-3" → use the higher number: servings: 3
- If no serving size is mentioned, estimate based on ingredient quantities

EXAMPLE - Proper Catalog Mapping, Aggregation and Star Ingredients:
Input: "Chicken Stir Fry: 2 chicken quarters, 1 tsp salt, 2 Tbsp soy sauce, 1 bell pepper, 1 cup rice, 1/2 tsp salt, 1 Tbsp soy sauce, fresh herbs"
Output:
{
  "ingredients": [
    {"name": "Chicken Drumstick", "quantity": 300, "unit": "g", "isStarIngredient": true},
    {"name": "Salt", "quantity": 6, "unit": "g", "isStarIngredient": false},
    {"name": "Soy Sauce", "quantity": 45, "unit": "ml", "isStarIngredient": true},
    {"name": "Bell Pepper", "quantity": 150, "unit": "g", "isStarIngredient": true},
    {"name": "White Rice", "quantity": 185, "unit": "g", "isStarIngredient": true},
    {"name": "Fresh Basil", "quantity": 5, "unit": "g", "isStarIngredient": false}
  ]
}
Note: "chicken quarters" → "Chicken Drumstick" (catalog mapping), "fresh herbs" → "Fresh Basil" (specific catalog herb), Salt aggregated (1 tsp + 0.5 tsp = 6g), Soy Sauce aggregated (2 Tbsp + 1 Tbsp = 45ml), 4 star ingredients selected

Return a JSON object with this EXACT structure:
{
  "name": "Recipe Name",
  "instructions": "1. Detailed step without quantities.\n\n2. Another detailed step.",
  "simpleInstructions": "1. Concise step without quantities.\n\n2. Another concise step.",
  "prepTimeMinutes": 10,
  "cookTimeMinutes": 15,
  "servings": [ORIGINAL SERVING SIZE FROM RECIPE],
  "ingredients": [
    {"name": "Ingredient Name", "quantity": [number], "unit": "[g|kg|ml|L|pcs]", "isStarIngredient": [true|false]}
  ]
}

IMPORTANT:
- DO NOT scale the recipe - keep original quantities in ingredients array
- DO NOT include quantities in instruction text - reference ingredients by name only
- servings field MUST match the original recipe's serving size
- unit field MUST be one of: g, kg, ml, L, pcs (NO OTHER UNITS!)
- pcs is ONLY for eggs, egg yolks, and egg whites - ALL other items including chicken parts use grams (g)
- ALWAYS use WHOLE ingredients (Lemon, not Lemon Zest; Onion, not Chopped Onion)
- Instructions specify HOW to prepare ingredients (zest, juice, chop, grate, etc.) but without quantities
- ALL ingredients from the list must be mentioned somewhere in the instructions
- AGGREGATE duplicate ingredients into single entries with combined quantities
- Set isStarIngredient: true for 3-4 main ingredients, false for others
- CRITICAL: EVERY ingredient name MUST exist in the provided catalog - use exact catalog matches only
- Map generic terms to specific catalog items (flour → All-Purpose Flour, cheese → specific cheese type)
- Only create new ingredient names if absolutely no catalog match exists (extremely rare)
- Return ONLY valid JSON, no markdown or extra text
"""

        private const val VERIFICATION_PROMPT = """
You are a recipe verification expert. Review the extracted recipe data and verify ALL conversions are correct.

VERIFICATION CHECKLIST:
1. Check ALL fraction conversions (1/4→0.25, 1/2→0.5, 3/4→0.75, 1/3→0.33, 2/3→0.67)
2. Verify ounce conversions (oz × 28 = grams)
3. Verify pound conversions (lb × 454 = grams)
4. Verify cup conversions:
   - SOLIDS (flour, sugar, vegetables, cheese, etc.) → grams
   - LIQUIDS (water, milk, oil, broth, etc.) → milliliters
5. Verify tsp/Tbsp/pinch conversions:
   - DRY ingredients: tsp × 4 = g, Tbsp × 12 = g, pinch = 0.5-1 g
   - LIQUID ingredients: tsp × 5 = ml, Tbsp × 15 = ml
6. Check that ingredient names are properly capitalized
7. Verify units are ONLY: g, kg, ml, L, pcs (NO OTHER UNITS!)
8. Verify pcs is ONLY used for eggs, egg yolks, and egg whites (all other items use g or kg)
9. Verify quantities are numbers (no text)
10. Check rounding:
   - Under 10 g/ml: round to nearest 1
   - 10+ g/ml: round to nearest 10
11. Verify serving size matches the original recipe
12. Check that instructions are properly formatted with "\n\n" between steps
13. CRITICAL: Verify ingredients are WHOLE items and mapped to catalog names:
    - "Lemon Zest" should be "Lemon"
    - "Lemon Juice" should be "Lemon"
    - "Grated Cheese" should be specific cheese type from catalog (e.g., "Parmesan", "Cheddar Cheese")
    - "Chopped Onion" should be "Onion"
    - "Minced Garlic" should be "Garlic"
    - "Ground Black Pepper" should be "Black Pepper"
    - "Extra Virgin Olive Oil" should be "Olive Oil"
    - Use EXACT catalog ingredient names when possible for consistency
14. CRITICAL: Verify ingredient names match standardized catalog names:
    - EVERY ingredient MUST exist in the provided catalog list
    - Check against complete catalog: Chicken Breast, Ground Beef, Olive Oil, Salt, Black Pepper, etc.
    - Use specific cheese names: "Parmesan" not "Cheese", "Mozzarella" not "Cheese"
    - Use specific meat cuts: "Chicken Breast" not "Chicken", "Ground Beef" not "Beef"
    - Map generic terms to specific catalog items: "flour" → "All-Purpose Flour"
    - Variations should be specified in instructions, not ingredient names
    - REJECT any ingredient names not found in the catalog - find closest match instead
    - Examples of required mappings:
      * "chicken quarters" → "Chicken Drumstick"
      * "fresh herbs" → specific herb like "Fresh Basil"
      * "flour" → "All-Purpose Flour"
      * "oil" → "Olive Oil" or "Vegetable Oil"
15. CRITICAL: Verify pcs unit is ONLY for eggs/egg yolks/egg whites:
    - Lemons, onions, tomatoes, etc. should use grams (g), not pcs
16. CRITICAL: Verify instructions DO NOT contain specific quantities:
    - WRONG: "Add 120g flour" or "Use 2 tablespoons oil"
    - RIGHT: "Add the flour" or "Use the oil"
    - Instructions should reference ingredients by name only
17. CRITICAL: Verify ALL ingredients from the ingredient list are mentioned in instructions:
    - Every ingredient in the ingredients array must be referenced somewhere in the instruction text
    - Check both detailed and simple instructions
18. CRITICAL: Verify instructions preserve ALL preparation methods from original recipe:
    - If original says "lemon zest", instructions MUST mention "zest the lemon"
    - If original says "lemon juice", instructions MUST mention "juice the lemon"
    - If original says "grated cheese", instructions MUST mention "grate the cheese"
    - DO NOT lose these preparation details when consolidating ingredients
19. CRITICAL: Verify ingredient names use standardized catalog names:
    - Use exact catalog matches: "Ground Beef" not "beef", "Olive Oil" not "oil"
    - Specify variations in instructions: "use kosher salt" not ingredient name "Kosher Salt"
20. CRITICAL: Verify NO DUPLICATE ingredients in the final list:
    - Check for same ingredients appearing multiple times (e.g., "Salt" appearing twice)
    - If duplicates exist, combine quantities into single entry
    - Example: "Salt": 7g + "Salt": 5g → "Salt": 12g (single entry)
21. CRITICAL: Verify pcs unit is ONLY for eggs (chicken legs/thighs/wings use grams):
    - Chicken Drumstick, Chicken Thigh, Chicken Wing → use grams (g), not pcs
    - Only Eggs, Egg Whites, Egg Yolks → use pcs
22. CRITICAL: Verify star ingredients (isStarIngredient) are correctly assigned:
    - Exactly 3-4 ingredients should have isStarIngredient: true
    - Star ingredients should be main proteins, carbs, key vegetables, or signature sauces
    - DO NOT star: salt, pepper, cooking oils, minor seasonings, garnishes
    - All other ingredients should have isStarIngredient: false

COMMON ERRORS TO FIX:
- Solid ingredients measured in ml (should be g)
- Liquid ingredients measured in g (should be ml)
- Fractions not converted to decimals
- Ounces not converted to grams
- tsp/Tbsp/pinch not converted to g or ml
- Wrong cup-to-gram conversions
- Incorrect rounding
- Missing or incorrect serving size
- Ingredient parts instead of whole ingredients (e.g., "Lemon Zest" instead of "Lemon")
- Using pcs for non-egg items (chicken legs, onions, etc. should use grams)
- Quantities mentioned in instruction text (should reference ingredients by name only)
- Duplicate ingredients not aggregated (same ingredient appearing multiple times)
- Missing isStarIngredient field or incorrect star ingredient selection
- Too many or too few star ingredients (should be 3-4 total)
- Missing ingredients in instructions (all ingredients must be mentioned)
- Non-standardized ingredient names (should use exact catalog names)
- Generic ingredient names when specific catalog names exist (e.g., "Cheese" instead of "Parmesan")
- CRITICAL: Ingredient names not found in catalog (must map to existing catalog ingredients)
- Using made-up ingredient names instead of catalog matches
- Not mapping generic terms to specific catalog items (e.g., "flour" must be "All-Purpose Flour")

If you find ANY errors, correct them and return the CORRECTED JSON.
If everything is correct, return the SAME JSON unchanged.

Return ONLY valid JSON, no markdown, no explanations, no extra text.
"""
    }

    sealed class Result {
        data class Success(
            val recipe: ScrapedRecipe,
            val tokenUsage: TokenUsageInfo? = null
        ) : Result()
        data class Error(val message: String) : Result()
    }
    
    data class TokenUsageInfo(
        val extractionTokens: Int,
        val verificationTokens: Int,
        val totalTokens: Int,
        val promptTokens: Int,
        val completionTokens: Int
    )


    suspend fun scrapeRecipeFromUrl(url: String): Result {
        val apiKey = preferences.getOpenAiApiKeySync()
        if (apiKey.isNullOrBlank()) {
            return Result.Error("OpenAI API key not configured. Please add it in Settings.")
        }
        
        // Get user's language preference
        val languageCode = localeManager.getLanguage()
        val languageName = when (languageCode) {
            "ru" -> "Russian"
            "ro" -> "Romanian"
            else -> "English"
        }
        
        // Add language instruction to the prompt
        val languageInstruction = if (languageCode != "en") {
            "\n\nIMPORTANT: Translate the recipe name, ingredient names, and instructions to $languageName. Keep measurements and units in their standard form (g, ml, kg, L, pcs)."
        } else {
            ""
        }

        return try {
            withTimeout(REQUEST_TIMEOUT_MS) {
                // First fetch the webpage content
                val webContent = fetchWebpageContent(url)
                
                // Track token usage
                var extractionTokens = 0
                var verificationTokens = 0
                var totalPromptTokens = 0
                var totalCompletionTokens = 0
                
                // First pass: Extract recipe
                val extractRequest = OpenAiRequest(
                    model = "gpt-4o",
                    messages = listOf(
                        OpenAiMessage(
                            role = "user",
                            content = listOf(
                                ContentPart(
                                    type = "text",
                                    text = """$RECIPE_EXTRACTION_PROMPT$languageInstruction

Here is the recipe webpage content to extract from:

URL: $url

Content:
$webContent
"""
                                )
                            )
                        )
                    )
                )

                val (extractedResponse, extractUsage) = makeRequestWithUsage(apiKey, extractRequest)
                extractUsage?.let {
                    extractionTokens = it.totalTokens
                    totalPromptTokens += it.promptTokens
                    totalCompletionTokens += it.completionTokens
                    android.util.Log.d("OpenAiService", "Extraction pass - Prompt: ${it.promptTokens}, Completion: ${it.completionTokens}, Total: ${it.totalTokens}")
                }
                
                // Second pass: Verify and correct
                val verifyRequest = OpenAiRequest(
                    model = "gpt-4o",
                    messages = listOf(
                        OpenAiMessage(
                            role = "user",
                            content = listOf(
                                ContentPart(
                                    type = "text",
                                    text = """$VERIFICATION_PROMPT$languageInstruction

Here is the extracted recipe data to verify:

$extractedResponse

Review this data carefully and correct any errors. Return the corrected JSON."""
                                )
                            )
                        )
                    )
                )
                
                val (verifiedResponse, verifyUsage) = makeRequestWithUsage(apiKey, verifyRequest)
                verifyUsage?.let {
                    verificationTokens = it.totalTokens
                    totalPromptTokens += it.promptTokens
                    totalCompletionTokens += it.completionTokens
                    android.util.Log.d("OpenAiService", "Verification pass - Prompt: ${it.promptTokens}, Completion: ${it.completionTokens}, Total: ${it.totalTokens}")
                }
                
                val totalTokens = extractionTokens + verificationTokens
                android.util.Log.i("OpenAiService", "Recipe scraping completed - Total tokens: $totalTokens (Extraction: $extractionTokens, Verification: $verificationTokens)")
                
                val tokenUsageInfo = TokenUsageInfo(
                    extractionTokens = extractionTokens,
                    verificationTokens = verificationTokens,
                    totalTokens = totalTokens,
                    promptTokens = totalPromptTokens,
                    completionTokens = totalCompletionTokens
                )
                
                parseResponse(verifiedResponse, tokenUsageInfo)
            }
        } catch (e: TimeoutCancellationException) {
            Result.Error("Request timed out. Please try again.")
        } catch (e: Exception) {
            Result.Error("Failed to process URL: ${e.message}")
        }
    }

    private suspend fun fetchWebpageContent(url: String): String {
        return try {
            val response: io.ktor.client.statement.HttpResponse = httpClient.get(url)
            val html = response.body<String>()
            // Extract text content, remove scripts and styles
            html.replace(Regex("<script[^>]*>[\\s\\S]*?</script>"), "")
                .replace(Regex("<style[^>]*>[\\s\\S]*?</style>"), "")
                .replace(Regex("<[^>]+>"), " ")
                .replace(Regex("\\s+"), " ")
                .take(15000) // Limit content size
        } catch (e: Exception) {
            "Could not fetch webpage. Please extract recipe from URL: $url"
        }
    }

    private suspend fun makeRequestWithUsage(apiKey: String, request: OpenAiRequest): Pair<String, TokenUsage?> {
        try {
            val httpResponse = httpClient.post(OPENAI_API_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(json.encodeToString(OpenAiRequest.serializer(), request))
            }
            
            // Check HTTP status
            if (!httpResponse.status.isSuccess()) {
                val errorBody = try {
                    httpResponse.body<String>()
                } catch (e: Exception) {
                    "Unable to read error response"
                }
                throw Exception("HTTP ${httpResponse.status.value}: $errorBody")
            }
            
            // Parse response using the configured JSON instance
            val response = httpResponse.body<OpenAiResponse>()

            // Check for API error
            response.error?.let { error ->
                throw Exception("OpenAI API Error: ${error.message}")
            }

            // Return content and usage from choices
            val content = response.choices?.firstOrNull()?.message?.content
                ?: throw Exception("No response content from OpenAI")
            
            return Pair(content, response.usage)
        } catch (e: Exception) {
            // Provide more helpful error messages
            val errorMessage = when {
                e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> 
                    "Invalid API key. Please check your OpenAI API key in Settings."
                e.message?.contains("429") == true || e.message?.contains("rate limit") == true -> 
                    "Rate limit exceeded. Please try again later."
                e.message?.contains("timeout") == true -> 
                    "Request timed out. Please try again."
                e.message?.contains("API Error") == true -> 
                    e.message
                else -> "Failed to process request: ${e.message}"
            }
            throw Exception(errorMessage)
        }
    }

    private suspend fun makeRequest(apiKey: String, request: OpenAiRequest): String {
        val (content, _) = makeRequestWithUsage(apiKey, request)
        return content
    }

    private fun parseResponse(responseContent: String, tokenUsage: TokenUsageInfo? = null): Result {
        return try {
            // Clean up response - remove markdown code blocks if present
            val cleanedContent = responseContent
                .replace(Regex("```json\\s*"), "")
                .replace(Regex("```\\s*"), "")
                .trim()
            
            val recipe = json.decodeFromString<ScrapedRecipe>(cleanedContent)
            
            // Round ingredient quantities
            val roundedIngredients = recipe.ingredients.map { ingredient ->
                ingredient.copy(quantity = roundQuantity(ingredient.quantity, ingredient.unit))
            }
            
            Result.Success(
                recipe = recipe.copy(ingredients = roundedIngredients),
                tokenUsage = tokenUsage
            )
        } catch (e: Exception) {
            Result.Error("Failed to parse recipe: ${e.message}\nResponse: ${responseContent.take(200)}")
        }
    }
    
    /**
     * Round quantity based on size and unit.
     * Rules for g/ml:
     * - Under 10: round to nearest 1 (3.2→3, 4.7→5, 8.3→8)
     * - 10 or more: round to nearest 10 (14→10, 25→30, 227→230)
     * 
     * For pcs, spray: round to whole numbers
     */
    private fun roundQuantity(quantity: Double, unit: String): Double {
        return when (unit) {
            "g", "ml" -> {
                if (quantity < 10.0) {
                    // Round to nearest 1 for small quantities
                    kotlin.math.round(quantity).coerceAtLeast(1.0)
                } else {
                    // Round to nearest 10 for larger quantities
                    val rounded = (quantity / 10.0).let { 
                        if (it - it.toLong() >= 0.5) kotlin.math.ceil(it) else kotlin.math.floor(it)
                    } * 10
                    rounded.coerceAtLeast(10.0)
                }
            }
            "pcs", "spray" -> {
                // Round to whole numbers
                kotlin.math.round(quantity).coerceAtLeast(1.0)
            }
            else -> quantity
        }
    }

}
