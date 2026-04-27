package com.chordcraft

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for ChordCraft.
 * 
 * @HiltAndroidApp triggers Hilt's code generation including a base class
 * for your application that serves as the application-level dependency container.
 */
@HiltAndroidApp
class ChordCraftApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-level components here
    }
}
