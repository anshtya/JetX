package com.anshtya.jetx.ui.navigation.authcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.shared.auth.AuthRepository
import com.anshtya.jetx.shared.auth.AuthStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class AuthCheckViewModel(
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