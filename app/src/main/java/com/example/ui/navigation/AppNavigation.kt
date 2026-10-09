package com.example.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TapGameViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

enum class AppScreen {
    SPLASH,
    LOGIN,
    REGISTER,
    HOME,
    JOIN_GAME,
    MATCHMAKING,
    LIVE_GAME,
    GAME_RESULT,
    DEPOSIT,
    WITHDRAWAL,
    GAME_HISTORY,
    TRANSACTION_HISTORY,
    LEADERBOARD,
    PROFILE,
    NOTIFICATIONS,
    HELP_FEEDBACK,
    SETTINGS,
    ADMIN_PANEL,
    FIREBASE_SETUP
}

enum class MainTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    HISTORY("History", Icons.Filled.ReceiptLong, Icons.Outlined.ReceiptLong),
    LEADERBOARD("Leaderboard", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun TapGameApp(
    viewModel: TapGameViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
    var currentTab by remember { mutableStateOf(MainTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val showBottomBar = currentScreen == AppScreen.HOME ||
            currentScreen == AppScreen.GAME_HISTORY ||
            currentScreen == AppScreen.LEADERBOARD ||
            currentScreen == AppScreen.PROFILE

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .border(1.dp, NavyCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    containerColor = NavySurface,
                    contentColor = NeonCyan
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = currentTab == tab && (
                                (tab == MainTab.HOME && currentScreen == AppScreen.HOME) ||
                                        (tab == MainTab.HISTORY && currentScreen == AppScreen.GAME_HISTORY) ||
                                        (tab == MainTab.LEADERBOARD && currentScreen == AppScreen.LEADERBOARD) ||
                                        (tab == MainTab.PROFILE && currentScreen == AppScreen.PROFILE)
                                )

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentTab = tab
                                currentScreen = when (tab) {
                                    MainTab.HOME -> AppScreen.HOME
                                    MainTab.HISTORY -> AppScreen.GAME_HISTORY
                                    MainTab.LEADERBOARD -> AppScreen.LEADERBOARD
                                    MainTab.PROFILE -> AppScreen.PROFILE
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = NeonCyan,
                                selectedTextColor = NeonCyan,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = NeonCyan.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        },
        containerColor = NavyBackground
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onStart = {
                            if (currentUser != null) {
                                currentScreen = AppScreen.HOME
                                currentTab = MainTab.HOME
                            } else {
                                currentScreen = AppScreen.LOGIN
                            }
                        }
                    )
                }

                AppScreen.LOGIN -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToHome = {
                            currentScreen = AppScreen.HOME
                            currentTab = MainTab.HOME
                        },
                        onNavigateToRegister = {
                            currentScreen = AppScreen.REGISTER
                        }
                    )
                }

                AppScreen.REGISTER -> {
                    BackHandler { currentScreen = AppScreen.LOGIN }
                    RegisterScreen(
                        viewModel = viewModel,
                        onNavigateToHome = {
                            currentScreen = AppScreen.HOME
                            currentTab = MainTab.HOME
                        },
                        onNavigateToLogin = {
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }

                AppScreen.HOME -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToJoinGame = { currentScreen = AppScreen.JOIN_GAME },
                        onNavigateToDeposit = { currentScreen = AppScreen.DEPOSIT },
                        onNavigateToWithdrawal = { currentScreen = AppScreen.WITHDRAWAL },
                        onNavigateToMatchmaking = { currentScreen = AppScreen.MATCHMAKING },
                        onNavigateToHistory = {
                            currentTab = MainTab.HISTORY
                            currentScreen = AppScreen.GAME_HISTORY
                        },
                        onNavigateToLeaderboard = {
                            currentTab = MainTab.LEADERBOARD
                            currentScreen = AppScreen.LEADERBOARD
                        },
                        onNavigateToProfile = {
                            currentTab = MainTab.PROFILE
                            currentScreen = AppScreen.PROFILE
                        }
                    )
                }

                AppScreen.JOIN_GAME -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    JoinGameScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME },
                        onNavigateToMatchmaking = { currentScreen = AppScreen.MATCHMAKING },
                        onNavigateToDeposit = { currentScreen = AppScreen.DEPOSIT }
                    )
                }

                AppScreen.MATCHMAKING -> {
                    MatchmakingScreen(
                        viewModel = viewModel,
                        onCancel = { currentScreen = AppScreen.HOME },
                        onMatchReady = { currentScreen = AppScreen.LIVE_GAME }
                    )
                }

                AppScreen.LIVE_GAME -> {
                    LiveGameScreen(
                        viewModel = viewModel,
                        onMatchFinished = { currentScreen = AppScreen.GAME_RESULT }
                    )
                }

                AppScreen.GAME_RESULT -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    GameResultScreen(
                        viewModel = viewModel,
                        onPlayAgain = { currentScreen = AppScreen.JOIN_GAME },
                        onReturnHome = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.DEPOSIT -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    DepositScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME },
                        onNavigateToHelp = { currentScreen = AppScreen.HELP_FEEDBACK }
                    )
                }

                AppScreen.WITHDRAWAL -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    WithdrawalScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME },
                        onNavigateToHelp = { currentScreen = AppScreen.HELP_FEEDBACK }
                    )
                }

                AppScreen.GAME_HISTORY -> {
                    GameHistoryScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentTab = MainTab.HOME
                            currentScreen = AppScreen.HOME
                        }
                    )
                }

                AppScreen.TRANSACTION_HISTORY -> {
                    BackHandler {
                        currentTab = MainTab.PROFILE
                        currentScreen = AppScreen.PROFILE
                    }
                    TransactionHistoryScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentTab = MainTab.PROFILE
                            currentScreen = AppScreen.PROFILE
                        }
                    )
                }

                AppScreen.LEADERBOARD -> {
                    LeaderboardScreen(viewModel = viewModel)
                }

                AppScreen.PROFILE -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToHome = {
                            currentTab = MainTab.HOME
                            currentScreen = AppScreen.HOME
                        },
                        onNavigateToMyGames = {
                            currentTab = MainTab.HISTORY
                            currentScreen = AppScreen.GAME_HISTORY
                        },
                        onNavigateToTransactionHistory = {
                            currentScreen = AppScreen.TRANSACTION_HISTORY
                        },
                        onNavigateToLeaderboard = {
                            currentTab = MainTab.LEADERBOARD
                            currentScreen = AppScreen.LEADERBOARD
                        },
                        onNavigateToSettings = {
                            currentScreen = AppScreen.SETTINGS
                        },
                        onNavigateToHelp = {
                            currentScreen = AppScreen.HELP_FEEDBACK
                        },
                        onNavigateToWithdrawal = {
                            currentScreen = AppScreen.WITHDRAWAL
                        }
                    )
                }

                AppScreen.NOTIFICATIONS -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    NotificationsScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.HELP_FEEDBACK -> {
                    BackHandler {
                        currentTab = MainTab.PROFILE
                        currentScreen = AppScreen.PROFILE
                    }
                    HelpFeedbackScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentTab = MainTab.PROFILE
                            currentScreen = AppScreen.PROFILE
                        }
                    )
                }

                AppScreen.SETTINGS -> {
                    BackHandler {
                        currentTab = MainTab.PROFILE
                        currentScreen = AppScreen.PROFILE
                    }
                    SettingsScreen(
                        viewModel = viewModel,
                        onBack = {
                            currentTab = MainTab.PROFILE
                            currentScreen = AppScreen.PROFILE
                        },
                        onNavigateToFirebaseSetup = {
                            currentScreen = AppScreen.FIREBASE_SETUP
                        }
                    )
                }

                AppScreen.ADMIN_PANEL -> {
                    BackHandler { currentScreen = AppScreen.HOME }
                    AdminPanelScreen(
                        viewModel = viewModel,
                        onBack = { currentScreen = AppScreen.HOME }
                    )
                }

                AppScreen.FIREBASE_SETUP -> {
                    BackHandler { currentScreen = AppScreen.SETTINGS }
                    FirebaseSetupScreen(
                        onBack = { currentScreen = AppScreen.SETTINGS }
                    )
                }
            }
        }
    }
}
