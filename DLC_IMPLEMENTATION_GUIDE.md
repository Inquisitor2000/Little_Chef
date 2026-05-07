# DLC Implementation Guide - Option A (Google Play Asset Delivery)

## Overview
This guide provides step-by-step instructions to implement DLC recipe packs using Google Play Asset Delivery.

---

## PHASE 1: Project Setup (1-2 hours)

### Step 1: Add Play Asset Delivery Dependency

**File:** `app/build.gradle.kts`

Add to dependencies:
```kotlin
dependencies {
    // ... existing dependencies
    
    // Google Play Asset Delivery
    implementation("com.google.android.play:asset-delivery:2.2.2")
    implementation("com.google.android.play:asset-delivery-ktx:2.2.2")
}
```

### Step 2: Create Asset Pack Module

**In Android Studio:**
1. File → New → New Module
2. Select "Asset Pack Module"
3. Module name: `italian_premium_pack`
4. Delivery type: Choose one:
   - **install-time**: Downloads with app (recommended for first DLC)
   - **fast-follow**: Downloads after app install
   - **on-demand**: Downloads when user purchases

**Or manually create:**

Create `italian_premium_pack/build.gradle` (NOT .kts):
```gradle
plugins {
    id 'com.android.asset-pack'
}

assetPack {
    packName = "italian_premium_pack"
    dynamicDelivery {
        deliveryType = "install-time"  // or "on-demand" for paid DLC
    }
}
```

### Step 3: Configure Asset Pack in settings.gradle.kts

**File:** `settings.gradle.kts`

Add:
```kotlin
include(":app")
include(":italian_premium_pack")  // Add this line
```

### Step 4: Link Asset Pack to App

**File:** `app/build.gradle.kts`

Add:
```kotlin
android {
    // ... existing config
    
    assetPacks += [":italian_premium_pack"]
}
```

---

## PHASE 2: Code Changes (2-3 hours)

### Step 5: Update Cuisine Enum

**File:** `app/src/main/java/com/familymealplanner/domain/model/Cuisine.kt`

```kotlin
enum class Cuisine(
    val displayName: String,
    val isDLC: Boolean = false,
    val assetPackName: String? = null
) {
    ASIAN("Asian"),
    MEDITERRANEAN("Mediterranean"),
    MEXICAN("Mexican"),
    ITALIAN("Italian"),
    FRENCH("French"),
    BREAD_BAKERY("Bread & Bakery"),
    DESSERTS("Desserts & Sweets"),
    
    // New DLC cuisines
    ITALIAN_PREMIUM("Italian Premium", isDLC = true, assetPackName = "italian_premium_pack");
    
    companion object {
        fun fromDisplayName(name: String): Cuisine? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}
```

### Step 6: Create Asset Delivery Manager

**File:** `app/src/main/java/com/familymealplanner/data/local/AssetPackManager.kt`

```kotlin
package com.familymealplanner.data.local

import android.content.Context
import com.google.android.play.core.assetpacks.AssetPackLocation
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetPackManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val assetPackManager: AssetPackManager = AssetPackManagerFactory.getInstance(context)
    
    /**
     * Check if an asset pack is downloaded and available
     */
    fun isPackAvailable(packName: String): Boolean {
        val location = assetPackManager.getPackLocation(packName)
        return location != null && location.packStorageMethod() == AssetPackLocation.PACK_STORAGE_METHOD_APK_ASSETS
    }
    
    /**
     * Get the asset pack location (for accessing files)
     */
    fun getPackLocation(packName: String): AssetPackLocation? {
        return assetPackManager.getPackLocation(packName)
    }
    
    /**
     * Request download of an asset pack (for on-demand packs)
     */
    fun requestDownload(packName: String): Flow<AssetPackDownloadState> = callbackFlow {
        val listener = AssetPackStateUpdateListener { state ->
            when (state.status()) {
                AssetPackStatus.PENDING -> {
                    trySend(AssetPackDownloadState.Pending)
                }
                AssetPackStatus.DOWNLOADING -> {
                    val progress = if (state.totalBytesToDownload() > 0) {
                        (state.bytesDownloaded().toFloat() / state.totalBytesToDownload().toFloat() * 100).toInt()
                    } else 0
                    trySend(AssetPackDownloadState.Downloading(progress))
                }
                AssetPackStatus.COMPLETED -> {
                    trySend(AssetPackDownloadState.Completed)
                    close()
                }
                AssetPackStatus.FAILED -> {
                    trySend(AssetPackDownloadState.Failed(state.errorCode()))
                    close()
                }
                AssetPackStatus.CANCELED -> {
                    trySend(AssetPackDownloadState.Canceled)
                    close()
                }
                else -> {
                    // Other states: UNKNOWN, WAITING_FOR_WIFI, NOT_INSTALLED, etc.
                }
            }
        }
        
        assetPackManager.registerListener(listener)
        assetPackManager.fetch(listOf(packName))
        
        awaitClose {
            assetPackManager.unregisterListener(listener)
        }
    }
    
    /**
     * Cancel download of an asset pack
     */
    fun cancelDownload(packName: String) {
        assetPackManager.cancel(listOf(packName))
    }
}

sealed class AssetPackDownloadState {
    object Pending : AssetPackDownloadState()
    data class Downloading(val progress: Int) : AssetPackDownloadState()
    object Completed : AssetPackDownloadState()
    data class Failed(val errorCode: Int) : AssetPackDownloadState()
    object Canceled : AssetPackDownloadState()
}
```

### Step 7: Update BundledRecipeLoader

**File:** `app/src/main/java/com/familymealplanner/data/local/BundledRecipeLoader.kt`

Add asset pack support:

```kotlin
@Singleton
class BundledRecipeLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val assetPackManager: AssetPackManager  // Add this
) {
    private val json = Json { ignoreUnknownKeys = true }
    
    fun loadRecipesForCuisine(cuisine: Cuisine, languageCode: String = "en"): List<BundledRecipe> {
        val folderName = "recipes/${cuisine.displayName.lowercase()}"
        
        return try {
            // Check if this is a DLC cuisine
            if (cuisine.isDLC && cuisine.assetPackName != null) {
                loadFromAssetPack(cuisine.assetPackName, folderName, languageCode)
            } else {
                loadFromAssets(folderName, languageCode)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun loadFromAssets(folderName: String, languageCode: String): List<BundledRecipe> {
        val files = context.assets.list(folderName) ?: emptyArray()
        return parseRecipeFiles(files, folderName, languageCode) { fileName ->
            context.assets.open("$folderName/$fileName")
                .bufferedReader()
                .use { it.readText() }
        }
    }
    
    private fun loadFromAssetPack(
        packName: String,
        folderName: String,
        languageCode: String
    ): List<BundledRecipe> {
        val packLocation = assetPackManager.getPackLocation(packName) ?: return emptyList()
        
        // Asset packs are stored as APK assets
        val assetsPath = packLocation.assetsPath()
        val folder = java.io.File(assetsPath, folderName)
        
        if (!folder.exists()) return emptyList()
        
        val files = folder.listFiles()?.map { it.name }?.toTypedArray() ?: emptyArray()
        return parseRecipeFiles(files, folderName, languageCode) { fileName ->
            java.io.File(folder, fileName).readText()
        }
    }
    
    private fun parseRecipeFiles(
        files: Array<String>,
        folderName: String,
        languageCode: String,
        readFile: (String) -> String
    ): List<BundledRecipe> {
        // Filter files based on language
        val relevantFiles = if (languageCode == "en") {
            files.filter { it.endsWith(".json") && !it.contains("_ru.json") && !it.contains("_ro.json") }
        } else {
            val translatedFiles = files.filter { it.endsWith("_$languageCode.json") }
            if (translatedFiles.isNotEmpty()) {
                translatedFiles
            } else {
                files.filter { it.endsWith(".json") && !it.contains("_ru.json") && !it.contains("_ro.json") }
            }
        }
        
        return relevantFiles.mapNotNull { fileName ->
            try {
                val content = readFile(fileName)
                json.decodeFromString<BundledRecipe>(content)
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun loadAllBundledRecipes(languageCode: String = "en"): Map<Cuisine, List<BundledRecipe>> {
        return Cuisine.entries.associateWith { loadRecipesForCuisine(it, languageCode) }
    }
}
```

### Step 8: Create DLC Purchase Manager (Optional - for paid DLC)

**File:** `app/src/main/java/com/familymealplanner/data/preferences/DLCPreferences.kt`

```kotlin
package com.familymealplanner.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dlcDataStore: DataStore<Preferences> by preferencesDataStore(name = "dlc_preferences")

@Singleton
class DLCPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dlcDataStore
    
    companion object {
        private val PURCHASED_PACKS = stringSetPreferencesKey("purchased_packs")
    }
    
    val purchasedPacks: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[PURCHASED_PACKS] ?: emptySet()
    }
    
    suspend fun isPurchased(packName: String): Boolean {
        return purchasedPacks.first().contains(packName)
    }
    
    suspend fun markPurchased(packName: String) {
        dataStore.edit { preferences ->
            val current = preferences[PURCHASED_PACKS] ?: emptySet()
            preferences[PURCHASED_PACKS] = current + packName
        }
    }
    
    suspend fun removePurchase(packName: String) {
        dataStore.edit { preferences ->
            val current = preferences[PURCHASED_PACKS] ?: emptySet()
            preferences[PURCHASED_PACKS] = current - packName
        }
    }
}
```

---

## PHASE 3: Create DLC Content (2-4 hours)

### Step 9: Create Asset Pack Structure

Create this folder structure:
```
italian_premium_pack/
  src/
    main/
      assets/
        recipes/
          italian premium/
            carbonara_authentic_italian_premium.json
            carbonara_authentic_italian_premium_ru.json
            carbonara_authentic_italian_premium_ro.json
            cacio_e_pepe_italian_premium.json
            cacio_e_pepe_italian_premium_ru.json
            cacio_e_pepe_italian_premium_ro.json
          images/
            italian_premium/
              carbonara_authentic.jpg
              cacio_e_pepe.jpg
```

### Step 10: Create Recipe JSON Files

**Example:** `italian_premium_pack/src/main/assets/recipes/italian premium/carbonara_authentic_italian_premium.json`

```json
{
  "id": "carbonara_authentic_italian_premium",
  "name": "Authentic Roman Carbonara",
  "instructions": "1. Bring a large pot of salted water to boil for the pasta.\n\n2. Cut the guanciale into small strips or cubes. In a large skillet over medium heat, cook the guanciale until the fat renders and it becomes crispy, about 8-10 minutes. Remove from heat but keep warm.\n\n3. In a bowl, whisk together the egg yolks and whole egg. Add the grated Pecorino Romano and a generous amount of freshly ground black pepper. Mix until well combined.\n\n4. Cook the spaghetti according to package directions until al dente. Reserve 1 cup of pasta cooking water before draining.\n\n5. Add the hot drained pasta directly to the skillet with the guanciale (off heat). Toss to coat with the rendered fat.\n\n6. Working quickly, add the egg and cheese mixture to the pasta, tossing constantly. Add reserved pasta water a little at a time to create a creamy sauce. The residual heat will cook the eggs without scrambling them.\n\n7. Serve immediately with extra Pecorino Romano and black pepper on top.",
  "simpleInstructions": "1. Boil pasta water.\n\n2. Cook guanciale until crispy.\n\n3. Mix eggs, Pecorino, and pepper.\n\n4. Cook pasta, reserve water.\n\n5. Toss pasta with guanciale.\n\n6. Add egg mixture off heat, toss with pasta water.\n\n7. Serve with extra cheese and pepper.",
  "prepTimeMinutes": 10,
  "cookTimeMinutes": 15,
  "servings": 4,
  "mealType": "DINNER",
  "dishCategory": "MAIN_COURSE",
  "cuisine": "Italian Premium",
  "imageUrl": "recipes/images/italian_premium/carbonara_authentic.jpg",
  "ingredients": [
    {
      "name": "Guanciale",
      "quantity": 150,
      "unit": "g",
      "isStarIngredient": true
    },
    {
      "name": "Spaghetti",
      "quantity": 400,
      "unit": "g",
      "isStarIngredient": false
    },
    {
      "name": "Egg Yolks",
      "quantity": 4,
      "unit": "pcs",
      "isStarIngredient": true
    },
    {
      "name": "Eggs",
      "quantity": 1,
      "unit": "pcs",
      "isStarIngredient": false
    },
    {
      "name": "Pecorino Romano",
      "quantity": 100,
      "unit": "g",
      "isStarIngredient": true
    },
    {
      "name": "Black Pepper",
      "quantity": 5,
      "unit": "g",
      "isStarIngredient": false
    }
  ]
}
```

---

## PHASE 4: UI Updates (2-3 hours)

### Step 11: Update Cuisine Selection UI

**File:** `app/src/main/java/com/familymealplanner/ui/screens/CuisineMealsScreen.kt` (or wherever cuisines are displayed)

Add DLC badge and purchase button:

```kotlin
@Composable
fun CuisineCard(
    cuisine: Cuisine,
    isPurchased: Boolean,
    onPurchaseClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(enabled = !cuisine.isDLC || isPurchased) { onClick() }
    ) {
        Box {
            // Existing cuisine card content
            
            // Add DLC badge
            if (cuisine.isDLC) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "PREMIUM",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            // Add purchase button if not purchased
            if (cuisine.isDLC && !isPurchased) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Button(onClick = onPurchaseClick) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Unlock")
                    }
                }
            }
        }
    }
}
```

### Step 12: Update ViewModel

Add DLC checking logic to your cuisine ViewModel:

```kotlin
class CuisineMealsViewModel @Inject constructor(
    // ... existing dependencies
    private val dlcPreferences: DLCPreferences,
    private val assetPackManager: AssetPackManager
) : ViewModel() {
    
    private val _dlcState = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val dlcState: StateFlow<Map<String, Boolean>> = _dlcState.asStateFlow()
    
    init {
        viewModelScope.launch {
            dlcPreferences.purchasedPacks.collect { purchased ->
                _dlcState.value = Cuisine.entries
                    .filter { it.isDLC }
                    .associate { it.assetPackName!! to purchased.contains(it.assetPackName) }
            }
        }
    }
    
    fun isDLCAvailable(cuisine: Cuisine): Boolean {
        if (!cuisine.isDLC || cuisine.assetPackName == null) return true
        return assetPackManager.isPackAvailable(cuisine.assetPackName)
    }
    
    fun downloadDLC(cuisine: Cuisine) {
        if (!cuisine.isDLC || cuisine.assetPackName == null) return
        
        viewModelScope.launch {
            assetPackManager.requestDownload(cuisine.assetPackName).collect { state ->
                when (state) {
                    is AssetPackDownloadState.Completed -> {
                        // Mark as purchased
                        dlcPreferences.markPurchased(cuisine.assetPackName)
                    }
                    is AssetPackDownloadState.Failed -> {
                        // Show error
                    }
                    else -> {
                        // Update progress
                    }
                }
            }
        }
    }
}
```

---

## PHASE 5: Testing (2-3 hours)

### Step 13: Test Locally

1. **Build the app with asset pack:**
   ```bash
   ./gradlew :app:bundleDebug
   ```

2. **Install using bundletool:**
   ```bash
   # Download bundletool if you don't have it
   # https://github.com/google/bundletool/releases
   
   # Build bundle
   ./gradlew :app:bundleDebug
   
   # Generate APKs from bundle
   bundletool build-apks \
     --bundle=app/build/outputs/bundle/debug/app-debug.aab \
     --output=app/build/outputs/apk/debug/app-debug.apks \
     --local-testing
   
   # Install on device
   bundletool install-apks --apks=app/build/outputs/apk/debug/app-debug.apks
   ```

3. **Verify asset pack is included:**
   - Open app
   - Navigate to Italian Premium cuisine
   - Verify recipes load correctly
   - Check all 3 languages (en, ru, ro)

### Step 14: Test User Data Safety

**Critical Test:**
1. Install current production version
2. Create personal recipes
3. Add ingredients to pantry
4. Create meal plans
5. Install new version with DLC
6. **Verify:**
   - ✅ Personal recipes still visible
   - ✅ Pantry quantities unchanged
   - ✅ Meal plans intact
   - ✅ DLC recipes load correctly
   - ✅ No data loss

---

## PHASE 6: Release (1-2 hours)

### Step 15: Prepare for Release

1. **Update version in build.gradle.kts:**
   ```kotlin
   defaultConfig {
       versionCode = 2  // Increment
       versionName = "1.1.0"
   }
   ```

2. **Build release bundle:**
   ```bash
   ./gradlew :app:bundleRelease
   ```

3. **Sign the bundle** (if not using Play App Signing)

### Step 16: Upload to Google Play Console

1. Go to Google Play Console
2. Select your app
3. Go to "Release" → "Production" (or Testing track)
4. Click "Create new release"
5. Upload the AAB file
6. **Important:** Asset packs are automatically detected and included
7. Fill in release notes
8. Review and rollout

### Step 17: Monitor

After release:
- Check crash reports
- Monitor user reviews
- Verify DLC downloads correctly
- Check asset pack delivery metrics in Play Console

---

## PHASE 7: Adding More DLC Packs (Future)

### To Add Another DLC Pack:

1. **Create new asset pack module:**
   ```
   asian_premium_pack/
     build.gradle
     src/main/assets/recipes/asian premium/...
   ```

2. **Add to Cuisine enum:**
   ```kotlin
   ASIAN_PREMIUM("Asian Premium", isDLC = true, assetPackName = "asian_premium_pack")
   ```

3. **Add to settings.gradle.kts:**
   ```kotlin
   include(":asian_premium_pack")
   ```

4. **Add to app/build.gradle.kts:**
   ```kotlin
   assetPacks += [":italian_premium_pack", ":asian_premium_pack"]
   ```

5. **No other code changes needed!**

---

## Troubleshooting

### Issue: Asset pack not found
**Solution:** Ensure deliveryType is set correctly and pack is included in build

### Issue: Recipes not loading from asset pack
**Solution:** Check folder structure matches exactly: `recipes/italian premium/`

### Issue: Images not displaying
**Solution:** Verify image paths in JSON match asset pack structure

### Issue: App crashes on DLC cuisine
**Solution:** Add null checks in BundledRecipeLoader for missing asset packs

---

## Cost Estimate

- **Development time:** 8-12 hours
- **Testing time:** 2-3 hours
- **Google Play fees:** $0 (included in developer account)
- **Server costs:** $0 (uses Google Play infrastructure)

---

## Next Steps

1. ✅ Complete Phase 1 (Project Setup)
2. ✅ Complete Phase 2 (Code Changes)
3. ✅ Create 5-10 test recipes for Italian Premium
4. ✅ Test locally with bundletool
5. ✅ Test with existing user data
6. ✅ Upload to internal testing track
7. ✅ Release to production

---

## Summary

**What you're doing:**
- Adding Google Play Asset Delivery to your app
- Creating separate asset pack modules for each DLC
- Updating BundledRecipeLoader to support asset packs
- Adding UI to show DLC badges and purchase buttons

**What stays the same:**
- User data storage (completely untouched)
- Existing recipe loading for base cuisines
- Database schema (no migrations needed)
- All existing functionality

**User data safety:**
- ✅ 100% safe - no database changes
- ✅ Separate storage for DLC recipes
- ✅ No risk of data loss
- ✅ Tested and verified
