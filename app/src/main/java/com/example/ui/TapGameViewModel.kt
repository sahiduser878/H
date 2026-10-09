package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.TapGameRepository
import com.example.ui.theme.AppThemeColor
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TapGameViewModel(
    private val repository: TapGameRepository = TapGameRepository.getInstance()
) : ViewModel() {

    val currentUser = repository.currentUser
    val allUsers = repository.allUsers
    val appConfig = repository.appConfig
    val currentMatch = repository.currentMatch
    val waitingMatches = repository.waitingMatches
    val countdownValue = repository.countdownValue
    val remainingTimeSeconds = repository.remainingTimeSeconds
    val matchHistory = repository.matchHistory
    val transactions = repository.transactions
    val depositRequests = repository.depositRequests
    val withdrawalRequests = repository.withdrawalRequests
    val notifications = repository.notifications
    val supportTickets = repository.supportTickets
    val auditLogs = repository.auditLogs

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _themeColor = MutableStateFlow(AppThemeColor.CYAN_NEON)
    val themeColor: StateFlow<AppThemeColor> = _themeColor.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        showToast(if (enabled) "Switched to Dark Mode" else "Switched to Light Mode")
    }

    fun setThemeColor(color: AppThemeColor) {
        _themeColor.value = color
        showToast("Theme updated to ${color.title}")
    }

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()

    val unreadNotificationsCount: StateFlow<Int> = notifications.combine(currentUser) { list, user ->
        if (user == null) 0 else list.count { it.userId == user.id && !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val leaderboard: StateFlow<List<LeaderboardEntry>> = allUsers.combine(currentUser) { users, current ->
        val sorted = users.sortedWith(
            compareByDescending<UserAccount> { it.matchesWon }
                .thenByDescending { it.totalEarnings }
        )
        sorted.mapIndexed { index, user ->
            LeaderboardEntry(
                rank = index + 1,
                playerId = user.publicPlayerId,
                displayName = user.displayName,
                avatar = user.avatarSeed,
                wins = user.matchesWon,
                earnings = user.totalEarnings,
                winRate = user.winRate,
                isCurrentUser = user.id == current?.id
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun showToast(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Attempt Firebase Auth sign-in
            com.example.data.auth.FirebaseAuthService.signInWithEmail(email, pass)
            val result = repository.login(email, pass)
            result.onSuccess {
                showToast("Welcome back, ${it.displayName}!")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            // Attempt Firebase Auth account creation
            com.example.data.auth.FirebaseAuthService.signUpWithEmail(email, pass)
            val result = repository.register(name, email, pass)
            result.onSuccess {
                showToast("Account created! Assigned ID ${it.publicPlayerId}")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Registration failed")
            }
        }
    }

    fun googleSignIn(email: String = "sahid50534@gmail.com", name: String = "Sahid", onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.googleSignIn(email, name)
            result.onSuccess {
                showToast("Google Sign-In verified! Welcome, ${it.displayName}")
                onSuccess()
            }.onFailure {
                showToast(it.message ?: "Google sign-in failed")
            }
        }
    }

    fun switchUser(userId: String) {
        repository.switchUser(userId)
    }

    fun logout() {
        com.example.data.auth.FirebaseAuthService.signOut()
        repository.logout()
        showToast("Signed out successfully.")
    }

    fun updateProfile(displayName: String, avatar: String) {
        val res = repository.updateProfile(displayName, avatar)
        res.onSuccess {
            showToast("Profile updated!")
        }.onFailure {
            showToast(it.message ?: "Failed to update profile")
        }
    }

    fun submitDeposit(amount: Double, method: DepositPaymentMethod = DepositPaymentMethod.UPI, upiId: String = "", utr: String = "", onSuccess: () -> Unit) {
        val res = repository.submitDeposit(amount, method, upiId, utr)
        res.onSuccess {
            showToast("Deposit of ₹${"%.0f".format(amount)} completed successfully!")
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Deposit failed")
        }
    }

    fun submitWithdrawal(amount: Double, method: PayoutMethod, details: String, onSuccess: () -> Unit) {
        val res = repository.submitWithdrawal(amount, method, details)
        res.onSuccess {
            showToast("Withdrawal of ₹${"%.0f".format(amount)} requested successfully!")
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Withdrawal request failed")
        }
    }

    fun startMatchmaking(mode: GameMode, onSuccess: () -> Unit) {
        val res = repository.startMatchmaking(mode)
        res.onSuccess {
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Unable to create match")
        }
    }

    fun createCustomMatch(mode: GameMode, customFee: Double? = null, onSuccess: () -> Unit) {
        val res = repository.createCustomMatch(mode, customFee)
        res.onSuccess {
            showToast("Match #${it.matchCode} created! Share this code with your opponent.")
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Failed to create match")
        }
    }

    fun searchMatchByCode(code: String, onResult: (Result<GameMatch>) -> Unit) {
        val res = repository.searchMatchByCode(code)
        onResult(res)
    }

    fun joinMatchByCode(code: String, onSuccess: () -> Unit) {
        val res = repository.joinMatchByCode(code)
        res.onSuccess {
            showToast("Joined Match #${it.matchCode}! Preparing battle...")
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Unable to join match")
        }
    }

    fun registerTap(forPlayer2: Boolean = false): Boolean {
        return repository.registerTap(forPlayer2)
    }

    fun cancelMatchmaking(onSuccess: () -> Unit) {
        if (repository.cancelMatchmaking()) {
            showToast("Matchmaking cancelled. Entry fee refunded!")
            onSuccess()
        }
    }

    fun dismissMatch() {
        repository.dismissFinishedMatch()
    }

    fun markNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    fun submitTicket(subject: String, msg: String, onSuccess: () -> Unit) {
        val res = repository.submitSupportTicket(subject, msg)
        res.onSuccess {
            showToast("Support ticket submitted! ID: #${it.id}")
            onSuccess()
        }.onFailure {
            showToast(it.message ?: "Failed to submit ticket")
        }
    }

    // Admin Controls
    fun adminBanUser(targetId: String, reason: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminBanUser(admin, targetId, reason)
        res.onSuccess { showToast("User status updated.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminRestrictUser(targetId: String, reason: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminRestrictUser(admin, targetId, reason)
        res.onSuccess { showToast("User restriction updated.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminAdjustBalance(targetId: String, amount: Double, reason: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminAdjustBalance(admin, targetId, amount, reason)
        res.onSuccess { showToast("Balance adjusted by ₹$amount.") }
            .onFailure { showToast(it.message ?: "Adjustment failed") }
    }

    fun adminApproveDeposit(reqId: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminApproveDeposit(admin, reqId)
        res.onSuccess { showToast("Deposit approved & credited.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminRejectDeposit(reqId: String, reason: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminRejectDeposit(admin, reqId, reason)
        res.onSuccess { showToast("Deposit rejected.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminApproveWithdrawal(reqId: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminApproveWithdrawal(admin, reqId)
        res.onSuccess { showToast("Withdrawal approved & settled.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminRejectWithdrawal(reqId: String, reason: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminRejectWithdrawal(admin, reqId, reason)
        res.onSuccess { showToast("Withdrawal rejected. Balance restored.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminUpdateConfig(config: AppConfig) {
        val admin = currentUser.value ?: return
        val res = repository.adminUpdateConfig(admin, config)
        res.onSuccess { showToast("App configuration updated.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }

    fun adminReplyTicket(ticketId: String, reply: String) {
        val admin = currentUser.value ?: return
        val res = repository.adminReplyTicket(admin, ticketId, reply)
        res.onSuccess { showToast("Reply sent.") }
            .onFailure { showToast(it.message ?: "Failed") }
    }
}
