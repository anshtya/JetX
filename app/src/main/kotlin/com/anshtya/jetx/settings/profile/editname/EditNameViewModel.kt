package com.anshtya.jetx.settings.profile.editname

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.settings.navigation.SettingsDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    val previousName = savedStateHandle.toRoute<SettingsDestination.UserProfile.EditName>().name

    private val _uiState = MutableStateFlow(EditNameUiState(name = previousName))
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name)
        }
    }

    fun onSaveName() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }

            val name = _uiState.value.name

            val nameError = if (name.isEmpty()) {
                "Name should not be empty"
            } else if (name.length > 30) {
                "Name should be less than 30 characters"
            } else {
                null
            }
            if (nameError != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        nameError = nameError
                    )
                }
            }

            profileRepository.updateName(name).fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nameSaved = true
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

data class EditNameUiState(
    val name: String,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val nameSaved: Boolean = false,
    val errorMessage: String? = null
)