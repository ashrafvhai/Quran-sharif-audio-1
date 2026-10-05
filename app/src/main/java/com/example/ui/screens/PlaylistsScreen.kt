package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.PlaylistEntity
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlaylistsScreen(
    favorites: List<FavoriteEntity>,
    downloads: List<DownloadEntity>,
    playlists: List<PlaylistEntity>,
    onPlaySurah: (Surah, Reciter) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onRemoveDownload: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableStateOf("Favorites") }
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistTitle by remember { mutableStateOf("") }

    val sections = listOf("Favorites", "Downloads", "Custom")

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
    ) {
        // TOP HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Library & Playlists",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            if (selectedSection == "Custom") {
                IconButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .testTag("create_playlist_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "New Playlist",
                        tint = Color(0xFF090A0E),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // SEGMENTED TABS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                .padding(4.dp)
        ) {
            sections.forEach { section ->
                val isSelected = selectedSection == section
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) GoldPrimary else Color.Transparent)
                        .clickable { selectedSection = section }
                        .padding(vertical = 9.dp)
                        .testTag("library_tab_${section.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = section,
                        color = if (isSelected) Color(0xFF090A0E) else TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // CONTENT SECTION
        when (selectedSection) {
            "Favorites" -> {
                if (favorites.isEmpty()) {
                    EmptyLibraryState(
                        icon = "⭐",
                        title = "No Favorites Yet",
                        message = "Star any Surah from the player or reciter profiles to keep your favorite recitations here."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(favorites, key = { "${it.surahNumber}_${it.reciterId}" }) { fav ->
                            val surah = QuranData.surahs.find { it.number == fav.surahNumber } ?: QuranData.surahs.first()
                            val reciter = ReciterData.getById(fav.reciterId)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                                    .clickable { onPlaySurah(surah, reciter) }
                                    .padding(14.dp)
                                    .testTag("fav_item_${surah.number}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Starred",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${surah.number}. ${surah.nameEnglish}",
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${reciter.name} • ${surah.nameTranslation}",
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Text(
                                        text = surah.nameArabic,
                                        color = GoldPrimary,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "Downloads" -> {
                if (downloads.isEmpty()) {
                    EmptyLibraryState(
                        icon = "📥",
                        title = "No Offline Downloads",
                        message = "Tap the download icon on any Surah in a reciter's catalog to save it for offline listening."
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(downloads, key = { "${it.surahNumber}_${it.reciterId}" }) { dl ->
                            val surah = QuranData.surahs.find { it.number == dl.surahNumber } ?: QuranData.surahs.first()
                            val reciter = ReciterData.getById(dl.reciterId)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(SurfaceCard)
                                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                                    .clickable { onPlaySurah(surah, reciter) }
                                    .padding(14.dp)
                                    .testTag("download_item_${surah.number}")
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DownloadDone,
                                        contentDescription = "Downloaded",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${surah.number}. ${surah.nameEnglish}",
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${reciter.name} • Offline Available",
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRemoveDownload(surah.number, reciter.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Delete,
                                            contentDescription = "Delete download",
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "Custom" -> {
                val allCustom = remember(playlists) {
                    if (playlists.isEmpty()) {
                        listOf(
                            PlaylistEntity("pl_sleep", "Sleep & Night Serenity", "Calming recitations with rain ambience"),
                            PlaylistEntity("pl_morning", "Morning Barakah", "Uplifting recitations for starting the day with focus"),
                            PlaylistEntity("pl_tahajjud", "Tahajjud Reflection", "Deep and poignant night prayers")
                        )
                    } else playlists
                }

                LazyColumn(
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allCustom, key = { it.id }) { pl ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(SurfaceCard)
                                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    val fatihah = QuranData.surahs.first()
                                    val reciter = ReciterData.reciters.first()
                                    onPlaySurah(fatihah, reciter)
                                }
                                .padding(16.dp)
                                .testTag("playlist_item_${pl.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GoldPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Folder,
                                        contentDescription = "Playlist",
                                        tint = GoldPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pl.title,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = pl.description.ifBlank { "Custom Quran Playlist" },
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Play Playlist",
                                    tint = GoldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // CREATE PLAYLIST DIALOG
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = Color(0xFF161822),
            title = {
                Text(
                    text = "Create Playlist",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter a title for your new recitation collection:",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newPlaylistTitle,
                        onValueChange = { newPlaylistTitle = it },
                        placeholder = { Text("e.g. Daily Quran Focus", color = TextMuted) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_playlist_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPlaylistTitle.isNotBlank()) {
                            onCreatePlaylist(newPlaylistTitle.trim())
                            newPlaylistTitle = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Create", color = Color(0xFF090A0E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun EmptyLibraryState(
    icon: String,
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            fontSize = 44.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            color = TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
    }
}
