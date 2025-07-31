package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authState: Flow<AuthState>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<Boolean>

    suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun signOut(): Result<Unit>
}