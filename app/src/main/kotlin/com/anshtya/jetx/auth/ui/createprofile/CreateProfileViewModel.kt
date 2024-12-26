package com.anshtya.jetx.auth.ui.createprofile

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.profile.model.Profile
import com.anshtya.jetx.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(username = username)
        }
    }

    fun setProfilePicture(imageBitmap: ImageBitmap) {
        _uiState.update {
            it.copy(profilePicture = imageBitmap)
        }
    }

    fun createProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(continueButtonEnabled = false)
            }

            val state = _uiState.value
            val inputsValid = validateInputs(
                name = state.name,
                username = state.username
            )
            if (!inputsValid) {
                _uiState.update {
                    it.copy(continueButtonEnabled = true)
                }
                return@launch
            }

            val result = profileRepository.createProfile(
                profile = Profile(
                    name = state.name,
                    username = state.username,
                    profilePicture = state.profilePicture
                )
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(profileCreated = true)
                }
            } else {
                _uiState.update {
                    it.copy(errorMessage = result.exceptionOrNull()?.message)
                }
            }
        }
    }

    private fun validateInputs(name: String, username: String): Boolean {
        _uiState.update {
            it.copy(
                usernameError = null,
                nameError = null
            )
        }

        return if (name.length > 50 || name.isEmpty()) {
            _uiState.update {
                it.copy(nameError = "Name should be less than 50 characters")
            }
            false
        } else if (username.length > 50 || username.isEmpty()) {
            _uiState.update {
                it.copy(nameError = "Username should be less than 50 characters")
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
    val name: String = "",
    val username: String = "",
    val profilePicture: ImageBitmap? = null,
    val nameError: String? = null,
    val usernameError: String? = null,
    val errorMessage: String? = null,
    val continueButtonEnabled: Boolean = false,
    val profileCreated: Boolean = false
)