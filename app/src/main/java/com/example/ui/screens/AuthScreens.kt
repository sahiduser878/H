package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.TapGameViewModel
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonOutlineButton
import com.example.ui.components.TapGameLogo
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onStart: () -> Unit
) {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(15)
            progress = i / 100f
        }
        delay(300)
        onStart()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .clickable { onStart() }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Center Logo and Tagline
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TapGameLogo(size = 180.dp)

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "2 PLAYER  •  REAL TIME  •  BIG REWARDS",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // Bottom Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Loading...", color = TextSecondary, fontSize = 12.sp)
                    Text("${(progress * 100).toInt()}%", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(NavySurfaceElevated)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonCyan, Color(0xFF8B5CF6), NeonMagenta)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    viewModel: TapGameViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var mobileNumber by remember { mutableStateOf("9876543210") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Exact Logo from the Screenshot
        TapGameLogo(size = 120.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // Headers
        Text(
            text = "Welcome Back!",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Login to continue",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Mobile Number Field matching Screenshot
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Mobile Number",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

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
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavySurfaceElevated)
                            .border(1.dp, NavyCardBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+91", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    TextField(
                        value = mobileNumber,
                        onValueChange = { mobileNumber = it },
                        placeholder = { Text("Enter your mobile number", color = TextMuted, fontSize = 14.sp) },
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
                            .fillMaxWidth()
                            .testTag("login_mobile_input")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field matching Screenshot
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

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
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("Enter your password", color = TextMuted, fontSize = 14.sp) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                            .testTag("login_password_input")
                    )

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "Forgot Password?",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showForgotDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        NeonButton(
            text = "Login",
            onClick = {
                viewModel.login(mobileNumber, password) {
                    onNavigateToHome()
                }
            },
            gradient = CyanGradient,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_submit_button")
        )

        Spacer(modifier = Modifier.height(18.dp))

        // OR Divider
        Row(
            modifier = Modifier.fillMaxWidth(0.8f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = NavyCardBorder)
            Text("  OR  ", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(modifier = Modifier.weight(1f), color = NavyCardBorder)
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Create New Account Button
        NeonOutlineButton(
            text = "Create New Account",
            onClick = onNavigateToRegister,
            borderColor = NavyCardBorder,
            textColor = TextPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("navigate_to_register")
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Footer Terms
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("By continuing, you agree to our ", color = TextSecondary, fontSize = 11.sp)
            Text(
                "Terms & Privacy",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showForgotDialog = true }
            )
        }
    }

    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Password Assistance") },
            text = { Text("Enter your registered mobile number (+91 $mobileNumber) to receive a secure SMS OTP verification code.") },
            confirmButton = {
                TextButton(onClick = {
                    showForgotDialog = false
                    viewModel.showToast("OTP sent to +91 $mobileNumber")
                }) {
                    Text("OK", color = NeonCyan)
                }
            },
            containerColor = NavySurfaceElevated,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }
}

@Composable
fun RegisterScreen(
    viewModel: TapGameViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("Player123") }
    var mobile by remember { mutableStateOf("9876543210") }
    var password by remember { mutableStateOf("password123") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        TapGameLogo(size = 110.dp)

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Create Account",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Join live 1v1 tapping battles & win rewards",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Display Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NavyCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Mobile Number") },
            leadingIcon = { Text(" +91 ", color = NeonCyan, fontWeight = FontWeight.Bold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NavyCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = NavyCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        NeonButton(
            text = "Create Account",
            onClick = {
                viewModel.register(name, mobile, password) {
                    onNavigateToHome()
                }
            },
            gradient = CyanGradient,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = TextSecondary, fontSize = 13.sp)
            Text(
                "Sign In",
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
