# Zepp App Store Listing Details for MapyNav

Use these pre-formatted fields when submitting the application on the [Zepp Developer Console](https://console.zepp.com).

---

## 1. Basic Information

* **Application Name:** `MapyNav` (or `MapyNav - Navigation HUD`)
* **App ID:** `1129966`
* **Version:** `1.0.0`
* **Category:** `Tools` (or `Navigation` / `Sports & Health`)
* **Price:** `Free`
* **Supported Devices:** Universal Round (all circular Amazfit watches: Balance, Balance 2, GTR 4, GTR 3 / 3 Pro, T-Rex 3, T-Rex Ultra, Cheetah, Cheetah Pro, Active Edge, etc.)
* **Privacy Policy URL:** `https://raw.githubusercontent.com/kaszast/mapynav-amazfit/main/docs/PRIVACY_POLICY.md`  
  *(or link to the GitHub repository: `https://github.com/kaszast/mapynav-amazfit/blob/main/docs/PRIVACY_POLICY.md`)*

---

## 2. Short Summary (Brief Introduction)

> Real-time turn-by-turn navigation HUD and directional haptics for Mapy.com (Mapy.cz) on your Amazfit smartwatch.

---

## 3. Detailed Description (Full Introduction)

```text
MapyNav transforms your Amazfit smartwatch into a clear, glanceable Heads-Up Display (HUD) for navigation powered by Mapy.com (Mapy.cz).

Designed specifically for cyclists, drivers, hikers, and runners, MapyNav mirrors active turn-by-turn maneuvers directly onto your wrist without needing to look at your phone.

Key Features:
• Crystal-Clear AMOLED HUD: High-contrast directional turn arrows, exact remaining distance in meters/kilometers, next street name, roundabout exits, and current clock time.
• Directional Haptics: Distinct, customizable vibration patterns for left turns, right turns, roundabouts, and arrival so you never miss an exit.
• Smart Battery Saving: The watch display only wakes and stays bright during active navigation events, preserving battery life during all-day trips.
• Universal Round Watch Design: Dynamic pixel-perfect scaling optimized for all round Amazfit smartwatches (Zepp OS 2.0+ to 4.x).
• Voice Guidance Support: If your Amazfit watch has a built-in speaker and is paired for audio, turn prompts can play aloud directly from your wrist.
• Complete Privacy: 100% offline local communication over Bluetooth. No location tracking, no analytics, no external servers.

Requirements:
1. Mapy.com (or Mapy.cz) installed on your Android smartphone.
2. Free MapyNav Companion Android App (downloadable from GitHub Releases).
3. Notification access granted on the Android companion app.

Stay on track with confidence — hands-free and distraction-free!
```

---

## 4. Review Notes / Testing Instructions for Zepp Reviewers

```text
Testing Instructions for Reviewers:

Dear Review Team,

MapyNav is a companion navigation app that receives navigation instructions from Mapy.com (Mapy.cz) running on an Android phone.

To test the application easily without needing to drive on the road:
1. Install the MapyNav Companion Android app on your test device:
   Download APK: https://github.com/kaszast/mapynav-amazfit/releases/download/v1.0.0/MapyNav-v1.0.0.apk
2. Open MapyNav on Android and grant the requested "Notification Access" permission.
3. Open the MapyNav app on the Amazfit smartwatch.
4. On the Android app screen, scroll down to the "Test Simulator" section and tap the test buttons ("Turn Left", "Roundabout", "Turn Right", "Destination").
5. The watch HUD will instantly update with high-contrast maneuver arrows, distance readouts, street names, and corresponding haptic vibrations.

Privacy Note:
The app communicates strictly locally via 127.0.0.1 on the phone and through the standard Zepp OS Bluetooth bridge. No personal data or location coordinates are collected or transmitted to any external server.

Source Code & Documentation:
https://github.com/kaszast/mapynav-amazfit
```

---

## 5. Store Assets Checklist

* [x] **App Icon (512x512):** `store_assets/app_icon_512x512.png`
* [x] **Screenshot 1 (Turn Left):** `store_assets/screenshots/screenshot_1_turn_left.png`
* [x] **Screenshot 2 (Roundabout):** `store_assets/screenshots/screenshot_2_roundabout.png`
* [x] **Screenshot 3 (Turn Right):** `store_assets/screenshots/screenshot_3_turn_right.png`
* [x] **Screenshot 4 (Destination):** `store_assets/screenshots/screenshot_4_destination.png`
* [x] **Zepp OS Package (.zab):** `zeppos/dist/1129966-MapyNav-1.0.0-*.zab` (or `releases/MapyNav-v1.0.0.zab`)
