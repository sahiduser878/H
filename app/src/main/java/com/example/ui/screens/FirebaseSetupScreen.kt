package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseSetupScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val firestoreRulesSnippet = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow update: if request.auth != null && request.auth.uid == userId
        && !request.resource.data.diff(resource.data).affectedKeys()
            .hasAny(['availableBalance', 'pendingBalance', 'role', 'status']);
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
    match /matches/{matchId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
    match /transactions/{txId} {
      allow read: if request.auth != null && (resource.data.userId == request.auth.uid || request.auth.token.admin == true);
      allow write: if request.auth != null && request.auth.token.admin == true;
    }
  }
}
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Architecture & Setup", color = TextPrimary, fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                NeonCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NavySurfaceElevated,
                    borderColor = NeonCyan
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("DEPLOYMENT STATUS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Realtime Local Engine Active", color = NeonCyan, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        }
                        NeonBadge(text = "ZERO-CONFIG READY", color = NeonGreen)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "The application runs an authoritative in-memory reactive data layer for smooth emulator testing, while providing full cloud schemas and security rules for production Firebase deployment.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            item {
                SectionHeader(title = "FIRESTORE DATABASE COLLECTIONS")
                Spacer(modifier = Modifier.height(6.dp))

                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    val collections = listOf(
                        "users" to "Private profiles, balances, auth status, KYC details",
                        "publicProfiles" to "Read-only display names, avatars, player IDs (#TAP-XXXX)",
                        "matches" to "Match IDs, player scores, final results, timestamps",
                        "matchQueue" to "Real-time active matchmaking queue pool",
                        "transactions" to "Immutable wallet ledger entries (entry fees, prizes, deposits)",
                        "depositRequests" to "User UPI UTR submission tickets for verification",
                        "withdrawalRequests" to "Payout requests awaiting admin or automated settlement",
                        "adminAuditLogs" to "Immutable audit trail of administrator adjustments"
                    )

                    collections.forEachIndexed { idx, (col, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(col, color = NeonCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(desc, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(start = 12.dp))
                        }
                        if (idx < collections.lastIndex) {
                            HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "PRODUCTION FIRESTORE SECURITY RULES")
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("firestore.rules", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            IconButton(
                                onClick = {
                                    val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cb.setPrimaryClip(ClipData.newPlainText("Rules", firestoreRulesSnippet))
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonCyan, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = firestoreRulesSnippet,
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            item {
                SectionHeader(title = "CLOUD FUNCTIONS DEPLOYMENT GUIDE")
                Spacer(modifier = Modifier.height(6.dp))

                NeonCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Authoritative functions required for high-stakes settlements:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. onMatchFinish: Validates client tap scores against rate limit thresholds and atomically credits prize pool.", color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("2. onDepositWebhook: Receives verified payment gateway callbacks and tops up user balances safely.", color = TextPrimary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("3. onMatchmakingTimeout: Auto-cancels stagnant queue entries and refunds reserved balances.", color = TextPrimary, fontSize = 12.sp)
                }
            }
        }
    }
}
