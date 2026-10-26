package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AmbientSoundData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.player.AudioPlayerManager
import com.example.player.PlayerUiState
import com.example.ui.components.AmbientVideoBackground
import com.example.ui.components.BackgroundVideoData
import com.example.ui.components.ReciterAvatar
import com.example.ui.components.SurahQueueSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenPlayer(
    state: PlayerUiState,
    playerManager: AudioPlayerManager,
    onDismiss: () -> Unit,
    onReciterProfileClick: (Reciter) -> Unit = {}
) {
    var showQueueSheet by remember { mutableStateOf(false) }
    var showReciterSheet by remember { mutableStateOf(false) }
    var showAmbientSheet by remember { mutableStateOf(false) }
    var showVideoSheet by remember { mutableStateOf(false) }
    var showMushafSheet by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showVolumeSheet by remember { mutableStateOf(false) }

    val surah = state.currentSurah
    val reciter = state.currentReciter

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("full_screen_player_root")
    ) {
        // Serene scenic background
        AmbientVideoBackground(
            themeId = state.selectedVideoThemeId,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_full_player_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING SURAH",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${surah.number}. ${surah.nameEnglish}",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showVolumeSheet = true },
                        modifier = Modifier.testTag("full_player_volume_top_btn")
                    ) {
                        Icon(
                            imageVector = if (state.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Volume Control",
                            tint = if (state.isMuted) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    IconButton(
                        onClick = { showQueueSheet = true },
                        modifier = Modifier.testTag("full_player_queue_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                            contentDescription = "Play Queue",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Large Qari Portrait Card (The user specifically asked for large reciter image during audio play)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF111827))
                    .border(2.dp, Color(0xFF10B981).copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                    .clickable { showReciterSheet = true }
                    .testTag("full_player_qari_large_photo"),
                contentAlignment = Alignment.Center
            ) {
                if (reciter.localDrawableRes != null) {
                    Image(
                        painter = painterResource(id = reciter.localDrawableRes),
                        contentDescription = reciter.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (reciter.avatarUrl.isNotEmpty()) {
                    AsyncImage(
                        model = reciter.avatarUrl,
                        contentDescription = reciter.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Scrim vignette at the bottom of the photo card
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.85f)
                                ),
                                startY = 120f
                            )
                        )
                )

                // Bottom badge on the image card
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reciter.arabicName,
                        color = Color(0xFFFDE047),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xCC10B981))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("HD Qari", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Surah Arabic Typography
            Text(
                text = surah.nameArabic,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${surah.number}. ${surah.nameEnglish} • ${surah.nameTranslation}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Reciter Badge (clickable)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x33FFFFFF))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                    .clickable { showReciterSheet = true }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReciterAvatar(reciter = reciter, size = 26.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${reciter.name} (${reciter.country})",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Sample Ayah Display
            if (surah.sampleAyahArabic.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x2B000000))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = surah.sampleAyahArabic,
                            color = Color.White,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                        if (surah.sampleAyahEnglish.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = surah.sampleAyahEnglish,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Progress Slider
            var isDragging by remember { mutableStateOf(false) }
            var dragPos by remember { mutableFloatStateOf(0f) }

            val currentSec = ((if (isDragging) dragPos else state.currentPositionMs.toFloat()) / 1000f).toInt()
            val totalSec = (state.durationMs / 1000f).toInt().coerceAtLeast(1)

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = if (isDragging) dragPos else state.currentPositionMs.toFloat(),
                    onValueChange = {
                        isDragging = true
                        dragPos = it
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        playerManager.seekTo(dragPos.toLong())
                    },
                    valueRange = 0f..state.durationMs.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("player_progress_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = String.format("%02d:%02d", currentSec / 60, currentSec % 60),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = String.format("%02d:%02d", totalSec / 60, totalSec % 60),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle toggle
                IconButton(
                    onClick = { playerManager.toggleShuffle() },
                    modifier = Modifier.testTag("shuffle_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (state.isShuffleEnabled) Color(0xFF60A5FA) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(
                    onClick = { playerManager.playPrevious() },
                    modifier = Modifier.size(48.dp).testTag("previous_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Surah",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play / Pause (Large)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { playerManager.togglePlayPause() }
                        .testTag("full_player_play_pause_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isBuffering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = Color.Black,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Next
                IconButton(
                    onClick = { playerManager.playNext() },
                    modifier = Modifier.size(48.dp).testTag("next_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Surah",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat One toggle
                IconButton(
                    onClick = { playerManager.toggleRepeatOne() },
                    modifier = Modifier.testTag("repeat_btn")
                ) {
                    Icon(
                        imageVector = if (state.isRepeatOne) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat",
                        tint = if (state.isRepeatOne) Color(0xFF10B981) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Row: Speed, Ambient, Video Themes, Mushaf, Queue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed
                IconButton(onClick = { showSpeedSheet = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Speed, contentDescription = "Speed", tint = Color.White, modifier = Modifier.size(22.dp))
                        Text("${state.playbackSpeed}x", color = Color.White, fontSize = 10.sp)
                    }
                }

                // Volume / Sound Control
                IconButton(
                    onClick = { showVolumeSheet = true },
                    modifier = Modifier.testTag("action_row_volume_btn")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (state.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = if (state.isMuted) Color(0xFFEF4444) else Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = if (state.isMuted) "Muted" else "${(state.recitationVolume * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }

                // Ambient
                IconButton(onClick = { showAmbientSheet = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.ambientSound.emoji, fontSize = 20.sp)
                        Text(state.ambientSound.name.take(6), color = Color.White, fontSize = 10.sp)
                    }
                }

                // Scenic Background Theme
                IconButton(onClick = { showVideoSheet = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Movie, contentDescription = "Background", tint = Color.White, modifier = Modifier.size(22.dp))
                        Text("Scenic", color = Color.White, fontSize = 10.sp)
                    }
                }

                // Mushaf
                IconButton(onClick = { showMushafSheet = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Mushaf", tint = Color.White, modifier = Modifier.size(22.dp))
                        Text("Mushaf", color = Color.White, fontSize = 10.sp)
                    }
                }

                // Queue
                IconButton(onClick = { showQueueSheet = true }) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Queue", tint = Color.White, modifier = Modifier.size(22.dp))
                        Text("Queue", color = Color.White, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Surah Queue Sheet (with continuous autoplay & custom order!)
        if (showQueueSheet) {
            SurahQueueSheet(
                state = state,
                playerManager = playerManager,
                onDismiss = { showQueueSheet = false }
            )
        }

        // Reciter Selection Sheet (Large Qari Portraits)
        if (showReciterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showReciterSheet = false },
                containerColor = Color(0xFF111827),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)) {
                    Text("Select Reciter (ক্বারী নির্বাচন)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("সুললিত কণ্ঠের বিশিষ্ট ১০ জন বিশ্বখ্যাত ক্বারী", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                        items(ReciterData.reciters) { qari ->
                            val isSelected = qari.id == reciter.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0x3310B981) else Color(0x14FFFFFF))
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF10B981) else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        playerManager.switchReciter(qari)
                                        showReciterSheet = false
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Large 72dp Reciter photo
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                                    ) {
                                        if (qari.localDrawableRes != null) {
                                            Image(
                                                painter = painterResource(id = qari.localDrawableRes),
                                                contentDescription = qari.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else if (qari.avatarUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = qari.avatarUrl,
                                                contentDescription = qari.name,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = qari.name,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = qari.arabicName,
                                            color = Color(0xFFFDE047),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${qari.country} • ${qari.style}",
                                            color = Color.White.copy(alpha = 0.65f),
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = "Active",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ambient Sound Sheet
        if (showAmbientSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAmbientSheet = false },
                containerColor = Color(0xFF111827),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Ambient Nature Sounds", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(AmbientSoundData.sounds) { ambient ->
                            val isSelected = ambient.id == state.ambientSound.id
                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) Color(0x44FFFFFF) else Color(0x1AFFFFFF))
                                    .clickable { playerManager.setAmbientSound(ambient) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(ambient.emoji, fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(ambient.name, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                    if (state.ambientSound.id != "none") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ambient Volume: ${(state.ambientVolume * 100).toInt()}%", color = Color.White, fontSize = 12.sp)
                        Slider(
                            value = state.ambientVolume,
                            onValueChange = { playerManager.setAmbientVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
                        )
                    }
                }
            }
        }

        // Scenic Background Theme Sheet
        if (showVideoSheet) {
            ModalBottomSheet(
                onDismissRequest = { showVideoSheet = false },
                containerColor = Color(0xFF111827),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Scenic Backgrounds (দৃশ্যাবলী)", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("শান্ত শীতল প্রাকৃতিক দৃশ্যপট", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                        items(BackgroundVideoData.themes) { theme ->
                            val isSelected = theme.id == state.selectedVideoThemeId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color(0x33FFFFFF) else Color(0x14FFFFFF))
                                    .clickable {
                                        playerManager.setVideoTheme(theme.id)
                                        showVideoSheet = false
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme.emoji, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(theme.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                    Text(theme.description, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mushaf Viewer Sheet
        if (showMushafSheet) {
            ModalBottomSheet(
                onDismissRequest = { showMushafSheet = false },
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${surah.number}. ${surah.nameArabic} - ${surah.nameEnglish}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${surah.revelationType} • ${surah.versesCount} Ayahs • Juz ${surah.juzNumber}", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x22FFFFFF))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                color = Color.White,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = surah.sampleAyahArabic,
                                color = Color.White,
                                fontSize = 18.sp,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (surah.sampleAyahEnglish.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = surah.sampleAyahEnglish,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Speed Sheet
        if (showSpeedSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSpeedSheet = false },
                containerColor = Color(0xFF111827),
                contentColor = Color.White
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                    Text("Playback Speed", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    val speeds = listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        speeds.forEach { sp ->
                            val isSelected = state.playbackSpeed == sp
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) Color.White else Color(0x22FFFFFF))
                                    .clickable {
                                        playerManager.setSpeed(sp)
                                        showSpeedSheet = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${sp}x",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Volume & Sound Control Sheet
        if (showVolumeSheet) {
            ModalBottomSheet(
                onDismissRequest = { showVolumeSheet = false },
                containerColor = Color(0xFF111827),
                contentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sound & Volume Control",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "সাউন্ড ও ভলিউম নিয়ন্ত্রণ",
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = { playerManager.toggleMute() },
                            modifier = Modifier.testTag("mute_unmute_btn")
                        ) {
                            Icon(
                                imageVector = if (state.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Mute Toggle",
                                tint = if (state.isMuted) Color(0xFFEF4444) else Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto Volume Lock Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2210B981))
                            .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "স্মার্ট ভলিউম লক সক্রিয়: পজ বা প্লে করলে সাউন্ড নিজে থেকে বাড়ে না।",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Recitation Volume Slider
                    Text(
                        text = "Recitation Volume (কুরআন তিলাওয়াত): ${(state.recitationVolume * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = if (state.isMuted) 0f else state.recitationVolume,
                        onValueChange = {
                            if (state.isMuted) playerManager.toggleMute()
                            playerManager.setRecitationVolume(it)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color(0xFF10B981),
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("recitation_volume_slider")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Ambient Nature Volume Slider (if active)
                    if (state.ambientSound.id != "none") {
                        Text(
                            text = "Ambient Nature Volume (${state.ambientSound.name}): ${(state.ambientVolume * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Slider(
                            value = if (state.isMuted) 0f else state.ambientVolume,
                            onValueChange = {
                                if (state.isMuted) playerManager.toggleMute()
                                playerManager.setAmbientVolume(it)
                            },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color(0xFF6366F1),
                                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("ambient_volume_slider")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}
