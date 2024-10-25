package com.anshtya.jetx.util

import com.anshtya.jetx.data.model.Result
import com.anshtya.jetx.data.model.ErrorResponse
import com.squareup.moshi.Moshi
import retrofit2.Response

suspend fun <U, V> Response<U>.onNetworkResponse(
    onSuccess: suspend (U?) -> Unit,
    transform: (U?) -> V
): Result<V> {
    return if (isSuccessful) {
        val body = body()
        onSuccess(body)

        Result.Success(transform(body))
    } else {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(ErrorResponse::class.java).lenient()
        val errorMessage = errorBody()?.source()?.let {
            adapter.fromJson(it)?.message
        } ?: "An error occurred"

        Result.Error(errorMessage)
    }
}