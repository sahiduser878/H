package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: TapGameViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToMyGames: () -> Unit,
    onNavigateToTransactionHistory: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToWithdrawal: () -> Unit,
    onLogout: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser
    val balance = user?.availableBalance ?: 10.0

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(user?.displayName ?: "Player123") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // User Avatar matching Screenshot
            NeonAvatar(
                seed = user?.avatarSeed ?: "1",
                size = 72.dp,
                borderColor = Color(0xFF0072FF)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Name + Edit Icon
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = user?.displayName ?: "Player123",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit name",
                    tint = NeonCyan,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable {
                            editName = user?.displayName ?: "Player123"
                            showEditDialog = true
                        }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ID: 458736
            Text(
                text = "ID: ${user?.publicPlayerId ?: "458736"}",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Verified Badge (Green) matching Screenshot
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF10B981).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Verified", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Wallet Balance Card with [Withdraw] button matching Screenshot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(GoldYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("₹", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text("Wallet Balance", color = TextSecondary, fontSize = 11.sp)
                            Text("₹ ${balance.toInt()}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    // Withdraw Button Pill
                    OutlinedButton(
                        onClick = onNavigateToWithdrawal,
                        shape = RoundedCornerShape(16.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF0072FF))
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Withdraw", color = Color(0xFF00C6FF), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5 Menu Items matching Screenshot
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 1. My Games
                ProfileMenuItem(
                    title = "My Games",
                    subtitle = "View game history",
                    icon = Icons.Default.Person,
                    iconColor = Color(0xFF0072FF),
                    onClick = onNavigateToMyGames
                )

                // 2. Transaction History
                ProfileMenuItem(
                    title = "Transaction History",
                    subtitle = "Deposits & withdrawals",
                    icon = Icons.Default.ReceiptLong,
                    iconColor = Color(0xFF00C6FF),
                    onClick = onNavigateToTransactionHistory
                )

                // 3. Leaderboard
                ProfileMenuItem(
                    title = "Leaderboard",
                    subtitle = "Top players",
                    icon = Icons.Default.EmojiEvents,
                    iconColor = Color(0xFFFFB703),
                    onClick = onNavigateToLeaderboard
                )

                // 4. Settings
                ProfileMenuItem(
                    title = "Settings",
                    subtitle = "App preferences",
                    icon = Icons.Default.Settings,
                    iconColor = Color(0xFF8B5CF6),
                    onClick = onNavigateToSettings
                )

                // 5. Help & Support
                ProfileMenuItem(
                    title = "Help & Support",
                    subtitle = "Get assistance",
                    icon = Icons.Default.HelpOutline,
                    iconColor = Color(0xFF38BDF8),
                    onClick = onNavigateToHelp
                )

                // 6. LOGOUT Button (Prominent Transparent Glass Red)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.2.dp, NeonRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.logout()
                            onLogout()
                        }
                        .testTag("profile_logout_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NeonRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = NeonRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    "Log Out",
                                    color = NeonRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "Exit account session safely",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NeonRed.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Display Name") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateProfile(editName, user?.avatarSeed ?: "1")
                    showEditDialog = false
                }) {
                    Text("SAVE", color = NeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
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
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }

            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        }
    }
}
