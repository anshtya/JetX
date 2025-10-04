package com.anshtya.jetx.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.model.ThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    private val _errorMessage = Channel<String>(Channel.BUFFERED)
    val errorMessage = _errorMessage.receiveAsFlow()

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