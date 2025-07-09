package com.anshtya.jetx.shared.auth

sealed interface AuthStatus {
    data object Loading: AuthStatus
    data class Success(
        val authenticated: Boolean,
        val profileCreated: Boolean
    ): AuthStatus
}