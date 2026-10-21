package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
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

    init {
        // Initialize queue with all 114 Surahs
        _uiState.update { it.copy(playbackQueue = QuranData.surahs) }
    }

    private var hasAudioFocus = false

    private fun requestSystemAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        try {
            audioManager?.let { am ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                        )
                        .setAcceptsDelayedFocusGain(true)
                        .setOnAudioFocusChangeListener { focusChange ->
                            when (focusChange) {
                                AudioManager.AUDIOFOCUS_LOSS -> {
                                    if (_uiState.value.isPlaying) {
                                        togglePlayPause()
                                    }
                                }
                                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                                    val currentVol = if (_uiState.value.isMuted) 0f else _uiState.value.recitationVolume
                                    recitationPlayer?.setVolume(currentVol * 0.3f, currentVol * 0.3f)
                                }
                                AudioManager.AUDIOFOCUS_GAIN -> {
                                    applyRecitationVolume()
                                }
                            }
                        }
                        .build()
                    audioFocusRequest = focusRequest
                    val res = am.requestAudioFocus(focusRequest)
                    hasAudioFocus = (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    return hasAudioFocus
                } else {
                    @Suppress("DEPRECATION")
                    val res = am.requestAudioFocus(
                        { focusChange ->
                            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                                applyRecitationVolume()
                            }
                        },
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                    hasAudioFocus = (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                    return hasAudioFocus
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Audio focus request issue: ${e.message}")
        }
        return false
    }

    private fun abandonSystemAudioFocus() {
        if (!hasAudioFocus) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(null)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error abandoning audio focus", e)
        }
        hasAudioFocus = false
    }

    private fun safeReleaseRecitationPlayer() {
        isRecitationPrepared = false
        val player = recitationPlayer
        recitationPlayer = null
        player?.let {
            try {
                it.setOnPreparedListener(null)
                it.setOnCompletionListener(null)
                it.setOnErrorListener(null)
                it.reset()
                it.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error safely releasing recitation player", e)
            }
        }
    }

    private fun safeReleaseAmbientPlayer() {
        isAmbientPrepared = false
        val player = ambientPlayer
        ambientPlayer = null
        player?.let {
            try {
                it.setOnPreparedListener(null)
                it.setOnCompletionListener(null)
                it.setOnErrorListener(null)
                it.reset()
                it.release()
            } catch (e: Exception) {
                Log.w(TAG, "Error safely releasing ambient player", e)
            }
        }
    }

    fun playSurah(surah: Surah, reciter: Reciter, startPlaying: Boolean = true) {
        usingFallback = false
        _uiState.update { current ->
            // If surah is not in current queue, prepend or ensure it's in queue
            val queue = if (current.playbackQueue.any { it.number == surah.number }) {
                current.playbackQueue
            } else {
                listOf(surah) + current.playbackQueue
            }
            current.copy(
                currentSurah = surah,
                currentReciter = reciter,
                isPlaying = false,
                isBuffering = true,
                currentPositionMs = 0L,
                durationMs = 1000L,
                playbackQueue = queue,
                errorMessage = null
            )
        }
        prepareRecitation(surah, reciter, useFallback = false, autoPlay = startPlaying)
    }

    fun switchReciter(reciter: Reciter) {
        val currentSurah = _uiState.value.currentSurah
        val shouldPlay = _uiState.value.isPlaying
        playSurah(currentSurah, reciter, shouldPlay)
    }

    fun setVideoTheme(themeId: String) {
        _uiState.update { it.copy(selectedVideoThemeId = themeId) }
    }

    private fun prepareRecitation(surah: Surah, reciter: Reciter, useFallback: Boolean, autoPlay: Boolean) {
        stopProgressTracking()
        safeReleaseRecitationPlayer()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isBuffering = true, errorMessage = null) }
                val url = if (useFallback) reciter.getFallbackAudioUrl(surah.number) else reciter.getAudioUrl(surah.number)
                val player = MediaPlayer()

                player.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                val localAudioFile = File(context.filesDir, "audio/surah_${surah.number}_${reciter.id}.mp3")
                if (localAudioFile.exists() && localAudioFile.length() > 5000L) {
                    player.setDataSource(localAudioFile.absolutePath)
                } else {
                    player.setDataSource(url)
                }

                val currentRecitationVolume = if (_uiState.value.isMuted) 0f else _uiState.value.recitationVolume
                player.setVolume(currentRecitationVolume, currentRecitationVolume)

                player.setOnPreparedListener { mp ->
                    isRecitationPrepared = true
                    val dur = mp.duration.toLong().coerceAtLeast(1000L)
                    _uiState.update {
                        it.copy(
                            isBuffering = false,
                            durationMs = dur,
                            isPlaying = autoPlay
                        )
                    }
                    applyPlaybackSpeed(_uiState.value.playbackSpeed)
                    if (autoPlay) {
                        requestSystemAudioFocus()
                        applyRecitationVolume()
                        mp.start()
                        startProgressTracking()
                        ensureAmbientPlaying()
                    }
                }

                player.setOnCompletionListener {
                    _uiState.update { it.copy(isPlaying = false, currentPositionMs = 0L) }
                    stopProgressTracking()
                    abandonSystemAudioFocus()

                    val sleepTimer = _uiState.value.sleepTimerMinutes
                    if (sleepTimer != null && sleepTimer == -1) {
                        clearSleepTimer()
                    } else if (_uiState.value.isAutoplayEnabled) {
                        // CONTINUOUS AUTOPLAY: Play the next audio in queue automatically!
                        playNext()
                    }
                }

                player.setOnErrorListener { _, what, extra ->
                    Log.w(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    abandonSystemAudioFocus()
                    if (!useFallback) {
                        usingFallback = true
                        prepareRecitation(surah, reciter, useFallback = true, autoPlay = autoPlay)
                    } else {
                        _uiState.update {
                            it.copy(
                                isBuffering = false,
                                isPlaying = false,
                                errorMessage = "Audio unavailable online. Please check connection."
                            )
                        }
                    }
                    true
                }

                player.prepareAsync()
                recitationPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize recitation player", e)
                abandonSystemAudioFocus()
                _uiState.update {
                    it.copy(
                        isBuffering = false,
                        isPlaying = false,
                        errorMessage = e.localizedMessage ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val player = recitationPlayer
        if (player != null && isRecitationPrepared) {
            if (_uiState.value.isPlaying) {
                // User wants to PAUSE
                try {
                    if (player.isPlaying) player.pause()
                } catch (e: Exception) {
                    Log.w(TAG, "Error pausing recitation player", e)
                }
                _uiState.update { it.copy(isPlaying = false) }
                stopProgressTracking()
                pauseAmbient()
                abandonSystemAudioFocus()
            } else {
                // User wants to RESUME
                try {
                    requestSystemAudioFocus()
                    // Maintain existing steady volume - no artificial boost
                    applyRecitationVolume()
                    player.start()
                    _uiState.update { it.copy(isPlaying = true) }
                    startProgressTracking()
                    ensureAmbientPlaying()
                } catch (e: Exception) {
                    Log.w(TAG, "Player failed to resume, re-preparing...", e)
                    playSurah(_uiState.value.currentSurah, _uiState.value.currentReciter, startPlaying = true)
                }
            }
        } else {
            // Not prepared, prepare and start
            playSurah(_uiState.value.currentSurah, _uiState.value.currentReciter, startPlaying = true)
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            recitationPlayer?.let { player ->
                if (isRecitationPrepared) {
                    val pos = positionMs.coerceIn(0L, _uiState.value.durationMs)
                    player.seekTo(pos.toInt())
                    _uiState.update { it.copy(currentPositionMs = pos) }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "seekTo error", e)
        }
    }

    fun skip15Forward() {
        try {
            recitationPlayer?.let { player ->
                if (isRecitationPrepared) {
                    val currentPos = try { player.currentPosition } catch (e: Exception) { _uiState.value.currentPositionMs.toInt() }
                    seekTo((currentPos + 15000L).coerceAtMost(_uiState.value.durationMs))
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
                    val currentPos = try { player.currentPosition } catch (e: Exception) { _uiState.value.currentPositionMs.toInt() }
                    seekTo((currentPos - 15000L).coerceAtLeast(0L))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "skip15Backward error", e)
        }
    }

    // CONTINUOUS AUTOPLAY & QUEUE ADVANCEMENT
    fun playNext() {
        val state = _uiState.value
        val queue = state.playbackQueue
        if (queue.isEmpty()) return

        if (state.isRepeatOne) {
            // Repeat the same Surah
            seekTo(0L)
            togglePlayPause()
            return
        }

        val currentIndex = queue.indexOfFirst { it.number == state.currentSurah.number }
        val nextIndex = if (currentIndex != -1 && currentIndex < queue.size - 1) {
            currentIndex + 1
        } else {
            0 // Loop back to start of queue
        }

        val nextSurah = queue[nextIndex]
        playSurah(nextSurah, state.currentReciter, startPlaying = true)
    }

    fun playPrevious() {
        val state = _uiState.value
        val queue = state.playbackQueue
        if (queue.isEmpty()) return

        val currentIndex = queue.indexOfFirst { it.number == state.currentSurah.number }
        val prevIndex = if (currentIndex > 0) {
            currentIndex - 1
        } else {
            queue.size - 1
        }

        val prevSurah = queue[prevIndex]
        playSurah(prevSurah, state.currentReciter, startPlaying = true)
    }

    // CUSTOM QUEUE MANAGEMENT - USER CAN SET WHICH ONE PLAYS AFTER WHICH
    fun setCustomQueue(newQueue: List<Surah>) {
        if (newQueue.isNotEmpty()) {
            _uiState.update { it.copy(playbackQueue = newQueue) }
        }
    }

    fun addToQueueNext(surah: Surah) {
        _uiState.update { current ->
            val mutable = current.playbackQueue.toMutableList()
            val currentIdx = mutable.indexOfFirst { it.number == current.currentSurah.number }
            val insertIdx = if (currentIdx != -1) currentIdx + 1 else 0
            mutable.removeAll { it.number == surah.number } // prevent duplicates
            val finalIdx = insertIdx.coerceIn(0, mutable.size)
            mutable.add(finalIdx, surah)
            current.copy(playbackQueue = mutable)
        }
    }

    fun addToQueueEnd(surah: Surah) {
        _uiState.update { current ->
            val mutable = current.playbackQueue.toMutableList()
            mutable.removeAll { it.number == surah.number }
            mutable.add(surah)
            current.copy(playbackQueue = mutable)
        }
    }

    fun removeFromQueue(surahNumber: Int) {
        _uiState.update { current ->
            val mutable = current.playbackQueue.toMutableList()
            mutable.removeAll { it.number == surahNumber }
            current.copy(playbackQueue = if (mutable.isEmpty()) QuranData.surahs else mutable)
        }
    }

    fun moveQueueItem(fromIndex: Int, toIndex: Int) {
        _uiState.update { current ->
            val mutable = current.playbackQueue.toMutableList()
            if (fromIndex in mutable.indices && toIndex in mutable.indices) {
                val item = mutable.removeAt(fromIndex)
                mutable.add(toIndex, item)
            }
            current.copy(playbackQueue = mutable)
        }
    }

    fun resetQueueToDefault() {
        _uiState.update { it.copy(playbackQueue = QuranData.surahs) }
    }

    fun toggleAutoplay() {
        _uiState.update { it.copy(isAutoplayEnabled = !it.isAutoplayEnabled) }
    }

    fun toggleShuffle() {
        _uiState.update { current ->
            val newShuffle = !current.isShuffleEnabled
            val newQueue = if (newShuffle) {
                val currentSurah = current.currentSurah
                val others = current.playbackQueue.filter { it.number != currentSurah.number }.shuffled()
                listOf(currentSurah) + others
            } else {
                QuranData.surahs
            }
            current.copy(isShuffleEnabled = newShuffle, playbackQueue = newQueue)
        }
    }

    fun toggleRepeatOne() {
        _uiState.update { it.copy(isRepeatOne = !it.isRepeatOne) }
    }

    fun setSpeed(speed: Float) {
        _uiState.update { it.copy(playbackSpeed = speed) }
        applyPlaybackSpeed(speed)
    }

    private fun applyPlaybackSpeed(speed: Float) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recitationPlayer?.let { player ->
                    if (isRecitationPrepared) {
                        val params = player.playbackParams
                        params.speed = speed
                        player.playbackParams = params
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "applyPlaybackSpeed failed", e)
        }
    }

    fun setAmbientSound(ambient: AmbientSound) {
        _uiState.update { it.copy(ambientSound = ambient) }
        if (ambient.id == "none") {
            stopAmbient()
            return
        }
        safeReleaseAmbientPlayer()
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val player = MediaPlayer()
                player.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                player.setDataSource(ambient.audioUrl)
                player.isLooping = true
                val vol = if (_uiState.value.isMuted) 0f else _uiState.value.ambientVolume
                player.setVolume(vol, vol)
                player.setOnPreparedListener { mp ->
                    isAmbientPrepared = true
                    if (_uiState.value.isPlaying) {
                        mp.start()
                    }
                }
                player.prepareAsync()
                ambientPlayer = player
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load ambient sound", e)
            }
        }
    }

    fun setAmbientVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(ambientVolume = vol) }
        try {
            val finalVol = if (_uiState.value.isMuted) 0f else vol
            ambientPlayer?.setVolume(finalVol, finalVol)
        } catch (e: Exception) {
            Log.w(TAG, "Error setting ambient volume", e)
        }
    }

    fun setRecitationVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        _uiState.update { it.copy(recitationVolume = vol) }
        applyRecitationVolume()
    }

    private fun applyRecitationVolume() {
        try {
            val vol = if (_uiState.value.isMuted) 0f else _uiState.value.recitationVolume
            recitationPlayer?.setVolume(vol, vol)
        } catch (e: Exception) {
            Log.w(TAG, "applyRecitationVolume error", e)
        }
    }

    fun toggleMute() {
        val currentMuted = _uiState.value.isMuted
        val newMuted = !currentMuted
        _uiState.update { it.copy(isMuted = newMuted) }
        applyRecitationVolume()
        setAmbientVolume(_uiState.value.ambientVolume)
    }

    private fun ensureAmbientPlaying() {
        if (_uiState.value.ambientSound.id != "none" && isAmbientPrepared) {
            try {
                ambientPlayer?.let { if (!it.isPlaying) it.start() }
            } catch (e: Exception) {
                Log.w(TAG, "ensureAmbientPlaying error", e)
            }
        }
    }

    private fun pauseAmbient() {
        try {
            ambientPlayer?.let { if (it.isPlaying) it.pause() }
        } catch (e: Exception) {
            Log.w(TAG, "pauseAmbient error", e)
        }
    }

    private fun stopAmbient() {
        safeReleaseAmbientPlayer()
    }

    fun setSleepTimer(minutes: Int?) {
        sleepTimerJob?.cancel()
        _uiState.update { it.copy(sleepTimerMinutes = minutes, sleepTimerSecondsRemaining = null) }
        if (minutes == null || minutes <= 0) return

        sleepTimerJob = coroutineScope.launch(Dispatchers.Default) {
            var totalSeconds = minutes * 60
            while (isActive && totalSeconds > 0) {
                _uiState.update { it.copy(sleepTimerSecondsRemaining = totalSeconds) }
                delay(1000)
                totalSeconds--
            }
            if (isActive) {
                togglePlayPause()
                clearSleepTimer()
            }
        }
    }

    fun clearSleepTimer() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _uiState.update { it.copy(sleepTimerMinutes = null, sleepTimerSecondsRemaining = null) }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch(Dispatchers.Default) {
            while (isActive) {
                recitationPlayer?.let { player ->
                    try {
                        if (isRecitationPrepared && player.isPlaying) {
                            val pos = player.currentPosition.toLong()
                            _uiState.update { it.copy(currentPositionMs = pos) }
                        }
                    } catch (e: Exception) {
                        // ignore transient position query errors
                    }
                }
                delay(400)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        abandonSystemAudioFocus()
        stopProgressTracking()
        clearSleepTimer()
        safeReleaseRecitationPlayer()
        safeReleaseAmbientPlayer()
    }
}
