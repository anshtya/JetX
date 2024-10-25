package com.anshtya.jetx.data.repository

import android.util.Log
import com.anshtya.jetx.data.datastore.TokenManager
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.network.ApiService
import com.anshtya.jetx.data.model.auth.AuthRequest
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
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    override suspend fun login(
        username: String,
        password: String
    ): Result<Unit> {
        return safeResult {
            val authRequest = AuthRequest(username = username, password = password)
            val response = apiService.login(authRequest)
            Log.d("foo", response.token)
//            tokenManager.saveToken(body!!.token)
        }
    }

    override suspend fun signup(
        username: String,
        password: String
    ): Result<Unit> {
        return safeResult {
            val authRequest = AuthRequest(username = username, password = password)
            val response = apiService.signup(authRequest)
            Log.d("foo", response.token)
//            tokenManager.saveToken(body!!.token)
        }
    }
}