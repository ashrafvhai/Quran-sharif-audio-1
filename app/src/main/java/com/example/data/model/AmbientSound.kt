package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.R

data class AmbientSound(
    val id: String,
    val name: String,
    val emoji: String,
    val audioUrl: String,
    val backgroundDrawableRes: Int? = null,
    val backgroundFallbackUrl: String = "",
    val primaryMoodColor: Color = Color(0xFF1E3A8A),
    val secondaryMoodColor: Color = Color(0xFF0F172A)
)

object AmbientSoundData {
    val sounds = listOf(
        AmbientSound(
            id = "none",
            name = "No sound",
            emoji = "🚫",
            audioUrl = "",
            primaryMoodColor = Color(0xFF263238),
            secondaryMoodColor = Color(0xFF102027)
        ),
        AmbientSound(
            id = "rain",
            name = "Rain",
            emoji = "🌧️",
            audioUrl = "https://actions.google.com/sounds/v1/weather/rain_heavy.ogg",
            backgroundDrawableRes = R.drawable.img_nature_rain,
            primaryMoodColor = Color(0xFF2B3A4A),
            secondaryMoodColor = Color(0xFF1A2634)
        ),
        AmbientSound(
            id = "birds",
            name = "Birds",
            emoji = "🐦",
            audioUrl = "https://actions.google.com/sounds/v1/ambiences/daytime_forest_bonfire.ogg",
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF233B26),
            secondaryMoodColor = Color(0xFF132316)
        ),
        AmbientSound(
            id = "fire",
            name = "Fire",
            emoji = "🔥",
            audioUrl = "https://actions.google.com/sounds/v1/ambiences/campfire.ogg",
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF5A1E12),
            secondaryMoodColor = Color(0xFF330F08)
        ),
        AmbientSound(
            id = "wave",
            name = "Wave",
            emoji = "🌊",
            audioUrl = "https://actions.google.com/sounds/v1/water/waves_crashing_on_rock_beach.ogg",
            backgroundDrawableRes = R.drawable.img_nature_waves,
            primaryMoodColor = Color(0xFF1E3A52),
            secondaryMoodColor = Color(0xFF102232)
        ),
        AmbientSound(
            id = "wind",
            name = "Wind",
            emoji = "💨",
            audioUrl = "https://actions.google.com/sounds/v1/weather/wind_strong_cold.ogg",
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF2E4053),
            secondaryMoodColor = Color(0xFF1C2833)
        )
    )

    fun getById(id: String): AmbientSound {
        return sounds.find { it.id == id } ?: sounds.first()
    }
}
