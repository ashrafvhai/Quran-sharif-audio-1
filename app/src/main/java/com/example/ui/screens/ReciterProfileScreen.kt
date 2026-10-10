package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.ui.components.ReciterAvatar
import com.example.player.PlayerUiState
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ReciterProfileScreen(
    reciter: Reciter,
    playerState: PlayerUiState,
    isFavoriteReciter: Boolean,
    onToggleFavoriteReciter: () -> Unit,
    onPlaySurah: (Surah, Reciter) -> Unit,
    onPlayAll: () -> Unit,
    downloadedSurahs: Set<Int>,
    downloadingSurahs: Set<Int>,
    onDownloadClick: (Surah) -> Unit,
    onViewMushaf: (Surah) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var isBioExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP NAVIGATION BAR
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, CircleShape)
                        .testTag("reciter_profile_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Reciter Profile",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onToggleFavoriteReciter,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceCardBorder, CircleShape)
                        .testTag("reciter_profile_favorite")
                ) {
                    Icon(
                        imageVector = if (isFavoriteReciter) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavoriteReciter) GoldPrimary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // PROFILE HERO SECTION
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Large Avatar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, GoldPrimary, CircleShape)
                ) {
                    ReciterAvatar(
                        reciter = reciter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = reciter.name,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = reciter.arabicName,
                    color = GoldPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${reciter.country} • ${reciter.style} • ${reciter.listenerCount}",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Play" Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(GoldPrimary)
                            .clickable { onPlayAll() }
                            .padding(horizontal = 28.dp, vertical = 12.dp)
                            .testTag("reciter_play_all"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color(0xFF090A0E),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Play All Surahs",
                            color = Color(0xFF090A0E),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // SHORT BIO WITH "MORE" EXPAND
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .animateContentSize()
            ) {
                Column {
                    Text(
                        text = "About",
                        color = GoldPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = reciter.bio,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        maxLines = if (isBioExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBioExpanded) "Show Less" else "More...",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { isBioExpanded = !isBioExpanded }
                            .testTag("reciter_bio_toggle")
                    )
                }
            }
        }

        // SUBHEADER: 114 SURAHS RECORDED
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "114 surahs recorded",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Full Murattal",
                    color = GoldPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 114 SURAHS LIST
        items(QuranData.surahs, key = { it.number }) { surah ->
            val isPlayingThis = playerState.isPlaying &&
                playerState.currentSurah.number == surah.number &&
                playerState.currentReciter.id == reciter.id
            val isDownloaded = downloadedSurahs.contains(surah.number)
            val isDownloading = downloadingSurahs.contains(surah.number)

            ReciterSurahItemRow(
                surah = surah,
                isPlaying = isPlayingThis,
                isDownloaded = isDownloaded,
                isDownloading = isDownloading,
                onRowClick = { onPlaySurah(surah, reciter) },
                onDownloadClick = { onDownloadClick(surah) },
                onViewMushaf = { onViewMushaf(surah) }
            )
        }
    }
}

@Composable
private fun ReciterSurahItemRow(
    surah: Surah,
    isPlaying: Boolean,
    isDownloaded: Boolean,
    isDownloading: Boolean,
    onRowClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onViewMushaf: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPlaying) GoldPrimary.copy(alpha = 0.12f) else SurfaceCard)
            .border(
                1.dp,
                if (isPlaying) GoldPrimary.copy(alpha = 0.8f) else SurfaceCardBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable { onRowClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("reciter_surah_${surah.number}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) GoldPrimary else Color(0x18FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                if (isPlaying) {
                    Icon(
                        imageVector = Icons.Filled.GraphicEq,
                        contentDescription = "Playing",
                        tint = Color(0xFF090A0E),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${surah.number}",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Surah English Name & Meaning
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = surah.nameEnglish,
                    color = if (isPlaying) GoldPrimary else TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${surah.nameTranslation} • ${surah.versesCount} verses",
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Arabic Name
            Text(
                text = surah.nameArabic,
                color = if (isPlaying) GoldPrimary else TextPrimary.copy(alpha = 0.85f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            // Download Icon
            if (isDownloading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(6.dp),
                    color = GoldPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onDownloadClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Filled.DownloadDone else Icons.Outlined.FileDownload,
                        contentDescription = "Download Surah",
                        tint = if (isDownloaded) GoldPrimary else TextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            // 3-dot Menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(Color(0xFF1B1D26))
                ) {
                    DropdownMenuItem(
                        text = { Text("Play with Ambient Mix", color = TextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onRowClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("View Arabic Scripture / Mushaf", color = TextPrimary) },
                        onClick = {
                            menuExpanded = false
                            onViewMushaf()
                        }
                    )
                }
            }
        }
    }
}
