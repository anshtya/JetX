package com.anshtya.jetx.profile.data

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.database.dao.UserProfileDao
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.response.GetUserProfileResponse
import com.anshtya.jetx.core.network.service.UserProfileService
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.fcm.FcmTokenManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class ProfileRepositoryImplTest {
    private val uuid = UUID.randomUUID()

    private val authManager: AuthManager = mockk {
        every { authState } returns MutableStateFlow(AuthState.Authenticated(uuid, "access"))
    }
    private val fcmTokenManager: FcmTokenManager = mockk {
        coEvery { getToken() } returns "token"
    }
    private val userProfileService: UserProfileService = mockk {
        coEvery { createProfile(any(), any()) } returns NetworkResult.Success(
            GetUserProfileResponse("", "", "", "")
        )
    }
    private val preferencesStore: PreferencesStore = mockk {
        coEvery { setProfileCreated() } just runs
    }
    private val userProfileDao: UserProfileDao = mockk {
        coEvery { upsertUserProfile(any()) } just runs
    }


    private lateinit var repository: ProfileRepositoryImpl

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = ProfileRepositoryImpl(
            authManager = authManager,
            userProfileService = userProfileService,
            avatarManager = mockk(),
            fcmTokenManager = fcmTokenManager,
            preferencesStore = preferencesStore,
            userProfileDao = userProfileDao,
            imageCompressor = mockk()
        )
    }

    @Test
    fun `createProfile creates profile`() = runTest {
        val result = repository.createProfile("name", "username", null)
        assertTrue(result.isSuccess)

        coVerify(exactly = 1) { preferencesStore.setProfileCreated() }
        coVerify(exactly = 1) { userProfileDao.upsertUserProfile(any()) }
    }

    @Test
    fun `createProfile failure doesn't creates profile`() = runTest {
        coEvery {
            userProfileService.createProfile(any(), any())
        } returns NetworkResult.Failure.OtherError(Exception(""))

        val result = repository.createProfile("name", "username", null)
        assertTrue(result.isFailure)

        coVerify(exactly = 0) { preferencesStore.setProfileCreated() }
        coVerify(exactly = 0) { userProfileDao.upsertUserProfile(any()) }
    }
}