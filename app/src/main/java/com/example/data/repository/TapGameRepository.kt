package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class TapGameRepository private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _appConfig = MutableStateFlow(AppConfig())
    val appConfig: StateFlow<AppConfig> = _appConfig.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserAccount>>(emptyList())
    val allUsers: StateFlow<List<UserAccount>> = _allUsers.asStateFlow()

    private val _currentMatch = MutableStateFlow<GameMatch?>(null)
    val currentMatch: StateFlow<GameMatch?> = _currentMatch.asStateFlow()

    private val _countdownValue = MutableStateFlow(5)
    val countdownValue: StateFlow<Int> = _countdownValue.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(45)
    val remainingTimeSeconds: StateFlow<Int> = _remainingTimeSeconds.asStateFlow()

    private val _matchHistory = MutableStateFlow<List<GameMatch>>(emptyList())
    val matchHistory: StateFlow<List<GameMatch>> = _matchHistory.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _depositRequests = MutableStateFlow<List<DepositRequest>>(emptyList())
    val depositRequests: StateFlow<List<DepositRequest>> = _depositRequests.asStateFlow()

    private val _withdrawalRequests = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawalRequests: StateFlow<List<WithdrawalRequest>> = _withdrawalRequests.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    private var matchJob: Job? = null
    private var opponentTapJob: Job? = null
    private var tapCountInCurrentSecond = 0
    private var currentSecondMarker = 0L

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val user1 = UserAccount(
            id = "usr_458736",
            email = "player123@tapgame.io",
            mobileNumber = "9876543210",
            displayName = "Player123",
            publicPlayerId = "458736",
            avatarSeed = "1",
            availableBalance = 1250.0,
            pendingBalance = 0.0,
            totalGames = 12,
            matchesWon = 8,
            totalEarnings = 720.0,
            isVerified = true,
            status = AccountStatus.ACTIVE,
            role = UserRole.USER
        )

        val user2 = UserAccount(
            id = "usr_882910",
            email = "neon_striker@tapgame.io",
            mobileNumber = "9812345678",
            displayName = "NeonStriker",
            publicPlayerId = "882910",
            avatarSeed = "2",
            availableBalance = 980.0,
            pendingBalance = 0.0,
            totalGames = 15,
            matchesWon = 9,
            totalEarnings = 810.0,
            isVerified = true,
            status = AccountStatus.ACTIVE,
            role = UserRole.USER
        )

        val adminUser = UserAccount(
            id = "usr_admin",
            email = "admin@tapgame.io",
            mobileNumber = "9999999999",
            displayName = "Admin",
            publicPlayerId = "000001",
            avatarSeed = "3",
            availableBalance = 50000.0,
            pendingBalance = 0.0,
            totalGames = 20,
            matchesWon = 18,
            totalEarnings = 1620.0,
            isVerified = true,
            status = AccountStatus.ACTIVE,
            role = UserRole.ADMIN
        )

        _allUsers.value = listOf(user1, user2, adminUser)
        _currentUser.value = user1

        val seedTransactions = listOf(
            WalletTransaction(
                userId = user1.id,
                type = TransactionType.DEPOSIT,
                amount = 500.0,
                status = TransactionStatus.COMPLETED,
                reference = "UPI Ref 9938210921",
                timestamp = System.currentTimeMillis() - 86400000 * 2
            ),
            WalletTransaction(
                userId = user1.id,
                type = TransactionType.MATCH_PRIZE,
                amount = 90.0,
                status = TransactionStatus.COMPLETED,
                reference = "Normal Mode Victory",
                timestamp = System.currentTimeMillis() - 86400000
            ),
            WalletTransaction(
                userId = user1.id,
                type = TransactionType.MATCH_ENTRY,
                amount = -50.0,
                status = TransactionStatus.COMPLETED,
                reference = "Normal Mode Entry",
                timestamp = System.currentTimeMillis() - 86400000
            )
        )
        _transactions.value = seedTransactions

        val seedMatches = listOf(
            GameMatch(
                id = "M8211",
                mode = GameMode.NORMAL,
                player1Id = user1.id,
                player1Name = user1.displayName,
                player1PlayerId = user1.publicPlayerId,
                player1Avatar = user1.avatarSeed,
                player2Id = "opp_771",
                player2Name = "Opponent",
                player2PlayerId = "771204",
                player2Avatar = "2",
                player1Score = 250,
                player2Score = 176,
                winnerId = user1.id,
                status = MatchStatus.FINISHED,
                entryFee = 50.0,
                prizeAmount = 90.0,
                timestamp = System.currentTimeMillis() - 86400000
            )
        )
        _matchHistory.value = seedMatches

        _notifications.value = listOf(
            AppNotification(
                userId = user1.id,
                title = "🎉 Welcome to TAP GAME!",
                message = "Your wallet is loaded with ₹1,250. Ready to win big rewards?",
                type = NotificationType.SYSTEM
            ),
            AppNotification(
                userId = user1.id,
                title = "🏆 Victory Reward Credited!",
                message = "You won ₹90.00 in Normal Mode! Funds added to your available balance.",
                type = NotificationType.MATCH
            )
        )
    }

    fun login(identifier: String, pass: String): Result<UserAccount> {
        val trimmed = identifier.trim()
        val user = _allUsers.value.find {
            it.email.equals(trimmed, ignoreCase = true) ||
                    it.mobileNumber.replace(" ", "").endsWith(trimmed.replace(" ", "").takeLast(10)) ||
                    it.displayName.equals(trimmed, ignoreCase = true)
        }
        return if (user != null) {
            if (user.status == AccountStatus.BANNED) {
                Result.failure(Exception("Account is banned."))
            } else {
                _currentUser.value = user
                Result.success(user)
            }
        } else {
            // Auto register/create session for fast seamless experience
            val publicId = "${Random.nextInt(100000, 999999)}"
            val newUser = UserAccount(
                email = if (trimmed.contains("@")) trimmed else "player_$publicId@tapgame.io",
                mobileNumber = if (trimmed.all { it.isDigit() }) trimmed else "9876543210",
                displayName = if (trimmed.contains("@")) trimmed.substringBefore("@") else "Player$publicId",
                publicPlayerId = publicId,
                avatarSeed = "1",
                availableBalance = 1250.0,
                totalGames = 0,
                matchesWon = 0,
                totalEarnings = 0.0,
                isVerified = true
            )
            _allUsers.value = _allUsers.value + newUser
            _currentUser.value = newUser
            Result.success(newUser)
        }
    }

    fun register(name: String, mobileOrEmail: String, pass: String): Result<UserAccount> {
        val publicId = "${Random.nextInt(100000, 999999)}"
        val isEmail = mobileOrEmail.contains("@")
        val newUser = UserAccount(
            email = if (isEmail) mobileOrEmail.trim() else "player_$publicId@tapgame.io",
            mobileNumber = if (!isEmail) mobileOrEmail.trim() else "9876543210",
            displayName = name.trim().ifEmpty { "Player$publicId" },
            publicPlayerId = publicId,
            avatarSeed = "1",
            availableBalance = 1250.0,
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
            isVerified = true
        )
        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser
        return Result.success(newUser)
    }

    fun switchUser(userId: String) {
        val user = _allUsers.value.find { it.id == userId }
        if (user != null) {
            _currentUser.value = user
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(displayName: String, avatarSeed: String): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val updated = user.copy(
            displayName = displayName.trim().ifEmpty { user.displayName },
            avatarSeed = avatarSeed
        )
        updateUserInternal(updated)
        return Result.success(Unit)
    }

    fun submitDeposit(amount: Double, method: DepositPaymentMethod, upiId: String = "", utr: String = ""): Result<DepositRequest> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        if (amount < 50.0) return Result.failure(Exception("Minimum deposit is ₹50"))

        val updatedUser = user.copy(availableBalance = user.availableBalance + amount)
        updateUserInternal(updatedUser)

        val tx = WalletTransaction(
            userId = user.id,
            type = TransactionType.DEPOSIT,
            amount = amount,
            status = TransactionStatus.COMPLETED,
            reference = "${method.title} Instant Deposit"
        )
        _transactions.value = listOf(tx) + _transactions.value

        val req = DepositRequest(
            userId = user.id,
            userName = user.displayName,
            amount = amount,
            paymentMethod = method,
            upiId = upiId,
            utrNumber = utr,
            status = TransactionStatus.COMPLETED
        )
        _depositRequests.value = listOf(req) + _depositRequests.value

        addNotification(
            userId = user.id,
            title = "✅ Deposit of ₹${"%.0f".format(amount)} Successful",
            message = "Added ₹$amount to wallet via ${method.title}. Available balance: ₹${"%.0f".format(updatedUser.availableBalance)}.",
            type = NotificationType.WALLET
        )

        return Result.success(req)
    }

    fun submitWithdrawal(amount: Double, method: PayoutMethod, details: String): Result<WithdrawalRequest> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        if (amount < 100.0) return Result.failure(Exception("Minimum withdrawal is ₹100"))
        if (user.availableBalance < amount) return Result.failure(Exception("Insufficient balance"))

        val updatedUser = user.copy(
            availableBalance = user.availableBalance - amount,
            pendingBalance = user.pendingBalance + amount
        )
        updateUserInternal(updatedUser)

        val req = WithdrawalRequest(
            userId = user.id,
            userName = user.displayName,
            amount = amount,
            payoutMethod = method,
            payoutDetails = details.trim(),
            status = TransactionStatus.PENDING
        )
        _withdrawalRequests.value = listOf(req) + _withdrawalRequests.value

        val tx = WalletTransaction(
            userId = user.id,
            type = TransactionType.WITHDRAWAL,
            amount = -amount,
            status = TransactionStatus.PENDING,
            reference = "Payout to ${method.title}"
        )
        _transactions.value = listOf(tx) + _transactions.value

        addNotification(
            userId = user.id,
            title = "💸 Withdrawal Request Submitted",
            message = "₹$amount withdrawal via ${method.title} is being processed. Typical time: 5-30 minutes.",
            type = NotificationType.WALLET
        )

        return Result.success(req)
    }

    fun startMatchmaking(mode: GameMode): Result<GameMatch> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        if (user.availableBalance < mode.entryFee) {
            return Result.failure(Exception("Insufficient balance. Entry fee is ₹${"%.0f".format(mode.entryFee)}."))
        }

        val updatedUser = user.copy(availableBalance = user.availableBalance - mode.entryFee)
        updateUserInternal(updatedUser)

        val tx = WalletTransaction(
            userId = user.id,
            type = TransactionType.MATCH_ENTRY,
            amount = -mode.entryFee,
            status = TransactionStatus.COMPLETED,
            reference = "${mode.displayName} Entry Fee"
        )
        _transactions.value = listOf(tx) + _transactions.value

        val oppNames = listOf("ThunderTap", "SpeedRider", "CyberNinja", "NeonFingers", "FlashKing")
        val oppName = oppNames.random()
        val oppId = "${Random.nextInt(100000, 999999)}"

        val match = GameMatch(
            mode = mode,
            player1Id = user.id,
            player1Name = user.displayName,
            player1PlayerId = user.publicPlayerId,
            player1Avatar = user.avatarSeed,
            player2Id = "opp_$oppId",
            player2Name = oppName,
            player2PlayerId = oppId,
            player2Avatar = "2",
            player1Score = 0,
            player2Score = 0,
            status = MatchStatus.WAITING,
            entryFee = mode.entryFee,
            prizeAmount = mode.prizePool
        )
        _currentMatch.value = match

        // Matchmaking flow: 2 seconds waiting, then 5 seconds countdown, then live match
        matchJob?.cancel()
        matchJob = scope.launch {
            delay(2000)
            val foundMatch = _currentMatch.value?.copy(status = MatchStatus.COUNTDOWN)
            if (foundMatch != null && foundMatch.status != MatchStatus.CANCELLED) {
                _currentMatch.value = foundMatch
                _countdownValue.value = 5

                for (i in 5 downTo 1) {
                    _countdownValue.value = i
                    delay(1000)
                }

                startLiveMatch(foundMatch)
            }
        }

        return Result.success(match)
    }

    private fun startLiveMatch(match: GameMatch) {
        val duration = match.mode.durationSeconds // 45 seconds as in screenshot!
        _remainingTimeSeconds.value = duration
        val liveMatch = match.copy(status = MatchStatus.IN_PROGRESS, player1Score = 0, player2Score = 0)
        _currentMatch.value = liveMatch

        opponentTapJob?.cancel()
        opponentTapJob = scope.launch {
            val endTime = System.currentTimeMillis() + (duration * 1000L)
            while (System.currentTimeMillis() < endTime && _currentMatch.value?.status == MatchStatus.IN_PROGRESS) {
                val delayMs = Random.nextLong(160, 280)
                delay(delayMs)
                val current = _currentMatch.value ?: break
                if (current.status == MatchStatus.IN_PROGRESS) {
                    _currentMatch.value = current.copy(player2Score = current.player2Score + 1)
                }
            }
        }

        scope.launch {
            for (sec in duration downTo 1) {
                _remainingTimeSeconds.value = sec
                delay(1000)
                if (_currentMatch.value?.status != MatchStatus.IN_PROGRESS) break
            }
            _remainingTimeSeconds.value = 0
            finalizeMatch()
        }
    }

    fun registerTap(): Boolean {
        val match = _currentMatch.value ?: return false
        if (match.status != MatchStatus.IN_PROGRESS) return false

        val now = System.currentTimeMillis()
        if (now / 1000L == currentSecondMarker) {
            tapCountInCurrentSecond++
            if (tapCountInCurrentSecond > 22) return false
        } else {
            currentSecondMarker = now / 1000L
            tapCountInCurrentSecond = 1
        }

        _currentMatch.value = match.copy(player1Score = match.player1Score + 1)
        return true
    }

    fun cancelMatchmaking(): Boolean {
        val match = _currentMatch.value ?: return false
        if (match.status != MatchStatus.WAITING) return false

        matchJob?.cancel()
        opponentTapJob?.cancel()

        val user = _currentUser.value
        if (user != null) {
            val updatedUser = user.copy(availableBalance = user.availableBalance + match.entryFee)
            updateUserInternal(updatedUser)

            val tx = WalletTransaction(
                userId = user.id,
                type = TransactionType.REFUND,
                amount = match.entryFee,
                status = TransactionStatus.COMPLETED,
                reference = "Refund: Match Cancelled"
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _currentMatch.value = match.copy(status = MatchStatus.CANCELLED, refundIssued = true)
        return true
    }

    private fun finalizeMatch() {
        opponentTapJob?.cancel()
        val match = _currentMatch.value ?: return
        if (match.status != MatchStatus.IN_PROGRESS) return

        val p1 = match.player1Score
        val p2 = match.player2Score

        val winnerId = when {
            p1 > p2 -> match.player1Id
            p2 > p1 -> match.player2Id
            else -> null
        }

        val finishedMatch = match.copy(status = MatchStatus.FINISHED, winnerId = winnerId)
        _currentMatch.value = finishedMatch
        _matchHistory.value = listOf(finishedMatch) + _matchHistory.value

        val user = _currentUser.value
        if (user != null && user.id == match.player1Id) {
            val won = winnerId == user.id
            val tied = winnerId == null

            if (won) {
                val updatedUser = user.copy(
                    availableBalance = user.availableBalance + match.prizeAmount,
                    totalGames = user.totalGames + 1,
                    matchesWon = user.matchesWon + 1,
                    totalEarnings = user.totalEarnings + match.prizeAmount
                )
                updateUserInternal(updatedUser)

                val prizeTx = WalletTransaction(
                    userId = user.id,
                    type = TransactionType.MATCH_PRIZE,
                    amount = match.prizeAmount,
                    status = TransactionStatus.COMPLETED,
                    reference = "${match.mode.displayName} Victory Prize"
                )
                _transactions.value = listOf(prizeTx) + _transactions.value

                addNotification(
                    userId = user.id,
                    title = "🏆 WINNER! (+₹${"%.0f".format(match.prizeAmount)})",
                    message = "Victory against ${match.player2Name} ($p1 vs $p2)! Prize credited.",
                    type = NotificationType.MATCH
                )
            } else if (tied) {
                val updatedUser = user.copy(
                    availableBalance = user.availableBalance + match.entryFee,
                    totalGames = user.totalGames + 1
                )
                updateUserInternal(updatedUser)

                val refundTx = WalletTransaction(
                    userId = user.id,
                    type = TransactionType.REFUND,
                    amount = match.entryFee,
                    status = TransactionStatus.COMPLETED,
                    reference = "Tie Refund"
                )
                _transactions.value = listOf(refundTx) + _transactions.value
            } else {
                val updatedUser = user.copy(totalGames = user.totalGames + 1)
                updateUserInternal(updatedUser)
            }
        }
    }

    fun dismissFinishedMatch() {
        _currentMatch.value = null
    }

    fun addNotification(userId: String, title: String, message: String, type: NotificationType) {
        val note = AppNotification(userId = userId, title = title, message = message, type = type)
        _notifications.value = listOf(note) + _notifications.value
    }

    fun markAllNotificationsRead() {
        val user = _currentUser.value ?: return
        _notifications.value = _notifications.value.map {
            if (it.userId == user.id) it.copy(isRead = true) else it
        }
    }

    fun submitSupportTicket(subject: String, message: String): Result<SupportTicket> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val ticket = SupportTicket(
            userId = user.id,
            userName = user.displayName,
            subject = subject.trim(),
            message = message.trim(),
            status = "RESOLVED",
            reply = "Thank you for contacting TAP GAME support! Our live team is on standby 24/7."
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        return Result.success(ticket)
    }

    fun adminBanUser(adminUser: UserAccount, targetUserId: String, reason: String): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN) return Result.failure(Exception("Unauthorized"))
        val target = _allUsers.value.find { it.id == targetUserId } ?: return Result.failure(Exception("Not found"))
        val newStatus = if (target.status == AccountStatus.BANNED) AccountStatus.ACTIVE else AccountStatus.BANNED
        updateUserInternal(target.copy(status = newStatus, moderationNote = reason))
        return Result.success(Unit)
    }

    fun adminRestrictUser(adminUser: UserAccount, targetUserId: String, reason: String): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN) return Result.failure(Exception("Unauthorized"))
        val target = _allUsers.value.find { it.id == targetUserId } ?: return Result.failure(Exception("Not found"))
        val newStatus = if (target.status == AccountStatus.RESTRICTED) AccountStatus.ACTIVE else AccountStatus.RESTRICTED
        updateUserInternal(target.copy(status = newStatus, moderationNote = reason))
        return Result.success(Unit)
    }

    fun adminAdjustBalance(adminUser: UserAccount, targetUserId: String, amount: Double, reason: String): Result<Unit> {
        if (adminUser.role != UserRole.ADMIN) return Result.failure(Exception("Unauthorized"))
        val target = _allUsers.value.find { it.id == targetUserId } ?: return Result.failure(Exception("Not found"))
        updateUserInternal(target.copy(availableBalance = (target.availableBalance + amount).coerceAtLeast(0.0)))
        val tx = WalletTransaction(
            userId = target.id,
            type = TransactionType.ADMIN_ADJUSTMENT,
            amount = amount,
            status = TransactionStatus.COMPLETED,
            reference = "Admin: $reason"
        )
        _transactions.value = listOf(tx) + _transactions.value
        logAudit(adminUser.email, "ADJUST_BALANCE", target.displayName, reason)
        return Result.success(Unit)
    }

    private fun logAudit(adminEmail: String, action: String, target: String, reason: String) {
        val entry = AdminAuditLog(
            adminEmail = adminEmail,
            action = action,
            target = target,
            reason = reason
        )
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    fun adminApproveDeposit(adminUser: UserAccount, reqId: String): Result<Unit> {
        return Result.success(Unit)
    }

    fun adminRejectDeposit(adminUser: UserAccount, reqId: String, reason: String): Result<Unit> {
        return Result.success(Unit)
    }

    fun adminApproveWithdrawal(adminUser: UserAccount, reqId: String): Result<Unit> {
        val req = _withdrawalRequests.value.find { it.id == reqId } ?: return Result.failure(Exception("Not found"))
        val target = _allUsers.value.find { it.id == req.userId } ?: return Result.failure(Exception("User not found"))
        updateUserInternal(target.copy(pendingBalance = (target.pendingBalance - req.amount).coerceAtLeast(0.0)))
        _withdrawalRequests.value = _withdrawalRequests.value.map {
            if (it.id == reqId) it.copy(status = TransactionStatus.COMPLETED) else it
        }
        return Result.success(Unit)
    }

    fun adminRejectWithdrawal(adminUser: UserAccount, reqId: String, reason: String): Result<Unit> {
        val req = _withdrawalRequests.value.find { it.id == reqId } ?: return Result.failure(Exception("Not found"))
        val target = _allUsers.value.find { it.id == req.userId } ?: return Result.failure(Exception("User not found"))
        updateUserInternal(target.copy(
            availableBalance = target.availableBalance + req.amount,
            pendingBalance = (target.pendingBalance - req.amount).coerceAtLeast(0.0)
        ))
        _withdrawalRequests.value = _withdrawalRequests.value.map {
            if (it.id == reqId) it.copy(status = TransactionStatus.REJECTED, rejectionReason = reason) else it
        }
        return Result.success(Unit)
    }

    fun adminUpdateConfig(adminUser: UserAccount, newConfig: AppConfig): Result<Unit> {
        _appConfig.value = newConfig
        return Result.success(Unit)
    }

    fun adminReplyTicket(adminUser: UserAccount, ticketId: String, reply: String): Result<Unit> {
        _supportTickets.value = _supportTickets.value.map {
            if (it.id == ticketId) it.copy(status = "RESOLVED", reply = reply) else it
        }
        return Result.success(Unit)
    }

    private fun updateUserInternal(updatedUser: UserAccount) {
        _allUsers.value = _allUsers.value.map {
            if (it.id == updatedUser.id) updatedUser else it
        }
        if (_currentUser.value?.id == updatedUser.id) {
            _currentUser.value = updatedUser
        }
    }

    companion object {
        @Volatile
        private var instance: TapGameRepository? = null

        fun getInstance(): TapGameRepository {
            return instance ?: synchronized(this) {
                instance ?: TapGameRepository().also { instance = it }
            }
        }
    }
}
