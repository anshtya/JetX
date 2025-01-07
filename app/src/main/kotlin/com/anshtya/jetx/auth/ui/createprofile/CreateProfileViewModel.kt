package com.anshtya.jetx.auth.ui.createprofile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun setProfilePicture(profilePicture: Bitmap) {
        _uiState.update {
            it.copy(profilePicture = profilePicture)
        }
    }

    fun createProfile() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    continueButtonEnabled = false,
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
                    it.copy(continueButtonEnabled = true)
                }
                return@launch
            }

            val result = profileRepository.createProfile(
                name = state.name,
                username = state.username,
                profilePicture = state.profilePicture
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(profileCreated = true)
                }
            } else {
                _uiState.update {
                    it.copy(
                        continueButtonEnabled = true,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    private fun validateInputs(name: String, username: String): Boolean {
        val errors = mutableMapOf<String, String?>()

        if (name.isEmpty()) {
            errors["nameError"] = "Name should not be empty"
        } else if (name.length > 50) {
            errors["nameError"] = "Name should be less than 50 characters"
        }

        if (username.isEmpty()) {
            errors["usernameError"] = "Username should not be empty"
        } else if (username.length > 50) {
            errors["usernameError"] = "Username should be less than 50 characters"
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
    val name: String = "",
    val username: String = "",
    val profilePicture: Bitmap? = null,
    val nameError: String? = null,
    val usernameError: String? = null,
    val errorMessage: String? = null,
    val continueButtonEnabled: Boolean = true,
    val profileCreated: Boolean = false
)