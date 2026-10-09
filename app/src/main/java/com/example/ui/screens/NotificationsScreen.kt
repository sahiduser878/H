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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationType
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonBadge
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser

    val userNotes = remember(notifications, user) {
        notifications.filter { it.userId == user?.id }
    }

    LaunchedEffect(Unit) {
        viewModel.markNotificationsRead()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markNotificationsRead() }) {
                        Text("Mark Read", color = NeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyBackground)
            )
        },
        containerColor = NavyBackground
    ) { padding ->
        if (userNotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No notifications yet", color = TextSecondary, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(userNotes) { note ->
                    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(note.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (!note.isRead) 1.5.dp else 1.dp,
                                color = if (!note.isRead) NeonCyan else NavyCardBorder,
                                shape = RoundedCornerShape(14.dp)
                            ),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!note.isRead) NavySurfaceElevated else NavySurface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        when (note.type) {
                                            NotificationType.MATCH -> NeonGreen.copy(alpha = 0.15f)
                                            NotificationType.WALLET -> GoldYellow.copy(alpha = 0.15f)
                                            NotificationType.ADMIN -> NeonMagenta.copy(alpha = 0.15f)
                                            NotificationType.SYSTEM -> NeonCyan.copy(alpha = 0.15f)
                                        },
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (note.type) {
                                        NotificationType.MATCH -> Icons.Default.EmojiEvents
                                        NotificationType.WALLET -> Icons.Default.AccountBalanceWallet
                                        NotificationType.ADMIN -> Icons.Default.Shield
                                        NotificationType.SYSTEM -> Icons.Default.Campaign
                                    },
                                    contentDescription = null,
                                    tint = when (note.type) {
                                        NotificationType.MATCH -> NeonGreen
                                        NotificationType.WALLET -> GoldYellow
                                        NotificationType.ADMIN -> NeonMagenta
                                        NotificationType.SYSTEM -> NeonCyan
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = note.title,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    if (!note.isRead) {
                                        NeonBadge(text = "NEW", color = NeonCyan)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = note.message,
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = dateStr,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
