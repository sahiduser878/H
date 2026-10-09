package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMatch
import com.example.data.model.GameMode
import com.example.data.model.MatchStatus
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonAvatar
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonOutlineButton
import com.example.ui.theme.*

// ==========================================
// SCREEN 7: JOIN GAME
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinGameScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit,
    onNavigateToMatchmaking: () -> Unit,
    onNavigateToDeposit: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser
    val balance = user?.availableBalance ?: 1250.0

    var selectedMode by remember { mutableStateOf(GameMode.NORMAL) }

    val availableRooms = listOf(
        GameMode.NORMAL to 50.0,
        GameMode.PRO to 100.0,
        GameMode.HIGH_ROLLER to 200.0,
        GameMode.ELITE to 500.0
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Join Game", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBackground)
            )
        },
        containerColor = NavyBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Your Balance Card matching Screenshot
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF0072FF), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF091C3E))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0072FF).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("Your Balance", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                "₹ ${balance.toInt()}",
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Select Game Mode Section Title
            item {
                Text("Select Game Mode", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            // Game Mode Cards matching Screenshot (Normal, Pro, High Rollers)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Normal Mode (Popular)
                    ModeSelectCard(
                        title = "Normal Mode",
                        entryFee = 50.0,
                        isPopular = true,
                        isSelected = selectedMode == GameMode.NORMAL,
                        onClick = { selectedMode = GameMode.NORMAL }
                    )

                    // Pro Mode
                    ModeSelectCard(
                        title = "Pro Mode",
                        entryFee = 100.0,
                        isPopular = false,
                        isSelected = selectedMode == GameMode.PRO,
                        onClick = { selectedMode = GameMode.PRO }
                    )

                    // High Rollers
                    ModeSelectCard(
                        title = "High Rollers",
                        entryFee = 200.0,
                        isPopular = false,
                        isSelected = selectedMode == GameMode.HIGH_ROLLER,
                        onClick = { selectedMode = GameMode.HIGH_ROLLER }
                    )
                }
            }

            // Available Games Section Title
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text("Available Games", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            // Available Games Rooms List matching Screenshot
            items(availableRooms.size) { index ->
                val (mode, entryFee) = availableRooms[index]

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0072FF).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text("2 Players", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Entry ₹ ${entryFee.toInt()}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        // Join Button
                        Button(
                            onClick = {
                                if (balance >= entryFee) {
                                    viewModel.startMatchmaking(mode) {
                                        onNavigateToMatchmaking()
                                    }
                                } else {
                                    viewModel.showToast("Insufficient balance for ₹${entryFee.toInt()} room")
                                    onNavigateToDeposit()
                                }
                            },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF)),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("join_room_${entryFee.toInt()}")
                        ) {
                            Text("Join", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Footer note
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Minimum deposit required to join", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ModeSelectCard(
    title: String,
    entryFee: Double,
    isPopular: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) NeonCyan else NavyCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF0C2448) else NavySurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$title  •  Entry ₹ ${entryFee.toInt()}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isPopular) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Popular", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 8 & 9: WAITING FOR OPPONENT & GAME READY
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchmakingScreen(
    viewModel: TapGameViewModel,
    onCancel: () -> Unit,
    onMatchReady: () -> Unit
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val countdown by viewModel.countdownValue.collectAsState()
    val match = currentMatch

    BackHandler {
        viewModel.cancelMatchmaking {
            onCancel()
        }
    }

    LaunchedEffect(match?.status) {
        if (match?.status == MatchStatus.IN_PROGRESS) {
            onMatchReady()
        }
    }

    if (match == null || match.status == MatchStatus.WAITING) {
        // SCREEN 8: WAITING FOR OPPONENT
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Waiting for Opponent", color = TextPrimary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.cancelMatchmaking { onCancel() } }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextSecondary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBackground)
                )
            },
            containerColor = NavyBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Combatant Circles (You vs Opponent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // You (Cyan)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0072FF).copy(alpha = 0.2f))
                                .border(3.dp, Color(0xFF00C6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF00C6FF), modifier = Modifier.size(54.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("You", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("₹ ${match?.entryFee?.toInt() ?: 50}", color = TextSecondary, fontSize = 13.sp)
                    }

                    // VS
                    Text("VS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 26.sp)

                    // Opponent (Magenta)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF2A85).copy(alpha = 0.2f))
                                .border(3.dp, Color(0xFFFF2A85), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFFFF2A85), modifier = Modifier.size(54.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Opponent", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("₹ ${match?.entryFee?.toInt() ?: 50}", color = TextSecondary, fontSize = 13.sp)
                    }
                }

                // Searching Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Searching for opponent...", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(14.dp))
                    CircularProgressIndicator(
                        color = NeonCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Bottom Entry Fee & Total Prize Card + Cancel Button
                Column(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Entry Fee", color = TextSecondary, fontSize = 11.sp)
                                    Text("₹ ${match?.entryFee?.toInt() ?: 50}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Total Prize", color = TextSecondary, fontSize = 11.sp)
                                    Text("₹ ${match?.prizeAmount?.toInt() ?: 90}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 15.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    NeonOutlineButton(
                        text = "Cancel",
                        onClick = { viewModel.cancelMatchmaking { onCancel() } },
                        borderColor = NavyCardBorder,
                        textColor = TextSecondary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    } else {
        // SCREEN 9: GAME READY (5 SECONDS COUNTDOWN)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Game Ready", color = TextPrimary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.cancelMatchmaking { onCancel() } }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBackground)
                )
            },
            containerColor = NavyBackground
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Game will start in",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Big Glowing Countdown Ring matching Screenshot
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFF0072FF).copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                        .border(4.dp, Color(0xFF00C6FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$countdown",
                            color = Color(0xFF00F0FF),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Seconds",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Get Ready!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "The game will start automatically",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// SCREEN 10: LIVE TAPPING MATCH
// ==========================================
@Composable
fun LiveGameScreen(
    viewModel: TapGameViewModel,
    onMatchFinished: () -> Unit
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val remainingTime by viewModel.remainingTimeSeconds.collectAsState()
    val context = LocalContext.current

    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    LaunchedEffect(currentMatch?.status) {
        if (currentMatch?.status == MatchStatus.FINISHED) {
            onMatchFinished()
        }
    }

    val match = currentMatch ?: return
    val p1Score = match.player1Score
    val p2Score = match.player2Score

    var buttonScale by remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP STATUS BAR matching Screenshot:
            // Left: "You / 125", Center: "⏱ 00:45", Right: "Opponent / 98"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // You Pill
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2248)),
                    modifier = Modifier.border(1.dp, Color(0xFF00C6FF), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NeonAvatar(seed = match.player1Avatar, size = 26.dp, borderColor = NeonCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("You", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$p1Score", color = NeonCyan, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Time Remaining Pill (⏱ 00:45)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "00:${if (remainingTime < 10) "0$remainingTime" else "$remainingTime"}",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Opponent Pill
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF330C24)),
                    modifier = Modifier.border(1.dp, Color(0xFFFF2A85), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$p2Score", color = NeonMagenta, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Opponent", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonAvatar(seed = match.player2Avatar, size = 26.dp, borderColor = NeonMagenta)
                    }
                }
            }

            // CENTER ARENA: Floating glowing bubbles matching Screenshot!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Giant Glowing Tap Target
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF00E5FF), Color(0xFF7C3AED), Color(0xFFFF2A85))
                            )
                        )
                        .border(4.dp, Color(0xFF00F0FF), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (viewModel.registerTap()) {
                                buttonScale = 0.94f
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator?.vibrate(15)
                                    }
                                } catch (_: Exception) {}
                            }
                        }
                        .testTag("live_tapping_arena"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TAP!",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                }

                LaunchedEffect(buttonScale) {
                    if (buttonScale < 1f) {
                        buttonScale = 1f
                    }
                }
            }

            // Prompt: "Tap as fast as you can!" with hand icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(Icons.Default.TouchApp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tap as fast as you can!", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // BOTTOM SCORE COMPARISON BAR matching Screenshot:
            // Progress bar comparing You vs Opponent scores
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeonAvatar(seed = match.player1Avatar, size = 24.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("You: $p1Score", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Opponent: $p2Score", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            NeonAvatar(seed = match.player2Avatar, size = 24.dp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dual Color Progress Bar
                    val total = (p1Score + p2Score).coerceAtLeast(1)
                    val p1Ratio = p1Score.toFloat() / total

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(p1Ratio.coerceAtLeast(0.05f))
                                .fillMaxHeight()
                                .background(Color(0xFF00C6FF))
                        )
                        Box(
                            modifier = Modifier
                                .weight((1f - p1Ratio).coerceAtLeast(0.05f))
                                .fillMaxHeight()
                                .background(Color(0xFFFF2A85))
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 11: GAME RESULT
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameResultScreen(
    viewModel: TapGameViewModel,
    onPlayAgain: () -> Unit,
    onReturnHome: () -> Unit
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    val match = currentMatch ?: return
    val user = currentUser
    val isWinner = match.winnerId == user?.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Result", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.dismissMatch(); onReturnHome() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBackground)
            )
        },
        containerColor = NavyBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Golden Crown & Ribbon: "Winner!" matching Screenshot
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = GoldYellow,
                    modifier = Modifier.size(68.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFB703), Color(0xFFFB8500))
                            )
                        )
                        .padding(horizontal = 32.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isWinner) "Winner!" else "Match Over!",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Two Result Cards (You vs Opponent) matching Screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // You Result Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (isWinner) 2.dp else 1.dp,
                            color = if (isWinner) Color(0xFF00C6FF) else NavyCardBorder,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF091C3E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NeonAvatar(seed = match.player1Avatar, size = 48.dp, borderColor = NeonCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${match.player1Score}", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)

                        if (isWinner) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0072FF).copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("+ ₹ ${match.prizeAmount.toInt()} (Prize)", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Opponent Result Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (!isWinner) 2.dp else 1.dp,
                            color = if (!isWinner) Color(0xFFFF2A85) else NavyCardBorder,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF280B20))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        NeonAvatar(seed = match.player2Avatar, size = 48.dp, borderColor = NeonMagenta)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(match.player2Name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${match.player2Score}", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)

                        if (!isWinner) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFF2A85).copy(alpha = 0.3f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("+ ₹ ${match.prizeAmount.toInt()} (Prize)", color = NeonMagenta, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Total Prize Card matching Screenshot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Prize", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("🪙 ₹ ${match.prizeAmount.toInt()}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            // Action Buttons matching Screenshot: Play Again (Gradient) & Back to Home (Outlined)
            Column(modifier = Modifier.fillMaxWidth()) {
                NeonButton(
                    text = "Play Again",
                    onClick = {
                        viewModel.dismissMatch()
                        onPlayAgain()
                    },
                    gradient = CyanGradient,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeonOutlineButton(
                    text = "Back to Home",
                    onClick = {
                        viewModel.dismissMatch()
                        onReturnHome()
                    },
                    borderColor = NavyCardBorder,
                    textColor = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
