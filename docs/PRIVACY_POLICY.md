# Privacy Policy for MapyNav

**Last updated:** October 4, 2026

This Privacy Policy applies to the **MapyNav** application suite, which includes the **MapyNav Companion Android App** and the **MapyNav Zepp OS Smartwatch App** (collectively, "the Application").

---

### 1. Overview and Core Principle

Your privacy is paramount. **MapyNav does not collect, store, transmit, or share any personal data, location data, identity information, or usage analytics with any external servers or third parties.**

The Application operates as an entirely local, offline bridge designed to display turn-by-turn navigation instructions from **Mapy.com** (Mapy.cz) on your paired **Amazfit** smartwatch.

---

### 2. Information Handled by the Application

The Application processes the following transient operational data strictly in memory on your local devices:

* **Navigation Information:** Turn maneuver symbols (e.g., turn left, roundabout, arrive), remaining distance to maneuver, street names, and estimated time of arrival (ETA) extracted locally from active Mapy.com Android system notifications.
* **Device Telemetry:** Smartwatch screen resolution and battery level for layout scaling and power optimization.

**All navigation data is processed strictly in real-time in memory and is never logged or persisted to disk or cloud storage.** Once navigation stops, all data is immediately discarded.

---

### 3. Local Communication Architecture

* The Android companion app runs an internal local HTTP server bound exclusively to `127.0.0.1` (localhost).
* Data transfer between the Android companion app and the Amazfit smartwatch occurs solely over the secure, locally paired Bluetooth link via the official Zepp OS bridge API.
* **No external network requests or internet access are used for navigation data transmission.**

---

### 4. Permissions Requested and Justification

#### Android Companion Application:
* **Notification Listener Access (`BIND_NOTIFICATION_LISTENER_SERVICE`):** Required exclusively to detect and parse active navigation prompts posted by the Mapy.cz / Mapy.com application. Notifications from all other applications are strictly ignored and discarded.
* **Foreground Service (`FOREGROUND_SERVICE_SPECIAL_USE`):** Required to prevent Android from prematurely terminating the local server during extended navigation trips when the phone screen is locked.
* **Battery Optimization Exemption:** Recommended to ensure continuous haptic alerts and prompt turn updates during background execution.

#### Zepp OS Smartwatch Application:
* **Vibration Sensor (`device:os.vibrate`):** Required to provide tactile, directional vibration patterns (distinct haptic feedback for left, right, roundabout, and arrival).
* **Display Wake Management (`device:os.bg_service`):** Required to wake and keep the display active when turn-by-turn events occur.
* **Device Information (`data:os.device.info`):** Required to detect screen resolution and adapt UI layout across different Amazfit smartwatch models.

---

### 5. Third-Party Services and Analytics

* The Application contains **no advertising SDKs, tracking pixels, or third-party analytics libraries**.
* The Application is not affiliated with, sponsored by, or endorsed by Seznam.cz, a.s. or Zepp Health. All trademarks belong to their respective owners.

---

### 6. Children's Privacy

The Application does not collect or solicit any information from anyone under the age of 13 or any other age group.

---

### 7. Changes to This Privacy Policy

If this Privacy Policy is updated, the revised version will be published on the Application's official repository at:  
[https://github.com/kaszast/mapynav-amazfit](https://github.com/kaszast/mapynav-amazfit)

---

### 8. Contact Information

For any inquiries or questions regarding this Privacy Policy, please contact:

* **Developer:** kaszast
* **Email:** kaszast@gmail.com
* **Project Repository:** [https://github.com/kaszast/mapynav-amazfit](https://github.com/kaszast/mapynav-amazfit)
