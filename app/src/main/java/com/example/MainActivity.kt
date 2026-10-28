package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
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
        playerManager = AudioPlayerManager.getInstance(this)
        com.example.data.localization.LanguageManager.initialize(this)

        setContent {
            // Request Notification Permission on Android 13+ for media playback controls
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* permission state */ }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

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
        // Do NOT release player on activity destroy so audio keeps playing in background!
    }
}
