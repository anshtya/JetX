package com.anshtya.jetx.core.network.auth

import com.anshtya.jetx.auth.data.AuthManager
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test

class AuthAuthenticatorTest {
    private val authManager: AuthManager = mockk()
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
        authAuthenticator = AuthAuthenticator(authManager)
    }

    @Test
    fun `returns null if refresh fails`() {
        coEvery { authManager.refreshSession() } returns false

        assertNull(authAuthenticator.authenticate(null, response401))
    }
}

