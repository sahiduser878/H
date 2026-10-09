package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Default Dark Theme Colors
val NavyBackground = Color(0xFF060B17)
val NavySurface = Color(0xFF0D172E)
val NavySurfaceElevated = Color(0xFF132242)
val NavyCardBorder = Color(0xFF1E3566)
val NavyGlassFill = Color(0xCC0D172E)

// Neon Accent Colors
val NeonCyan = Color(0xFF00F0FF)
val NeonCyanGlow = Color(0x3300F0FF)
val NeonMagenta = Color(0xFFFF2A85)
val NeonMagentaGlow = Color(0x33FF2A85)
val NeonPurple = Color(0xFF9333EA)
val NeonBlue = Color(0xFF3B82F6)
val NeonGreen = Color(0xFF10B981)
val NeonAmber = Color(0xFFF59E0B)
val NeonRed = Color(0xFFEF4444)
val GoldYellow = Color(0xFFFFD700)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Gradients
val PrimaryNeonGradient = Brush.horizontalGradient(
    colors = listOf(NeonCyan, NeonPurple, NeonMagenta)
)

val CyanGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
)

val MagentaGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFFF0844), Color(0xFFFFB199))
)

val CardGlowBorder = Brush.linearGradient(
    colors = listOf(Color(0xFF00F0FF), Color(0xFF9333EA), Color(0xFFFF2A85))
)

// Accent Themes selectable by User in Settings
enum class AppThemeColor(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val previewGradient: Brush
) {
    CYAN_NEON(
        title = "Cyber Cyan",
        primaryColor = Color(0xFF00F0FF),
        secondaryColor = Color(0xFFFF2A85),
        previewGradient = Brush.horizontalGradient(listOf(Color(0xFF00F0FF), Color(0xFF0072FF)))
    ),
    GOLD_ROYAL(
        title = "Royal Gold",
        primaryColor = Color(0xFFFFD700),
        secondaryColor = Color(0xFFFF8C00),
        previewGradient = Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF5E00)))
    ),
    EMERALD_GREEN(
        title = "Emerald Win",
        primaryColor = Color(0xFF10B981),
        secondaryColor = Color(0xFF06B6D4),
        previewGradient = Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
    ),
    ELECTRIC_PURPLE(
        title = "Electric Violet",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFFEC4899),
        previewGradient = Brush.horizontalGradient(listOf(Color(0xFFA855F7), Color(0xFFEC4899)))
    ),
    FIRE_RUBY(
        title = "Ruby Flame",
        primaryColor = Color(0xFFFF3366),
        secondaryColor = Color(0xFFFF9900),
        previewGradient = Brush.horizontalGradient(listOf(Color(0xFFFF3366), Color(0xFFFF6600)))
    )
}
