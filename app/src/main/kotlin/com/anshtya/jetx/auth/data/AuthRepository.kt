package com.anshtya.jetx.auth.data

import com.anshtya.jetx.common.Result

interface AuthRepository {
    suspend fun login(
        username: String,
        password: String
    ): Result<Unit>

    suspend fun signup(
        username: String,
        password: String
    ): Result<Unit>
}