package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.common.model.ThemeOption
import com.anshtya.jetx.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    preferencesStore: PreferencesStore
) : ViewModel() {
    val state: StateFlow<MainActivityState> = preferencesStore.appUiProperties
        .map { uiProperties ->
            MainActivityState(theme = uiProperties.theme)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainActivityState()
        )
}

data class MainActivityState(
    val theme: ThemeOption = ThemeOption.SYSTEM_DEFAULT
)