package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun TapGameLogo(
    modifier: Modifier = Modifier,
    size: Dp = 150.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glowing circular crest with pointing hand & crown
        Box(
            modifier = Modifier
                .size(size * 0.75f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(NeonCyan.copy(alpha = 0.35f), NeonMagenta.copy(alpha = 0.2f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(size * 0.6f)
                    .clip(CircleShape)
                    .background(NavySurfaceElevated)
                    .border(2.5.dp, NeonCyan, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Tap",
                        tint = NeonCyan,
                        modifier = Modifier.size(size * 0.35f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // TAP GAME Title
        Text(
            text = "TAP",
            color = TextPrimary,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            lineHeight = 32.sp
        )
        Text(
            text = "GAME",
            color = NeonCyan,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Magenta Banner: ✦ TAP & WIN ✦
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF8B5CF6), Color(0xFFFF2A85))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = "✦ TAP & WIN ✦",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun NeonCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    borderColor: Color = NavyCardBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = NavySurface,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(borderWidth, borderColor, shape)
            .shadow(4.dp, shape, ambientColor = NeonCyanGlow, spotColor = NeonCyanGlow),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    gradient: Brush = CyanGradient,
    textColor: Color = Color.White,
    testTag: String = "neon_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color(0xFF1B2844)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (enabled) Modifier.background(gradient)
                    else Modifier.background(Color(0xFF1B2844))
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) textColor else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) textColor else TextMuted,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun NeonOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = NeonCyan,
    textColor: Color = TextPrimary,
    icon: ImageVector? = null,
    testTag: String = "neon_outline_button"
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(listOf(borderColor, borderColor))
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = NavySurface.copy(alpha = 0.6f),
            contentColor = textColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun NeonBadge(
    text: String,
    color: Color = NeonCyan,
    backgroundColor: Color = color.copy(alpha = 0.15f)
) {
    Box(
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NeonAvatar(
    seed: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    borderColor: Color = NeonCyan
) {
    val gradientColors = when (seed) {
        "1" -> listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
        "2" -> listOf(Color(0xFFFF2A85), Color(0xFF9333EA))
        "3" -> listOf(Color(0xFFFFD700), Color(0xFFFF5722))
        else -> listOf(NeonCyan, NeonPurple)
    }

    Box(
        modifier = modifier
            .size(size)
            .border(2.dp, borderColor, CircleShape)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            tint = Color.White,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { onActionClick() }
                    .padding(4.dp)
            )
        }
    }
}
