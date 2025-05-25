package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.common.model.AppUiProperties
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
    val uiState: StateFlow<MainActivityUiState> = preferencesStore.appUiProperties
        .map { MainActivityUiState.Success(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = MainActivityUiState.Loading
        )
}

sealed interface MainActivityUiState {
    data object Loading: MainActivityUiState
    data class Success(val uiProperties: AppUiProperties): MainActivityUiState
}