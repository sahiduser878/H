package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TapGameColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF040814),
    primaryContainer = Color(0xFF0F3057),
    onPrimaryContainer = Color(0xFFBCE9FF),
    secondary = NeonMagenta,
    onSecondary = Color(0xFF260012),
    secondaryContainer = Color(0xFF4C0E28),
    onSecondaryContainer = Color(0xFFFFD8E5),
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = NavyBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NavyCardBorder,
    error = NeonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Always enforce cohesive neon arcade palette
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TapGameColorScheme,
        typography = Typography,
        content = content
    )
}
