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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import com.example.data.model.ReciterData
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranData
import com.example.data.repository.QuranRepository
import com.example.player.AudioPlayerManager
import com.example.player.PlayerUiState
import kotlinx.coroutines.launch

import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings

@Composable
fun PlaylistsScreen(
    playerState: PlayerUiState,
    playerManager: AudioPlayerManager,
    repository: QuranRepository,
    onOpenQueue: () -> Unit,
    language: AppLanguage = AppLanguage.BENGALI,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val playlists by repository.allPlaylists.collectAsState(initial = emptyList())
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val completedDownloads by repository.completedDownloads.collectAsState(initial = emptyList())
    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistTitle by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(LocalizedStrings.playlistsHeader(language), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "Your favorite Surahs & custom queue"
                        AppLanguage.BENGALI -> "আপনার পছন্দের সুরার তালিকা ও প্লে ক্রম"
                        AppLanguage.ARABIC -> "سورك المفضلة وترتيب التشغيل الخاص بك"
                    },
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 12.sp
                )
            }

            IconButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White)
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Playlist", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Open Queue Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x226366F1))
                .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable { onOpenQueue() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(0xFF6366F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Now Playing Queue", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("কাস্টম প্লে ক্রম সাজান (${playerState.playbackQueue.size} টি সুরা)", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Text("Edit ➔", color = Color(0xFF818CF8), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Favorites section
            item {
                Text("Favorites (প্রিয় সুরাসমূহ)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (favorites.isEmpty()) {
                item {
                    Text("No favorites added yet", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp))
                }
            } else {
                items(favorites) { fav ->
                    val surah = QuranData.getByNumber(fav.surahNumber)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x14FFFFFF))
                            .clickable {
                                playerManager.playSurah(surah, playerState.currentReciter, startPlaying = true)
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${surah.number}. ${surah.nameEnglish}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(surah.nameArabic, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Downloaded Offline Surahs section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Downloaded Surahs (অফলাইন সুরাসমূহ)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (completedDownloads.isEmpty()) {
                item {
                    Text("No surahs downloaded for offline yet. You can stream any surah online or download for offline from the Home tab.", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, modifier = Modifier.padding(vertical = 6.dp))
                }
            } else {
                items(completedDownloads) { dl ->
                    val surah = QuranData.getByNumber(dl.surahNumber)
                    val reciter = ReciterData.getById(dl.reciterId)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x14FFFFFF))
                            .clickable {
                                playerManager.playSurah(surah, reciter, startPlaying = true)
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DownloadDone, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${surah.number}. ${surah.nameEnglish}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${reciter.name} • Offline HD", color = Color(0xFF10B981).copy(alpha = 0.8f), fontSize = 11.sp)
                            }
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        repository.removeDownload(dl.surahNumber, dl.reciterId)
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }

            // Custom Playlists section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("My Playlists (কাস্টম প্লেলিস্ট)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (playlists.isEmpty()) {
                item {
                    Text("No custom playlists yet. Tap + above to create one.", color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp))
                }
            } else {
                items(playlists) { pl ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x14FFFFFF))
                            .clickable { onOpenQueue() }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pl.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(if (pl.description.isNotEmpty()) pl.description else "Custom Playlist", color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("New Playlist", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = newPlaylistTitle,
                        onValueChange = { newPlaylistTitle = it },
                        placeholder = { Text("Playlist name...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newPlaylistTitle.isNotBlank()) {
                                coroutineScope.launch {
                                    repository.createPlaylist(newPlaylistTitle.trim())
                                    newPlaylistTitle = ""
                                    showCreateDialog = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}
