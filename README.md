# WalkNxt 🚶‍♂️

WalkNxt is a completely offline, privacy-first walking tracker for Android.

It is built for users who want to track their daily steps, walking duration, distance, pace, and calories burned without sacrificing their privacy to cloud servers.

## ✨ Features
- **100% Offline:** All your walking data stays securely on your device. Zero data collection.
- **Hardware Step Counter:** Uses the low-power Android hardware step counter for highly accurate step tracking with virtually zero battery drain.
- **Fitness Metrics:** Real-time calculation of active calories burned and walking pace.
- **Daily Goals:** Set your daily step goals and track your progress with a beautiful, custom-built semi-circle progress meter.
- **Clean UI:** A modern, distraction-free Material 3 user interface.
- **Export Your Data:** Export all your walks to a standard CSV file anytime.

## 🛠 Tech Stack
- **Language:** Kotlin
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Local Storage:** Room Database & DataStore Preferences
- **Concurrency:** Kotlin Coroutines & Flows
- **Background Tracking:** Android Foreground Services
- **Sensors:** `Sensor.TYPE_STEP_COUNTER` and Fused Location Provider (for distance when walking)

## 🚀 Building the App
1. Clone the repository: `git clone https://github.com/kushal-at/WalkNext.git`
2. Open the project in Android Studio (Jellyfish or newer recommended).
3. Build and run the app on your emulator or physical device.

## 🤝 Contributing
Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
