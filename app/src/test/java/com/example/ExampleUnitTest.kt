package com.example

import com.example.data.model.GameMode
import com.example.data.model.UserRole
import com.example.data.repository.TapGameRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repository: TapGameRepository

    @Before
    fun setUp() {
        repository = TapGameRepository.getInstance()
    }

    @Test
    fun testUserLoginSuccess() {
        val result = repository.login("player123@tapgame.io", "password123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("Player123", user?.displayName)
        assertEquals("458736", user?.publicPlayerId)
        assertEquals(1250.0, user!!.availableBalance, 0.01)
    }

    @Test
    fun testUserRegistrationAssignsUniqueId() {
        val uniqueEmail = "player_${System.currentTimeMillis()}@tapgame.io"
        val result = repository.register("SpeedRunner", uniqueEmail, "password123")
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("SpeedRunner", user!!.displayName)
        assertEquals(1250.0, user.availableBalance, 0.01)
    }

    @Test
    fun testMatchmakingDeductsFeeAndRefundsOnCancel() {
        val user = repository.currentUser.value!!
        val initialBalance = user.availableBalance

        val matchResult = repository.startMatchmaking(GameMode.NORMAL)
        assertTrue(matchResult.isSuccess)

        val balanceAfterEntry = repository.currentUser.value!!.availableBalance
        assertEquals(initialBalance - GameMode.NORMAL.entryFee, balanceAfterEntry, 0.01)

        val cancelSuccess = repository.cancelMatchmaking()
        assertTrue(cancelSuccess)

        val balanceAfterRefund = repository.currentUser.value!!.availableBalance
        assertEquals(initialBalance, balanceAfterRefund, 0.01)
    }

    @Test
    fun testAdminOperationsAndAuditLogging() {
        val adminResult = repository.login("admin@tapgame.io", "admin")
        assertTrue(adminResult.isSuccess)
        val admin = adminResult.getOrNull()!!
        assertEquals(UserRole.ADMIN, admin.role)

        val targetUser = repository.allUsers.value.first { it.id != admin.id }
        val prevBalance = targetUser.availableBalance

        val adjustResult = repository.adminAdjustBalance(
            adminUser = admin,
            targetUserId = targetUser.id,
            amount = 50.0,
            reason = "Test Bonus"
        )
        assertTrue(adjustResult.isSuccess)

        val updatedTarget = repository.allUsers.value.first { it.id == targetUser.id }
        assertEquals(prevBalance + 50.0, updatedTarget.availableBalance, 0.01)

        val latestLog = repository.auditLogs.value.first()
        assertEquals("Test Bonus", latestLog.reason)
        assertEquals(admin.email, latestLog.adminEmail)
    }
}
