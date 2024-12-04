package com.anshtya.jetx.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anshtya.jetx.auth.data.AuthTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authTokenManager: AuthTokenManager
) : ViewModel() {
    private val showMainGraph = flow { emit(authTokenManager.authenticated.first()) }

    val uiState: StateFlow<MainActivityUiState> = combine(
        flow {
            // fetch from repository
            emit(true)
        },
        authTokenManager.authenticated,
        showMainGraph
    ) { t, authenticated, showMainGraph ->
        MainActivityUiState.Success(
            authenticated = authenticated,
            showMainGraph = showMainGraph,
            darkTheme = t
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = MainActivityUiState.Loading
    )
}