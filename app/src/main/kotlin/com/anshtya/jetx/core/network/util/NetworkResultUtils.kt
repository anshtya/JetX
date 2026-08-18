package com.anshtya.jetx.core.network.util

import com.anshtya.jetx.core.network.model.NetworkResult

/**
 * Converts a [NetworkResult] into a standard [Result] type.
 *
 * This is typically used by repositories to consume network results
 * in a consistent way, converting successes and failures into
 * Kotlin's [Result] wrapper.
 *
 * @receiver [NetworkResult] The network operation result to convert.
 * @return A [Result] containing the successful value or an [Exception] for failures.
 */
fun <T> NetworkResult<T>.toResult(): Result<T> {
    return when (this) {
        is NetworkResult.Success -> Result.success(this.data)
        is NetworkResult.Failure -> Result.failure(this.toError())
    }
}

/**
 * Converts a [NetworkResult.Failure] into an [Exception] with a descriptive message.
 *
 * This preserves the original stack trace while generating an
 * appropriate message for easier debugging and repository-level handling.
 *
 * @receiver [NetworkResult.Failure] The failure result to convert.
 * @return An [Exception] containing the generated error message and original stack trace.
 */
private fun NetworkResult.Failure.toError(): Exception {
    return when (this) {
        is NetworkResult.Failure.HttpError -> {
            val message = if (code == 500) "Something went wrong" else errorMessage
            Exception(message)
        }
        is NetworkResult.Failure.Unknown -> Exception("Can't connect to server", exception)
    }
}