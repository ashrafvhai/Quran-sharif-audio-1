package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

@Composable
fun EasyQuranifyTheme(
    content: @Composable () -> Unit
) {
    val activeCustomTheme by ThemeManager.currentTheme.collectAsState()

    val darkColors = darkColorScheme(
        primary = activeCustomTheme.primaryColor,
        onPrimary = Color.Black,
        primaryContainer = activeCustomTheme.cardBackground,
        onPrimaryContainer = Color.White,
        secondary = activeCustomTheme.primaryColor,
        onSecondary = Color.Black,
        background = activeCustomTheme.surfaceBackground,
        onBackground = Color.White,
        surface = activeCustomTheme.surfaceBackground,
        onSurface = Color.White,
        surfaceVariant = activeCustomTheme.cardBackground,
        onSurfaceVariant = Color.White.copy(alpha = 0.8f),
        outline = activeCustomTheme.cardBorder
    )

    MaterialTheme(
        colorScheme = darkColors,
        typography = Typography,
        content = content
    )
}
