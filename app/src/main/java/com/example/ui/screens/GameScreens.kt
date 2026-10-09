package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMatch
import com.example.data.model.GameMode
import com.example.data.model.MatchStatus
import com.example.ui.TapGameViewModel
import com.example.ui.components.GlassmorphicButton
import com.example.ui.components.NeonAvatar
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonOutlineButton
import com.example.ui.theme.*

// ==========================================
// SCREEN 7: JOIN GAME & MATCH ID CREATOR/JOINER
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
    val allUsers by viewModel.allUsers.collectAsState()
    val waitingMatches by viewModel.waitingMatches.collectAsState()
    val user = currentUser
    val balance = user?.availableBalance ?: 1250.0

    var selectedTab by remember { mutableStateOf(0) } // 0: Rooms, 1: Enter Match ID
    var selectedMode by remember { mutableStateOf(GameMode.MICRO) }
    var matchIdInput by remember { mutableStateOf("") }
    var searchedMatch by remember { mutableStateOf<GameMatch?>(null) }
    var searchError by remember { mutableStateOf<String?>(null) }
    var isSearching by remember { mutableStateOf(false) }

    val availableRooms = listOf(
        GameMode.MICRO to 10.0,
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

            // Mode Selector Tabs (Rooms vs Match ID)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavySurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 0) Color(0xFF0072FF) else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Game Rooms (₹10 - ₹500)",
                            color = if (selectedTab == 0) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedTab == 1) Color(0xFF0072FF) else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Join with Match ID",
                            color = if (selectedTab == 1) Color.White else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            if (selectedTab == 0) {
                // Select Game Mode Section Title
                item {
                    Text("Select Game Mode", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                // Game Mode Cards (Micro ₹10, Normal ₹50, Pro ₹100, High Rollers ₹200)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModeSelectCard(
                            title = "₹10 Micro Match",
                            entryFee = 10.0,
                            prizePool = 18.0,
                            isPopular = true,
                            isSelected = selectedMode == GameMode.MICRO,
                            onClick = { selectedMode = GameMode.MICRO }
                        )

                        ModeSelectCard(
                            title = "Normal Mode",
                            entryFee = 50.0,
                            prizePool = 90.0,
                            isPopular = true,
                            isSelected = selectedMode == GameMode.NORMAL,
                            onClick = { selectedMode = GameMode.NORMAL }
                        )

                        ModeSelectCard(
                            title = "Pro Mode",
                            entryFee = 100.0,
                            prizePool = 180.0,
                            isPopular = false,
                            isSelected = selectedMode == GameMode.PRO,
                            onClick = { selectedMode = GameMode.PRO }
                        )

                        ModeSelectCard(
                            title = "High Rollers",
                            entryFee = 200.0,
                            prizePool = 360.0,
                            isPopular = false,
                            isSelected = selectedMode == GameMode.HIGH_ROLLER,
                            onClick = { selectedMode = GameMode.HIGH_ROLLER }
                        )
                    }
                }

                // Available Games Section Title
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Available 1v1 Rooms", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                // Available Games Rooms List matching Screenshot with ₹10 room first
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
                                    Text("2 Players • 1v1", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        "Entry ₹ ${entryFee.toInt()} • Prize ₹ ${(entryFee * 1.8).toInt()}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Create Match & Get 6-digit Code Button
                            Button(
                                onClick = {
                                    if (balance >= entryFee) {
                                        viewModel.createCustomMatch(mode, entryFee) {
                                            onNavigateToMatchmaking()
                                        }
                                    } else {
                                        viewModel.showToast("Insufficient balance for ₹${entryFee.toInt()} room")
                                        onNavigateToDeposit()
                                    }
                                },
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF)),
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("join_room_${entryFee.toInt()}")
                            ) {
                                Text("Create", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            } else {
                // ==========================================
                // TAB 1: JOIN USING 6-DIGIT MATCH ID
                // ==========================================
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Enter 6-Digit Match ID",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Enter the code shared by the room creator to play together in real-time.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                            )

                            OutlinedTextField(
                                value = matchIdInput,
                                onValueChange = {
                                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                        matchIdInput = it
                                        searchError = null
                                    }
                                },
                                placeholder = { Text("e.g. 748291", color = TextMuted) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("match_id_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = NavyCardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            GlassmorphicButton(
                                text = if (isSearching) "Searching..." else "Search Match",
                                onClick = {
                                    if (matchIdInput.length == 6) {
                                        isSearching = true
                                        searchError = null
                                        viewModel.searchMatchByCode(matchIdInput) { res ->
                                            isSearching = false
                                            res.onSuccess {
                                                searchedMatch = it
                                            }.onFailure {
                                                searchedMatch = null
                                                searchError = it.message ?: "Match not found"
                                            }
                                        }
                                    } else {
                                        searchError = "Please enter a valid 6-digit Match ID"
                                    }
                                },
                                accentGlow = NeonCyan,
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "search_match_button"
                            )

                            searchError?.let { err ->
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "⚠️ $err",
                                    color = NeonMagenta,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Searched Match Preview Card
                searchedMatch?.let { match ->
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, NeonGreen, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2448))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        NeonAvatar(seed = match.player1Avatar, size = 36.dp, borderColor = NeonCyan)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Host: ${match.player1Name}",
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "Match ID #${match.matchCode}",
                                                color = NeonCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(NeonGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("WAITING", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                HorizontalDivider(color = NavyCardBorder)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Entry Fee", color = TextSecondary, fontSize = 11.sp)
                                        Text("₹ ${match.entryFee.toInt()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Total Prize", color = TextSecondary, fontSize = 11.sp)
                                        Text("₹ ${match.prizeAmount.toInt()}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Mode", color = TextSecondary, fontSize = 11.sp)
                                        Text(match.mode.displayName, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                GlassmorphicButton(
                                    text = "Join Match (₹${match.entryFee.toInt()})",
                                    onClick = {
                                        viewModel.joinMatchByCode(match.matchCode) {
                                            onNavigateToMatchmaking()
                                        }
                                    },
                                    accentGlow = NeonMagenta,
                                    modifier = Modifier.fillMaxWidth().testTag("join_searched_match_button")
                                )
                            }
                        }
                    }
                }

                // Open Waiting Matches List (Instant Join)
                if (waitingMatches.isNotEmpty()) {
                    item {
                        Text("Active Waiting Matches", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    items(waitingMatches.size) { idx ->
                        val m = waitingMatches[idx]
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
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    NeonAvatar(seed = m.player1Avatar, size = 32.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("${m.player1Name} • #${m.matchCode}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Entry ₹${m.entryFee.toInt()} • Prize ₹${m.prizeAmount.toInt()}", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                Button(
                                    onClick = {
                                        viewModel.joinMatchByCode(m.matchCode) {
                                            onNavigateToMatchmaking()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(14.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text("Join", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Quick Account Switcher for testing real multiplayer on 1 emulator
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "⚡ Multi-Player Testing Tool",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "Currently playing as: ${user?.displayName} (${user?.publicPlayerId}). Switch account below to test Player 2 joining this match on the emulator:",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allUsers.take(2).forEach { account ->
                                    val isCurrent = account.id == user?.id
                                    OutlinedButton(
                                        onClick = {
                                            if (!isCurrent) {
                                                viewModel.switchUser(account.id)
                                                viewModel.showToast("Switched to ${account.displayName}")
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (isCurrent) NeonCyan.copy(alpha = 0.2f) else Color.Transparent
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = account.displayName,
                                            color = if (isCurrent) NeonCyan else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
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
                    Text("100% Real Players • No Bots • Instant Settlement", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ModeSelectCard(
    title: String,
    entryFee: Double,
    prizePool: Double,
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
                Column {
                    Text(
                        text = "$title  •  Entry ₹ ${entryFee.toInt()}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Prize Pool: ₹ ${prizePool.toInt()}",
                        color = GoldYellow,
                        fontSize = 11.sp
                    )
                }
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
    val context = LocalContext.current

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
        // SCREEN 8: WAITING FOR REAL OPPONENT WITH 6-DIGIT MATCH ID
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Matchmaking Lobby", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 1. Prominent 6-Digit Match ID Card with Copy Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.5.dp, Color(0xFF0072FF), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2248))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "SHARE MATCH ID WITH OPPONENT",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val displayCode = match?.matchCode ?: "849201"
                        Text(
                            text = displayCode.chunked(3).joinToString(" "),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Match ID", displayCode)
                                clipboard.setPrimaryClip(clip)
                                viewModel.showToast("Match ID #$displayCode copied to clipboard!")
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C6FF)),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("copy_match_id_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Match ID", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                // 2. Combatant Circles (You vs Waiting Opponent)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // You (Cyan)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0072FF).copy(alpha = 0.2f))
                                .border(3.dp, Color(0xFF00C6FF), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            NeonAvatar(seed = match?.player1Avatar ?: "1", size = 68.dp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(match?.player1Name ?: "You", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Host", color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }

                    // VS
                    Text("VS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 24.sp)

                    // Opponent (Magenta Waiting)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF2A85).copy(alpha = 0.2f))
                                .border(3.dp, Color(0xFFFF2A85).copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Color(0xFFFF2A85), modifier = Modifier.size(42.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Waiting...", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Real Player", color = TextMuted, fontSize = 11.sp)
                    }
                }

                // 3. Searching Pulse Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Waiting for opponent to join...",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Anyone with Match ID #${match?.matchCode ?: "849201"} can join immediately",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    CircularProgressIndicator(
                        color = NeonCyan,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 4. Match Summary & Cancel Button
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
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Entry Fee", color = TextSecondary, fontSize = 11.sp)
                                    Text("₹ ${match?.entryFee?.toInt() ?: 10}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldYellow, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Total Prize", color = TextSecondary, fontSize = 11.sp)
                                    Text("₹ ${match?.prizeAmount?.toInt() ?: 18}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    NeonOutlineButton(
                        text = "Cancel & Refund Entry Fee",
                        onClick = { viewModel.cancelMatchmaking { onCancel() } },
                        borderColor = NavyCardBorder,
                        textColor = TextSecondary,
                        modifier = Modifier.fillMaxWidth().testTag("cancel_matchmaking_button")
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } else {
        // SCREEN 9: GAME READY (5 SECONDS COUNTDOWN)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Game Ready!", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                // Combatant Header: Player 1 vs Player 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        NeonAvatar(seed = match.player1Avatar, size = 52.dp, borderColor = NeonCyan)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(match.player1Name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Text("⚔️", fontSize = 24.sp)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        NeonAvatar(seed = match.player2Avatar ?: "2", size = 52.dp, borderColor = NeonMagenta)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(match.player2Name ?: "Challenger", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Text(
                    text = "Battle Starts In",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Big Glowing Countdown Ring
                Box(
                    modifier = Modifier
                        .size(200.dp)
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
                            fontSize = 68.sp,
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
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Text(
                        text = "Real 1v1 Battle!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Match #${match.matchCode} • Both players connected",
                        color = NeonGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ==========================================
// SCREEN 10: LIVE TAPPING MATCH (PURE REAL PLAYERS)
// ==========================================
@Composable
fun LiveGameScreen(
    viewModel: TapGameViewModel,
    onMatchFinished: () -> Unit
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val remainingTime by viewModel.remainingTimeSeconds.collectAsState()
    val context = LocalContext.current

    var isSplitMode by remember { mutableStateOf(false) } // Optional Pass-and-play 2-player split

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

    var buttonScale1 by remember { mutableStateOf(1f) }
    var buttonScale2 by remember { mutableStateOf(1f) }

    fun vibrateTap() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(15)
            }
        } catch (_: Exception) {}
    }

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
            // Left: Player 1 / Score, Center: ⏱ 00:45, Right: Player 2 / Score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player 1 Pill
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
                        Text(match.player1Name, color = TextSecondary, fontSize = 11.sp)
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

                // Player 2 Pill
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
                        Text(match.player2Name ?: "Opponent", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonAvatar(seed = match.player2Avatar ?: "2", size = 26.dp, borderColor = NeonMagenta)
                    }
                }
            }

            // Mode Toggle (Single Player vs 2-Player Split Arena)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavySurface)
                    .padding(2.dp)
            ) {
                Text(
                    text = "1-Player Tap",
                    color = if (!isSplitMode) NeonCyan else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isSplitMode = false }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Text(
                    text = "2-Player Split (1-Screen PvP)",
                    color = if (isSplitMode) NeonMagenta else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { isSplitMode = true }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // CENTER ARENA: Tapping targets
            if (!isSplitMode) {
                // SINGLE TAP ARENA (Target for current player)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Interactive Giant Glowing Tap Target
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .scale(buttonScale1)
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
                                    if (viewModel.registerTap(forPlayer2 = false)) {
                                        buttonScale1 = 0.94f
                                        vibrateTap()
                                    }
                                }
                                .testTag("live_tapping_arena"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(52.dp))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "TAP!",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Challenger Tap button (For testing or dual-control)
                        Button(
                            onClick = {
                                if (viewModel.registerTap(forPlayer2 = true)) {
                                    vibrateTap()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF280B20)),
                            modifier = Modifier.border(1.dp, Color(0xFFFF2A85), RoundedCornerShape(16.dp))
                        ) {
                            Text(
                                "Challenger Tap (+1 for ${match.player2Name ?: "P2"})",
                                color = NeonMagenta,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    LaunchedEffect(buttonScale1) {
                        if (buttonScale1 < 1f) {
                            buttonScale1 = 1f
                        }
                    }
                }
            } else {
                // 2-PLAYER SPLIT ARENA (Pass-and-play side-by-side)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Player 1 Target
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .scale(buttonScale1)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF07214A))
                            .border(2.dp, Color(0xFF00C6FF), RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (viewModel.registerTap(forPlayer2 = false)) {
                                    buttonScale1 = 0.95f
                                    vibrateTap()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(match.player1Name, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$p1Score", color = NeonCyan, fontSize = 36.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TAP HERE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    // Right Player 2 Target
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .scale(buttonScale2)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF3B0C2A))
                            .border(2.dp, Color(0xFFFF2A85), RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (viewModel.registerTap(forPlayer2 = true)) {
                                    buttonScale2 = 0.95f
                                    vibrateTap()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(match.player2Name ?: "Challenger", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("$p2Score", color = NeonMagenta, fontSize = 36.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(8.dp))
                            Icon(Icons.Default.TouchApp, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("TAP HERE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    LaunchedEffect(buttonScale1) {
                        if (buttonScale1 < 1f) buttonScale1 = 1f
                    }
                    LaunchedEffect(buttonScale2) {
                        if (buttonScale2 < 1f) buttonScale2 = 1f
                    }
                }
            }

            // Prompt: "Tap as fast as you can!"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Real-Time Battle • Tap as fast as you can!", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // BOTTOM SCORE COMPARISON BAR matching Screenshot
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
                            NeonAvatar(seed = match.player1Avatar, size = 22.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${match.player1Name}: $p1Score", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${match.player2Name ?: "Opponent"}: $p2Score", color = NeonMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            NeonAvatar(seed = match.player2Avatar ?: "2", size = 22.dp)
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
    val isTie = match.winnerId == null

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
                                if (isTie) listOf(Color(0xFF0072FF), Color(0xFF00C6FF))
                                else listOf(Color(0xFFFFB703), Color(0xFFFB8500))
                            )
                        )
                        .padding(horizontal = 32.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            isTie -> "It's a Tie! (Refunded)"
                            isWinner -> "Victory! Winner!"
                            else -> "Match Over!"
                        },
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Two Result Cards (Player 1 vs Player 2) matching Screenshot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Player 1 Result Card
                val p1Won = match.winnerId == match.player1Id
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (p1Won) 2.dp else 1.dp,
                            color = if (p1Won) Color(0xFF00C6FF) else NavyCardBorder,
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
                        Text(match.player1Name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${match.player1Score}", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)

                        if (p1Won) {
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

                // Player 2 Result Card
                val p2Won = match.winnerId == match.player2Id
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = if (p2Won) 2.dp else 1.dp,
                            color = if (p2Won) Color(0xFFFF2A85) else NavyCardBorder,
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
                        NeonAvatar(seed = match.player2Avatar ?: "2", size = 48.dp, borderColor = NeonMagenta)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(match.player2Name ?: "Challenger", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${match.player2Score}", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)

                        if (p2Won) {
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
                    Text("Total Prize Awarded", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("🪙 ₹ ${match.prizeAmount.toInt()}", color = GoldYellow, fontWeight = FontWeight.Black, fontSize = 18.sp)
                }
            }

            // Action Buttons: Play Again & Back to Home
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
