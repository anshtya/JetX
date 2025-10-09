package com.anshtya.jetx.auth.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.CheckUserResponse
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.profile.data.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImplTest {
    private var phoneNumber = "+12234567891"
    private val uuid = UUID.randomUUID()

    private val authService: AuthService = mockk {
        coEvery { checkUser(phoneNumber) } returns NetworkResult.Success(CheckUserResponse(true))
        coEvery { logoutUser(any()) } returns NetworkResult.Success(Unit)
    }
    private val logoutManager: LogoutManager = mockk {
        coEvery { performLocalCleanup() } returns Result.success(Unit)
    }
    private val profileRepository: ProfileRepository = mockk()
    private val authManager: AuthManager = mockk {
        every { authState } returns MutableStateFlow(AuthState.Authenticated(uuid, "access"))
        every { deleteSession() } just runs
    }
    private val preferencesStore: PreferencesStore = mockk {
        coEvery { setProfileCreated() } just runs
    }
    private val fcmTokenManager: FcmTokenManager = mockk {
        coEvery { getToken() } returns "fcm"
    }
    private lateinit var repository: AuthRepositoryImpl

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = AuthRepositoryImpl(
            profileRepository = profileRepository,
            authService = authService,
            logoutManager = logoutManager,
            fcmTokenManager = fcmTokenManager,
            authManager = authManager,
            preferencesStore = preferencesStore,
        )
    }

    @Test
    fun `checkUser delegates to service`() = runTest {
        coEvery {
            authService.checkUser(phoneNumber)
        } returns NetworkResult.Success(CheckUserResponse(true))

        val phoneNumberParts = phoneNumber.split("+1")

        val result = repository.checkUser(
            number = phoneNumberParts.last().toLong(),
            countryCode = 1
        )
        assertTrue(result.getOrNull() == true)
        coVerify(exactly = 1) { authService.checkUser(phoneNumber) }
    }

    @Test
    fun `checkUser throws error for invalid phone number`() = runTest {
        val result = repository.checkUser(
            number = 23,
            countryCode = 1234
        )
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { authService.checkUser(any()) }
    }

    @Test
    fun `logout clears token and sets state logged out`() = runTest {
        coEvery { authService.logoutUser(any()) } returns NetworkResult.Success(Unit)

        val result = repository.logout()
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { logoutManager.performLocalCleanup() }
    }
}