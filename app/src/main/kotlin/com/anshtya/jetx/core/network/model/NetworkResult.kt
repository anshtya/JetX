package com.anshtya.jetx.core.network.model

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

    sealed class Failure : NetworkResult<Nothing>() {
        data class HttpError(
            val code: Int,
            val errorMessage: String
        ) : Failure()

        data class Unknown(val exception: Exception) : Failure()
    }
}