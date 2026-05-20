# Little Chef iOS Migration Plan

> Complete architecture, screen, and data mapping from Android (Kotlin/Compose) to iOS (Swift/SwiftUI)
> Generated from full codebase analysis (May 2026)

---

## 1. Architecture Overview

### Android (Current) → iOS (Target)

| Layer | Android | iOS Target |
|-------|---------|------------|
| **UI** | Jetpack Compose (18 screens) | SwiftUI (18 screens) |
| **State** | ViewModel + StateFlow + MutableStateFlow | @Observable / ObservableObject + @Published |
| **DI** | Hilt/Dagger | Manual DI or Swinject |
| **Navigation** | Navigation Compose (NavHost) | NavigationStack / NavigationPath |
| **Database** | Room (9 entities, 8 DAOs) | SwiftData or GRDB |
| **Preferences** | DataStore (Proto/Preferences) | @AppStorage / UserDefaults / File |
| **Networking** | Ktor (OpenAI calls) | URLSession + async/await |
| **Localization** | Android resources (3 locales) | String catalogs (`.xcstrings`) |
| **Bundled Data** | JSON in assets (200+ recipes) | JSON in Bundle |
| **DLC** | Play Asset Packs (3 packs) | On-Demand Resources (ODR) |
| **Billing** | Play Billing Library | StoreKit 2 |
| **Image Loading** | Coil (in Compose) | AsyncImage / Kingfisher |
| **Fonts** | Custom Roboto/Rubik TTF files | Same TTF files in Bundle |

### Architectural Pattern

```swift
// SwiftUI + MVVM with @Observable (iOS 17+)
struct RecipeListView: View {
    @State private var viewModel = RecipeListViewModel()
    var body: some View { ... }
}

@Observable
@MainActor
class RecipeListViewModel {
    var recipes: [Meal] = []
    var isLoading = false
    private let getRecipesUseCase: GetRecipesUseCase
    
    init(getRecipesUseCase: GetRecipesUseCase = UseCaseProvider.shared.getRecipesUseCase) {
        self.getRecipesUseCase = getRecipesUseCase
    }
    
    func loadRecipes() async { ... }
}
```

---

## 2. Domain Layer — Direct Port (Pure Kotlin → Pure Swift)

All domain models, use cases, repository interfaces, and utilities are **pure Kotlin logic** with no Android dependencies. They translate 1:1 to Swift.

### 2.1 Models to Port (~25 types)

```
Meal / MealPlan / MealPlanEntry / Recipe / Cuisine
Ingredient / CatalogIngredient / GroceryItem
GroceryCategory / NutritionInfo / Allergen
MealType / UnitOptions / InventoryTransaction
PantryItem / SuggestionMeal
CookingState / TimerInfo
```

### 2.2 Use Cases to Port (~20 use cases)

| Use Case | Purpose |
|----------|---------|
| `GetMealsUseCase` | Fetch all user-created meals (scraped + manual) |
| `GetBundledRecipesUseCase` | Fetch bundled recipes by cuisine |
| `GetBundledRecipeDetailUseCase` | Get single bundled recipe |
| `SearchMealsUseCase` | Search across all meal types |
| `CreateMealPlanUseCase` | Create a weekly meal plan |
| `GetMealPlanUseCase` | Load meal plan by ID |
| `GetMealPlanByWeekUseCase` | Get meal plan for a date range |
| `GetNextUncookedMealPlanUseCase` | Auto-navigate to next uncooked day |
| `StartCookingUseCase` | Initialize cooking session |
| `CompleteCookingUseCase` | Mark meal plan entry as cooked |
| `CheckRecipeIngredientsUseCase` | Compare ingredients vs pantry |
| `AddToGroceryListUseCase` | Add missing ingredients to grocery list |
| `GetGroceryItemsUseCase` | Fetch grocery items |
| `GetPantryItemsUseCase` | Fetch pantry items |
| `UpdatePantryItemUseCase` | Update pantry stock |
| `GetNutritionInfoUseCase` | Calculate nutrition for a meal |
| `CreateScrapedMealUseCase` | Save AI-scraped recipe |
| `DuplicateRecipeUseCase` | Copy a bundled recipe to user recipes |
| `GetRecipeSuggestionsUseCase` | Get meal suggestions |
| `UpdateGroceryItemCheckedUseCase` | Toggle grocery check-off |

### 2.3 Repository Interfaces to Port (6 protocols)

```
MealRepository / BundledRecipeRepository / MealPlanRepository
GroceryRepository / PantryRepository / IngredientRepository
```

### 2.4 Domain Utilities to Port (6 files)

| Utility | Android | iOS |
|---------|---------|-----|
| **IngredientMatcher** | Fuzzy name matching (Levenshtein + token) | Same algorithm in Swift |
| **CategoryInferencer** | Auto-categorize by ingredient name keywords | Same logic |
| **UnitConversion** | Unit conversion + compatibility matrix | Same |
| **TimeAdjuster** | Adjust cook/prep time by servings | Same |
| **NutritionCalculator** | Calculate per-serving nutrition | Same |
| **IngredientNormalizer** | Normalize ingredient names | Same |

---

## 3. Data Layer — Platform Replacements

### 3.1 Database: Room → SwiftData

**9 Entities → 9 @Model classes:**

| Room Entity | Key Fields | SwiftData Model |
|-------------|-----------|-----------------|
| `MealEntity` | id, name, type, instructions, servings, prep/cook time, createdAt, cuisineType, isBundled, isScraped, imageUrl, sourceRecipeJson | `Meal` |
| `MealIngredientEntity` | id, mealId, ingredientId, name, quantity, unit | `MealIngredient` |
| `MealPlanEntity` | id, weekStartDate | `MealPlan` |
| `MealPlanEntryEntity` | id, mealPlanId, mealId, dayOfWeek, mealType, isCooked | `MealPlanEntry` |
| `GroceryItemEntity` | id, name, quantity, unit, category, isChecked, mealPlanId | `GroceryItem` |
| `InventoryItemEntity` | id, name, quantity, unit, category, subcategory, allergens, updatedAt, isCustom | `PantryItem` |
| `CustomIngredientEntity` | id, name, unit, category, subcategory, allergens, defaultQuantity | `CustomIngredient` |
| `CatalogIngredientEntity` | id, names (multi-lang), defaultCategory, defaultUnit, compatibleUnits | `CatalogIngredient` |
| `NutritionInfoEntity` | id, mealId, calories, protein, carbs, fat, fiber, sugar, salt, saturatedFat, servingSize | `NutritionInfo` |

### 3.2 Preferences: DataStore → Simple Storage

| Android | iOS |
|---------|-----|
| `OnboardingPreferences` | `@AppStorage` + Codable model |
| `DLCPreferences` | `@AppStorage` for purchases |
| `LocaleManager` | `@AppStorage` language code |

### 3.3 JSON Bundled Data Loading

**Same approach:** Bundle JSON files → decode to Swift structs via `Codable`.

Files to embed in app bundle:
- `bundled_recipes_en.json`, `bundled_recipes_ru.json`, `bundled_recipes_ro.json`
- `nutrition_data.json`
- `category_mappings.json`
- `translation_map.json`

### 3.4 Networking: Ktor → URLSession

**OpenAI Service Pattern:**
```
URLSession.shared.data(for: request) → Decode JSON → Return Result<ScrapedRecipe, Error>
```

Key differences:
- No Ktor dependency needed — use `URLSession` + `async/await`
- Image upload → base64 encode in multipart
- Streaming responses → `URLSession.bytes`

### 3.5 Image Handling

- No image loading library needed initially — `AsyncImage` for bundled recipe images
- Or use `Kingfisher` for advanced caching if needed
- User recipe images stored in app's Documents directory

### 3.6 Asset Loading (DLC Replacement)

| Android (Play Asset Packs) | iOS (On-Demand Resources) |
|---------------------------|--------------------------|
| `AssetPackManager` | `NSBundleResourceRequest` |
| Request → Download → Use | `beginAccessingResources` |
| Asset packs: `indian`, `mexican`, `fastfood` | ODR tags: same names |

---

## 4. UI Layer — Complete Screen Map

### 4.1 All 18 Screens

| # | Android Screen | iOS Screen | Type | ViewModel |
|---|---------------|------------|------|-----------|
| 1 | **PlanScreen** | `PlanView` | Bottom Tab | `PlanViewModel` |
| 2 | **MealsScreen** | `MealsView` | Bottom Tab | `MealsViewModel` |
| 3 | **GroceriesScreen** | `GroceriesView` | Bottom Tab | `GroceriesViewModel` |
| 4 | **PantryScreen** | `PantryView` | Bottom Tab | `PantryViewModel` |
| 5 | **CuisineMealsScreen** | `CuisineMealsView` | Sub-route | `CuisineMealsViewModel` |
| 6 | **BundledRecipeDetailScreen** | `BundledRecipeDetailView` | Sub-route | `BundledRecipeDetailViewModel` |
| 7 | **RecipeDetailScreen** | `RecipeDetailView` | Sub-route | `RecipeDetailViewModel` |
| 8 | **EditRecipeScreen** | `EditRecipeView` | Sub-route | `EditRecipeViewModel` |
| 9 | **ScrapeRecipeScreen** | `ScrapeRecipeView` | Sub-route | `ScrapeRecipeViewModel` |
| 10 | **ManualRecipeScreen** | `ManualRecipeView` | Sub-route | `ManualRecipeViewModel` |
| 11 | **MealPlanDetailScreen** | `MealPlanDetailView` | Sub-route | `MealPlanDetailViewModel` |
| 12 | **SuggestionScreen** | `SuggestionView` | Sub-route | `SuggestionViewModel` |
| 13 | **AddCustomIngredientScreen** | `AddCustomIngredientView` | Sub-route | (shared VM via @Environment) |
| 14 | **SettingsScreen** | `SettingsView` | Sheet/Sub-route | `SettingsViewModel` |
| 15 | **OnboardingScreen** | `OnboardingView` | Modal (first launch) | `OnboardingViewModel` |
| 16 | **LanguageSelectionScreen** | `LanguageSelectionView` | Step 1 | (part of OnboardingVM) |
| 17 | **WelcomeScreen** | `WelcomeView` | Step 2 | (part of OnboardingVM) |
| 18 | **ServingSizeScreen** | `ServingSizeView` | Step 3 | (part of OnboardingVM) |

### 4.2 Key Screen Details

#### PlanScreen / PlanView (Tab 1)
- **Android:** Horizontal calendar strip + meal cards for selected day (3 slots: breakfast/lunch/dinner)
- **iOS:** `HorizontalCalendarView` + `MealSlotCard` for each meal type
- **Interactions:** Tap meal → `MealPlanDetailView`. Long press → context menu (remove, mark cooked). Tap empty slot → `SuggestionView` or recipe picker
- **Data:** `PlanViewModel` loads `MealPlan` for current week, `GetMealPlanByWeekUseCase`
- **Extra:** Auto-navigate to next uncooked day via `GetNextUncookedMealPlanUseCase`

#### MealsScreen / MealsView (Tab 2)
- **Android:** 2 sections: user recipes (scrollable grid) + cuisine categories (horizontal scroll)
- **iOS:** `List` or `ScrollView` with `LazyVGrid` for user recipes + `ScrollView(.horizontal)` for cuisine cards
- **Interactions:** Tap user recipe → `RecipeDetailView`. Tap cuisine → `CuisineMealsView`. Tap + → `ScrapeRecipeView`. DLC cuisines show lock icon, tap triggers purchase flow
- **Search bar** at top → filters both user + bundled recipes

#### GroceriesScreen / GroceriesView (Tab 3)
- **Android:** Grouped list by category (checkbox to mark checked). Delete checked items button
- **iOS:** `List` with `Section` headers by category, `Toggle` for each item
- **Data:** `GroceryRepository`, items auto-populated from meal plan

#### PantryScreen / PantryView (Tab 4)
- **Android:** Hierarchical categories (e.g., Vegetables → Leafy Greens → Spinach). Search + stock count badge
- **iOS:** `List` with hierarchical disclosure (`OutlineGroup` or custom accordion)
- **Data:** `PantryRepository`, CRUD via `AddCustomIngredientView`

#### BundledRecipeDetailScreen / BundledRecipeDetailView
- **Most complex screen.** ~500 lines Compose.
- **Sections:** Hero image → Nutrition card → Time + servings → Ingredients (with check-off) → Instructions → Duplicate button → Add to Plan button
- **State machine:** Loading → Success → Error
- **Cooking mode:** Ingredient highlight + timer + step completion
- **iOS:** `ScrollView` with pinned section headers, `AsyncImage` for hero, custom `NutritionCardView`

### 4.3 Shared UI Components

| Android | iOS |
|---------|-----|
| `NutritionCard` | `NutritionCardView` |
| `TimeAdjuster` (stepper with servings) | `ServingSizeStepper` |
| `IngredientRow` (with check-off) | `IngredientRowView` |
| `CuisineCard` (for category grid) | `CuisineCardView` |
| `MealCard` (for recipe grid) | `MealCardView` |
| `DayCard` (calendar day) | `DayCardView` |
| `MealSlotCard` (meal plan entry) | `MealSlotCardView` |
| `CupertinoPicker` (unit picker) | `Picker` with `.wheel` style |
| `HapticFeedbackUtil` | `UIImpactFeedbackGenerator` |
| `PriceFormatter` | `NumberFormatter` for currency |

---

## 5. Navigation Architecture

### 5.1 Route Structure

```
TabView (4 bottom tabs)
├── PlanView (Tab 1: Plan)
│   ├── MealPlanDetailView(mealPlanId) — sub-route
│   └── SuggestionView — modal/sheet
├── MealsView (Tab 2: Meals)
│   ├── CuisineMealsView(cuisine) — sub-route
│   │   └── BundledRecipeDetailView(cuisine, recipeId) — sub-route
│   ├── RecipeDetailView(mealId) — sub-route
│   │   ├── EditRecipeView(mealId) — sub-route
│   │   └── (navigate to Plan tab / Groceries tab)
│   └── ScrapeRecipeView — sheet/modal
│       └── ManualRecipeView — sub-route (back from scrape)
├── GroceriesView (Tab 3: Groceries)
│   └── AddCustomIngredientSheet(initialName)
└── PantryView (Tab 4: Pantry)
    ├── AddCustomIngredientView(initialName?, ingredientId?) — sub-route
    └── SettingsView — sheet

OnboardingView — modal (presented before TabView on first launch)
```

### 5.2 iOS Implementation

```swift
// TabView + NavigationStack per tab (iOS 16+)
TabView(selection: $selectedTab) {
    NavigationStack {
        PlanView()
            .navigationDestination(for: Route.self) { route in
                switch route {
                case .mealPlanDetail(let id): MealPlanDetailView(mealPlanId: id)
                case .suggestion: SuggestionView()
                default: EmptyView()
                }
            }
    }
    .tabItem { Label("Plan", systemImage: "calendar") }
    .tag(Tab.plan)
    
    NavigationStack {
        MealsView()
            .navigationDestination(for: Route.self) { ... }
    }
    .tabItem { Label("Meals", systemImage: "fork.knife") }
    .tag(Tab.meals)
    
    // ... Groceries, Pantry tabs
}

// Routes enum
enum Route: Hashable {
    case mealPlanDetail(String)
    case cuisineMeals(String)
    case bundledRecipeDetail(cuisine: String, recipeId: String)
    case recipeDetail(String)
    case editRecipe(String)
    case scrapeRecipe
    case manualRecipe
    case suggestion
    case addCustomIngredient(initialName: String, ingredientId: String?)
    case settings
}
```

### 5.3 Auto-collapsing Bottom Tab Bar

- **Android:** Auto-hides bottom nav after 15s inactivity on Plan screen
- **iOS:** Can be done with custom `TabView` or `ZStack` + timer — but not standard SwiftUI. Consider skipping this non-standard behavior or implementing via `UITabBarController` customization

---

## 6. Theme & Design System

### 6.1 Color Scheme

| Token | Light Mode | Dark Mode |
|-------|-----------|-----------|
| Background | #FAF9F9 | #2E2E2E |
| Surface | #E8E8E8 | #3A3A3A |
| On Background | #141414 | #F5F5F5 |
| On Background Variant | #1F1F1F | #F0F0F0 |
| Accent (default light) | #D68C45 (Toasted Almond) | — |
| Accent (default dark) | — | #5398BE (Blue Bell) |
| Error | #BF2727 / #CE3737 | same |

**User-customizable accent color** — persists across launches. Use `@AppStorage` for hex string.

### 6.2 Typography

Android collapses M3 typography to 3 sizes with `textScale` factor:

| Style | Size | Weight | Used For |
|-------|------|--------|----------|
| heading | 24sp × scale | Bold | Screen titles, hero text |
| title | 16sp × scale | Normal | Buttons, headers, descriptions |
| body | 14sp × scale | Normal | All reading text, labels, chips |

### 6.3 Fonts

6 custom font variants, each mapped to `Font.Weight`:

| Font | Filename | Character |
|------|----------|-----------|
| Roboto Light | `roboto_light.ttf` | Thin, elegant |
| Roboto Regular | `roboto_regular.ttf` | System default |
| Roboto Medium | `roboto_medium.ttf` | Slightly bolder |
| Rubik Light | `rubik_light.ttf` | Soft, friendly |
| Rubik Regular | `rubik_regular.ttf` | Rounded, readable |
| Rubik Medium | `rubik_medium.ttf` | Bold, friendly |

### 6.4 Shapes

```swift
// RoundedCornerShape equivalents
let extraSmall: CGFloat = 8
let small: CGFloat = 12
let medium: CGFloat = 16
let large: CGFloat = 20
let extraLarge: CGFloat = 28
```

### 6.5 Other Theme Details

- **Overscroll disabled** — `ScrollView` without bounce (`.scrollBounceBehavior(.basedOnSize)` on iOS 16.4+)
- **Status bar** — matches background color
- **Share sheet** — `ShareLink` for recipe sharing (Android uses Android share intent)

---

## 7. Billing & DLC

### 7.1 StoreKit 2 (iOS 15+)

Replace Play Billing with StoreKit 2:

```swift
// Product definition
let products: [Product] = try await Product.products(for: ["com.littlechef.dlc.indian",
                                                            "com.littlechef.dlc.mexican",
                                                            "com.littlechef.dlc.fastfood"])

// Purchase
let result = try await product.purchase()
switch result {
case .success(let verification):
    // Mark as purchased in UserDefaults
    // Begin ODR download
case .userCancelled: break
case .pending: break
}
```

### 7.2 On-Demand Resources (DLC)

Replace `AssetPackManager` with `NSBundleResourceRequest`:

```swift
let request = NSBundleResourceRequest(tags: ["mexican"])
try await request.beginAccessingResources()
// Resources now available in main bundle
```

### 7.3 DLC Cuisines

| DLC Pack | Cuisine | Asset Tag |
|----------|---------|-----------|
| Indian Delights | `INDIAN` | `indian` |
| Mexican Fiesta | `MEXICAN` | `mexican` |
| Fast & Hungry | `TWO_FAST_TWO_HUNGRY` | `fastfood` |

---

## 8. AI Scraping (OpenAI)

### 8.1 API Integration

Replace Ktor HTTP calls with `URLSession`:

```swift
func scrapeRecipe(from url: String) async throws -> ScrapedRecipe {
    var request = URLRequest(url: URL(string: "https://api.openai.com/v1/chat/completions")!)
    request.httpMethod = "POST"
    request.setValue("Bearer \(apiKey)", forHTTPHeaderField: "Authorization")
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body = OpenAIRequest(model: "gpt-4o", messages: [...])
    request.httpBody = try JSONEncoder().encode(body)
    
    let (data, _) = try await URLSession.shared.data(for: request)
    return try JSONDecoder().decode(OpenAIResponse.self, from: data).toScrapedRecipe()
}
```

### 8.2 Image Upload Support

- **Android:** Takes photo from camera or picks from gallery
- **iOS:** `PHPickerViewController` or `UIImagePickerController` via `PhotosPicker` (iOS 16+)

### 8.3 API Key Storage

- **Android:** Stored in DataStore
- **iOS:** Keychain (using `SecItemAdd`/`SecItemCopyMatching`) — more secure than UserDefaults

---

## 9. Internationalization (i18n)

### 9.1 Locale Support: EN, RO, RU

**Android:** `values/`, `values-ru/`, `values-ro/` string resources
**iOS:** `.xcstrings` files for each language

### 9.2 TranslationSystem (Dynamic Translation)

The Android app has a `TranslationSystem` that translates ingredient/category names to the user's language at runtime. This needs to be ported as a Swift dictionary lookup + Codable JSON decoder:

```swift
struct TranslationSystem {
    private let translations: [String: [String: String]] // [en_term: [locale: translated_term]]
    
    func translate(_ term: String, to locale: String) -> String {
        translations[term.lowercased()]?[locale] ?? term
    }
}
```

### 9.3 Onboarding Language Selection

Presented before the main app. Sets `LocaleManager.language` → stored in `@AppStorage`. On change, the app restarts (or reloads root view).

---

## 10. Implementation Order (Recommended Phases)

### Phase 0: Project Setup (~2-3 days)
- Create Xcode project with SwiftUI lifecycle
- Set up folder structure matching Clean Architecture:
  ```
  LittleChef/
  ├── Domain/
  │   ├── Models/
  │   ├── UseCases/
  │   ├── RepositoryInterfaces/
  │   └── Utilities/
  ├── Data/
  │   ├── Repositories/
  │   ├── Local/
  │   ├── Remote/
  │   └── Preferences/
  ├── Presentation/
  │   ├── Views/
  │   ├── ViewModels/
  │   ├── Components/
  │   └── Navigation/
  ├── Resources/
  │   ├── Assets.xcassets
  │   ├── JSON/
  │   └── Fonts/
  └── App/
      └── LittleChefApp.swift
  ```
- Add bundled JSON files, font TTF files, asset catalog
- Set up DI container

### Phase 1: Domain Layer (~3-4 days)
- Port all models to Swift structs
- Port all use cases to Swift async functions
- Define all repository protocols
- Port all 6 utility classes with unit tests (critical — test behavior matches Android)
- Copy unit test values from Android JUnit tests

### Phase 2: Data Layer Foundation (~4-5 days)
- Set up SwiftData models (9 entities)
- Implement JSON bundled recipe loader
- Implement repository implementations
- Set up preferences storage
- Write Core Data/SwiftData migration logic

### Phase 3: Navigation + Theme (~2-3 days)
- Implement TabView + NavigationStack routing
- Port navigation argument system (enum-based routes)
- Set up theme (Colors + Typography + Shapes)
- Implement font loading
- Set up dark/light mode toggle
- Add accent color customization

### Phase 4: Core Screens (~5-7 days)
Build screens in dependency order:

1. **PantryView** (simplest — list with CRUD)
2. **GroceriesView** (list with check-off)
3. **MealsView + CuisineMealsView** (grid + horizontal scroll)
4. **BundledRecipeDetailView** (most complex — nutrition, ingredients, cooking mode)
5. **RecipeDetailView** (similar to bundled)
6. **EditRecipeView** (form-based)

### Phase 5: Meal Planning (~3-4 days)
7. **PlanView** (calendar + meal slots)
8. **MealPlanDetailView** (cooking mode)
9. **SuggestionView** (recipe suggestions)

### Phase 6: Recipe Creation (~3-4 days)
10. **ScrapeRecipeView** (URL input + image picker + AI parsing)
11. **ManualRecipeView** (form-based recipe creation)
12. **AddCustomIngredientView** (reusable across flows)

### Phase 7: Onboarding + Settings (~2-3 days)
13. **OnboardingView** (3-step flow)
14. **SettingsView** (API key, theme, DLC management)

### Phase 8: DLC + StoreKit (~3-4 days)
- Set up StoreKit 2 configuration
- Implement product listing and purchase flow
- Implement On-Demand Resources for DLC packs
- Wire DLC unlock to cuisine visibility

### Phase 9: UI Polish + i18n (~3-4 days)
- Complete all `.xcstrings` for EN/RO/RU
- Implement dynamic translation system
- Haptic feedback integration
- Share sheet implementation
- App icon, launch screen
- Accessibility (VoiceOver labels)

### Phase 10: Testing + App Store (~5-7 days)
- Unit tests for all use cases and utilities
- UI tests for critical flows
- Test on real devices (iPhone SE → Pro Max)
- App Store Connect setup
- Screenshots for all 3 locales
- Submit for review

---

## 11. Key Technical Decisions

### 11.1 SwiftData vs Core Data
**Recommendation:** SwiftData (iOS 17+). Simpler API, native Swift concurrency, `@Model` macros. The app's data model is straightforward (9 entities, no complex relationships). If iOS 16 support is needed, use GRDB (faster dev than Core Data).

### 11.2 @Observable vs ObservableObject
**Recommendation:** `@Observable` macro (iOS 17+). Cleaner syntax, no `@Published` boilerplate, better performance. Sets minimum deployment target to iOS 17.

### 11.3 Dependency Injection
**Recommendation:** Manual DI with a shared container (`UseCaseProvider`/`ServiceLocator` pattern). The app has ~20 use cases and 6 repositories — not big enough to warrant Swinject/Needle. A simple container class with lazy properties suffices:

```swift
@MainActor
final class DIContainer {
    static let shared = DIContainer()
    
    lazy var mealRepository: MealRepository = LocalMealRepository()
    lazy var getMealsUseCase = GetMealsUseCase(repository: mealRepository)
    // ...
}
```

### 11.4 Error Handling
- Android uses `Result<T>` sealed class pattern
- iOS: Use `throws` with custom `AppError` enum, or use `Result<T, Error>` for consistency
- Map all error states to user-facing messages

### 11.5 Concurrency
- Android: `viewModelScope.launch { }`
- iOS: `Task { await viewModel.load() }` or `async` methods on `@MainActor`
- Use `task()` modifier on SwiftUI views for lifecycle-bound async work

---

## 12. File Reference Map

Each Android file path → iOS counterpart:

| Android | iOS |
|---------|-----|
| `domain/model/*.kt` | `Domain/Models/*.swift` |
| `domain/usecase/*.kt` | `Domain/UseCases/*.swift` |
| `domain/repository/*.kt` | `Domain/RepositoryInterfaces/*.swift` |
| `domain/util/*.kt` | `Domain/Utilities/*.swift` |
| `data/local/dao/*.kt` | `Data/Local/Stores/*.swift` |
| `data/local/entity/*.kt` | `Data/Local/Models/*.swift` |
| `data/local/*.kt` (loaders, managers) | `Data/Local/*.swift` |
| `data/remote/*.kt` | `Data/Remote/*.swift` |
| `data/preferences/*.kt` | `Data/Preferences/*.swift` |
| `data/repository/*.kt` | `Data/Repositories/*.swift` |
| `ui/screens/*Screen.kt` | `Presentation/Views/*View.swift` |
| `ui/screens/*ViewModel.kt` | `Presentation/ViewModels/*ViewModel.swift` |
| `ui/components/*.kt` | `Presentation/Components/*.swift` |
| `ui/navigation/*.kt` | `Presentation/Navigation/*.swift` |
| `ui/theme/*.kt` | `Resources/Theme/*.swift` |
| `ui/onboarding/*.kt` | `Presentation/Views/Onboarding/*.swift` |
| `billing/*.kt` | `Data/StoreKit/*.swift` |
| `MainActivity.kt` | `App/LittleChefApp.swift` |

---

## 13. Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|------------|
| **SwiftData stability** (new API) | Medium | Fallback to GRDB if issues arise |
| **ODR download UX** | Medium | Show download progress for DLC packs (handle slow connections) |
| **Recipe JSON format compatibility** | High | Write integration tests that verify iOS decoding matches Android |
| **OpenAI API response parsing** | Medium | Same JSON schema — shared test fixtures |
| **Navigation parity** | Medium | Navigable tabs pattern differs — test all deep link flows |
| **Cooking mode timer accuracy** | Low | Use Swift `Task.sleep` / `Timer` — same behavior |
| **Haptic feedback parity** | Low | `UIImpactFeedbackGenerator` has different feel — acceptable |
| **Overscroll disable** | Low | `UIScrollView` customization via `UIScrollView.appearance()` |

---

## 14. Total Estimated Effort

| Phase | Days | What |
|-------|------|------|
| 0 — Setup | 2-3 | Project, folders, resources |
| 1 — Domain | 3-4 | Models, use cases, utilities |
| 2 — Data | 4-5 | SwiftData, repos, loaders |
| 3 — Nav + Theme | 2-3 | Routing, colors, fonts |
| 4 — Core Screens | 5-7 | 6 screens (list, detail, grid) |
| 5 — Meal Planning | 3-4 | Calendar, cooking mode |
| 6 — Recipe Creation | 3-4 | Scrape, manual, custom ingredient |
| 7 — Onboarding | 2-3 | 3-step flow + settings |
| 8 — DLC + StoreKit | 3-4 | Purchases, ODR |
| 9 — Polish + i18n | 3-4 | Localization, haptics, share |
| 10 — Testing + App Store | 5-7 | Unit, UI, screenshots, submission |
| **Total** | **35-48** | **~6-10 weeks** |

---

## 15. Quick Start

To begin the migration:

1. Create the Xcode project (iOS 17+, SwiftUI)
2. Set up the `DIContainer` and folder structure
3. Port domain models as Swift structs with `Codable`
4. Port `IngredientMatcher` with unit tests
5. Load and verify bundled recipe JSON decodes correctly
6. Build `PantryView` → `GroceriesView` → `MealsView` in order

Each subsequent phase builds on the prior one. The domain layer has zero dependencies and can be built/tested independently first.
