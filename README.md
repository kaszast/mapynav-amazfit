# MapyNav-Amazfit

[![GitHub Release](https://img.shields.io/github/v/release/kaszast/mapynav-amazfit)](https://github.com/kaszast/mapynav-amazfit/releases)
[![Build & Publish Release](https://github.com/kaszast/mapynav-amazfit/actions/workflows/release.yml/badge.svg)](https://github.com/kaszast/mapynav-amazfit/actions/workflows/release.yml)
[![Zepp OS](https://img.shields.io/badge/Zepp%20OS-2.0%2B%20%7C%203.x%20%7C%204.x-orange.svg)](https://docs.zepp.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

*Read this document in: [English](#english) | [Magyar](#magyar)*

---

<a name="english"></a>
## English

### Overview

**MapyNav-Amazfit** is a real-time navigation HUD (Heads-Up Display) and haptic notification bridge between the **Mapy.cz** Android navigation app and **Amazfit** smartwatches running **Zepp OS** (2.0+, 3.x, and 4.x).

It features **universal round smartwatch support**, automatically scaling layout, fonts, and icons for all circular Amazfit devices (360×360, 416×416, 454×454, 466×466, 480×480 across NXP, ZPS, and Apollo architectures).

#### Key Features
- **Real-time Navigation Mirroring:** Captures turn-by-turn maneuvers, remaining distance, current street name, roundabout exits, and ETA directly from Mapy.cz notifications.
- **Adaptive AMOLED HUD:** Optimized for round watch screens with high-contrast maneuver arrows, dynamic scaling, current clock time, and distance readout.
- **Smart Screen Wake:** Screen turns on and stays awake only while active navigation events occur, preserving watch battery.
- **Directional Haptics:** Distinct vibration patterns for left, right, roundabout, and arrival at customizable distance thresholds.
- **Background Reliability:** Android background foreground service with battery optimization exemption to prevent system sleep.
- **Dual-Language Android UI:** Clean Jetpack Compose interface in English (default) and Hungarian, with an in-app language switch and icon test simulator.
- **TTS Audio Over Watch Speaker:** If the watch is paired as a Bluetooth audio device, voice prompts (*"In 200 meters turn right"*) are played via the watch speaker.

---

### Architecture

```
MapyNav-Amazfit/
├── android/                 # Android Companion App (Kotlin, Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/hu/maci/mapynav/
│   │   │   ├── model/       # Data models: NavState, ManeuverType
│   │   │   ├── parser/      # MapyNotificationParser (HU, CZ, EN recognition)
│   │   │   ├── server/      # Embedded local HTTP server (127.0.0.1:8088)
│   │   │   ├── service/     # NotificationListenerService & Foreground Service
│   │   │   └── ui/          # Compose UI: Dashboard, Live HUD, Test simulator
├── zeppos/                  # Universal Zepp OS Mini App
│   ├── app.json             # Manifest with targets.round ("st": "r", API 2.0-3.0+)
│   ├── app.js               # Lifecycle management
│   ├── app-side/            # Zepp Side Service (HTTP bridge to 127.0.0.1:8088)
│   ├── page/                # Device App (Adaptive layout & AMOLED HUD)
│   └── assets/round.r/      # Scaled navigation icons and 512x512 app icon
├── releases/                # Ready-to-install pre-built binaries (.apk, .zab)
└── .github/workflows/       # CI/CD automated release build workflow
```

---

### Prerequisites & Dependencies

Before building the project, ensure you have the following installed on your system:

| Component | Required Version | Description / Package |
| :--- | :--- | :--- |
| **Java JDK** | 17 (JDK 17) | OpenJDK 17 (`openjdk-17-jdk` on Linux) |
| **Android SDK** | API 35/37 | Android SDK Platform 35+, Build-Tools 35.0.0+ |
| **Gradle** | 9.8.0 | Managed via Gradle Wrapper (`./gradlew`) |
| **Node.js** | 18.x or 20.x+ | Node runtime with npm |
| **Zeus CLI** | ^1.9.3 | Zepp OS build tool (`@zeppos/zeus-cli`) |
| **Python** *(Optional)* | 3.8+ | Python with `Pillow` (only if regenerating icons) |

#### Linux (Ubuntu/Debian) Dependency Setup:
```bash
# Java 17 and Android tools
sudo apt update
sudo apt install -y openjdk-17-jdk curl git

# Node.js 20.x
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Optional: Zeus CLI globally (or use local npm run scripts in zeppos/)
sudo npm install -g @zeppos/zeus-cli
```

---

### Building the Project

#### 1. Android Companion App (.apk)
```bash
cd android
./gradlew assembleDebug

# Output APK location:
# android/app/build/outputs/apk/debug/app-debug.apk

# Install onto connected phone via ADB:
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 2. Zepp OS Universal Package (.zab)
```bash
cd zeppos
npm install
npm run build

# Output package location:
# zeppos/dist/1058291-MapyNav-1.0.0-*.zab
```

---

### Installation & Setup

#### Easy Install (Pre-built Binaries)
Download the latest `MapyNav-v1.0.0.apk` and `MapyNav-v1.0.0.zab` from the [GitHub Releases](https://github.com/kaszast/mapynav-amazfit/releases) page.

#### Installing the Zepp OS App onto Your Watch
1. Enable Developer Mode in the **Zepp app** on your phone:
   - Go to **Profile** > **Settings** > **About** > Tap the **Zepp logo 7 times quickly**.
   - Navigate back to **Profile** > your watch device page > scroll down to **Developer Mode**.
2. Run preview mode on your computer:
   ```bash
   cd zeppos
   npm run preview
   ```
   *(Select your watch model when prompted; for example, Amazfit Balance / Balance 2, GTR 4, T-Rex 3, etc.)*
3. In the mobile Zepp app, open **Developer Mode** > tap the **QR Scanner icon** at the top right, and scan the QR code displayed in your terminal. The package will transfer and install via Bluetooth.

#### Android Companion App Configuration
1. Open the **MapyNav** app on your phone.
2. Grant **Notification Access** (*Értesítés-hozzáférés*) for MapyNav when prompted.
3. Exclude the app from **Battery Optimization** to allow smooth background execution during navigation.
4. Start navigation in **Mapy.cz** — the watch HUD will activate automatically.

---

<a name="magyar"></a>
## Magyar

### Áttekintés

A **MapyNav-Amazfit** egy valós idejű navigációs HUD (Heads-Up Display) és haptikus értesítő rendszer a **Mapy.cz** Androidos alkalmazás és a **Zepp OS** (2.0+, 3.x, 4.x) rendszert futtató **Amazfit** okosórák között.

A szoftver **univerzális kerek kijelzős támogatással** rendelkezik: automatikusan és arányosan skálázza a felületet, az ikonokat és a betűméreteket minden kerek Amazfit órára (360×360, 416×416, 454×454, 466×466, 480×480 felbontásokon, NXP, ZPS és Apollo processzorcsaládokon).

#### Főbb funkciók
- **Valós idejű navigáció-tükrözés:** Kanyarodási manőverek, hátralévő méterek/kilométerek, utcanév, körforgalmi kijáratszám és érkezési idő (ETA) beolvasása a Mapy.cz értesítéseiből.
- **Adaptív AMOLED HUD:** Kerek kijelzőre optimalizált megjelenés, nagy kontrasztú nyilak, alsó pontosidő- és távolságkijelzés.
- **Okos képernyő-ébrentartás:** A kijelző csak aktív navigáció és beérkező utasítás esetén kapcsol be és marad ébren, kímélve az óra akkumulátorát.
- **Egyedi rezgésminták:** Megkülönböztethető haptikus visszajelzés balra/jobbra kanyarodáskor, körforgalomnál és célba érkezéskor.
- **Megbízható háttérfutás:** Android előtérbeli szolgáltatás (foreground service) akkumulátorkímélő-kivétellel, megakadályozva az alkalmazás leállítását.
- **Kétnyelvű kezelőfelület:** Angol (alapértelmezett) és magyar nyelv, beépített nyelvválasztóval és manővertesztelő szimulátorral.
- **Hangos navigáció az órán:** Ha az óra Bluetooth audio eszközként csatlakozik a telefonhoz, a magyar hangutasítások (*„200 méter múlva forduljon jobbra”*) közvetlenül az óra hangszóróján szólnak.

---

### Rendszer felépítése

```
MapyNav-Amazfit/
├── android/                 # Android Companion app (Kotlin, Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/hu/maci/mapynav/
│   │   │   ├── model/       # Adatmodellek: NavState, ManeuverType
│   │   │   ├── parser/      # MapyNotificationParser (HU, CZ, EN szövegelemzés)
│   │   │   ├── server/      # Beágyazott HTTP webszerver (127.0.0.1:8088)
│   │   │   ├── service/     # Értesítésfigyelő és előtérbeli háttérszolgáltatás
│   │   │   └── ui/          # Compose felület: Státusz, Élő HUD, Tesztelő
├── zeppos/                  # Univerzális Zepp OS Mini App
│   ├── app.json             # Manifest targets.round konfigurációval ("st": "r")
│   ├── app.js               # Alkalmazás életciklus
│   ├── app-side/            # Zepp Side Service (HTTP bridge a telefonhoz)
│   ├── page/                # Órán futó felület (Adaptív elrendezés és HUD)
│   └── assets/round.r/      # Skálázott navigációs ikonok és 512x512 app ikon
├── releases/                # Előre lefordított telepítőcsomagok (.apk, .zab)
└── .github/workflows/       # GitHub Actions automata release munkafolyamat
```

---

### Szükséges csomagok és előfeltételek

A forráskód fordításához az alábbi környezet szükséges:

| Komponens | Elvárt verzió | Leírás / Csomag |
| :--- | :--- | :--- |
| **Java JDK** | 17 (JDK 17) | OpenJDK 17 (`openjdk-17-jdk`) |
| **Android SDK** | API 35/37 | Android SDK Platform 35+, Build-Tools 35.0.0+ |
| **Gradle** | 9.8.0 | A beépített Gradle Wrapper kezeli (`./gradlew`) |
| **Node.js** | 18.x vagy 20.x+ | Node.js környezet és npm |
| **Zeus CLI** | ^1.9.3 | Zepp OS fordítóeszköz (`@zeppos/zeus-cli`) |
| **Python** *(Opcionális)* | 3.8+ | Python Pillow könyvtárral (csak ikon-újrageneráláshoz) |

#### Szükséges eszközök telepítése Ubuntu/Debian Linuxon:
```bash
# Java 17 és alapeszközök
sudo apt update
sudo apt install -y openjdk-17-jdk curl git

# Node.js 20.x
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt install -y nodejs

# Zeus CLI telepítése globálisan (vagy lokálisan a zeppos könyvtáron belül)
sudo npm install -g @zeppos/zeus-cli
```

---

### Fordítás (Build)

#### 1. Android alkalmazás fordítása (.apk)
```bash
cd android
./gradlew assembleDebug

# Elkészült fájl helye:
# android/app/build/outputs/apk/debug/app-debug.apk

# Telepítés csatlakoztatott telefonra ADB-n keresztül:
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### 2. Zepp OS univerzális csomag készítése (.zab)
```bash
cd zeppos
npm install
npm run build

# Elkészült univerális telepítőcsomag:
# zeppos/dist/1058291-MapyNav-1.0.0-*.zab
```

---

### Telepítés és Beállítás

#### Gyors telepítés (Előre lefordított csomagok)
Töltsd le a legfrissebb `MapyNav-v1.0.0.apk` és `MapyNav-v1.0.0.zab` fájlokat a [GitHub Releases](https://github.com/kaszast/mapynav-amazfit/releases) oldalról vagy a projekt `releases/` mappájából.

#### Zepp OS App telepítése az órára
1. Kapcsold be a Fejlesztői módot a telefonos **Zepp appban**:
   * **Profil** > **Beállítások** > **Névjegy** > Érintsd meg a **Zepp logót 7-szer gyorsan**.
   * Lépj vissza a **Profil** > óra típusa oldalra, és görgess le a **Fejlesztői mód** menüponthoz.
2. Indítsd el az előnézeti/telepítési módot a számítógépen:
   ```bash
   cd zeppos
   npm run preview
   ```
   *(Válaszd ki az órád típusát a listából: pl. Amazfit Balance / Balance 2, GTR 4, T-Rex 3, stb.)*
3. A telefonos Zepp appban nyisd meg a **Fejlesztői mód** menüt, koppints a jobb felső **QR-kód olvasóra**, és olvasd be a terminálban megjelenő QR-kódot. A csomag Bluetoothon keresztül azonnal települ az órára.

#### Android Companion app beállítása
1. Indítsd el a **MapyNav** appot a telefonon.
2. Add meg az **Értesítés-hozzáférés** jogosultságot a gombra kattintva.
3. Add hozzá a MapyNav-ot az **Akkumulátorkímélő kivételeihez**, hogy a rendszer a képernyő lezárásakor se állítsa le a háttérfolyamatot.
4. Indíts el egy útvonaltervezést a **Mapy.cz**-ben — az órán a navigációs kijelző azonnal aktiválódik.

---

### Licenc

MIT License — kaszast
