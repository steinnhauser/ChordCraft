# ChordCraft - Android Setup Guide

A modern Kotlin Android application for learning piano without reading sheet music.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room (scaffolded, ready to implement)
- **Async**: Coroutines + Flow
- **Build System**: Gradle with Kotlin DSL

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 17**
   ```bash
   # Check Java version
   java -version
   # Should show version 17 or higher
   ```

2. **Android Studio** (Recommended: Hedgehog 2023.1.1 or later)
   - Download from: https://developer.android.com/studio
   - Includes Android SDK, Build-Tools, and Emulator

3. **Android SDK**
   - API Level 34 (Android 14) - for compilation
   - API Level 24 (Android 7.0) - minimum supported

### Environment Setup

Add these to your `~/.bashrc` or `~/.zshrc`:

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin
```

Reload your shell:
```bash
source ~/.bashrc  # or source ~/.zshrc
```

## Installation & Setup

### Option 1: Using Android Studio (Recommended for Development)

1. **Open the Project**
   ```bash
   cd /home/steinnhm/personal/projects/ChordCraft
   ```
   - Launch Android Studio
   - Select "Open" and navigate to the ChordCraft directory
   - Click OK

2. **Wait for Gradle Sync**
   - Android Studio will automatically sync Gradle dependencies
   - This may take a few minutes on first run
   - Watch the bottom status bar for progress

3. **Configure an Emulator** (if you don't have a physical device)
   - Click Tools → Device Manager
   - Click "Create Device"
   - Select a device (e.g., Pixel 6)
   - Choose a system image (API 34 recommended)
   - Finish setup

4. **Run the Application**
   - Click the green "Run" button (▶️) in the toolbar
   - Or press `Shift + F10`
   - Select your emulator or connected device
   - Wait for the app to build and install

### Option 2: Command Line Build

1. **Navigate to Project**
   ```bash
   cd /home/steinnhm/personal/projects/ChordCraft
   ```

2. **Make Gradle Wrapper Executable**
   ```bash
   chmod +x gradlew
   ```

3. **Build the Project**
   ```bash
   ./gradlew build
   ```

4. **Install on Device/Emulator**
   
   Make sure a device is connected or emulator is running:
   ```bash
   adb devices  # Should show at least one device
   ```
   
   Install the app:
   ```bash
   ./gradlew installDebug
   ```

5. **Generate APK**
   ```bash
   ./gradlew assembleDebug
   # APK will be in: app/build/outputs/apk/debug/app-debug.apk
   ```

## Running on a Physical Device

1. **Enable Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Developer Options will appear in Settings

2. **Enable USB Debugging**:
   - Settings → Developer Options → USB Debugging
   - Toggle it ON

3. **Connect via USB**:
   - Connect your device to your computer
   - Accept the USB debugging prompt on your device
   - Verify connection: `adb devices`

4. **Run the App**:
   ```bash
   ./gradlew installDebug
   ```

## Useful Gradle Commands

```bash
# Clean build artifacts
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Check for dependency updates
./gradlew dependencyUpdates

# List all available tasks
./gradlew tasks

# Build and install in one command
./gradlew installDebug

# View dependency tree
./gradlew :app:dependencies
```

## Project Structure

```
ChordCraft/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   ├── proguard-rules.pro        # ProGuard rules for release builds
│   └── src/main/
│       ├── AndroidManifest.xml   # App manifest
│       ├── java/com/chordcraft/
│       │   ├── ChordCraftApplication.kt  # Application class
│       │   ├── MainActivity.kt           # Main activity
│       │   ├── di/
│       │   │   └── AppModule.kt         # Hilt dependency injection
│       │   ├── ui/
│       │   │   ├── theme/               # Material 3 theme
│       │   │   │   ├── Color.kt
│       │   │   │   ├── Theme.kt
│       │   │   │   └── Type.kt
│       │   │   └── screens/
│       │   │       └── HomeScreen.kt    # Home screen UI
│       │   └── data/
│       │       └── models/
│       │           └── Song.kt          # Song data model
│       └── res/
│           └── values/
│               └── strings.xml          # String resources
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Gradle settings
├── gradle.properties             # Gradle properties
└── gradle/
    ├── libs.versions.toml        # Centralized dependency versions
    └── wrapper/
        └── gradle-wrapper.properties

```

## Troubleshooting

### Gradle Sync Failed

**Issue**: "Gradle sync failed: ..."

**Solutions**:
1. Check internet connection (Gradle needs to download dependencies)
2. In Android Studio: File → Invalidate Caches / Restart
3. Delete `.gradle` folder and retry:
   ```bash
   rm -rf .gradle
   ./gradlew clean build
   ```

### SDK Not Found

**Issue**: "Android SDK not found"

**Solutions**:
1. Verify `ANDROID_HOME` is set:
   ```bash
   echo $ANDROID_HOME
   ```
2. Install SDK via Android Studio: Tools → SDK Manager
3. Accept licenses:
   ```bash
   $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses
   ```

### Build Errors

**Issue**: Compilation errors or dependency conflicts

**Solutions**:
1. Clean and rebuild:
   ```bash
   ./gradlew clean build
   ```
2. Check JDK version (must be 17):
   ```bash
   java -version
   ```
3. Update Android Studio to the latest version
4. Sync Gradle files in Android Studio

### ADB Device Not Found

**Issue**: `adb devices` shows no devices

**Solutions**:
1. For physical device:
   - Check USB debugging is enabled
   - Try a different USB cable
   - Restart ADB: `adb kill-server && adb start-server`
2. For emulator:
   - Launch emulator from Android Studio Device Manager
   - Wait for full boot before installing

### App Crashes on Launch

**Issue**: App installs but crashes immediately

**Solutions**:
1. Check Logcat in Android Studio: View → Tool Windows → Logcat
2. Look for stack traces showing the error
3. Verify minimum SDK version compatibility (app requires Android 7.0+)

## Next Steps

Now that your project is set up, you can:

1. **Run the app** - See the basic home screen with ChordCraft branding
2. **Explore the code** - All files are thoroughly commented
3. **Add features**:
   - Implement MIDI file parsing
   - Create piano keyboard visualization
   - Build song library with Room database
   - Add audio playback functionality
   - Implement learning/practice modes

## Development Tips

1. **Enable Compose Preview**: In Android Studio, open any `@Composable` function and click the split view icon to see live previews

2. **Use Logcat**: View → Tool Windows → Logcat to see runtime logs

3. **Hot Reload**: When editing Compose UI, click the lightning bolt icon (⚡) for instant preview updates

4. **Debugging**: Set breakpoints by clicking the left gutter in code editor

5. **Code Navigation**: 
   - `Ctrl + Click` (Linux) to jump to definitions
   - `Ctrl + B` to go to declaration
   - `Alt + F7` to find usages

## Resources

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Material Design 3](https://m3.material.io/)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Database](https://developer.android.com/training/data-storage/room)

## Support

For issues specific to ChordCraft development:
1. Check this SETUP.md file
2. Review the inline code comments
3. Consult the [.github/agent.md](.github/agent.md) for project goals

For Android development questions:
- [Stack Overflow - Android](https://stackoverflow.com/questions/tagged/android)
- [Android Developers Community](https://developer.android.com/community)

---

**Happy Coding! 🎹**
