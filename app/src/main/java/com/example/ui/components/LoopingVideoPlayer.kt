package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import kotlinx.coroutines.delay

data class BackgroundTheme(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val imageResId: Int
)

object BackgroundVideoData {
    val individualThemes = listOf(
        BackgroundTheme("aurora", "Aurora Borealis", "Polar lights dancing in the night sky", "🌌", R.drawable.bg_aurora),
        BackgroundTheme("rain", "Gentle Rain", "Soothing raindrops falling softly", "🌧️", R.drawable.bg_rain),
        BackgroundTheme("sunset", "Golden Sunset", "Warm amber horizon at dusk", "🌅", R.drawable.bg_sunset),
        BackgroundTheme("forest", "Mystic Forest", "Lush green trees and filtered sunlight", "🌲", R.drawable.bg_forest),
        BackgroundTheme("waves", "Ocean Waves", "Gentle ripples on the seashore", "🌊", R.drawable.bg_waves),
        BackgroundTheme("stars", "Cosmic Stars", "Deep starry night universe", "✨", R.drawable.bg_stars),
        BackgroundTheme("lake", "Serene Lake", "Calm mirror waters reflection", "🏞️", R.drawable.bg_lake),
        BackgroundTheme("clouds", "Clouds Timelapse", "Drifting peaceful clouds in blue sky", "☁️", R.drawable.bg_clouds_timelapse),
        BackgroundTheme("waterfall", "Mountain Waterfall", "Crystal stream rushing down cliffs", "🌊", R.drawable.bg_waterfall),
        BackgroundTheme("desert", "Golden Dunes", "Peaceful desert sands under twilight", "🏜️", R.drawable.bg_desert)
    )

    val themes = listOf(
        BackgroundTheme("auto_mix", "Auto Ambient Mix", "Cycles smoothly through scenic themes", "🎨", R.drawable.bg_aurora)
    ) + individualThemes

    fun getThemeById(id: String): BackgroundTheme {
        return individualThemes.find { it.id == id } ?: individualThemes.first()
    }
}

@Composable
fun AmbientVideoBackground(
    themeId: String,
    modifier: Modifier = Modifier
) {
    val currentTheme = if (themeId == "auto_mix") {
        val individual = BackgroundVideoData.individualThemes
        val activeIndex by produceState(initialValue = 0) {
            while (true) {
                delay(10000)
                value = (value + 1) % individual.size
            }
        }
        individual[activeIndex]
    } else {
        BackgroundVideoData.getThemeById(themeId)
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // High quality static background image (no video playback as requested by user)
        Image(
            painter = painterResource(id = currentTheme.imageResId),
            contentDescription = currentTheme.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Subtle gradient overlay for contrast and serene readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
        )
    }
}
