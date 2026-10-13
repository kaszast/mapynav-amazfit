# Zepp App Store Listing Details for NaviHUD

Use these pre-formatted fields when submitting the application on the [Zepp Developer Console](https://console.zepp.com).

---

## 1. Basic Information

* **Application Name:** `NaviHUD` *(Must exactly match package name `NaviHUD`)*
* **App ID:** `1129966`
* **Version:** `1.0.1` (Version Code: `2`)
* **Category:** `Tools` (or `Navigation` / `Sports & Health`)
* **Price:** `Free`
* **Supported Devices:** Universal Round (all circular Amazfit watches: Balance, Balance 2, GTR 4, GTR 3 / 3 Pro, T-Rex 3, T-Rex Ultra, Cheetah, Cheetah Pro, Active Edge, etc.)
* **Privacy Policy URL:** `https://raw.githubusercontent.com/kaszast/mapynav-amazfit/main/docs/PRIVACY_POLICY.md`  
  *(or link to the GitHub repository: `https://github.com/kaszast/mapynav-amazfit/blob/main/docs/PRIVACY_POLICY.md`)*

---

## 2. English Store Listing (Angol szövegek)

### Brief Introduction (Rövid leírás - 75 karakter / max 100)
```text
Real-time turn-by-turn navigation HUD and haptics for Amazfit smartwatches.
```

### Detailed Description (Részletes leírás - 438 karakter)
```text
NaviHUD turns your Amazfit smartwatch into a clear navigation Heads-Up Display (HUD).

Features:
• Crystal-clear turn arrows and exact remaining distance
• Street names, roundabout exit numbers, and ETA
• Directional haptic vibration alerts for turns
• AMOLED battery saver: wakes display only on turn events
• 100% offline local Bluetooth link, zero data tracking

Requires the free NaviHUD Companion Android app installed on your phone.
```

### Ultra-compact Description (Ha a mező limit < 250 karakter - 222 karakter)
```text
NaviHUD turns your Amazfit smartwatch into a clear navigation HUD.
• High-contrast turn arrows & remaining distance
• Street names & roundabout exits
• Directional vibration alerts
• 100% offline local Bluetooth connection
```

---

## 3. Hungarian Store Listing (Magyar szövegek)

### Brief Introduction (Rövid leírás - 69 karakter / max 100)
```text
Valós idejű navigációs HUD és rezgéses értesítések Amazfit okosórára.
```

### Detailed Description (Részletes leírás - 443 karakter)
```text
A NaviHUD valós idejű navigációs kijelzővé (HUD) alakítja Amazfit okosórádat.

Főbb funkciók:
• Nagy kontrasztú kanyarodási nyilak és pontos távolság
• Utcanevek, körforgalmi kijáratok és érkezési idő
• Megkülönböztethető rezgésminták kanyaroknál
• AMOLED akkumulátorkímélő: csak manővereknél ébred
• 100% offline helyi Bluetooth kapcsolat, nincs adatgyűjtés

Használatához a telefonra telepített ingyenes NaviHUD Android alkalmazás szükséges.
```

### Ultra-compact Description (Ha a mező limit < 250 karakter - 234 karakter)
```text
A NaviHUD navigációs kijelzővé (HUD) alakítja Amazfit okosórádat.
• Nagy kontrasztú kanyarodási nyilak és távolság
• Utcanevek és körforgalmi kijáratok
• Megkülönböztethető rezgéses értesítések
• 100% offline helyi Bluetooth kapcsolat
```

---

## 4. Review Notes / Testing Instructions for Zepp Reviewers

```text
Dear Review Team,

Thank you for your guidance. In this submission (v1.0.1):
1. App Name: Updated to "NaviHUD" consistently in both App Store listing and app package (app.json) to comply with trademark guidelines.
2. App Icon: Packaged icon strictly adheres to the 248x248 px circular silhouette specification with the required 4 px transparent safe area padding.
3. Preview Images: Provided 360x360 px RGBA circular screenshots with transparent background outside the round display for both English (en) and Hungarian (hu).

How to test the application easily without needing to drive on the road:
1. Install the NaviHUD Companion Android app on your test device:
   Download APK: https://github.com/kaszast/mapynav-amazfit/releases/download/v1.0.0/MapyNav-v1.0.0.apk
2. Open NaviHUD on Android and grant the requested "Notification Access" permission.
3. Open the NaviHUD app on the Amazfit smartwatch.
4. On the Android app screen, scroll down to the "Test Simulator" section and tap the test buttons ("Turn Left", "Roundabout", "Turn Right", "Destination").
5. The watch HUD will instantly update with high-contrast maneuver arrows, distance readouts, street names, and corresponding haptic vibrations.

Privacy Note:
The app communicates strictly locally via 127.0.0.1 on the phone and through the standard Zepp OS Bluetooth bridge. No personal data or location coordinates are collected or transmitted to any external server.

Source Code & Documentation:
https://github.com/kaszast/mapynav-amazfit
```

---

## 5. Store Assets Checklist

* [x] **Store Icon (240x240 circular PNG):** `store_assets/app_icon_240x240_circular.png`
* [x] **Large Icon (512x512 PNG):** `store_assets/app_icon_512x512.png`
* [x] **Round Screen Previews (English - 360x360 RGBA):**
  * Preview 1: `store_assets/screenshots/en/screenshot_1.png`
  * Preview 2: `store_assets/screenshots/en/screenshot_2.png`
  * Preview 3: `store_assets/screenshots/en/screenshot_3.png`
  * Preview 4: `store_assets/screenshots/en/screenshot_4.png`
* [x] **Round Screen Previews (Hungarian - 360x360 RGBA):**
  * Preview 1: `store_assets/screenshots/hu/screenshot_1.png`
  * Preview 2: `store_assets/screenshots/hu/screenshot_2.png`
  * Preview 3: `store_assets/screenshots/hu/screenshot_3.png`
  * Preview 4: `store_assets/screenshots/hu/screenshot_4.png`
* [x] **Zepp OS Package (.zab):** `store_assets/1129966-NaviHUD-1.0.1.zab` (and `releases/NaviHUD-v1.0.1.zab`)
