<div align="center">

# 🚶 WalkNxt

**A privacy-first, 100% offline walking tracker for Android.**

No cloud. No ads. No data collection. Just you and your walk.

[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-blue.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-orange.svg)](https://developer.android.com)
[![Latest Release](https://img.shields.io/github/v/release/kushal-at/WalkNext?label=Download)](https://github.com/kushal-at/WalkNext/releases/latest)

</div>

---

## 📥 Download & Install

> **You do not need to compile the app yourself.** Pre-built APKs are available in the Releases section.

**[👉 Download the latest release here](https://github.com/kushal-at/WalkNext/releases/latest)**

### Installation Steps

1. **Download** `Release.apk` from the [Releases page](https://github.com/kushal-at/WalkNext/releases/latest).
2. **Transfer** the APK to your Android device (via USB, email, or cloud storage).
3. On your device, go to **Settings → Apps → Special Access → Install Unknown Apps** and allow your file manager or browser.
4. **Open** the downloaded APK file and tap **Install**.
5. That's it — **WalkNxt is now ready to use, completely offline.**

> **Minimum Requirement:** Android 8.0 (API 26) or higher.

---

## ✨ Features

| Feature | Details |
|---|---|
| 🏃 **Step Counter** | Hardware-based step sensor — accurate and battery-efficient |
| 🎯 **Daily Step Goals** | Set your target and track live progress with a semi-circle meter |
| 🔥 **Calories Burned** | Automatic calorie estimation based on your walk |
| ⏱️ **Pace Tracking** | Real-time and average pace in min/km or min/mi |
| 📏 **Distance** | GPS-fused distance measurement with sensor fallback |
| 📊 **Walk History** | Full history of all your past sessions |
| 📤 **Data Export** | Export all walks to a standard CSV file |
| 🌗 **Theme Support** | System, Light, and Dark mode |
| 🔒 **100% Offline** | Zero network requests, ever. All data lives on your device |
| 🗑️ **Delete History** | Full control — wipe your data anytime from Settings |

---

## 🔒 Privacy

**WalkNxt does not collect, transmit, or share any of your personal data.**

- No user accounts, no login
- No analytics or crash reporting
- No internet permission needed for core functionality
- All data is stored locally in a private Room database on your device

Read the full [Privacy Policy](PRIVACY_POLICY.md).

---

## 📸 Screenshots

*Coming soon in a future release.*

---

## 🛠 Building from Source

If you prefer to compile the app yourself:

**Prerequisites:**
- Android Studio (Jellyfish 2023.3.1 or newer)
- Android SDK (API 34)
- JDK 17

**Steps:**
```bash
git clone https://github.com/kushal-at/WalkNext.git
cd WalkNext
```
Then open the project in **Android Studio** and click **Run**.

Alternatively, source archives are available on the [Releases page](https://github.com/kushal-at/WalkNext/releases).

---

## 🧱 Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM + Clean Architecture |
| Local DB | Room (SQLite) |
| Preferences | SharedPreferences |
| Concurrency | Kotlin Coroutines & Flows |
| Background | Android Foreground Service |
| Sensors | `Sensor.TYPE_STEP_COUNTER`, Fused Location Provider |

---

## 🤝 Contributing

Contributions, issues and feature requests are welcome!

1. Fork the repository
2. Create your branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'feat: add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a **Pull Request**

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">
Made with ❤️ for privacy-conscious walkers everywhere.
</div>
