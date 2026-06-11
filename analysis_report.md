# Little Chef — Deep Analysis & Optimization Report

*Generated 2026-06-06 from full source audit + release build (26MB APK)*

---

## 1. Room Destructive Migration — WORTH FIXING

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** — v1 only, `fallbackToDestructiveMigration()` | **HIGH** — total data loss on schema change | Medium |

**Evidence**: `DatabaseModule.kt:32` uses `.fallbackToDestructiveMigration()`. Schema version = 1 (`AppDatabase.kt:20`). Schema JSON files exist at `app/schemas/com.littlechef.app.data.local.AppDatabase/1.json`. There is also a stale schema from an old package name (`com.familymealplanner`).

**False-positive check**: The app IS currently in development (versionCode 1, versionName 1.0). Destructive migrations are fine until the first production release. But there's no migration plan for when schema changes. The callout is correct but **not urgent pre-ship** — set up proper migrations before v1.0 stable release.

**Verdict**: ✅ Worth fixing — add `Migration` objects before Play Store release. Low effort to define now while schema is fresh.

---

## 2. Crash Reporting — SUPERSEDED

| Status | Impact |
|--------|--------|
| **RESOLVED** — Firebase Analytics + Crashlytics removed entirely (June 11, 2026) | No crash reporting — intentional, no Firebase deps at all |

**Note**: Firebase + all analytics code was removed from the app. App has no crash reporting. Acceptable for current scope.

---

## 3. Kotlin 1.9.21 + Compose BOM 2023.10.01 — WORTH FIXING

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** — 18+ months outdated | **MEDIUM** — build perf, compiler fixes | Medium |

**Evidence**: `build.gradle.kts` shows Kotlin 1.9.21, Compose BOM 2023.10.01, `kotlinCompilerExtensionVersion = "1.5.6"`. The Compose compiler is still a separate KSP plugin (pre-Kotlin 2.0 pattern). Current stable: Kotlin 2.1.x with Compose compiler plugin built-in.

**False-positive check**: The app compiles and runs fine. These are not buggy versions. However, Kotlin 2.0+ ships the Compose compiler as a Kotlin compiler plugin (no KSP dependency), which reduces build complexity and improves incremental compilation. Newer Compose BOMs have Material3 API additions and performance improvements.

The upgrade path is non-trivial: Kotlin 1.9→2.0+ requires updating all KSP plugins, Hilt (need 2.50+), Room (need 2.6.1+), and the Compose compiler config changes completely. This is a **weekend refactor**, not a quick fix.

**Verdict**: ⚠️ Worth planning but not urgent. Budget 1-2 days for the upgrade. Run `compose-upgrade` assistant when ready.



## 4. OpenAI Prompts Embedded in Code — WORTH FIXING (low priority)

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** but cosmetic | **LOW** — code readability, no runtime impact | Low |

**Evidence**: `OpenAiService.kt` — ~310 lines of prompt strings inline. The `RECIPE_EXTRACTION_PROMPT` constant alone is 299 lines. Another `VERIFICATION_PROMPT` at ~100 lines.

**False-positive check**: These are compile-time constants embedded in the APK. R8 strips them if unused, but they ARE used, so they ship in the release APK (~12KB of text). No runtime cost beyond memory. The real cost is developer experience: editing multi-line strings inside Kotlin raw strings with escaped characters is painful.

**Verdict**: ✅ Worth extracting to `assets/prompts/` → load at init. Cleaner iteration on prompts without recompiling code. Low risk, moderate DX benefit.

---

## 5. Split AddIngredientDrawer (1260 lines) — WORTH FIXING (low priority)

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** — file is too large | **LOW** — maintainability only | Medium |

**Evidence**: `AddIngredientDrawer.kt` — 1260 lines. Contains: search mode, category browse mode, custom ingredient rendering (with trash icon + delete), catalog ingredient rendering, quantity picker dialog, allergen display, multiple LazyColumn variants, fragment caching with `refreshTrigger`/`LaunchedEffect`. Also has unused parameter `preferences` (verified in build warnings).

**False-positive check**: The file works correctly, is well-understood by the codebase, and has zero bugs. Splitting it introduces risk of missed imports or broken state. The content is all related to a single UI concern (ingredient selection drawer). 1260 lines is large but not pathological for Compose screens.

**Verdict**: ⚠️ Worth planning before NEXT feature that touches it. Split into: `IngredientSearchContent`, `IngredientBrowseContent`, `IngredientItemRow`, `DeleteIngredientDialog`. Do it when adding next ingredient feature.

---

## 6. API Key Plaintext in DataStore — WORTH FIXING

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** — no encryption | **MEDIUM** — security risk for paid API key | Medium |

**Evidence**: `OnboardingPreferences.kt` uses standard `DataStore<Preferences>` (no encryption). The OpenAI API key is stored as plaintext string key `openai_api_key`. No `EncryptedSharedPreferences` or `androidx.security:crypto` dependency in `build.gradle.kts`.

**False-positive check**: Risk profile depends on threat model:
- DataStore file is in app's private directory → not accessible to other apps
- On rooted devices or with ADB backup → key is readable
- The key funds GPT-4o calls which cost real money
- No `allowBackup` in manifest — actually `android:allowBackup="true"` is set!

**Verdict**: ✅ Worth fixing — add `androidx.security:security-crypto:1.1.0-alpha06`, migrate API key to `EncryptedSharedPreferences`. DataStore encryption is more complex; simplest: keep OpenAI key in EncryptedSharedPreferences alongside DataStore. Also fix `allowBackup="true"` → `false` or at minimum `android:fullBackupContent="@xml/backup_rules"` (already set).

---

## 7. 130+ Kotlin Warnings — PARTIALLY FALSE POSITIVE

| Status | Impact | Effort |
|--------|--------|--------|
| **PARTIAL** — many are minor, a few are bugs | **LOW-MEDIUM** | Medium (bulk cleanup) |

**Evidence**: Release build output shows ~50 warnings. Categories:
- **Unused variables** (~30): `savedLanguage`, `currentLocale`, `navBarWidth`, `matchedTranslatedName`, `perfStart`, `previousStep`, `startedByUserName`, `coroutineScope`, etc.
- **Unused parameters** (~15): `innerPadding`, `category`, `subcategory`, `allergens`, `onRemove`, `validUnits`, `onNavigateToCuisine`, `title` (BottomDrawer), `tag`, `operation` (PerformanceLogger)
- **Shadowed names** (~5): `coroutineScope`, `uiState`, `catalogIngredientNames`, `selectedServings`
- **Always-true conditions** (~4): `it != null`, `subcategory != null`, `createdLanguage != null`
- **Elvis on non-nullable** (~1): `?:` on String

**Real bugs** (low severity):
1. `PlanScreen.kt:54` — `onNavigateToCuisine` parameter never used. Cuisine navigation from plan screen may be broken.
2. `PlanScreen.kt:58-59` — `showAddMealDialog` and `userMeals` declared but never used. Possibly dead code from refactor.
3. `BottomDrawer.kt:25` — `title` parameter never used. The drawer ignores the title. Not a bug, but confusing API.
4. `PerformanceLogger.kt:39-40` — The entire class is a no-op. `start` / `tag` / `operation` unused. Performance logging disabled.
5. `SettingsScreen.kt:43,47,54` — `onNavigateBack`, `activity`, `currentLanguage` all unused. Callback wired but never invoked.
6. `ScrapeRecipeScreen.kt:446` — `dragAmount` unused. Swipe-to-dismiss not fully wired?

**Verdict**: ⚠️ Worth cleaning in a single batch pass. The `onNavigateToCuisine` and `showAddMealDialog` unused variables on PlanScreen suggest a navigation gap. 2-3 actual issues among the noise. Run `./gradlew lint` for full report.

---

## 9. Bonus Finding — No `shrinkResources` in Release Build

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** but minor | **LOW** — ~1-2MB potential savings | Low |

**Evidence**: `build.gradle.kts` has `isMinifyEnabled = true` but no `shrinkResources = true`. The 26MB APK could be ~24-25MB with resource shrinking. Also no `resConfigs` — all 3 locale resources ship (expected, language split disabled), but all Play Core drawables, Material3 locales, etc. ship too.

**False-positive check**: `shrinkResources` requires R8 (which is enabled). It strips unused resource files. For this app, most resources are used (recipe images, localized strings). The savings are likely in Play Core/Asset Delivery/Billing unused assets.

**Verdict**: ⚠️ Worth adding — `shrinkResources = true` + ensure `R8` keeps rules in `proguard-rules.pro` handle dynamic resource lookups (Coil uses resource IDs). One-line change, low risk.

---

## 10. Bonus Finding — No `android:allowBackup` Risk

| Status | Impact | Effort |
|--------|--------|--------|
| **REAL** — API key in backups | **MEDIUM** | Low |

**Evidence**: `AndroidManifest.xml:9` — `android:allowBackup="true"`. With API key in DataStore, ADB backup (on debug builds) or auto-backup (on release) would include the unencrypted API key.

**False-positive check**: On API 31+ (targetSdk 34), auto-backup is opt-in by default for non-system apps... actually, `allowBackup=true` means backup IS allowed. The `fullBackupContent="@xml/backup_rules"` is set. Need to check what's in `backup_rules.xml`.

**Verdict**: ✅ Worth fixing — either set `allowBackup=false` (simplest), or exclude the DataStore files from backup in `backup_rules.xml`. Combined with API key encryption (#8), this is defense-in-depth.

---

# Summary Prioritization

| Priority | Finding | Impact | Effort | Quick Win? |
|----------|---------|--------|--------|------------|
| **P0** | Room migration strategy | HIGH | Med | ⚠️ Before Play Store |
| **P1** | Encrypt API key | MED | Med | ✅ |
| **P1** | Fix allowBackup | MED | Low | ✅ 1 attribute |
| **P1** | Add shrinkResources | LOW | Low | ✅ 1 line |
| **P2** | Extract prompts to assets | LOW | Low | ✅ |
| **P2** | Clean Kotlin warnings | LOW-MED | Med | ⚠️ Check PlanScreen nav |
| **P3** | Kotlin/Compose upgrade | MED | High | Schedule later |
| **P3** | Split AddIngredientDrawer | LOW | Med | On next feature touch |

**Superseded items** (removed June 11, 2026):
- #2 Crashlytics — Firebase/Analytics removed entirely
- #8 BillingManager leak — Google Play Billing removed entirely, all recipes bundled free
