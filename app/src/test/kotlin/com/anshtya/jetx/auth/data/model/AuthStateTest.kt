package com.anshtya.jetx.auth.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import java.util.UUID

class AuthStateTest {
    private val userId = UUID.randomUUID()
    private val authenticated = AuthState.Authenticated(userId = userId, accessToken = "token")

    @Test
    fun `requireUserId returns id when authenticated`() {
        assertEquals(userId, authenticated.requireUserId())
    }

    @Test
    fun `requireUserId throws when initializing`() {
        assertThrows(IllegalStateException::class.java) {
            AuthState.Initializing.requireUserId()
        }
    }

    @Test
    fun `requireUserId throws when unauthenticated`() {
        assertThrows(IllegalStateException::class.java) {
            AuthState.Unauthenticated.requireUserId()
        }
    }

    @Test
    fun `currentUserIdOrNull is null unless authenticated`() {
        assertEquals(userId, authenticated.currentUserIdOrNull())
        assertNull(AuthState.Initializing.currentUserIdOrNull())
        assertNull(AuthState.Unauthenticated.currentUserIdOrNull())
    }

    @Test
    fun `currentAccessTokenOrNull is null unless authenticated`() {
        assertEquals("token", authenticated.currentAccessTokenOrNull())
        assertNull(AuthState.Initializing.currentAccessTokenOrNull())
        assertNull(AuthState.Unauthenticated.currentAccessTokenOrNull())
    }
}
