package com.anshtya.jetx.core.network.model

import kotlinx.serialization.json.Json
import retrofit2.HttpException

/**
 * Represents the result of a network or API operation in a safe and consistent way.
 *
 * This sealed class encapsulates three possible outcomes of an API call:
 * - [Success]: The call was successful, and the expected data is available.
 * - [Failure]: The call failed due to an HTTP or other error.
 *
 * @param T The type of data expected in the successful response.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    sealed class Failure() : NetworkResult<Nothing>() {
        abstract val exception: Exception

        data class HttpError(override val exception: HttpException) : Failure() {
            private val json = Json { ignoreUnknownKeys = true }
            fun code(): Int = exception.code()
            fun errorMessage(): String = exception.response()?.errorBody()?.let {
                json.decodeFromString<ErrorResult>(it.string()).message
            } ?: "An unknown error occurred"
        }

        data class OtherError(override val exception: Exception) : Failure()
    }
}