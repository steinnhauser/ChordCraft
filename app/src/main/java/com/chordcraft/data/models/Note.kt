package com.chordcraft.data.models

/**
 * Represents a single musical note to be played.
 * 
 * This is parsed from the song's JSON note data.
 * 
 * @property note The note pitch in scientific notation (e.g., "C4", "F#5", "Bb4")
 * @property time The start time of the note in seconds
 * @property duration How long the note should be played in seconds
 */
data class Note(
    val note: String,
    val time: Double,
    val duration: Double
) {
    /**
     * Get the base note name without octave (e.g., "C", "F#", "Bb").
     */
    val baseName: String
        get() = note.dropLast(1)
    
    /**
     * Get the octave number (e.g., 4, 5).
     */
    val octave: Int
        get() = note.last().toString().toIntOrNull() ?: 4
    
    /**
     * Check if this is a black key (sharp or flat).
     */
    val isBlackKey: Boolean
        get() = baseName.contains("#") || baseName.contains("b")
    
    /**
     * Get the end time of this note.
     */
    val endTime: Double
        get() = time + duration
}
