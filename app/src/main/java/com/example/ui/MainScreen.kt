package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.local.QuranDatabase
import com.example.data.model.AmbientSound
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import com.example.data.repository.QuranRepository
import com.example.player.AudioPlayerManager
import com.example.ui.components.AmbientSoundSheet
import com.example.ui.components.AppTab
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.GlobalSearchSheet
import com.example.ui.components.MiniPlayer
import com.example.ui.components.MushafViewerSheet
import com.example.ui.components.SleepTimerDialog
import com.example.ui.components.SurahQueueSheet
import com.example.ui.screens.FullScreenPlayer
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.ReciterProfileScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Initialize Database & Repository
    val database = remember { QuranDatabase.getDatabase(context) }
    val repository = remember { QuranRepository(database.quranDao()) }

    // Initialize Audio Player Manager
    val playerManager = remember { AudioPlayerManager(context, coroutineScope) }
    DisposableEffect(Unit) {
        onDispose { playerManager.release() }
    }

    val playerState by playerManager.uiState.collectAsState()
    val favorites by repository.allFavorites.collectAsState(initial = emptyList())
    val downloads by repository.completedDownloads.collectAsState(initial = emptyList())
    val playlists by repository.allPlaylists.collectAsState(initial = emptyList())

    // Navigation & Sheet States
    var currentTab by remember { mutableStateOf(AppTab.HOME) }
    var selectedReciterForProfile by remember { mutableStateOf<Reciter?>(null) }
    var isPlayerExpanded by remember { mutableStateOf(false) }

    var showAmbientSheet by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }
    var showMushafSheet by remember { mutableStateOf(false) }
    var showSleepTimerSheet by remember { mutableStateOf(false) }
    var showGlobalSearchSheet by remember { mutableStateOf(false) }

    var mushafSurahTarget by remember { mutableStateOf(playerState.currentSurah) }

    // Track downloading simulation states
    var downloadingSurahs by remember { mutableStateOf(setOf<Int>()) }

    val downloadedNumbers = remember(downloads) {
        downloads.map { it.surahNumber }.toSet()
    }

    val isCurrentTrackFavorite = remember(favorites, playerState.currentSurah, playerState.currentReciter) {
        favorites.any { it.surahNumber == playerState.currentSurah.number && it.reciterId == playerState.currentReciter.id }
    }

    val isReciterFavorite = remember(favorites, selectedReciterForProfile) {
        val reciter = selectedReciterForProfile
        reciter != null && favorites.any { it.reciterId == reciter.id }
    }

    // Helper functions
    val playTrack = { surah: Surah, reciter: Reciter, ambient: AmbientSound? ->
        try {
            if (ambient != null && ambient.id != playerState.ambientSound.id) {
                playerManager.setAmbientSound(ambient)
            }
            playerManager.playSurah(surah, reciter, startPlaying = true)
            coroutineScope.launch {
                try {
                    repository.recordHistory(surah.number, reciter.id, 0L, 0L)
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }

    val handleDownloadClick = { surah: Surah ->
        val reciterId = playerState.currentReciter.id
        if (downloadedNumbers.contains(surah.number)) {
            coroutineScope.launch {
                repository.removeDownload(surah.number, reciterId)
            }
        } else {
            downloadingSurahs = downloadingSurahs + surah.number
            repository.startDownloadSimulation(coroutineScope, surah.number, reciterId)
            coroutineScope.launch {
                kotlinx.coroutines.delay(1300L)
                downloadingSurahs = downloadingSurahs - surah.number
            }
        }
    }

    // Main Scaffold layout
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090A0E))
    ) {
        Scaffold(
            bottomBar = {
                Column {
                    // Floating MiniPlayer docked right above bottom navigation bar
                    if (!isPlayerExpanded) {
                        MiniPlayer(
                            state = playerState,
                            onExpand = { isPlayerExpanded = true },
                            onTogglePlay = { playerManager.togglePlayPause() },
                            onNext = { playerManager.playNext() }
                        )
                    }

                    BottomNavigationBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            currentTab = tab
                            selectedReciterForProfile = null
                        }
                    )
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                // If a Reciter Profile is active, show it
                val activeProfile = selectedReciterForProfile
                if (activeProfile != null) {
                    ReciterProfileScreen(
                        reciter = activeProfile,
                        playerState = playerState,
                        isFavoriteReciter = isReciterFavorite,
                        onToggleFavoriteReciter = {
                            coroutineScope.launch {
                                repository.toggleFavorite(1, activeProfile.id, isReciterFavorite)
                            }
                        },
                        onPlaySurah = { surah, rec ->
                            playTrack(surah, rec, null)
                        },
                        onPlayAll = {
                            playTrack(QuranData.surahs.first(), activeProfile, null)
                        },
                        downloadedSurahs = downloadedNumbers,
                        downloadingSurahs = downloadingSurahs,
                        onDownloadClick = { surah -> handleDownloadClick(surah) },
                        onViewMushaf = { surah ->
                            mushafSurahTarget = surah
                            showMushafSheet = true
                        },
                        onBack = { selectedReciterForProfile = null }
                    )
                } else {
                    // Tab content
                    when (currentTab) {
                        AppTab.HOME -> {
                            HomeScreen(
                                playerState = playerState,
                                onPlaySurah = { surah, reciter, ambient ->
                                    playTrack(surah, reciter, ambient)
                                },
                                onSelectReciter = { reciter ->
                                    selectedReciterForProfile = reciter
                                },
                                onOpenSearch = { showGlobalSearchSheet = true }
                            )
                        }

                        AppTab.RECITERS -> {
                            RecitersScreen(
                                onSelectReciter = { reciter ->
                                    selectedReciterForProfile = reciter
                                },
                                onQuickPlayReciter = { reciter ->
                                    playTrack(playerState.currentSurah, reciter, null)
                                }
                            )
                        }

                        AppTab.PLAYLISTS -> {
                            PlaylistsScreen(
                                favorites = favorites,
                                downloads = downloads,
                                playlists = playlists,
                                onPlaySurah = { surah, reciter ->
                                    playTrack(surah, reciter, null)
                                },
                                onCreatePlaylist = { title ->
                                    coroutineScope.launch {
                                        repository.createPlaylist(title)
                                    }
                                },
                                onRemoveDownload = { surahNum, recId ->
                                    coroutineScope.launch {
                                        repository.removeDownload(surahNum, recId)
                                    }
                                }
                            )
                        }

                        AppTab.SETTINGS -> {
                            SettingsScreen()
                        }
                    }
                }
            }
        }

        // FULL SCREEN PLAYER SLIDE-OVER
        AnimatedVisibility(
            visible = isPlayerExpanded,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            BackHandler(enabled = isPlayerExpanded) {
                isPlayerExpanded = false
            }

            FullScreenPlayer(
                state = playerState,
                isFavorite = isCurrentTrackFavorite,
                onToggleFavorite = {
                    coroutineScope.launch {
                        repository.toggleFavorite(
                            playerState.currentSurah.number,
                            playerState.currentReciter.id,
                            isCurrentTrackFavorite
                        )
                    }
                },
                onTogglePlay = { playerManager.togglePlayPause() },
                onSeekTo = { pos -> playerManager.seekTo(pos) },
                onSkip15Forward = { playerManager.skip15Forward() },
                onSkip15Backward = { playerManager.skip15Backward() },
                onNext = { playerManager.playNext() },
                onPrevious = { playerManager.playPrevious() },
                onChangeSpeed = { speed -> playerManager.setSpeed(speed) },
                onOpenAmbientSheet = { showAmbientSheet = true },
                onOpenQueueSheet = { showQueueSheet = true },
                onOpenMushafSheet = {
                    mushafSurahTarget = playerState.currentSurah
                    showMushafSheet = true
                },
                onOpenSleepTimer = { showSleepTimerSheet = true },
                onToggleMute = { playerManager.toggleMute() },
                onMinimize = { isPlayerExpanded = false }
            )
        }

        // BOTTOM SHEETS
        if (showAmbientSheet) {
            AmbientSoundSheet(
                selectedSound = playerState.ambientSound,
                ambientVolume = playerState.ambientVolume,
                onSoundSelected = { sound ->
                    playerManager.setAmbientSound(sound)
                },
                onVolumeChange = { vol ->
                    playerManager.setAmbientVolume(vol)
                },
                onDismiss = { showAmbientSheet = false }
            )
        }

        if (showQueueSheet) {
            SurahQueueSheet(
                currentSurah = playerState.currentSurah,
                isPlaying = playerState.isPlaying,
                onSurahSelected = { surah ->
                    playTrack(surah, playerState.currentReciter, null)
                    showQueueSheet = false
                },
                downloadedSurahs = downloadedNumbers,
                downloadingSurahs = downloadingSurahs,
                onDownloadClick = { surah -> handleDownloadClick(surah) },
                onDismiss = { showQueueSheet = false }
            )
        }

        if (showMushafSheet) {
            MushafViewerSheet(
                surah = mushafSurahTarget,
                onDismiss = { showMushafSheet = false }
            )
        }

        if (showSleepTimerSheet) {
            SleepTimerDialog(
                currentMinutes = playerState.sleepTimerMinutes,
                remainingSeconds = playerState.sleepTimerSecondsRemaining,
                onSetTimer = { minutes ->
                    playerManager.setSleepTimer(minutes)
                },
                onDismiss = { showSleepTimerSheet = false }
            )
        }

        if (showGlobalSearchSheet) {
            GlobalSearchSheet(
                onSelectSurah = { surah ->
                    playTrack(surah, playerState.currentReciter, null)
                },
                onSelectReciter = { reciter ->
                    selectedReciterForProfile = reciter
                },
                onDismiss = { showGlobalSearchSheet = false }
            )
        }
    }
}
