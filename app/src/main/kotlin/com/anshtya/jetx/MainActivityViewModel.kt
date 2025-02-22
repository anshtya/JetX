package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.SettingsValues.THEME
import com.anshtya.jetx.settings.data.model.ThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val preferencesStore: PreferencesStore
) : ViewModel() {
    private val _state = MutableStateFlow(MainActivityState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            preferencesStore.getStringFlow(THEME)
                .map { it?.let { enumValueOf<ThemeOption>(it) } ?: ThemeOption.SYSTEM_DEFAULT }
                .collect { theme ->
                    _state.update {
                        it.copy(themeOption = theme)
                    }
                }
        }
    }
}

data class MainActivityState(
    val themeOption: ThemeOption? = null
)