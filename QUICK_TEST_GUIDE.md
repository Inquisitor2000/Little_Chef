# Quick Test Guide - DLC Billing

## 🚀 Quick Start Testing

### Option 1: Test UI Flow Only (No Billing Setup Required)

Add this temporary code to test the UI without Google Play:

**File**: `/app/src/main/java/com/familymealplanner/billing/BillingManager.kt`

**Add at the start of `launchPurchaseFlow()` function (line ~60):**

```kotlin
fun launchPurchaseFlow(activity: Activity, productId: String) {
    Log.d(TAG, "launchPurchaseFlow called with productId: $productId")
    
    // ⚠️ TEMPORARY: Mock purchase for UI testing only
    if (BuildConfig.DEBUG) {
        scope.launch {
            _purchaseState.value = PurchaseState.Loading
            delay(1000)
            _purchaseState.value = PurchaseState.Downloading(productId, 0)
            delay(500)
            _purchaseState.value = PurchaseState.Downloading(productId, 50)
            delay(500)
            _purchaseState.value = PurchaseState.Downloading(productId, 100)
            delay(500)
            dlcPreferences.markPurchased(productId)
            _purchaseState.value = PurchaseState.Success(productId)
        }
        return
    }
    // END TEMPORARY CODE
    
    val client = billingClient
    // ... rest of the function
```

**Test Steps:**
1. Build and run the app
2. Tap "Italian Premium" → Drawer opens
3. Tap "$1.99" button
4. Watch the UI flow:
   - Toast: "Opening Google Play..."
   - Toast: "Downloading recipes... 0%"
   - Toast: "Downloading recipes... 50%"
   - Toast: "Downloading recipes... 100%"
   - Toast: "Purchase successful! Recipes unlocked."
   - Drawer closes
   - Lock icon changes to unlocked 🔓

**⚠️ IMPORTANT**: Remove this code before testing real billing!

---

### Option 2: Test Real Google Play Billing

#### Prerequisites:
1. **Create Product in Google Play Console**
   - Go to: https://play.google.com/console
   - Select your app
   - Navigate to: **Monetize → Products → In-app products**
   - Click **Create product**
   - Product ID: `italian_premium_pack`
   - Name: Italian Premium Recipe Pack
   - Price: $1.99
   - Click **Save** → **Activate**

2. **Upload App to Play Console**
   ```bash
   # Build release bundle
   ./gradlew bundleRelease
   
   # Upload to Play Console → Internal testing track
   ```

3. **Add Test Account**
   - Play Console → **Setup → License testing**
   - Add your Google account email
   - Purchases will be free for this account

4. **Install on Real Device**
   ```bash
   # Build signed APK
   ./gradlew assembleRelease
   
   # Install on device
   adb install app/build/outputs/apk/release/app-release.apk
   ```

#### Test Steps:
1. Sign in to device with test account
2. Open the app
3. Tap "Italian Premium" → Drawer opens
4. Tap "$1.99" button → Google Play dialog appears
5. Complete purchase (free for test account)
6. Watch download progress
7. Verify recipes unlock

#### Check Logs:
```bash
adb logcat | grep -E "MealsScreen|BillingManager"
```

**Expected logs:**
```
MealsScreen: Purchase button clicked
MealsScreen: Launching billing flow for: italian_premium_pack
BillingManager: launchPurchaseFlow called with productId: italian_premium_pack
BillingManager: Billing client is ready, setting state to Loading
BillingManager: Querying product details for: italian_premium_pack
BillingManager: Product details: <product info>
BillingManager: Launching billing flow...
```

---

## 🧪 Test Scenarios

### Scenario 1: First Time Purchase
1. Fresh install (or clear app data)
2. Open app → Italian Premium shows 🔒
3. Tap Italian Premium → Drawer opens
4. Scroll through 12 recipe preview cards
5. Tap "$1.99" → Purchase flow starts
6. Complete purchase → Download starts
7. Download completes → Lock changes to 🔓
8. Tap Italian Premium → Recipes load

### Scenario 2: Purchase Restoration
1. Complete purchase (Scenario 1)
2. Close app
3. Clear app data: `adb shell pm clear com.familymealplanner`
4. Open app → Italian Premium shows 🔒 initially
5. Wait 2-3 seconds → Lock changes to 🔓 (purchase restored)
6. Tap Italian Premium → Recipes available

### Scenario 3: User Cancels Purchase
1. Tap Italian Premium → Drawer opens
2. Tap "$1.99" → Google Play dialog appears
3. Press back or cancel
4. Toast: "Purchase cancelled"
5. Drawer stays open
6. Lock still shows 🔒

### Scenario 4: Network Error
1. Turn off WiFi and mobile data
2. Tap Italian Premium → Drawer opens
3. Tap "$1.99"
4. Error toast appears
5. Drawer stays open

### Scenario 5: Download Failure
1. Complete purchase successfully
2. If download fails:
   - Error toast appears
   - Purchase is saved (won't charge again)
   - User can retry by reopening app

---

## 🐛 Troubleshooting

### Issue: Button doesn't respond
**Check:**
- Is the button visible and not covered?
- Check Logcat for "Purchase button clicked"
- Try tapping multiple times

**Fix:**
- Restart app
- Clear app data
- Reinstall app

### Issue: "Billing not ready"
**Check:**
- Google Play Services installed?
- Internet connection active?
- Device signed in to Google account?

**Fix:**
```bash
# Check Google Play Services version
adb shell dumpsys package com.android.vending | grep version

# Update Google Play Services from Play Store
```

### Issue: "Product not found"
**Check:**
- Product created in Play Console?
- Product ID is exactly: `italian_premium_pack`
- Product is Active (not Draft)?

**Fix:**
- Create product in Play Console
- Wait 2-3 hours for propagation
- Try again

### Issue: "Item unavailable"
**Check:**
- App uploaded to Play Console?
- Testing on internal testing track?
- Test account added to license testing?

**Fix:**
- Upload app to Play Console
- Add test account
- Install from Play Console or use signed APK

### Issue: Google Play dialog doesn't appear
**Check:**
- Testing on real device (not emulator)?
- Google Play Services installed?
- Using signed APK (not debug)?

**Fix:**
- Test on real device
- Build release APK
- Sign APK with release keystore

---

## 📊 Verification Checklist

### UI Verification:
- [ ] Italian Premium card shows 🔒 icon (unpurchased)
- [ ] Tapping card opens drawer
- [ ] Drawer shows Italian flag icon
- [ ] Drawer shows "Italian Premium" title
- [ ] Drawer shows "12 recipes" count
- [ ] Drawer shows 12 scrollable recipe cards
- [ ] Unlock button shows 🔓 icon and "$1.99"
- [ ] Can scroll recipe cards manually
- [ ] Drawer has bottom padding (not cut off)

### Purchase Flow Verification:
- [ ] Tapping "$1.99" shows "Opening Google Play..." toast
- [ ] Google Play dialog appears (real billing only)
- [ ] Can complete purchase
- [ ] Shows "Downloading recipes..." toast with progress
- [ ] Shows "Purchase successful!" toast
- [ ] Drawer closes automatically
- [ ] Lock icon changes to 🔓
- [ ] Can access Italian Premium recipes

### Persistence Verification:
- [ ] Close and reopen app → Lock still shows 🔓
- [ ] Clear app data → Purchase restores after 2-3 seconds
- [ ] Uninstall and reinstall → Purchase restores

### Error Handling Verification:
- [ ] Cancel purchase → Shows "Purchase cancelled" toast
- [ ] Network error → Shows error toast
- [ ] Download failure → Shows error toast
- [ ] Can retry after error

---

## 🔄 Reset Testing State

### Clear Purchase Data (for retesting):
```bash
# Clear app data (removes local purchase record)
adb shell pm clear com.familymealplanner

# Note: This won't refund real purchases!
# For test accounts, purchases are free and can be repeated
```

### Clear Google Play Cache:
```bash
# Clear Play Store data
adb shell pm clear com.android.vending

# Restart device
adb reboot
```

---

## 📱 Device Requirements

### Minimum Requirements:
- Android 8.0 (API 26) or higher
- Google Play Services installed
- Google account signed in
- Internet connection

### Recommended for Testing:
- Real Android device (not emulator)
- Android 10+ for best compatibility
- Stable internet connection
- Test Google account

### Not Supported:
- ❌ Emulators without Google Play
- ❌ Devices without Google Play Services
- ❌ Offline testing (billing requires internet)
- ❌ Rooted devices (may have issues)

---

## 📞 Getting Help

### If billing still doesn't work:

1. **Collect Logs:**
   ```bash
   adb logcat -d > logcat.txt
   ```

2. **Check Product Status:**
   - Go to Play Console
   - Verify product is Active
   - Check product ID matches exactly

3. **Verify App Configuration:**
   - App uploaded to Play Console?
   - Internal testing track configured?
   - Test account added?

4. **Test Account:**
   - Signed in on device?
   - Added to license testing?
   - Not using production account?

5. **Share Information:**
   - Logcat output
   - Product status screenshot
   - Device model and Android version
   - Testing method (mock vs real billing)

---

## ✅ Success Criteria

You'll know it's working when:
1. ✅ Drawer opens smoothly
2. ✅ All 12 recipe cards visible
3. ✅ Button triggers billing (or mock flow)
4. ✅ Progress toasts appear
5. ✅ Lock icon changes
6. ✅ Recipes become accessible
7. ✅ Purchase persists across app restarts

---

## 🎯 Next Steps After Testing

Once billing works:
1. Remove mock purchase code (if added)
2. Add remaining 11 Italian Premium recipes
3. Test on multiple devices
4. Test with different Google accounts
5. Test error scenarios
6. Prepare for production release

---

## 📝 Notes

- Test purchases are **free** for test accounts
- Real purchases can be **refunded** within 48 hours
- Product setup can take **2-3 hours** to propagate
- Always test on **real devices** for billing
- Keep **debug logs** enabled during testing
- Remove **mock code** before production

---

## 🚨 Common Mistakes

1. ❌ Testing on emulator without Google Play
2. ❌ Using debug build for real billing
3. ❌ Product ID mismatch (typo)
4. ❌ Product not activated in Play Console
5. ❌ App not uploaded to Play Console
6. ❌ Test account not added
7. ❌ Forgetting to remove mock code
8. ❌ Not waiting for product propagation

---

## ✨ Pro Tips

- Use **mock code** for rapid UI testing
- Use **test accounts** for free purchases
- Check **Logcat** for detailed debugging
- Wait **2-3 hours** after creating product
- Test on **multiple devices** if possible
- Keep **screenshots** of successful tests
- Document **any issues** encountered
