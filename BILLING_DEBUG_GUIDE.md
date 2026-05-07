# Google Play Billing Debug Guide

## Issue: Clicking $1.99 button doesn't trigger billing

## Debug Steps

### 1. Check Logcat Output
After clicking the $1.99 button, check Logcat for these messages:

```
MealsScreen: Purchase button clicked
MealsScreen: Activity: <activity instance>
MealsScreen: Product ID: italian_premium_pack
MealsScreen: Launching billing flow for: italian_premium_pack
BillingManager: launchPurchaseFlow called with productId: italian_premium_pack
BillingManager: Billing client is ready, setting state to Loading
BillingManager: Querying product details for: italian_premium_pack
```

### 2. Common Issues & Solutions

#### Issue: "Billing not ready"
**Cause**: BillingClient not initialized or disconnected
**Solution**: 
- Check if Google Play Services is installed
- Restart the app
- Check internet connection

#### Issue: "Product not found"
**Cause**: Product not configured in Google Play Console
**Solution**:
1. Go to Google Play Console
2. Navigate to: Monetize → Products → In-app products
3. Create product with ID: `italian_premium_pack`
4. Set price to $1.99
5. Activate the product

#### Issue: "Activity is null"
**Cause**: Context is not an Activity
**Solution**: Already handled in code with null check

#### Issue: No logs appear
**Cause**: Button click not registered
**Solution**: Check if button is clickable and not blocked by other UI

### 3. Test Purchase Flow

#### For Testing (Without Real Payment):
1. Add test account in Google Play Console:
   - Go to Setup → License testing
   - Add your Google account email
   - Test purchases are free for test accounts

2. Build and install signed APK:
   ```bash
   ./gradlew assembleRelease
   ```

3. Install on device:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

#### For Development Testing:
If you don't have the product set up yet, you can temporarily test the UI flow:

1. **Mock Purchase** (temporary):
   In `BillingManager.kt`, add this at the start of `launchPurchaseFlow`:
   ```kotlin
   // TEMPORARY: Mock purchase for testing
   if (BuildConfig.DEBUG) {
       scope.launch {
           delay(1000)
           dlcPreferences.markPurchased(productId)
           _purchaseState.value = PurchaseState.Success(productId)
       }
       return
   }
   ```

2. This will simulate a successful purchase without Google Play
3. **Remove this code before production!**

### 4. Verify Product Configuration

The product ID must match exactly:
- **Code**: `italian_premium_pack` (in Cuisine.kt)
- **Google Play Console**: `italian_premium_pack`

Check in `Cuisine.kt`:
```kotlin
ITALIAN_PREMIUM(
    displayName = "Italian Premium",
    iconRes = R.drawable.ic_sub_pasta,
    description = "Authentic Italian recipes",
    isDLC = true,
    assetPackName = "italian_premium_pack" // Must match Play Console
)
```

### 5. Check Toast Messages

When you click the button, you should see Toast messages:
- "Opening Google Play..." (immediately)
- Then either:
  - "Purchase successful!" (if completed)
  - "Purchase cancelled" (if cancelled)
  - "Error: <message>" (if failed)

### 6. Verify Billing Library

Check `app/build.gradle.kts` has:
```kotlin
implementation("com.android.billingclient:billing:6.1.0")
implementation("com.android.billingclient:billing-ktx:6.1.0")
```

### 7. Check Permissions

In `AndroidManifest.xml`, ensure:
```xml
<uses-permission android:name="com.android.vending.BILLING" />
```

### 8. Test on Real Device

Google Play Billing **does not work** on:
- Emulators without Google Play
- Devices without Google Play Services
- Debug builds (sometimes)

**Always test on**:
- Real device with Google Play
- Signed release build
- Device with test account signed in

### 9. Expected Flow

1. User taps $1.99 button
2. Toast: "Opening Google Play..."
3. Google Play dialog appears
4. User completes/cancels purchase
5. Toast shows result
6. If successful: drawer closes, navigates to recipes
7. Lock icon changes from 🔒 to 🔓

### 10. Troubleshooting Checklist

- [ ] Google Play Services installed and updated
- [ ] Internet connection active
- [ ] Product created in Play Console
- [ ] Product ID matches exactly
- [ ] Product is active (not draft)
- [ ] App is signed (for release testing)
- [ ] Test account added (for testing)
- [ ] Device signed in with test account
- [ ] Billing permission in manifest
- [ ] Billing library dependencies added
- [ ] BillingClient initialized successfully

## Quick Test Commands

### View Logcat:
```bash
adb logcat | grep -E "MealsScreen|BillingManager"
```

### Clear App Data (reset purchases):
```bash
adb shell pm clear com.familymealplanner
```

### Check Google Play Services:
```bash
adb shell dumpsys package com.android.vending | grep version
```

## Next Steps

1. Run the app
2. Click on "Italian Premium"
3. Click the $1.99 button
4. Check Logcat for the debug messages
5. Share the logs to identify the issue

## Common Error Messages

### "Product not found"
→ Create product in Google Play Console

### "Billing not ready"
→ Check Google Play Services, restart app

### "Item unavailable"
→ Product not published or not available in your region

### "Developer error"
→ Product ID mismatch or app not properly configured

### "User cancelled"
→ Normal - user closed the dialog

## Production Checklist

Before releasing:
- [ ] Remove all debug logs
- [ ] Remove mock purchase code
- [ ] Test with real payment (small amount)
- [ ] Verify purchase restoration works
- [ ] Test on multiple devices
- [ ] Test with different Google accounts
- [ ] Verify refund handling
- [ ] Test offline behavior
