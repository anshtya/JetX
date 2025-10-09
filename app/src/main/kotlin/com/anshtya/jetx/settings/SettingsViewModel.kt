package com.anshtya.jetx.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.model.ThemeOption
import com.anshtya.jetx.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SettingsViewModel @Inject constructor(
    authManager: AuthManager,
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

    val userProfile: StateFlow<UserProfile?> = authManager.authState
        .filter { it is AuthState.Authenticated }
        .flatMapLatest { authState ->
            profileRepository.getProfileFlow(authState.currentUserIdOrNull()!!)
        }
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val userSettings: StateFlow<UserSettings?> = preferencesStore
        .appUiProperties
        .map {
            UserSettings(theme = it.theme)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun changeTheme(option: ThemeOption) {
        viewModelScope.launch {
            preferencesStore.setTheme(option.name)
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            authRepository.logout().onFailure {
                _errorMessage.send("An error occurred")
            }
        }
    }
}

data class UserSettings(
    val theme: ThemeOption
)