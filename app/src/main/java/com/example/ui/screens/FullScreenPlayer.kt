package com.example.ui.screens

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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.PlayerUiState
import com.example.ui.components.AmbientVideoBackground
import com.example.ui.components.BackgroundVideoData
import com.example.ui.components.ReciterAvatar
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

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
    onOpenReciterSwitcher: () -> Unit,
    onToggleMute: () -> Unit,
    onMinimize: () -> Unit,
    onSelectVideoTheme: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Current background scene theme
    val currentVideoTheme = remember(state.selectedVideoThemeId) {
        BackgroundVideoData.getThemeById(state.selectedVideoThemeId)
    }

    // User dragging slider state
    var isDraggingSlider by remember { mutableStateOf(false) }
    var dragSliderPosition by remember { mutableFloatStateOf(0f) }

    val maxDuration = state.durationMs.toFloat().coerceAtLeast(1f)
    val currentSliderValue = if (isDraggingSlider) {
        dragSliderPosition.coerceIn(0f, maxDuration)
    } else {
        state.currentPositionMs.toFloat().coerceIn(0f, maxDuration)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Serene Still Image Background (Smooth, Clean, Crisp)
        AmbientVideoBackground(
            ambient = state.ambientSound,
            isPlaying = state.isPlaying,
            videoThemeId = state.selectedVideoThemeId,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Foreground Layout: Top Bar -> Completely Empty Middle -> Bottom Control Dock
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ==========================================================
            // TOP BAR: Minimalist & Clean
            // ==========================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minimize Down Arrow Button
                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x44000000))
                        .testTag("player_minimize_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Top Background Theme Pill (Click to open 20 themes sheet)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0x44000000))
                        .clickable { onOpenAmbientSheet() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("ambient_sound_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currentVideoTheme.emoji,
                            fontSize = 15.sp
                        )
                        Text(
                            text = currentVideoTheme.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Change Background",
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Top Right Controls (Theme Cycle & Favorite Star)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quick Theme Cycle Button
                    IconButton(
                        onClick = {
                            val themes = BackgroundVideoData.individualThemes
                            val currentIndex = themes.indexOfFirst { it.id == state.selectedVideoThemeId }
                            val nextTheme = themes[(currentIndex + 1).coerceAtLeast(0) % themes.size]
                            onSelectVideoTheme?.invoke(nextTheme.id)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .testTag("player_quick_theme_cycle")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shuffle,
                            contentDescription = "Cycle Scene Theme",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x44000000))
                            .testTag("player_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ==========================================================
            // CENTER: COMPLETELY EMPTY AS REQUESTED
            // "মাঝখান থেকে সবকিছু সর মাসখানে যেনো কিসু না থাকে"
            // ZERO visual elements here, allowing the user to experience
            // the background scenery completely clean and unobstructed!
            // ==========================================================
            Spacer(modifier = Modifier.weight(1f))

            // ==========================================================
            // BOTTOM CONTROLS: Clean floating buttons without black background container
            // "কন্ট্রোল প্যানেল এর জাস্ট ব্যাটন গুলা থাকবে আর বাকি যেই কালো অংশটা বেনগ্রাউন্ট হিসেবে আছে ঐটা লাগবেনা"
            // ==========================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. TRACK & RECITER INFO ROW
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenReciterSwitcher() }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Reciter Info with colorful circle avatar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape)
                            ) {
                                ReciterAvatar(
                                    reciter = state.currentReciter,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Column {
                                Text(
                                    text = state.currentReciter.name,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${state.currentReciter.subtitle} • Tap to switch",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Surah Information Badge
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = state.currentSurah.nameArabic,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${state.currentSurah.nameEnglish} • #${state.currentSurah.number}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // 2. SLIDER & TIMESTAMPS
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Slider(
                            value = currentSliderValue.coerceIn(0f, maxDuration),
                            onValueChange = {
                                isDraggingSlider = true
                                dragSliderPosition = it.coerceIn(0f, maxDuration)
                            },
                            onValueChangeFinished = {
                                isDraggingSlider = false
                                onSeekTo(dragSliderPosition.toLong())
                            },
                            valueRange = 0f..maxDuration,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color(0x33FFFFFF)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .testTag("player_seek_slider")
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val currentPos = if (isDraggingSlider) dragSliderPosition.toLong() else state.currentPositionMs
                            Text(
                                text = formatTimeMs(currentPos),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            val remainingMs = (state.durationMs - currentPos).coerceAtLeast(0L)
                            Text(
                                text = "-${formatTimeMs(remainingMs)}",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // 3. MAIN CONTROLS ROW: Prev, Rewind 15, Stop/Play, Forward 15, Next
                    // Pure floating icons without any white background or container
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Surah Button
                        IconButton(
                            onClick = onPrevious,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_previous_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "Previous Surah",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        // Rewind 15s Button
                        IconButton(
                            onClick = onSkip15Backward,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_skip_backward_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FastRewind,
                                contentDescription = "Rewind 15 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Stop / Play Button - Pure Floating Icon without any white background circle
                        IconButton(
                            onClick = onTogglePlay,
                            modifier = Modifier
                                .size(72.dp)
                                .testTag("player_play_pause_button")
                        ) {
                            if (state.isBuffering) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(36.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = if (state.isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                    contentDescription = if (state.isPlaying) "Stop Button" else "Play Button",
                                    tint = Color.White,
                                    modifier = Modifier.size(54.dp)
                                )
                            }
                        }

                        // Forward 15s Button
                        IconButton(
                            onClick = onSkip15Forward,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_skip_forward_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FastForward,
                                contentDescription = "Forward 15 seconds",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Next Surah Button
                        IconButton(
                            onClick = onNext,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("player_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Next Surah",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    // 4. SECONDARY DOCK ACCESSORIES ROW (Volume, Speed, Sleep, Mushaf, Queue)
                    // Pure floating indicators with NO white background
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Volume / Mute Toggle Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onToggleMute() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("player_volume_button")
                        ) {
                            Icon(
                                imageVector = if (state.isMuted) Icons.Filled.VolumeMute else Icons.Filled.VolumeUp,
                                contentDescription = "Audio Volume",
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = if (state.isMuted) "Muted" else "100%",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Playback Speed Pill
                        val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f)
                        val currentSpeedIndex = speeds.indexOf(state.playbackSpeed).let { if (it == -1) 1 else it }
                        val nextSpeed = speeds[(currentSpeedIndex + 1) % speeds.size]

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onChangeSpeed(nextSpeed) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("player_speed_pill")
                        ) {
                            Text(
                                text = "${state.playbackSpeed}x",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Sleep Timer Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenSleepTimer() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("player_sleep_timer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Nightlight,
                                contentDescription = "Sleep Timer",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (state.sleepTimerSecondsRemaining != null) {
                                    "%02d:%02d".format(
                                        state.sleepTimerSecondsRemaining / 60,
                                        state.sleepTimerSecondsRemaining % 60
                                    )
                                } else {
                                    "Sleep"
                                },
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Mushaf Text Button
                        IconButton(
                            onClick = onOpenMushafSheet,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("player_mushaf_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = "Mushaf Scripture Text",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Queue Button
                        IconButton(
                            onClick = onOpenQueueSheet,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("player_queue_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QueueMusic,
                                contentDescription = "Surah Queue",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // Floating Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )
    }
}

private fun formatTimeMs(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
