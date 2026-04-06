# Little Chef - Family Meal Planner App Memory

## Project Overview
Little Chef is a comprehensive Android meal planning application built with Kotlin and Jetpack Compose. It helps families organize recipes, manage pantry inventory, create grocery lists, and plan meals with smart ingredient matching and AI-powered recipe scraping.

**Package**: `com.familymealplanner`
**Min SDK**: 26 (Android 8.0)
**Target SDK**: 34 (Android 14)
**Build System**: Gradle with Kotlin DSL

---

## Architecture

### Clean Architecture Pattern (MVVM)
```
app/src/main/java/com/familymealplanner/
├── data/              # Data layer
│   ├── local/         # Room database, DAOs, entities, loaders
│   ├── remote/        # API services (OpenAI)
│   ├── repository/    # Repository implementations
│   └── preferences/   # DataStore preferences
├── domain/            # Business logic
│   ├── model/         # Domain models
│   ├── repository/    # Repository interfaces
│   ├── usecase/       # Use cases
│   └── util/          # Domain utilities
├── ui/                # Presentation layer
│   ├── screens/       # Composable screens + ViewModels
│   ├── navigation/    # Navigation setup
│   ├── theme/         # App theming
│   ├── onboarding/    # Onboarding flow
│   ├── util/          # UI utilities
│   └── export/        # Export functionality
├── di/                # Dependency injection (Hilt)
└── utils/             # App-level utilities
```

---

## Core Technologies

### Dependencies
- **UI**: Jetpack Compose (Material 3)
- **DI**: Hilt (Dagger)
- **Database**: Room (SQLite)
- **Navigation**: Navigation Compose
- **Image Loading**: Coil
- **HTTP Client**: Ktor
- **Serialization**: Kotlinx Serialization
- **Preferences**: DataStore
- **Testing**: JUnit, Kotest, MockK, Robolectric

---

## Database Schema (Room)

### Entities
1. **MealEntity** - User recipes and scraped recipes
2. **MealIngredientEntity** - Recipe ingredients (junction table)
3. **IngredientEntity** - Pantry inventory items
4. **IngredientAllergenEntity** - Ingredient allergens (junction table)
5. **IngredientSubstituteEntity** - Ingredient substitutions
6. **AllergenEntity** - Allergen definitions
7. **MealPlanEntity** - Scheduled meals
8. **InventoryTransactionEntity** - Ingredient usage history
9. **GroceryItemEntity** - Shopping list items

### Key DAOs
- AllergenDao, IngredientDao, MealDao, MealPlanDao, GroceryItemDao, etc.

---

## Domain Models

### Core Models
- **Meal**: Recipe with ingredients, instructions, times, metadata
  - `id`, `name`, `instructions`, `simpleInstructions`
  - `prepTimeMinutes`, `cookTimeMinutes`, `servings`
  - `isScraped`, `isBundled`, `imagePath`
  - `mealType` (BREAKFAST, LUNCH, DINNER, SNACK, DESSERT)
  - `dishCategory`, `createdInLanguage`, `ingredients`

- **Ingredient**: Pantry item with categorization
  - `id`, `name`, `unit`, `category`, `subcategory`
  - `preferredDisplayUnit`, `createdInLanguage`
  - `allergens`, `substitutes`

- **MealPlan**: Scheduled meal with status tracking
  - `id`, `meal`, `plannedDate`, `mealType`, `status`
  - `startedAt`, `completedAt`, `ingredientSubstitutions`
  - `plannedServings`, adjusted times
  - Status: PLANNED, COOKING, COMPLETED, ABORTED

- **GroceryItem**: Shopping list item
  - `id`, `ingredientName`, `ingredientId`, `category`
  - `quantity`, `unit`, `mealName`, `mealType`
  - `plannedDate`, `isChecked`, `checkedAt`

### Enums
- **MealType**: BREAKFAST, LUNCH, DINNER, SNACK, DESSERT
- **DishCategory**: MAIN_COURSE, SIDE_DISH, APPETIZER, SOUP, SALAD, etc.
- **Cuisine**: ITALIAN, MEXICAN, ASIAN, MEDITERRANEAN, FRENCH, BREAD_BAKERY, SOUPS_STEWS, VEGETARIAN_VEGAN, MEAT_DISHES, DESSERTS_SWEETS
- **MealPlanStatus**: PLANNED, COOKING, COMPLETED, ABORTED

---

## Navigation Structure

### Bottom Navigation (5 tabs)
1. **Plan** - Weekly meal planning calendar
2. **Meals** - Recipe browsing (cuisines + user recipes)
3. **Groceries** - Shopping list
4. **Pantry** - Ingredient inventory
5. **Settings** - App configuration

### Routes
- `plan` - Meal planning screen
- `meals` - Recipe browsing
- `groceries` - Shopping list
- `pantry` - Pantry inventory
- `settings` - Settings
- `scrape_recipe` - AI recipe scraping
- `manual_recipe` - Manual recipe entry
- `recipe_detail/{mealId}` - User recipe details
- `cuisine_meals/{cuisineName}` - Cuisine recipe list
- `bundled_recipe/{cuisineName}/{recipeId}` - Bundled recipe details
- `suggestion` - Meal suggestions (ingredient-based + vibe-based)
- `add_custom_ingredient` - Custom ingredient form
- `meal_plan_detail/{mealPlanId}` - Cooking mode

---

## Key Features

### 1. Recipe Management
- **50+ Bundled Recipes** across 10 cuisines (Italian, Mexican, Asian, etc.)
- **AI Recipe Scraping** via OpenAI API (URL → structured recipe)
- **Manual Recipe Entry** with ingredient matching
- **Recipe Translation** (English, Russian, Romanian)
- Recipes sorted by complexity (fewest ingredients first)
- Detailed + simplified instructions
- Prep/cook times, servings, allergen tracking

### 2. Smart Meal Suggestions
**Three-Tier Ingredient Matching**:
- Perfect Matches (100% ingredients available)
- Good Matches (80-99% available)
- Partial Matches (50-79% available)

**Chef's Pick - Vibe-Based Recommendations**:
- Mood: Quick, Comfort, Healthy, Fancy
- Effort: Easy, Moderate, Challenging
- Serving Size: Solo, Couple, Family, Party
- Works independently from pantry inventory

### 3. Pantry Management
- **Voice Input** for quick ingredient entry
- Automatic ingredient matching with 500+ catalog
- Real-time inventory tracking
- Reserved quantities for planned meals
- Category-based organization (15 categories, 60+ subcategories)
- Unit conversion (metric/imperial)
- Allergen tracking per ingredient

### 4. Grocery List
- Auto-generation from planned meals
- Manual item addition
- Category grouping
- Check-off functionality
- Plain text export for easy sharing via messaging apps

### 5. Meal Planning
- Weekly calendar view
- Time-based suggestions (breakfast in morning, etc.)
- Serving size adjustment (1, 2, 4, 6 people)
- Cooking mode with step-by-step instructions
- Automatic ingredient deduction when cooking
- Meal status tracking (PLANNED → COOKING → COMPLETED)

### 6. Allergen Management
- 7 common allergens: Dairy, Eggs, Fish, Gluten, Nuts, Shellfish, Soy
- Visual allergen tags on recipes
- Ingredient-level allergen info
- Custom allergen profiles

### 7. Localization
- **Full support**: English, Russian, Romanian
- Language selection in onboarding
- Dynamic translation system
- Ingredient/category translation
- Recipe instruction translation

### 8. Customization
- Multiple accent color themes (light/dark)
- Dark/Light mode support
- Font selection (Roboto, Poppins, Lato, etc.)
- Text scale adjustment
- Family size configuration

---

## Screens & ViewModels

### Main Screens
1. **PlanScreen** (PlanViewModel)
   - Weekly meal plan display
   - Grouped by date and meal type
   - Cooking timer for active meals
   - Clear completed meals
   - Navigate to meal plan detail

2. **MealsScreen** (MealsViewModel)
   - Cuisine browsing (10 cuisines)
   - User recipes section (expandable)
   - Add recipe button
   - Recipe preloading for performance

3. **PantryScreen** (PantryViewModel)
   - Hierarchical ingredient display (category → subcategory)
   - Expandable/collapsible sections
   - Voice input integration
   - Edit/delete ingredients
   - Reserved quantity tracking

4. **GroceriesScreen** (GroceriesViewModel)
   - Shopping list with check-off
   - Category grouping
   - Add custom items
   - Move to pantry when checked
   - Export functionality

5. **SuggestionScreen** (SuggestionViewModel)
   - Ingredient-based matching (3 tiers)
   - Vibe-based recommendations
   - Filter by meal type and dish category
   - Missing ingredients display

6. **CuisineMealsScreen** (CuisineMealsViewModel)
   - Bundled recipe list for cuisine
   - Filter by meal type
   - Sort by complexity
   - Allergen preloading

7. **BundledRecipeDetailScreen** (BundledRecipeDetailViewModel)
   - Recipe details with images
   - Ingredient list with allergens
   - Add to meal plan
   - Add to grocery list
   - Serving size adjustment

8. **RecipeDetailScreen** (MealDetailViewModel)
   - User recipe details
   - Edit/delete functionality
   - Similar to bundled recipe detail

9. **MealPlanDetailScreen** (MealPlanDetailViewModel)
   - Cooking mode with step-by-step instructions
   - Ingredient checklist
   - Start/complete/abort cooking
   - Timer display
   - Ingredient substitution

10. **ScrapeRecipeScreen** (ScrapeRecipeViewModel)
    - URL input for recipe scraping
    - OpenAI API integration
    - Ingredient extraction and matching
    - Save to database

11. **ManualRecipeScreen** (ManualRecipeViewModel)
    - Manual recipe entry form
    - Dynamic ingredient list
    - Ingredient matching
    - Custom ingredient creation

12. **SettingsScreen** (SettingsViewModel)
    - Language selection
    - OpenAI API key management
    - Custom grocery list header (max 100 chars, supports emojis)
    - Accent color picker
    - Font selection
    - Text scale
    - Allergen management

### Onboarding Screens
- **WelcomeScreen** - App introduction
- **LanguageSelectionScreen** - Language picker
- **ServingSizeScreen** - Family size configuration
- **OnboardingScreen** - Coordinator

---

## Data Layer

### Local Data
- **BundledRecipeLoader**: Loads recipes from JSON assets
- **ImagePreloader**: Preloads recipe images for performance
- **TranslationSystem**: Manages multi-language support
- **IngredientTranslator**: Translates ingredient names
- **RecipeTranslator**: Translates recipe content
- **CategoryTranslator**: Translates categories
- **SubstituteInitializer**: Initializes ingredient substitutes

### Remote Data
- **OpenAiService**: Recipe scraping via OpenAI API

### Preferences (DataStore)
- **OnboardingPreferences**: Onboarding state, theme, language, custom grocery header
- **LocaleManager**: Language management
- **FavoriteRecipesPreferences**: Favorite recipes (future)

---

## Domain Layer

### Use Cases
- **CreateMealUseCase**: Create user recipe
- **CreateScrapedMealUseCase**: Save scraped recipe
- **UpdateMealUseCase**: Update recipe
- **DeleteMealUseCase**: Delete recipe
- **CreateMealPlanUseCase**: Schedule meal
- **StartCookingUseCase**: Begin cooking
- **CompleteCookingUseCase**: Finish cooking
- **AbortCookingUseCase**: Cancel cooking
- **CheckRecipeIngredientsUseCase**: Check ingredient availability
- **CreateIngredientUseCase**: Add pantry item
- **UpdateIngredientUseCase**: Update pantry item
- **DeleteIngredientUseCase**: Remove pantry item
- **AdjustInventoryUseCase**: Adjust quantities
- **RestockIngredientUseCase**: Restock item
- **AddGroceryItemToPantryUseCase**: Move grocery to pantry
- **CreateAllergenUseCase**: Add allergen
- **DeleteAllergenUseCase**: Remove allergen
- **PreloadCuisineAllergensUseCase**: Cache allergens
- **FixIngredientCategoriesUseCase**: Fix missing categories
- **AddIngredientSubstituteUseCase**: Add substitute

### Utilities
- **IngredientMatcher**: Fuzzy ingredient matching
- **VoiceInputParser**: Parse voice input to ingredients
- **VoiceRecognitionManager**: Speech recognition
- **CategoryInferencer**: Infer ingredient categories
- **InstructionTranslator**: Translate instructions
- **PermissionChecker**: Check app permissions

### Catalogs
- **IngredientCatalog**: 500+ common ingredients with metadata
- **CategoryIcons**: Icons for categories
- **UnitConversion**: Unit conversion logic
- **UnitOptions**: Allowed units per ingredient type
- **NonDeductibleIngredients**: Ingredients not deducted when cooking

---

## UI Components

### Reusable Components
- **RecipeImage**: Optimized recipe image loading
- **QuantityStepper**: Increment/decrement quantity
- **SwipeToDelete**: Swipe-to-delete gesture
- **BottomDrawer**: Bottom sheet drawer
- **AddIngredientDrawer**: Ingredient addition UI
- **VoiceInputBottomSheet**: Voice input UI
- **HapticFeedback**: Haptic feedback utility
- **PerformanceLogger**: Performance monitoring

### Export
- **GroceriesTextExport**: Plain text grocery list export
  - `generatePlainText()`: Full formatted list with emojis, dates, categories
  - `generateCompactText()`: Minimal consolidated list
  - Supports localization and unit translation
  - Smart formatting with meal grouping
- **Color**: Material 3 color scheme
- **AppFonts**: Font family management
- **Theme**: Dynamic theming with accent colors

---

## Bundled Recipes

### Recipe Structure (JSON)
```json
{
  "name": "Recipe Name",
  "instructions": "Detailed step-by-step...",
  "simpleInstructions": "Simplified version...",
  "prepTimeMinutes": 20,
  "cookTimeMinutes": 35,
  "servings": 2,
  "mealType": "BREAKFAST",
  "dishCategory": "MAIN_COURSE",
  "ingredients": [
    {
      "name": "Ingredient Name",
      "quantity": 100,
      "unit": "g",
      "isStarIngredient": true
    }
  ],
  "sourceUrl": "https://...",
  "cuisine": "Asian",
  "id": "recipe_id",
  "imageUrl": "recipes/images/asian/recipe.jpg"
}
```

### Recipe Storage
- **Location**: `app/src/main/assets/recipes/{cuisine}/`
- **Languages**: Each recipe has 3 versions (en, ro, ru)
- **Images**: `app/src/main/assets/recipes/images/{cuisine}/`
- **Total**: 50+ recipes across 10 cuisines

---

## Performance Optimizations

1. **Image Preloading**: Background preload of recipe images
2. **Allergen Caching**: Preload allergens for cuisines
3. **O(1) Ingredient Lookups**: Pre-built catalog maps
4. **Lazy Loading**: LazyColumn/LazyVerticalGrid for lists
5. **Recipe Preloading**: Preload popular cuisines
6. **Efficient Queries**: Room queries with proper indexing
7. **Coroutine Scoping**: Proper lifecycle-aware coroutines

---

## Key Algorithms

### Ingredient Matching (3-Tier System)
```kotlin
// Perfect Match: 100% ingredients available
// Good Match: 80-99% available
// Partial Match: 50-79% available
val matchPercentage = (availableIngredients / totalIngredients) * 100
```

### Vibe-Based Recommendations
```kotlin
// Filters recipes by:
// - Mood (cooking time, complexity)
// - Effort (ingredient count, steps)
// - Serving size (recipe servings)
// Random selection from matching recipes
```

### Serving Size Adjustment
```kotlin
// Adjusts prep/cook times based on servings and ingredient count
val prepAdjustment = when (servings) {
    4 -> if (ingredientCount < 10) 5 else 10
    6 -> if (ingredientCount < 10) 15 else 20
    else -> 0
}
```

### Unit Conversion
```kotlin
// Weight: g ↔ kg ↔ oz ↔ lb
// Volume: ml ↔ L ↔ cup ↔ tbsp ↔ tsp
// Piece: pcs (no conversion)
```

---

## State Management

### UI State Pattern
```kotlin
sealed class ScreenUiState {
    object Loading : ScreenUiState()
    data class Success(val data: List<Item>) : ScreenUiState()
    data class Error(val message: String) : ScreenUiState()
}
```

### ViewModel Pattern
```kotlin
@HiltViewModel
class ScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScreenUiState>(Loading)
    val uiState: StateFlow<ScreenUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            // Load data
        }
    }
}
```

---

## Testing Strategy

### Unit Tests
- Use cases
- ViewModels
- Utilities (IngredientMatcher, VoiceInputParser, etc.)

### Integration Tests
- Repository implementations
- Database operations

### UI Tests
- Composable screens
- Navigation flows

### Test Tools
- JUnit 4
- Kotest (assertions, property testing)
- MockK (mocking)
- Robolectric (Android framework)
- Coroutines Test

---

## Future Enhancements

1. **Firebase Integration**: Family sharing, cloud sync
2. **API Usage Tracking**: OpenAI tier management
3. **Enhanced Voice Recognition**: Longer input duration
4. **Nutritional Information**: Calorie tracking
5. **Shopping List Optimization**: Store layout
6. **Recipe Rating**: Favorites system
7. **More Cuisines**: Expand recipe library
8. **Meal History**: Track cooking history
9. **Recipe Recommendations**: ML-based suggestions
10. **Barcode Scanning**: Quick pantry additions

---

## Important Notes

### Ingredient Deduction
- Some ingredients are NOT deducted when cooking (water, salt, pepper, oil, etc.)
- See `NonDeductibleIngredients` for full list

### Language Handling
- Ingredients created in one language are tagged with `createdInLanguage`
- Translation system handles display in current language
- Fallback to original name if translation unavailable

### Allergen System
- 7 common allergens tracked
- Catalog ingredients have pre-defined allergens
- Custom ingredients can have user-defined allergens
- Allergen count displayed on recipe cards

### Export Functionality
- **Plain Text Export**: Beautiful, formatted grocery lists for sharing
- **Fully Localized**: Exports in the user's selected language (English/Romanian/Russian)
  - Ingredient names translated via TranslationSystem
  - Category names translated via TranslationSystem
  - Unit names from Android string resources
  - App name from Android string resources
  - Header text from Android string resources
  - Servings text from Android string resources
  - Footer text from Android string resources
  - Date formatting respects device locale
- **Custom Header Support**: Users can set a custom header (max 100 characters) in Settings
  - Supports emojis, numbers, and text
  - Falls back to default localized header if empty
  - Stored in OnboardingPreferences
- **Two formats available**:
  - Full format: Organized by meals/categories with emojis and dates
  - Compact format: Simple consolidated list
- **Easy sharing**: Via WhatsApp, SMS, email, or any messaging app
- **Smart formatting**: Removes checked items, groups by category
- Tracks cooking time with countdown
- Deducts ingredients from pantry on completion
- Supports ingredient substitution
- Can abort cooking (no deduction)

### Meal Plan Status Flow
```
PLANNED → (Start Cooking) → COOKING → (Complete) → COMPLETED
                                    ↓ (Abort)
                                  ABORTED
```

---

## Common Patterns

### Navigation
```kotlin
navController.navigate(NavDestination.Screen.createRoute(param))
navController.popBackStack()
```

### Data Passing Between Screens
```kotlin
// Via SavedStateHandle
backStackEntry.savedStateHandle.set("key", value)
backStackEntry.savedStateHandle.get<Type>("key")
```

### Localization
```kotlin
stringResource(R.string.key)
context.getString(R.string.key)
mealType.getLocalizedName(context)
```

### Image Loading
```kotlin
RecipeImage(
    imagePath = meal.imagePath,
    contentDescription = meal.name,
    modifier = Modifier.size(100.dp)
)
```

---

## Development Guidelines

1. **Always use Hilt for DI** - No manual instantiation
2. **Follow Clean Architecture** - Respect layer boundaries
3. **Use StateFlow for UI state** - Reactive UI updates
4. **Localize all strings** - Support 3 languages
5. **Handle loading/error states** - User feedback
6. **Use coroutines properly** - viewModelScope, lifecycleScope
7. **Optimize images** - Use Coil with proper sizing
8. **Test use cases** - Business logic coverage
9. **Follow Material 3 guidelines** - Consistent UI
10. **Document complex logic** - KDoc comments

---

## Quick Reference

### Main Entry Points
- **Application**: `MealPlannerApp.kt`
- **MainActivity**: `MainActivity.kt`
- **Navigation**: `AppNavHost.kt`
- **Database**: `AppDatabase.kt`
- **DI Modules**: `di/` folder

### Key Files to Check
- Recipe loading: `BundledRecipeLoader.kt`
- Translation: `TranslationSystem.kt`
- Ingredient matching: `IngredientMatcher.kt`
- Voice input: `VoiceInputParser.kt`, `VoiceRecognitionManager.kt`
- Unit conversion: `UnitConversion.kt`
- Allergen logic: `PreloadCuisineAllergensUseCase.kt`

---

**Last Updated**: Initial creation
**Version**: 1.0
**Maintainer**: Development Team
