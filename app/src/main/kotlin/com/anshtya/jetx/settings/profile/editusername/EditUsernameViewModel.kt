package com.anshtya.jetx.settings.profile.editusername

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.settings.navigation.SettingsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class EditUsernameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    val previousUsername = savedStateHandle.toRoute<SettingsDestination.UserProfile.EditUsername>()
        .username

    private val _uiState = MutableStateFlow(EditUsernameUiState(username = previousUsername))
    val uiState = _uiState.asStateFlow()

    init {
        checkUsername()
    }

    private fun checkUsername() {
        _uiState
            .map { it.username }
            .distinctUntilChanged()
            .debounce(350L)
            .filter { it.isNotEmpty() }
            .onEach { username ->
                profileRepository.checkUsername(username).fold(
                    onSuccess = { result ->
                        _uiState.update {
                            it.copy(
                                usernameValid = result.valid,
                                usernameError = result.message
                            )
                        }
                    },
                    onFailure = { throwable ->
                        _uiState.update {
                            it.copy(
                                usernameValid = false,
                                usernameError = throwable.message
                            )
                        }
                    }
                )
            }.launchIn(viewModelScope)
    }

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(
                username = username,
                usernameError = null
            )
        }
    }

    fun onSaveUsername() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val username = _uiState.value.username

            val usernameError = if (username.isEmpty()) {
                "Username should not be empty"
            } else if (username.length > 30) {
                "Username should be less than 30 characters"
            } else {
                null
            }
            if (usernameError != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        usernameError = usernameError
                    )
                }
            }

            profileRepository.updateUsername(username).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            usernameSaved = true
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "An error occurred"
                        )
                    }
                }
            )
        }
    }

    fun onErrorShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}

data class EditUsernameUiState(
    val username: String,
    val isLoading: Boolean = false,
    val usernameError: String? = null,
    val usernameValid: Boolean = false,
    val usernameSaved: Boolean = false,
    val errorMessage: String? = null
)