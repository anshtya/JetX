package com.anshtya.jetx.core.preferences.store

import android.content.SharedPreferences
import com.anshtya.jetx.core.preferences.model.AuthToken
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TokenStoreTest {
    private val preferences: SharedPreferences = mockk()
    private val tokenStore = TokenStore(preferences)

    private fun stored(access: String?, refresh: String?) {
        every { preferences.getString(TokenStore.ACCESS_TOKEN, null) } returns access
        every { preferences.getString(TokenStore.REFRESH_TOKEN, null) } returns refresh
    }

    @Test
    fun `getAuthToken returns token when both values are stored`() {
        stored(access = "access", refresh = "refresh")

        assertEquals(AuthToken("access", "refresh"), tokenStore.getAuthToken())
    }

    @Test
    fun `getAuthToken returns null when nothing is stored`() {
        stored(access = null, refresh = null)

        assertNull(tokenStore.getAuthToken())
    }

    @Test
    fun `getAuthToken returns null when only access token is stored`() {
        stored(access = "access", refresh = null)

        assertNull(tokenStore.getAuthToken())
    }

    @Test
    fun `getAuthToken returns null when only refresh token is stored`() {
        stored(access = null, refresh = "refresh")

        assertNull(tokenStore.getAuthToken())
    }
}
