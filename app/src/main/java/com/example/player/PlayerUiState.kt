package com.example.player

import com.example.data.model.AmbientSound
import com.example.data.model.AmbientSoundData
import com.example.data.model.QuranData
import com.example.data.model.Reciter
import com.example.data.model.ReciterData
import com.example.data.model.Surah

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
    val selectedVideoThemeId: String = "auto_mix",
    val playbackQueue: List<Surah> = QuranData.surahs,
    val isAutoplayEnabled: Boolean = true,
    val isShuffleEnabled: Boolean = false,
    val isRepeatOne: Boolean = false
)
