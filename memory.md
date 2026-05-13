# Little Chef - Family Meal Planner App Memory

## Project Overview
Little Chef is a comprehensive Android meal planning application built with Kotlin and Jetpack Compose. It helps families organize recipes, manage pantry inventory, create grocery lists, and plan meals with smart ingredient matching and AI-powered recipe scraping.

**Package**: `com.littlechef.app`
**Min SDK**: 27 (Android 8.1), **Target SDK**: 34 (Android 14)
**App Name**: "Little Chef"
**Build System**: Gradle with Kotlin DSL, Kotlin 1.9.21, Compose BOM 2023.10.01, compileSdk 34
**App Bundle**: Language split disabled (`language { enableSplit = false }`) so all 3 locales ship in every APK split

---

## Architecture

### Clean Architecture + MVVM
```
app/src/main/java/com/littlechef/app/
├── data/              # Data layer (Room, DataStore, repositories)
├── domain/            # Business logic (models, interfaces, use cases)
├── ui/                # Presentation (screens, viewmodels, navigation, theme)
├── di/                # Hilt dependency injection modules
└── utils/             # App-level utilities
```

### Key Libraries
- **UI**: Jetpack Compose Material3 (BOM 2023.10.01)
- **DI**: Dagger Hilt 2.48.1
- **Database**: Room 2.6.1 (with KSP)
- **Navigation**: Navigation Compose 2.7.6
- **Image Loading**: Coil Compose 2.5.0
- **HTTP Client**: Ktor 2.3.7 (OkHttp engine)
- **Serialization**: Kotlinx Serialization 1.6.2
- **Preferences**: DataStore Preferences 1.0.0
- **Billing**: Google Play Billing 6.1.0
- **Asset Delivery**: Play Asset Delivery 2.2.2

---

## Navigation System

### NavHost Setup (`ui/navigation/AppNavHost.kt`)
- Uses `NavHost` with `NavController`
- Onboarding-aware start destination
- Routes defined in `NavDestination` sealed class (`ui/navigation/NavDestination.kt`)

### Bottom Navigation (4 tabs)
1. **Plan** (`route = "plan"`) - Weekly meal planning calendar
2. **Meals** (`route = "meals"`) - Recipe browsing (cuisines + user recipes)
3. **Groceries** (`route = "groceries"`) - Shopping list
4. **Pantry** (`route = "pantry"`) - Ingredient inventory

### Routes with Arguments
```kotlin
NavDestination.RecipeDetail.createRoute(mealId)  // -> "recipe_detail/{mealId}"
NavDestination.CuisineMeals.createRoute(cuisineName)
NavDestination.BundledRecipeDetail.createRoute(cuisineName, recipeId)
NavDestination.MealPlanDetail.createRoute(mealPlanId)
NavDestination.AddCustomIngredient.createRoute(initialName, ingredientId)
NavDestination.AddCustomIngredientForGrocery.createRoute(initialName)
NavDestination.AddCustomIngredientForRecipe.createRoute(initialName)
```

### Special Navigation Patterns
- SavedStateHandle for passing data back between screens (e.g., ManualRecipeScreen receives custom ingredient data)
- `navController.popBackStack()` with inclusive flag for removing intermediate screens
- Scraped/manual recipe flow navigates to the new recipe detail after creation

---

## State Management

### ViewModel Pattern
```kotlin
@HiltViewModel
class ScreenViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
}
```

### UI State Pattern (Sealed Interface)
```kotlin
sealed interface ScreenUiState {
    object Loading : ScreenUiState
    data class Success(val data: List<Item>) : ScreenUiState
    data class Error(val message: String) : ScreenUiState
}
```

### State Collection
- Use `collectAsStateWithLifecycle()` for lifecycle-aware collection (preferred)
- Use `by` delegate pattern: `val uiState by viewModel.uiState.collectAsState()`

---

## Database (Room)

### Database Name: `little_chef_db`
### Version: 1 (destructive migrations during development)

### 9 Entities
| Entity | Table | Key Fields |
|--------|-------|------------|
| `MealEntity` | meals | id, name, instructions, simpleInstructions, prepTimeMinutes, cookTimeMinutes, servings, mealType, dishCategory, imagePath, isScraped, isBundled, createdInLanguage |
| `MealIngredientEntity` | meal_ingredients | mealId, ingredientName, quantity, unit, isStarIngredient |
| `IngredientEntity` | ingredients | id, name, unit, category, subcategory, preferredDisplayUnit, createdInLanguage |
| `AllergenEntity` | allergens | id, name, displayName |
| `IngredientAllergenEntity` | ingredient_allergens | ingredientId, allergenId (junction) |
| `IngredientSubstituteEntity` | ingredient_substitutes | ingredientId, substituteName, notes |
| `MealPlanEntity` | meal_plans | id, mealId, plannedDate, mealType, status (PLANNED/COOKING/COMPLETED/ABORTED), plannedServings, startedAt, completedAt |
| `InventoryTransactionEntity` | inventory_transactions | id, ingredientId, quantityChange, reason, createdAt |
| `GroceryItemEntity` | grocery_items | id, ingredientName, ingredientId, quantity, unit, category, mealName, mealType, plannedDate, isChecked |

### Foreign Keys
- CASCADE delete: MealPlan → Meal, MealIngredient → Meal
- Allergens table prepopulated with 9 common allergens via DatabaseModule callback

---

## Domain Models (Key)

### Meal
```kotlin
data class Meal(
    val id: String, name: String, instructions: String?, simpleInstructions: String?,
    prepTimeMinutes: Int, cookTimeMinutes: Int, servings: Int,
    mealType: MealType, dishCategory: DishCategory?,
    imagePath: String?, isScraped: Boolean, isBundled: Boolean,
    createdInLanguage: String, ingredients: List<MealIngredient>,
    sourceUrl: String?, cuisine: String?,
    createdAt: Long, updatedAt: Long
)
```

### Ingredient
```kotlin
data class Ingredient(
    val id: String, name: String, unit: String,
    category: String?, subcategory: String?,
    preferredDisplayUnit: String?,
    createdInLanguage: String = "en",
    createdAt: Long, updatedAt: Long,
    allergens: List<Allergen> = emptyList(),
    substitutes: List<IngredientSubstitute> = emptyList()
)
```

### MealPlan Status Flow
```
PLANNED → (Start Cooking) → COOKING → (Complete) → COMPLETED
                                    ↓ (Abort)
                                  ABORTED
```

---

## Theme

### File: `ui/theme/Theme.kt`

### Color Schemes
- Custom light/dark schemes (dynamic colors disabled)
- **Light default**: Toasted Almond (#FFD68C45)
- **Dark default**: Blue Bell (#FF5398be)
- User-customizable accent colors stored in DataStore
- Status bar color matches background

### Font System
6 font options via Google Fonts (Roboto + Rubik families):
1. Roboto Light, Regular (default), Medium
2. Rubik Light, Regular, Medium

### Text Scale
- User-adjustable, applies multiplier to ALL typography levels
- Base sizes customized: titleSmall 16sp, bodySmall 14sp, labelLarge 16sp, labelSmall 12sp

### Shapes (AppShapes)
| Shape | Radius |
|-------|--------|
| extraSmall | 8.dp |
| small | 12.dp |
| medium | 16.dp |
| large | 20.dp |
| extraLarge | 28.dp |

### Overscroll
**CRITICAL**: Overscroll/bounce effects are disabled globally at the theme level:
```kotlin
CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
    MaterialTheme(..., content = content)
}
```
This is done in `Theme.kt:169` and applies to the entire app.

---

## UI Components & Patterns

### BottomDrawer (`ui/util/BottomDrawer.kt`)
- Reusable `ModalBottomSheet` wrapper used for ingredient addition, settings, etc.
- Uses `skipPartiallyExpanded = true` (opens fully expanded)
- **IMPORTANT QUIRK**: Must use `modifier.statusBarsPadding()` to prevent the drag handle from overlapping the status bar
- Content receives `ColumnScope` for composable content

### Delete Confirmation Dialog Pattern
**Two styles used in the codebase:**

#### 1. Pantry Item Delete (SwipeToDeleteContainer + Dialog in SwipeToDelete.kt)
- `SwipeToDeleteContainer` wraps list items
- Shows swipe action, triggers `Dialog` with `RoundedCornerShape(28.dp)` surface
- Buttons stacked vertically: Delete (primary) + Cancel (secondaryContainer)
- Title + message in centered Text

#### 2. Custom Ingredient Delete (AlertDialog in AddIngredientDrawer.kt)
**This is the pattern we use for custom ingredient deletion.** The `DrawerCustomIngredientItem` has a delete bin button directly visible (not swipe). Clicking it shows an `AlertDialog` with:
- `containerColor = MaterialTheme.colorScheme.background`
- `shape = RoundedCornerShape(20.dp)`
- `tonalElevation = 6.dp`
- Title: "Delete {Name}?" (uses `drawer_delete_custom_title` string resource with ingredient name as `%1$s`, title-cased)
- Text: "This will permanently delete the ingredient from your database." (uses `drawer_delete_custom_message`)
- Two buttons stacked vertically:
  - Delete: `primary` color, full width, horizontal padding 16.dp, text from `pantry_delete_button`
  - Cancel: `secondaryContainer` color, full width, horizontal padding 16.dp, text from `pantry_cancel_button`

#### 3. EditPantryItemDialog delete (PantryScreen.kt, lines 659-725)
- AlertDialog triggered from the edit dialog's delete icon button
- Same style as #2 (background, shape 20dp, tonalElevation 6dp, same button layout)
- Title: "Delete {Name}?" with ingredient name
- Message: "This will remove the item from your pantry." (`pantry_delete_message`)

### AddIngredientDrawer (`ui/screens/AddIngredientDrawer.kt`)
- Large composable (~1260 lines) used from both GroceriesScreen and PantryScreen
- Modes: search mode (`isSearchActive`) or category browse mode (`showCategories`)
- Custom ingredients have a delete bin button (trash icon) directly visible on each item (not swipe-to-delete)
- Deleting a custom ingredient shows confirmation dialog (see pattern #2 above)
- After deletion: `refreshTrigger` increments → `LaunchedEffect` reloads all custom ingredients
- `onDeleteCustomIngredient` calls `ingredientRepository.deleteIngredient(ingredient)` directly
- Selection flows: if `onEditExistingIngredient` is provided → navigates to edit screen, else → shows in-drawer quantity dialog

### SwipeToDeleteContainer (`ui/util/SwipeToDelete.kt`)
- Used for the pantry edit dialog (trash icon button, not swipe)
- And for meal/recipe lists
- Shows a delete icon with scaling animation (0.8f → 1.2f) with spring physics
- Confirmation dialog uses `Dialog` composable (not AlertDialog) with `RoundedCornerShape(28.dp)`

---

## String Resources

### Naming Conventions
- `screen_*` - Screen titles
- `nav_*` - Navigation labels
- `pantry_*` - Pantry screen
- `groceries_*` - Groceries screen
- `recipe_*` - Recipe related
- `meal_plan_*` - Meal plan related
- `add_ingredient_*` - Ingredient addition drawer
- `drawer_*` - Drawer-specific strings
- `button_*` - Common button labels

### Languages
- English: `res/values/strings.xml` (~805 lines)
- Romanian: `res/values-ro/strings.xml` (~545 lines)
- Russian: `res/values-ru/strings.xml` (~575 lines)
- ALL user-facing strings MUST be added in all 3 languages

---

## Dependency Injection (Hilt)

### 5 Modules (all `SingletonComponent`)
| Module | Provides |
|--------|----------|
| `AppModule` | Coroutine dispatchers, `StringBuilder` |
| `DatabaseModule` | AppDatabase, db file migration, all 9 DAOs |
| `RepositoryModule` | `@Binds` all 6 repository interfaces → implementations |
| `NetworkModule` | Ktor HttpClient, Json serializer |
| `ImageModule` | Coil ImageLoader |

### Hilt Annotations
- `@HiltAndroidApp` - Application (`MealPlannerApp`)
- `@AndroidEntryPoint` - MainActivity
- `@HiltViewModel` - All ViewModels
- `@Inject constructor` - Constructor injection

---

## Key Screens & ViewModels

| Screen | ViewModel | Description |
|--------|-----------|-------------|
| PlanScreen | PlanViewModel | Weekly meal plan calendar, grouped by date/meal type |
| MealsScreen | MealsViewModel | Cuisine browsing + user recipes |
| GroceriesScreen | GroceriesViewModel | Shopping list with check-off, export |
| PantryScreen | PantryViewModel | Hierarchical pantry (category → subcategory), edit dialog |
| SuggestionScreen | SuggestionViewModel | Ingredient-based + vibe-based meal suggestions |
| ScrapeRecipeScreen | ScrapeRecipeViewModel | OpenAI URL recipe scraping |
| ManualRecipeScreen | ManualRecipeViewModel | Manual recipe creation with ingredient matching |
| SettingsScreen | SettingsViewModel | Language, API key, accent color, font, text scale |
| MealPlanDetailScreen | MealPlanDetailViewModel | Cooking mode with step-by-step, substitutions |
| CuisineMealsScreen | CuisineMealsViewModel | Bundled recipe list per cuisine |
| BundledRecipeDetailScreen | BundledRecipeDetailViewModel | Bundled recipe details |
| RecipeDetailScreen | RecipeDetailViewModel | User recipe details |

### Serving Size System
- **Onboarding**: `ServingSizeScreen` shows a +/- stepper (circular 48dp buttons) with centered value display
- **Range**: 1–6 with min/max enforcement (visual dimming at bounds via `alpha(0.4f)` on disabled button)
- **Cycle**: Recipe detail screens cycle 1→2→3→4→5→6→1 via `cycleServings()` (tap servings label)
- **Default**: Stored in DataStore as `default_serving_size`, initialized from onboarding, consumed by BundledRecipeDetailViewModel and CuisineMealsViewModel
- **Per-plan override**: `MealPlan.plannedServings: Int?` persisted in Room

### Time Adjustment Formula (`ui/util/TimeAdjuster.kt`)
Shared utility used by ALL screens — preview cards and detail screens now produce the same math:
```kotlin
TimeAdjuster.adjustPrepTime(baseMinutes, baseServings, selectedServings)
TimeAdjuster.adjustCookTime(baseMinutes, baseServings, selectedServings)
```
Formula: `prepAdj = basePrep × (ratio - 1) × 0.35`, `cookAdj = baseCook × (ratio - 1) × 0.05`
Used by: CuisineMealsScreen, PlanScreen, RecipeDetailScreen, BundledRecipeDetailScreen, MealPlanDetailScreen.

### Egg Quantity Rounding
- `roundEggQuantity(quantity: Double, ingredientName: String): Double` in `domain/model/UnitConversion.kt`
- Rounds egg-related ingredients to nearest 0.5 (e.g., 2.5 eggs → 2.5, 1.33 → 1.5, 0.2 → 0.5)
- Matches on name contains "egg" (eggs, egg whites, egg yolks)
- Applied at **15 touch points** across: RecipeDetailScreen (4), BundledRecipeDetailViewModel (5), BundledRecipeDetailScreen (1), MealPlanDetailScreen (1), PlanViewModel (1), StartCookingUseCase (2)

### Nutrition Labels Per Serving
4 files added to `domain/model/`, `data/local/`, `ui/util/`, `ui/components/`:
- **`NutritionInfo.kt`** — data class: calories, fatsG, carbsG, proteinG, pieceG (optional, for pcs conversion)
- **`NutritionLoader.kt`** — `@Singleton`, loads `assets/nutrition/ingredient_nutrition.json` (266 ingredients, ~355 entries) with in-memory cache
- **`NutritionCalculator.kt`** — sums ingredient contributions:
  - `g`/`ml` units → `qty × (per100g / 100)`
  - `pcs` units → `qty × pieceG × (per100g / 100)`
  - Divides total by servings → per-serving `NutritionInfo`
- **`NutritionCard.kt`** — 4×1 Row (Cal | Fat | Carbs | Protein) with `formatNutritionValue()` (public)
- **String keys**: `nutrition_calories_short`, `nutrition_fats_short`, `nutrition_carbs_short`, `nutrition_protein_short`

**Integration**: Nutrition info rendered INSIDE the recipe info Card, below time items with a thin `onSurfaceVariant(alpha=0.2f)` divider. Reuses the same `InfoColumn` composable for visual consistency. All three detail screens load `NutritionLoader` in ViewModel `init`.

### TimeAdjuster (`ui/util/TimeAdjuster.kt`)
Singleton with two pure functions for serving-based time scaling:
- `adjustPrepTime(baseMinutes, baseServings, selectedServings)` — scales prep 35% per doubling
- `adjustCookTime(baseMinutes, baseServings, selectedServings)` — scales cook 5% per doubling
Shared by all preview cards and detail screens for consistency.

### InfoColumn Composable
```kotlin
@Composable
private fun InfoColumn(value: String, label: String, clickable: Boolean = false, onClick: () -> Unit = {})
```
- Value: `bodyMedium`, `FontWeight.Bold` (primary color if clickable)
- Label: `bodySmall`, `onSurfaceVariant`
- Used for: prep time, cook time, servings (clickable), total time, and nutrition values in the recipe info Card

### Time/Nutrition Divider Style
Thin vertical/horizontal dividers use `onSurfaceVariant.copy(alpha = 0.2f)` (not `outlineVariant`). Applied to both the time item vertical dividers (40dp) and the nutrition Row vertical dividers (28dp). Horizontal separator between time and nutrition sections uses a full-width `Box(height = 1.dp)`.

### Pantry Screen Specifics
- AddIngredientDrawer is opened via `showAddIngredientDrawer` state
- Edit/delete via EditPantryItemDialog (trash icon in title bar → confirmation dialog → adjust inventory to 0)
- Swipe-to-delete is NOT used in pantry screen (despite SwipeToDeleteContainer existing)

### Groceries Screen Specifics
- SwipeToDeleteGroceryItem: swipe left reveals delete bin (confirms via AlertDialog), swipe right checks item
- Delete confirmation dialog: primary/cancel buttons, colored background, specific shape
- AddIngredientDrawer opens for adding custom items

### Empty State Pattern (Unified)
All 4 main screens (Plan, Meals→Suggestions, Groceries, Pantry) share an identical empty state layout:

```
Icon (100dp, alpha=0.6f)  ← specific to each screen
Title (bodyLarge + Bold, centered)
Subtitle (bodyLarge + Bold, onSurfaceVariant, centered, hPadding)
```

Unified in May 2026 across all 4 screens:
- **Icon size**: 120dp → **100dp** (consistent sizing)
- **Title text**: `titleMedium` → **`bodyLarge` + `FontWeight.Bold`** (follows the 3-size typography system)
- **Subtitle text**: `bodyMedium` → **`bodyLarge` + `FontWeight.Bold`** (consistent sizing)
- **SuggestionScreen empty state**: Moved from inside `LazyColumn` (with emoji `💡`) to standalone `Box` centered on screen (with `ic_sub_whole_spices` icon), matching the other 3 screens

Screen-specific icons:
| Screen | Icon Resource |
|--------|--------------|
| Plan | `ic_empty_plan` |
| Meals (Suggestions) | `ic_sub_whole_spices` |
| Groceries | `ic_empty_groceries` |
| Pantry | `ic_empty_pantry` |

---

## Ingredient Catalog

- `IngredientCatalog` (domain/model/IngredientCatalog.kt) contains 500+ common ingredients
- Organized into 15 categories with 60+ subcategories
- Each catalog ingredient has: nameKey, category, subcategory, unit, allergens
- Custom ingredients created by user are stored in Room (IngredientEntity)
- AddIngredientDrawer combines catalog + custom ingredients

### Category Structure
- 15 categories: Meat & Poultry, Seafood, Dairy & Eggs, Vegetables, Fruits, Grains & Bread, Legumes & Beans, Nuts & Seeds, Oils & Fats, Spices & Herbs, Condiments & Sauces, Sweeteners & Baking, Canned & Preserved, Beverages, Snacks & Misc

---

## Translation System (`data/local/TranslationSystem.kt`)
- Supports 3 languages: English (en), Russian (ru), Romanian (ro)
- Dynamic translation via JSON-based translation maps
- Ingredients tagged with `createdInLanguage`
- Falls back to original name if translation unavailable

### Architecture (refactored May 2026)
- `setLanguage()` — sync, ~0ns, sets currentLanguage field. Called on main thread.
- `loadTranslationData()` — I/O-bound, loads JSON files on Dispatchers.IO. Called async.
- Called twice: once in `MealPlannerApp.onCreate()` (after super.onCreate), once in `MainActivity.onCreate()` (for activity recreation after onboarding language change)
- `reloadTranslations()` removed — was causing double-load

---

## Utility Components

### HapticFeedbackHelper (`ui/util/HapticFeedback.kt`)
```kotlin
fun performSuccess()     // CONFIRM
fun performDestructive() // LONG_PRESS
fun performError()       // REJECT
fun performLight()       // CLOCK_TICK
```

### GroceriesTextExport (`ui/export/`)
- Two formats: full (organized by meals/categories with emojis) and compact (simple list)
- Fully localized in all 3 languages
- Custom header support (max 100 chars, stored in OnboardingPreferences)

---

## Onboarding Flow
Screens (in order): Welcome → Language Selection → Serving Size → (accent color theme applied)
- Entry points: `MealsScreen` if first launch after onboarding, else `PlanScreen`
- State tracked via `OnboardingPreferences` (DataStore)

---

## Meal Suggestions System
Three-tier ingredient matching:
1. Perfect Matches: 100% ingredients available
2. Good Matches: 80-99% available
3. Partial Matches: 50-79% available

*(Vibe-based Chef's Pick feature was removed — unused strings deleted)*

---

## Asset Packs (DLC)

### Cuisine Enum (`domain/model/Cuisine.kt`)
```kotlin
enum class Cuisine(
    val displayName: String,
    val isDLC: Boolean = false,
    val assetPackName: String? = null
) {
    // ... built-in cuisines ...
    TWO_FAST_TWO_HUNGRY(
        displayName = "Two Fast Two Hungry",
        isDLC = true,
        assetPackName = "fast_hungry_pack"
    ),
    EASTERN_TRADITIONAL(
        displayName = "Eastern Traditional",
        isDLC = true,
        assetPackName = "eastern_traditional_pack"
    ),
    EXOTIC_TROPICS(
        displayName = "Exotic Tropics",
        isDLC = true,
        assetPackName = "exotic_tropics_pack"
    )
}
```

**Important:** Recipe folder name = `cuisine.displayName.lowercase()` (see `BundledRecipeLoader.kt:43`)

### Two Fast Two Hungry Pack (`:fast_hungry_pack`)
- **Asset Pack Name**: `fast_hungry_pack`
- **Folder**: `two fast two hungry/` (lowercase of displayName)
- **Price**: $0.99
- **12 Recipes** (each in EN/RO/RU):
  1. Cheese Omelette (`5_minute_omelette.json`)
  2. Chicken Stir Fry (`speedy_stir_fry.json`)
  3. Pasta Aglio e Olio (`quick_pasta_aglio_e_olio.json`)
  4. Beef Tacos (`10_minute_tacos.json`)
  5. Egg Fried Rice (`fast_fried_rice.json`)
  6. Cheese Quesadilla (`express_quesadilla.json`)
  7. Egg Ramen (`rapid_ramen_bowl.json`)
  8. Grilled Cheese (`quick_grilled_cheese.json`)
  9. Chicken Wrap (`speedy_chicken_wrap.json`)
  10. Coconut Chicken Curry (`15_minute_curry.json`)
  11. Shrimp Noodles (`fast_noodle_bowl.json`)
  12. Toast and Egg Scramble (`quick_toast_skillet.json`)

**String IDs** (EN/RO/RU strings.xml): `premium_2fast_cheese_omelette`, `premium_2fast_chicken_stir_fry`, etc.
**Preview Images**: `app/src/main/assets/recipes/images/2fast2hungry/` (main assets, accessible before purchase)

### Eastern Traditional Pack (`:eastern_traditional_pack`)
- **Status**: COMPLETE — 12 recipes, 3 languages (EN/RO/RU)
- **Asset Pack Name**: `eastern_traditional_pack`
- **Folder**: `eastern traditional/` (lowercase of displayName)
- **Price**: $1.49
- **12 Recipes** (each in EN/RO/RU):

| # | Recipe | EN File ID | String ID | Meal Type | Category |
|---|--------|-----------|-----------|-----------|----------|
| 1 | Borscht (Beet Soup) | `borscht.json` | `premium_recipe_borscht` | DINNER | SOUP |
| 2 | Pierogi (Potato & Cheese Dumplings) | `pierogi.json` | `premium_recipe_pierogi` | DINNER | MAIN_COURSE |
| 3 | Golubtsy (Stuffed Cabbage Rolls) | `golubtsy.json` | `premium_recipe_golubtsy` | DINNER | MAIN_COURSE |
| 4 | Beef Stroganoff | `beef_stroganoff.json` | `premium_recipe_stroganoff` | DINNER | MAIN_COURSE |
| 5 | Pelmeni (Siberian Dumplings) | `pelmeni.json` | `premium_recipe_pelmeni` | DINNER | MAIN_COURSE |
| 6 | Kasha (Buckwheat Porridge) | `kasha.json` | `premium_recipe_kasha` | LUNCH | SIDE_DISH |
| 7 | Shchi (Cabbage Soup) | `shchi.json` | `premium_recipe_shchi` | DINNER | SOUP |
| 8 | Kotleti (Russian Meat Patties) | `kotleti.json` | `premium_recipe_kotleti` | DINNER | MAIN_COURSE |
| 9 | Vareniki (Cherry Dumplings) | `vareniki.json` | `premium_recipe_vareniki` | DESSERT | DESSERT |
| 10 | Olivier Salad (Russian Salad) | `olivier_salad.json` | `premium_recipe_olivier` | LUNCH | SALAD |
| 11 | Blini (Russian Pancakes) | `blini.json` | `premium_recipe_blini` | BREAKFAST | MAIN_COURSE |
| 12 | Solyanka (Meat Soup) | `solyanka.json` | `premium_recipe_solyanka` | DINNER | SOUP |

**String IDs** (EN/RO/RU strings.xml): `premium_recipe_borscht` through `premium_recipe_solyanka`
**Preview Images**: `app/src/main/assets/recipes/images/easterntraditional/` (main assets, accessible before purchase) — placeholder images created, replace with real images

### Exotic Tropics Pack (`:exotic_tropics_pack`)
- **Status**: COMPLETE — 12 recipes, 3 languages (EN/RO/RU)
- **Asset Pack Name**: `exotic_tropics_pack`
- **Folder**: `exotic tropics/` (lowercase of displayName)
- **Price**: $1.49
- **12 Recipes** (each in EN/RO/RU):

| # | Recipe | EN File ID | String ID | Meal Type | Category |
|---|--------|-----------|-----------|-----------|----------|
| 1 | Coconut Curry | `coconut_curry.json` | `premium_recipe_coconut_curry` | DINNER | MAIN_COURSE |
| 2 | Mango Sticky Rice | `mango_sticky_rice.json` | `premium_recipe_mango_sticky_rice` | DESSERT | DESSERT |
| 3 | Pineapple Fried Rice | `pineapple_fried_rice.json` | `premium_recipe_pineapple_fried_rice` | LUNCH | RICE_BOWL |
| 4 | Grilled Plantains | `grilled_plantains.json` | `premium_recipe_plantains` | SNACK | SIDE_DISH |
| 5 | Papaya Salad | `papaya_salad.json` | `premium_recipe_papaya_salad` | LUNCH | SALAD |
| 6 | Coconut Rice | `coconut_rice.json` | `premium_recipe_coconut_rice` | LUNCH | SIDE_DISH |
| 7 | Tuna Poke Bowl | `tuna_poke_bowl.json` | `premium_recipe_tuna_poke` | DINNER | RICE_BOWL |
| 8 | Mango Lassi | `mango_lassi.json` | `premium_recipe_mango_lassi` | BREAKFAST | BEVERAGE |
| 9 | Tropical Fruit Salad | `tropical_fruit_salad.json` | `premium_recipe_tropical_fruit_salad` | SNACK | DESSERT |
| 10 | Coconut Shrimp | `coconut_shrimp.json` | `premium_recipe_coconut_shrimp` | DINNER | SEAFOOD |
| 11 | Pineapple Salsa | `pineapple_salsa.json` | `premium_recipe_pineapple_salsa` | SNACK | APPETIZER |
| 12 | Banana Fritters | `banana_fritters.json` | `premium_recipe_banana_fritters` | DESSERT | DESSERT |

**String IDs** (EN/RO/RU strings.xml): `premium_recipe_coconut_curry` through `premium_recipe_banana_fritters`
**Preview Images**: `app/src/main/assets/recipes/images/exotictropics/` (main assets, accessible before purchase) — placeholder images created, replace with real images

### Technical
- Uses Play Asset Delivery + Google Play Billing
- JSON schema: `BundledRecipe` + `BundledIngredient` in `BundledRecipeLoader.kt`
- Valid `mealType`: BREAKFAST, LUNCH, DINNER, SNACK, DESSERT
- Valid `dishCategory`: PASTA, SALAD, SOUP, MAIN_COURSE, APPETIZER, SIDE_DISH, BREAD, SEAFOOD, CHICKEN, BEEF, PORK, VEGETARIAN, RICE_BOWL, SANDWICH, PIZZA, DESSERT, BEVERAGE, BAKED_DISH

---

## Important Implementation Details & Quirks

### ModalBottomSheet (BottomDrawer)
- CRITICAL: Always use `Modifier.statusBarsPadding()` on the `ModalBottomSheet`'s `modifier` parameter to prevent the drag handle from going behind the status bar.
- Uses `skipPartiallyExpanded = true` for full-screen appearance.
- The `title` parameter in `BottomDrawer` is unused (legacy).

### Custom Ingredient Delete Flow
- Delete button is directly visible (not swipe-to-delete): a circular red Surface with delete icon
- Shows confirmation AlertDialog (see pattern #2 above)
- After deletion: `refreshTrigger` is incremented, which triggers `LaunchedEffect(visible, refreshTrigger)` to reload ingredients

### Confirmation Dialog Style Reference
Always use this style for delete confirmations:
```kotlin
AlertDialog(
    onDismissRequest = { /* dismiss */ },
    containerColor = MaterialTheme.colorScheme.background,
    shape = RoundedCornerShape(20.dp),
    tonalElevation = 6.dp,
    title = { /* centered title */ },
    text = { /* centered message */ },
    confirmButton = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(/* primary color, full width, hPadding 16 */)
            Button(/* secondaryContainer color, full width, hPadding 16 */)
        }
    }
)
```

### Overscroll
- Disabled globally in `Theme.kt:169` via `CompositionLocalProvider(LocalOverscrollConfiguration provides null)`
- Do NOT add additional overscroll disabling at the screen level

### LazyColumn / LazyVerticalGrid
- Used throughout for lists (recipes, ingredients, meal plans)
- `contentPadding` with `bottom = 76.dp` to account for nav bar
- Overscroll is already disabled globally, no need for extra modifiers

### Navigation Arguments
- Arguments passed via route strings, not NavArguments objects
- Use `backStackEntry.arguments?.getString("key")` or SavedStateHandle

### Allergen Colors
- 9 allergens with specific display colors (imported as vector icons)

### Unit Conversion (`domain/model/UnitConversion.kt`)
- Weight: g ↔ kg ↔ oz ↔ lb
- Volume: ml ↔ L ↔ cup ↔ tbsp ↔ tsp
- Piece: pcs (no conversion)
- Non-deductible ingredients: water, salt, pepper, oil, spices, etc.

### NonDeductibleIngredients
Certain ingredients are NOT deducted when cooking (water, salt, pepper, oil, etc.). Located in `domain/model/NonDeductibleIngredients.kt`. The deprecated `isNonDeductibleByName()` method was removed — all ingredients are now deductible.

---

## Recent Fixes (May 2026)

### Navbar Haptic/Navigate on Same Tab (`MainActivity.kt:384-397`)
- Problem: Tapping an already-selected bottom nav tab fired haptic + navigated (refreshing screen)
- Fix: Wrapped haptic + navigate in `if (!selected)` check; only `lastInteractionTime` updates when already on tab

### My Recipes Spoiler Flash (`MealsScreen.kt`)
- Problem: Loading state showed MyRecipesPlaceholder ("My Recipes" + spinner) even for users with zero recipes, then vanished when Room emitted empty data — brief flash
- Fix: Changed condition from `if (isLoading || scrapedMeals.isNotEmpty())` to `if (scrapedMeals.isNotEmpty())`, removed dead code (MyRecipesPlaceholder, isLoading variable)

### Translation System Double-Load (Cold Start Issue 2)
- Problem: `initialize()` loaded 2 JSON files on main thread, then `reloadTranslations()` in MainActivity cleared cache and reloaded same files — double-load (~58KB I/O + JSON parsing on main thread for non-English)
- Fix: Split into `setLanguage()` (sync) + `loadTranslationData()` (async I/O). Applied in both `MealPlannerApp.onCreate()` (cold start) and `MainActivity.onCreate()` (activity recreation after onboarding). Ensures correct language immediately after onboarding without restart.

### Groceries AnimatedVisibility (GroceriesScreen.kt)
- Problem: Expandable section groups (meal/recipe groups, category groups) used plain `if (expanded)` — content popped in/out instantly
- Fix: Merged header + content into single `item {}` blocks, wrapped content in `AnimatedVisibility` with `expandVertically() + fadeIn()` / `shrinkVertically() + fadeOut()`, used `Column` + `forEach` instead of lazy calls

### minSdk 26→27 + Lint Cleanup
- **minSdk bumped to 27** (`app/build.gradle.kts:15`) — eliminates `windowLightNavigationBar` API lint in themes.xml. All deps support API 21+. No refactor needed.
- **App Bundle language split disabled** — `bundle { language { enableSplit = false } }` added so all 3 languages (EN/RO/RU) ship in every APK split; LocaleManager changes locale at runtime and would miss strings otherwise.
- **Dead SDK_INT branch removed** from `LocaleManager.applyLocale()` — `Build.VERSION.SDK_INT >= N` is always true at minSdk 27.
- **Modifier parameter order** fixed in `PremiumPreviewDrawer.kt` — optional `imageUrl` before optional `modifier` is bad Compose convention; swapped.
- **@InternalSerializationApi opt-in** suppressed via `lint { disable += "UnsafeOptInUsageError" }` — false positive with kotlinx.serialization + KSP plugin.

### Asset Pack Rename (`2fast_2hungry_pack` → `fast_hungry_pack`)
- Problem: Play Asset Pack names can't start with a digit. Renamed from `2fast_2hungry_pack` to `fast_hungry_pack`.
- Updated 7 files: asset pack folder, build.gradle.kts pack name, settings.gradle.kts include, Cuisine.kt enum, memory docs.

### ABC Delight Recipe Not Rendering
- Problem: RecipeTranslator generates filenames from JSON `id` field. ABC Delight had `id: "abc_delight"` but file was named `abc_pudding_avocado_banana_chocolate_delight_desserts & sweets.json` — no pattern matched.
- Fix: Renamed 3 locale files (EN/RU/RO) to `abc_delight.json`, `abc_delight_ru.json`, `abc_delight_ro.json`. Verified: only 1 mismatch across 164 EN recipes.

### Serving Size Flicker Fix (RecipeDetail + BundledRecipeDetail ViewModels)
- Problem: When opening a recipe, `selectedServings` was initialized to a hardcoded `2`, then the ViewModel loaded the recipe/DataStore and updated it. If the recipe had a different serving size (e.g. 4), users saw a ~1s flash of stale servings → correct value, causing ingredient quantities to visibly recalculate.
- Fix: Reordered assignments in `loadMeal()` and `loadRecipe()` so `_selectedServings` is set **before** `_meal`/`_recipe` is published to the UI. The screen now renders with the correct serving size on first composition.

### Edit Ingredient Dialog Button Padding (ManualRecipeScreen.kt)
- Problem: Both Save and Cancel buttons used default Material 3 horizontal padding (24dp). Russian "Сохранить" wrapped to 2 lines.
- Fix: Reduced `contentPadding` to `horizontal = 12.dp` on both buttons.

### Grocery Export Separator (GroceriesTextExport.kt)
- Problem: Horizontal separator line was 26 `━` characters.
- Fix: Changed all 4 occurrences from 26→23 characters.

### ManualRecipeScreen Bottom Padding
- Problem: Scrollable content ended with only 16dp bottom spacing — navbar pill obstructed the last fields.
- Fix: Increased bottom Spacer from 16dp → 80dp (matches LazyColumn `contentPadding` pattern used on other screens).

---

## Development Guidelines

1. **Always use Hilt for DI** - No manual instantiation
2. **Respect layer boundaries** - UI doesn't import data layer directly (except ViewModels)
3. **Use StateFlow for UI state** - MutableStateFlow → asStateFlow → collect
4. **Localize all strings** - Must add in English, Romanian, AND Russian
5. **Handle loading/error states** - Every screen should handle Loading/Success/Error
6. **Use coroutines properly** - viewModelScope for ViewModels, lifecycleScope for composables
7. **Follow Material 3** - Use MaterialTheme colors, shapes, typography
8. **String resources naming**: `screen_*`, `nav_*`, `pantry_*`, `groceries_*`, `recipe_*`, `drawer_*`
9. **Delete confirmation dialogs** always follow the same style: AlertDialog with background color, 20dp shape, 6dp elevation, stacked buttons
10. **Ingredient names** in delete dialogs should be title-cased via `.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }`

### Nutrition JSON Format (`assets/nutrition/ingredient_nutrition.json`)
```json
{
  "ingredient_name": {
    "kcal": 155,
    "fats": 11.0,
    "carbs": 1.1,
    "protein": 13.0,
    "pieceG": 50.0
  }
}
```
- `pieceG` is optional — only for pcs-unit ingredients (eggs: 50g, egg whites: 33g, etc.)
- All values per 100g; `pieceG` converts pcs to grams before calculation
- 365 entries covering 84 unique DLC + 266 unique bundled ingredients

**Last Updated**: May 13, 2026 (edit ingredient dialog padding, grocery separator length, ManualRecipeScreen bottom padding)
