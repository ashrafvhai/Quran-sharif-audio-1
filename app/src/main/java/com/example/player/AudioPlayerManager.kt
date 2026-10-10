package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import com.example.data.model.AmbientSound
import com.example.data.model.AmbientSoundData
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentSurah: Surah = QuranData.surahs.first(),
    val currentReciter: Reciter = ReciterData.reciters.first(),
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 1000L,
    val playbackSpeed: Float = 1.0f,
    val ambientSound: AmbientSound = AmbientSoundData.getById("none"),
    val ambientVolume: Float = 0.5f,
    val recitationVolume: Float = 1.0f,
    val sleepTimerMinutes: Int? = null,
    val sleepTimerSecondsRemaining: Int? = null,
    val errorMessage: String? = null,
    val isMuted: Boolean = false,
    val selectedVideoThemeId: String = "auto_mix"
)

class AudioPlayerManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val TAG = "AudioPlayerManager"

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    @Volatile
    private var recitationPlayer: MediaPlayer? = null
    @Volatile
    private var isRecitationPrepared = false

    @Volatile
    private var ambientPlayer: MediaPlayer? = null
    @Volatile
    private var isAmbientPrepared = false

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var usingFallback = false

    fun playSurah(surah: Surah, reciter: Reciter, startPlaying: Boolean = true) {
        usingFallback = false
        _uiState.update {
            it.copy(
                currentSurah = surah,
                currentReciter = reciter,
                isBuffering = true,
                errorMessage = null,
                currentPositionMs = 0L,
                durationMs = 1000L
            )
        }

        prepareRecitation(surah, reciter, useFallback = false, autoPlay = startPlaying)
    }

    fun switchReciter(reciter: Reciter) {
        val currentSurah = _uiState.value.currentSurah
        val shouldPlay = _uiState.value.isPlaying
        playSurah(currentSurah, reciter, startPlaying = shouldPlay)
    }

    fun setVideoTheme(themeId: String) {
        _uiState.update { it.copy(selectedVideoThemeId = themeId) }
    }

    private fun requestSystemAudioFocus() {
        try {
            audioManager?.let { am ->
                // Ensure media stream has healthy audible volume
                val currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                if (currentVol < (maxVol * 0.5f) && maxVol > 0) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, (maxVol * 0.85f).toInt(), 0)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val playbackAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    val focusReq = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(playbackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener { _ ->
                            applyRecitationVolume()
                        }
                        .build()
                    audioFocusRequest = focusReq
                    am.requestAudioFocus(focusReq)
                } else {
                    @Suppress("DEPRECATION")
                    am.requestAudioFocus(
                        { _ ->
                            applyRecitationVolume()
                        },
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Audio focus request issue: ${e.message}")
        }
    }

    private fun safeReleaseRecitationPlayer() {
        isRecitationPrepared = false
        val player = recitationPlayer
        recitationPlayer = null
        if (player != null) {
            try {
                player.setOnPreparedListener(null)
                player.setOnCompletionListener(null)
                player.setOnErrorListener(null)
                player.reset()
                player.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error safely releasing recitation player", e)
            }
        }
    }

    private fun safeReleaseAmbientPlayer() {
        isAmbientPrepared = false
        val player = ambientPlayer
        ambientPlayer = null
        if (player != null) {
            try {
                player.setOnPreparedListener(null)
                player.setOnCompletionListener(null)
                player.setOnErrorListener(null)
                player.reset()
                player.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error safely releasing ambient player", e)
            }
        }
    }

    private fun prepareRecitation(surah: Surah, reciter: Reciter, useFallback: Boolean, autoPlay: Boolean) {
        stopProgressTracking()
        safeReleaseRecitationPlayer()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val url = if (useFallback) reciter.getFallbackAudioUrl(surah.number) else reciter.getAudioUrl(surah.number)
                Log.d(TAG, "Preparing recitation URL: $url")

                val player = MediaPlayer().apply {
                    // Set MUSIC audio attributes so sound routes through media speaker clearly
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                            .build()
                    )
                    setDataSource(url)

                    // Ensure high volume initially
                    setVolume(1.0f, 1.0f)

                    setOnPreparedListener { mp ->
                        isRecitationPrepared = true
                        try {
                            val duration = mp.duration.toLong().coerceAtLeast(1000L)
                            _uiState.update {
                                it.copy(
                                    isBuffering = false,
                                    durationMs = duration
                                )
                            }
                            applyPlaybackSpeed(_uiState.value.playbackSpeed)
                            applyRecitationVolume()

                            if (autoPlay) {
                                try {
                                    requestSystemAudioFocus()
                                    mp.start()
                                    _uiState.update { it.copy(isPlaying = true) }
                                    startProgressTracking()
                                    ensureAmbientPlaying()
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error starting MediaPlayer onPrepared", e)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onPrepared callback", e)
                        }
                    }

                    setOnCompletionListener {
                        try {
                            _uiState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
                            stopProgressTracking()
                            if (_uiState.value.sleepTimerMinutes == -1) {
                                clearSleepTimer()
                            } else {
                                playNext()
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in onCompletion callback", e)
                        }
                    }

                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "Recitation error: what=$what extra=$extra")
                        isRecitationPrepared = false
                        coroutineScope.launch {
                            if (!usingFallback) {
                                usingFallback = true
                                prepareRecitation(surah, reciter, useFallback = true, autoPlay = true)
                            } else {
                                _uiState.update {
                                    it.copy(
                                        isBuffering = false,
                                        isPlaying = false,
                                        errorMessage = "Recitation stream unavailable. Please check your internet connection."
                                    )
                                }
                            }
                        }
                        true
                    }

                    prepareAsync()
                }
                recitationPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize recitation player", e)
                _uiState.update {
                    it.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = "Cannot connect to recitation server: ${e.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        try {
            val player = recitationPlayer
            if (player == null || !isRecitationPrepared) {
                playSurah(_uiState.value.currentSurah, _uiState.value.currentReciter, startPlaying = true)
                return
            }

            if (_uiState.value.isPlaying) {
                try {
                    if (player.isPlaying) {
                        player.pause()
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Error pausing recitation player", e)
                }
                _uiState.update { it.copy(isPlaying = false) }
                stopProgressTracking()
                pauseAmbient()
            } else {
                try {
                    requestSystemAudioFocus()
                    applyRecitationVolume()
                    player.start()
                    _uiState.update { it.copy(isPlaying = true) }
                    startProgressTracking()
                    ensureAmbientPlaying()
                } catch (e: Exception) {
                    Log.w(TAG, "Player failed to start, re-preparing...", e)
                    playSurah(_uiState.value.currentSurah, _uiState.value.currentReciter, startPlaying = true)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in togglePlayPause", e)
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            recitationPlayer?.let { player ->
                if (isRecitationPrepared) {
                    val maxDur = _uiState.value.durationMs
                    val clamped = positionMs.coerceIn(0L, maxDur)
                    player.seekTo(clamped.toInt())
                    _uiState.update { it.copy(currentPositionMs = clamped) }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Seek error", e)
        }
    }

    fun skip15Forward() {
        try {
            recitationPlayer?.let { player ->
                if (isRecitationPrepared) {
                    val current = try { player.currentPosition } catch (_: Exception) { _uiState.value.currentPositionMs.toInt() }
                    val maxDur = try { player.duration } catch (_: Exception) { _uiState.value.durationMs.toInt() }
                    val newPos = (current + 15000).coerceAtMost(maxDur).toLong()
                    seekTo(newPos)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "skip15Forward error", e)
        }
    }

    fun skip15Backward() {
        try {
            recitationPlayer?.let { player ->
                if (isRecitationPrepared) {
                    val current = try { player.currentPosition } catch (_: Exception) { _uiState.value.currentPositionMs.toInt() }
                    val newPos = (current - 15000).coerceAtLeast(0).toLong()
                    seekTo(newPos)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "skip15Backward error", e)
        }
    }

    fun playNext() {
        try {
            val currentNum = _uiState.value.currentSurah.number
            val nextNum = if (currentNum >= 114) 1 else currentNum + 1
            val nextSurah = QuranData.surahs.find { it.number == nextNum } ?: QuranData.surahs.first()
            playSurah(nextSurah, _uiState.value.currentReciter, startPlaying = true)
        } catch (e: Exception) {
            Log.e(TAG, "playNext error", e)
        }
    }

    fun playPrevious() {
        try {
            val currentNum = _uiState.value.currentSurah.number
            val prevNum = if (currentNum <= 1) 114 else currentNum - 1
            val prevSurah = QuranData.surahs.find { it.number == prevNum } ?: QuranData.surahs.last()
            playSurah(prevSurah, _uiState.value.currentReciter, startPlaying = true)
        } catch (e: Exception) {
            Log.e(TAG, "playPrevious error", e)
        }
    }

    fun setSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isRecitationPrepared) {
            try {
                recitationPlayer?.let {
                    val params = it.playbackParams
                    params.speed = speed
                    it.playbackParams = params
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not set playback speed", e)
            }
        }
    }

    fun setAmbientSound(ambient: AmbientSound) {
        _uiState.update { it.copy(ambientSound = ambient) }

        if (ambient.id == "none" || ambient.audioUrl.isBlank()) {
            stopAmbient()
            return
        }

        safeReleaseAmbientPlayer()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                val player = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    setDataSource(ambient.audioUrl)
                    isLooping = true
                    val vol = _uiState.value.ambientVolume
                    setVolume(vol, vol)

                    setOnPreparedListener {
                        isAmbientPrepared = true
                        try {
                            if (_uiState.value.isPlaying) {
                                it.start()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Ambient start error", e)
                        }
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.w(TAG, "Ambient playback issue: what=$what extra=$extra")
                        isAmbientPrepared = false
                        true
                    }
                    prepareAsync()
                }
                ambientPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ambient sound", e)
            }
        }
    }

    fun setAmbientVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(ambientVolume = clamped) }
        try {
            ambientPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting ambient volume", e)
        }
    }

    fun setRecitationVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(recitationVolume = clamped) }
        applyRecitationVolume()
    }

    private fun applyRecitationVolume() {
        val vol = if (_uiState.value.isMuted) 0f else _uiState.value.recitationVolume.coerceIn(0.1f, 1.0f)
        try {
            recitationPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting recitation volume", e)
        }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        applyRecitationVolume()
    }

    private fun ensureAmbientPlaying() {
        try {
            if (_uiState.value.ambientSound.id != "none" && isAmbientPrepared) {
                ambientPlayer?.start()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Ambient play error", e)
        }
    }

    private fun pauseAmbient() {
        try {
            if (isAmbientPrepared) {
                ambientPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Ambient pause error", e)
        }
    }

    private fun stopAmbient() {
        safeReleaseAmbientPlayer()
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null) {
            _uiState.update { it.copy(sleepTimerMinutes = null, sleepTimerSecondsRemaining = null) }
            return
        }

        if (minutes == -1) {
            _uiState.update { it.copy(sleepTimerMinutes = -1, sleepTimerSecondsRemaining = null) }
            return
        }

        val totalSeconds = minutes * 60
        _uiState.update { it.copy(sleepTimerMinutes = minutes, sleepTimerSecondsRemaining = totalSeconds) }

        sleepTimerJob = coroutineScope.launch(Dispatchers.Main) {
            var remaining = totalSeconds
            while (isActive && remaining > 0) {
                delay(1000L)
                remaining--
                _uiState.update { it.copy(sleepTimerSecondsRemaining = remaining) }
            }
            try {
                if (recitationPlayer?.isPlaying == true) {
                    recitationPlayer?.pause()
                }
            } catch (_: Exception) {}
            pauseAmbient()
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    sleepTimerMinutes = null,
                    sleepTimerSecondsRemaining = null
                )
            }
            stopProgressTracking()
        }
    }

    fun clearSleepTimer() {
        setSleepTimer(null)
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch(Dispatchers.Main) {
            while (isActive) {
                try {
                    val player = recitationPlayer
                    if (player != null && isRecitationPrepared && _uiState.value.isPlaying) {
                        val current = player.currentPosition.toLong()
                        val dur = player.duration.toLong().coerceAtLeast(1000L)
                        _uiState.update {
                            it.copy(
                                currentPositionMs = current.coerceIn(0L, dur),
                                durationMs = dur
                            )
                        }
                    }
                } catch (_: Exception) {}
                delay(500L)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracking()
        sleepTimerJob?.cancel()
        safeReleaseRecitationPlayer()
        safeReleaseAmbientPlayer()
    }
}
