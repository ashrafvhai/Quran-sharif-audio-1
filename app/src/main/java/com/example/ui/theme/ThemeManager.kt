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
        id = "oled_bw",
        displayName = "Monochrome Glass B&W",
        description = "Pure OLED black with crisp frosted glass and white accents",
        emoji = "⚪",
        primaryColor = Color(0xFFFFFFFF),
        surfaceBackground = Color(0xFF000000),
        cardBackground = Color(0x1AFFFFFF),
        cardBorder = Color(0x33FFFFFF)
    ),
    FROST_CRYSTAL(
        id = "frost_crystal",
        displayName = "Frosted Crystal Glass",
        description = "Smoky obsidian with translucent silver crystal glass",
        emoji = "❄️",
        primaryColor = Color(0xFFE2E8F0),
        surfaceBackground = Color(0xFF08090C),
        cardBackground = Color(0x22CBD5E1),
        cardBorder = Color(0x3DF1F5F9)
    ),
    SACRED_EMERALD(
        id = "emerald",
        displayName = "Midnight Emerald Glass",
        description = "Deep obsidian with sacred emerald frosted glass glow",
        emoji = "🟢",
        primaryColor = Color(0xFF10B981),
        surfaceBackground = Color(0xFF020F08),
        cardBackground = Color(0x2210B981),
        cardBorder = Color(0x4434D399)
    ),
    CELESTIAL_GOLD(
        id = "gold",
        displayName = "Celestial Amber Glass",
        description = "Deep dark void with warm golden amber glass glow",
        emoji = "🟡",
        primaryColor = Color(0xFFF59E0B),
        surfaceBackground = Color(0xFF0E0A04),
        cardBackground = Color(0x22F59E0B),
        cardBorder = Color(0x44FBBF24)
    ),
    DEEP_SAPPHIRE(
        id = "sapphire",
        displayName = "Deep Sapphire Glass",
        description = "Cosmic midnight blue with starlit sapphire glow",
        emoji = "🔵",
        primaryColor = Color(0xFF38BDF8),
        surfaceBackground = Color(0xFF030A14),
        cardBackground = Color(0x2238BDF8),
        cardBorder = Color(0x4460A5FA)
    )
}

object ThemeManager {
    private val _currentTheme = MutableStateFlow(AppCustomTheme.OLED_GLASS_BW)
    val currentTheme: StateFlow<AppCustomTheme> = _currentTheme.asStateFlow()

    fun setTheme(theme: AppCustomTheme) {
        _currentTheme.value = theme
    }

    fun setThemeById(id: String) {
        _currentTheme.value = AppCustomTheme.entries.find { it.id == id } ?: AppCustomTheme.OLED_GLASS_BW
    }
}
