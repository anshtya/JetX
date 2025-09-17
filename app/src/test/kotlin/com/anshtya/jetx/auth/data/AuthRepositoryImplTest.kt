package com.anshtya.jetx.auth.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.model.response.CheckUserResponse
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.core.preferences.model.AuthToken
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {
    private val service: AuthService = mockk()
    private val logoutManager: LogoutManager = mockk()
    private val tokenStore: TokenStore = mockk()
    private lateinit var repository: AuthRepositoryImpl

    private var phoneNumber = "+12234567891"
    private var pin = "1234"

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(
            authService = service,
            tokenStore = tokenStore,
            logoutManager = logoutManager,
            scope = TestScope()
        )
    }

    @Test
    fun `initialAuth sets authenticated state`() = runTest {
        coEvery {
            service.refreshToken(any())
        } returns NetworkResult.Success(AuthTokenResponse("access", "refresh"))
        coEvery {
            tokenStore.getAuthToken()
        } returns AuthToken("access", "refresh")
        coEvery {
            tokenStore.storeAuthToken(any(), any())
        } returns Unit
        coEvery {
            tokenStore.getAccessToken()
        } returns "access"

        repository = AuthRepositoryImpl(service, tokenStore, logoutManager, this)

        advanceUntilIdle()

        val authState = repository.authState.first()
        assertTrue(authState is AuthState.Authenticated)
        assertEquals("access", (authState as AuthState.Authenticated).token)
    }

    @Test
    fun `initialAuth sets unauthenticated state when no auth tokens`() = runTest {
        coEvery {
            tokenStore.getAuthToken()
        } returns AuthToken(null, null)

        repository = AuthRepositoryImpl(service, tokenStore, logoutManager, this)

        advanceUntilIdle()

        val authState = repository.authState.first()
        assertTrue(authState is AuthState.Unauthenticated)
    }

    @Test
    fun `login success saves token and updates state`() = runTest {
        val authResponse = AuthTokenResponse("access", "refresh")
        coEvery {
            service.login(any(), any())
        } returns NetworkResult.Success(authResponse)
        every {
            tokenStore.storeAuthToken(
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        } returns Unit

        val result = repository.login(phoneNumber, pin)
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { service.login(phoneNumber, pin) }
        verify(exactly = 1) {
            tokenStore.storeAuthToken(
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        }

        val state = repository.authState.first()
        assertTrue(state is AuthState.Authenticated)
        assertEquals("access", (state as AuthState.Authenticated).token)
    }

    @Test
    fun `register success saves token and updates state`() = runTest {
        val authResponse = AuthTokenResponse("access", "refresh")
        coEvery {
            service.register(any(), any())
        } returns NetworkResult.Success(authResponse)
        every {
            tokenStore.storeAuthToken(
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        } returns Unit

        val result = repository.register(phoneNumber, pin)
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { service.register(phoneNumber, pin) }
        verify(exactly = 1) {
            tokenStore.storeAuthToken(
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        }

        val state = repository.authState.first()
        assertTrue(state is AuthState.Authenticated)
        assertEquals("access", (state as AuthState.Authenticated).token)
    }

    @Test
    fun `login failure does not save token and keeps state logged out`() = runTest {
        coEvery {
            service.login(phoneNumber = phoneNumber, pin = pin)
        } returns NetworkResult.Failure.OtherError(Exception("error"))

        val result = repository.login(phoneNumber = phoneNumber, pin = pin)
        assertTrue(result.isFailure)

        coVerify { service.login(phoneNumber = phoneNumber, pin = pin) }
        verify(exactly = 0) { tokenStore.storeAuthToken(any(), any()) }

        val state = repository.authState.first()
        assertFalse(state is AuthState.Authenticated)
    }

    @Test
    fun `checkUser delegates to service`() = runTest {
        coEvery {
            service.checkUser(phoneNumber)
        } returns NetworkResult.Success(CheckUserResponse(true))

        val phoneNumberParts = phoneNumber.split("+1")

        val result = repository.checkUser(
            number = phoneNumberParts.last().toLong(),
            countryCode = 1
        )
        assertTrue(result.getOrNull() == true)
        coVerify(exactly = 1) { service.checkUser(phoneNumber) }
    }

    @Test
    fun `checkUser throws error for invalid phone number`() = runTest {
        val result = repository.checkUser(
            number = 23,
            countryCode = 1234
        )
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { service.checkUser(any()) }
    }

    @Test
    fun `logout clears token and sets state logged out`() = runTest {
        // Pretend already logged in
        coEvery {
            service.login(phoneNumber = any(), pin = any())
        } returns NetworkResult.Success(AuthTokenResponse("access", "refresh"))
        every {
            tokenStore.storeAuthToken(any(), any())
        } returns Unit
        repository.login(phoneNumber = phoneNumber, pin = pin)
        var state = repository.authState.first()
        assertTrue(state is AuthState.Authenticated)

        coEvery { service.logoutUser(any()) } returns NetworkResult.Success(Unit)
        coEvery { logoutManager.performLocalCleanup() } returns Unit

        val result = repository.logout()
        assertTrue(result.isSuccess)

        coVerify { logoutManager.performLocalCleanup() }

        state = repository.authState.first()
        assertTrue(state is AuthState.Unauthenticated)
    }
}