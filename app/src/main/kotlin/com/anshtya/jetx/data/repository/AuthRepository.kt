package com.anshtya.jetx.data.repository

import com.anshtya.jetx.data.datastore.AuthTokenManager
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.model.auth.AuthRequest
import com.anshtya.jetx.data.network.service.AuthService
import com.anshtya.jetx.util.safeResult
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String
    ): Result<Unit>

    suspend fun signup(
        username: String,
        password: String
    ): Result<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authTokenManager: AuthTokenManager
) : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): Result<Unit> {
        return safeResult {
            val authRequest = AuthRequest(username = username, password = password)
            val response = authService.login(authRequest)
            authTokenManager.saveToken(response.accessToken, response.refreshToken)
        }
    }

    override suspend fun signup(
        username: String,
        password: String
    ): Result<Unit> {
        return safeResult {
            val authRequest = AuthRequest(username = username, password = password)
            val response = authService.signup(authRequest)
            authTokenManager.saveToken(response.accessToken, response.refreshToken)
        }
    }
}