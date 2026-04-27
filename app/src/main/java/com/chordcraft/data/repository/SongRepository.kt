package com.chordcraft.data.repository

import com.chordcraft.data.local.SongDao
import com.chordcraft.data.local.SongEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing song data.
 * 
 * This follows the Repository pattern, providing a clean API to access song data
 * while abstracting the data source (Room database). If we later add cloud sync,
 * we'd coordinate between local and remote sources here.
 * 
 * @property songDao The DAO for database operations (injected by Hilt)
 */
@Singleton
class SongRepository @Inject constructor(
    private val songDao: SongDao
) {
    
    /**
     * Get all songs as a Flow. UI can collect this to observe changes.
     * 
     * @return Flow emitting list of songs whenever the database changes
     */
    fun getAllSongs(): Flow<List<SongEntity>> {
        return songDao.getAllSongs()
    }
    
    /**
     * Get a specific song by ID.
     * 
     * @param songId The unique identifier
     * @return The song if found, null otherwise
     */
    suspend fun getSongById(songId: Long): SongEntity? {
        return songDao.getSongById(songId)
    }
    
    /**
     * Add a new song to the library.
     * 
     * @param song The song to add
     * @return The ID of the newly added song
     */
    suspend fun addSong(song: SongEntity): Long {
        return songDao.insertSong(song)
    }
    
    /**
     * Add multiple songs at once.
     * Useful for batch imports.
     * 
     * @param songs List of songs to add
     */
    suspend fun addSongs(songs: List<SongEntity>) {
        songDao.insertSongs(songs)
    }
    
    /**
     * Update an existing song.
     * 
     * @param song The song with updated data
     */
    suspend fun updateSong(song: SongEntity) {
        songDao.updateSong(song)
    }
    
    /**
     * Delete a song from the library.
     * 
     * @param song The song to delete
     */
    suspend fun deleteSong(song: SongEntity) {
        songDao.deleteSong(song)
    }
    
    /**
     * Delete a song by its ID.
     * 
     * @param songId The ID of the song to delete
     */
    suspend fun deleteSongById(songId: Long) {
        songDao.deleteSongById(songId)
    }
    
    /**
     * Delete all songs from the library.
     * Use with caution!
     */
    suspend fun deleteAllSongs() {
        songDao.deleteAllSongs()
    }
}
