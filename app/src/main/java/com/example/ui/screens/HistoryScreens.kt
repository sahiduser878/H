package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.ui.TapGameViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHistoryScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit
) {
    val matches by viewModel.matchHistory.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredMatches = remember(matches, selectedFilter, user) {
        val userMatches = matches.filter { it.player1Id == user?.id || it.player2Id == user?.id }
        when (selectedFilter) {
            "WON" -> userMatches.filter { it.winnerId == user?.id }
            "LOST" -> userMatches.filter { it.winnerId != null && it.winnerId != user?.id }
            else -> userMatches
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match History", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All Matches (${matches.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "WON",
                    onClick = { selectedFilter = "WON" },
                    label = { Text("Won") }
                )
                FilterChip(
                    selected = selectedFilter == "LOST",
                    onClick = { selectedFilter = "LOST" },
                    label = { Text("Lost") }
                )
            }

            if (filteredMatches.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = TextMuted, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No match records found", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMatches) { match ->
                        val isP1 = match.player1Id == user?.id
                        val myScore = if (isP1) match.player1Score else match.player2Score
                        val oppScore = if (isP1) match.player2Score else match.player1Score
                        val oppName = (if (isP1) match.player2Name else match.player1Name) ?: "Opponent"
                        val oppAvatar = (if (isP1) match.player2Avatar else match.player1Avatar) ?: "2"
                        val won = match.winnerId == user?.id
                        val tie = match.winnerId == null

                        val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(match.timestamp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = NavySurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        NeonBadge(
                                            text = when {
                                                won -> "VICTORY"
                                                tie -> "TIED"
                                                else -> "DEFEAT"
                                            },
                                            color = when {
                                                won -> NeonGreen
                                                tie -> NeonAmber
                                                else -> NeonRed
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(match.mode.displayName, color = TextSecondary, fontSize = 12.sp)
                                    }

                                    Text(dateStr, color = TextMuted, fontSize = 11.sp)
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        NeonAvatar(seed = oppAvatar, size = 36.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("vs $oppName", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Score: $myScore - $oppScore", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }

                                    Text(
                                        text = if (won) "+₹${"%.0f".format(match.prizeAmount)}" else "-₹${"%.0f".format(match.entryFee)}",
                                        color = if (won) NeonGreen else TextSecondary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser

    var selectedFilter by remember { mutableStateOf("ALL") }

    val userTransactions = remember(transactions, selectedFilter, user) {
        val list = transactions.filter { it.userId == user?.id }
        when (selectedFilter) {
            "CREDIT" -> list.filter { it.amount > 0 }
            "DEBIT" -> list.filter { it.amount < 0 }
            else -> list
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet Ledger", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${transactions.size})") }
                )
                FilterChip(
                    selected = selectedFilter == "CREDIT",
                    onClick = { selectedFilter = "CREDIT" },
                    label = { Text("Credits (+)") }
                )
                FilterChip(
                    selected = selectedFilter == "DEBIT",
                    onClick = { selectedFilter = "DEBIT" },
                    label = { Text("Debits (-)") }
                )
            }

            if (userTransactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No transaction history yet", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(userTransactions) { tx ->
                        val isPositive = tx.amount >= 0
                        val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(tx.timestamp))

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
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                if (isPositive) NeonGreen.copy(alpha = 0.15f) else NeonRed.copy(alpha = 0.15f),
                                                RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (tx.type) {
                                                TransactionType.DEPOSIT -> Icons.Default.AddCircle
                                                TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
                                                TransactionType.MATCH_PRIZE -> Icons.Default.EmojiEvents
                                                TransactionType.MATCH_ENTRY -> Icons.Default.SportsEsports
                                                TransactionType.REFUND -> Icons.Default.Replay
                                                TransactionType.SIGNUP_BONUS -> Icons.Default.CardGiftcard
                                                TransactionType.ADMIN_ADJUSTMENT -> Icons.Default.Balance
                                            },
                                            contentDescription = null,
                                            tint = if (isPositive) NeonGreen else NeonRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = tx.type.name.replace("_", " "),
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = tx.reference.ifEmpty { "TX #${tx.id}" },
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        Text(dateStr, color = TextMuted, fontSize = 10.sp)
                                    }
                                }

                                Text(
                                    text = "${if (isPositive) "+" else ""}₹${"%.2f".format(tx.amount)}",
                                    color = if (isPositive) NeonGreen else TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
