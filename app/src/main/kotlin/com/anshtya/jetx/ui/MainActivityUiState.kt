package com.anshtya.jetx.ui

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState

    data class Success(
        val authenticated: Boolean,
        val showMainGraph: Boolean,
        val darkTheme: Boolean
    ) : MainActivityUiState
}