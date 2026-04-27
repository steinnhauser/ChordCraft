package com.chordcraft.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chordcraft.data.models.Note
import com.chordcraft.ui.viewmodels.PlaybackState
import com.chordcraft.ui.viewmodels.PlaybackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaybackScreen(
    songId: Long,
    onBackClick: () -> Unit,
    viewModel: PlaybackViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showSettings by remember { mutableStateOf(false) }
    
    // Force landscape orientation
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top controls
            TopControlsBar(
                onBackClick = onBackClick,
                onSettingsClick = { showSettings = true },
                isMuted = state.isMuted,
                onMuteToggle = { viewModel.toggleMute() }
            )
            
            // Falling notes and piano
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (state.song != null && state.notes.isNotEmpty()) {
                    PlaybackCanvas(state = state, modifier = Modifier.fillMaxSize())
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }
            
            // Bottom controls
            BottomControls(
                state = state,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onRestartClick = { viewModel.restart() },
                onSeek = { time -> viewModel.seekTo(time) }
            )
        }
    }
    
    if (showSettings) {
        SettingsDialog(
            isLooping = state.isLooping,
            playbackSpeed = state.playbackSpeed,
            onLoopingToggle = { viewModel.toggleLooping() },
            onSpeedChange = { speed -> viewModel.setPlaybackSpeed(speed) },
            onDismiss = { showSettings = false }
        )
    }
}

@Composable
private fun TopControlsBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isMuted: Boolean,
    onMuteToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1B1F))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        
        Row {
            // Simple text for mute
            TextButton(onClick = onMuteToggle) {
                Text(if (isMuted) "🔇" else "🔊", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            
            IconButton(onClick = onSettingsClick) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
            }
        }
    }
}

@Composable
private fun PlaybackCanvas(
    state: PlaybackState,
    modifier: Modifier = Modifier
) {
    val notes = state.notes
    val currentTime = state.currentTime
    val activeNoteIndices = state.activeNoteIndices
    
    val uniqueNotes = notes.map { it.note }.distinct().sorted()
    val keyRange = remember(uniqueNotes) {
        if (uniqueNotes.isEmpty()) emptyList()
        else {
            val allKeys = generatePianoKeys()
            val startIdx = maxOf(0, allKeys.indexOf(uniqueNotes.first()) - 2)
            val endIdx = minOf(allKeys.size - 1, allKeys.indexOf(uniqueNotes.last()) + 2)
            allKeys.subList(startIdx, endIdx + 1)
        }
    }
    
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val pianoHeight = canvasHeight * 0.15f
        val pianoTop = canvasHeight - pianoHeight
        val noteAreaHeight = pianoTop
        
        if (keyRange.isNotEmpty()) {
            val keyWidth = canvasWidth / keyRange.size
            
            // Draw falling notes
            notes.forEachIndexed { index, note ->
                val keyIndex = keyRange.indexOf(note.note)
                if (keyIndex >= 0) {
                    val timeUntilPlay = note.time.toFloat() - currentTime
                    
                    if (timeUntilPlay <= PlaybackViewModel.NOTE_FALL_DURATION && currentTime < note.endTime.toFloat()) {
                        val fallProgress = 1f - (timeUntilPlay / PlaybackViewModel.NOTE_FALL_DURATION).coerceIn(0f, 1f)
                        val noteY = noteAreaHeight * fallProgress
                        val noteHeight = (note.duration.toFloat() / PlaybackViewModel.NOTE_FALL_DURATION) * noteAreaHeight
                        val x = keyIndex * keyWidth
                        
                        drawRect(
                            color = Color(0xFF6750A4),
                            topLeft = Offset(x, noteY - noteHeight),
                            size = Size(keyWidth, noteHeight)
                        )
                    }
                }
            }
            
            // Draw white keys
            keyRange.forEachIndexed { index, key ->
                val x = index * keyWidth
                val isBlackKey = key.contains("#") || key.contains("b")
                val isActive = notes.any { note ->
                    note.note == key && activeNoteIndices.contains(notes.indexOf(note))
                }
                
                if (!isBlackKey) {
                    drawRect(
                        color = if (isActive) Color(0xFF6750A4) else Color.White,
                        topLeft = Offset(x, pianoTop),
                        size = Size(keyWidth - 2f, pianoHeight)
                    )
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(x, pianoTop),
                        size = Size(keyWidth, pianoHeight),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            }
            
            // Draw black keys
            keyRange.forEachIndexed { index, key ->
                val x = index * keyWidth
                val isBlackKey = key.contains("#") || key.contains("b")
                val isActive = notes.any { note ->
                    note.note == key && activeNoteIndices.contains(notes.indexOf(note))
                }
                
                if (isBlackKey) {
                    val blackKeyWidth = keyWidth * 0.6f
                    val blackKeyHeight = pianoHeight * 0.6f
                    drawRect(
                        color = if (isActive) Color(0xFF6750A4) else Color.Black,
                        topLeft = Offset(x + keyWidth / 2 - blackKeyWidth / 2, pianoTop),
                        size = Size(blackKeyWidth, blackKeyHeight)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomControls(
    state: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onSeek: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1B1F))
            .padding(8.dp)
    ) {
        val progress = state.song?.let {
            (state.currentTime / it.duration.toFloat()).coerceIn(0f, 1f)
        } ?: 0f
        
        Slider(
            value = progress,
            onValueChange = { newProgress ->
                state.song?.let {
                    onSeek(newProgress * it.duration.toFloat())
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF6750A4),
                activeTrackColor = Color(0xFF6750A4),
                inactiveTrackColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRestartClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Restart",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(24.dp))
            
            FilledIconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF6750A4)
                )
            ) {
                Text(
                    text = if (state.isPlaying) "⏸" else "▶",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    isLooping: Boolean,
    playbackSpeed: Float,
    onLoopingToggle: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Playback Settings") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Loop Song")
                    Switch(checked = isLooping, onCheckedChange = { onLoopingToggle() })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Playback Speed: ${String.format("%.2f", playbackSpeed)}x")
                Slider(
                    value = playbackSpeed,
                    onValueChange = onSpeedChange,
                    valueRange = 0.25f..2.0f,
                    steps = 6
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun generatePianoKeys(): List<String> {
    val notes = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val octaves = 3..6
    return octaves.flatMap { octave ->
        notes.map { note -> "$note$octave" }
    }
}
