package com.anshtya.jetx.core.network.util

import com.anshtya.jetx.core.network.model.ErrorResult
import com.anshtya.jetx.core.network.model.NetworkResult
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.coroutines.cancellation.CancellationException

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
        val body = response.body()

        when {
            response.isSuccessful && body != null -> NetworkResult.Success(body)
            response.isSuccessful && response.code() == 204 -> {
                @Suppress("UNCHECKED_CAST")
                NetworkResult.Success(Unit as T)
            }
            else -> NetworkResult.Failure.HttpError(
                code = response.code(),
                errorMessage = parseErrorMessage(response.errorBody())
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        NetworkResult.Failure.Unknown(e)
    }
}

private val json = Json { ignoreUnknownKeys = true }

private fun parseErrorMessage(errorBody: ResponseBody?): String {
    val message = errorBody?.let {
        runCatching {
            json.decodeFromString<ErrorResult>(it.string()).message
        }.getOrNull()
    }
    return message ?: "An unknown error occurred"
}