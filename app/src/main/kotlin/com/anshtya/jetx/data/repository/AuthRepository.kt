package com.anshtya.jetx.data.repository

import android.util.Log
import com.anshtya.jetx.data.datastore.TokenManager
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.network.ApiService
import com.anshtya.jetx.data.network.model.auth.AuthRequest
import com.anshtya.jetx.util.onNetworkResponse
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
        val authRequest = AuthRequest(username = username, password = password)
        val response = apiService.login(authRequest)

        return response.onNetworkResponse(
            onSuccess = { body ->
                Log.d("foo", body!!.token)
//                tokenManager.saveToken(body!!.token)
            }
        ) { _ -> }
    }

    override suspend fun signup(
        username: String,
        password: String
    ): Result<Unit> {
        val authRequest = AuthRequest(username = username, password = password)
        val response = apiService.signup(authRequest)

        return response.onNetworkResponse(
            onSuccess = { body ->
                Log.d("foo", body!!.token)
//                tokenManager.saveToken(body!!.token)
            }
        ) { _ -> }
    }
}