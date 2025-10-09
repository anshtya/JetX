package com.anshtya.jetx.settings.profile

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ViewProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onEditProfilePhoto(image: Bitmap?) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true)
            }
            if (image != null) {
                profileRepository.updateProfilePhoto(image)
            } else {
                profileRepository.removeProfilePhoto()
            }.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false)
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

data class ViewProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)