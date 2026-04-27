package com.chordcraft.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a song stored locally.
 * 
 * This is the database table schema for songs. Room will automatically
 * create a table called "songs" with these columns.
 * 
 * @property id Auto-generated unique identifier
 * @property title Song title (e.g., "Für Elise")
 * @property artist Artist or composer name (e.g., "Beethoven")
 * @property bpm Beats per minute (tempo)
 * @property notesJson JSON string containing piano note data with timing
 * @property duration Song duration in seconds
 * @property difficulty Difficulty level: "Beginner", "Intermediate", or "Advanced"
 * @property dateAdded Timestamp when the song was added (milliseconds since epoch)
 */
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val title: String,
    val artist: String,
    val bpm: Int,
    
    // JSON format example: [{"note": "C4", "time": 0.0, "duration": 0.5}, ...]
    val notesJson: String,
    
    val duration: Int,  // in seconds
    val difficulty: String = "Beginner",
    val dateAdded: Long = System.currentTimeMillis()
)
