package com.anshtya.jetx.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.ui.AuthInputValidator
import com.anshtya.jetx.auth.ui.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _authSuccessful = MutableStateFlow<Boolean?>(null)
    val authSuccessful = _authSuccessful.asStateFlow()

    fun changeUsername(username: String) {
        _uiState.update {
            it.copy(email = username)
        }
    }

    fun changePassword(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun changePasswordVisibility() {
        _uiState.update {
            it.copy(passwordVisible = !(_uiState.value.passwordVisible))
        }
    }

    fun errorShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun signUp() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    emailError = null,
                    passwordError = null,
                    authButtonEnabled = false
                )
            }

            val state = _uiState.value
            val inputsValid = AuthInputValidator.validateAuthInputs(
                email = state.email,
                password = state.password,
                setErrors = { errorMap ->
                    _uiState.update {
                        it.copy(
                            emailError = errorMap[AuthInputValidator.EMAIL_ERROR],
                            passwordError = errorMap[AuthInputValidator.PASSWORD_ERROR]
                        )
                    }
                }
            )
            if (!inputsValid) {
                _uiState.update {
                    it.copy(authButtonEnabled = true)
                }
                return@launch
            }

            val authResult = authRepository.signUp(
                email = state.email,
                password = state.password
            )

            if (authResult.isSuccess) {
                _authSuccessful.update { true }

            } else {
                _uiState.update {
                    it.copy(
                        authButtonEnabled = true,
                        errorMessage = authResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}