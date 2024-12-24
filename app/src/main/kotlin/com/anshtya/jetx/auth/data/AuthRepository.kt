package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authStatus: Flow<AuthStatus>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signOut(): Result<Unit>
}