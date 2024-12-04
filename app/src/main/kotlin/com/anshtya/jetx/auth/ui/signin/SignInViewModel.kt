package com.anshtya.jetx.auth.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.common.Result
import com.anshtya.jetx.auth.data.AuthRepository
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
    private val _uiState = MutableStateFlow(SignInUiState())
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

    fun signIn() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(signInButtonEnabled = false)
            }

            val authResponse = authRepository.login(
                username = _uiState.value.username,
                password = _uiState.value.password
            )

            when(authResponse) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(signInSuccessful = true)
                    }
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            signInButtonEnabled = true,
                            errorMessage = authResponse.errorMessage
                        )
                    }
                }
            }
        }
    }
}