package com.anshtya.jetx.core.network.auth

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test

class AuthAuthenticatorTest {
    private val authTokenProvider: AuthTokenProvider = mockk()
    private lateinit var authAuthenticator: AuthAuthenticator

    private val response401 = Response.Builder()
        .code(401)
        .request(
            Request.Builder()
                .url("http://www.test.com")
                .build()
        )
        .protocol(Protocol.HTTP_2)
        .message("Unauthenticated")
        .build()

    @Before
    fun setup() {
        authAuthenticator = AuthAuthenticator(authTokenProvider)
    }

    @Test
    fun `returns null if no refresh token present`() {
        every { authTokenProvider.getStoredToken(any()) } returns null

        assertNull(authAuthenticator.authenticate(null, response401))
    }

    @Test
    fun `returns null if no refresh fails`() {
        val refreshToken = "refresh"
        every { authTokenProvider.getStoredToken(any()) } returns refreshToken
        every { authTokenProvider.getNewToken(refreshToken) } returns Result.failure(Exception(""))

        assertNull(authAuthenticator.authenticate(null, response401))
    }
}

