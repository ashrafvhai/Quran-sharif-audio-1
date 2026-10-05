package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.player.PlayerUiState
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun FullScreenPlayer(
    state: PlayerUiState,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSkip15Forward: () -> Unit,
    onSkip15Backward: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onChangeSpeed: (Float) -> Unit,
    onOpenAmbientSheet: () -> Unit,
    onOpenQueueSheet: () -> Unit,
    onOpenMushafSheet: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onToggleMute: () -> Unit,
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showVolumePopup by remember { mutableStateOf(false) }

    // Vinyl rotation animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(28000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // User dragging slider state
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderPosition by remember { mutableFloatStateOf(0f) }

    val currentSliderValue = if (isDraggingSlider) {
        dragSliderPosition
    } else {
        if (state.durationMs > 0) state.currentPositionMs.toFloat() else 0f
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
    ) {
        // 1. Full-Bleed Nature Background
        val ambient = state.ambientSound
        if (ambient.backgroundDrawableRes != null) {
            Image(
                painter = painterResource(id = ambient.backgroundDrawableRes),
                contentDescription = ambient.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (ambient.backgroundFallbackUrl.isNotBlank()) {
            AsyncImage(
                model = ambient.backgroundFallbackUrl,
                contentDescription = ambient.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Default Mood Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                ambient.primaryMoodColor,
                                ambient.secondaryMoodColor,
                                Color(0xFF07080B)
                            )
                        )
                    )
            )
        }

        // 2. Dark Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.70f),
                            Color.Black.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        // 3. Foreground Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize Button
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                        .testTag("player_minimize_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Ambient Sound Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x55000000))
                        .border(1.2.dp, GoldPrimary.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .clickable { onOpenAmbientSheet() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("ambient_sound_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = ambient.emoji,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (ambient.id == "none") "Background Sound" else "${ambient.name} • ${(state.ambientVolume * 100).toInt()}%",
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Select Ambient",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Favorite Star Button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x33000000))
                        .testTag("player_favorite_button")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) GoldPrimary else TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CENTER COVER / MEDALLION ART
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF3B2E15),
                                Color(0xFF14151D)
                            )
                        )
                    )
                    .border(2.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating ring decoration
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .rotate(if (state.isPlaying) rotation else 0f)
                        .border(1.5.dp, GoldPrimary.copy(alpha = 0.25f), CircleShape)
                )

                // Inner avatar / calligraphy emblem
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "سُورَةُ",
                        color = GoldPrimary.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = state.currentSurah.nameArabic,
                        color = GoldPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Surah ${state.currentSurah.number}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TITLES & RECITER
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.currentSurah.nameEnglish,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.currentSurah.nameTranslation,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = state.currentReciter.name,
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "(${state.currentReciter.country})",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SEEK BAR & TIMESTAMPS
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentSliderValue,
                    onValueChange = {
                        isDraggingSlider = true
                        dragSliderPosition = it
                    },
                    onValueChangeFinished = {
                        isDraggingSlider = false
                        onSeekTo(dragSliderPosition.toLong())
                    },
                    valueRange = 0f..state.durationMs.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldPrimary,
                        activeTrackColor = GoldPrimary,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("player_seek_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentPos = if (isDraggingSlider) dragSliderPosition.toLong() else state.currentPositionMs
                    Text(
                        text = formatTimeMs(currentPos),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    val remainingMs = (state.durationMs - currentPos).coerceAtLeast(0L)
                    Text(
                        text = "-${formatTimeMs(remainingMs)}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // MAIN PLAYBACK CONTROLS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Surah
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("player_previous_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }

                // -15s Skip
                IconButton(
                    onClick = onSkip15Backward,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("player_skip_backward_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastRewind,
                        contentDescription = "Rewind 15 seconds",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Play / Pause Large Center Button
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    GoldPrimary,
                                    Color(0xFFD49E24)
                                )
                            )
                        )
                        .clickable { onTogglePlay() }
                        .testTag("player_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isBuffering) {
                        CircularProgressIndicator(
                            color = Color(0xFF090A0E),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color(0xFF090A0E),
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                // +15s Skip
                IconButton(
                    onClick = onSkip15Forward,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("player_skip_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.FastForward,
                        contentDescription = "Forward 15 seconds",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Next Surah
                IconButton(
                    onClick = onNext,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("player_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next Surah",
                        tint = TextPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // SECONDARY CONTROLS (Playback speed & Sleep timer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Playback Speed Pill
                val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
                val currentSpeedIndex = speeds.indexOf(state.playbackSpeed).let { if (it == -1) 1 else it }
                val nextSpeed = speeds[(currentSpeedIndex + 1) % speeds.size]

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFFFFF))
                        .clickable { onChangeSpeed(nextSpeed) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("player_speed_pill")
                ) {
                    Text(
                        text = "${state.playbackSpeed}x",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Sleep Timer Button with indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (state.sleepTimerMinutes != null) GoldPrimary.copy(alpha = 0.2f) else Color(0x33FFFFFF))
                        .border(
                            1.dp,
                            if (state.sleepTimerMinutes != null) GoldPrimary else Color.Transparent,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onOpenSleepTimer() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("player_sleep_timer_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Nightlight,
                            contentDescription = "Sleep Timer",
                            tint = if (state.sleepTimerMinutes != null) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (state.sleepTimerSecondsRemaining != null) {
                                "%02d:%02d".format(
                                    state.sleepTimerSecondsRemaining / 60,
                                    state.sleepTimerSecondsRemaining % 60
                                )
                            } else if (state.sleepTimerMinutes == -1) {
                                "End of Surah"
                            } else {
                                "Sleep Timer"
                            },
                            color = if (state.sleepTimerMinutes != null) GoldPrimary else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTTOM ROW: Volume, Mushaf scripture, Cast, Queue list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Volume button
                IconButton(
                    onClick = { onToggleMute() },
                    modifier = Modifier.testTag("player_volume_button")
                ) {
                    Icon(
                        imageVector = if (state.isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                        contentDescription = "Volume",
                        tint = if (state.isMuted) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Arabic Text / Mushaf Scripture Button
                IconButton(
                    onClick = onOpenMushafSheet,
                    modifier = Modifier.testTag("player_mushaf_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = "Mushaf Scripture Text",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Cast Button
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Ready to stream via Cast & AirPlay")
                        }
                    },
                    modifier = Modifier.testTag("player_cast_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cast,
                        contentDescription = "Cast",
                        tint = TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Queue List Button
                IconButton(
                    onClick = onOpenQueueSheet,
                    modifier = Modifier.testTag("player_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.QueueMusic,
                        contentDescription = "Surah Queue",
                        tint = GoldPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Floating Snackbar for user messages (e.g. Cast)
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

private fun formatTimeMs(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
