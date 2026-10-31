package com.anshtya.jetx.settings

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.settings.data.UserPreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val userId = UUID.randomUUID()
    private val profile = UserProfile(
        id = userId,
        name = "Name",
        username = "username",
        phoneNumber = "123",
        pictureUrl = null
    )

    private val authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    private val authManager: AuthManager = mockk {
        every { authState } returns this@SettingsViewModelTest.authState
    }
    private val authRepository: AuthRepository = mockk()
    private val profileRepository: ProfileRepository = mockk {
        every { getProfileFlow(userId) } returns flowOf(profile)
    }
    private val theme = MutableStateFlow(ThemeOption.SYSTEM_DEFAULT)
    private val userPreferencesRepository: UserPreferencesRepository = mockk {
        every { theme } returns this@SettingsViewModelTest.theme
        coEvery { setTheme(any()) } returns Unit
    }

    private fun viewModel() = SettingsViewModel(
        authManager = authManager,
        authRepository = authRepository,
        profileRepository = profileRepository,
        userPreferencesRepository = userPreferencesRepository
    )

    @Test
    fun `userProfile is null until the user is authenticated`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userProfile.collect() }
        advanceUntilIdle()

        assertNull(viewModel.userProfile.value)
        authState.value = AuthState.Unauthenticated
        advanceUntilIdle()
        assertNull(viewModel.userProfile.value)
        verify(exactly = 0) { profileRepository.getProfileFlow(any()) }
    }

    @Test
    fun `userProfile loads profile of the authenticated user`() = runTest {
        authState.value = AuthState.Authenticated(userId, "token")
        advanceUntilIdle()
        val viewModel = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userProfile.collect() }
        advanceUntilIdle()

        assertEquals(profile, viewModel.userProfile.value)
    }

    @Test
    fun `userProfile appears when authentication completes`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userProfile.collect() }
        advanceUntilIdle()
        assertNull(viewModel.userProfile.value)

        authState.value = AuthState.Authenticated(userId, "token")
        advanceUntilIdle()

        assertEquals(profile, viewModel.userProfile.value)
    }

    @Test
    fun `userProfile ignores missing profile`() = runTest {
        every { profileRepository.getProfileFlow(userId) } returns flowOf(null)
        authState.value = AuthState.Authenticated(userId, "token")
        advanceUntilIdle()
        val viewModel = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userProfile.collect() }
        advanceUntilIdle()

        assertNull(viewModel.userProfile.value)
    }

    @Test
    fun `userSettings follows the theme preference`() = runTest {
        val viewModel = viewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.userSettings.collect() }
        advanceUntilIdle()

        assertEquals(UserSettings(ThemeOption.SYSTEM_DEFAULT), viewModel.userSettings.value)
        theme.value = ThemeOption.DARK
        advanceUntilIdle()
        assertEquals(UserSettings(ThemeOption.DARK), viewModel.userSettings.value)
    }

    @Test
    fun `changeTheme saves the selected theme`() = runTest {
        viewModel().changeTheme(ThemeOption.LIGHT)

        coVerify { userPreferencesRepository.setTheme(ThemeOption.LIGHT) }
    }
}
