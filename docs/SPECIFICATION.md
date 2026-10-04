# MapyNav-Amazfit - Rendszerspecifikáció

## 1. Áttekintés és Cél
A projekt célja egy kétkomponensű navigációs rendszer létrehozása:
- **Android Companion alkalmazás:** A Mapy.cz (`cz.seznam.mapy`) Androidos navigációs értesítéseit valós időben elfogja, feldolgozza (manővertípus, távolság, utcanév), és helyi kapcsolaton közzéteszi.
- **Zepp OS Mini App (Amazfit Balance / Balance 2):** Az órán futó alkalmazás, amely a Zepp BLE bridge-en keresztül fogadja az adatokat, és a 480x480-as AMOLED kijelzőn megjeleníti a kanyarodási nyilat, távolságot, utcanevet, valamint manőver előtt egyedi rezgésmintát ad.
- **Hang / TTS:** A telefon navigációs hangja a telefon média/Bluetooth audio csatornáján keresztül közvetlenül a Balance óra beépített hangszóróján szólal meg (Bluetooth Speaker mód).

---

## 2. Rendszerarchitektúra

```
┌────────────────────────────────────────────────────────┐
│ Android Telefon                                        │
│                                                        │
│  ┌───────────────────────┐                             │
│  │ Mapy.cz App           │                             │
│  │ (Navigáció fut)       │                             │
│  └──────────┬────────────┘                             │
│             │ Értesítés (Ongoing Notification)         │
│             ▼                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │ MapyNav Android Companion App                     │  │
│  │  - NotificationListenerService                   │  │
│  │  - MapyNotificationParser (Irány, m, utca, ETA)  │  │
│  │  - Local HTTP/WS Server (127.0.0.1:8088)         │  │
│  │  - Jetpack Compose UI (Státusz, tesztelő, config)│  │
│  └──────────────────┬───────────────────────────────┘  │
│                     │ HTTP/WS                          │
│                     ▼                                  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Zepp App (Hivatalos keretrendszer)               │  │
│  │  └─ App-Side-Service (Mini App háttérszál)       │  │
│  └──────────────────┬───────────────────────────────┘  │
└─────────────────────┼──────────────────────────────────┘
                      │ Bluetooth Low Energy (Zepp BLE)
┌─────────────────────┼──────────────────────────────────┐
│ Amazfit Balance     ▼                                  │
│  ┌──────────────────────────────────────────────────┐  │
│  │ Zepp OS Device App (480x480 AMOLED HUD)          │  │
│  │  - Kanyarodási nyíl grafika (SVG / Vektor / PNG) │  │
│  │  - Hátralévő távolság kijelzés (pl. "150 m")     │  │
│  │  - Utcanév / cél kijelzés                        │  │
│  │  - Haptikus rezgésmotor vezérlés (egyedi minták) │  │
│  │  - Képernyő ébrentartás                          │  │
│  └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
```

---

## 3. Komponensek részletes specifikációja

### 3.1. Android Companion App (`android/`)
- **Nyelv és keretrendszer:** Kotlin, Jetpack Compose, Material 3, Android 8.0+ (API 26 - 35).
- **Modulok és osztályok:**
  1. `service/MapyNotificationListenerService.kt`:
     - Android `NotificationListenerService` implementáció.
     - Csak a `cz.seznam.mapy` csomagra szűr.
     - Elfogja a folyamatos értesítések frissüléseit (`onNotificationPosted`, `onNotificationRemoved`).
  2. `parser/MapyNotificationParser.kt`:
     - Kinyeri a szöveges tartalmakat: Cím (pl. „200 m múlva forduljon jobbra”), Szöveg (pl. „Fő utca”), Kiegészítő infó (ETA, hátralévő km).
     - Elemzi az értesítés ikonját vagy a szöveg kulcsszavait (magyar, cseh, angol regex/pattern matching).
     - Manőver típusok: `STRAIGHT`, `TURN_LEFT`, `TURN_RIGHT`, `SLIGHT_LEFT`, `SLIGHT_RIGHT`, `SHARP_LEFT`, `SHARP_RIGHT`, `U_TURN`, `ROUNDABOUT_1`..`ROUNDABOUT_6`, `DESTINATION_REACHED`.
  3. `server/LocalNavigationServer.kt`:
     - Beágyazott könnyűsúlyú HTTP / WebSocket szerver (`127.0.0.1:8088`).
     - REST API: `GET /api/nav` -> Aktuális navigációs JSON állapot.
     - WebSocket: `/api/ws` -> Valós idejű push üzenetek változás esetén.
  4. `ui/`:
     - Engedélyek kezelése (Notification Listener bekapcsolása).
     - Élő állapotkijelző (szerver státusz, csatlakozott óra, utolsó detektált manőver).
     - Szimulátor / Tesztelő (teszt kanyarok küldése az órára fizikai séta nélkül).
     - Rezgésminta beállítások.

### 3.2. Zepp OS Mini App (`zeppos/`)
- **Kompatibilitás:** Amazfit Balance (Zepp OS 3.0 / 3.5 / 4.0, 480x480 kör alakú kijelző).
- **Architektúra:**
  1. `app.json`: Manifest fájl (alkalmazás metaadatok, célhardver: `balance` / `480x480`, engedélyek).
  2. `app-side/index.js` (Side Service):
     - Futás a Zepp appban a telefonon.
     - Lekérdezi vagy WebSocketen figyeli a `http://127.0.0.1:8088/api/nav` végpontot.
     - Zepp BLE üzenetküldővel (`zml` vagy `device.send`) átküldi a csomagot az órára.
  3. `page/index.js` (Device App):
     - Kijelző kezelése:
       - Felső sáv: Kapcsolati státusz, akku, ETA.
       - Középső nagy zóna: Nagyméretű kanyarodási nyíl ikon + Távolság nagy számmal (pl. 250 m).
       - Alsó sáv: Utcanév / következő szakasz neve.
     - Haptika:
       - Forduló típusonként eltérő rezgésminták (bal = két rövid, jobb = egy hosszú, körforgalom = pulzálás).
       - Küszöbérték alapú rezgés (pl. 150 m-nél, majd 30 m-nél).
     - Képernyő ébrentartás a navigáció aktív ideje alatt.

---

## 4. Adatmodell (JSON Interfész)

```json
{
  "active": true,
  "action": "TURN_RIGHT",
  "directionText": "Forduljon jobbra",
  "distance": "150 m",
  "distanceMeters": 150,
  "street": "Kossuth Lajos utca",
  "eta": "14:25",
  "remainingDistance": "3.4 km",
  "remainingTime": "8 perc",
  "timestamp": 1728026400000
}
```
