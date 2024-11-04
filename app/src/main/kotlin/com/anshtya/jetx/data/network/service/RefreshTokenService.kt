package com.anshtya.jetx.data.network.service

import com.anshtya.jetx.data.model.auth.AuthResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface RefreshTokenService {
    @GET("auth/refresh")
    suspend fun refresh(
        @Header("Authorization") token: String
    ): AuthResponse
}