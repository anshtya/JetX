package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for handling authentication-related operations.
 *
 * This defines the contract for login, registration, user validation,
 * and logout flows in the application.
 */
interface AuthRepository {

    /**
     * A [Flow] representing the current authentication state of the user.
     *
     * Collectors can observe this flow to react to login, logout, or session changes.
     */
    val authState: Flow<AuthState>

    /**
     * Attempts to log the user in with the provided phone number and PIN.
     *
     * @param phoneNumber The phone number used for authentication.
     * @param pin The PIN or password associated with the phone number.
     * @return A [Result] containing [Unit] if login was successful,
     * or an error if the login attempt failed.
     */
    suspend fun login(
        phoneNumber: String,
        pin: String
    ): Result<Unit>

    /**
     * Registers a new user with the given phone number and PIN.
     *
     * @param phoneNumber The phone number to register.
     * @param pin The PIN or password chosen by the user.
     * @return A [Result] containing [Unit] if registration was successful,
     * or an error if registration failed.
     */
    suspend fun register(
        phoneNumber: String,
        pin: String
    ): Result<Unit>

    /**
     * Checks if a user exists for the given phone number and country code.
     *
     * @param number The user's phone number (without country code).
     * @param countryCode The country code of the phone number.
     * @return A [Result] containing `true` if the user exists, `false` otherwise,
     * or an error if the check failed.
     */
    suspend fun checkUser(
        number: Long,
        countryCode: Int
    ): Result<Boolean>

    /**
     * Logs out the currently authenticated user.
     *
     * @return A [Result] containing [Unit] if logout was successful,
     * or an error if the logout attempt failed.
     */
    suspend fun logout(): Result<Unit>
}