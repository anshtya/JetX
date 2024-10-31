package com.anshtya.jetx.util

import com.anshtya.jetx.data.model.ErrorResponse
import com.anshtya.jetx.data.model.Result
import com.squareup.moshi.Moshi
import retrofit2.HttpException

suspend fun <T> safeResult(
    errorMessage: String = "An error occurred.",
    work: suspend () -> T
): Result<T> {
    return try {
        Result.Success(work())
    } catch(e: Exception) {
        when (e) {
            is HttpException -> {
                Result.Error(
                    statusCode = e.code(),
                    errorMessage = e.getErrorMessage()
                )
            }
            else -> {
                Result.Error(
                    statusCode = null,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

private fun HttpException.getErrorMessage(): String {
    val moshi = Moshi.Builder().build()
    val adapter = moshi.adapter(ErrorResponse::class.java).lenient()

    return response()?.errorBody()?.source()?.let {
        adapter.fromJson(it)?.message
    } ?: "Server error occurred."
}