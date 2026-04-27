package com.chordcraft.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.chordcraft.ui.screens.HomeScreen
import com.chordcraft.ui.screens.LibraryScreen
import com.chordcraft.ui.screens.PlaybackScreen

/**
 * Navigation routes in the app.
 * 
 * Sealed class ensures type safety when navigating.
 * Each route has a unique string identifier.
 */
sealed class Screen(val route: String) {
    /** Home screen route */
    object Home : Screen("home")
    
    /** Library screen route */
    object Library : Screen("library")
    
    /** Playback screen route with song ID argument */
    object Playback : Screen("playback/{songId}") {
        fun createRoute(songId: Long) = "playback/$songId"
    }
}

/**
 * Main navigation graph for the app.
 * 
 * This composable sets up all screens and their routes.
 * NavHost handles the backstack and navigation transitions.
 * 
 * @param navController Controller for navigation actions
 */
@Composable
fun ChordCraftNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route  // Start at home screen
    ) {
        // Home screen destination
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToLibrary = {
                    // Navigate to library when button is clicked
                    navController.navigate(Screen.Library.route)
                }
            )
        }
        
        // Library screen destination
        composable(route = Screen.Library.route) {
            LibraryScreen(
                onBackClick = {
                    // Pop back to previous screen (home)
                    navController.popBackStack()
                },
                onSongClick = { songId ->
                    // Navigate to playback screen with song ID
                    navController.navigate(Screen.Playback.createRoute(songId))
                }
            )
        }
        
        // Playback screen destination
        composable(
            route = Screen.Playback.route,
            arguments = listOf(
                navArgument("songId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getLong("songId") ?: 0L
            PlaybackScreen(
                songId = songId,
                onBackClick = {
                    // Pop back to library
                    navController.popBackStack()
                }
            )
        }
    }
}
