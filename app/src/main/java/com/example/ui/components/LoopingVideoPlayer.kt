package com.example.ui.components

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RawRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AmbientSound
import kotlinx.coroutines.delay

data class BackgroundVideoTheme(
    val id: String,
    val name: String,
    val banglaName: String,
    val emoji: String,
    @RawRes val rawResIds: List<Int>
)

object BackgroundVideoData {
    val themes: List<BackgroundVideoTheme> = listOf(
        BackgroundVideoTheme(
            id = "auto_mix",
            name = "Auto Mix Videos",
            banglaName = "মিক্সিং বাগ্রাউণ্ড",
            emoji = "🔀",
            rawResIds = listOf(
                R.raw.video_waves,
                R.raw.video_rain,
                R.raw.video_sky,
                R.raw.video_stars
            )
        ),
        BackgroundVideoTheme(
            id = "sea",
            name = "Ocean Waves",
            banglaName = "সমুদ্র ও ঢেউ",
            emoji = "🌊",
            rawResIds = listOf(R.raw.video_waves)
        ),
        BackgroundVideoTheme(
            id = "rain",
            name = "Rain Drops",
            banglaName = "বৃষ্টির স্নিগ্ধ ধারা",
            emoji = "🌧️",
            rawResIds = listOf(R.raw.video_rain)
        ),
        BackgroundVideoTheme(
            id = "sky",
            name = "Clouds & Sky",
            banglaName = "মেঘ ও নীল আকাশ",
            emoji = "☁️",
            rawResIds = listOf(R.raw.video_sky)
        ),
        BackgroundVideoTheme(
            id = "stars",
            name = "Night Sky & Stars",
            banglaName = "তারা ও শান্ত রাত",
            emoji = "✨",
            rawResIds = listOf(R.raw.video_stars)
        )
    )

    fun getThemeById(id: String): BackgroundVideoTheme {
        return themes.find { it.id == id } ?: themes.first()
    }
}

/**
 * 100% Silent, Clean Nature Video Background using TextureView + MediaPlayer.
 *
 * Key guarantees:
 * 1. Zero Audio Interference: Never requests audio focus, volume is strictly 0.0f,
 *    allowing recitation audio to play with full power and clarity through the media speaker.
 * 2. "মিক্সিং বাগ্রাউণ্ড" (Auto Mix): Automatically plays videos sequentially one after another.
 * 3. Offline-Ready: Bundled high-efficiency H.264 local MP4 video assets.
 * 4. Cinematic Ken Burns atmospheric backdrop transition.
 */
@Composable
fun AmbientVideoBackground(
    ambient: AmbientSound,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    videoThemeId: String = "auto_mix",
    autoSwitchIntervalSeconds: Int = 12
) {
    val context = LocalContext.current
    val theme = remember(videoThemeId) { BackgroundVideoData.getThemeById(videoThemeId) }
    var currentVideoIndex by remember(videoThemeId) { mutableIntStateOf(0) }
    var isVideoReady by remember { mutableStateOf(false) }

    // If Auto Mix (মিক্সিং বাগ্রাউণ্ড), cycle to the next video automatically every few seconds
    LaunchedEffect(videoThemeId, isPlaying) {
        if (theme.rawResIds.size > 1) {
            while (true) {
                delay(autoSwitchIntervalSeconds * 1000L)
                if (isPlaying) {
                    currentVideoIndex = (currentVideoIndex + 1) % theme.rawResIds.size
                }
            }
        }
    }

    val currentRawRes = remember(theme, currentVideoIndex) {
        if (theme.rawResIds.isNotEmpty()) {
            theme.rawResIds[currentVideoIndex.coerceIn(0, theme.rawResIds.lastIndex)]
        } else {
            R.raw.video_waves
        }
    }

    // Subtle Ken Burns slow zoom animation for cinematic living wallpaper feel
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ken_burns_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF07080B))
    ) {
        // 1. Silent, Muted Looping Video via TextureView & MediaPlayer
        Crossfade(
            targetState = currentRawRes,
            animationSpec = tween(durationMillis = 900),
            label = "video_res_crossfade"
        ) { rawRes ->
            SilentTextureVideoPlayer(
                context = context,
                rawResId = rawRes,
                isPlaying = isPlaying,
                onVideoEnded = {
                    if (theme.rawResIds.size > 1) {
                        currentVideoIndex = (currentVideoIndex + 1) % theme.rawResIds.size
                    }
                },
                onPrepared = { isVideoReady = true },
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scaleAnim)
            )
        }

        // 2. High-quality graceful fallback layer with Ken Burns animation
        if (!isVideoReady) {
            val fallbackDrawable = when {
                ambient.backgroundDrawableRes != null -> ambient.backgroundDrawableRes
                currentRawRes == R.raw.video_rain -> R.drawable.img_nature_rain
                else -> R.drawable.img_nature_waves
            }

            Image(
                painter = painterResource(id = fallbackDrawable),
                contentDescription = ambient.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scaleAnim)
            )
        }

        // 3. Ultra-Clean Cinematic Minimalist Gradient Overlays
        // Provides beautiful contrast for the clean buttons without dimming the video
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.75f)
                        )
                    )
                )
        )
    }
}

/**
 * Direct TextureView implementation that guarantees ZERO audio focus consumption
 * and 100% silent video frame decoding.
 */
@Composable
private fun SilentTextureVideoPlayer(
    context: Context,
    @RawRes rawResId: Int,
    isPlaying: Boolean,
    onVideoEnded: () -> Unit,
    onPrepared: () -> Unit,
    modifier: Modifier = Modifier
) {
    var playerRef by remember { mutableStateOf<MediaPlayer?>(null) }
    var surfaceRef by remember { mutableStateOf<Surface?>(null) }

    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                        val surface = Surface(surfaceTexture)
                        surfaceRef = surface

                        try {
                            val uri = Uri.parse("android.resource://${ctx.packageName}/$rawResId")
                            val player = MediaPlayer().apply {
                                setSurface(surface)
                                setDataSource(ctx, uri)
                                // CRITICAL: Absolutely zero audio volume to ensure recitation is crystal clear
                                setVolume(0f, 0f)
                                isLooping = false

                                setOnPreparedListener { mp ->
                                    onPrepared()
                                    try {
                                        mp.start()
                                    } catch (_: Exception) {}
                                }

                                setOnCompletionListener { mp ->
                                    onVideoEnded()
                                    try {
                                        mp.seekTo(0)
                                        mp.start()
                                    } catch (_: Exception) {}
                                }

                                setOnErrorListener { _, _, _ ->
                                    true
                                }

                                prepareAsync()
                            }
                            playerRef = player
                        } catch (e: Exception) {
                            android.util.Log.w("SilentVideoPlayer", "Error preparing raw video: ${e.message}")
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        try {
                            playerRef?.stop()
                            playerRef?.release()
                            playerRef = null
                            surfaceRef?.release()
                            surfaceRef = null
                        } catch (_: Exception) {}
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        },
        update = {
            playerRef?.let { player ->
                try {
                    if (isPlaying) {
                        if (!player.isPlaying) player.start()
                    } else {
                        if (player.isPlaying) player.pause()
                    }
                } catch (_: Exception) {}
            }
        },
        modifier = modifier
    )

    DisposableEffect(rawResId) {
        onDispose {
            try {
                playerRef?.stop()
                playerRef?.release()
                playerRef = null
                surfaceRef?.release()
                surfaceRef = null
            } catch (_: Exception) {}
        }
    }
}
