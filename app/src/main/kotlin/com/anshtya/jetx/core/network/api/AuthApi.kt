package com.anshtya.jetx.core.network.api

import com.anshtya.jetx.core.network.model.body.CheckUserBody
import com.anshtya.jetx.core.network.model.body.LogoutBody
import com.anshtya.jetx.core.network.model.body.RefreshTokenBody
import com.anshtya.jetx.core.network.model.body.UserCredentialsBody
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.model.response.CheckUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/register")
    suspend fun register(
        @Body body: UserCredentialsBody
    ): Response<AuthTokenResponse>

    @POST("auth/login")
    suspend fun login(
        @Body body: UserCredentialsBody
    ): Response<AuthTokenResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body body: RefreshTokenBody
    ): Response<AuthTokenResponse>

    @POST("auth/check")
    suspend fun checkUser(
        @Body body: CheckUserBody
    ): Response<CheckUserResponse>

    @POST("auth/logout")
    suspend fun logout(
        @Body body: LogoutBody
    ): Response<Unit>
}