package com.anshtya.jetx.registration

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    private val authRepository: AuthRepository = mockk {
        coEvery { checkUser(any(), any()) } returns Result.success(true)
        coEvery { register(any(), any()) } returns Result.success(Unit)
        coEvery { login(any(), any()) } returns Result.success(Unit)
    }
    private lateinit var viewModel: RegistrationViewModel

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = RegistrationViewModel(authRepository)
        setStateWithNumber()
    }

    @Test
    fun `onPhoneNumberConfirm sets loading and sends navigation event`() = runTest {
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)

        val shouldNavigate = viewModel.navigationEvent.first()
        assertTrue(shouldNavigate)
    }

    @Test
    fun `onPhoneNumberConfirm handles repository error`() = runTest {
        coEvery { authRepository.checkUser(any(), any()) } returns Result.failure(Exception(""))

        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `onErrorShown clears error message`() = runTest {
        coEvery { authRepository.checkUser(any(), any()) } returns Result.failure(Exception(""))

        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onErrorShown()

        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `authUser calls login when user exists`() = runTest {
        assertFalse(viewModel.userExists)
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        assertTrue(viewModel.userExists)

        viewModel.authUser("1234")
        advanceUntilIdle()

        coVerify(exactly = 0) { authRepository.register(any(), any()) }
        coVerify(exactly = 1) { authRepository.login(any(), any()) }
    }

    @Test
    fun `authUser calls register when user exists`() = runTest {
        coEvery { authRepository.checkUser(any(), any()) } returns Result.success(false)

        assertFalse(viewModel.userExists)
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        assertFalse(viewModel.userExists)

        viewModel.authUser("1234")
        advanceUntilIdle()

        coVerify(exactly = 1) { authRepository.register(any(), any()) }
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `authUser handles error`() = runTest {
        coEvery { authRepository.register(any(), any()) } returns Result.failure(Exception(""))

        viewModel.authUser("1234")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
    }

    private fun setStateWithNumber() {
        viewModel.onPhoneNumberChange("1234567890")
        viewModel.onCountryCodeChange("1")
    }
}