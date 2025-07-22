package com.anshtya.jetx.auth.ui.signin

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
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _profileCreated = MutableStateFlow<Boolean?>(null)
    val profileCreated = _profileCreated.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(email = username)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    fun onPasswordVisibilityChange() {
        _uiState.update {
            it.copy(passwordVisible = !(_uiState.value.passwordVisible))
        }
    }

    fun onErrorShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun signIn() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    emailError = null,
                    passwordError = null,
                    isLoading = true
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
                    it.copy(isLoading = false)
                }
                return@launch
            }

            val authResult = authRepository.signIn(
                email = state.email,
                password = state.password
            )

            if (authResult.isSuccess) {
                _profileCreated.update { authResult.getOrNull() }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = authResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }
}