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

    private val _waitingMatches = MutableStateFlow<List<GameMatch>>(emptyList())
    val waitingMatches: StateFlow<List<GameMatch>> = _waitingMatches.asStateFlow()

    private var matchJob: Job? = null
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
            availableBalance = 10.0,
            pendingBalance = 0.0,
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
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
            availableBalance = 10.0,
            pendingBalance = 0.0,
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
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
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
            isVerified = true,
            status = AccountStatus.ACTIVE,
            role = UserRole.ADMIN
        )

        _allUsers.value = listOf(user1, user2, adminUser)
        _currentUser.value = user1

        val seedTransactions = listOf(
            WalletTransaction(
                userId = user1.id,
                type = TransactionType.SIGNUP_BONUS,
                amount = 10.0,
                status = TransactionStatus.COMPLETED,
                reference = "₹10 Welcome Signup Bonus",
                timestamp = System.currentTimeMillis()
            )
        )
        _transactions.value = seedTransactions

        _matchHistory.value = emptyList()

        _notifications.value = listOf(
            AppNotification(
                userId = user1.id,
                title = "🎉 Welcome to TAP GAME!",
                message = "₹10 Signup Bonus credited directly to your real wallet! Play ₹10 Micro Match instantly.",
                type = NotificationType.SYSTEM
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
            // New user registration on first login with ₹10 welcome bonus
            val publicId = "${Random.nextInt(100000, 999999)}"
            val isEmail = trimmed.contains("@")
            val newUser = UserAccount(
                email = if (isEmail) trimmed else "player_$publicId@tapgame.io",
                mobileNumber = if (!isEmail && trimmed.all { it.isDigit() }) trimmed else "9876543210",
                displayName = if (isEmail) trimmed.substringBefore("@") else "Player$publicId",
                publicPlayerId = publicId,
                avatarSeed = "1",
                availableBalance = 10.0,
                totalGames = 0,
                matchesWon = 0,
                totalEarnings = 0.0,
                isVerified = true
            )
            _allUsers.value = _allUsers.value + newUser
            _currentUser.value = newUser

            val bonusTx = WalletTransaction(
                userId = newUser.id,
                type = TransactionType.SIGNUP_BONUS,
                amount = 10.0,
                status = TransactionStatus.COMPLETED,
                reference = "₹10 Welcome Signup Bonus"
            )
            _transactions.value = listOf(bonusTx) + _transactions.value

            addNotification(
                userId = newUser.id,
                title = "🎁 ₹10 Signup Bonus Credited!",
                message = "Welcome! Your ₹10 bonus is in your real wallet. Play ₹10 Micro Match now!",
                type = NotificationType.SYSTEM
            )

            Result.success(newUser)
        }
    }

    fun register(name: String, mobileOrEmail: String, pass: String): Result<UserAccount> {
        val publicId = "${Random.nextInt(100000, 999999)}"
        val isEmail = mobileOrEmail.contains("@")
        val clean = mobileOrEmail.trim()

        val existing = _allUsers.value.find {
            (isEmail && it.email.equals(clean, ignoreCase = true)) ||
                    (!isEmail && it.mobileNumber.endsWith(clean.takeLast(10)))
        }
        if (existing != null) {
            _currentUser.value = existing
            return Result.success(existing)
        }

        val newUser = UserAccount(
            email = if (isEmail) clean else "player_$publicId@tapgame.io",
            mobileNumber = if (!isEmail) clean else "+91 9876543210",
            displayName = name.trim().ifEmpty { if (isEmail) clean.substringBefore("@") else "Player$publicId" },
            publicPlayerId = publicId,
            avatarSeed = "1",
            availableBalance = 10.0,
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
            isVerified = true
        )
        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser

        val bonusTx = WalletTransaction(
            userId = newUser.id,
            type = TransactionType.SIGNUP_BONUS,
            amount = 10.0,
            status = TransactionStatus.COMPLETED,
            reference = "₹10 Welcome Signup Bonus"
        )
        _transactions.value = listOf(bonusTx) + _transactions.value

        addNotification(
            userId = newUser.id,
            title = "🎁 ₹10 Signup Bonus Credited!",
            message = "Welcome! Your ₹10 bonus is in your real wallet. Play ₹10 Micro Match now!",
            type = NotificationType.SYSTEM
        )

        return Result.success(newUser)
    }

    fun googleSignIn(accountEmail: String = "sahid50534@gmail.com", accountName: String = "Sahid"): Result<UserAccount> {
        val existing = _allUsers.value.find { it.email.equals(accountEmail.trim(), ignoreCase = true) }
        if (existing != null) {
            _currentUser.value = existing
            return Result.success(existing)
        }

        val publicId = "${Random.nextInt(100000, 999999)}"
        val newUser = UserAccount(
            email = accountEmail.trim(),
            mobileNumber = "+91 9876543210",
            displayName = accountName.trim().ifEmpty { accountEmail.substringBefore("@") },
            publicPlayerId = publicId,
            avatarSeed = "2",
            availableBalance = 10.0,
            pendingBalance = 0.0,
            totalGames = 0,
            matchesWon = 0,
            totalEarnings = 0.0,
            isVerified = true
        )
        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser

        val bonusTx = WalletTransaction(
            userId = newUser.id,
            type = TransactionType.SIGNUP_BONUS,
            amount = 10.0,
            status = TransactionStatus.COMPLETED,
            reference = "₹10 Google Sign-In Welcome Bonus"
        )
        _transactions.value = listOf(bonusTx) + _transactions.value

        addNotification(
            userId = newUser.id,
            title = "🎁 ₹10 Google Sign-Up Bonus!",
            message = "Signed in with Google! ₹10 bonus credited to your real wallet. Ready for 1v1 battles!",
            type = NotificationType.SYSTEM
        )

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

    fun createCustomMatch(mode: GameMode, customFee: Double? = null): Result<GameMatch> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val fee = customFee ?: mode.entryFee
        val prize = customFee?.let { it * 1.8 } ?: mode.prizePool

        if (user.status == AccountStatus.BANNED || user.status == AccountStatus.RESTRICTED) {
            return Result.failure(Exception("Account is restricted from playing matches"))
        }

        if (user.availableBalance < fee) {
            return Result.failure(Exception("Insufficient balance. Need ₹${fee.toInt()}, available is ₹${user.availableBalance.toInt()}"))
        }

        // Atomically reserve entry fee
        val updatedUser = user.copy(availableBalance = user.availableBalance - fee)
        updateUserInternal(updatedUser)

        val tx = WalletTransaction(
            userId = user.id,
            type = TransactionType.MATCH_ENTRY,
            amount = -fee,
            status = TransactionStatus.COMPLETED,
            reference = "${mode.displayName} Entry Fee"
        )
        _transactions.value = listOf(tx) + _transactions.value

        val code = Random.nextInt(100000, 999999).toString()
        val match = GameMatch(
            matchCode = code,
            creatorId = user.id,
            mode = mode,
            player1Id = user.id,
            player1Name = user.displayName,
            player1PlayerId = user.publicPlayerId,
            player1Avatar = user.avatarSeed,
            player2Id = null,
            player2Name = null,
            player2PlayerId = null,
            player2Avatar = null,
            player1Score = 0,
            player2Score = 0,
            status = MatchStatus.WAITING,
            entryFee = fee,
            prizeAmount = prize
        )

        _currentMatch.value = match
        _waitingMatches.value = listOf(match) + _waitingMatches.value.filter { it.id != match.id }
        return Result.success(match)
    }

    fun startMatchmaking(mode: GameMode): Result<GameMatch> {
        return createCustomMatch(mode)
    }

    fun searchMatchByCode(code: String): Result<GameMatch> {
        val trimmed = code.trim()
        if (trimmed.length != 6) {
            return Result.failure(Exception("Match ID must be 6 numeric digits"))
        }

        val found = _waitingMatches.value.find { it.matchCode == trimmed && it.status == MatchStatus.WAITING }
            ?: if (_currentMatch.value?.matchCode == trimmed && _currentMatch.value?.status == MatchStatus.WAITING) _currentMatch.value else null

        return if (found != null) {
            Result.success(found)
        } else {
            Result.failure(Exception("No active waiting match found with ID #$trimmed"))
        }
    }

    fun joinMatchByCode(code: String): Result<GameMatch> {
        val user = _currentUser.value ?: return Result.failure(Exception("Not logged in"))
        val searchRes = searchMatchByCode(code)
        if (searchRes.isFailure) return searchRes

        val match = searchRes.getOrNull() ?: return Result.failure(Exception("Match not found"))

        if (match.status != MatchStatus.WAITING) {
            return Result.failure(Exception("Match is already in progress or completed"))
        }

        if (match.creatorId == user.id) {
            return Result.failure(Exception("You cannot join your own match. Share Match ID #${match.matchCode} with a real opponent!"))
        }

        if (user.availableBalance < match.entryFee) {
            return Result.failure(Exception("Insufficient balance to join. Entry fee is ₹${match.entryFee.toInt()}, available is ₹${user.availableBalance.toInt()}"))
        }

        // Atomically reserve second player's entry fee
        val updatedUser = user.copy(availableBalance = user.availableBalance - match.entryFee)
        updateUserInternal(updatedUser)

        val tx = WalletTransaction(
            userId = user.id,
            type = TransactionType.MATCH_ENTRY,
            amount = -match.entryFee,
            status = TransactionStatus.COMPLETED,
            reference = "Match #${match.matchCode} Entry Fee"
        )
        _transactions.value = listOf(tx) + _transactions.value

        // Lock match against other participants and set player 2 details
        val joinedMatch = match.copy(
            player2Id = user.id,
            player2Name = user.displayName,
            player2PlayerId = user.publicPlayerId,
            player2Avatar = user.avatarSeed,
            status = MatchStatus.COUNTDOWN
        )

        _currentMatch.value = joinedMatch
        _waitingMatches.value = _waitingMatches.value.filter { it.matchCode != code }

        // Start 5-second synchronized countdown, then live match
        matchJob?.cancel()
        matchJob = scope.launch {
            _countdownValue.value = 5
            for (i in 5 downTo 1) {
                _countdownValue.value = i
                delay(1000)
            }
            startLiveMatch(joinedMatch)
        }

        return Result.success(joinedMatch)
    }

    private fun startLiveMatch(match: GameMatch) {
        val duration = match.mode.durationSeconds // 45 seconds as in screenshot!
        _remainingTimeSeconds.value = duration
        val liveMatch = match.copy(status = MatchStatus.IN_PROGRESS, player1Score = 0, player2Score = 0)
        _currentMatch.value = liveMatch

        matchJob?.cancel()
        matchJob = scope.launch {
            for (sec in duration downTo 1) {
                _remainingTimeSeconds.value = sec
                delay(1000)
                if (_currentMatch.value?.status != MatchStatus.IN_PROGRESS) break
            }
            _remainingTimeSeconds.value = 0
            finalizeMatch()
        }
    }

    fun registerTap(isOpponentOrPlayer2: Boolean = false): Boolean {
        val match = _currentMatch.value ?: return false
        if (match.status != MatchStatus.IN_PROGRESS) return false

        val now = System.currentTimeMillis()
        if (now / 1000L == currentSecondMarker) {
            tapCountInCurrentSecond++
            if (tapCountInCurrentSecond > 22) return false // Anti-cheat rate limit
        } else {
            currentSecondMarker = now / 1000L
            tapCountInCurrentSecond = 1
        }

        val currentUser = _currentUser.value
        val isUserPlayer2 = currentUser != null && currentUser.id == match.player2Id

        _currentMatch.value = if (isOpponentOrPlayer2 || isUserPlayer2) {
            match.copy(player2Score = match.player2Score + 1)
        } else {
            match.copy(player1Score = match.player1Score + 1)
        }
        return true
    }

    fun cancelMatchmaking(): Boolean {
        val match = _currentMatch.value ?: return false
        if (match.status != MatchStatus.WAITING) return false

        matchJob?.cancel()

        val user = _currentUser.value
        if (user != null && user.id == match.creatorId) {
            val updatedUser = user.copy(availableBalance = user.availableBalance + match.entryFee)
            updateUserInternal(updatedUser)

            val tx = WalletTransaction(
                userId = user.id,
                type = TransactionType.REFUND,
                amount = match.entryFee,
                status = TransactionStatus.COMPLETED,
                reference = "Refund: Cancelled Match #${match.matchCode}"
            )
            _transactions.value = listOf(tx) + _transactions.value
        }

        _waitingMatches.value = _waitingMatches.value.filter { it.id != match.id }
        _currentMatch.value = match.copy(status = MatchStatus.CANCELLED, refundIssued = true)
        return true
    }

    private fun finalizeMatch() {
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
        if (user != null && (user.id == match.player1Id || user.id == match.player2Id)) {
            val isUserWinner = winnerId == user.id
            val isTie = winnerId == null

            if (isUserWinner) {
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

                val opponentName = if (user.id == match.player1Id) (match.player2Name ?: "Challenger") else match.player1Name
                addNotification(
                    userId = user.id,
                    title = "🏆 WINNER! (+₹${"%.0f".format(match.prizeAmount)})",
                    message = "Victory against $opponentName ($p1 vs $p2)! Prize credited to your balance.",
                    type = NotificationType.MATCH
                )
            } else if (isTie) {
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
                    reference = "Tie Refund #${match.matchCode}"
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
