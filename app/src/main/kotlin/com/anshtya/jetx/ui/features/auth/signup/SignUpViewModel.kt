package com.anshtya.jetx.ui.features.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.repository.AuthRepository
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
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    fun changeUsername(username: String) {
        _uiState.update {
            it.copy(username = username)
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
                    signUpButtonEnabled = false
                )
            }

            val username = _uiState.value.username
            val password = _uiState.value.password

            val authResponse = authRepository.signup(
                username = username,
                password = password
            )

            when (authResponse) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(signUpSuccessful = true)
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            signUpButtonEnabled = true,
                            errorMessage = authResponse.errorMessage
                        )
                    }
                }
            }
        }
    }
}