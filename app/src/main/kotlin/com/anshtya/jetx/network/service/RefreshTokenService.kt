package com.anshtya.jetx.network.service

import com.anshtya.jetx.auth.data.model.AuthResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface RefreshTokenService {
    @GET("auth/refresh")
    suspend fun refresh(
        @Header("Authorization") token: String
    ): AuthResponse
}