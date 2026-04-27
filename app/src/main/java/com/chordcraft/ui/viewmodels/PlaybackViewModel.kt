package com.chordcraft.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chordcraft.data.local.SongEntity
import com.chordcraft.data.models.Note
import com.chordcraft.data.repository.SongRepository
import com.chordcraft.utils.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Playback state for the player screen.
 */
data class PlaybackState(
    val song: SongEntity? = null,
    val notes: List<Note> = emptyList(),
    val currentTime: Float = 0f,  // Current playback position in seconds
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val isLooping: Boolean = false,
    val playbackSpeed: Float = 1.0f,  // 1.0 = normal speed
    val activeNoteIndices: Set<Int> = emptySet()  // Indices of notes currently being played
)

/**
 * ViewModel for the playback screen.
 * 
 * Manages song playback, timing, and visual state for the falling notes interface.
 * 
 * @HiltViewModel enables Hilt injection
 * @property savedStateHandle Provides access to navigation arguments
 * @property repository Song repository for fetching song data
 */
@HiltViewModel
class PlaybackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SongRepository
) : ViewModel() {
    
    // Get song ID from navigation arguments
    private val songId: Long = savedStateHandle.get<Long>("songId") ?: 0L
    
    // Mutable state for playback
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()
    
    // Job for the playback timer
    private var playbackJob: Job? = null
    
    // Constant for how long before a note hits the key (2 seconds as requested)
    companion object {
        const val NOTE_FALL_DURATION = 2.0f  // seconds
        private const val TIMER_TICK_MS = 16L  // ~60 FPS
    }
    
    init {
        loadSong()
    }
    
    /**
     * Load the song from the database.
     */
    private fun loadSong() {
        viewModelScope.launch {
            val song = repository.getSongById(songId)
            if (song != null) {
                val notes = JsonParser.parseNotes(song.notesJson)
                _state.update { it.copy(song = song, notes = notes) }
            }
        }
    }
    
    /**
     * Toggle play/pause.
     */
    fun togglePlayPause() {
        if (_state.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }
    
    /**
     * Start playback.
     */
    fun play() {
        _state.update { it.copy(isPlaying = true) }
        startPlaybackTimer()
    }
    
    /**
     * Pause playback.
     */
    fun pause() {
        _state.update { it.copy(isPlaying = false) }
        stopPlaybackTimer()
    }
    
    /**
     * Restart the song from the beginning.
     */
    fun restart() {
        _state.update { it.copy(currentTime = 0f, activeNoteIndices = emptySet()) }
        if (_state.value.isPlaying) {
            stopPlaybackTimer()
            startPlaybackTimer()
        }
    }
    
    /**
     * Toggle mute.
     */
    fun toggleMute() {
        _state.update { it.copy(isMuted = !it.isMuted) }
    }
    
    /**
     * Toggle looping.
     */
    fun toggleLooping() {
        _state.update { it.copy(isLooping = !it.isLooping) }
    }
    
    /**
     * Set playback speed.
     * 
     * @param speed Speed multiplier (0.5 = half speed, 1.0 = normal, 2.0 = double speed)
     */
    fun setPlaybackSpeed(speed: Float) {
        _state.update { it.copy(playbackSpeed = speed.coerceIn(0.25f, 2.0f)) }
    }
    
    /**
     * Seek to a specific time in the song.
     * 
     * @param time Time in seconds
     */
    fun seekTo(time: Float) {
        val song = _state.value.song ?: return
        _state.update { 
            it.copy(
                currentTime = time.coerceIn(0f, song.duration.toFloat()),
                activeNoteIndices = emptySet()
            ) 
        }
    }
    
    /**
     * Start the playback timer.
     * 
     * This updates currentTime at ~60 FPS and tracks which notes are active.
     */
    private fun startPlaybackTimer() {
        stopPlaybackTimer()  // Cancel any existing timer
        
        playbackJob = viewModelScope.launch {
            while (_state.value.isPlaying) {
                val deltaTime = (TIMER_TICK_MS / 1000f) * _state.value.playbackSpeed
                
                _state.update { state ->
                    val newTime = state.currentTime + deltaTime
                    val song = state.song
                    
                    // Check if we've reached the end
                    if (song != null && newTime >= song.duration) {
                        if (state.isLooping) {
                            // Loop back to start
                            state.copy(currentTime = 0f, activeNoteIndices = emptySet())
                        } else {
                            // Stop at end
                            stopPlaybackTimer()
                            state.copy(
                                currentTime = song.duration.toFloat(),
                                isPlaying = false,
                                activeNoteIndices = emptySet()
                            )
                        }
                    } else {
                        // Update active notes
                        val activeNotes = state.notes.mapIndexedNotNull { index, note ->
                            if (newTime >= note.time && newTime < note.endTime) {
                                index
                            } else {
                                null
                            }
                        }.toSet()
                        
                        state.copy(
                            currentTime = newTime,
                            activeNoteIndices = activeNotes
                        )
                    }
                }
                
                delay(TIMER_TICK_MS)
            }
        }
    }
    
    /**
     * Stop the playback timer.
     */
    private fun stopPlaybackTimer() {
        playbackJob?.cancel()
        playbackJob = null
    }
    
    override fun onCleared() {
        super.onCleared()
        stopPlaybackTimer()
    }
}
