package com.chordcraft.data.models

/**
 * Data model representing a piano song in ChordCraft.
 * 
 * This will eventually be used with Room database for persistence.
 * The song data includes metadata and note information for playback.
 * 
 * @property id Unique identifier for the song
 * @property title Song title
 * @property artist Artist or composer name
 * @property bpm Beats per minute (tempo)
 * @property notesJson JSON string containing note data with timing information
 * @property duration Song duration in seconds
 * @property difficulty Difficulty level (e.g., "Beginner", "Intermediate", "Advanced")
 */
data class Song(
    val id: Long = 0,
    val title: String,
    val artist: String,
    val bpm: Int,
    val notesJson: String,  // JSON format: [{"note": "C4", "time": 0.0, "duration": 0.5}, ...]
    val duration: Int,      // in seconds
    val difficulty: String = "Beginner"
)
