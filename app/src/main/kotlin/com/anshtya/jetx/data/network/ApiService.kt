package com.anshtya.jetx.data.network

import com.anshtya.jetx.data.network.model.auth.AuthRequest
import com.anshtya.jetx.data.network.model.auth.AuthResponse
import com.anshtya.jetx.util.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    @Headers(Constants.CONTENT_TYPE_JSON)
    suspend fun login(
        @Body authRequest: AuthRequest
    ): Response<AuthResponse>

    @POST("auth/signup")
    @Headers(Constants.CONTENT_TYPE_JSON)
    suspend fun signup(
        @Body authRequest: AuthRequest
    ): Response<AuthResponse>
}