# LocalMusic — Android Jetpack Compose UI Concept & Prototype

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-purple.svg)](https://developer.android.com/jetpack/compose)
[![GitHub Pages](https://img.shields.io/badge/Live%20Showcase-GitHub%20Pages-blue.svg)](https://northcrx.github.io/localmusic/)

An expressive, modern Android music player UI concept crafted with **Jetpack Compose**, **Material 3 Expressive Design**, and custom Compose Canvas animations.

> [!IMPORTANT]
> ### 🎨 Design Concept & UI Prototype Notice
> **Please note:** This repository represents a **UI/UX design architecture and prototype** of the application. It is currently in the design & UI building phase and is **not an actual local media player engine** yet. In upcoming development phases, local storage audio decoding and media indexing (via Jetpack Media3 / ExoPlayer) will be integrated into this design.

---

## 🌐 Live GitHub Pages Showcase
View and interact with the live phone prototype directly in your browser:  
👉 **[https://northcrx.github.io/localmusic/](https://northcrx.github.io/localmusic/)**

---

## ✨ Design Highlights

- 💽 **Photorealistic Vinyl Turntable**:
  - Full-screen modal bottom sheet featuring a canvas-drawn vinyl disc with concentric groove rings.
  - Realistic tonearm needle angled over the disc.
  - Smooth spinning disc animation during playback.
  - Complete playback controls: shuffle, previous, elevated play/pause, next, and repeat.
  - Bottom actions for *Lyrics* and *Up next (12)*.

- 🎵 **Floating Progress Ring Mini-Player**:
  - Floating pill container docked cleanly above the bottom navigation bar.
  - Circular Canvas progress ring arc surrounding an embedded play/pause trigger.
  - Headphone avatar badge and dynamic track details.
  - Tap-to-expand transition directly into the Vinyl Player.

- 🔀 **2x2 Quick Shuffle Cards**:
  - Interactive grid on the Home screen.
  - Top-left **Shuffle Play** tile with a dedicated shuffle action button that randomizes the track queue on tap.
  - Quick-access song tiles for instant music playback.

- 🔍 **Unified Search & Explore**:
  - Immediate routing to the search interface with real-time text query filtering.
  - Filter chips (*All*, *Songs*, *Album*, *Artist*) and instant track selection.

- 🎨 **Material 3 Expressive Dark Theme**:
  - Deep dark aesthetic with smooth rounded surfaces and contrast-balanced text tokens.
  - Clean edge-to-edge layout with zero double-inset clipping.

---

## 📦 Download Prebuilt APK

You can download the compiled debug APK directly to test the UI on an Android device or emulator:
- **Download:** [`app-debug.apk`](docs/app-debug.apk) (also accessible via GitHub Pages)

---

## 🛠 Tech Stack & Requirements

- **Language:** Kotlin 2.0.21
- **UI Toolkit:** Jetpack Compose (BOM 2024.09.00) + Material 3
- **Navigation:** Jetpack Navigation Compose 2.8.3
- **Build System:** Gradle 9.8.2 / Android Gradle Plugin 8.8.0
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 34 (Android 14)

---

## 🚀 Building & Running Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/northcrx/localmusic.git
   cd localmusic
   ```

2. **Build the debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Output location:**
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🗺 Roadmap (Future Phases)

- [ ] Jetpack Media3 / ExoPlayer audio playback service
- [ ] Android MediaStore local audio file scanner & permissions
- [ ] Equalizer audio session effects
- [ ] Embedded ID3 tag cover art extractor
- [ ] Real-time synchronized lyrics renderer

---

## 📄 License
This design showcase is open-sourced under the MIT License.
