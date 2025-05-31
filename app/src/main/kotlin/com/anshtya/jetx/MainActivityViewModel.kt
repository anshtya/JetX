package com.anshtya.jetx

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.common.model.AppUiProperties
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.preferences.PreferencesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository,
    preferencesStore: PreferencesStore
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesStore.appUiProperties,
        authRepository.authStatus
    ) { appUiProperties, authStatus ->
        if (authStatus is AuthStatus.Loading) MainActivityUiState.Loading
        else MainActivityUiState.Success(appUiProperties)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainActivityUiState.Loading
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val uiProperties: AppUiProperties) : MainActivityUiState
}