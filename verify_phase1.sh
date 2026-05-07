#!/bin/bash

echo "=========================================="
echo "Phase 1 Verification Script"
echo "=========================================="
echo ""

# Check if asset pack module exists
if [ -d "italian_premium_pack" ]; then
    echo "✅ Asset pack module directory exists"
else
    echo "❌ Asset pack module directory NOT found"
    exit 1
fi

# Check if build.gradle exists
if [ -f "italian_premium_pack/build.gradle" ]; then
    echo "✅ Asset pack build.gradle exists"
else
    echo "❌ Asset pack build.gradle NOT found"
    exit 1
fi

# Check if assets directory exists
if [ -d "italian_premium_pack/src/main/assets/recipes" ]; then
    echo "✅ Assets directory structure exists"
else
    echo "❌ Assets directory structure NOT found"
    exit 1
fi

# Check settings.gradle.kts
if grep -q "italian_premium_pack" settings.gradle.kts; then
    echo "✅ Asset pack included in settings.gradle.kts"
else
    echo "❌ Asset pack NOT included in settings.gradle.kts"
    exit 1
fi

# Check project build.gradle.kts
if grep -q "com.android.asset-pack" build.gradle.kts; then
    echo "✅ Asset pack plugin added to project build.gradle.kts"
else
    echo "❌ Asset pack plugin NOT found in project build.gradle.kts"
    exit 1
fi

# Check app build.gradle.kts for dependencies
if grep -q "asset-delivery" app/build.gradle.kts; then
    echo "✅ Asset delivery dependencies added to app/build.gradle.kts"
else
    echo "❌ Asset delivery dependencies NOT found in app/build.gradle.kts"
    exit 1
fi

# Check app build.gradle.kts for assetPacks
if grep -q "assetPacks" app/build.gradle.kts; then
    echo "✅ Asset pack linked in app/build.gradle.kts"
else
    echo "❌ Asset pack NOT linked in app/build.gradle.kts"
    exit 1
fi

echo ""
echo "=========================================="
echo "✅ Phase 1 Setup Complete!"
echo "=========================================="
echo ""
echo "Next Steps:"
echo "1. Open Android Studio"
echo "2. Sync Gradle (File → Sync Project with Gradle Files)"
echo "3. Verify no errors in Build output"
echo "4. Proceed to Phase 2"
echo ""
