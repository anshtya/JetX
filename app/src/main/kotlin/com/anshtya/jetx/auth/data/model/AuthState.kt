package com.anshtya.jetx.auth.data.model

sealed interface AuthState {
    data object Initializing : AuthState
    data class Authenticated(
        val token: String
    ) : AuthState
    data object Unauthenticated: AuthState
}