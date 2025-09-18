package com.anshtya.jetx.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var userExists: Boolean = false
        private set
    private val phone = PhoneNumberUtil.getInstance()

    private val _uiState = MutableStateFlow(
        RegistrationUiState(
            phoneCountryCode = phone.getCountryCodeForRegion(Locale.getDefault().country).toString()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<Boolean>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onPhoneNumberChange(number: String) {
        _uiState.update {
            it.copy(phoneNumber = number)
        }
    }

    fun onCountryCodeChange(code: String) {
        _uiState.update {
            it.copy(phoneCountryCode = code)
        }
    }

    fun onPhoneNumberConfirm() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val state = _uiState.value

            if (state.phoneNumber.isBlank() || state.phoneCountryCode.isBlank()) {
                _uiState.update {
                    it.copy(errorMessage = "Invalid phone number.")
                }
                return@launch
            }

            authRepository.checkUser(
                number = state.phoneNumber.toLong(),
                countryCode = state.phoneCountryCode.toInt()
            ).onSuccess {
                userExists = it
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message,
                        isLoading = false
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(isLoading = false)
            }
            _navigationEvent.send(true)
        }
    }

    fun authUser(
        pin: String
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val state = _uiState.value
            val phoneNumber = "+${state.phoneCountryCode}${state.phoneNumber}"
            if (userExists) {
                authRepository.login(phoneNumber, pin)
            } else {
                authRepository.register(phoneNumber, pin)
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        errorMessage = throwable.message,
                        isLoading = false
                    )
                }
                return@launch
            }
        }
    }

    fun onErrorShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val phoneCountryCode: String,
    val phoneNumber: String = "",
    val errorMessage: String? = null
)