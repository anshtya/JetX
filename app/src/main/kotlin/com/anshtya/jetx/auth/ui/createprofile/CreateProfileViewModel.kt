package com.anshtya.jetx.auth.ui.createprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthDatastore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val authDatastore: AuthDatastore
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onContinueClick() {
        viewModelScope.launch {
            authDatastore.setProfileCreated(true)
            _uiState.update {
                it.copy(profileCreated = true)
            }
        }
    }
}

data class CreateProfileUiState(
    val profileCreated: Boolean = false
)