package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppCustomTheme(
    val id: String,
    val displayName: String,
    val description: String,
    val emoji: String,
    val primaryColor: Color,
    val surfaceBackground: Color,
    val cardBackground: Color,
    val cardBorder: Color
) {
    OLED_GLASS_BW(
        "oled_bw",
        "Monochrome Glass B&W",
        "Pure OLED black with crisp frosted glass and white accents",
        "⚪",
        Color(0xFFFFFFFF),
        Color(0xFF000000),
        Color(0x1BFFFFFF),
        Color(0x34FFFFFF)
    ),
    FROST_CRYSTAL(
        "frost_crystal",
        "Frosted Crystal Glass",
        "Smoky obsidian with translucent silver crystal glass",
        "❄️",
        Color(0xFFE2E8F0),
        Color(0xFF080B10),
        Color(0x22FFFFFF),
        Color(0x3DFFFFFF)
    ),
    SACRED_EMERALD(
        "emerald",
        "Midnight Emerald Glass",
        "Deep obsidian with sacred emerald frosted glass glow",
        "🟢",
        Color(0xFF10B981),
        Color(0xFF021B08),
        Color(0x2210B981),
        Color(0x4410B981)
    ),
    CELESTIAL_GOLD(
        "gold",
        "Celestial Amber Glass",
        "Deep dark void with warm golden amber glass glow",
        "🟡",
        Color(0xFFF59E0B),
        Color(0xFF0F0B04),
        Color(0x22F59E0B),
        Color(0x44F59E0B)
    ),
    DEEP_SAPPHIRE(
        "sapphire",
        "Deep Sapphire Glass",
        "Cosmic midnight blue with starlit sapphire glow",
        "🔵",
        Color(0xFF38BDF8),
        Color(0xFF030A14),
        Color(0x2238BDF8),
        Color(0x4438BDF8)
    )
}

object ThemeManager {
    private val _currentTheme = MutableStateFlow(AppCustomTheme.OLED_GLASS_BW)
    val currentTheme: StateFlow<AppCustomTheme> = _currentTheme.asStateFlow()

    fun setTheme(theme: AppCustomTheme) {
        _currentTheme.value = theme
    }

    fun setThemeById(id: String) {
        val theme = AppCustomTheme.entries.find { it.id == id } ?: AppCustomTheme.OLED_GLASS_BW
        _currentTheme.value = theme
    }
}
