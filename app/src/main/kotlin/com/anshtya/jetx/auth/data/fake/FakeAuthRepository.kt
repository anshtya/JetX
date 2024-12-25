package com.anshtya.jetx.auth.data.fake

import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.auth.data.model.Profile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {
    override val authStatus: Flow<AuthStatus>
        get() = flow { AuthStatus(false, false) }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override suspend fun signOut(): Result<Unit> {
        return Result.success(Unit)
    }

    override suspend fun createProfile(profile: Profile): Result<Unit> {
        return Result.success(Unit)
    }
}