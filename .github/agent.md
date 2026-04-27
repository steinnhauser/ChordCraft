# 🤖 ChordCraft Agent Development Guide

**Comprehensive documentation for AI agents and developers working on ChordCraft**

This document provides deep technical insights into the ChordCraft architecture, patterns, conventions, and development workflow to enable efficient collaboration and code contribution.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture Patterns](#architecture-patterns)
3. [Directory Structure](#directory-structure)
4. [Technology Stack](#technology-stack)
5. [Core Components](#core-components)
6. [Data Flow](#data-flow)
7. [Database Schema](#database-schema)
8. [UI Architecture](#ui-architecture)
9. [Coding Conventions](#coding-conventions)
10. [Build System](#build-system)
11. [Testing Strategy](#testing-strategy)
12. [Common Tasks](#common-tasks)
13. [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

### Purpose
ChordCraft is an Android application for learning piano through visual, interactive playback. It eliminates the need to read traditional sheet music by using a Guitar Hero-style falling note visualization.

### Core Features
- **Song Library**: Manage and browse songs with template creation
- **Visual Playback**: Guitar Hero-style falling notes onto a piano keyboard
- **Playback Controls**: Play/pause, restart, seek, loop, speed control
- **Landscape Mode**: Optimized horizontal viewing for playback
- **Persistent Storage**: Room database for local song storage

### Development Status
- ✅ Complete MVVM architecture
- ✅ Full song library with 9 scale templates
- ✅ Complete playback visualization (60 FPS canvas rendering)
- ✅ Playback controls and settings
- 🚧 MIDI audio playback (planned)
- 🚧 JSON file import (planned)

---

## 🏛️ Architecture Patterns

### MVVM (Model-View-ViewModel)

ChordCraft follows the MVVM pattern strictly:

**Model**:
- `SongEntity` (Room database entity)
- `Song` (domain model)
- `Note` (note data model)

**View**:
- Composable functions in `ui/screens/`
- All UI rendering happens here
- Observes ViewModel state via `collectAsStateWithLifecycle()`

**ViewModel**:
- `LibraryViewModel`: Manages library screen state and song templates
- `PlaybackViewModel`: Manages playback timing, state, and controls
- Exposes `StateFlow` for reactive UI updates
- Contains business logic and state management

### Repository Pattern

Data access is abstracted through repositories:

```kotlin
SongDao (Room) → SongRepository (abstraction) → ViewModel (consumer)
```

**Benefits**:
- Single source of truth
- Easy to swap data sources
- Testable without database

### Dependency Injection (Hilt)

All dependencies are injected using Hilt:

- `@HiltAndroidApp` on `ChordCraftApplication`
- `@AndroidEntryPoint` on `MainActivity` and screens with ViewModels
- `@HiltViewModel` on all ViewModels
- `@Inject` constructor injection
- `AppModule` provides singleton database and DAOs

### Unidirectional Data Flow

```
User Action → ViewModel (update state) → StateFlow emission → UI recomposition
```

State flows **down**, events flow **up**. ViewModels never reference Views.

---

## 📁 Directory Structure

### Complete Source Tree

```
app/src/main/java/com/chordcraft/
├── ChordCraftApplication.kt      # Application class, Hilt initialization
├── MainActivity.kt                # Main entry point, sets up navigation
│
├── data/                          # Data layer
│   ├── local/                     # Local data sources
│   │   ├── AppDatabase.kt         # Room database configuration
│   │   ├── SongDao.kt             # Data Access Object for songs
│   │   └── SongEntity.kt          # Database entity (table definition)
│   ├── models/                    # Data models
│   │   ├── Note.kt                # Note representation (parsed from JSON)
│   │   └── Song.kt                # Song domain model
│   ├── remote/                    # Remote data sources (empty, for future)
│   └── repository/                # Repository pattern
│       └── SongRepository.kt      # Abstracts data access
│
├── di/                            # Dependency Injection
│   └── AppModule.kt               # Hilt module providing dependencies
│
├── domain/                        # Domain layer (empty, for future use cases)
│   ├── models/                    # Domain models
│   └── usecases/                  # Business logic use cases
│
├── ui/                            # UI layer
│   ├── components/                # Reusable UI components (empty, for future)
│   ├── navigation/                # Navigation configuration
│   │   └── Navigation.kt          # NavHost setup with routes
│   ├── screens/                   # Screen-level composables
│   │   ├── HomeScreen.kt          # Landing screen
│   │   ├── LibraryScreen.kt       # Song library management
│   │   └── PlaybackScreen.kt      # Visual playback interface
│   ├── theme/                     # Material Design 3 theming
│   │   ├── Color.kt               # Color palette
│   │   ├── Theme.kt               # Theme configuration
│   │   └── Type.kt                # Typography
│   └── viewmodels/                # ViewModels
│       ├── LibraryViewModel.kt    # Library state management
│       └── PlaybackViewModel.kt   # Playback state and timing
│
└── utils/                         # Utility classes
    └── JsonParser.kt              # JSON note array parser
```

### Key Directories

- **`data/local/`**: Room database, DAOs, entities
- **`data/repository/`**: Repository interfaces and implementations
- **`di/`**: Hilt dependency injection modules
- **`ui/screens/`**: Screen-level composables
- **`ui/viewmodels/`**: State management and business logic
- **`ui/theme/`**: Material Design 3 theming
- **`utils/`**: Shared utility functions

---

## 🔧 Technology Stack

### Core Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin | 2.0.0 | Programming language |
| Android Gradle Plugin | 8.3.0 | Build system |
| Compose BOM | 2024.06.00 | UI framework |
| Hilt | 2.51.1 | Dependency injection |
| Room | 2.6.1 | Local database |
| Navigation Compose | 2.7.7 | Screen navigation |
| Coroutines | 1.8.1 | Async programming |
| Lifecycle ViewModel | 2.8.2 | ViewModel lifecycle |
| KSP | 2.0.0-1.0.21 | Annotation processing |

### Build Configuration

**Version Catalog** (`gradle/libs.versions.toml`):
- Centralized dependency management
- All versions defined in `[versions]` section
- Libraries defined in `[libraries]` section
- Plugins defined in `[plugins]` section

**Gradle Kotlin DSL**:
- Type-safe build scripts
- Better IDE support
- `build.gradle.kts` instead of `.gradle`

### SDK Requirements

- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java**: 17 (required for Kotlin 2.0)

---

## 🧩 Core Components

### 1. Database Layer (Room)

**SongEntity.kt** - Database table:
```kotlin
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val artist: String,
    val bpm: Int,
    val notesJson: String,      // JSON array of notes
    val duration: Double,        // Total duration in seconds
    val difficulty: String,      // "Easy", "Medium", "Hard"
    val dateAdded: Long         // Timestamp
)
```

**SongDao.kt** - Data access:
```kotlin
@Dao
interface SongDao {
    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongs(): Flow<List<SongEntity>>
    
    @Insert
    suspend fun insertSong(song: SongEntity): Long
    
    @Delete
    suspend fun deleteSong(song: SongEntity)
    
    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?
}
```

**AppDatabase.kt** - Database configuration:
```kotlin
@Database(entities = [SongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    companion object {
        const val DATABASE_NAME = "chordcraft_database"
    }
}
```

### 2. Repository Layer

**SongRepository.kt** - Single source of truth:
```kotlin
@Singleton
class SongRepository @Inject constructor(
    private val songDao: SongDao
) {
    fun getAllSongs(): Flow<List<SongEntity>> = songDao.getAllSongs()
    suspend fun addSong(song: SongEntity): Long = songDao.insertSong(song)
    suspend fun deleteSong(song: SongEntity) = songDao.deleteSong(song)
    suspend fun getSongById(id: Long): SongEntity? = songDao.getSongById(id)
}
```

### 3. ViewModel Layer

**LibraryViewModel.kt** - Library state:
```kotlin
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Loading)
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()
    
    // Sealed class for UI states
    sealed class LibraryUiState {
        object Loading : LibraryUiState()
        object Empty : LibraryUiState()
        data class Success(val songs: List<SongEntity>) : LibraryUiState()
        data class Error(val message: String) : LibraryUiState()
    }
    
    // Template songs (9 scales)
    enum class SongTemplate { C_MAJOR_SCALE, G_MAJOR_SCALE, ... }
}
```

**PlaybackViewModel.kt** - Playback timing:
```kotlin
@HiltViewModel
class PlaybackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SongRepository
) : ViewModel() {
    
    data class PlaybackState(
        val song: SongEntity? = null,
        val notes: List<Note> = emptyList(),
        val currentTime: Float = 0f,
        val isPlaying: Boolean = false,
        val isMuted: Boolean = false,
        val isLooping: Boolean = false,
        val playbackSpeed: Float = 1.0f,
        val activeNoteIndices: Set<Int> = emptySet()
    )
    
    companion object {
        const val NOTE_FALL_DURATION = 2.0f  // 2 seconds
        const val TIMER_TICK_MS = 16L        // ~60 FPS
    }
}
```

### 4. UI Layer (Compose)

**Navigation.kt** - Screen routing:
```kotlin
@Composable
fun ChordCraftNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onNavigateToLibrary = { navController.navigate("library") })
        }
        composable("library") {
            LibraryScreen(
                onBackClick = { navController.navigateUp() },
                onSongClick = { songId -> navController.navigate("playback/$songId") }
            )
        }
        composable("playback/{songId}") { backStackEntry ->
            val songId = backStackEntry.arguments?.getString("songId")?.toLongOrNull() ?: 0
            PlaybackScreen(
                songId = songId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}
```

**PlaybackScreen.kt** - Canvas rendering:
- 60 FPS playback timer (16ms ticks)
- Custom Canvas drawing for notes and piano
- Forces landscape orientation
- Purple theme (#6750A4)
- Note fall duration: 2 seconds
- Piano height: 15% of screen

---

## 🔄 Data Flow

### Song Creation Flow

```
User taps "+" → Bottom Sheet opens → User selects template
        ↓
LibraryViewModel.addSongFromTemplate(template)
        ↓
Generate scale notes (up then down pattern)
        ↓
Create SongEntity with notesJson
        ↓
SongRepository.addSong(song)
        ↓
SongDao.insertSong(song) → Room Database
        ↓
Flow<List<SongEntity>> emits new list
        ↓
UI recomposes with new song in list
```

### Playback Flow

```
User clicks song card → Navigate to "playback/{songId}"
        ↓
PlaybackViewModel loads song from repository
        ↓
Parse notesJson using JsonParser.parseNotes()
        ↓
User clicks play button
        ↓
startPlaybackTimer() launches coroutine
        ↓
Every 16ms: update currentTime, calculate activeNoteIndices
        ↓
StateFlow emits new PlaybackState
        ↓
PlaybackCanvas recomposes and draws at new positions
```

### State Observation Pattern

All screens follow this pattern:
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    when (state) {
        is UiState.Loading -> LoadingView()
        is UiState.Success -> ContentView(state.data)
        is UiState.Error -> ErrorView(state.message)
    }
}
```

---

## 💾 Database Schema

### Songs Table

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | Unique song ID |
| title | TEXT | NOT NULL | Song name |
| artist | TEXT | NOT NULL | Composer/artist |
| bpm | INTEGER | NOT NULL | Beats per minute |
| notesJson | TEXT | NOT NULL | JSON array of notes |
| duration | REAL | NOT NULL | Total duration in seconds |
| difficulty | TEXT | NOT NULL | "Easy", "Medium", or "Hard" |
| dateAdded | INTEGER | NOT NULL | Unix timestamp |

### Notes JSON Format

```json
[
  {
    "note": "C4",
    "time": 0.0,
    "duration": 1.0
  },
  {
    "note": "D4",
    "time": 2.0,
    "duration": 1.0
  }
]
```

**Note Fields**:
- `note`: String (e.g., "C4", "G#5", "Bb3")
- `time`: Double (seconds from start)
- `duration`: Double (seconds)

---

## 🎨 UI Architecture

### Compose Best Practices Used

1. **State Hoisting**: State lifted to ViewModels, events passed down
2. **Separation of Concerns**: Each composable has a single responsibility
3. **Reusability**: Extract common UI patterns into components
4. **Performance**: `remember`, `derivedStateOf`, `LaunchedEffect` used correctly
5. **Material 3**: Consistent design system throughout

### Screen Composables

**HomeScreen.kt**:
- Simple landing screen
- Single "My Library" button
- Callback: `onNavigateToLibrary`

**LibraryScreen.kt**:
- States: Loading, Empty, Success, Error
- Bottom sheets: AddSongMethodSheet, TemplateSelectionSheet
- SongItem cards with delete functionality
- FAB for adding songs
- Callback: `onSongClick(songId)`

**PlaybackScreen.kt**:
- Forces landscape via DisposableEffect
- Custom Canvas rendering at 60 FPS
- TopControlsBar: back, mute, settings
- PlaybackCanvas: falling notes + piano
- BottomControls: play/pause, restart, seek
- SettingsDialog: loop toggle, speed slider

### Theme System

**Color.kt** - Material 3 colors:
```kotlin
val PrimaryLight = Color(0xFF6750A4)  // Main purple
val SecondaryLight = Color(0xFF625B71)
// ... full light and dark palettes
```

**Theme.kt** - Dynamic theming:
- Supports light and dark modes
- Dynamic colors on Android 12+
- Fallback to custom purple theme

**Type.kt** - Typography scale using default Material 3 typography

### Canvas Rendering (PlaybackScreen)

**Piano Keyboard**:
- White keys: Full height rectangles
- Black keys: 60% width, 60% height, overlaid
- Active keys: Change to purple (#6750A4)
- Border: 2px black stroke

**Falling Notes**:
- Calculate position based on time until note should be played
- Fall over 2 seconds (NOTE_FALL_DURATION)
- Height represents note duration
- Purple rectangles (#6750A4)
- Only draw if within visible range

**Performance**:
- 60 FPS (16ms per frame)
- Canvas invalidation only on state change
- Efficient drawing with simple primitives

---

## 📝 Coding Conventions

### Kotlin Style

Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

**Naming**:
- Classes: `PascalCase`
- Functions: `camelCase`
- Constants: `UPPER_SNAKE_CASE`
- Private properties: `_camelCase` for backing properties

**Formatting**:
- 4-space indentation
- Maximum line length: 120 characters
- Braces on same line for classes/functions

**Nullability**:
- Use `?` for nullable types
- Prefer `?.`, `?:`, `let`, `apply` over explicit null checks
- Avoid `!!` unless absolutely necessary

**Immutability**:
- Prefer `val` over `var`
- Use immutable collections where possible
- Data classes for immutable data

**Scope Functions**:
- `let`: Transform nullable, scope isolation
- `apply`: Object configuration
- `with`: Operate on object without returning it
- `run`: Execute block and return result
- `also`: Side effects

### Compose Conventions

**Composable Naming**:
- Always start with capital letter
- Describe what it displays: `SongItem`, `PlaybackCanvas`

**Parameters**:
- `modifier: Modifier = Modifier` as last parameter
- State before callbacks
- Required params first, optional last

**State Management**:
```kotlin
// ViewModel
private val _state = MutableStateFlow(initialValue)
val state: StateFlow<Type> = _state.asStateFlow()

// Composable
val state by viewModel.state.collectAsStateWithLifecycle()
```

**Side Effects**:
- `LaunchedEffect`: Coroutines tied to composition
- `DisposableEffect`: Cleanup required (e.g., orientation)
- `SideEffect`: Publish to non-Compose state

### Documentation

**KDoc for public APIs**:
```kotlin
/**
 * Brief description of what the function does.
 * 
 * More detailed explanation if needed.
 * 
 * @param parameter Description of parameter
 * @return Description of return value
 */
fun myFunction(parameter: Type): ReturnType
```

**Inline comments**:
- Explain "why", not "what"
- Keep them minimal
- Update when code changes

---

## 🔨 Build System

### Gradle Configuration

**Root `build.gradle.kts`**:
```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

**App `build.gradle.kts`**:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.chordcraft"
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
        targetSdk = 34
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

### Version Catalog (`gradle/libs.versions.toml`)

Centralized dependency management:
```toml
[versions]
kotlin = "2.0.0"
agp = "8.3.0"
composeBom = "2024.06.00"
hilt = "2.51.1"
room = "2.6.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
# ... all dependencies

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
# ... all plugins
```

### Common Gradle Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build and install debug APK
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check dependencies
./gradlew dependencies

# Sync project
./gradlew sync
```

---

## 🧪 Testing Strategy

### Unit Tests (JUnit 5 + MockK)

**ViewModel Tests**:
```kotlin
class LibraryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private lateinit var repository: SongRepository
    private lateinit var viewModel: LibraryViewModel
    
    @Before
    fun setup() {
        repository = mockk()
        viewModel = LibraryViewModel(repository)
    }
    
    @Test
    fun `addSongFromTemplate creates correct song`() {
        // Test implementation
    }
}
```

**Repository Tests**:
- Use fake DAO implementations
- Test Flow emissions
- Verify database operations

### UI Tests (Compose Testing)

```kotlin
class LibraryScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun emptyState_displaysMessage() {
        composeTestRule.setContent {
            LibraryScreen(
                viewModel = LibraryViewModel(fakeRepository)
            )
        }
        
        composeTestRule.onNodeWithText("No songs yet")
            .assertIsDisplayed()
    }
}
```

### Test Organization

```
app/src/
├── test/                  # Unit tests
│   └── java/com/chordcraft/
│       ├── viewmodels/
│       └── repository/
└── androidTest/           # Instrumented tests
    └── java/com/chordcraft/
        └── ui/
```

---

## 🔧 Common Tasks

### Adding a New Screen

1. **Create composable** in `ui/screens/YourScreen.kt`
2. **Create ViewModel** in `ui/viewmodels/YourViewModel.kt`
3. **Add route** to `Navigation.kt`
4. **Add navigation call** from previous screen

### Adding a New Database Entity

1. **Create entity** in `data/local/YourEntity.kt`
2. **Create DAO** in `data/local/YourDao.kt`
3. **Update database** in `AppDatabase.kt` (increment version!)
4. **Add to Hilt module** in `di/AppModule.kt`
5. **Create repository** in `data/repository/YourRepository.kt`

### Adding a New Dependency

1. **Open** `gradle/libs.versions.toml`
2. **Add version** in `[versions]` section
3. **Add library** in `[libraries]` section
4. **Reference in** `app/build.gradle.kts` using `libs.library.name`
5. **Sync gradle**

### Modifying Playback Visualization

**Key locations**:
- Note fall duration: `PlaybackViewModel.NOTE_FALL_DURATION`
- Timer tick rate: `PlaybackViewModel.TIMER_TICK_MS`
- Piano rendering: `PlaybackScreen.PlaybackCanvas`
- Colors: `ui/theme/Color.kt`

---

## 🐛 Troubleshooting

### Common Issues

**Build Errors**:
```bash
# Clean and rebuild
./gradlew clean build

# Check Java version (must be 17)
java -version

# Invalidate caches (Android Studio)
File → Invalidate Caches → Invalidate and Restart
```

**Hilt Errors**:
- Ensure `@HiltAndroidApp` on Application class
- Ensure `@AndroidEntryPoint` on Activity/Fragment
- Ensure `@HiltViewModel` on ViewModels
- Check KSP is applied: `alias(libs.plugins.ksp)`

**Room Errors**:
- Increment database version when schema changes
- Add migration or set `fallbackToDestructiveMigration()`
- Ensure all queries are valid SQL

**Compose Errors**:
- Remember to collect State as `val state by viewModel.state.collectAsStateWithLifecycle()`
- Use `remember` for computed values
- Check modifier chains

### Debug Tips

**Logging**:
```kotlin
import android.util.Log

private const val TAG = "YourClass"
Log.d(TAG, "Debug message")
Log.e(TAG, "Error message", exception)
```

**Compose Layout Inspector**:
- Tools → Layout Inspector
- View composable tree
- Inspect recomposition counts

**Database Inspector**:
- View → Tool Windows → App Inspection → Database Inspector
- Inspect Room database live

**Logcat**:
- View → Tool Windows → Logcat
- Filter by tag, process, or message

---

## 🎯 Development Workflow

### Feature Development Checklist

- [ ] Create data models if needed
- [ ] Create/update Room entities and DAOs
- [ ] Create/update repository
- [ ] Create ViewModel with state management
- [ ] Create composable UI
- [ ] Add navigation route
- [ ] Wire up dependencies in Hilt
- [ ] Add unit tests
- [ ] Add UI tests
- [ ] Update documentation

### Code Review Checklist

- [ ] Follows MVVM pattern
- [ ] Uses Hilt for DI
- [ ] ViewModels expose StateFlow
- [ ] Composables are stateless (state hoisted)
- [ ] Proper error handling
- [ ] No memory leaks (lifecycle-aware)
- [ ] Performance optimized (remember, derivedStateOf)
- [ ] Accessible (content descriptions)
- [ ] Documented (KDoc for public APIs)
- [ ] Tested (unit + UI tests)

---

## 📚 Resources

### Documentation
- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room](https://developer.android.com/training/data-storage/room)

### Tools
- [Android Studio](https://developer.android.com/studio)
- [Gradle](https://gradle.org/)
- [Material Design 3](https://m3.material.io/)

### Community
- [Kotlin Slack](https://kotlinlang.slack.com/)
- [Android Dev Reddit](https://www.reddit.com/r/androiddev/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/android)

---

## 🤝 Contributing

### Getting Started

1. **Read this document thoroughly**
2. **Set up development environment**
3. **Build and run the app**
4. **Explore the codebase**
5. **Pick an issue or feature**
6. **Create a feature branch**
7. **Make changes following conventions**
8. **Test thoroughly**
9. **Submit pull request**

### Pull Request Guidelines

- Clear, descriptive title
- Description of changes and why
- Link to related issue
- Screenshots for UI changes
- Tests added/updated
- Documentation updated
- No merge conflicts

---

**Last Updated**: April 27, 2026  
**Version**: 1.0.0  
**Maintainers**: ChordCraft Development Team

For questions or clarifications, open an issue or discussion on GitHub.
