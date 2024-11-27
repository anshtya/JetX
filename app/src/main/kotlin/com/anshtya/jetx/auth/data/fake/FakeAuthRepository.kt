package com.anshtya.jetx.auth.data.fake

import com.anshtya.jetx.common.Result
import com.anshtya.jetx.auth.data.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class FakeAuthRepository @Inject constructor() : AuthRepository {
    override suspend fun login(username: String, password: String): Result<Unit> {
        delay(1000)
        return Result.Success(Unit)
    }

    override suspend fun signup(username: String, password: String): Result<Unit> {
        delay(1000)
        return Result.Success(Unit)
    }
}