package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameMode
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonAvatar
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: TapGameViewModel,
    onNavigateToJoinGame: () -> Unit,
    onNavigateToDeposit: () -> Unit,
    onNavigateToWithdrawal: () -> Unit,
    onNavigateToMatchmaking: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Bar: Avatar, Player Name, ID and Wallet Balance Pill with (+) Button
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigateToProfile() }
                ) {
                    NeonAvatar(
                        seed = user?.avatarSeed ?: "1",
                        size = 44.dp,
                        borderColor = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = user?.displayName ?: "Player123",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${user?.publicPlayerId ?: "458736"}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Balance Pill with (+) button matching Screenshot
                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Gold Coin icon
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(GoldYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("₹", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "${user?.availableBalance?.toInt() ?: 1250}",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Green Plus Button
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(NeonGreen)
                                .clickable { onNavigateToDeposit() }
                                .testTag("home_add_funds_plus"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add funds",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Hero Banner: TAP & WIN - 2 PLAYER GAME
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, NeonCyan.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
                    .clickable { onNavigateToJoinGame() },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F326D), Color(0xFF1F0D42), Color(0xFF071126))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left 3D Neon Tap Hand Icon
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .border(2.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "TAP & WIN",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonCyan.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "2 PLAYER GAME",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Fast • Fun • Real Rewards",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 3. Stats Row (Total Games, Won, Win Rate)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Games
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Games", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${user?.totalGames ?: 12}",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Won
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Won", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${user?.matchesWon ?: 8}",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Win Rate
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Win Rate", color = TextSecondary, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${user?.winRate ?: 66}%",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // 4. Primary Actions (Join Game & Quick Play)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Join Game Card (Electric Cyan / Blue Gradient)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigateToJoinGame() }
                        .testTag("action_join_game"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0072FF), Color(0xFF0052CC))
                                )
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Join Game",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Find Opponent & Play",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Quick Play Card (Vibrant Magenta Gradient)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            viewModel.startMatchmaking(GameMode.NORMAL) {
                                onNavigateToMatchmaking()
                            }
                        }
                        .testTag("action_quick_play"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFFFF2A85), Color(0xFF9333EA))
                                )
                            )
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Quick Play",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Instant Match",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 5. Secondary Actions (Deposit & Withdrawal)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Deposit Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToDeposit() }
                        .testTag("action_deposit"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Deposit", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Add Money", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Withdrawal Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToWithdrawal() }
                        .testTag("action_withdrawal"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonMagenta.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowOutward,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Withdrawal", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("Withdraw Winnings", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
