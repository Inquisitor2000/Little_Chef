# DLC Debug Testing Setup

## Current Status

The Italian Premium DLC recipe pack is now visible in debug builds for testing purposes.

## How It Works

### Asset Pack System
- **Production**: DLC recipes are delivered via Google Play Asset Delivery from the `italian_premium_pack` module
- **Debug/Testing**: Asset packs are NOT available when running from Android Studio, so we use a fallback mechanism

### Fallback Mechanism
The `BundledRecipeLoader` now has a two-tier loading strategy:

1. **First**: Try to load from asset pack (for production builds distributed via Google Play)
2. **Fallback**: If asset pack is not available, load from main app assets (for debug builds)

This allows you to:
- Test the UI and recipe display in debug mode
- See the Italian Premium recipes without building an App Bundle
- Develop and test the purchase flow

### Recipe Files Location

**Asset Pack (Production)**:
```
italian_premium_pack/src/main/assets/recipes/italian premium/
├── carbonara_authentic_italian_premium.json
├── carbonara_authentic_italian_premium_ru.json
└── carbonara_authentic_italian_premium_ro.json
```

**Main App Assets (Debug Fallback)**:
```
app/src/main/assets/recipes/italian premium/
├── carbonara_authentic_italian_premium.json
├── carbonara_authentic_italian_premium_ru.json
└── carbonara_authentic_italian_premium_ro.json
```

## Testing the App

### Debug Mode (Android Studio)
1. Run the app from Android Studio
2. Navigate to "Chef's Choice" section
3. Click on "Italian Premium" card
4. You should see the Authentic Roman Carbonara recipe
5. The recipe loads from main app assets (fallback)

### Production Testing (App Bundle)
To test the actual asset pack delivery:

1. Build an App Bundle:
   ```bash
   ./gradlew bundleRelease
   ```

2. Test with bundletool:
   ```bash
   # Download bundletool if you don't have it
   # https://github.com/google/bundletool/releases
   
   # Generate APKs from bundle
   bundletool build-apks --bundle=app/build/outputs/bundle/release/app-release.aab \
     --output=app.apks \
     --mode=universal
   
   # Install on device
   bundletool install-apks --apks=app.apks
   ```

3. In production mode, recipes load from the asset pack (not from main assets)

## Next Steps

### 1. Add Purchase UI
- Find the recipe detail screen
- Add DLC detection logic
- Show "Unlock" button for unpurchased DLC recipes
- Implement Google Play Billing integration

### 2. DLC Purchase Flow
- When user clicks "Unlock", trigger Google Play Billing
- Purchase unlocks the entire Italian Premium pack
- Store purchase state in `DLCPreferences`
- Trigger asset pack download via `AssetPackManager`

### 3. Production Cleanup
Before releasing to production:
- **REMOVE** the recipe files from `app/src/main/assets/recipes/italian premium/`
- Keep only the asset pack versions in `italian_premium_pack/`
- The fallback mechanism will remain for safety, but won't be used in production

## Important Notes

- The fallback mechanism is intentional and safe for production
- In production, asset packs are always available after download
- The fallback only activates if asset pack loading fails
- This provides a safety net without compromising the DLC model
