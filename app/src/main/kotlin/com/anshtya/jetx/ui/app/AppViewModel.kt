package com.anshtya.jetx.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.model.UserState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    authManager: AuthManager,
    preferencesStore: PreferencesStore
) : ViewModel() {
    private val _navState = MutableStateFlow<AppNavState>(AppNavState.Initialising)
    val navState = _navState.asStateFlow()

    init {
        combine(
            authManager.authState,
            preferencesStore.userState
        ) { authState, userState ->
            handleUserData(authState, userState)
        }.launchIn(viewModelScope)
    }

    private fun handleUserData(
        authState: AuthState,
        userState: UserState
    ) {
        val updatedNavState = when (authState) {
            is AuthState.Authenticated -> {
                if (userState.profileCreated) AppNavState.Authenticated
                else AppNavState.CreateProfile
            }

            is AuthState.Unauthenticated -> {
                if (userState.onboardingCompleted) AppNavState.Unauthenticated
                else AppNavState.Onboarding
            }

            else -> AppNavState.Initialising
        }

        _navState.update { updatedNavState }
    }
}

sealed interface AppNavState {
    data object Initialising : AppNavState
    data object Onboarding : AppNavState
    data object Authenticated : AppNavState
    data object Unauthenticated : AppNavState
    data object CreateProfile : AppNavState
}