package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import com.example.data.repository.QuranRepository
import com.example.player.AudioPlayerManager
import com.example.player.PlayerUiState
import com.example.ui.components.ReciterAvatar

@Composable
fun HomeScreen(
    playerState: PlayerUiState,
    playerManager: AudioPlayerManager,
    repository: QuranRepository,
    onOpenQueue: () -> Unit,
    language: AppLanguage = AppLanguage.BENGALI,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val favoriteSurahNumbers = remember(favorites) {
        favorites.map { it.surahNumber }.toSet()
    }
    val downloads by repository.completedDownloads.collectAsState(initial = emptyList())
    val downloadedSurahs = remember(downloads, playerState.currentReciter) {
        downloads.filter { it.reciterId == playerState.currentReciter.id && it.isDownloaded }
            .map { it.surahNumber }.toSet()
    }

    val surahs = remember(searchQuery) {
        repository.searchSurahs(searchQuery)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Header with Autoplay Indicator & Queue Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = LocalizedStrings.appName(language),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalizedStrings.appSlogan(language),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
            }

            // Quick Queue Sheet button
            IconButton(
                onClick = onOpenQueue,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x26FFFFFF))
                    .testTag("home_queue_shortcut_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                    contentDescription = "Queue Order",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Side-scrollable Reciters Carousel (Featured Qaris with large images)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = LocalizedStrings.featuredQaris(language),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalizedStrings.swipeQarisTip(language),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
            Text(
                text = LocalizedStrings.recitersCount(ReciterData.reciters.size, language),
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_reciters_carousel"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ReciterData.reciters, key = { it.id }) { reciter ->
                val isSelected = playerState.currentReciter.id == reciter.id
                val isPlayingNow = isSelected && playerState.isPlaying

                Box(
                    modifier = Modifier
                        .width(148.dp)
                        .height(205.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF111827))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color(0xFF10B981) else Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .clickable {
                            playerManager.switchReciter(reciter)
                        }
                        .testTag("reciter_card_${reciter.id}")
                ) {
                    // Reciter Photo
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

                    // Scrim gradient over image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.98f)
                                    ),
                                    startY = 50f
                                )
                            )
                    )

                    // Top badge for Active status
                    if (isSelected) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xE610B981))
                                .padding(horizontal = 7.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isPlayingNow) Icons.Default.GraphicEq else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPlayingNow) "Playing" else "Active",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Bottom info (Arabic, Name, Country)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = reciter.arabicName,
                            color = Color(0xFFFDE047),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reciter.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = reciter.country,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(LocalizedStrings.searchSurah(language), color = Color.White.copy(alpha = 0.45f), fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.5f))
            },
            modifier = Modifier.fillMaxWidth().testTag("home_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                focusedContainerColor = Color(0x1AFFFFFF),
                unfocusedContainerColor = Color(0x14FFFFFF)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Surah List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(surahs, key = { it.number }) { surah ->
                val isCurrentlyPlaying = playerState.currentSurah.number == surah.number && playerState.isPlaying
                val isSelected = playerState.currentSurah.number == surah.number
                val isFav = favoriteSurahNumbers.contains(surah.number)
                var showMenu by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isSelected) Color(0x2EFFFFFF) else Color(0x14FFFFFF))
                        .border(
                            1.dp,
                            if (isSelected) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            playerManager.playSurah(surah, playerState.currentReciter, startPlaying = true)
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Surah Number
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrentlyPlaying) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${surah.number}",
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // English & Translation
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = surah.nameEnglish,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${surah.nameTranslation} • ${LocalizedStrings.getSurahSubtitle(surah, language)}",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        // Arabic Calligraphy
                        Text(
                            text = surah.nameArabic,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Downloaded Offline Badge
                        if (downloadedSurahs.contains(surah.number)) {
                            Icon(
                                imageVector = Icons.Default.DownloadDone,
                                contentDescription = "Offline Available",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        // Options Menu (Play Next, Add to Queue, Download HD)
                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Options",
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Play Next (পরেরটা বাজাও)") },
                                    onClick = {
                                        playerManager.addToQueueNext(surah)
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Add to Queue (তালিকায় যোগ করো)") },
                                    onClick = {
                                        playerManager.addToQueueEnd(surah)
                                        showMenu = false
                                    }
                                )
                                if (downloadedSurahs.contains(surah.number)) {
                                    DropdownMenuItem(
                                        text = { Text("Remove Download (ডাউনলোড মুছুন)", color = Color(0xFFEF4444)) },
                                        onClick = {
                                            coroutineScope.launch {
                                                repository.removeDownload(surah.number, playerState.currentReciter.id)
                                            }
                                            showMenu = false
                                        }
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = { Text("Download HD (এইচডি ডাউনলোড)", color = Color(0xFF10B981)) },
                                        onClick = {
                                            repository.downloadSurah(coroutineScope, surah.number, playerState.currentReciter)
                                            showMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
