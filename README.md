# MapyNav-Amazfit

A **MapyNav-Amazfit** egy kétkomponensű navigációs HUD (Heads-Up Display) és haptikus értesítő rendszer, amely a **Mapy.cz** Androidos navigációját valós időben közvetíti az **Amazfit Balance / Balance 2** okosórára (Zepp OS 3.x / 4.x).

---

## Rendszer felépítése

```
MapyNav-Amazfit/
├── android/            # Android Companion alkalmazás (Kotlin, Jetpack Compose)
│   ├── app/
│   │   ├── src/main/java/hu/maci/mapynav/
│   │   │   ├── model/       # NavState, ManeuverType
│   │   │   ├── parser/      # MapyNotificationParser (HU, CZ, EN nyelvek)
│   │   │   ├── server/      # LocalNavigationServer (127.0.0.1:8088 HTTP REST/JSON)
│   │   │   ├── service/     # MapyNotificationListenerService (Értesítésfigyelő)
│   │   │   └── ui/          # Jetpack Compose UI (Státusz, tesztelő és HUD)
├── zeppos/             # Zepp OS Mini App Amazfit Balance-hez (480x480 AMOLED)
│   ├── app.json        # Zepp OS 3.0+ alkalmazás manifest
│   ├── app.js          # Alkalmazás életciklus
│   ├── app-side/       # Side Service (Zepp appban futó HTTP polling bridge)
│   ├── page/           # Device App (Órán futó 480x480 kör alakú AMOLED HUD)
│   └── assets/         # 120x120 méretű navigációs nyílikonok
├── docs/               # Rendszerspecifikáció és dokumentáció
└── tasks/              # Terv és állapotkezelés
```

---

## 1. Android Alkalmazás

### Főbb funkciók
- **Értesítés-olvasás:** A `cz.seznam.mapy` navigációs értesítéseit olvassa be.
- **Intelligens Parser:** Felismeri a manőver típusát (balra, jobbra, körforgalom hanyadik kijárat, enyhe/éles kanyarok, cél), a hátralévő távolságot méterben és kilométerben, valamint a következő utcát és az érkezési időt (ETA).
- **Helyi HTTP szerver:** `127.0.0.1:8088/api/nav` végponton azonnal elérhetővé teszi az aktuális állapotot a Zepp Side Service számára (teljesen offline, loopback interfész).
- **Beépített Tesztelő (Szimulátor):** Egyetlen kattintással szimulálható kanyarodás, körforgalom és célba érés az óra ellenőrzéséhez.

### Telepítés és Build
```bash
cd android
./gradlew assembleDebug
# A generált APK: app/build/outputs/apk/debug/app-debug.apk
adb install app/build/outputs/apk/debug/app-debug.apk
```

Az app első indításakor engedélyezni kell az **Értesítés-hozzáférést** a felugró gomb segítségével.

---

## 2. Zepp OS Mini App (Amazfit Balance)

### Kijelző (480x480 AMOLED)
- **Felső zóna:** Állapotkijelzés (Aktív / Készenlét).
- **Középső zóna:** Nagyméretű kanyarodási nyíl ikon + nagyméretű távolság kijelzés (pl. `150 m`).
- **Alsó zóna:** Következő utcanév (pl. `Kossuth Lajos utca`), körforgalom kijáratszám és ETA.
- **Képernyő ébrentartás:** A navigáció ideje alatt a kijelző nem kapcsol ki automatikusan.

### Haptika (Rezgésminták)
- **Balra kanyarodás:** 2 rövid rezgés.
- **Jobbra kanyarodás:** 1 hosszú rezgés.
- **Körforgalom:** 3 pulzáló rezgés.
- **Megérkezés:** Hosszú, duplázott rezgés.
- **Távolsági küszöbök:** 150 méternél, majd a manőver előtt rezeg.

### Telepítés az órára
1. A telefon **Zepp appjában** kapcsold be a Fejlesztői módot:
   * *Profil > Beállítások > Névjegy > Koppints 7-szer gyorsan a felső Zepp logóra*.
   * Lépj vissza a *Profil > Amazfit Balance* oldalra, és alul nyisd meg a *Fejlesztői mód* menüpontot.
2. A számítógépen a `zeppos` könyvtárban futtasd:
   ```bash
   cd "/home/maci/androidstudio projects/MapyNav-Amazfit/zeppos"
   npm install
   npm run build     # Létrehozza a .zab csomagot a dist/ mappában
   npm run preview   # Kiírja a terminálba a telepítő QR-kódot
   ```
3. A telefonos Zepp app *Fejlesztői mód > QR-kód olvasó* funkciójával olvasd be a terminálban megjelenő QR-kódot: a Zepp app közvetlenül Bluetoothon feltölti és telepíti a MapyNav-ot az Amazfit Balance-ra.

---

## 3. Hangos Navigáció (Magyar TTS az óra hangszóróján)

Az Amazfit Balance beépített hardveres hangszóróval rendelkezik:
1. Az órán nyisd meg a Vezérlőközpontot vagy a *Beállítások > Beállítások > Bluetooth hangszóró* menüt, és kapcsold be.
2. A telefon Bluetooth menüjében párosítsd a Balance-t média- és híváseszközként.
3. A Mapy.cz magyar nyelvű hangutasításai (*„100 méter múlva forduljon balra”*) közvetlenül az óra hangszóróján fognak szólni a vizuális nyilak és a rezgések mellett.
