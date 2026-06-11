# Little Chef - Family Meal Planner App Memory

## Project Overview
Android meal planning app. Kotlin + Jetpack Compose. Organize recipes, manage pantry, grocery lists, plan meals with smart ingredient matching + AI recipe scraping.

**Package**: `com.littlechef.app`
**Min SDK**: 27 (Android 8.1), **Target SDK**: 34 (Android 14)
**App Name**: "Little Chef"
**Build**: Gradle Kotlin DSL, Kotlin 1.9.21, Compose BOM 2023.10.01, compileSdk 34
**App Bundle**: `language { enableSplit = false }` — all 3 locales in every APK split

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
- **Database**: Room 2.6.1 (KSP)
- **Navigation**: Navigation Compose 2.7.6
- **Image Loading**: Coil Compose 2.5.0
- **HTTP Client**: Ktor 2.3.7 (OkHttp engine)
- **Serialization**: Kotlinx Serialization 1.6.2
- **Preferences**: DataStore Preferences 1.0.0


---

## Navigation System

### NavHost Setup (`ui/navigation/AppNavHost.kt`)
- `NavHost` + `NavController`
- Onboarding-aware start destination
- Routes in `NavDestination` sealed class (`ui/navigation/NavDestination.kt`)

### Bottom Navigation (4 tabs)
1. **Plan** (`route = "plan"`) — Weekly meal planning calendar
2. **Meals** (`route = "meals"`) — Recipe browsing (cuisines + user recipes)
3. **Groceries** (`route = "groceries"`) — Shopping list
4. **Pantry** (`route = "pantry"`) — Ingredient inventory

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
- SavedStateHandle for passing data back between screens
- `navController.popBackStack()` with inclusive flag for removing intermediate screens
- Scraped/manual recipe flow navigates to new recipe detail after creation



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
- Prefer `collectAsStateWithLifecycle()`
- `by` delegate: `val uiState by viewModel.uiState.collectAsState()`

---

## Database (Room)

### Database Name: `little_chef_db`
### Version: 1 (destructive migrations during dev)

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
- User-customizable accent colors via DataStore
- Status bar color matches background

### Font System
6 options via Google Fonts (Roboto + Rubik):
1. Roboto Light, Regular (default), Medium
2. Rubik Light, Regular, Medium

### Text Scale
- User-adjustable multiplier for ALL typography
- Base sizes: titleSmall 16sp, bodySmall 14sp, labelLarge 16sp, labelSmall 12sp

### Shapes (AppShapes)
| Shape | Radius |
|-------|--------|
| extraSmall | 8.dp |
| small | 12.dp |
| medium | 16.dp |
| large | 20.dp |
| extraLarge | 28.dp |

### Overscroll
**CRITICAL**: Disabled globally in `Theme.kt:169`:
```kotlin
CompositionLocalProvider(LocalOverscrollConfiguration provides null) {
    MaterialTheme(..., content = content)
}
```

---

## UI Components & Patterns

### BottomDrawer (`ui/util/BottomDrawer.kt`)
- `ModalBottomSheet` wrapper for ingredient addition, settings, etc.
- `skipPartiallyExpanded = true` (full expanded)
- **QUIRK**: Must use `modifier.statusBarsPadding()` to prevent drag handle from overlapping status bar
- Content receives `ColumnScope`

### Delete Confirmation Dialog Pattern
**Two styles:**

#### 1. Pantry Item Delete (SwipeToDeleteContainer + Dialog in SwipeToDelete.kt)
- `SwipeToDeleteContainer` wraps list items
- Swipe triggers `Dialog` with `RoundedCornerShape(28.dp)`
- Buttons stacked: Delete (primary) + Cancel (secondaryContainer)

#### 2. Custom Ingredient Delete (AlertDialog in AddIngredientDrawer.kt)
**Pattern used for custom ingredient deletion.** Bin button visible (not swipe). `AlertDialog`:
- `containerColor = MaterialTheme.colorScheme.background`
- `shape = RoundedCornerShape(20.dp)`
- `tonalElevation = 6.dp`
- Title: "Delete {Name}?" (`drawer_delete_custom_title` string, `%1$s` title-cased)
- Text: "This will permanently delete the ingredient from your database." (`drawer_delete_custom_message`)
- Two stacked buttons:
  - Delete: `primary`, full width, hPadding 16.dp, `pantry_delete_button`
  - Cancel: `secondaryContainer`, full width, hPadding 16.dp, `pantry_cancel_button`

#### 3. EditPantryItemDialog delete (PantryScreen.kt:659-725)
- AlertDialog from edit dialog delete icon
- Same style as #2
- Title: "Delete {Name}?", Message: "This will remove the item from your pantry." (`pantry_delete_message`)

### AddIngredientDrawer (`ui/screens/AddIngredientDrawer.kt`)
- ~1260 lines, used from GroceriesScreen and PantryScreen
- Modes: search (`isSearchActive`) or category browse (`showCategories`)
- Custom ingredients show trash icon directly (not swipe-to-delete)
- Delete shows confirmation (see pattern #2 above)
- Delete: `refreshTrigger` increments → `LaunchedEffect(visible, refreshTrigger)` reloads
- `onDeleteCustomIngredient` calls `ingredientRepository.deleteIngredient(ingredient)` directly
- Selection: if `onEditExistingIngredient` provided → edit screen, else → in-drawer quantity dialog

### SwipeToDeleteContainer (`ui/util/SwipeToDelete.kt`)
- Used for pantry edit dialog (trash icon, not swipe)
- Also for meal/recipe lists
- Delete icon scaling animation (0.8f → 1.2f) with spring physics
- Confirmation uses `Dialog` composable with `RoundedCornerShape(28.dp)`

### CupertinoPicker (`ui/components/CupertinoPicker.kt`)
- Shared barrel/carousel selector extracted from 3 duplicates (AddCustomIngredientScreen, ScrapeRecipeScreen, SuggestionScreen)
- Infinite scroll via repeated item list + snap-to-center
- Haptic (`VIRTUAL_KEY`) fires only on scroll settle when item changes — `lastHapticItem` guard + `isSnapping` flag prevent double-fire
- Item height 40dp, vertical padding 40dp (120dp container)
- Used via `DishCategoryStepper`, `MealTypePicker`, `UnitPicker`
- Replaces manual `LazyColumn` + `snapshotFlow` + `debounce` approach

---

## String Resources

### Naming Conventions
- `screen_*` — Screen titles
- `nav_*` — Navigation labels
- `pantry_*` — Pantry screen
- `groceries_*` — Groceries screen
- `recipe_*` — Recipe related
- `meal_plan_*` — Meal plan related
- `add_ingredient_*` — Ingredient addition drawer
- `drawer_*` — Drawer-specific strings
- `button_*` — Common button labels

### Languages
- English: `res/values/strings.xml` (~805 lines)
- Romanian: `res/values-ro/strings.xml` (~545 lines)
- Russian: `res/values-ru/strings.xml` (~575 lines)
- ALL user-facing strings in all 3 languages

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
- `@HiltAndroidApp` — Application (`MealPlannerApp`)
- `@AndroidEntryPoint` — MainActivity
- `@HiltViewModel` — All ViewModels
- `@Inject constructor` — Constructor injection

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
- **Onboarding**: `ServingSizeScreen` +/- stepper (circular 48dp buttons), centered value
- **Range**: 1–6 with min/max enforcement (`alpha(0.4f)` on disabled)
- **Cycle**: Recipe detail screens cycle 1→2→3→4→5→6→1 via `cycleServings()` (tap servings label)
- **Default**: Stored in DataStore as `default_serving_size`, from onboarding, consumed by BundledRecipeDetailViewModel + CuisineMealsViewModel
- **Per-plan override**: `MealPlan.plannedServings: Int?` in Room

### Time Adjustment Formula (`ui/util/TimeAdjuster.kt`)
Shared by ALL screens — preview cards + detail screens use same math:
```kotlin
TimeAdjuster.adjustPrepTime(baseMinutes, baseServings, selectedServings)
TimeAdjuster.adjustCookTime(baseMinutes, baseServings, selectedServings)
```
Formula: `prepAdj = basePrep × (ratio - 1) × 0.35`, `cookAdj = baseCook × (ratio - 1) × 0.05`
Used by: CuisineMealsScreen, PlanScreen, RecipeDetailScreen, BundledRecipeDetailScreen, MealPlanDetailScreen.

### Egg Quantity Rounding
- `roundEggQuantity(quantity: Double, ingredientName: String): Double` in `domain/model/UnitConversion.kt`
- Rounds egg ingredients to nearest 0.5
- Matches name contains "egg" (eggs, egg whites, egg yolks)
- Applied at **15 touch points** across: RecipeDetailScreen (4), BundledRecipeDetailViewModel (5), BundledRecipeDetailScreen (1), MealPlanDetailScreen (1), PlanViewModel (1), StartCookingUseCase (2)

### Nutrition Labels Per Serving
4 files in `domain/model/`, `data/local/`, `ui/util/`, `ui/components/`:
- **`NutritionInfo.kt`** — data class: calories, fatsG, carbsG, proteinG, pieceG (optional)
- **`NutritionLoader.kt`** — `@Singleton`, loads `assets/nutrition/ingredient_nutrition.json` (266 ingredients, ~355 entries) with in-memory cache
- **`NutritionCalculator.kt`** — sums ingredient contributions:
  - `g`/`ml` → `qty × (per100g / 100)`
  - `pcs` → `qty × pieceG × (per100g / 100)`
  - Divide by servings → per-serving `NutritionInfo`
- **`NutritionCard.kt`** — 4×1 Row (Cal | Fat | Carbs | Protein) with `formatNutritionValue()` (public)
- **String keys**: `nutrition_calories_short`, `nutrition_fats_short`, `nutrition_carbs_short`, `nutrition_protein_short`

**Integration**: Rendered INSIDE recipe info Card, below time items with `onSurfaceVariant(alpha=0.2f)` divider. Uses `InfoColumn`. All three detail screens load `NutritionLoader` in ViewModel `init`.

### TimeAdjuster (`ui/util/TimeAdjuster.kt`)
Singleton with two pure functions:
- `adjustPrepTime(baseMinutes, baseServings, selectedServings)` — scales prep 35% per doubling
- `adjustCookTime(baseMinutes, baseServings, selectedServings)` — scales cook 5% per doubling
Shared by all preview cards + detail screens.

### InfoColumn Composable
```kotlin
@Composable
private fun InfoColumn(value: String, label: String, clickable: Boolean = false, onClick: () -> Unit = {})
```
- Value: `bodyMedium`, `FontWeight.Bold` (primary if clickable)
- Label: `bodySmall`, `onSurfaceVariant`
- Used for: prep time, cook time, servings (clickable), total time, nutrition

### Time/Nutrition Divider Style
Dividers use `onSurfaceVariant.copy(alpha = 0.2f)` (not `outlineVariant`). Time dividers 40dp, nutrition Row dividers 28dp. Horizontal separator between sections = `Box(height = 1.dp)`.

### Pantry Screen Specifics
- AddIngredientDrawer via `showAddIngredientDrawer` state
- Edit/delete via EditPantryItemDialog (trash icon → confirm → adjust inventory to 0)
- Swipe-to-delete NOT used in pantry
- **Text scaling**: Item names + availability use `bodyLarge` + `FontWeight.Bold` (was `bodyMedium` + `Normal`)
- **Category/subcategory headers**: `bodyMedium`→`bodyLarge` + `Bold`, item count `bodySmall`→`bodyMedium`

### Groceries Screen Specifics
- SwipeToDeleteGroceryItem: swipe left = delete bin (AlertDialog), swipe right = check
- Delete confirm: primary/cancel buttons, colored background, specific shape
- AddIngredientDrawer for custom items

### Empty State Pattern (Unified)
All 4 main screens share identical empty state:
```
Icon (100dp, alpha=0.6f)  ← screen-specific
Title (bodyLarge + Bold, centered)
Subtitle (bodyLarge + Bold, onSurfaceVariant, centered, hPadding)
```
Unified May 2026:
- **Icon size**: 120dp → **100dp**
- **Title**: `titleMedium` → **`bodyLarge` + `FontWeight.Bold`**
- **Subtitle**: `bodyMedium` → **`bodyLarge` + `FontWeight.Bold`**
- **SuggestionScreen**: Moved from `LazyColumn` (emoji `💡`) to standalone centered `Box` (`ic_sub_whole_spices`)

Screen icons:
| Screen | Icon |
|--------|------|
| Plan | `ic_empty_plan` |
| Meals (Suggestions) | `ic_sub_whole_spices` |
| Groceries | `ic_empty_groceries` |
| Pantry | `ic_empty_pantry` |

---

## Ingredient Catalog

- `IngredientCatalog` (`domain/model/IngredientCatalog.kt`): 500+ common ingredients
- 15 categories, 60+ subcategories
- Each: nameKey, category, subcategory, unit, allergens
- Custom ingredients in Room (`IngredientEntity`)
- AddIngredientDrawer combines catalog + custom

### Category Structure
Meat & Poultry, Seafood, Dairy & Eggs, Vegetables, Fruits, Grains & Bread, Legumes & Beans, Nuts & Seeds, Oils & Fats, Spices & Herbs, Condiments & Sauces, Sweeteners & Baking, Canned & Preserved, Beverages, Snacks & Misc

---

## Translation System (`data/local/TranslationSystem.kt`)
- 3 languages: English (en), Russian (ru), Romanian (ro)
- Dynamic via JSON translation maps
- Ingredients tagged `createdInLanguage`
- Falls back to original name if translation unavailable

### Architecture (refactored May 2026)
- `setLanguage()` — sync, ~0ns, sets currentLanguage field. Main thread.
- `loadTranslationData()` — I/O-bound, loads JSON on Dispatchers.IO. Async.
- Called twice: `MealPlannerApp.onCreate()` (cold start) + `MainActivity.onCreate()` (activity recreation after onboarding)
- `reloadTranslations()` removed (caused double-load)

---

## Utility Components

### HapticFeedbackHelper (`ui/util/HapticFeedback.kt`)
```kotlin
fun performSuccess()     // CONFIRM
fun performDestructive() // LONG_PRESS
fun performError()       // REJECT
fun performLight()       // CLOCK_TICK
```

#### Barrel Selector (CupertinoPicker)
- `ui/components/CupertinoPicker.kt`
- Haptic (`VIRTUAL_KEY`) on scroll settle when item changes, not during scroll
- `lastHapticItem` guard + `isSnapping` flag prevent double-fire
- `rememberHapticFeedback()` in same util file

#### Bin Icon (Delete) Button Haptic Pattern
`Icons.Default.Delete` buttons: `performLight()` on press, `performDestructive()` on confirm:

| Screen | Bin press | Confirm delete |
|--------|-----------|----------------|
| PantryScreen | `performLight()` | `performDestructive()` |
| AddIngredientDrawer | `performLight()` | (in dialog) |
| ManualRecipeScreen | `performDestructive()` (direct) | — |
| RecipeDetailScreen | `performLight()` | (in dialog) |
| PlanScreen | `performLight()` (clear completed) | (in dialog) |
| IngredientDetailScreen | `performLight()` | (in dialog) |
| MealDetailScreen | `performLight()` | (in dialog) |
| MealFormScreen | `performLight()` (remove from form) | — |

### GroceriesTextExport (`ui/export/`)
- Two formats: full (organized by meals/categories with emojis) and compact (simple list)
- Fully localized in 3 languages
- Custom header (max 100 chars, stored in OnboardingPreferences)

---

## Onboarding Flow
Welcome → Language Selection → Serving Size → (accent color theme applied)
- Entry: `MealsScreen` if first launch after onboarding, else `PlanScreen`
- State: `OnboardingPreferences` (DataStore)

---

## Meal Suggestions System
Three-tier ingredient matching:
1. Perfect Matches: 100% available
2. Good Matches: 80-99% available
3. Partial Matches: 50-79% available

*(Vibe-based Chef's Pick removed — unused strings deleted)*

### Filtering (refactored May 21)
- **Removed**: meal type filter (`MealTypePicker` + `filterByMealType()` in VM) — was redundant with dish category
- **Replaced**: dual-column `MealTypePicker` + `DishCategoryPicker` → single `DishCategoryStepper` (CupertinoPicker-based)
- `selectedMealType` removed from `SuggestionUiState.Success`
- `MealSuggestion.mealTypeString` removed

---

## Recipe Assets (All Bundled)

All 230+ recipes (14 cuisines) ship with the app — no in-app purchases, no DLC packs, no asset delivery.

`Cuisine` enum has no `isDLC` or `assetPackName` fields. All recipes are JSON files under `app/src/main/assets/recipes/` organized by cuisine folder.

Recipe folder name = `cuisine.displayName.lowercase()` (see `BundledRecipeLoader.kt:43`)

### Recipe List by Cuisine

#### Two Fast Two Hungry (12 recipes)
1. Cheese Omelette · Chicken Stir Fry · Pasta Aglio e Olio · Beef Tacos · Egg Fried Rice · Cheese Quesadilla · Egg Ramen · Grilled Cheese · Chicken Wrap · Coconut Chicken Curry · Shrimp Noodles · Toast and Egg Scramble

#### Eastern Traditional (12 recipes)
Borscht · Pierogi · Golubtsy · Beef Stroganoff · Pelmeni · Kasha · Shchi · Kotleti · Vareniki · Olivier Salad · Blini · Solyanka

#### Exotic Tropics (12 recipes)
Coconut Curry · Mango Sticky Rice · Pineapple Fried Rice · Grilled Plantains · Papaya Salad · Coconut Rice · Tuna Poke Bowl · Mango Lassi · Tropical Fruit Salad · Coconut Shrimp · Pineapple Salsa · Banana Fritters

### Technical
- JSON schema: `BundledRecipe` + `BundledIngredient` in `BundledRecipeLoader.kt`
- Valid `mealType`: BREAKFAST, LUNCH, DINNER, SNACK, DESSERT
- Valid `dishCategory`: PASTA, SALAD, SOUP, MAIN_COURSE, APPETIZER, SIDE_DISH, BREAD, SEAFOOD, CHICKEN, BEEF, PORK, VEGETARIAN, RICE_BOWL, SANDWICH, PIZZA, DESSERT, BEVERAGE, BAKED_DISH

---

## Important Implementation Details & Quirks

### ModalBottomSheet (BottomDrawer)
- **CRITICAL**: Use `Modifier.statusBarsPadding()` on `ModalBottomSheet`'s `modifier` — prevents drag handle from going behind status bar
- `skipPartiallyExpanded = true`
- `title` parameter unused (legacy)

### Custom Ingredient Delete Flow
- Delete button directly visible: circular red Surface with delete icon
- Shows confirmation AlertDialog (pattern #2 above)
- After delete: `refreshTrigger` increments → `LaunchedEffect(visible, refreshTrigger)` reloads

### Confirmation Dialog Style Reference
Use this for delete confirmations:
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
- Do NOT add screen-level overscroll disabling

### LazyColumn / LazyVerticalGrid
- `contentPadding` with `bottom = 76.dp` for nav bar
- Overscroll already disabled globally

### Navigation Arguments
- Passed via route strings, not NavArguments objects
- Use `backStackEntry.arguments?.getString("key")` or SavedStateHandle

### Allergen Colors
- 9 allergens with display colors (vector icons)

### Unit Conversion (`domain/model/UnitConversion.kt`)
- Weight: g ↔ kg ↔ oz ↔ lb
- Volume: ml ↔ L ↔ cup ↔ tbsp ↔ tsp
- Piece: pcs (no conversion)

### NonDeductibleIngredients
Located in `domain/model/NonDeductibleIngredients.kt`. `isNonDeductibleByName()` removed — all ingredients now deductible.

---

## Recent Fixes (May 2026)

### Navbar Haptic/Navigate on Same Tab (`MainActivity.kt:384-397`)
- Problem: Tapping selected tab fired haptic + navigated (refresh)
- Fix: Wrap haptic + navigate in `if (!selected)` check

### My Recipes Spoiler Flash (`MealsScreen.kt`)
- Problem: Loading showed MyRecipesPlaceholder even for zero recipes, then vanished — brief flash
- Fix: Changed to `if (scrapedMeals.isNotEmpty())`, removed dead code

### Translation System Double-Load (Cold Start)
- Problem: `initialize()` + `reloadTranslations()` loaded same JSON twice (~58KB I/O + parsing on main thread)
- Fix: Split into `setLanguage()` (sync) + `loadTranslationData()` (async). Applied in both `MealPlannerApp.onCreate()` and `MainActivity.onCreate()`.

### Groceries AnimatedVisibility (GroceriesScreen.kt)
- Problem: Expandable groups used `if (expanded)` — popped in/out instantly
- Fix: Merged header + content into `item {}` blocks, wrapped in `AnimatedVisibility` with `expandVertically() + fadeIn()` / `shrinkVertically() + fadeOut()`

### minSdk 26→27 + Lint Cleanup
- **minSdk 27** (`app/build.gradle.kts:15`) — eliminates `windowLightNavigationBar` lint
- **App Bundle language split disabled** — `bundle { language { enableSplit = false } }`
- **Dead SDK_INT branch removed** from `LocaleManager.applyLocale()`
- **Modifier order** fixed in `PremiumPreviewDrawer.kt`
- **@InternalSerializationApi opt-in** suppressed via `lint { disable += "UnsafeOptInUsageError" }`

### Asset Pack Rename (`2fast_2hungry_pack` → `fast_hungry_pack`)
- Problem: Asset pack names can't start with digit
- Updated 7 files

### WebP Image Conversion (~17.5MB APK savings)
- **Problem**: 265 image assets in JPG/PNG — APK size ~38.5MB
- **Fix**: Converted all recipe photos to lossy WebP q60, drawables + mipmaps to lossless WebP. Updated 528 JSON `imageUrl` refs `.jpg`→`.webp`. Added missing `imageUrl` to DLC packs. Updated `MealsScreen.kt` hardcoded DLC preview URLs
- **Result**: APK ~20.9MB (recipe images 30.9→14.6MB, drawables 944→560KB, mipmaps 528→340KB)

### Recipe Name Styling (Detail TopAppBar)
- Changed recipe name in detail TopAppBars to `headlineSmall.copy(fontSize = 22.sp)` (22sp Bold with user font). Removed redundant `titleLarge` vs `headlineSmall` conditional

### ABC Delight Recipe Not Rendering
- Problem: JSON `id: "abc_delight"` but filename was different — no pattern match
- Fix: Renamed 3 locale files to `abc_delight.json`, `abc_delight_ru.json`, `abc_delight_ro.json`

### Serving Size Flicker Fix
- Problem: `selectedServings` initialized to 2, then VM loaded recipe/DataStore — flicker
- Fix: Set `_selectedServings` **before** publishing `_meal`/`_recipe`

### Edit Ingredient Dialog Button Padding (ManualRecipeScreen.kt)
- Problem: Default 24dp padding caused Russian "Сохранить" to wrap
- Fix: Reduced `contentPadding` to `horizontal = 12.dp`

### Grocery Export Separator (GroceriesTextExport.kt)
- Problem: 26 `━` characters
- Fix: Changed to 23

### ManualRecipeScreen Bottom Padding
- Problem: 16dp bottom spacing — navbar obstructed last fields
- Fix: Bottom Spacer 16dp → 80dp

### CupertinoPicker Extraction (shared component)
- Problem: 3 duplicate barrel pickers (AddCustomIngredientScreen, ScrapeRecipeScreen, SuggestionScreen) — ~150 lines each, haptic bugs
- Fix: Extracted into `ui/components/CupertinoPicker.kt` (228 lines). Single source. Haptic fires only on settle (VIRTUAL_KEY), `lastHapticItem` + `isSnapping` guards prevent double-fire

### Delete Button Haptic Pattern (8 screens)
- Problem: Delete bin buttons had no haptic feedback — inconsistent UX
- Fix: `performLight()` on bin icon press, `performDestructive()` on confirm delete. Applied across PantryScreen, AddIngredientDrawer, ManualRecipeScreen, RecipeDetailScreen, PlanScreen, IngredientDetailScreen, MealDetailScreen, MealFormScreen

### Suggestion Screen Filter Refactor
- Problem: Meal type + dish category dual picker was redundant (meal plan always shows all types)
- Fix: Removed `MealTypePicker`, `filterByMealType()`, `selectedMealType` from state. Single `DishCategoryStepper` (CupertinoPicker)

### Pantry Text Scaling
- Problem: Pantry used `bodyMedium` + `Normal` for item text — smaller than Groceries screen
- Fix: All pantry text `bodyMedium`→`bodyLarge` + `FontWeight.Bold`. Category headers same. Item count `bodySmall`→`bodyMedium`

### Card Heights (PlanScreen + CuisineMealsScreen)
- Problem: Fixed `height(140.dp)` on meal plan cards — overflow with long content
- Fix: `height(140.dp)`→`heightIn(min = 140.dp)`. Row uses `height(IntrinsicSize.Min)`. Image uses `fillMaxHeight()`

### Landing Page + GitHub Pages
- Added: `docs/` static site (HTML + 7 screenshots + privacy/terms). GitHub Actions workflow deploys to Pages on push to main

---

## Landing Page & GitHub Pages (`docs/`)
- Static HTML landing page in `docs/` with screenshots, feature highlights, privacy + terms pages
- GitHub Pages deployment via `.github/workflows/deploy-pages.yml` — auto-deploys `docs/` on push to main
- Screenshots: 7 `.webp` images showing key app screens (plan, groceries, pantry, recipe detail, language selection, cuisine selections)
- Privacy policy: `docs/privacy.html`, Terms: `docs/terms.html`

---

## Development Guidelines

1. **Always use Hilt for DI** — no manual instantiation
2. **Respect layer boundaries** — UI doesn't import data layer directly (except ViewModels)
3. **Use StateFlow** — MutableStateFlow → asStateFlow → collect
4. **Localize all strings** — English, Romanian, Russian
5. **Handle loading/error states** — every screen: Loading/Success/Error
6. **Use coroutines properly** — viewModelScope for ViewModels, lifecycleScope for composables
7. **Follow Material 3** — MaterialTheme colors, shapes, typography
8. **String naming**: `screen_*`, `nav_*`, `pantry_*`, `groceries_*`, `recipe_*`, `drawer_*`
9. **Delete confirmations**: AlertDialog, background color, 20dp shape, 6dp elevation, stacked buttons
10. **Ingredient names**: title-cased via `.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }`

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
- `pieceG` optional — for pcs-unit ingredients (eggs: 50g, egg whites: 33g, etc.)
- All values per 100g; `pieceG` converts pcs to grams
- 365 entries

---

## Pending Work

### P0 — Before Play Store
- **Room migration strategy** — add `Migration` objects, remove `fallbackToDestructiveMigration()` (HIGH data loss risk)

### P1 — Quick Wins
- **Encrypt API key** — migrate OpenAI key from plaintext DataStore to `EncryptedSharedPreferences` (androidx.security:security-crypto). Combined with allowBackup.
- **Fix allowBackup** — set `android:allowBackup="false"` in manifest (or exclude DataStore from backup_rules.xml). API key backup leak.
- **Add shrinkResources** — add `shrinkResources = true` to release build. Saves ~1-2MB APK. One line.

### P2 — Low Effort
- **Extract prompts to assets** — move ~310 lines of OpenAI prompts from `OpenAiService.kt` to `assets/prompts/`. Better DX.
- **Clean ~50 Kotlin warnings** — batch pass: unused params, shadowed names, dead code. Check PlanScreen navigation gap.

### Infrastructure
- **GitHub Pages** — repo now private. Landing page (`docs/`) needs separate public repo or alternative hosting (Netlify, Vercel). Deploy from there instead.

### P3 — Plan Later
- **Kotlin 1.9.21→2.1.x + Compose BOM upgrade** — non-trivial (KSP→built-in compiler, Hilt 2.50+, Room 2.6.1+). Budget 1-2 days.
- **Split AddIngredientDrawer.kt** — 1260-line file. Split when next feature touches ingredient selection.

---

**Last Updated**: June 11, 2026
