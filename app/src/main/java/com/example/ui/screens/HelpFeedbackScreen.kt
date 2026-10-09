package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TapGameViewModel
import com.example.ui.theme.*

data class HelpMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpFeedbackScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<HelpMenuItem?>(null) }
    var showChatDialog by remember { mutableStateOf(false) }
    var chatMessage by remember { mutableStateOf("") }
    var chatLog by remember { mutableStateOf(listOf("Support: Hi! Welcome to TAP GAME live assistance. How can we help you today?")) }

    val menuItems = listOf(
        HelpMenuItem(
            title = "How to Play",
            subtitle = "Learn the game rules & flow",
            icon = Icons.Default.SportsEsports,
            iconColor = Color(0xFF8B5CF6),
            details = "1. Choose an Arena mode (Normal ₹50, Pro ₹100, High Rollers ₹200).\n2. Wait for matchmaking with a live rival.\n3. When the 5-second countdown ends, tap the glowing center bubble as fast as you can!\n4. When the 45-second clock expires, the player with the highest verified score wins the cash prize credited instantly to their wallet!"
        ),
        HelpMenuItem(
            title = "Deposit Guide",
            subtitle = "Step by step deposit process",
            icon = Icons.Default.Lock,
            iconColor = Color(0xFF0072FF),
            details = "Select UPI, PhonePe, Paytm, or Google Pay. Choose an amount (₹50 to ₹2,000) and click 'Proceed to Pay'. Funds are credited automatically with 100% security."
        ),
        HelpMenuItem(
            title = "Withdrawal Guide",
            subtitle = "Step by step withdrawal process",
            icon = Icons.Default.Security,
            iconColor = Color(0xFF00C6FF),
            details = "Minimum withdrawal is ₹100. Select your preferred payout method (UPI, Bank Transfer, Paytm Wallet, or UPI Lite). Enter your details and tap 'Request Withdrawal'. Funds are processed within 5-30 minutes."
        ),
        HelpMenuItem(
            title = "FAQs",
            subtitle = "Common questions & answers",
            icon = Icons.Default.HelpOutline,
            iconColor = Color(0xFF38BDF8),
            details = "Q: Is the game fair?\nA: Yes! Real-time anti-cheat rate limiting ensures all taps are human-speed verified.\n\nQ: What happens if there's a tie?\nA: Both players receive a 100% refund of their entry fee.\n\nQ: How fast are payouts?\nA: Withdrawals are processed within 5 to 30 minutes."
        ),
        HelpMenuItem(
            title = "Contact Us",
            subtitle = "Get in touch with us",
            icon = Icons.Default.Email,
            iconColor = Color(0xFF6366F1),
            details = "Email: support@tapgame.io\nLive Support: Available 24/7\nOfficial Telegram: @TapGameOfficial"
        ),
        HelpMenuItem(
            title = "Feedback",
            subtitle = "Share your feedback",
            icon = Icons.Default.RateReview,
            iconColor = Color(0xFF00F0FF),
            details = "We are continuously improving! Send your feature suggestions and feedback directly to our developer team."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Feedback", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 6 Menu Items exactly matching Screenshot
            items(menuItems.size) { index ->
                val item = menuItems[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
                        .clickable { selectedItem = item },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(item.iconColor.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = item.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.subtitle,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Details",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bottom Support Card: "Need Help? / We are here for you! / Chat Now"
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF091730))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .border(1.5.dp, NeonCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Headphones,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text("Need Help?", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("We are here for you!", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        // Chat Now Button Pill
                        Button(
                            onClick = { showChatDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text("Chat Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog for clicked guide
    if (selectedItem != null) {
        val item = selectedItem!!
        AlertDialog(
            onDismissRequest = { selectedItem = null },
            title = { Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(item.details, color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(onClick = { selectedItem = null }) {
                    Text("GOT IT", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }

    // Live Support Chat Dialog
    if (showChatDialog) {
        AlertDialog(
            onDismissRequest = { showChatDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Headphones, contentDescription = null, tint = NeonCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("24/7 Live Support", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    chatLog.forEach { line ->
                        Text(line, color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = chatMessage,
                        onValueChange = { chatMessage = it },
                        placeholder = { Text("Type your question...", fontSize = 12.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (chatMessage.isNotBlank()) {
                        chatLog = chatLog + "You: $chatMessage" + "Support: Thanks! Our specialist is reviewing your request."
                        chatMessage = ""
                    }
                }) {
                    Text("SEND", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChatDialog = false }) {
                    Text("CLOSE", color = TextMuted)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }
}
