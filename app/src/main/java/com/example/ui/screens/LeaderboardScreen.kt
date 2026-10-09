package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonAvatar
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    viewModel: TapGameViewModel
) {
    val leaderboard by viewModel.leaderboard.collectAsState()
    var selectedTab by remember { mutableStateOf(0) } // 0: Global, 1: Weekly

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TAP CHAMPIONSHIPS",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Text(
            text = "Top speed tap warriors & tournament prize earners",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavySurface,
            contentColor = NeonCyan,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("All Time Masters", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Weekly League", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top 3 Podium Cards
        if (leaderboard.size >= 3) {
            val first = leaderboard[0]
            val second = leaderboard[1]
            val third = leaderboard[2]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Rank 2 Podium
                PodiumColumn(
                    entry = second,
                    rank = 2,
                    rankColor = Color(0xFFC0C0C0),
                    height = 110.dp,
                    modifier = Modifier.weight(1f)
                )

                // Rank 1 Podium (Tallest)
                PodiumColumn(
                    entry = first,
                    rank = 1,
                    rankColor = GoldYellow,
                    height = 135.dp,
                    modifier = Modifier.weight(1f)
                )

                // Rank 3 Podium
                PodiumColumn(
                    entry = third,
                    rank = 3,
                    rankColor = Color(0xFFCD7F32),
                    height = 95.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full leaderboard list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(leaderboard) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (item.isCurrentUser) 1.5.dp else 1.dp,
                            color = if (item.isCurrentUser) NeonCyan else NavyCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isCurrentUser) NavySurfaceElevated else NavySurface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "#${item.rank}",
                                color = when (item.rank) {
                                    1 -> GoldYellow
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> TextSecondary
                                },
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                modifier = Modifier.width(32.dp)
                            )
                            NeonAvatar(seed = item.avatar, size = 36.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.displayName,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (item.isCurrentUser) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        NeonBadge(text = "YOU", color = NeonCyan)
                                    }
                                }
                                Text(
                                    text = "${item.wins} Wins • Win Rate ${item.winRate}%",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "₹${"%.0f".format(item.earnings)}",
                            color = GoldYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: com.example.data.model.LeaderboardEntry,
    rank: Int,
    rankColor: Color,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NeonAvatar(seed = entry.avatar, size = 44.dp, borderColor = rankColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = entry.displayName.take(8),
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "₹${"%.0f".format(entry.earnings)}",
            color = GoldYellow,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .border(1.dp, rankColor.copy(alpha = 0.5f), RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = rankColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "#$rank",
                        color = rankColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
