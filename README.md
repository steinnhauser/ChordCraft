# 🎹 ChordCraft

**A modern Android app for learning piano through visual, interactive playback**

ChordCraft transforms piano learning by eliminating the need to read traditional sheet music. Watch notes fall Guitar Hero-style onto a visual piano keyboard, making it easy and fun to learn your favorite songs and scales.

![ChordCraft](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

## ✨ Features

### 📚 Song Library
- **Template Songs**: Pre-built scales in multiple keys (C Major, G Major, D Major, A Major, F Major, A Minor, E Minor, D Minor, C Minor)
- **Song Management**: Add, view, and delete songs from your personal library
- **Persistent Storage**: All songs saved locally using Room database

### 🎮 Interactive Playback
- **Guitar Hero-Style Visualization**: Notes fall from the top and land on piano keys
- **Landscape Mode**: Automatic rotation for optimal viewing
- **Visual Piano**: Realistic keyboard with white and black keys
- **Active Key Highlighting**: Keys light up in purple when you should play them
- **Smart Display**: Shows only relevant keys plus padding for clean interface

### 🎛️ Playback Controls
- **Play/Pause**: Start and stop playback at any time
- **Restart**: Jump back to the beginning instantly
- **Seek Bar**: Scrub through songs with progress slider
- **Loop Mode**: Repeat songs for practice
- **Speed Control**: Adjust playback from 0.25x to 2.0x speed
- **Mute Toggle**: Silent practice mode

## 🏗️ Architecture

ChordCraft follows modern Android development best practices:

- **MVVM Architecture**: Clear separation of concerns
- **Jetpack Compose**: Declarative UI with Material Design 3
- **Hilt**: Dependency injection for clean, testable code
- **Room**: Local database for persistent storage
- **Coroutines + Flow**: Reactive, asynchronous programming
- **Navigation Compose**: Type-safe screen navigation

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin 2.0.0 |
| UI Framework | Jetpack Compose (BOM 2024.06.00) |
| Dependency Injection | Hilt 2.51.1 |
| Database | Room 2.6.1 |
| Async | Coroutines 1.8.1 + Flow |
| Navigation | Navigation Compose 2.7.7 |
| Build System | Gradle 8.4 with Kotlin DSL |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Compile SDK | 34 |

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Latest stable version (Hedgehog or later recommended)
- **JDK**: Java 17
- **Android SDK**: API Level 34
- **Gradle**: 8.4 (included via wrapper)

### Building the App

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd ChordCraft
   ```

2. **Open in Android Studio**:
   - File → Open → Select the ChordCraft directory
   - Wait for Gradle sync to complete

3. **Build and run**:
   ```bash
   ./gradlew installDebug
   ```
   
   Or use Android Studio's Run button (▶️)

### Running on Emulator

1. Create an Android Virtual Device (AVD):
   - Tools → Device Manager → Create Device
   - Recommended: Pixel 9 with API 34

2. Launch the emulator and click Run (▶️)

### Running on Physical Device

1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect via USB
4. Click Run (▶️) and select your device

## 📱 Usage

### Adding Songs

1. **Launch the app** and tap "My Library"
2. **Tap the + button** at the bottom
3. **Choose "Add from Template"**
4. **Select a scale** (e.g., C Major Scale)
5. Song is instantly added to your library!

### Playing Songs

1. **Open "My Library"**
2. **Tap on any song card** (look for the play icon ▶️)
3. **Phone automatically rotates to landscape**
4. **Tap the play button** to start playback
5. **Watch notes fall** and keys light up!

### Playback Controls

- **⏸/▶**: Pause or resume playback
- **↻**: Restart from beginning
- **Slider**: Seek to any position in the song
- **⚙️**: Open settings for loop and speed control
- **🔇/🔊**: Toggle mute (visual practice)
- **←**: Return to library

## 📂 Project Structure

```
ChordCraft/
├── app/
│   ├── build.gradle.kts           # App module build configuration
│   └── src/main/
│       ├── AndroidManifest.xml    # App manifest
│       ├── java/com/chordcraft/
│       │   ├── ChordCraftApplication.kt    # Application class (Hilt)
│       │   ├── MainActivity.kt              # Main entry point
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.kt      # Room database
│       │   │   │   ├── SongDao.kt          # Data Access Object
│       │   │   │   └── SongEntity.kt       # Database entity
│       │   │   ├── models/
│       │   │   │   ├── Note.kt             # Note data model
│       │   │   │   └── Song.kt             # Song data model
│       │   │   └── repository/
│       │   │       └── SongRepository.kt   # Repository pattern
│       │   ├── di/
│       │   │   └── AppModule.kt            # Hilt DI module
│       │   ├── ui/
│       │   │   ├── navigation/
│       │   │   │   └── Navigation.kt       # App navigation
│       │   │   ├── screens/
│       │   │   │   ├── HomeScreen.kt       # Landing screen
│       │   │   │   ├── LibraryScreen.kt    # Song library
│       │   │   │   └── PlaybackScreen.kt   # Playback visualization
│       │   │   ├── theme/
│       │   │   │   ├── Color.kt            # Material 3 colors
│       │   │   │   ├── Theme.kt            # App theme
│       │   │   │   └── Type.kt             # Typography
│       │   │   └── viewmodels/
│       │   │       ├── LibraryViewModel.kt  # Library state
│       │   │       └── PlaybackViewModel.kt # Playback state
│       │   └── utils/
│       │       └── JsonParser.kt           # JSON parsing utility
│       └── res/
│           ├── drawable/                    # Vector drawables
│           ├── mipmap-*/                    # App icons
│           └── values/
│               └── strings.xml              # String resources
├── gradle/
│   ├── libs.versions.toml          # Version catalog
│   └── wrapper/                    # Gradle wrapper
├── build.gradle.kts                # Root build file
├── settings.gradle.kts             # Gradle settings
├── gradlew                         # Gradle wrapper script (Unix)
├── gradlew.bat                     # Gradle wrapper script (Windows)
└── README.md                       # This file
```

## 🎨 Design

### Color Scheme

- **Primary Purple**: `#6750A4` - Used for active keys, falling notes, and accents
- **Background Dark**: `#1C1B1F` - Control bars and dialogs
- **Black**: `#000000` - Main playback background
- **White**: `#FFFFFF` - Piano white keys

### UI Components

- **Material Design 3**: Modern, accessible components
- **Custom Canvas Drawing**: High-performance note rendering at 60 FPS
- **Responsive Layout**: Adapts to different screen sizes
- **Dark Theme**: Optimized for focus during playback

## 🔮 Future Features

- [ ] **MIDI Audio Playback**: Real piano sounds using MIDI synthesis
- [ ] **JSON Import**: Load custom songs from files
- [ ] **Portrait Mode**: Support both orientations
- [ ] **Note Highlighting**: Show which note is currently playing
- [ ] **Practice Mode**: Scoring and feedback system
- [ ] **Speed Ramping**: Gradually increase speed for learning
- [ ] **Custom Song Creation**: In-app song editor
- [ ] **More Templates**: Additional scales, chords, and patterns
- [ ] **Export/Share**: Share songs with other users

## 🧪 Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## 🤝 Contributing

Contributions are welcome! Please see [.github/agent.md](.github/agent.md) for detailed development guidelines and architecture documentation.

### Quick Start for Contributors

1. Read [.github/agent.md](.github/agent.md) for architecture details
2. Fork the repository
3. Create a feature branch: `git checkout -b feature/amazing-feature`
4. Make your changes following the coding conventions
5. Commit with clear messages: `git commit -m 'Add amazing feature'`
6. Push to your fork: `git push origin feature/amazing-feature`
7. Open a Pull Request

## 📄 License

This project is open source. Please add your preferred license.

## 🙏 Acknowledgments

- Built with modern Android development practices
- Inspired by Guitar Hero and similar rhythm games
- Uses Material Design 3 components from Google

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Check existing issues and discussions

---

**Made with ❤️ using Kotlin and Jetpack Compose**
