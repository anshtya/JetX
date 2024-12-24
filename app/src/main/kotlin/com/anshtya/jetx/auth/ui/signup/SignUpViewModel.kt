package com.anshtya.jetx.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
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

            val authResult = authRepository.signUp(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            if (authResult.isSuccess) {
                _uiState.update {
                    it.copy(authSuccessful = true)
                }

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