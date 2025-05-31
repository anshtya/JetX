package com.anshtya.jetx.auth.data.model

sealed interface AuthStatus {
    data object Loading: AuthStatus
    data class Success(
        val authenticated: Boolean,
        val profileCreated: Boolean
    ): AuthStatus
}