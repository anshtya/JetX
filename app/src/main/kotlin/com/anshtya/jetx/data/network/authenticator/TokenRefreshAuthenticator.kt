package com.anshtya.jetx.data.network.authenticator

import com.anshtya.jetx.data.datastore.AuthTokenManager
import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.network.service.RefreshTokenService
import com.anshtya.jetx.util.safeResult
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenRefreshAuthenticator  @Inject constructor(
    private val refreshTokenService: RefreshTokenService,
    private val authTokenManager: AuthTokenManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // TODO: restrict from multi-access from threads
        val tokenResult = runBlocking { getNewAccessToken() }

        return when(tokenResult) {
            is Result.Success -> {
                val accessToken = tokenResult.data
                response.request.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            }

            is Result.Error -> {
                runBlocking { authTokenManager.deleteToken() }
                null
            }
        }
    }

    private suspend fun getNewAccessToken(): Result<String> {
        return safeResult {
            val refreshToken = authTokenManager.getRefreshToken()!!
            val response = refreshTokenService.refresh("Bearer $refreshToken")
            authTokenManager.saveToken(response.accessToken, response.refreshToken)

            response.accessToken
        }
    }
}