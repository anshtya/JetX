package com.anshtya.jetx.ui.navigation.authcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthCheckViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val userState: StateFlow<AuthCheckUiState> = authRepository.authStatus
        .map {
            when (it) {
                AuthStatus.Loading -> AuthCheckUiState.Loading
                is AuthStatus.Success -> AuthCheckUiState.Success(
                    authenticated = it.authenticated,
                    profileCreated = it.profileCreated
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AuthCheckUiState.Loading
        )
}

sealed interface AuthCheckUiState {
    data object Loading : AuthCheckUiState
    data class Success(
        val authenticated: Boolean,
        val profileCreated: Boolean
    ) : AuthCheckUiState
}