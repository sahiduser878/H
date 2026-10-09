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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositPaymentMethod
import com.example.data.model.PayoutMethod
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedMethod by remember { mutableStateOf(DepositPaymentMethod.UPI) }
    var selectedAmount by remember { mutableStateOf("200") }

    val presetAmounts = listOf("50", "100", "200", "500", "1000", "2000")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Deposit", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .clickable { onNavigateToHelp() }
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Headphones, contentDescription = "Help", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Help", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
            // Select Payment Method
            item {
                Text(
                    text = "Select Payment Method",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Payment Methods List
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DepositPaymentMethod.values().forEach { method ->
                        val isSelected = selectedMethod == method

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NeonCyan else NavyCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMethod = method },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NavySurfaceElevated else NavySurface
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
                                    // Method icon container
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (method) {
                                                    DepositPaymentMethod.UPI -> Color(0xFF0072FF).copy(alpha = 0.2f)
                                                    DepositPaymentMethod.PHONEPE -> Color(0xFF5F259F).copy(alpha = 0.2f)
                                                    DepositPaymentMethod.PAYTM -> Color(0xFF00B9F5).copy(alpha = 0.2f)
                                                    DepositPaymentMethod.GOOGLE_PAY -> Color(0xFF4285F4).copy(alpha = 0.2f)
                                                    DepositPaymentMethod.BANK_TRANSFER -> Color(0xFF10B981).copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (method) {
                                                DepositPaymentMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                                                else -> Icons.Default.Payment
                                            },
                                            contentDescription = null,
                                            tint = when (method) {
                                                DepositPaymentMethod.UPI -> Color(0xFF00C6FF)
                                                DepositPaymentMethod.PHONEPE -> Color(0xFF9D65E0)
                                                DepositPaymentMethod.PAYTM -> Color(0xFF00B9F5)
                                                DepositPaymentMethod.GOOGLE_PAY -> Color(0xFF4285F4)
                                                DepositPaymentMethod.BANK_TRANSFER -> Color(0xFF10B981)
                                            },
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(method.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(method.subtitle, color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                // Selection checkmark circle
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 0.dp else 1.5.dp,
                                            color = if (isSelected) NeonCyan else TextMuted,
                                            shape = CircleShape
                                        )
                                        .background(if (isSelected) NeonCyan else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Enter Amount Section
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Enter Amount",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Amount Presets (2 rows of 3)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        presetAmounts.take(3).forEach { amt ->
                            val isSelected = selectedAmount == amt
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else NavyCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedAmount = amt },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF0D284F) else NavySurface
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "₹ $amt",
                                        color = if (isSelected) NeonCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        presetAmounts.drop(3).forEach { amt ->
                            val isSelected = selectedAmount == amt
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonCyan else NavyCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { selectedAmount = amt },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF0D284F) else NavySurface
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "₹ $amt",
                                        color = if (isSelected) NeonCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Proceed to Pay Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                NeonButton(
                    text = "Proceed to Pay",
                    onClick = {
                        val amount = selectedAmount.toDoubleOrNull() ?: 200.0
                        viewModel.submitDeposit(amount, selectedMethod) {
                            onBack()
                        }
                    },
                    gradient = Brush.horizontalGradient(
                        listOf(Color(0xFF0072FF), Color(0xFFFF2A85))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_proceed_button")
                )
            }

            // Footer
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("100% Secure Payments", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("•", color = TextMuted)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("⚡ Powered by Razorpay", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithdrawalScreen(
    viewModel: TapGameViewModel,
    onBack: () -> Unit,
    onNavigateToHelp: () -> Unit = {}
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val user = currentUser
    val available = user?.availableBalance ?: 1250.0

    var selectedMethod by remember { mutableStateOf(PayoutMethod.UPI) }
    var amountText by remember { mutableStateOf("") }
    var payoutDetails by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Withdrawal", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .clickable { onNavigateToHelp() }
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Headphones, contentDescription = "Help", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Help", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
            // Available Balance Card
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
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0072FF).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(26.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("Available Balance", color = TextSecondary, fontSize = 12.sp)
                            Text(
                                "₹ ${available.toInt()}",
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text("Min. withdrawal ₹100", color = Color(0xFF38BDF8), fontSize = 11.sp)
                        }
                    }
                }
            }

            // Select Method Section
            item {
                Text(
                    text = "Select Method",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PayoutMethod.values().forEach { method ->
                        val isSelected = selectedMethod == method

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NeonCyan else NavyCardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedMethod = method },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) NavySurfaceElevated else NavySurface
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
                                        imageVector = when (method) {
                                            PayoutMethod.BANK_TRANSFER -> Icons.Default.AccountBalance
                                            else -> Icons.Default.Payment
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) NeonCyan else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(method.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = if (isSelected) 0.dp else 1.5.dp,
                                            color = if (isSelected) NeonCyan else TextMuted,
                                            shape = CircleShape
                                        )
                                        .background(if (isSelected) NeonCyan else Color.Transparent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Enter Amount Section with [Max] button inside!
            item {
                Text(
                    text = "Enter Amount",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

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
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = amountText,
                            onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                            placeholder = { Text("Enter amount (min ₹100)", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("withdraw_amount_input")
                        )

                        // Max Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan)
                                .clickable { amountText = "${available.toInt()}" }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Max", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Recipient Details
            item {
                OutlinedTextField(
                    value = payoutDetails,
                    onValueChange = { payoutDetails = it },
                    label = { Text("Enter ${selectedMethod.title} details (e.g. mobile/UPI ID/A/C)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = NavyCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            // Request Withdrawal Button
            item {
                NeonButton(
                    text = "Request Withdrawal",
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 100.0
                        viewModel.submitWithdrawal(amt, selectedMethod, payoutDetails.ifEmpty { "Verified Account" }) {
                            onBack()
                        }
                    },
                    gradient = Brush.horizontalGradient(
                        listOf(Color(0xFF0072FF), Color(0xFFFF2A85))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_request_button")
                )
            }

            // Processing Time Notice Card matching Screenshot
            item {
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Processing time: 5-30 minutes", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Withdrawals are processed manually and may take up to 30 minutes.", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
