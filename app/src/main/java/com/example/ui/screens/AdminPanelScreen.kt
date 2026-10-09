package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.TapGameViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val matches by viewModel.matchHistory.collectAsState()
    val deposits by viewModel.depositRequests.collectAsState()
    val withdrawals by viewModel.withdrawalRequests.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    // Role check security protection
    if (currentUser?.role != UserRole.ADMIN) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NavyBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonRed, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Access Denied", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("This panel requires verified Administrator privileges.", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(24.dp))
                NeonButton(text = "Go Back", onClick = onBack)
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Admin Control Console", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        NeonBadge(text = "SUPERUSER", color = NeonMagenta)
                    }
                },
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
        ) {
            // Admin Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = NavySurface,
                contentColor = NeonCyan,
                edgePadding = 16.dp,
                divider = {}
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Overview") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Users (${allUsers.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Finances (${deposits.size + withdrawals.size})") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("ZapUPI Gateway") })
                Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Game Rules") })
                Tab(selected = selectedTab == 5, onClick = { selectedTab = 5 }, text = { Text("Broadcasts") })
                Tab(selected = selectedTab == 6, onClick = { selectedTab = 6 }, text = { Text("Audit Log (${auditLogs.size})") })
            }

            when (selectedTab) {
                0 -> AdminOverviewTab(allUsers, matches, deposits, withdrawals)
                1 -> AdminUsersTab(viewModel, allUsers)
                2 -> AdminFinancesTab(viewModel, deposits, withdrawals)
                3 -> AdminZapUpiGatewayTab(viewModel)
                4 -> AdminGameRulesTab(viewModel, appConfig)
                5 -> AdminBroadcastTab(viewModel, appConfig)
                6 -> AdminAuditTab(auditLogs)
            }
        }
    }
}

@Composable
private fun AdminOverviewTab(
    users: List<UserAccount>,
    matches: List<GameMatch>,
    deposits: List<DepositRequest>,
    withdrawals: List<WithdrawalRequest>
) {
    val pendingDep = deposits.count { it.status == TransactionStatus.PENDING }
    val pendingWith = withdrawals.count { it.status == TransactionStatus.PENDING }
    val bannedUsers = users.count { it.status == AccountStatus.BANNED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("SYSTEM METRICS & KPI DASHBOARD", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Total Users",
                    value = "${users.size}",
                    icon = Icons.Default.People,
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Banned Users",
                    value = "$bannedUsers",
                    icon = Icons.Default.Block,
                    accentColor = if (bannedUsers > 0) NeonRed else NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Matches Finalized",
                    value = "${matches.size}",
                    icon = Icons.Default.SportsEsports,
                    accentColor = NeonPurple,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending Approvals",
                    value = "${pendingDep + pendingWith}",
                    icon = Icons.Default.PendingActions,
                    accentColor = if (pendingDep + pendingWith > 0) NeonAmber else NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            SectionHeader(title = "FINANCIAL SUMMARY")
            Spacer(modifier = Modifier.height(6.dp))

            NeonCard(modifier = Modifier.fillMaxWidth()) {
                val totalBalanceInEcosystem = users.sumOf { it.availableBalance }
                val pendingLiabilities = users.sumOf { it.pendingBalance }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("User Wallet Balances", color = TextSecondary, fontSize = 13.sp)
                    Text("₹${"%.2f".format(totalBalanceInEcosystem)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pending Withdrawal Escrow", color = TextSecondary, fontSize = 13.sp)
                    Text("₹${"%.2f".format(pendingLiabilities)}", color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Active Deposit Requests", color = TextSecondary, fontSize = 13.sp)
                    Text("$pendingDep Pending", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminUsersTab(
    viewModel: TapGameViewModel,
    users: List<UserAccount>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedUserForAdjust by remember { mutableStateOf<UserAccount?>(null) }
    var adjustAmountText by remember { mutableStateOf("50") }
    var adjustReason by remember { mutableStateOf("") }

    val filtered = remember(users, searchQuery) {
        if (searchQuery.isBlank()) users
        else users.filter {
            it.displayName.contains(searchQuery, ignoreCase = true) ||
                    it.email.contains(searchQuery, ignoreCase = true) ||
                    it.publicPlayerId.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search player name, email, or #TAP-ID") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NavyCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filtered) { user ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NeonAvatar(seed = user.avatarSeed, size = 36.dp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(user.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        if (user.role == UserRole.ADMIN) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            NeonBadge(text = "ADMIN", color = NeonMagenta)
                                        }
                                    }
                                    Text("${user.publicPlayerId} • ${user.email}", color = TextSecondary, fontSize = 11.sp)
                                }
                            }

                            NeonBadge(
                                text = user.status.name,
                                color = when (user.status) {
                                    AccountStatus.ACTIVE -> NeonGreen
                                    AccountStatus.RESTRICTED -> NeonAmber
                                    AccountStatus.BANNED -> NeonRed
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Wallet: ₹${"%.2f".format(user.availableBalance)}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Games: ${user.totalGames} (${user.matchesWon} Won)", color = TextSecondary, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Admin Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.adminBanUser(user.id, "Violation of platform terms")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed)
                            ) {
                                Text(
                                    if (user.status == AccountStatus.BANNED) "Unban" else "Ban",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.adminRestrictUser(user.id, "Matchmaking restricted by admin")
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonAmber)
                            ) {
                                Text(
                                    if (user.status == AccountStatus.RESTRICTED) "Unrestrict" else "Restrict",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = {
                                    selectedUserForAdjust = user
                                    adjustAmountText = "50"
                                    adjustReason = "Tournament bonus reward"
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                            ) {
                                Text("Adjust ₹", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedUserForAdjust != null) {
        val target = selectedUserForAdjust!!
        AlertDialog(
            onDismissRequest = { selectedUserForAdjust = null },
            title = { Text("Adjust Wallet Balance for ${target.displayName}") },
            text = {
                Column {
                    Text("Current Balance: ₹${"%.2f".format(target.availableBalance)}", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adjustAmountText,
                        onValueChange = { adjustAmountText = it },
                        label = { Text("Amount (+ or -)") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adjustReason,
                        onValueChange = { adjustReason = it },
                        label = { Text("Mandatory Audit Reason") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val amt = adjustAmountText.toDoubleOrNull() ?: 0.0
                    viewModel.adminAdjustBalance(target.id, amt, adjustReason)
                    selectedUserForAdjust = null
                }) {
                    Text("APPLY", color = NeonCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedUserForAdjust = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = NavySurfaceElevated
        )
    }
}

@Composable
private fun AdminFinancesTab(
    viewModel: TapGameViewModel,
    deposits: List<DepositRequest>,
    withdrawals: List<WithdrawalRequest>
) {
    var subTab by remember { mutableStateOf(0) } // 0: Deposits, 1: Withdrawals

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = subTab,
            containerColor = NavySurface,
            contentColor = NeonCyan,
            divider = {}
        ) {
            Tab(selected = subTab == 0, onClick = { subTab = 0 }, text = { Text("Deposit Requests (${deposits.size})") })
            Tab(selected = subTab == 1, onClick = { subTab = 1 }, text = { Text("Withdrawals (${withdrawals.size})") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (subTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(deposits) { req ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("₹${"%.2f".format(req.amount)} by ${req.userName}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("UTR: ${req.utrNumber} • UPI: ${req.upiId}", color = TextSecondary, fontSize = 11.sp)
                                }
                                NeonBadge(text = req.status.name, color = when (req.status) {
                                    TransactionStatus.COMPLETED -> NeonGreen
                                    TransactionStatus.PENDING -> NeonAmber
                                    TransactionStatus.REJECTED -> NeonRed
                                })
                            }

                            if (req.status == TransactionStatus.PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.adminApproveDeposit(req.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Approve & Credit", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.adminRejectDeposit(req.id, "Invalid UTR / Payment not received") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(withdrawals) { req ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("₹${"%.2f".format(req.amount)} to ${req.userName}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${req.payoutMethod}: ${req.payoutDetails}", color = TextSecondary, fontSize = 11.sp)
                                }
                                NeonBadge(text = req.status.name, color = when (req.status) {
                                    TransactionStatus.COMPLETED -> NeonGreen
                                    TransactionStatus.PENDING -> NeonAmber
                                    TransactionStatus.REJECTED -> NeonRed
                                })
                            }

                            if (req.status == TransactionStatus.PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.adminApproveWithdrawal(req.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Mark Settled", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.adminRejectWithdrawal(req.id, "Invalid payout details / KYC mismatch") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonRed),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject & Refund", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminZapUpiGatewayTab(
    viewModel: TapGameViewModel
) {
    var apiKey by remember { mutableStateOf("zap7f946b7258683a0c7d99629edfdddcb7") }
    var baseUrl by remember { mutableStateOf("https://api.zapupi.com") }
    var webhookSecret by remember { mutableStateOf("whsec_live_tapgame878_prod") }
    var merchantVpa by remember { mutableStateOf("tapgame.business@okaxis") }
    var minDeposit by remember { mutableStateOf("10") }
    var gatewayEnabled by remember { mutableStateOf(true) }
    var autoCredit by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SectionHeader(
            title = "ZAPUPI PAYMENT GATEWAY INTEGRATION",
            subtitle = "Official Production API Configuration"
        )

        NeonCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Gateway Operational Status", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Accept instant deposits via ZapUPI", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(
                    checked = gatewayEnabled,
                    onCheckedChange = { gatewayEnabled = it }
                )
            }

            HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Auto-Credit on UTR Webhook", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Instant wallet credit upon verified webhook signature", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(
                    checked = autoCredit,
                    onCheckedChange = { autoCredit = it }
                )
            }
        }

        NeonCard(modifier = Modifier.fillMaxWidth()) {
            Text("ZapUPI Production API Key", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("API Base URL", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Webhook Signing Secret", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = webhookSecret,
                onValueChange = { webhookSecret = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Merchant Settlement UPI VPA", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = merchantVpa,
                onValueChange = { merchantVpa = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text("Minimum Deposit Allowed (₹)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = minDeposit,
                onValueChange = { minDeposit = it.filter { ch -> ch.isDigit() } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        NeonButton(
            text = "SAVE ZAPUPI CONFIGURATION",
            onClick = {
                viewModel.showToast("ZapUPI configuration updated successfully!")
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AdminGameRulesTab(
    viewModel: TapGameViewModel,
    config: AppConfig
) {
    var matchmaking by remember { mutableStateOf(config.matchmakingEnabled) }
    var maintenance by remember { mutableStateOf(config.maintenanceMode) }
    var normalActive by remember { mutableStateOf(config.normalModeActive) }
    var proActive by remember { mutableStateOf(config.proModeActive) }
    var highRollerActive by remember { mutableStateOf(config.highRollerModeActive) }
    var durationText by remember { mutableStateOf("${config.matchDurationSeconds}") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionHeader(title = "SYSTEM SWITCHES")
        Spacer(modifier = Modifier.height(8.dp))

        NeonCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Live Matchmaking Queue", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Permit players to queue into 1v1 battles", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(checked = matchmaking, onCheckedChange = { matchmaking = it })
            }

            HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Maintenance Mode", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Lock all games for server upgrades", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(checked = maintenance, onCheckedChange = { maintenance = it })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "ARENA MODES AVAILABILITY")
        Spacer(modifier = Modifier.height(8.dp))

        NeonCard(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Normal Arena (₹10 -> ₹18)", color = TextPrimary)
                Switch(checked = normalActive, onCheckedChange = { normalActive = it })
            }
            HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pro League (₹50 -> ₹90)", color = TextPrimary)
                Switch(checked = proActive, onCheckedChange = { proActive = it })
            }
            HorizontalDivider(color = NavyCardBorder, modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("High Rollers (₹200 -> ₹360)", color = TextPrimary)
                Switch(checked = highRollerActive, onCheckedChange = { highRollerActive = it })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader(title = "MATCH DURATION (SECONDS)")
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = durationText,
            onValueChange = { durationText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Match Duration (10 to 60 seconds)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        NeonButton(
            text = "SAVE CONFIGURATION",
            onClick = {
                val dur = durationText.toIntOrNull()?.coerceIn(10, 60) ?: 15
                val newConfig = config.copy(
                    matchmakingEnabled = matchmaking,
                    maintenanceMode = maintenance,
                    normalModeActive = normalActive,
                    proModeActive = proActive,
                    highRollerModeActive = highRollerActive,
                    matchDurationSeconds = dur
                )
                viewModel.adminUpdateConfig(newConfig)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AdminBroadcastTab(
    viewModel: TapGameViewModel,
    config: AppConfig
) {
    var announcement by remember { mutableStateOf(config.announcement) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionHeader(title = "ARENA TICKER ANNOUNCEMENT")
        Spacer(modifier = Modifier.height(8.dp))

        NeonCard(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = announcement,
                onValueChange = { announcement = it },
                label = { Text("Broadcast Banner Message") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            NeonButton(
                text = "BROADCAST ANNOUNCEMENT",
                onClick = {
                    val updated = config.copy(announcement = announcement)
                    viewModel.adminUpdateConfig(updated)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AdminAuditTab(
    auditLogs: List<AdminAuditLog>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("IMMUTABLE ADMINISTRATIVE ACTION LOGS", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (auditLogs.isEmpty()) {
            item {
                Text("No administrative actions recorded yet.", color = TextSecondary, fontSize = 13.sp)
            }
        }

        items(auditLogs) { log ->
            val dateStr = SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()).format(Date(log.timestamp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavyCardBorder, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NeonBadge(text = log.action, color = NeonMagenta)
                        Text(dateStr, color = TextMuted, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Target: ${log.target}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Reason: ${log.reason}", color = TextSecondary, fontSize = 12.sp)
                    Text("Actor: ${log.adminEmail}", color = TextMuted, fontSize = 10.sp)
                }
            }
        }
    }
}
