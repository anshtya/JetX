package com.anshtya.jetx.registration

import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.auth.data.FakeAuthRepository
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {
    private lateinit var fakeAuthRepository: FakeAuthRepository
    private lateinit var viewModel: RegistrationViewModel

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        fakeAuthRepository = FakeAuthRepository()
        viewModel = RegistrationViewModel(fakeAuthRepository)
        setStateWithNumber()
    }

    @Test
    fun `onPhoneNumberConfirm sets loading and sends navigation event`() = runTest {
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        TestCase.assertFalse(viewModel.uiState.value.isLoading)

        val shouldNavigate = viewModel.navigationEvent.first()
        TestCase.assertTrue(shouldNavigate)
    }

    @Test
    fun `onPhoneNumberConfirm handles repository error`() = runTest {
        fakeAuthRepository.shouldFailCheckUser = true

        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        TestCase.assertFalse(state.isLoading)
        TestCase.assertNotNull(state.errorMessage)
    }

    @Test
    fun `onErrorShown clears error message`() = runTest {
        fakeAuthRepository.shouldFailCheckUser = true
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        TestCase.assertNotNull(viewModel.uiState.value.errorMessage)

        viewModel.onErrorShown()

        TestCase.assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `authUser calls register when user exists`() = runTest {
        TestCase.assertFalse(viewModel.userExists)
        fakeAuthRepository.shouldUserExist = true
        viewModel.onPhoneNumberConfirm()
        advanceUntilIdle()

        TestCase.assertTrue(viewModel.userExists)

        viewModel.authUser("1234")
        advanceUntilIdle()

        val shouldNavigate = viewModel.navigationEvent.first()
        TestCase.assertTrue(shouldNavigate)
    }

    @Test
    fun `authUser handles error`() = runTest {
        fakeAuthRepository.shouldFailLogin = true

        viewModel.authUser("1234")
        advanceUntilIdle()

        TestCase.assertNotNull(viewModel.uiState.value.errorMessage)
    }

    private fun setStateWithNumber() {
        viewModel.onPhoneNumberChange("1234567890")
        viewModel.onCountryCodeChange("1")
    }
}