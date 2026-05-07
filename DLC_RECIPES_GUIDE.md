# DLC Recipes Guide - Where and How to Add Premium Recipes

## Overview
DLC (Downloadable Content) recipes are stored in separate asset pack modules that users download after purchasing.

## Directory Structure

```
Little_Chef/
├── app/                          # Main app
│   └── src/main/assets/recipes/  # Free recipes
│       ├── italian/              # Free Italian recipes
│       ├── mexican/              # Free Mexican recipes
│       └── ...
│
└── italian_premium_pack/         # DLC Asset Pack
    ├── build.gradle              # Asset pack config
    └── src/main/
        └── assets/
            └── recipes/
                └── italian premium/   # Premium Italian recipes
                    ├── carbonara_authentic_italian_premium.json
                    ├── carbonara_authentic_italian_premium_ro.json
                    ├── carbonara_authentic_italian_premium_ru.json
                    └── ... (add more recipes here)
```

## Where to Add DLC Recipes

### Location:
```
/italian_premium_pack/src/main/assets/recipes/italian premium/
```

### Current Recipes:
Currently only has 1 recipe (Carbonara) with 3 language versions:
- `carbonara_authentic_italian_premium.json` (English)
- `carbonara_authentic_italian_premium_ro.json` (Romanian)
- `carbonara_authentic_italian_premium_ru.json` (Russian)

### You Need to Add:
According to the preview drawer, you need **12 recipes total**:
1. ✅ Carbonara Romana (already exists)
2. ❌ Osso Buco alla Milanese
3. ❌ Risotto ai Funghi Porcini
4. ❌ Saltimbocca alla Romana
5. ❌ Pappardelle al Cinghiale
6. ❌ Cacio e Pepe
7. ❌ Vitello Tonnato
8. ❌ Arancini Siciliani
9. ❌ Bistecca alla Fiorentina
10. ❌ Panna Cotta
11. ❌ Tiramisu Classico
12. ❌ Cannoli Siciliani

## Recipe File Format

### File Naming Convention:
```
{recipe_name}_{cuisine}_italian_premium.json
{recipe_name}_{cuisine}_italian_premium_ro.json  (Romanian)
{recipe_name}_{cuisine}_italian_premium_ru.json  (Russian)
```

### Example:
```
osso_buco_milanese_italian_premium.json
osso_buco_milanese_italian_premium_ro.json
osso_buco_milanese_italian_premium_ru.json
```

### JSON Structure:
```json
{
  "id": "osso_buco_milanese_italian_premium",
  "name": "Osso Buco alla Milanese",
  "instructions": "Detailed step-by-step instructions...",
  "simpleInstructions": "Simplified instructions...",
  "prepTimeMinutes": 20,
  "cookTimeMinutes": 120,
  "servings": 4,
  "mealType": "DINNER",
  "dishCategory": "MAIN_COURSE",
  "cuisine": "Italian Premium",
  "imageUrl": "recipes/images/italian_premium/osso_buco.jpg",
  "ingredients": [
    {
      "name": "Veal Shanks",
      "quantity": 4,
      "unit": "pcs",
      "isStarIngredient": true
    },
    {
      "name": "Flour",
      "quantity": 100,
      "unit": "g",
      "isStarIngredient": false
    }
  ]
}
```

### Required Fields:
- `id`: Unique identifier (use filename without .json)
- `name`: Display name of the recipe
- `instructions`: Detailed cooking instructions
- `simpleInstructions`: Simplified version
- `prepTimeMinutes`: Preparation time
- `cookTimeMinutes`: Cooking time
- `servings`: Number of servings (default: 4)
- `mealType`: BREAKFAST, LUNCH, DINNER, SNACK, DESSERT
- `dishCategory`: PASTA, MAIN_COURSE, APPETIZER, DESSERT, etc.
- `cuisine`: "Italian Premium" (must match exactly)
- `imageUrl`: Path to recipe image (optional but recommended)
- `ingredients`: Array of ingredient objects

### Ingredient Object:
```json
{
  "name": "Ingredient Name",
  "quantity": 100,
  "unit": "g",  // g, ml, kg, l, cup, tbsp, tsp, pcs, oz, lb
  "isStarIngredient": true  // Main ingredients
}
```

## How to Add a New Recipe

### Step 1: Create the JSON file
Create a new file in:
```
/italian_premium_pack/src/main/assets/recipes/italian premium/
```

### Step 2: Name the file
```
{recipe_name}_italian_premium.json
```

### Step 3: Copy the template
Use the Carbonara recipe as a template:
```bash
cp carbonara_authentic_italian_premium.json osso_buco_milanese_italian_premium.json
```

### Step 4: Edit the content
Update all fields with the new recipe information.

### Step 5: Create translations (optional)
Create Romanian and Russian versions:
```
osso_buco_milanese_italian_premium_ro.json
osso_buco_milanese_italian_premium_ru.json
```

### Step 6: Add recipe image (optional)
Place image in:
```
/italian_premium_pack/src/main/assets/recipes/images/italian_premium/
```

Update `imageUrl` field:
```json
"imageUrl": "recipes/images/italian_premium/osso_buco.jpg"
```

## Quick Recipe Template

Save this as a template for new recipes:

```json
{
  "id": "RECIPE_ID_HERE",
  "name": "Recipe Name Here",
  "instructions": "1. First step\n\n2. Second step\n\n3. Third step",
  "simpleInstructions": "1. Simple first step\n\n2. Simple second step",
  "prepTimeMinutes": 15,
  "cookTimeMinutes": 30,
  "servings": 4,
  "mealType": "DINNER",
  "dishCategory": "MAIN_COURSE",
  "cuisine": "Italian Premium",
  "imageUrl": "recipes/images/italian_premium/RECIPE_IMAGE.jpg",
  "ingredients": [
    {
      "name": "Main Ingredient",
      "quantity": 500,
      "unit": "g",
      "isStarIngredient": true
    },
    {
      "name": "Secondary Ingredient",
      "quantity": 200,
      "unit": "g",
      "isStarIngredient": false
    }
  ]
}
```

## Meal Types
- `BREAKFAST` - Morning meals
- `LUNCH` - Midday meals
- `DINNER` - Evening meals
- `SNACK` - Light snacks
- `DESSERT` - Sweet dishes

## Dish Categories
- `PASTA` - Pasta dishes
- `MAIN_COURSE` - Main dishes
- `APPETIZER` - Starters
- `SIDE_DISH` - Side dishes
- `DESSERT` - Desserts
- `SOUP` - Soups
- `SALAD` - Salads
- `BREAD` - Bread and baked goods
- `SEAFOOD` - Fish and seafood
- `CHICKEN` - Chicken dishes
- `BEEF` - Beef dishes
- `PORK` - Pork dishes
- `VEGETARIAN` - Vegetarian dishes
- `RICE_BOWL` - Rice-based dishes
- `SANDWICH` - Sandwiches
- `PIZZA` - Pizza
- `BEVERAGE` - Drinks
- `BAKED_DISH` - Baked dishes

## Units
- `g` - Grams
- `ml` - Milliliters
- `kg` - Kilograms
- `l` - Liters
- `cup` - Cups
- `tbsp` - Tablespoons
- `tsp` - Teaspoons
- `pcs` - Pieces (for countable items like eggs)
- `oz` - Ounces
- `lb` - Pounds

## Testing Your Recipes

### 1. Build the asset pack:
```bash
./gradlew :italian_premium_pack:bundleRelease
```

### 2. Install the app:
```bash
./gradlew installDebug
```

### 3. Test the purchase flow:
- Open the app
- Tap "Italian Premium"
- Click "$1.99" button
- Complete test purchase
- Verify recipes appear

### 4. Check recipe loading:
Look for logs:
```
RecipeLoader: Loading recipes from: italian premium
RecipeLoader: Found X recipes
```

## Important Notes

### Cuisine Name Must Match:
In the JSON file:
```json
"cuisine": "Italian Premium"
```

Must match the folder name:
```
/recipes/italian premium/
```

### File Naming:
- Use lowercase
- Use underscores for spaces
- End with `_italian_premium.json`
- Example: `osso_buco_milanese_italian_premium.json`

### ID Format:
- Same as filename without `.json`
- Example: `osso_buco_milanese_italian_premium`

### Star Ingredients:
Mark 2-3 main ingredients as `isStarIngredient: true`
These are the key ingredients that define the dish.

## Checklist for Adding a Recipe

- [ ] Create JSON file with correct naming
- [ ] Set unique ID
- [ ] Add recipe name
- [ ] Write detailed instructions
- [ ] Write simple instructions
- [ ] Set prep and cook times
- [ ] Set servings (usually 4)
- [ ] Set meal type
- [ ] Set dish category
- [ ] Set cuisine to "Italian Premium"
- [ ] Add all ingredients
- [ ] Mark star ingredients
- [ ] Add image (optional)
- [ ] Create translations (optional)
- [ ] Test in app
- [ ] Verify recipe appears after purchase

## Example: Adding Osso Buco

### 1. Create file:
`osso_buco_milanese_italian_premium.json`

### 2. Content:
```json
{
  "id": "osso_buco_milanese_italian_premium",
  "name": "Osso Buco alla Milanese",
  "instructions": "1. Season veal shanks with salt and pepper, dredge in flour.\n\n2. Heat olive oil and butter in a large pot. Brown the veal shanks on all sides.\n\n3. Add onions, carrots, and celery. Cook until softened.\n\n4. Add white wine and let it reduce by half.\n\n5. Add tomatoes and beef broth. Bring to a simmer.\n\n6. Cover and cook on low heat for 2 hours until meat is tender.\n\n7. Prepare gremolata: mix parsley, lemon zest, and garlic.\n\n8. Serve osso buco with gremolata on top and risotto alla milanese.",
  "simpleInstructions": "1. Brown seasoned veal shanks.\n\n2. Add vegetables and cook.\n\n3. Add wine, tomatoes, and broth.\n\n4. Simmer 2 hours.\n\n5. Top with gremolata and serve.",
  "prepTimeMinutes": 20,
  "cookTimeMinutes": 120,
  "servings": 4,
  "mealType": "DINNER",
  "dishCategory": "MAIN_COURSE",
  "cuisine": "Italian Premium",
  "imageUrl": "recipes/images/italian_premium/osso_buco.jpg",
  "ingredients": [
    {
      "name": "Veal Shanks",
      "quantity": 4,
      "unit": "pcs",
      "isStarIngredient": true
    },
    {
      "name": "Flour",
      "quantity": 100,
      "unit": "g",
      "isStarIngredient": false
    },
    {
      "name": "Olive Oil",
      "quantity": 50,
      "unit": "ml",
      "isStarIngredient": false
    },
    {
      "name": "Butter",
      "quantity": 50,
      "unit": "g",
      "isStarIngredient": false
    },
    {
      "name": "Onion",
      "quantity": 1,
      "unit": "pcs",
      "isStarIngredient": false
    },
    {
      "name": "Carrots",
      "quantity": 2,
      "unit": "pcs",
      "isStarIngredient": false
    },
    {
      "name": "Celery",
      "quantity": 2,
      "unit": "pcs",
      "isStarIngredient": false
    },
    {
      "name": "White Wine",
      "quantity": 250,
      "unit": "ml",
      "isStarIngredient": true
    },
    {
      "name": "Canned Tomatoes",
      "quantity": 400,
      "unit": "g",
      "isStarIngredient": false
    },
    {
      "name": "Beef Broth",
      "quantity": 500,
      "unit": "ml",
      "isStarIngredient": false
    },
    {
      "name": "Parsley",
      "quantity": 30,
      "unit": "g",
      "isStarIngredient": false
    },
    {
      "name": "Lemon",
      "quantity": 1,
      "unit": "pcs",
      "isStarIngredient": true
    },
    {
      "name": "Garlic",
      "quantity": 2,
      "unit": "pcs",
      "isStarIngredient": false
    }
  ]
}
```

## Summary

**Location**: `/italian_premium_pack/src/main/assets/recipes/italian premium/`

**Current Status**: 1 recipe (Carbonara)

**Needed**: 11 more recipes to reach 12 total

**Format**: JSON files following the template

**Naming**: `{recipe_name}_italian_premium.json`

**Cuisine Field**: Must be "Italian Premium"

Add your recipes to this folder and they'll automatically appear in the app after users purchase the DLC! 🍝
