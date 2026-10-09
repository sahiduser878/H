package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit,
    onNavigateToFirebaseSetup: () -> Unit
) {
    var hapticFeedback by remember { mutableStateOf(true) }
    var soundEffects by remember { mutableStateOf(true) }
    var matchAlerts by remember { mutableStateOf(true) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            SectionHeader(title = "GAMEPLAY & AUDIO")
            Spacer(modifier = Modifier.height(8.dp))

            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Haptic Tap Vibration", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Vibrate on each verified tap touch", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = hapticFeedback,
                        onCheckedChange = { hapticFeedback = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0F3057))
                    )
                }

                HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Arena Sound Effects", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Play countdown and victory sounds", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = soundEffects,
                        onCheckedChange = { soundEffects = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0F3057))
                    )
                }

                HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Match Push Notifications", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Receive alerts when rival challenges you", color = TextSecondary, fontSize = 11.sp)
                    }
                    Switch(
                        checked = matchAlerts,
                        onCheckedChange = { matchAlerts = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = Color(0xFF0F3057))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "CLOUD BACKEND & ARCHITECTURE")
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .clickable { onNavigateToFirebaseSetup() },
                shape = RoundedCornerShape(14.dp),
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
                                .background(NeonCyan.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = NeonCyan)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Firebase & Cloud Architecture", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Inspect Firestore schema, rules & backend sync", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeonCyan)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            SectionHeader(title = "LEGAL & ABOUT")
            Spacer(modifier = Modifier.height(8.dp))

            NeonCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTermsDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Terms of Service", color = TextPrimary, fontSize = 14.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }

                HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPrivacyDialog = true }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Privacy Policy", color = TextPrimary, fontSize = 14.sp)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                }

                HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Version", color = TextSecondary, fontSize = 13.sp)
                    Text("2.4.0 (Build 2026)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service") },
            text = {
                Text(
                    "TAP GAME is a skill-based PvP speed competition. Users agree to fair play rules prohibiting automated clicking scripts, hardware macros, or reverse-engineered client mods. Violations result in immediate account restriction or permanent ban.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("AGREE", color = NeonCyan)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Text(
                    "We protect user credentials and financial records with strict access controls. No private authentication secrets, banking passwords, or personal identity numbers are ever shared with third parties or stored in unencrypted client assets.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("CLOSE", color = NeonCyan)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }
}
