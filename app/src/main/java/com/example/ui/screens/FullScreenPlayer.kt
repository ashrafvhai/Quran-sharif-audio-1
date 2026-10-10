package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.GoldPrimary
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
    onOpenReciterSwitcher: () -> Unit,
    onToggleMute: () -> Unit,
    onMinimize: () -> Unit,
    onSelectVideoTheme: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Clean Display Mode (True by default whenever video is playing as requested by the user)
    var isCleanDisplayMode by remember { mutableStateOf(true) }

    // Current background video theme
    val currentVideoTheme = remember(state.selectedVideoThemeId) {
        BackgroundVideoData.getThemeById(state.selectedVideoThemeId)
    }

    // Vinyl rotation animation for details mode
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

    val maxDuration = state.durationMs.toFloat().coerceAtLeast(1f)
    val currentSliderValue = if (isDraggingSlider) {
        dragSliderPosition.coerceIn(0f, maxDuration)
    } else {
        state.currentPositionMs.toFloat().coerceIn(0f, maxDuration)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
    ) {
        // 1. Full-Bleed Nature Video Background (100% Silent, Clean, Auto-mixing / Sequential)
        AmbientVideoBackground(
            ambient = state.ambientSound,
            isPlaying = state.isPlaying,
            videoThemeId = state.selectedVideoThemeId,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Foreground Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP BAR: Minimize, Background Changer Option Pill, Favorite & Mode Toggle
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
                        .background(Color(0x44000000))
                        .testTag("player_minimize_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minimize Player",
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Top Background Changer Option Pill ("baground চেঞ্জ করার যেই অপশন টা উপর দিয়ে আছে ঐটা থাকবে")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0x66000000))
                        .border(1.2.dp, GoldPrimary.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
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
                            text = currentVideoTheme.banglaName, // e.g. "মিক্সিং বাগ্রাউণ্ড" or "সমুদ্র ও ঢেউ"
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Change Background",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Right controls: Clean / Detail mode switch & Favorite Star
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Mode Toggle (Clean Video View vs Detailed View)
                    IconButton(
                        onClick = { isCleanDisplayMode = !isCleanDisplayMode },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (!isCleanDisplayMode) GoldPrimary.copy(alpha = 0.3f) else Color(0x44000000))
                            .testTag("player_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isCleanDisplayMode) Icons.Filled.Tune else Icons.Filled.AutoAwesome,
                            contentDescription = if (isCleanDisplayMode) "Show Full Details" else "Clean Video View",
                            tint = if (isCleanDisplayMode) TextSecondary else GoldPrimary,
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
                            tint = if (isFavorite) GoldPrimary else TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // ==========================================
            // CENTER DISPLAY (CLEAN MODE vs FULL DETAIL)
            // ==========================================
            if (isCleanDisplayMode) {
                // --- ULTRA-CLEAN DISPLAY MODE ---
                // The center is completely uncluttered so the video is fully visible!
                Spacer(modifier = Modifier.weight(1f))

                // Elegant floating frosted info pill for Surah and Qari
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x55000000))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = state.currentSurah.nameArabic,
                            color = GoldPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${state.currentSurah.nameEnglish} • ${state.currentReciter.name}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatTimeMs(state.currentPositionMs),
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = formatTimeMs(state.durationMs),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- CLEAN BOTTOM CONTROLS ---
                // "শুধু থাকবে স্টপ বাটনে এবং চেঞ্জ ব্যাটন"
                // Floating minimalist glass bar with Stop, Change & Video cycle buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x66000000))
                        .border(1.2.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // চেঞ্জ বাটন (পূর্ববর্তী সূরা)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onPrevious() }
                                .padding(8.dp)
                                .testTag("player_previous_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "পূর্ববর্তী সূরা (Change)",
                                tint = TextPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "পূর্ববর্তী",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // স্টপ বাটন (স্টপ / প্লে বাটন - Large prominent center button)
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        if (state.isPlaying) {
                                            listOf(GoldPrimary, Color(0xFFC48E19))
                                        } else {
                                            listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                        }
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
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (state.isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                                        contentDescription = if (state.isPlaying) "স্টপ বাটন" else "প্লে বাটন",
                                        tint = Color(0xFF090A0E),
                                        modifier = Modifier.size(34.dp)
                                    )
                                    Text(
                                        text = if (state.isPlaying) "স্টপ" else "প্লে",
                                        color = Color(0xFF090A0E),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // চেঞ্জ বাটন (পরবর্তী সূরা)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onNext() }
                                .padding(8.dp)
                                .testTag("player_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "পরবর্তী সূরা (Change)",
                                tint = TextPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "পরবর্তী",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // ভিডিও চেঞ্জ বাটন (Quick Background Scene Change)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    // Cycle to next video theme
                                    val themes = BackgroundVideoData.themes
                                    val currentIndex = themes.indexOfFirst { it.id == state.selectedVideoThemeId }
                                    val nextTheme = themes[(currentIndex + 1).coerceAtLeast(0) % themes.size]
                                    onSelectVideoTheme?.invoke(nextTheme.id)
                                }
                                .padding(8.dp)
                                .testTag("player_video_cycle_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shuffle,
                                contentDescription = "ভিডিও পরিবর্তন (Change Video)",
                                tint = GoldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "ভিডিও বদল",
                                color = GoldPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            } else {
                // --- FULL DETAILS MODE (EXPANDED) ---
                Spacer(modifier = Modifier.height(6.dp))

                // CENTER COVER / MEDALLION ART WITH QARI PORTRAIT
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFF3B2E15),
                                    Color(0xFF14151D)
                                )
                            )
                        )
                        .border(2.5.dp, GoldPrimary.copy(alpha = 0.7f), CircleShape)
                        .clickable { onOpenReciterSwitcher() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .rotate(if (state.isPlaying) rotation else 0f)
                            .border(1.5.dp, GoldPrimary.copy(alpha = 0.35f), CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .border(2.dp, GoldPrimary, CircleShape)
                    ) {
                        ReciterAvatar(
                            reciter = state.currentReciter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xEE090A0E))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${state.currentSurah.nameArabic} • ${state.currentSurah.number}",
                            color = GoldPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // TITLES & RECITER
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.currentSurah.nameEnglish,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = state.currentSurah.nameTranslation,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Qari Switcher Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x551A1C28))
                            .border(1.2.dp, GoldPrimary.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                            .clickable { onOpenReciterSwitcher() }
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                            .testTag("player_reciter_pill")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            ReciterAvatar(
                                reciter = state.currentReciter,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = state.currentReciter.name,
                                color = GoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "• Change Qari ▾",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // SEEK BAR & TIMESTAMPS
                Column(modifier = Modifier.fillMaxWidth()) {
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

                // MAIN PLAYBACK CONTROLS ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("player_previous_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Previous Surah",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkip15Backward,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("player_skip_backward_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastRewind,
                            contentDescription = "Rewind 15 seconds",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(GoldPrimary, Color(0xFFD49E24))
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
                                modifier = Modifier.size(34.dp)
                            )
                        } else {
                            Icon(
                                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (state.isPlaying) "Pause" else "Play",
                                tint = Color(0xFF090A0E),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onSkip15Forward,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("player_skip_forward_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "Forward 15 seconds",
                            tint = TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier
                            .size(42.dp)
                            .testTag("player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next Surah",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // SECONDARY CONTROLS (Speed & Sleep timer)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                Spacer(modifier = Modifier.height(8.dp))

                // BOTTOM SHEET SHORTCUTS (Volume, Mushaf, Cast, Queue)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
        }

        // Floating Snackbar
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
