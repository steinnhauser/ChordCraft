package com.chordcraft.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chordcraft.data.local.SongEntity
import com.chordcraft.ui.viewmodels.LibraryUiState
import com.chordcraft.ui.viewmodels.LibraryViewModel
import com.chordcraft.ui.viewmodels.SongTemplate

/**
 * Library screen that displays the user's song collection.
 * 
 * Shows different states:
 * - Loading: While fetching songs from database
 * - Empty: When no songs exist (with helpful message)
 * - Success: List of songs
 * - Error: If something went wrong
 * 
 * @param onBackClick Callback when back button is pressed
 * @param onSongClick Callback when a song is clicked to play
 * @param viewModel The ViewModel managing library state (injected by Hilt)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBackClick: () -> Unit,
    onSongClick: (Long) -> Unit = {},  // Default empty to maintain compatibility
    viewModel: LibraryViewModel = hiltViewModel()
) {
    // Collect UI state from ViewModel
    // collectAsStateWithLifecycle is lifecycle-aware and stops collecting when not visible
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // State for controlling bottom sheets
    var showAddSongSheet by remember { mutableStateOf(false) }
    var showTemplateSheet by remember { mutableStateOf(false) }
    
    // Scaffold provides the basic Material Design layout structure
    Scaffold(
        topBar = {
            // Top app bar with title and back button
            TopAppBar(
                title = { Text("My Library") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←", style = MaterialTheme.typography.headlineMedium)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Floating Action Button (FAB) to add new songs
            // Only show when we're not in an error state
            if (uiState !is LibraryUiState.Error) {
                FloatingActionButton(
                    onClick = { showAddSongSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add song"
                    )
                }
            }
        }
    ) { paddingValues ->
        // Main content area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Display different UI based on current state
            when (uiState) {
                is LibraryUiState.Loading -> {
                    LoadingState()
                }
                is LibraryUiState.Empty -> {
                    EmptyState()
                }
                is LibraryUiState.Success -> {
                    SongList(
                        songs = (uiState as LibraryUiState.Success).songs,
                        onSongClick = onSongClick,
                        onDeleteSong = { song -> viewModel.deleteSong(song) }
                    )
                }
                is LibraryUiState.Error -> {
                    ErrorState(message = (uiState as LibraryUiState.Error).message)
                }
            }
        }
    }
    
    // Bottom sheet for choosing add song method
    if (showAddSongSheet) {
        AddSongMethodSheet(
            onDismiss = { showAddSongSheet = false },
            onFromTemplate = {
                showAddSongSheet = false
                showTemplateSheet = true
            },
            onImportJson = {
                showAddSongSheet = false
                // TODO: Implement JSON import
            }
        )
    }
    
    // Bottom sheet for choosing template
    if (showTemplateSheet) {
        TemplateSelectionSheet(
            onDismiss = { showTemplateSheet = false },
            onTemplateSelected = { template ->
                showTemplateSheet = false
                viewModel.addSongFromTemplate(template)
            }
        )
    }
}

/**
 * Bottom sheet for selecting how to add a song.
 * 
 * @param onDismiss Callback when sheet is dismissed
 * @param onFromTemplate Callback when "From template" is selected
 * @param onImportJson Callback when "Import JSON" is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSongMethodSheet(
    onDismiss: () -> Unit,
    onFromTemplate: () -> Unit,
    onImportJson: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Sheet title
            Text(
                text = "Add Song",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // From Template option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFromTemplate() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📋 From Template",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Choose from pre-made song templates",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Import JSON option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onImportJson() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "📄 Import JSON",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Import a song from JSON file (coming soon)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Bottom sheet for selecting a song template.
 * 
 * @param onDismiss Callback when sheet is dismissed
 * @param onTemplateSelected Callback when a template is selected
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateSelectionSheet(
    onDismiss: () -> Unit,
    onTemplateSelected: (SongTemplate) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Sheet title
            Text(
                text = "Choose Template",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // List all available templates
            SongTemplate.values().forEach { template ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTemplateSelected(template) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "🎹 ${template.displayName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = template.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Loading state composable.
 * Shows a circular progress indicator while data loads.
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Empty state composable.
 * Shows when the user has no songs in their library yet.
 */
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Musical note emoji
        Text(
            text = "🎵",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Songs Yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Get started by tapping the + button in the corner to add your first song!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Error state composable.
 * Shows when something went wrong loading songs.
 */
@Composable
private fun ErrorState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * List of songs composable.
 * 
 * Uses LazyColumn for efficient rendering of long lists.
 * Only renders visible items, recycling views as you scroll.
 * 
 * @param songs List of songs to display
 * @param onSongClick Callback when a song is clicked
 * @param onDeleteSong Callback when user deletes a song
 */
@Composable
private fun SongList(
    songs: List<SongEntity>,
    onSongClick: (Long) -> Unit,
    onDeleteSong: (SongEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = songs,
            key = { song -> song.id }  // Unique key for efficient recomposition
        ) { song ->
            SongItem(
                song = song,
                onClick = { onSongClick(song.id) },
                onDelete = { onDeleteSong(song) }
            )
        }
    }
}

/**
 * Individual song item composable.
 * 
 * Displays song information in a card with play and delete options.
 * 
 * @param song The song to display
 * @param onClick Callback when card is clicked
 * @param onDelete Callback when delete button is clicked
 */
@Composable
private fun SongItem(
    song: SongEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play icon
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Song information
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Song title
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Artist name
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Song metadata (BPM, duration, difficulty)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${song.bpm} BPM",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "${song.duration}s",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = song.difficulty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Delete button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete song",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
