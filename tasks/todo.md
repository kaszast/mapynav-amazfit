# MapyNav-Amazfit Feladatlista és Terv

## Terv lépései

- [x] 1. Projekt alapstruktúra és konfiguráció
  - [x] Android projekt alapok (Gradle kts, manifest, dependenciák) felállítása → ellenőrzés: Gradle fájlok szintaktikai ellenőrzése lefutott
  - [x] Zepp OS projekt alapok (`app.json`, `package.json`, mappastruktúra) létrehozása → ellenőrzés: Zepp OS 3.0 konfigurációs séma validálva

- [x] 2. Android Komponens fejlesztése
  - [x] Adatmodell (`NavState.kt`, `ManeuverType.kt`) létrehozása → ellenőrzés: JSON szerializáció és típuskonverziók zöldek
  - [x] Mapy.cz értesítés parser (`MapyNotificationParser.kt`) implementálása (magyar/cseh/angol szöveg és távolság) → ellenőrzés: 6 unit teszteset sikeresen lefutott
  - [x] Értesítésfigyelő szolgáltatás (`MapyNotificationListenerService.kt`) és Lifecycle/State kezelés → ellenőrzés: Kétirányú állapotfrissítés implementálva
  - [x] Beágyazott helyi webszerver (`LocalNavigationServer.kt`) megvalósítása (127.0.0.1:8088) → ellenőrzés: HTTP GET /api/nav és /api/health tesztek zöldek
  - [x] Jetpack Compose kezelőfelület (Jogosultság kérés, Élő HUD előnézet, Manuális szimulátor) → ellenőrzés: Sikeres APK build (`assembleDebug`)

- [x] 3. Zepp OS Mini App fejlesztése (Amazfit Balance)
  - [x] `app.json` konfigurálása (Balance 480x480 kör alakú kijelző, jogosultságok) → ellenőrzés: JSON validáció sikeres
  - [x] `app-side/index.js` (Side Service) megvalósítása: HTTP kliens a `127.0.0.1:8088`-hoz és BLE hívások → ellenőrzés: Szintaktika ellenőrizve
  - [x] Ikonkészlet (12 db 120x120-as kanyarodási és navigációs PNG ikon) előállítása → ellenőrzés: `zeppos/assets/` generálva
  - [x] `page/index.js` (Device App) felület: 480x480 AMOLED HUD (nagy nyíl, méterszámláló, utcanév, ETA) → ellenőrzés: JS szintaktika ellenőrizve
  - [x] Rezgésmotor (`Vibrator`) vezérlés egyedi mintákkal a manőver típusokhoz → ellenőrzés: Implementálva és küszöbökhöz kötve

- [x] 4. Integrációs tesztelés és végpontok ellenőrzése
  - [x] Android backend és szimulált Mapy.cz események átvitele és modellezése → ellenőrzés: Unit tesztek és APK build 100%-ban sikeres
  - [x] Hibakezelés (kapcsolat megszakadása, navigáció leállítása / idle állapot) → ellenőrzés: IDLE állapot és automatikus tisztítás beállítva

- [x] 5. Lokalizáció és Nyelvválasztó
  - [x] Angol (alapértelmezett fallback) és magyar szótárak (`strings.xml`, `values-hu/strings.xml`) → ellenőrzés: Resource-ok lefordítva
  - [x] Dinamikus in-app nyelvváltó (`LocaleHelper`, `SharedPreferences`, Android 13+ LocaleManager) → ellenőrzés: Compose és Context megfelelően frissül
- [x] 6. Android UI Professzionális újratervezése
  - [x] Window Insets / Status Bar padding javítása (Scaffold, TopAppBar, enableEdgeToEdge) → ellenőrzés: Safe areák és státuszbár insets kezelve
  - [x] Dashboard és Kártyastruktúra harmonizálása (Rendszerállapot összefogása, INACTIVE sortörés javítása) → ellenőrzés: Letisztult állapot-sorok, levágásmentes badge
  - [x] Teszt szimulátor grid és Live HUD áttervezése → ellenőrzés: 2 oszlopos rendezett gombok, szép HUD
- [x] 7. Univerzális kerek Amazfit támogatás
  - [x] Biztonsági mentés elkészítése (Git commit `93de18b` + fizikai `MapyNav-Amazfit_zeppos_balance_backup` könyvtár) → ellenőrzés: Visszaállítási pont rögzítve
  - [x] Zepp OS v3 univerzális round target (`st: "r"`, API 2.0-3.0+) konfigurálása az `app.json`-ban → ellenőrzés: 30 kerek Amazfit típus, 75 platform lefedve
  - [x] Dinamikus layout (`getLayout()`, valós képernyőméret lekérdezés, méretezési skálázás 360-tól 480 px-ig) → ellenőrzés: Kerek kijelző arányok és margók adaptívak
  - [x] Univerzális `.zab` csomagolás (`zeus build`) és Android feliratok általánosítása → ellenőrzés: `.zab` build sikeres, Android unit tesztek zöldek

- [x] 8. Verziószám és Release automatizáció
  - [x] Verziószám (`v1.0.0`) elhelyezése az Android fejlécben a MapyNav cím mellett kisebb betűmérettel → ellenőrzés: Képernyőkép és layout ellenőrizve
  - [x] Release telepítőcsomagok (`releases/MapyNav-v1.0.0.apk`, `releases/MapyNav-v1.0.0.zab`) előállítása és GitHub Actions workflow (`.github/workflows/release.yml`) → ellenőrzés: Fájlok léteznek és érvényesek
  - [x] Zepp OS 512x512 app ikon generálása a 248 px build figyelmeztetés megszüntetésére → ellenőrzés: `npm run build` warning nélkül lefutott, automatikus átméretezés 248/124/80 méretre sikeres

- [x] 9. Kétnyelvű dokumentáció és CI Release publikálás
  - [x] Átfogó kétnyelvű (angol és magyar) `README.md` létrehozása a követelményekkel, architektúrával és build lépésekkel → ellenőrzés: Fájl mentve és ellenőrizve
  - [x] GitHub Actions release munkafolyamat (`.github/workflows/release.yml`) javítása (workspace abszolút útvonalak, npm lockfile szinkron) → ellenőrzés: CI lefutott zölden
  - [x] GitHub Release v1.0.0 közzététele binárisokkal (`MapyNav-v1.0.0.apk` és `MapyNav-v1.0.0.zab`) → ellenőrzés: GitHub Release éles és letölthető

---

## Review
- GitHub Release v1.0.0: https://github.com/kaszast/mapynav-amazfit/releases/tag/v1.0.0
- Rendszerspecifikáció: `docs/SPECIFICATION.md`
- Kétnyelvű projekt dokumentáció: `README.md`
- Android APK: `releases/MapyNav-v1.0.0.apk` (18.7 MB)
- Zepp OS Universal Round ZAB csomag: `releases/MapyNav-v1.0.0.zab` (782 KB, 30 kerek Amazfit típus, 75 platform)
- Biztonsági mentés pont: Git commit `93de18b` és `/home/maci/androidstudio projects/MapyNav-Amazfit_zeppos_balance_backup/`


