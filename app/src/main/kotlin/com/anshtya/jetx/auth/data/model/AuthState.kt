package com.anshtya.jetx.auth.data.model

import java.util.UUID

sealed class AuthState {
    data object Initializing : AuthState()

    data class Authenticated(
        val userId: UUID,
        val accessToken: String
    ) : AuthState()

    data object Unauthenticated : AuthState()

    fun currentUserIdOrNull(): UUID? {
        return if (this is Authenticated) userId else null
    }

    fun currentAccessTokenOrNull(): String? {
        return if (this is Authenticated) accessToken else null
    }
}