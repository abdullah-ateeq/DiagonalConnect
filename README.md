# Diagonal Connect

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.2.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![API](https://img.shields.io/badge/Min%20SDK-24-brightgreen)](https://developer.android.com/about/dashboards)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-37-blue)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

> **Think diagonally. Build the path.**

**Diagonal Connect** is a refined, local 2-player connection strategy game built for Android using modern **Jetpack Compose** and **Material 3**. 

Unlike classic connection games where pieces connect orthogonally (up/down/left/right), in Diagonal Connect, pieces **connect exclusively through their diagonal neighbors** (corner-to-corner). Orthogonally adjacent pieces side-by-side or stacked vertically do not link!

---

## 🎮 Gameplay & Rules

| Player | Color & Symbol | Start Edge (Rail) | Target Edge (Rail) |
| :--- | :--- | :--- | :--- |
| **Player 1** | 🟡 Gold / Amber Diamond | **Top** | **Bottom** |
| **Player 2** | 🔵 Teal / Cyan Square | **Left** | **Right** |

### Core Rules
1. **Turn-Based Placement:** Players take turns placing one piece on any empty cell of the $8 \times 8$ grid.
2. **Permanent Placements:** Placed pieces are locked in position and cannot be moved or removed.
3. **Diagonal-Only Connectivity:** Two pieces belong to the same connected network **only if they touch diagonally** (corner-to-corner). Side-by-side or top-to-bottom touching pieces never connect.
4. **Winning Condition:** The first player to form an unbroken, continuous diagonal path connecting their starting rail to their target rail wins immediately!
5. **Draw:** If the board becomes completely full with no player connecting their target rails, the game ends in a draw.

---

## ✨ Features

- **📱 Local 2-Player Pass & Play:** Seamless turn-based local multiplayer on a single device with portrait and landscape orientation support.
- **🎨 Modern Material 3 UI & Custom Graphics:** Crisp, responsive board drawing with custom Canvas animations for piece placements, winning path highlights, target edge rails, and last move rings.
- **🎵 Built-In Synthesized Audio:** Uses a lightweight custom additive sound synthesizer (`ToneSynth`) to generate clean 16-bit PCM audio effects (piece placement, turn switches, victory fanfares) on the fly without external media assets.
- **📳 Tactile Haptic Feedback:** Integrated device haptics for interactive moves and match conclusions.
- **📊 Statistics & Match Records:** Keeps track of total games played, Player 1 wins, Player 2 wins, draws, and shortest winning path records using **Jetpack DataStore Preferences**.
- **⚙️ Customization & Themes:** Toggle between Light, Dark, or System UI themes, as well as Classic and Midnight board color palettes. Option to toggle last move indicators, edge highlights, sound, and haptics.
- **♿ Accessibility & Safety:** Full TalkBack support with explicit semantic node annotations, live region announcements, match undo support, and animation input locking to prevent double-tap misclicks.

---

## 📂 Project Architecture

The project adheres to modern Android architecture principles with Unidirectional Data Flow (UDF), Kotlin Coroutines, and Jetpack Compose:

```
com.example.diagonalconnect/
├── audio/
│   ├── SoundPlayer.kt        # Manages game sound playback
│   └── ToneSynth.kt          # 16-bit PCM additive wave synthesizer
├── data/
│   ├── SettingsRepository.kt # DataStore persistence for settings & game stats
│   └── Models                # AppSettings, GameStats, Theme Enums
├── game/
│   ├── DiagonalConnectGame.kt # Match state controller (undo, turn locking, animation)
│   ├── DiagonalRules.kt       # Pure BFS pathfinding algorithm & board rules
│   └── GameModels.kt          # Immutable state classes (Board, GridPosition, Player)
└── ui/
    ├── components/
    │   ├── BoardDrawing.kt    # Custom Canvas rendering for pieces and paths
    │   ├── DiagonalBoard.kt   # Interactive board UI with touch handling
    │   ├── MiniBoard.kt       # Lightweight board previews for menus & rules
    │   └── CommonComponents.kt# Buttons, headers, emblems, stat blocks
    ├── screens/
    │   ├── HomeScreen.kt      # Main menu & statistics overview
    │   ├── GameScreen.kt      # Play screen with turn indicators & controls
    │   ├── ResultScreen.kt    # Victory/Draw summary screen
    │   ├── HowToPlayScreen.kt # Interactive rule guide with visual diagrams
    │   ├── SettingsScreen.kt  # Theme & feedback options
    │   ├── AboutScreen.kt     # App information & feature list
    │   └── SplashScreen.kt   # Animated splash entry
    ├── GameViewModel.kt       # Game UI state & user interactions
    ├── SettingsViewModel.kt   # Preferences management ViewModel
    └── theme/                 # Material 3 Color palettes, typography & board styling
```

---

## 🛠️ Tech Stack & Dependencies

- **Language:** [Kotlin 2.2.10](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Compose BOM `2026.02.01`, Material 3)
- **Architecture:** ViewModel, StateFlow, Coroutines, UDF
- **Navigation:** Jetpack Navigation Compose (`2.10.1`)
- **Data Persistence:** Jetpack DataStore Preferences (`1.2.1`)
- **Build System:** Gradle (`AGP 9.4.1`) & Kotlin Compose Compiler Plugin

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** Ladybug (2024.2.1) or newer
- **JDK** 11 or higher
- **Android SDK** API 35 or 37
- **Device / Emulator:** Android 7.0 (API level 24) or higher

### Installation & Build

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/abdullah-ateeq/DiagonalConnect.git
   cd DiagonalConnect
   ```

2. **Build the Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests:**
   ```bash
   ./gradlew test
   ```

4. **Install on Connected Device / Emulator:**
   ```bash
   ./gradlew installDebug
   ```

---

## 🧪 Testing

The core game logic and BFS pathfinding rules are thoroughly covered by pure Kotlin unit tests:
- `DiagonalRulesTest`: Verifies diagonal neighbor detection, orthogonal disconnection, BFS path reconstruction, edge goal matching, and draw conditions.
- `DiagonalConnectGameTest`: Tests game state transitions, turn switching, move history, animation locking, and undo functionality.

Run the test suite via Gradle:
```bash
./gradlew testDebugUnitTest
```

---

## 📄 License

```text
MIT License

Copyright (x) 2026 Abdullah Ateeq

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
