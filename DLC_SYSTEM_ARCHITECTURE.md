# DLC System Architecture

## 📐 System Overview

The DLC (Downloadable Content) system consists of three main components:
1. **UI Layer** - Premium preview drawer and lock indicators
2. **Billing Layer** - Google Play in-app purchases
3. **Content Layer** - On-demand asset pack downloads

---

## 🔄 Complete Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER JOURNEY                             │
└─────────────────────────────────────────────────────────────────┘

1. App Launch
   │
   ├─→ MealsScreen loads
   │   │
   │   ├─→ MealsViewModel.init()
   │   │   │
   │   │   ├─→ BillingManager.init()
   │   │   │   └─→ Connect to Google Play Billing
   │   │   │
   │   │   └─→ BillingManager.queryPurchases()
   │   │       └─→ Restore previous purchases
   │   │           └─→ DLCPreferences.markPurchased()
   │   │
   │   └─→ Display cuisine cards
   │       │
   │       ├─→ Regular cuisines: No lock icon
   │       └─→ DLC cuisines: 🔒 or 🔓 based on purchase status
   │
   │
2. User taps "Italian Premium" (unpurchased)
   │
   ├─→ MealsScreen.handleCuisineClick()
   │   │
   │   ├─→ Check: cuisine.isDLC? → YES
   │   │
   │   └─→ showPremiumPreview = getPremiumPackPreview(cuisine)
   │       │
   │       └─→ PremiumPreviewDrawer appears
   │           │
   │           ├─→ Show cuisine icon (Italian flag)
   │           ├─→ Show "Italian Premium" title
   │           ├─→ Show "12 recipes" count
   │           ├─→ Show 12 scrollable recipe preview cards
   │           └─→ Show unlock button: 🔓 "$1.99"
   │
   │
3. User taps "$1.99" button
   │
   ├─→ PremiumPreviewDrawer.onPurchase()
   │   │
   │   └─→ MealsViewModel.purchaseDLC(activity, "italian_premium_pack")
   │       │
   │       └─→ BillingManager.launchPurchaseFlow()
   │           │
   │           ├─→ Set state: PurchaseState.Loading
   │           │   └─→ Toast: "Opening Google Play..."
   │           │
   │           ├─→ Query product details from Google Play
   │           │   └─→ Product: italian_premium_pack ($1.99)
   │           │
   │           └─→ Launch Google Play billing dialog
   │               │
   │               ├─→ User completes purchase
   │               │   │
   │               │   └─→ BillingManager.onPurchasesUpdated()
   │               │       │
   │               │       └─→ BillingManager.handlePurchase()
   │               │           │
   │               │           └─→ BillingManager.acknowledgePurchase()
   │               │               │
   │               │               ├─→ DLCPreferences.markPurchased()
   │               │               │   └─→ Save to DataStore
   │               │               │
   │               │               └─→ AssetPackManager.requestDownload()
   │               │                   │
   │               │                   ├─→ State: Pending
   │               │                   │   └─→ Toast: "Downloading... 0%"
   │               │                   │
   │               │                   ├─→ State: Downloading(50)
   │               │                   │   └─→ Toast: "Downloading... 50%"
   │               │                   │
   │               │                   ├─→ State: Downloading(100)
   │               │                   │   └─→ Toast: "Downloading... 100%"
   │               │                   │
   │               │                   └─→ State: Completed
   │               │                       │
   │               │                       └─→ PurchaseState.Success
   │               │                           │
   │               │                           └─→ Toast: "Purchase successful!"
   │               │
   │               └─→ User cancels
   │                   └─→ PurchaseState.Cancelled
   │                       └─→ Toast: "Purchase cancelled"
   │
   │
4. After successful purchase
   │
   ├─→ MealsScreen observes PurchaseState.Success
   │   │
   │   ├─→ Close drawer: showPremiumPreview = null
   │   │
   │   ├─→ Navigate to cuisine: onNavigateToCuisine(cuisine)
   │   │
   │   └─→ Lock icon updates: 🔒 → 🔓
   │
   │
5. User taps "Italian Premium" (purchased)
   │
   └─→ MealsScreen.handleCuisineClick()
       │
       ├─→ Check: cuisine.isDLC? → YES
       │
       ├─→ Check: isPurchased? → YES
       │
       └─→ Navigate directly: onNavigateToCuisine(cuisine)
           │
           └─→ Load recipes from asset pack
               └─→ Display Italian Premium recipes
```

---

## 🏗️ Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          UI LAYER                                │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              MealsScreen.kt                              │   │
│  │  - Display cuisine cards with lock indicators           │   │
│  │  - Handle cuisine clicks (DLC vs regular)               │   │
│  │  - Show premium preview drawer                          │   │
│  │  - Observe purchase state                               │   │
│  │  - Display toast messages                               │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│                            │ uses                                │
│                            ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │         PremiumPreviewDrawer.kt                          │   │
│  │  - Modal bottom sheet                                    │   │
│  │  - Cuisine icon and name                                │   │
│  │  - Recipe count                                          │   │
│  │  - Scrollable recipe preview cards                      │   │
│  │  - Unlock button with price                             │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────────┬───────────────────────────────────┘
                                │
                                │ communicates via
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      VIEWMODEL LAYER                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            MealsViewModel.kt                             │   │
│  │  - Manage UI state                                       │   │
│  │  - Expose purchase state flow                           │   │
│  │  - Trigger purchase flow                                │   │
│  │  - Check DLC purchase status                            │   │
│  │  - Reset purchase state                                 │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│                            │ delegates to                        │
│                            ▼                                     │
└───────────────────────────────────────────────────────────────────┘
                                │
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      BILLING LAYER                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            BillingManager.kt                             │   │
│  │  - Initialize BillingClient                             │   │
│  │  - Query product details                                │   │
│  │  - Launch purchase flow                                 │   │
│  │  - Handle purchase callbacks                            │   │
│  │  - Acknowledge purchases                                │   │
│  │  - Restore purchases                                    │   │
│  │  - Manage purchase state                                │   │
│  │  - Trigger asset pack download                          │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│                            │ uses                                │
│                            ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │         Google Play BillingClient                        │   │
│  │  - Connect to Google Play services                      │   │
│  │  - Query in-app products                                │   │
│  │  - Process payments                                     │   │
│  │  - Verify purchases                                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────────┬───────────────────────────────────┘
                                │
                                │ coordinates with
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      CONTENT LAYER                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │          AssetPackManager.kt                             │   │
│  │  - Check pack availability                              │   │
│  │  - Request pack download                                │   │
│  │  - Monitor download progress                            │   │
│  │  - Get pack location                                    │   │
│  │  - Cancel downloads                                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                            │                                     │
│                            │ uses                                │
│                            ▼                                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │      Google Play AssetPackManager                        │   │
│  │  - Download asset packs from Play Store                 │   │
│  │  - Track download state                                 │   │
│  │  - Provide pack location                                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────────┬───────────────────────────────────┘
                                │
                                │ stores to
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                      STORAGE LAYER                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │           DLCPreferences.kt                              │   │
│  │  - Store purchased pack IDs                             │   │
│  │  - Check purchase status                                │   │
│  │  - Persist across app restarts                          │   │
│  │  - Uses DataStore                                       │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │         Asset Pack Storage                               │   │
│  │  /data/app/.../italian_premium_pack/                    │   │
│  │    └─ assets/recipes/italian premium/                   │   │
│  │         ├─ carbonara_romana_italian_premium.json        │   │
│  │         ├─ osso_buco_italian_premium.json               │   │
│  │         └─ ... (10 more recipes)                        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔐 Purchase State Machine

```
┌─────────┐
│  Idle   │ ◄─────────────────────────────────┐
└────┬────┘                                    │
     │                                         │
     │ User taps "$1.99"                      │
     ▼                                         │
┌─────────┐                                    │
│ Loading │ ──── Toast: "Opening Google Play..." │
└────┬────┘                                    │
     │                                         │
     │ Google Play dialog appears              │
     │                                         │
     ├─→ User completes ──┐                   │
     │                    │                   │
     │                    ▼                   │
     │              ┌────────────┐            │
     │              │ Downloading│            │
     │              │  (0-100%)  │            │
     │              └─────┬──────┘            │
     │                    │                   │
     │                    │ Download complete │
     │                    ▼                   │
     │              ┌─────────┐               │
     │              │ Success │ ──────────────┘
     │              └─────────┘
     │                    │
     │                    └─→ Navigate to recipes
     │
     ├─→ User cancels ──┐
     │                  │
     │                  ▼
     │            ┌───────────┐
     │            │ Cancelled │ ──────────────┘
     │            └───────────┘
     │
     └─→ Error occurs ──┐
                        │
                        ▼
                  ┌─────────┐
                  │  Error  │ ──────────────┘
                  └─────────┘
```

---

## 📦 Data Flow

### Purchase Data Flow:
```
User Action
    │
    ▼
MealsScreen
    │
    ▼
MealsViewModel.purchaseDLC()
    │
    ▼
BillingManager.launchPurchaseFlow()
    │
    ▼
Google Play BillingClient
    │
    ▼
BillingManager.onPurchasesUpdated()
    │
    ▼
BillingManager.acknowledgePurchase()
    │
    ├─→ DLCPreferences.markPurchased()
    │       │
    │       └─→ DataStore (persistent)
    │
    └─→ AssetPackManager.requestDownload()
            │
            └─→ Google Play AssetPackManager
                    │
                    └─→ Download to device storage
```

### Purchase Status Check Flow:
```
MealsScreen loads
    │
    ▼
CuisineCard renders
    │
    ▼
MealsViewModel.isDLCPurchased("italian_premium_pack")
    │
    ▼
BillingManager.isPurchased()
    │
    ▼
DLCPreferences.isPurchased()
    │
    ▼
DataStore query
    │
    ├─→ true  → Show 🔓 icon
    └─→ false → Show 🔒 icon
```

---

## 🗂️ File Structure

```
app/
├── src/main/java/com/familymealplanner/
│   ├── billing/
│   │   └── BillingManager.kt          # Handles Google Play billing
│   │
│   ├── data/
│   │   ├── local/
│   │   │   └── AssetPackManager.kt    # Handles asset pack downloads
│   │   │
│   │   └── preferences/
│   │       └── DLCPreferences.kt      # Stores purchase status
│   │
│   ├── ui/
│   │   ├── components/
│   │   │   └── PremiumPreviewDrawer.kt # Premium preview UI
│   │   │
│   │   └── screens/
│   │       ├── MealsScreen.kt         # Main screen with DLC handling
│   │       └── MealsViewModel.kt      # ViewModel with billing integration
│   │
│   └── domain/model/
│       └── Cuisine.kt                 # Cuisine enum with DLC flag
│
└── build.gradle.kts                   # Dependencies and asset pack config

italian_premium_pack/
├── build.gradle                       # Asset pack configuration
└── src/main/assets/recipes/italian premium/
    ├── carbonara_romana_italian_premium.json
    ├── carbonara_romana_italian_premium_ro.json
    ├── carbonara_romana_italian_premium_ru.json
    └── ... (33 more files for 11 recipes)
```

---

## 🔄 State Synchronization

### Purchase State Synchronization:
```
┌──────────────────┐
│  BillingManager  │
│  purchaseState   │
└────────┬─────────┘
         │
         │ StateFlow
         ▼
┌──────────────────┐
│  MealsViewModel  │
│  purchaseState   │
└────────┬─────────┘
         │
         │ collectAsState()
         ▼
┌──────────────────┐
│   MealsScreen    │
│  UI updates      │
└──────────────────┘
```

### DLC Status Synchronization:
```
┌──────────────────┐
│ DLCPreferences   │
│ purchasedPacks   │
└────────┬─────────┘
         │
         │ Flow
         ▼
┌──────────────────┐
│  BillingManager  │
│  isPurchased()   │
└────────┬─────────┘
         │
         │ suspend fun
         ▼
┌──────────────────┐
│  MealsViewModel  │
│ isDLCPurchased() │
└────────┬─────────┘
         │
         │ collectAsState()
         ▼
┌──────────────────┐
│   CuisineCard    │
│  Lock icon       │
└──────────────────┘
```

---

## 🎯 Key Design Decisions

### 1. On-Demand Asset Packs
**Why**: Reduces initial app size, downloads only after purchase
**How**: `deliveryType = "on-demand"` in asset pack config
**Benefit**: Users don't download content they haven't purchased

### 2. Purchase State Management
**Why**: Provide real-time feedback to user
**How**: StateFlow from BillingManager to UI
**Benefit**: Smooth UI updates, progress tracking

### 3. DataStore for Persistence
**Why**: Fast, type-safe, coroutine-based storage
**How**: DLCPreferences wraps DataStore
**Benefit**: Purchase status persists across app restarts

### 4. Separate Preview from Content
**Why**: Show what's available before purchase
**How**: Hardcoded preview data in MealsScreen
**Benefit**: Users see value before buying

### 5. Automatic Purchase Restoration
**Why**: Users shouldn't lose purchases
**How**: BillingManager.queryPurchases() on init
**Benefit**: Seamless experience across devices

---

## 🔒 Security Considerations

### 1. Server-Side Verification (Future Enhancement)
Currently: Client-side purchase verification only
Recommended: Add backend server to verify purchases with Google Play

### 2. Purchase Token Security
- Never log purchase tokens
- Store securely in DataStore
- Validate on each app start

### 3. Asset Pack Security
- Asset packs signed by Google Play
- Cannot be tampered with
- Verified during download

### 4. Product ID Validation
- Product IDs hardcoded in Cuisine enum
- Validated against Google Play Console
- No user input for product IDs

---

## 📊 Performance Considerations

### 1. Lazy Loading
- Asset packs downloaded only when needed
- Recipes loaded only when cuisine is opened
- Preview cards use placeholders (no real images)

### 2. State Management
- StateFlow for reactive updates
- Minimal recomposition
- Efficient state observation

### 3. Network Efficiency
- Single billing connection per app session
- Cached product details
- Batch purchase queries

### 4. Storage Efficiency
- Asset packs stored in app-specific directory
- Automatic cleanup on uninstall
- Minimal DataStore usage

---

## 🧪 Testing Strategy

### Unit Tests:
- DLCPreferences purchase status logic
- BillingManager state transitions
- AssetPackManager download state handling

### Integration Tests:
- Purchase flow end-to-end
- Asset pack download after purchase
- Purchase restoration on app restart

### UI Tests:
- Drawer opens on DLC tap
- Lock icon updates after purchase
- Toast messages appear correctly

### Manual Tests:
- Real billing on test account
- Multiple devices
- Network error scenarios
- Download interruption

---

## 🚀 Future Enhancements

### 1. Multiple DLC Packs
- French Premium
- Asian Premium
- Mediterranean Premium
- Bundle discounts

### 2. Subscription Model
- Monthly access to all DLC
- Auto-renewal
- Free trial period

### 3. Promotional Pricing
- Limited-time discounts
- Seasonal sales
- First-time buyer discount

### 4. Offline Support
- Queue downloads for later
- Retry failed downloads
- Download over WiFi only option

### 5. Analytics
- Track purchase conversion rate
- Monitor download success rate
- A/B test pricing

---

## 📝 Summary

The DLC system is a complete solution for:
- ✅ Displaying premium content previews
- ✅ Processing in-app purchases via Google Play
- ✅ Downloading content on-demand
- ✅ Persisting purchase status
- ✅ Providing user feedback
- ✅ Handling errors gracefully

All code is in place and ready for testing once the product is configured in Google Play Console.
