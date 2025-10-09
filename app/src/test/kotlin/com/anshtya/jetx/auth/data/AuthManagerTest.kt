package com.anshtya.jetx.auth.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.core.preferences.model.AuthToken
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AuthManagerTest {
    private val uuid = UUID.randomUUID()

    private val service: AuthService = mockk {
        coEvery {
            refreshToken(any())
        } returns NetworkResult.Success(AuthTokenResponse(uuid, "access", "refresh"))
    }
    private val tokenStore: TokenStore = mockk {
        every { getUserId() } returns uuid.toString()
        every { storeAuthToken(any(), any(), any()) } just runs
        every { clearTokenStore() } just runs
    }
    private lateinit var authManager: AuthManager

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        authManager = AuthManager(
            authService = service,
            tokenStore = tokenStore,
            scope = TestScope()
        )
    }

    @Test
    fun `initialAuth sets authenticated state`() = runTest {
        coEvery { tokenStore.getAuthToken() } returns AuthToken("access", "refresh")
        authManager = AuthManager(
            authService = service,
            tokenStore = tokenStore,
            scope = TestScope()
        )

        advanceUntilIdle()

        val authState = authManager.authState.first()
        assertTrue(authState is AuthState.Authenticated)
        assertEquals(uuid, (authState as AuthState.Authenticated).userId)
        assertEquals("access", authState.accessToken)
    }

    @Test
    fun `initialAuth sets unauthenticated state when no auth tokens`() = runTest {
        coEvery { tokenStore.getAuthToken() } returns AuthToken(null, null)
        authManager = AuthManager(
            authService = service,
            tokenStore = tokenStore,
            scope = TestScope()
        )

        advanceUntilIdle()

        val authState = authManager.authState.first()
        assertTrue(authState is AuthState.Unauthenticated)
    }

    @Test
    fun `deleteSession clears token and sets state logged out`() = runTest {
       authManager.deleteSession()

        val state = authManager.authState.first()
        assertTrue(state is AuthState.Unauthenticated)
    }
}