package com.anshtya.jetx.core.network.service

import com.anshtya.jetx.core.network.api.AuthApi
import com.anshtya.jetx.core.network.model.NetworkResult
import com.anshtya.jetx.core.network.model.body.CheckUserBody
import com.anshtya.jetx.core.network.model.body.LogoutBody
import com.anshtya.jetx.core.network.model.body.RefreshTokenBody
import com.anshtya.jetx.core.network.model.body.UserCredentialsBody
import com.anshtya.jetx.core.network.model.response.AuthTokenResponse
import com.anshtya.jetx.core.network.model.response.CheckUserResponse
import com.anshtya.jetx.core.network.util.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun register(
        phoneNumber: String,
        pin: String
    ): NetworkResult<AuthTokenResponse> {
        return safeApiCall {
            authApi.register(
                UserCredentialsBody(
                    phoneNumber = phoneNumber,
                    pin = pin
                )
            )
        }
    }

    suspend fun login(
        phoneNumber: String,
        pin: String,
        fcmToken: String
    ): NetworkResult<AuthTokenResponse> {
        return safeApiCall {
            authApi.login(
                UserCredentialsBody(
                    phoneNumber = phoneNumber,
                    pin = pin,
                    fcmToken = fcmToken
                )
            )
        }
    }

    suspend fun checkUser(
        phoneNumber: String
    ): NetworkResult<CheckUserResponse> {
        return safeApiCall { authApi.checkUser(CheckUserBody(phoneNumber)) }
    }

    suspend fun refreshToken(
        refreshToken: String
    ): NetworkResult<AuthTokenResponse> {
        return safeApiCall { authApi.refreshToken(RefreshTokenBody(refreshToken)) }
    }

    suspend fun logoutUser(
        token: String
    ): NetworkResult<Unit> {
        return safeApiCall { authApi.logout(LogoutBody(token)) }
    }
}