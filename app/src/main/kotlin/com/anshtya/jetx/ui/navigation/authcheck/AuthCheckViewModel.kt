package com.anshtya.jetx.ui.navigation.authcheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.ui.navigation.authcheck.AuthCheckUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AuthCheckViewModel @Inject constructor(
    authRepository: AuthRepository,
    preferencesStore: PreferencesStore
) : ViewModel() {
    val userState: StateFlow<AuthCheckUiState> = authRepository.authStatus
        .map {
            when (it) {
                AuthStatus.INITIALIZING -> AuthCheckUiState.Loading
                AuthStatus.AUTHORIZED -> {
                    UserState(
                        authenticated = true,
                        profileCreated = preferencesStore.profileFlow.first().profileCreated
                    ).toSuccessState()
                }
                AuthStatus.UNAUTHORIZED -> {
                    UserState(
                        authenticated = false,
                        profileCreated = false
                    ).toSuccessState()
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AuthCheckUiState.Loading
        )
}

sealed interface AuthCheckUiState {
    data object Loading: AuthCheckUiState
    data class Success(val state: UserState): AuthCheckUiState
}

data class UserState(
    val authenticated: Boolean,
    val profileCreated: Boolean
)  {
    fun toSuccessState() = Success(this)
}