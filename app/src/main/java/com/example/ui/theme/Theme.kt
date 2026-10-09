package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun getDarkColorScheme(accentColor: AppThemeColor = AppThemeColor.CYAN_NEON) = darkColorScheme(
    primary = accentColor.primaryColor,
    onPrimary = Color(0xFF040814),
    primaryContainer = Color(0xFF0F3057),
    onPrimaryContainer = Color(0xFFBCE9FF),
    secondary = accentColor.secondaryColor,
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

fun getLightColorScheme(accentColor: AppThemeColor = AppThemeColor.CYAN_NEON) = lightColorScheme(
    primary = if (accentColor.primaryColor == NeonCyan) Color(0xFF0284C7) else accentColor.primaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = accentColor.secondaryColor,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFCE7F3),
    onSecondaryContainer = Color(0xFFBE185D),
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = Color(0xFFF1F5F9),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = NeonRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    themeColor: AppThemeColor = AppThemeColor.CYAN_NEON,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        getDarkColorScheme(themeColor)
    } else {
        getLightColorScheme(themeColor)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
