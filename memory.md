# Little Chef - Family Meal Planner App Memory

## Project Overview
Little Chef is a comprehensive Android meal planning application built with Kotlin and Jetpack Compose. It helps families organize recipes, manage pantry inventory, create grocery lists, and plan meals with smart ingredient matching and AI-powered recipe scraping.

**Package**: `com.familymealplanner`
**Min SDK**: 26 (Android 8.0), **Target SDK**: 34 (Android 14)
**App Name**: "Little Chef"
**Build System**: Gradle with Kotlin DSL, Kotlin 1.9.21, Compose BOM 2023.10.01, compileSdk 34

---

## Architecture

### Clean Architecture + MVVM
```
app/src/main/java/com/familymealplanner/
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

### Database Name: `family_meal_planner_db`
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
- English: `res/values/strings.xml` (798 lines)
- Romanian: `res/values-ro/strings.xml`
- Russian: `res/values-ru/strings.xml`
- ALL user-facing strings MUST be added in all 3 languages

---

## Dependency Injection (Hilt)

### 5 Modules (all `SingletonComponent`)
| Module | Provides |
|--------|----------|
| `AppModule` | Coroutine dispatchers, PermissionChecker, VoiceRecognitionManager |
| `DatabaseModule` | AppDatabase, all 9 DAOs |
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
| RecipeDetailScreen | MealDetailViewModel | User recipe details |

### Pantry Screen Specifics
- AddIngredientDrawer is opened via `showAddIngredientDrawer` state
- Edit/delete via EditPantryItemDialog (trash icon in title bar → confirmation dialog → adjust inventory to 0)
- Swipe-to-delete is NOT used in pantry screen (despite SwipeToDeleteContainer existing)

### Groceries Screen Specifics
- SwipeToDeleteGroceryItem: swipe left reveals delete bin (confirms via AlertDialog), swipe right checks item
- Delete confirmation dialog: primary/cancel buttons, colored background, specific shape
- AddIngredientDrawer opens for adding custom items

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

Chef's Pick (vibe-based, independent of pantry):
- Mood: Quick, Comfort, Healthy, Fancy
- Effort: Easy, Moderate, Challenging
- Serving Size: Solo, Couple, Family, Party

---

## Asset Packs (DLC)
- `:2fast_2hungry` - Quick meals pack
- `:eastern_traditional_pack` - Eastern European cuisine
- `:exotic_tropics_pack` - Tropical flavors
- Uses Play Asset Delivery + Google Play Billing

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
Certain ingredients are NOT deducted when cooking (water, salt, pepper, oil, etc.). Located in `domain/model/NonDeductibleIngredients.kt`.

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

**Last Updated**: May 2026
