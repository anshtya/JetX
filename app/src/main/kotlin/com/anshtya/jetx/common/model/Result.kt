package com.anshtya.jetx.common.model

sealed interface Result<out T> {
    data class Success<T>(val data: T): Result<T>
    data class Error<Nothing>(val errorMessage: String = "An error occurred"): Result<Nothing>
}