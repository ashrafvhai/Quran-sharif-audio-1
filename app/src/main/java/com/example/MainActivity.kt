package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.data.local.QuranDatabase
import com.example.data.repository.QuranRepository
import com.example.player.AudioPlayerManager
import com.example.ui.MainScreen
import com.example.ui.theme.EasyQuranifyTheme

class MainActivity : ComponentActivity() {
    private lateinit var playerManager: AudioPlayerManager
    private lateinit var repository: QuranRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = QuranDatabase.getDatabase(this)
        val quranDao = database.quranDao()
        repository = QuranRepository(quranDao, applicationContext)
        playerManager = AudioPlayerManager(this, lifecycleScope)

        setContent {
            EasyQuranifyTheme {
                MainScreen(
                    playerManager = playerManager,
                    repository = repository
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::playerManager.isInitialized) {
            playerManager.release()
        }
    }
}
