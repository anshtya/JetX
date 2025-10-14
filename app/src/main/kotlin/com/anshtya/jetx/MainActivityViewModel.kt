package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import com.anshtya.jetx.core.preferences.model.ThemeOption
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    store: JetxPreferencesStore
) : ViewModel() {
    val state: StateFlow<MainActivityState> = store.user.appUiProperties
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