package com.anshtya.jetx.auth.data

import android.util.Log
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.core.network.service.AuthService
import com.anshtya.jetx.core.network.util.toResult
import com.anshtya.jetx.core.preferences.TokenStore
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthRepositoryImpl(
    private val authService: AuthService,
    private val tokenStore: TokenStore,
    private val logoutManager: LogoutManager,
    scope: CoroutineScope
) : AuthRepository {
    private val tag = this::class.simpleName

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState = _authState.asStateFlow()

    init {
        scope.launch { initialAuth() }
    }

    private suspend fun initialAuth() {
        val authToken = tokenStore.getAuthToken()
        if (authToken.accessToken == null || authToken.refreshToken == null) {
            _authState.update { AuthState.Unauthenticated }
            return
        }

        authService.refreshToken(authToken.refreshToken)
            .toResult()
            .onSuccess {
                tokenStore.storeAuthToken(access = it.accessToken, refresh = it.refreshToken)
            }.onFailure { throwable ->
                Log.e(tag, throwable.message, throwable)
            }
        val token = tokenStore.getAccessToken()!!
        _authState.update { AuthState.Authenticated(token) }
    }

    override suspend fun login(
        phoneNumber: String,
        pin: String
    ): Result<Unit> {
        return authService.login(phoneNumber, pin)
            .toResult()
            .onSuccess { authResponse ->
                tokenStore.storeAuthToken(
                    access = authResponse.accessToken,
                    refresh = authResponse.refreshToken
                )
                _authState.update { AuthState.Authenticated(authResponse.accessToken) }
            }
            .onFailure {
                Log.e(tag, it.message, it)
            }
            .map {}
    }

    override suspend fun register(
        phoneNumber: String,
        pin: String
    ): Result<Unit> {
        return authService.register(phoneNumber, pin)
            .toResult()
            .onSuccess { authResponse ->
                tokenStore.storeAuthToken(
                    access = authResponse.accessToken,
                    refresh = authResponse.refreshToken
                )
                _authState.update { AuthState.Authenticated(authResponse.accessToken) }
            }
            .onFailure {
                Log.e(tag, it.message, it)
            }
            .map {}
    }

    /**
     * Checks if a user exists for the given phone number and country code.
     *
     * Implementation detail: Validates the phone number format using [PhoneNumberUtil]
     * before performing a network call to check if the user exists.
     */
    override suspend fun checkUser(
        number: Long,
        countryCode: Int
    ): Result<Boolean> {
        val phone = PhoneNumberUtil.getInstance()

        val phoneNumber = Phonenumber.PhoneNumber()
            .setCountryCode(countryCode)
            .setNationalNumber(number)
        val validNumber = phone.isValidNumber(phoneNumber)
        if (!validNumber) {
            return Result.failure(Exception("Invalid phone number."))
        }

        return authService.checkUser(
            phoneNumber = phone.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164)
        )
            .toResult()
            .map { it.exists }
            .onFailure {
                Log.e(tag, it.message, it)
            }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            val currentState = _authState.value
            val token = when (currentState) {
                is AuthState.Authenticated -> currentState.token
                else -> {
                    // Already logged out, just ensure clean state
                    return Result.success(Unit)
                }
            }

            authService.logoutUser(token)
                .toResult()
                .recover {
                    Log.e(tag, it.message, it)
                    throw it
                }

            logoutManager.performLocalCleanup()
            _authState.update { AuthState.Unauthenticated }
        }
    }
}