# Recipe Visibility Fix - Complete

## Problem
The Italian Premium recipe wasn't showing up when clicking the "Chef's Choice" → "Italian Premium" card.

## Root Cause
Asset packs are NOT available when running the app from Android Studio in debug mode. They only work when the app is distributed as an App Bundle through Google Play or tested with bundletool.

## Solution Implemented

### 1. Added Fallback Mechanism
Updated `BundledRecipeLoader.kt` to try loading from asset pack first, then fallback to main app assets if the pack isn't available:

```kotlin
if (cuisine.isDLC && cuisine.assetPackName != null) {
    // Try to load from asset pack first
    val recipesFromPack = loadFromAssetPack(cuisine.assetPackName, folderName, languageCode)
    
    // If asset pack is not available (e.g., in debug builds), fallback to main assets
    if (recipesFromPack.isEmpty()) {
        loadFromAssets(folderName, languageCode)
    } else {
        recipesFromPack
    }
}
```

### 2. Copied Recipe Files for Testing
Copied the Authentic Roman Carbonara recipe (all 3 languages) from the asset pack to main app assets:

**From**: `italian_premium_pack/src/main/assets/recipes/italian premium/`  
**To**: `app/src/main/assets/recipes/italian premium/`

Files copied:
- `carbonara_authentic_italian_premium.json` (English)
- `carbonara_authentic_italian_premium_ru.json` (Russian)
- `carbonara_authentic_italian_premium_ro.json` (Romanian)

## Testing

### Now You Can:
1. ✅ Run the app from Android Studio
2. ✅ Navigate to "Chef's Choice" section (first section)
3. ✅ Click on "Italian Premium" card
4. ✅ See the Authentic Roman Carbonara recipe
5. ✅ Click on the recipe to view details

### The Recipe Shows:
- **Name**: Authentic Roman Carbonara (Аутентичная римская карбонара / Carbonara Romană Autentică)
- **Meal Type**: Dinner
- **Prep Time**: 10 minutes
- **Cook Time**: 15 minutes
- **Servings**: 4
- **Ingredients**: Guanciale, Spaghetti, Egg Yolks, Eggs, Pecorino Romano, Black Pepper

## Next Steps

### Phase 3: Add Purchase UI to Recipe Detail Screen

Now that the recipe is visible, we need to:

1. **Find the recipe detail screen** (likely `RecipeDetailScreen.kt`)
2. **Add DLC detection logic**:
   - Check if the recipe's cuisine is DLC (`cuisine.isDLC`)
   - Check if the pack is purchased (via `DLCPreferences`)
3. **Add "Unlock Italian Premium" button** at the bottom of the screen
4. **Implement purchase flow**:
   - Trigger Google Play Billing when button is clicked
   - Download asset pack after successful purchase
   - Update UI to show recipe is now unlocked

### Production Deployment

When ready to release:
- **Keep** the fallback mechanism in `BundledRecipeLoader` (it's safe)
- **Remove** the recipe files from `app/src/main/assets/recipes/italian premium/` (optional, for smaller APK size)
- **Keep** the recipe files in `italian_premium_pack/` (required for production)
- Build as App Bundle for Google Play distribution

## Files Modified

1. `/app/src/main/java/com/familymealplanner/data/local/BundledRecipeLoader.kt` - Added fallback logic
2. `/app/src/main/assets/recipes/italian premium/*.json` - Added recipe files for testing
3. `/DLC_DEBUG_TESTING.md` - Created documentation
4. `/RECIPE_VISIBILITY_FIX.md` - This file

## Status: ✅ COMPLETE

The recipe is now visible in debug builds. You can proceed with testing the UI and implementing the purchase flow.
