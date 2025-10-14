package com.anshtya.jetx.auth.data

import android.util.Log
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.coroutine.DefaultScope
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.JetxPreferencesStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for managing sessions and auth state.
 *
 * This class is responsible for initializing, maintaining, and updating the
 * user's authentication session. It handles session persistence, token
 * management, and exposes the current [AuthState] as a reactive flow for
 * other layers (e.g., UI or repositories) to observe.
 */
@Singleton
class AuthManager @Inject constructor(
    private val authService: AuthService,
    private val store: JetxPreferencesStore,
    @DefaultScope private val scope: CoroutineScope
) {
    private val tag = this::class.simpleName

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState = _authState.asStateFlow()

    init {
        scope.launch { initialAuth() }
    }

    private suspend fun initialAuth() {
        val authToken = store.token.getAuthToken()
        if (authToken.accessToken == null || authToken.refreshToken == null) {
            _authState.update { AuthState.Unauthenticated }
            return
        }

        authService.refreshToken(authToken.refreshToken)
            .toResult()
            .onSuccess { response ->
                storeSession(
                    userId = response.userId,
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken
                )
            }.onFailure { throwable ->
                Log.e(tag, "Failed to refresh session, reusing old session", throwable)

                val userId = store.account.getUserId()!!
                _authState.update {
                    AuthState.Authenticated(
                        userId = UUID.fromString(userId),
                        accessToken = authToken.accessToken
                    )
                }
            }
    }

    suspend fun refreshSession(): Boolean {
        val refreshToken = store.token.getAuthToken().refreshToken ?: return false
        return authService.refreshToken(refreshToken)
            .toResult()
            .fold(
                onSuccess = { response ->
                    storeSession(
                        userId = response.userId,
                        accessToken = response.accessToken,
                        refreshToken = response.refreshToken
                    )
                    true
                },
                onFailure = { throwable ->
                    Log.e(tag, throwable.message, throwable)
                    false
                }
            )
    }

    suspend fun storeSession(
        userId: UUID,
        accessToken: String,
        refreshToken: String,
    ) {
        store.account.storeUserId(userId.toString())
        store.token.storeAuthToken(
            access = accessToken,
            refresh = refreshToken
        )

        _authState.update {
            AuthState.Authenticated(
                userId = userId,
                accessToken = accessToken
            )
        }
    }

    suspend fun deleteSession() {
        store.token.clear()
        store.account.clear()
        _authState.update { AuthState.Unauthenticated }
    }
}