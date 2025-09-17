package com.anshtya.jetx.core.network.util

import com.anshtya.jetx.core.network.model.NetworkResult
import retrofit2.HttpException
import retrofit2.Response

/**
 * Executes a Retrofit API call safely and wraps the result in a [NetworkResult].
 *
 * This function handles:
 * 1. Successful responses by returning [NetworkResult.Success] with the response body.
 * 2. HTTP and client errors by returning [NetworkResult.Failure] with the appropriate error message.
 *
 * @param T The type of the expected response body.
 * @param apiCall A suspend lambda that performs the Retrofit API call returning [Response<T>].
 * @return A [NetworkResult] representing either success with the response body or an error/exception.
 *
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            // Check for no content response
            if (response.code() == 204) {
                @Suppress("UNCHECKED_CAST")
                NetworkResult.Success(Unit as T)
            } else {
                response.body()?.let { NetworkResult.Success(it) }
                    ?: NetworkResult.Failure.OtherError(
                        IllegalStateException("Response body is null")
                    )
            }
        } else {
            NetworkResult.Failure.HttpError(HttpException(response))
        }
    } catch (e: Exception) {
        NetworkResult.Failure.OtherError(e)
    }
}