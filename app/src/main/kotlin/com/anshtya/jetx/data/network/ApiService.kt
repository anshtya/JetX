package com.anshtya.jetx.data.network

import com.anshtya.jetx.data.model.auth.AuthRequest
import com.anshtya.jetx.data.model.auth.AuthResponse
import com.anshtya.jetx.util.Constants
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    @Headers(Constants.CONTENT_TYPE_JSON)
    suspend fun login(
        @Body authRequest: AuthRequest
    ): AuthResponse

    @POST("auth/signup")
    @Headers(Constants.CONTENT_TYPE_JSON)
    suspend fun signup(
        @Body authRequest: AuthRequest
    ): AuthResponse
}