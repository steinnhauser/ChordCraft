package com.chordcraft.di

import android.content.Context
import androidx.room.Room
import com.chordcraft.data.local.AppDatabase
import com.chordcraft.data.local.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for application-level dependencies.
 * 
 * @Module tells Hilt this is a dependency provider module.
 * @InstallIn(SingletonComponent::class) makes dependencies available
 * throughout the entire application lifecycle.
 * 
 * This module provides instances of:
 * - Database (Room)
 * - DAOs
 * - Repositories (via constructor injection)
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provides the Room database instance.
     * 
     * @Singleton ensures only one database instance exists.
     * @ApplicationContext gives us the application context (never leaks).
     * 
     * The database is built using Room's builder pattern, specifying:
     * - Context for file location
     * - Database class
     * - Database name
     * - fallbackToDestructiveMigration() for development (recreates DB on schema changes)
     * 
     * @param context Application context injected by Hilt
     * @return The app's Room database instance
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            // In development, we can recreate the DB on schema changes
            // In production, you'd write proper migrations
            .fallbackToDestructiveMigration()
            .build()
    }
    
    /**
     * Provides the SongDao from the database.
     * 
     * Since the database is provided by Hilt, we can inject it here
     * and extract the DAO. Hilt will cache this as a Singleton.
     * 
     * @param database The app database (injected by Hilt)
     * @return The SongDao for database operations
     */
    @Provides
    @Singleton
    fun provideSongDao(database: AppDatabase): SongDao {
        return database.songDao()
    }
    
    // Note: SongRepository doesn't need a @Provides method because it uses
    // constructor injection with @Inject. Hilt will automatically provide it.
}
