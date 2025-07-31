package com.anshtya.jetx.auth.data.model

sealed interface AuthState {
    data object Initializing : AuthState
    data object Authenticated : AuthState
    data object Unauthenticated: AuthState
    data class RefreshError(val sessionExists: Boolean): AuthState
}