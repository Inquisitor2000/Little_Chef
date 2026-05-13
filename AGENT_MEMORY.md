# Agent Memory

## WebP Image Conversion (May 2026)

Converted all image assets to WebP format for ~17.5MB APK size savings (from ~38.5MB to ~20.9MB).

### Scope

| Category | Count | Original | WebP | Savings |
|---|---|---|---|---|
| Drawable PNGs | 96 | 944 KB | 560 KB (lossless) | -41% |
| Mipmap PNGs | 15 | 528 KB | 340 KB (lossless) | -36% |
| Recipe JPGs (assets) | 154 | 30.9 MB | 14.6 MB (lossy q60) | -53% |
| **Total** | **265** | **~32.4 MB** | **~15.5 MB** | **~17.5 MB** |

### Files Changed

- **Images**: 154 recipe photos in `app/src/main/assets/recipes/images/` — JPG data re-encoded as lossy WebP q60, then renamed `.jpg` → `.webp`.
- **Drawables**: 96 PNGs in `app/src/main/res/drawable/` — lossless WebP (PNG source replaced in-place, same filenames).
- **Mipmaps**: 15 launcher icon PNGs in `app/src/main/res/mipmap-*/` — lossless WebP.
- **JSON imageUrl refs** (528 files): `app/src/main/assets/recipes/` and `fast_hungry_pack/src/main/assets/recipes/` — all `.jpg` → `.webp`.
- **DLC pack JSONs** (72 files): `eastern_traditional_pack/` and `exotic_tropics_pack/` — were missing `imageUrl` fields entirely. Added them pointing to main app assets.
- **MealsScreen.kt**: Hardcoded DLC preview URLs updated from `.jpg` → `.webp`.

### Quality Setting

- **Recipe images**: Lossy WebP quality **60**. Good visual quality at ~47% of original size.
- **First attempt** at q95 ballooned to 50MB — redone at q60.

### Verification

- Zero `.jpg` files left in `assets/recipes/images/`
- Zero stale `.jpg` references remaining in any JSON file
- All 200 images matched their JSON `imageUrl` references
- No file missing an `imageUrl` field
- Edge case: `mochi_gulung_mochi_rolls.html.jpg` → `mochi_gulung_mochi_rolls.html.webp` handled correctly

### Relevant Files

| File | Purpose |
|---|---|
| `app/src/main/assets/recipes/images/*.webp` | All 154 recipe images |
| `app/src/main/res/drawable/*.webp` | Drawable icons (96 files) |
| `app/src/main/res/mipmap-*/*.webp` | Launcher icons (15 files) |
| `app/src/main/assets/recipes/**/*.json` | Recipe data with imageUrl refs |
| `fast_hungry_pack/src/main/assets/recipes/**/*.json` | DLC recipe data |
| `eastern_traditional_pack/src/main/assets/recipes/**/*.json` | DLC pack (imageUrl added) |
| `exotic_tropics_pack/src/main/assets/recipes/**/*.json` | DLC pack (imageUrl added) |
| `app/src/main/java/com/munchies/kitchen/MealsScreen.kt` | DLC preview URLs |

## Recipe Name Styling

Changed recipe name in recipe detail TopAppBars to use `headlineSmall.copy(fontSize = 22.sp)` (22sp Bold with user's selected font — Roboto or Rubik). Removed the redundant conditional between `titleLarge` and `headlineSmall` (they render identically at 24sp Bold).

Files touched:
- `app/src/main/java/com/munchies/kitchen/RecipeDetailScreen.kt`
- `app/src/main/java/com/munchies/kitchen/BundledRecipeDetailScreen.kt`
