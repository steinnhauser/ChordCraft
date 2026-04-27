package com.chordcraft.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Room database for the ChordCraft application.
 * 
 * This is the main database configuration. Room will generate the implementation
 * at compile time. The database holds all entities and provides DAOs.
 * 
 * @Database annotation parameters:
 * - entities: List of all entity classes (tables) in the database
 * - version: Database schema version (increment when schema changes)
 * - exportSchema: Whether to export schema to a file (useful for migrations)
 */
@Database(
    entities = [SongEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to Song database operations.
     * Room will implement this automatically.
     */
    abstract fun songDao(): SongDao
    
    companion object {
        /**
         * Database name used for the SQLite file.
         */
        const val DATABASE_NAME = "chordcraft_database"
    }
}
