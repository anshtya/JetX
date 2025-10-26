package com.anshtya.jetx.profile.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProfileUiState())
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

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(
                nameError = null,
                name = name
            )
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(
                usernameError = null,
                username = username
            )
        }
    }

    fun setProfilePicture(profilePicture: Uri?) {
        _uiState.update {
            it.copy(profilePicture = profilePicture)
        }
    }

    fun createProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    usernameError = null,
                    nameError = null
                )
            }

            val state = _uiState.value
            val inputsValid = validateInputs(
                name = state.name,
                username = state.username
            )
            if (!inputsValid) {
                _uiState.update {
                    it.copy(isLoading = false)
                }
                return@launch
            }

            profileRepository.createProfile(
                name = state.name,
                username = state.username,
                photo = state.profilePicture
            ).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(profileCreated = true)
                    }
                },
                onFailure = { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message
                        )
                    }
                }
            )
        }
    }

    private fun validateInputs(name: String, username: String): Boolean {
        val errors = mutableMapOf<String, String?>()

        if (name.isEmpty()) {
            errors["nameError"] = "Name should not be empty"
        } else if (name.length > 30) {
            errors["nameError"] = "Name should be less than 30 characters"
        }

        if (username.isEmpty()) {
            errors["usernameError"] = "Username should not be empty"
        } else if (username.length > 30) {
            errors["usernameError"] = "Username should be less than 30 characters"
        }

        return if (errors.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    nameError = errors["nameError"],
                    usernameError = errors["usernameError"]
                )
            }
            false
        } else {
            true
        }
    }

    fun onErrorShown() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }
}

data class CreateProfileUiState(
    val isLoading: Boolean = false,
    val name: String = "",
    val username: String = "",
    val profilePicture: Uri? = null,
    val nameError: String? = null,
    val usernameError: String? = null,
    val usernameValid: Boolean = false,
    val errorMessage: String? = null,
    val profileCreated: Boolean = false
)