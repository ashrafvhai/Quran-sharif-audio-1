package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.AmbientSound
import kotlinx.coroutines.delay

data class BackgroundTheme(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    @DrawableRes val drawableResId: Int
)

object BackgroundVideoData {
    val themes: List<BackgroundTheme> = listOf(
        BackgroundTheme(
            id = "auto_mix",
            name = "Auto Mix (10s Cycle)",
            description = "Cycles through all 20 serene scenes every 10 seconds",
            emoji = "🔀",
            drawableResId = R.drawable.bg_waves
        ),
        BackgroundTheme(
            id = "waves",
            name = "Ocean Waves",
            description = "Tranquil blue rolling sea waves",
            emoji = "🌊",
            drawableResId = R.drawable.bg_waves
        ),
        BackgroundTheme(
            id = "rain",
            name = "Rain Drops",
            description = "Soothing raindrops on clear glass",
            emoji = "🌧️",
            drawableResId = R.drawable.bg_rain
        ),
        BackgroundTheme(
            id = "sky",
            name = "Azure Sky",
            description = "Peaceful clear clouds and open sky",
            emoji = "☁️",
            drawableResId = R.drawable.bg_sky
        ),
        BackgroundTheme(
            id = "stars",
            name = "Night Sky & Stars",
            description = "Starlit calm celestial night sky",
            emoji = "✨",
            drawableResId = R.drawable.bg_stars
        ),
        BackgroundTheme(
            id = "sunset",
            name = "Golden Sunset",
            description = "Warm serene amber evening glow",
            emoji = "🌅",
            drawableResId = R.drawable.bg_sunset
        ),
        BackgroundTheme(
            id = "aurora",
            name = "Cosmic Aurora",
            description = "Ethereal northern polar lights",
            emoji = "🌌",
            drawableResId = R.drawable.bg_aurora
        ),
        BackgroundTheme(
            id = "forest",
            name = "Emerald Forest",
            description = "Peaceful deep green woodland",
            emoji = "🌲",
            drawableResId = R.drawable.bg_forest
        ),
        BackgroundTheme(
            id = "moon",
            name = "Crescent Moon",
            description = "Silver nocturnal crescent moon",
            emoji = "🌙",
            drawableResId = R.drawable.bg_moon
        ),
        BackgroundTheme(
            id = "river",
            name = "Mountain River",
            description = "Serene crystal mountain brook",
            emoji = "🏞️",
            drawableResId = R.drawable.bg_river
        ),
        BackgroundTheme(
            id = "galaxy",
            name = "Deep Galaxy",
            description = "Mystical cosmic starry spiral",
            emoji = "🪐",
            drawableResId = R.drawable.bg_galaxy
        ),
        BackgroundTheme(
            id = "desert",
            name = "Desert Dunes",
            description = "Quiet warm rolling sand waves",
            emoji = "🏜️",
            drawableResId = R.drawable.bg_desert
        ),
        BackgroundTheme(
            id = "lake",
            name = "Alpine Lake",
            description = "Still and reflective mountain lake",
            emoji = "⛵",
            drawableResId = R.drawable.bg_lake
        ),
        BackgroundTheme(
            id = "waterfall",
            name = "Misty Waterfall",
            description = "Gentle cascading crystal waters",
            emoji = "💧",
            drawableResId = R.drawable.bg_waterfall
        ),
        BackgroundTheme(
            id = "dawn",
            name = "Serene Dawn",
            description = "Fresh quiet morning horizon",
            emoji = "🌄",
            drawableResId = R.drawable.bg_dawn
        ),
        BackgroundTheme(
            id = "twilight",
            name = "Peaceful Twilight",
            description = "Soft purple evening dusk",
            emoji = "🌆",
            drawableResId = R.drawable.bg_twilight
        ),
        BackgroundTheme(
            id = "sakura",
            name = "Blossom Petals",
            description = "Gentle spring flora and petals",
            emoji = "🌸",
            drawableResId = R.drawable.bg_sakura
        ),
        BackgroundTheme(
            id = "breeze",
            name = "Mountain Breeze",
            description = "High altitude fresh clean breeze",
            emoji = "🍃",
            drawableResId = R.drawable.bg_breeze
        ),
        BackgroundTheme(
            id = "clouds_timelapse",
            name = "Clouds Flow",
            description = "Dreamy majestic floating clouds",
            emoji = "⛅",
            drawableResId = R.drawable.bg_clouds_timelapse
        ),
        BackgroundTheme(
            id = "crystal_waters",
            name = "Crystal Waters",
            description = "Pure shimmering transparent spring",
            emoji = "🫧",
            drawableResId = R.drawable.bg_crystal_waters
        ),
        BackgroundTheme(
            id = "candle",
            name = "Spiritual Glow",
            description = "Warm comforting radiant sanctuary light",
            emoji = "🕯️",
            drawableResId = R.drawable.bg_candle
        )
    )

    // The 20 individual theme drawables (excluding auto_mix which cycles them)
    val individualThemes: List<BackgroundTheme> = themes.filter { it.id != "auto_mix" }

    fun getThemeById(id: String): BackgroundTheme {
        return themes.find { it.id == id } ?: themes.first()
    }
}

/**
 * Clean, serene background scene display.
 * As requested by the user:
 * - Background videos stay as static still images without moving/jitter ("কোনো নাড়াচাড়া না করে")
 * - If Auto Mix is selected, the background smoothly cycles through the 20 scenes every 10 seconds
 * - 100% in English
 * - Crisp colored backdrop that lets reciter portraits and recitation audio shine
 */
@Composable
fun AmbientVideoBackground(
    ambient: AmbientSound,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    videoThemeId: String = "auto_mix",
    autoSwitchIntervalSeconds: Int = 10
) {
    val allScenes = BackgroundVideoData.individualThemes
    var currentCycleIndex by remember { mutableIntStateOf(0) }

    // If Auto Mix is selected and playing, advance scene every 10 seconds
    LaunchedEffect(videoThemeId, isPlaying) {
        if (videoThemeId == "auto_mix" && allScenes.isNotEmpty()) {
            while (true) {
                delay(autoSwitchIntervalSeconds * 1000L)
                if (isPlaying) {
                    currentCycleIndex = (currentCycleIndex + 1) % allScenes.size
                }
            }
        }
    }

    val currentTheme = remember(videoThemeId, currentCycleIndex) {
        if (videoThemeId == "auto_mix") {
            allScenes[currentCycleIndex.coerceIn(0, allScenes.lastIndex)]
        } else {
            BackgroundVideoData.getThemeById(videoThemeId)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // High quality static image background with smooth crossfade
        Crossfade(
            targetState = currentTheme.drawableResId,
            animationSpec = tween(durationMillis = 800),
            label = "scene_crossfade"
        ) { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = currentTheme.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Subtle gradient overlay for status and navigation bar legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.3f)
                        )
                    )
                )
        )
    }
}
