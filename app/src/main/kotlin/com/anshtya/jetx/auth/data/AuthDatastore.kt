package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthStatus
import kotlinx.coroutines.flow.Flow

interface AuthDatastore {
    val authStatus: Flow<AuthStatus>

    suspend fun setAuthCompleted(value: Boolean)

    suspend fun setProfileCreated(value: Boolean)

    suspend fun onSignIn()

    suspend fun onSignOut()
}