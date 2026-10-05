package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R

data class AmbientSound(
    val id: String,
    val name: String,
    val emoji: String,
    val audioUrl: String,
    @DrawableRes val backgroundDrawableRes: Int? = null,
    val backgroundFallbackUrl: String = "",
    val primaryMoodColor: Color = Color(0xFF141620),
    val secondaryMoodColor: Color = Color(0xFF090A0E)
)

object AmbientSoundData {
    val sounds: List<AmbientSound> = listOf(
        AmbientSound(
            id = "none",
            name = "No sound",
            emoji = "🚫",
            audioUrl = "",
            backgroundDrawableRes = null,
            primaryMoodColor = Color(0xFF1B1B22),
            secondaryMoodColor = Color(0xFF0B0B0E)
        ),
        AmbientSound(
            id = "rain",
            name = "Rain",
            emoji = "🌧️",
            audioUrl = "https://actions.google.com/sounds/v1/weather/rain_heavy.ogg",
            backgroundDrawableRes = R.drawable.img_nature_rain,
            primaryMoodColor = Color(0xFF1C2C3D),
            secondaryMoodColor = Color(0xFF0D141C)
        ),
        AmbientSound(
            id = "birds",
            name = "Birds",
            emoji = "🐦",
            audioUrl = "https://actions.google.com/sounds/v1/ambiences/daytime_forest_bonfire.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF173322),
            secondaryMoodColor = Color(0xFF0A170F)
        ),
        AmbientSound(
            id = "fire",
            name = "Fire",
            emoji = "🔥",
            audioUrl = "https://actions.google.com/sounds/v1/ambiences/campfire.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF3E1D13),
            secondaryMoodColor = Color(0xFF1A0A06)
        ),
        AmbientSound(
            id = "wave",
            name = "Wave",
            emoji = "🌊",
            audioUrl = "https://actions.google.com/sounds/v1/water/waves_crashing_on_rock_beach.ogg",
            backgroundDrawableRes = R.drawable.img_nature_waves,
            primaryMoodColor = Color(0xFF142738),
            secondaryMoodColor = Color(0xFF0A131C)
        ),
        AmbientSound(
            id = "wind",
            name = "Wind",
            emoji = "💨",
            audioUrl = "https://actions.google.com/sounds/v1/weather/wind_strong_cold.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF1F2B33),
            secondaryMoodColor = Color(0xFF0E1317)
        ),
        AmbientSound(
            id = "cat_purr",
            name = "Cat purr",
            emoji = "🐱",
            audioUrl = "https://actions.google.com/sounds/v1/animals/cat_purr.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF2B2120),
            secondaryMoodColor = Color(0xFF140F0F)
        ),
        AmbientSound(
            id = "owl",
            name = "Owl",
            emoji = "🦉",
            audioUrl = "https://actions.google.com/sounds/v1/animals/screech_owl.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF1B1D28),
            secondaryMoodColor = Color(0xFF0C0E14)
        ),
        AmbientSound(
            id = "river",
            name = "River",
            emoji = "🏞️",
            audioUrl = "https://actions.google.com/sounds/v1/water/babbling_brook.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF122C2C),
            secondaryMoodColor = Color(0xFF081616)
        ),
        AmbientSound(
            id = "whale",
            name = "Whale",
            emoji = "🐋",
            audioUrl = "https://actions.google.com/sounds/v1/water/scuba_bubbles.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF0C243B),
            secondaryMoodColor = Color(0xFF05101A)
        ),
        AmbientSound(
            id = "crickets",
            name = "Crickets",
            emoji = "🦗",
            audioUrl = "https://actions.google.com/sounds/v1/ambiences/crickets_chirping_at_night.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1509114397022-ed747cca3f65?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF16251A),
            secondaryMoodColor = Color(0xFF0A120D)
        ),
        AmbientSound(
            id = "thunderstorm",
            name = "Thunderstorm",
            emoji = "⛈️",
            audioUrl = "https://actions.google.com/sounds/v1/weather/thunderstorm.ogg",
            backgroundDrawableRes = R.drawable.img_nature_rain,
            primaryMoodColor = Color(0xFF181C2E),
            secondaryMoodColor = Color(0xFF0C0D17)
        ),
        AmbientSound(
            id = "thunder",
            name = "Thunder",
            emoji = "⚡",
            audioUrl = "https://actions.google.com/sounds/v1/weather/thunder_crack.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1513002749550-c59d786b8e6c?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF261D3B),
            secondaryMoodColor = Color(0xFF110C1C)
        ),
        AmbientSound(
            id = "train",
            name = "Train",
            emoji = "🚆",
            audioUrl = "https://actions.google.com/sounds/v1/transportation/subway_interior.ogg",
            backgroundDrawableRes = null,
            backgroundFallbackUrl = "https://images.unsplash.com/photo-1474487548417-781cb71495f3?w=1080&auto=format&fit=crop&q=80",
            primaryMoodColor = Color(0xFF272522),
            secondaryMoodColor = Color(0xFF121110)
        )
    )

    fun getById(id: String): AmbientSound {
        return sounds.find { it.id == id } ?: sounds.first()
    }
}
