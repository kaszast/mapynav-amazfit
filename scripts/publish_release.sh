#!/usr/bin/env bash
set -e

VERSION="${1:-1.0.0}"
TAG="v${VERSION}"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "=== MapyNav Release Előkészítés (${TAG}) ==="

cd "$ROOT_DIR"
mkdir -p releases

echo "1. Android APK másolása..."
if [ -f "android/app/build/outputs/apk/debug/app-debug.apk" ]; then
    cp "android/app/build/outputs/apk/debug/app-debug.apk" "releases/MapyNav-${TAG}.apk"
    echo "   -> releases/MapyNav-${TAG}.apk kész."
else
    echo "   APK nem található, fordítás szükséges: cd android && ./gradlew assembleDebug"
fi

echo "2. Zepp OS ZAB csomag másolása..."
LATEST_ZAB=$(ls -t zeppos/dist/*.zab 2>/dev/null | head -n 1 || true)
if [ -n "$LATEST_ZAB" ]; then
    cp "$LATEST_ZAB" "releases/MapyNav-${TAG}.zab"
    echo "   -> releases/MapyNav-${TAG}.zab kész."
else
    echo "   ZAB nem található, fordítás szükséges: cd zeppos && npm run build"
fi

echo ""
echo "3. Git Tag létrehozása helyben..."
if git rev-parse "$TAG" >/dev/null 2>&1; then
    echo "   A(z) $TAG tag már létezik."
else
    git tag -a "$TAG" -m "MapyNav Release $TAG"
    echo "   -> $TAG tag sikeresen létrehozva."
fi

echo ""
echo "=== Kész! ==="
echo "A telepítőcsomagok elérhetők a 'releases/' könyvtárban:"
ls -lh releases/
echo ""
echo "A GitHub Releases közzétételéhez futtasd a push parancsot:"
echo "   git push origin main"
echo "   git push origin $TAG"
echo "A GitHub Actions workflow automatikusan közzéteszi a Release-t az APK és ZAB csomagokkal!"
