package com.chordcraft.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Song database operations.
 * 
 * Room will implement this interface automatically at compile time.
 * All database queries are defined here. Using Flow for queries that
 * should observe changes, and suspend functions for one-time operations.
 */
@Dao
interface SongDao {
    
    /**
     * Get all songs as a Flow. This will automatically emit new data
     * whenever the songs table changes, allowing the UI to react in real-time.
     * 
     * Ordered by date added (most recent first).
     */
    @Query("SELECT * FROM songs ORDER BY dateAdded DESC")
    fun getAllSongs(): Flow<List<SongEntity>>
    
    /**
     * Get a single song by its ID.
     * 
     * @param songId The unique identifier of the song
     * @return The song if found, null otherwise
     */
    @Query("SELECT * FROM songs WHERE id = :songId")
    suspend fun getSongById(songId: Long): SongEntity?
    
    /**
     * Insert a new song into the database.
     * 
     * @param song The song to insert
     * @return The row ID of the newly inserted song
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity): Long
    
    /**
     * Insert multiple songs at once.
     * 
     * @param songs List of songs to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)
    
    /**
     * Update an existing song.
     * 
     * @param song The song with updated values
     */
    @Update
    suspend fun updateSong(song: SongEntity)
    
    /**
     * Delete a song from the database.
     * 
     * @param song The song to delete
     */
    @Delete
    suspend fun deleteSong(song: SongEntity)
    
    /**
     * Delete a song by its ID.
     * 
     * @param songId The unique identifier of the song to delete
     */
    @Query("DELETE FROM songs WHERE id = :songId")
    suspend fun deleteSongById(songId: Long)
    
    /**
     * Delete all songs from the database.
     * Useful for clearing the library.
     */
    @Query("DELETE FROM songs")
    suspend fun deleteAllSongs()
}
