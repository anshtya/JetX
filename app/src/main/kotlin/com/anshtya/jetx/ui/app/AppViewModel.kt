package com.anshtya.jetx.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authRepository: AuthRepository,
    preferencesStore: PreferencesStore
) : ViewModel() {
    private val _navState = MutableStateFlow<AppNavState>(AppNavState.Initialising)
    val navState = _navState.asStateFlow()

    init {
        combine(
            authRepository.authState,
            preferencesStore.userState
        ) { authState, userState ->
            UserData(authState, userState)
        }.distinctUntilChanged()
            .onEach(::handleUserData)
            .launchIn(viewModelScope)
    }

    private fun handleUserData(userData: UserData) {
        val authState = userData.authState
        val userState = userData.userState

        val updatedNavState = when(authState) {
            is AuthState.Authenticated -> {
                userState.profileCreated?.let { profileCreated ->
                    if (profileCreated) {
                        AppNavState.Authenticated
                    } else {
                        AppNavState.CreateProfile
                    }
                } ?: _navState.value
            }
            is AuthState.Unauthenticated -> AppNavState.Unauthenticated
            is AuthState.RefreshError -> {
                if (authState.sessionExists) {
                    AppNavState.Authenticated
                } else {
                    AppNavState.Unauthenticated
                }
            }
            else -> AppNavState.Initialising
        }

        _navState.update { updatedNavState }
    }
}

sealed interface AppNavState {
    data object Initialising : AppNavState
    data object Authenticated : AppNavState
    data object Unauthenticated : AppNavState
    data object CreateProfile : AppNavState
}

data class UserData(
    val authState: AuthState,
    val userState: UserState
)