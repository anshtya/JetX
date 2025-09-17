package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeAuthRepository : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    var shouldFailLogin = false
    var shouldFailRegister = false
    var shouldFailCheckUser = false
    var shouldUserExist = false
    var shouldFailLogout = false

    override suspend fun login(phoneNumber: String, pin: String): Result<Unit> {
        return if (shouldFailLogin) {
            Result.failure(Exception("Login failed"))
        } else {
            _authState.update { AuthState.Authenticated("token") }
            Result.success(Unit)
        }
    }

    override suspend fun register(phoneNumber: String, pin: String): Result<Unit> {
        return if (shouldFailRegister) {
            Result.failure(Exception("Registration failed"))
        } else {
            _authState.update { AuthState.Authenticated("token") }
            Result.success(Unit)
        }
    }

    override suspend fun checkUser(number: Long, countryCode: Int): Result<Boolean> {
        return if (shouldFailCheckUser) {
            Result.failure(Exception("Check user failed"))
        } else {
            Result.success(shouldUserExist)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return if (shouldFailLogout) {
            Result.failure(Exception("Logout failed"))
        } else {
            _authState.update { AuthState.Unauthenticated }
            Result.success(Unit)
        }
    }
}