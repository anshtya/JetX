package com.anshtya.jetx.network.service

import com.anshtya.jetx.auth.data.model.AuthRequest
import com.anshtya.jetx.auth.data.model.AuthResponse
import com.anshtya.jetx.network.NetworkUtils
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    @POST("auth/login")
    @Headers(NetworkUtils.CONTENT_TYPE_JSON)
    suspend fun login(
        @Body authRequest: AuthRequest
    ): AuthResponse

    @POST("auth/signup")
    @Headers(NetworkUtils.CONTENT_TYPE_JSON)
    suspend fun signup(
        @Body authRequest: AuthRequest
    ): AuthResponse
}