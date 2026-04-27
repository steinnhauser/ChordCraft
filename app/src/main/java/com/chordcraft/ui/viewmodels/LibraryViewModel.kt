package com.chordcraft.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chordcraft.data.local.SongEntity
import com.chordcraft.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Library screen.
 * 
 * This sealed class represents all possible states the library can be in.
 * Using sealed classes ensures we handle all cases when consuming the state.
 */
sealed class LibraryUiState {
    /** Initial state while data is loading */
    object Loading : LibraryUiState()
    
    /** No songs in the library */
    object Empty : LibraryUiState()
    
    /** Songs are available in the library */
    data class Success(val songs: List<SongEntity>) : LibraryUiState()
    
    /** An error occurred */
    data class Error(val message: String) : LibraryUiState()
}

/**
 * Available song templates that users can choose from.
 * 
 * Each template generates a pre-configured song with specific note patterns.
 * All scales go up and then back down.
 */
enum class SongTemplate(val displayName: String, val description: String) {
    // Major Scales
    C_MAJOR_SCALE("C Major Scale", "C D E F G A B - No sharps or flats"),
    G_MAJOR_SCALE("G Major Scale", "G A B C D E F# - One sharp"),
    D_MAJOR_SCALE("D Major Scale", "D E F# G A B C# - Two sharps"),
    A_MAJOR_SCALE("A Major Scale", "A B C# D E F# G# - Three sharps"),
    F_MAJOR_SCALE("F Major Scale", "F G A Bb C D E - One flat"),
    
    // Minor Scales (Natural Minor)
    A_MINOR_SCALE("A Minor Scale", "A B C D E F G - Relative to C Major"),
    E_MINOR_SCALE("E Minor Scale", "E F# G A B C D - Relative to G Major"),
    D_MINOR_SCALE("D Minor Scale", "D E F G A Bb C - Relative to F Major"),
    C_MINOR_SCALE("C Minor Scale", "C D Eb F G Ab Bb - Three flats"),
}

/**
 * ViewModel for the Library screen.
 * 
 * Manages the state and business logic for displaying and managing songs.
 * Follows MVVM pattern: View observes uiState, calls methods on ViewModel,
 * ViewModel updates state based on repository data.
 * 
 * @HiltViewModel enables Hilt to inject this ViewModel into composables.
 * @property repository Song repository (injected by Hilt)
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: SongRepository
) : ViewModel() {
    
    /**
     * UI state as a StateFlow.
     * 
     * The UI collects this flow to update when state changes.
     * StateFlow always has a value (unlike regular Flow) and is hot
     * (keeps running even without collectors).
     * 
     * We use stateIn to convert the repository's Flow<List<SongEntity>>
     * into a StateFlow<LibraryUiState>.
     */
    val uiState: StateFlow<LibraryUiState> = repository.getAllSongs()
        .map { songs ->
            // Transform the list of songs into appropriate UI state
            when {
                songs.isEmpty() -> LibraryUiState.Empty
                else -> LibraryUiState.Success(songs)
            }
        }
        .catch { exception ->
            // If an error occurs in the flow, emit Error state
            emit(LibraryUiState.Error(exception.message ?: "Unknown error occurred"))
        }
        .stateIn(
            scope = viewModelScope,  // Tied to ViewModel lifecycle
            started = SharingStarted.WhileSubscribed(5000),  // Keep active for 5s after last subscriber
            initialValue = LibraryUiState.Loading  // Initial state while loading
        )
    
    /**
     * Delete a song from the library.
     * 
     * Launched in viewModelScope so it's cancelled if ViewModel is destroyed.
     * Uses Dispatchers.IO (from repository) for database operations.
     * 
     * @param song The song to delete
     */
    fun deleteSong(song: SongEntity) {
        viewModelScope.launch {
            try {
                repository.deleteSong(song)
                // No need to update UI state manually - the Flow will emit new data
            } catch (e: Exception) {
                // In a production app, you'd emit this error to a SharedFlow
                // or use a separate error handling mechanism
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Add a song from a template.
     * 
     * @param template The template to use for generating the song
     */
    fun addSongFromTemplate(template: SongTemplate) {
        viewModelScope.launch {
            try {
                val song = when (template) {
                    // Major scales
                    SongTemplate.C_MAJOR_SCALE -> generateScale(
                        "C Major Scale",
                        listOf("C4", "D4", "E4", "F4", "G4", "A4", "B4")
                    )
                    SongTemplate.G_MAJOR_SCALE -> generateScale(
                        "G Major Scale",
                        listOf("G4", "A4", "B4", "C5", "D5", "E5", "F#5")
                    )
                    SongTemplate.D_MAJOR_SCALE -> generateScale(
                        "D Major Scale",
                        listOf("D4", "E4", "F#4", "G4", "A4", "B4", "C#5")
                    )
                    SongTemplate.A_MAJOR_SCALE -> generateScale(
                        "A Major Scale",
                        listOf("A4", "B4", "C#5", "D5", "E5", "F#5", "G#5")
                    )
                    SongTemplate.F_MAJOR_SCALE -> generateScale(
                        "F Major Scale",
                        listOf("F4", "G4", "A4", "Bb4", "C5", "D5", "E5")
                    )
                    
                    // Minor scales
                    SongTemplate.A_MINOR_SCALE -> generateScale(
                        "A Minor Scale",
                        listOf("A4", "B4", "C5", "D5", "E5", "F5", "G5"),
                        difficulty = "Beginner"
                    )
                    SongTemplate.E_MINOR_SCALE -> generateScale(
                        "E Minor Scale",
                        listOf("E4", "F#4", "G4", "A4", "B4", "C5", "D5"),
                        difficulty = "Beginner"
                    )
                    SongTemplate.D_MINOR_SCALE -> generateScale(
                        "D Minor Scale",
                        listOf("D4", "E4", "F4", "G4", "A4", "Bb4", "C5"),
                        difficulty = "Beginner"
                    )
                    SongTemplate.C_MINOR_SCALE -> generateScale(
                        "C Minor Scale",
                        listOf("C4", "D4", "Eb4", "F4", "G4", "Ab4", "Bb4"),
                        difficulty = "Intermediate"
                    )
                }
                repository.addSong(song)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Generate a scale song with the given notes.
     * 
     * Creates a scale that:
     * - Goes up the scale with all provided notes
     * - Then comes back down (excluding the top note to avoid repetition)
     * - Each note has 1 second duration
     * - Notes are spaced 2 seconds apart
     * 
     * @param title The song title
     * @param scaleNotes List of notes in the scale (ascending order)
     * @param difficulty Difficulty level (default: "Beginner")
     * @return SongEntity with the scale notes
     */
    private fun generateScale(
        title: String,
        scaleNotes: List<String>,
        difficulty: String = "Beginner"
    ): SongEntity {
        // Create ascending scale
        val scaleUp = scaleNotes
        
        // Create descending scale (reverse and skip the last note to avoid duplication)
        val scaleDown = scaleNotes.reversed().drop(1)
        
        // Combine for full sequence
        val fullSequence = scaleUp + scaleDown
        
        // Build the notes JSON array
        // Each note object has: note (pitch), time (start time), duration
        val notesJson = buildString {
            append("[")
            fullSequence.forEachIndexed { index, note ->
                if (index > 0) append(",")
                append("""
                    {
                        "note": "$note",
                        "time": ${index * 2.0},
                        "duration": 1.0
                    }
                """.trimIndent())
            }
            append("]")
        }
        
        // Calculate total duration
        // Last note starts at (noteCount-1) * 2 seconds, duration 1 second
        val totalDuration = (fullSequence.size - 1) * 2 + 1
        
        return SongEntity(
            title = title,
            artist = "Template",
            bpm = 120,  // 120 BPM (standard tempo)
            notesJson = notesJson,
            duration = totalDuration,
            difficulty = difficulty
        )
    }
}
