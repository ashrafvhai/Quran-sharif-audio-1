package com.example.player

import android.content.Context
import android.media.AudioAttributes
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
    val ambientSound: AmbientSound = AmbientSoundData.getById("rain"),
    val ambientVolume: Float = 0.5f,
    val recitationVolume: Float = 1.0f,
    val sleepTimerMinutes: Int? = null,
    val sleepTimerSecondsRemaining: Int? = null,
    val errorMessage: String? = null,
    val isMuted: Boolean = false
)

class AudioPlayerManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val TAG = "AudioPlayerManager"

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    @Volatile
    private var recitationPlayer: MediaPlayer? = null

    @Volatile
    private var ambientPlayer: MediaPlayer? = null

    private var progressJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var usingFallback = false

    init {
        // Prepare initial ambient sound (Rain) in background safely
        try {
            setAmbientSound(AmbientSoundData.getById("rain"))
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ambient sound", e)
        }
    }

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

    private fun prepareRecitation(surah: Surah, reciter: Reciter, useFallback: Boolean, autoPlay: Boolean) {
        stopProgressTracking()
        try {
            val oldPlayer = recitationPlayer
            recitationPlayer = null
            try {
                oldPlayer?.stop()
            } catch (_: Exception) {}
            try {
                oldPlayer?.release()
            } catch (_: Exception) {}

            val url = if (useFallback) reciter.getFallbackAudioUrl(surah.number) else reciter.getAudioUrl(surah.number)
            Log.d(TAG, "Preparing recitation URL: $url")

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)

                setOnPreparedListener { mp ->
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
                    if (!usingFallback) {
                        usingFallback = true
                        prepareRecitation(surah, reciter, useFallback = true, autoPlay = true)
                    } else {
                        _uiState.update {
                            it.copy(
                                isBuffering = false,
                                isPlaying = false,
                                errorMessage = "Audio stream unavailable. Please check your internet connection."
                            )
                        }
                    }
                    true
                }

                prepareAsync()
            }
            recitationPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize recitation player", e)
            _uiState.update { it.copy(isBuffering = false, isPlaying = false, errorMessage = e.localizedMessage) }
        }
    }

    fun togglePlayPause() {
        try {
            val player = recitationPlayer
            if (player == null) {
                // First tap: start current surah
                playSurah(_uiState.value.currentSurah, _uiState.value.currentReciter, startPlaying = true)
                return
            }

            if (_uiState.value.isPlaying) {
                try {
                    player.pause()
                } catch (e: Exception) {
                    Log.w(TAG, "Error pausing recitation player", e)
                }
                _uiState.update { it.copy(isPlaying = false) }
                stopProgressTracking()
                pauseAmbient()
            } else {
                try {
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
                val maxDur = _uiState.value.durationMs
                val clamped = positionMs.coerceIn(0L, maxDur)
                player.seekTo(clamped.toInt())
                _uiState.update { it.copy(currentPositionMs = clamped) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Seek error", e)
        }
    }

    fun skip15Forward() {
        try {
            recitationPlayer?.let { player ->
                val current = try { player.currentPosition } catch (_: Exception) { _uiState.value.currentPositionMs.toInt() }
                val maxDur = try { player.duration } catch (_: Exception) { _uiState.value.durationMs.toInt() }
                val newPos = (current + 15000).coerceAtMost(maxDur).toLong()
                seekTo(newPos)
            }
        } catch (e: Exception) {
            Log.e(TAG, "skip15Forward error", e)
        }
    }

    fun skip15Backward() {
        try {
            recitationPlayer?.let { player ->
                val current = try { player.currentPosition } catch (_: Exception) { _uiState.value.currentPositionMs.toInt() }
                val newPos = (current - 15000).coerceAtLeast(0).toLong()
                seekTo(newPos)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    // Ambient Sound Player
    fun setAmbientSound(ambient: AmbientSound) {
        _uiState.update { it.copy(ambientSound = ambient) }

        if (ambient.id == "none" || ambient.audioUrl.isBlank()) {
            stopAmbient()
            return
        }

        try {
            val oldPlayer = ambientPlayer
            ambientPlayer = null
            try { oldPlayer?.stop() } catch (_: Exception) {}
            try { oldPlayer?.release() } catch (_: Exception) {}

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
                    true
                }
                prepareAsync()
            }
            ambientPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load ambient sound", e)
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
        val vol = if (_uiState.value.isMuted) 0f else _uiState.value.recitationVolume
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
            if (_uiState.value.ambientSound.id != "none") {
                ambientPlayer?.start()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Ambient play error", e)
        }
    }

    private fun pauseAmbient() {
        try {
            ambientPlayer?.pause()
        } catch (e: Exception) {
            Log.w(TAG, "Ambient pause error", e)
        }
    }

    private fun stopAmbient() {
        try {
            val old = ambientPlayer
            ambientPlayer = null
            try { old?.stop() } catch (_: Exception) {}
            try { old?.release() } catch (_: Exception) {}
        } catch (e: Exception) {
            Log.w(TAG, "Ambient stop error", e)
        }
    }

    // Sleep Timer
    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        if (minutes == null) {
            _uiState.update { it.copy(sleepTimerMinutes = null, sleepTimerSecondsRemaining = null) }
            return
        }

        if (minutes == -1) {
            // End of surah
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
            // Timer expired: stop playback gracefully
            try {
                recitationPlayer?.pause()
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
                    if (player != null && _uiState.value.isPlaying) {
                        val current = player.currentPosition.toLong()
                        val dur = player.duration.toLong().coerceAtLeast(1000L)
                        _uiState.update {
                            it.copy(
                                currentPositionMs = current.coerceIn(0L, dur),
                                durationMs = dur
                            )
                        }
                    }
                } catch (_: Exception) {
                    // Ignored safe guard against IllegalStateException from MediaPlayer
                }
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
        try {
            val r = recitationPlayer
            recitationPlayer = null
            r?.release()
        } catch (_: Exception) {}
        try {
            val a = ambientPlayer
            ambientPlayer = null
            a?.release()
        } catch (_: Exception) {}
    }
}
