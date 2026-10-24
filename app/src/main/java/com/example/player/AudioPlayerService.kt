package com.example.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AudioPlayerService : Service() {
    companion object {
        const val CHANNEL_ID = "quran_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.player.START"
        const val ACTION_PLAY_PAUSE = "com.example.player.PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.player.NEXT"
        const val ACTION_PREV = "com.example.player.PREV"
        const val ACTION_STOP = "com.example.player.STOP"

        fun startService(context: Context) {
            val intent = Intent(context, AudioPlayerService::class.java).apply {
                action = ACTION_START
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                android.util.Log.e("AudioPlayerService", "Failed to start service", e)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, AudioPlayerService::class.java).apply {
                action = ACTION_STOP
            }
            try {
                context.startService(intent)
            } catch (e: Exception) {
                android.util.Log.e("AudioPlayerService", "Failed to stop service", e)
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var stateJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observeState()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Recitation Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notification with playback controls for background Quran recitation"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val playerManager = AudioPlayerManager.getInstance(this)
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                playerManager.togglePlayPause()
            }
            ACTION_NEXT -> {
                playerManager.playNext()
            }
            ACTION_PREV -> {
                playerManager.playPrevious()
            }
            ACTION_STOP -> {
                if (playerManager.uiState.value.isPlaying) {
                    playerManager.togglePlayPause()
                }
                stopForegroundCompat()
                stopSelf()
                return START_NOT_STICKY
            }
        }

        updateNotification(playerManager.uiState.value)
        return START_STICKY
    }

    private fun observeState() {
        stateJob?.cancel()
        stateJob = serviceScope.launch {
            val playerManager = AudioPlayerManager.getInstance(this@AudioPlayerService)
            playerManager.uiState.collectLatest { state ->
                updateNotification(state)
            }
        }
    }

    private fun updateNotification(state: PlayerUiState) {
        val surah = state.currentSurah
        val reciter = state.currentReciter
        val isPlaying = state.isPlaying

        // Tap to open app
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Actions
        val prevIntent = Intent(this, AudioPlayerService::class.java).apply { action = ACTION_PREV }
        val prevPending = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playPauseIntent = Intent(this, AudioPlayerService::class.java).apply { action = ACTION_PLAY_PAUSE }
        val playPausePending = PendingIntent.getService(this, 2, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, AudioPlayerService::class.java).apply { action = ACTION_NEXT }
        val nextPending = PendingIntent.getService(this, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val stopIntent = Intent(this, AudioPlayerService::class.java).apply { action = ACTION_STOP }
        val stopPending = PendingIntent.getService(this, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playPauseIcon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val playPauseText = if (isPlaying) "Pause" else "Play"

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("${surah.number}. ${surah.nameEnglish} • ${surah.nameArabic}")
            .setContentText("${reciter.name} (HD Recitation)")
            .setSubText("Easy Quranify")
            .setContentIntent(openAppPendingIntent)
            .setOngoing(isPlaying)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(android.R.drawable.ic_media_previous, "Previous", prevPending)
            .addAction(playPauseIcon, playPauseText, playPausePending)
            .addAction(android.R.drawable.ic_media_next, "Next", nextPending)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Close", stopPending)
            .build()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Exception) {
            android.util.Log.e("AudioPlayerService", "Error posting foreground notification", e)
        }
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stateJob?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
