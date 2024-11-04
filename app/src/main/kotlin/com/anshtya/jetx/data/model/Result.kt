package com.anshtya.jetx.data.model

sealed interface Result<out T> {
    data class Success<T>(val data: T): Result<T>

    data class Error(
        val statusCode: Int?,
        val errorMessage: String?
    ): Result<Nothing>
}