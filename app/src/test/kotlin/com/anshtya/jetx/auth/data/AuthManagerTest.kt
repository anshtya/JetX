package com.anshtya.jetx.auth.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.core.preferences.model.AuthToken
import com.anshtya.jetx.core.preferences.store.AccountStore
import com.anshtya.jetx.core.preferences.store.TokenStore
import io.mockk.coEvery
import io.mockk.coVerify
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

    private val accountStore: AccountStore = mockk {
        coEvery { getUserId() } returns uuid.toString()
        coEvery { storeUserId(uuid.toString()) } just runs
        coEvery { clear() } just runs
    }

    private val tokenStore: TokenStore = mockk {
        coEvery { storeAuthToken(any(), any()) } just runs
        coEvery { clear() } just runs
    }

    private val store: JetxPreferencesStore = mockk {
        every { account } returns accountStore
        every { token } returns tokenStore
    }
    private lateinit var authManager: AuthManager

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        authManager = AuthManager(
            authService = service,
            store = store,
            scope = TestScope()
        )
    }

    @Test
    fun `initialAuth sets authenticated state`() = runTest {
        coEvery { tokenStore.getAuthToken() } returns AuthToken("access", "refresh")
        authManager = AuthManager(
            authService = service,
            store = store,
            scope = TestScope()
        )

        advanceUntilIdle()

        val authState = authManager.authState.first()
        assertTrue(authState is AuthState.Authenticated)
        assertEquals(uuid, (authState as AuthState.Authenticated).userId)
        assertEquals("access", authState.accessToken)

        coVerify { accountStore.storeUserId(uuid.toString()) }
        coVerify { tokenStore.storeAuthToken(any(), any()) }
    }

    @Test
    fun `initialAuth sets unauthenticated state when no auth tokens`() = runTest {
        coEvery { tokenStore.getAuthToken() } returns AuthToken(null, null)
        authManager = AuthManager(
            authService = service,
            store = store,
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

        coVerify(exactly = 1) { accountStore.clear() }
        coVerify(exactly = 1) { tokenStore.clear() }
    }
}