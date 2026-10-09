package com.example.data.model

import java.util.UUID

enum class AccountStatus {
    ACTIVE,
    RESTRICTED,
    BANNED
}

enum class UserRole {
    USER,
    ADMIN
}

data class UserAccount(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val mobileNumber: String = "+91 9876543210",
    val displayName: String,
    val publicPlayerId: String, // e.g. "458736" or "#TAP-8849"
    val avatarSeed: String = "1",
    val availableBalance: Double = 1250.0,
    val pendingBalance: Double = 0.0,
    val totalGames: Int = 12,
    val matchesWon: Int = 8,
    val totalEarnings: Double = 720.0,
    val isVerified: Boolean = true,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val role: UserRole = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis(),
    val moderationNote: String = ""
) {
    val winRate: Int
        get() = if (totalGames > 0) ((matchesWon.toDouble() / totalGames) * 100).toInt() else 0
}

enum class GameMode(
    val displayName: String,
    val entryFee: Double,
    val prizePool: Double,
    val durationSeconds: Int,
    val isPopular: Boolean = false,
    val description: String = ""
) {
    NORMAL(
        displayName = "Normal Mode",
        entryFee = 50.0,
        prizePool = 90.0,
        durationSeconds = 45,
        isPopular = true,
        description = "Popular 2-player battle"
    ),
    PRO(
        displayName = "Pro Mode",
        entryFee = 100.0,
        prizePool = 180.0,
        durationSeconds = 45,
        isPopular = false,
        description = "High intensity speed battle"
    ),
    HIGH_ROLLER(
        displayName = "High Rollers",
        entryFee = 200.0,
        prizePool = 360.0,
        durationSeconds = 45,
        isPopular = false,
        description = "High stakes showdown"
    ),
    ELITE(
        displayName = "Elite Arena",
        entryFee = 500.0,
        prizePool = 900.0,
        durationSeconds = 45,
        isPopular = false,
        description = "Ultimate mega prize arena"
    )
}

enum class MatchStatus {
    WAITING,
    COUNTDOWN,
    IN_PROGRESS,
    FINISHED,
    CANCELLED
}

data class GameMatch(
    val id: String = UUID.randomUUID().toString().take(8),
    val mode: GameMode = GameMode.NORMAL,
    val player1Id: String,
    val player1Name: String,
    val player1PlayerId: String,
    val player1Avatar: String,
    val player2Id: String,
    val player2Name: String,
    val player2PlayerId: String,
    val player2Avatar: String,
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val winnerId: String? = null,
    val status: MatchStatus = MatchStatus.WAITING,
    val entryFee: Double = 50.0,
    val prizeAmount: Double = 90.0,
    val timestamp: Long = System.currentTimeMillis(),
    val refundIssued: Boolean = false
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    MATCH_ENTRY,
    MATCH_PRIZE,
    REFUND,
    ADMIN_ADJUSTMENT
}

enum class TransactionStatus {
    PENDING,
    COMPLETED,
    REJECTED
}

data class WalletTransaction(
    val id: String = UUID.randomUUID().toString().take(10),
    val userId: String,
    val type: TransactionType,
    val amount: Double,
    val status: TransactionStatus,
    val timestamp: Long = System.currentTimeMillis(),
    val reference: String = "",
    val idempotencyKey: String = UUID.randomUUID().toString()
)

enum class DepositPaymentMethod(val title: String, val subtitle: String) {
    UPI("UPI", "Fast & Secure"),
    PHONEPE("PhonePe", "Instant Deposit"),
    PAYTM("Paytm", "Instant Deposit"),
    GOOGLE_PAY("Google Pay", "Instant Deposit"),
    BANK_TRANSFER("Bank Transfer", "1-2 Working Hours")
}

data class DepositRequest(
    val id: String = UUID.randomUUID().toString().take(8),
    val userId: String,
    val userName: String,
    val amount: Double,
    val paymentMethod: DepositPaymentMethod = DepositPaymentMethod.UPI,
    val upiId: String = "",
    val utrNumber: String = "",
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)

enum class PayoutMethod(val title: String) {
    UPI("UPI"),
    BANK_TRANSFER("Bank Transfer"),
    PAYTM_WALLET("Paytm Wallet"),
    UPI_LITE("UPI Lite")
}

data class WithdrawalRequest(
    val id: String = UUID.randomUUID().toString().take(8),
    val userId: String,
    val userName: String,
    val amount: Double,
    val payoutMethod: PayoutMethod = PayoutMethod.UPI,
    val payoutDetails: String = "",
    val status: TransactionStatus = TransactionStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val rejectionReason: String = ""
)

enum class NotificationType {
    MATCH,
    WALLET,
    SYSTEM,
    ADMIN
}

data class AppNotification(
    val id: String = UUID.randomUUID().toString().take(8),
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class SupportTicket(
    val id: String = UUID.randomUUID().toString().take(8),
    val userId: String,
    val userName: String,
    val subject: String,
    val message: String,
    val status: String = "OPEN",
    val reply: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val rank: Int,
    val playerId: String,
    val displayName: String,
    val avatar: String,
    val wins: Int,
    val earnings: Double,
    val winRate: Int,
    val isCurrentUser: Boolean = false
)

data class AdminAuditLog(
    val id: String = UUID.randomUUID().toString().take(8),
    val adminEmail: String,
    val action: String,
    val target: String,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppConfig(
    val maintenanceMode: Boolean = false,
    val matchmakingEnabled: Boolean = true,
    val announcement: String = "⚡ TAP & WIN LIVE TOURNAMENT! Double prizes active!",
    val minDeposit: Double = 50.0,
    val maxDeposit: Double = 10000.0,
    val minWithdrawal: Double = 100.0,
    val maxWithdrawal: Double = 10000.0,
    val matchDurationSeconds: Int = 45,
    val countdownSeconds: Int = 5,
    val normalModeActive: Boolean = true,
    val proModeActive: Boolean = true,
    val highRollerModeActive: Boolean = true,
    val eliteModeActive: Boolean = true
)
