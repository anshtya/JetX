package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthRequest
import com.anshtya.jetx.common.Result
import com.anshtya.jetx.common.safeResult
import com.anshtya.jetx.network.service.AuthService
import javax.inject.Inject

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