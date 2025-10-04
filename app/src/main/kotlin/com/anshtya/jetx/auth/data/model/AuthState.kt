package com.anshtya.jetx.auth.data.model

sealed class AuthState {
    data object Initializing : AuthState()

    data class Authenticated(
        val userId: String
    ) : AuthState()

    data object Unauthenticated : AuthState()

    fun currentUserIdOrNull(): String? {
        return if (this is Authenticated) userId else null
    }
}