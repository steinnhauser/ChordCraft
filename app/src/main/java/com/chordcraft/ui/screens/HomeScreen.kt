package com.chordcraft.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Home screen composable that displays the main interface of the app.
 * 
 * This is the initial screen users see when launching ChordCraft.
 * Shows a welcome message and navigation to the library.
 * 
 * @param onNavigateToLibrary Callback when user wants to go to their library
 * @param modifier Modifier to be applied to the screen layout.
 */
@Composable
fun HomeScreen(
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Column layout centers content vertically and horizontally
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App title
        Text(
            text = "ChordCraft",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subtitle/description
        Text(
            text = "Learn Piano Without Sheet Music",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Button to navigate to library
        Button(
            onClick = onNavigateToLibrary,
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("My Library")
        }
    }
}
