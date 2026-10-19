package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.data.repository.QuranRepository
import com.example.player.AudioPlayerManager
import com.example.ui.components.AppTab
import com.example.ui.components.BottomNavBar
import com.example.ui.components.MiniPlayer
import com.example.ui.components.SurahQueueSheet
import com.example.ui.screens.FullScreenPlayer
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.screens.SettingsScreen

import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    playerManager: AudioPlayerManager,
    repository: QuranRepository
) {
    val playerState by playerManager.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }
    var isPlayerExpanded by remember { mutableStateOf(false) }
    var showQueueSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                AppTab.HOME -> HomeScreen(
                    playerState = playerState,
                    playerManager = playerManager,
                    repository = repository,
                    onOpenQueue = { showQueueSheet = true }
                )
                AppTab.RECITERS -> RecitersScreen(
                    playerState = playerState,
                    playerManager = playerManager
                )
                AppTab.PLAYLISTS -> PlaylistsScreen(
                    playerState = playerState,
                    playerManager = playerManager,
                    repository = repository,
                    onOpenQueue = { showQueueSheet = true }
                )
                AppTab.SETTINGS -> SettingsScreen(
                    playerState = playerState,
                    playerManager = playerManager
                )
            }
        }

        // Bottom Controls: MiniPlayer + BottomNavBar
        Column(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            // MiniPlayer
            MiniPlayer(
                state = playerState,
                onTogglePlayPause = { playerManager.togglePlayPause() },
                onExpandPlayer = { isPlayerExpanded = true },
                onNext = { playerManager.playNext() },
                onOpenQueue = { showQueueSheet = true }
            )

            // Bottom Navigation Bar
            BottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Full Screen Player Modal
        AnimatedVisibility(
            visible = isPlayerExpanded,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            FullScreenPlayer(
                state = playerState,
                playerManager = playerManager,
                onDismiss = { isPlayerExpanded = false }
            )
        }

        // Global Queue Sheet
        if (showQueueSheet) {
            SurahQueueSheet(
                state = playerState,
                playerManager = playerManager,
                onDismiss = { showQueueSheet = false }
            )
        }
    }
}
