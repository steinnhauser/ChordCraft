package com.chordcraft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.chordcraft.ui.navigation.ChordCraftNavigation
import com.chordcraft.ui.theme.ChordCraftTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point activity for the ChordCraft application.
 * 
 * @AndroidEntryPoint enables dependency injection in this Android activity.
 * This allows Hilt to provide dependencies to this activity and its composables.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for modern Android UI
        enableEdgeToEdge()
        
        // Set the content view using Jetpack Compose
        setContent {
            // Apply the app's theme
            ChordCraftTheme {
                // Create navigation controller
                // rememberNavController() survives configuration changes
                val navController = rememberNavController()
                
                // Set up navigation graph
                // This handles all screen navigation in the app
                ChordCraftNavigation(navController = navController)
            }
        }
    }
}
