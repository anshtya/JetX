package com.anshtya.jetx.auth.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.model.response.CheckUserResponse
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.TokenStore
import com.anshtya.jetx.core.preferences.model.AuthToken
import com.anshtya.jetx.fcm.FcmTokenManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
    private val fcmTokenManager: FcmTokenManager = mockk {
        coEvery { getToken() } returns "fcm"
    }
    private val preferencesStore: PreferencesStore = mockk {
        coEvery { setProfileCreated() } just runs
    }
    private val userProfileDao: UserProfileDao = mockk()
    private val userProfileService: UserProfileService = mockk()
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
            fcmTokenManager = fcmTokenManager,
            preferencesStore = preferencesStore,
            userProfileService = userProfileService,
            userProfileDao = userProfileDao,
            scope = TestScope()
        )
    }

    @Test
    fun `initialAuth sets authenticated state`() = runTest {
        coEvery {
            service.refreshToken(any())
        } returns NetworkResult.Success(AuthTokenResponse("userid", "access", "refresh"))
        coEvery {
            tokenStore.getAuthToken()
        } returns AuthToken("access", "refresh")
        coEvery {
            tokenStore.storeAuthToken(any(), any(), any())
        } returns Unit
        coEvery {
            tokenStore.getUserId()
        } returns "userid"

        repository = AuthRepositoryImpl(
            authService = service,
            fcmTokenManager = fcmTokenManager,
            tokenStore = tokenStore,
            preferencesStore = preferencesStore,
            logoutManager = logoutManager,
            userProfileDao = userProfileDao,
            userProfileService = userProfileService,
            scope = this,
        )

        advanceUntilIdle()

        val authState = repository.authState.first()
        assertTrue(authState is AuthState.Authenticated)
        assertEquals("userid", (authState as AuthState.Authenticated).userId)
    }

    @Test
    fun `initialAuth sets unauthenticated state when no auth tokens`() = runTest {
        coEvery {
            tokenStore.getAuthToken()
        } returns AuthToken(null, null)

        repository = AuthRepositoryImpl(
            authService = service,
            fcmTokenManager = fcmTokenManager,
            tokenStore = tokenStore,
            preferencesStore = preferencesStore,
            logoutManager = logoutManager,
            userProfileDao = userProfileDao,
            userProfileService = userProfileService,
            scope = this,
        )

        advanceUntilIdle()

        val authState = repository.authState.first()
        assertTrue(authState is AuthState.Unauthenticated)
    }

    @Test
    fun `login success saves user id and updates state`() = runTest {
        val authResponse = AuthTokenResponse("userid", "access", "refresh")
        coEvery {
            service.login(any(), any(), any())
        } returns NetworkResult.Success(authResponse)
        every {
            tokenStore.storeAuthToken(
                userId = authResponse.userId,
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        } returns Unit

        val result = repository.login(phoneNumber, pin)
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { service.login(phoneNumber, pin, "fcm") }
        verify(exactly = 1) {
            tokenStore.storeAuthToken(
                userId = authResponse.userId,
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        }
        coVerify(exactly = 1) { preferencesStore.setProfileCreated() }

        val state = repository.authState.first()
        assertTrue(state is AuthState.Authenticated)
        assertEquals("userid", (state as AuthState.Authenticated).userId)
    }

    @Test
    fun `register success saves token and updates state`() = runTest {
        val authResponse = AuthTokenResponse("userid", "access", "refresh")
        coEvery {
            service.register(any(), any())
        } returns NetworkResult.Success(authResponse)
        every {
            tokenStore.storeAuthToken(
                userId = authResponse.userId,
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        } returns Unit

        val result = repository.register(phoneNumber, pin)
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { service.register(phoneNumber, pin) }
        verify(exactly = 1) {
            tokenStore.storeAuthToken(
                userId = authResponse.userId,
                access = authResponse.accessToken,
                refresh = authResponse.refreshToken
            )
        }

        val state = repository.authState.first()
        assertTrue(state is AuthState.Authenticated)
        assertEquals("userid", (state as AuthState.Authenticated).userId)
    }

    @Test
    fun `login failure does not save token and keeps state logged out`() = runTest {
        coEvery {
            service.login(phoneNumber = phoneNumber, pin = pin, fcmToken = "fcm")
        } returns NetworkResult.Failure.OtherError(Exception("error"))

        val result = repository.login(phoneNumber = phoneNumber, pin = pin)
        assertTrue(result.isFailure)

        coVerify { service.login(phoneNumber = phoneNumber, pin = pin, fcmToken = "fcm") }
        verify(exactly = 0) { tokenStore.storeAuthToken(any(), any(), any()) }

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
        coEvery { tokenStore.getToken(any()) } returns "token"
        coEvery { service.logoutUser(any()) } returns NetworkResult.Success(Unit)
        coEvery { logoutManager.performLocalCleanup() } returns Result.success(Unit)

        val result = repository.logout()
        assertTrue(result.isSuccess)

        coVerify { logoutManager.performLocalCleanup() }

        val state = repository.authState.first()
        assertTrue(state is AuthState.Unauthenticated)
    }
}