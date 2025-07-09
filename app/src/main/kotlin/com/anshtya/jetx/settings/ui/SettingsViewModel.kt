package com.anshtya.jetx.settings.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.shared.auth.AuthRepository
import com.anshtya.jetx.shared.model.ThemeOption
import com.anshtya.jetx.shared.preferences.PreferencesStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    var signedOut by mutableStateOf(false)
        private set

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
            authRepository.signOut()
            signedOut = true
        }
    }
}