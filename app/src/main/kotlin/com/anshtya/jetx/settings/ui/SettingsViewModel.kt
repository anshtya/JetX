package com.anshtya.jetx.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.SettingsValues.THEME
import com.anshtya.jetx.settings.data.SettingsRepository
import com.anshtya.jetx.settings.data.model.ThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    preferencesStore: PreferencesStore,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val userSettings: StateFlow<UserSettings?> = preferencesStore
        .getStringFlow(THEME)
        .map {
            UserSettings(
                theme = enumValueOf<ThemeOption>(it ?: ThemeOption.SYSTEM_DEFAULT.name)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun changeTheme(option: ThemeOption) {
        viewModelScope.launch {
            settingsRepository.setTheme(option)
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}