package com.anshtya.jetx.profile.data

import android.net.Uri
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.core.network.model.response.CheckUsernameResponse
import com.anshtya.jetx.core.network.model.response.UserProfileSearchItem
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Repository interface for managing user profiles.
 *
 * Provides methods for creating, updating, and retrieving user profile data,
 * as well as checking username availability and managing profile photos.
 */
interface ProfileRepository {

    /**
     * Creates a new user profile with the specified details.
     *
     * @param name The display name of the user.
     * @param username The unique username chosen by the user.
     * @param photo An optional [Uri] representing the user's profile photo.
     * @return A [Result] containing [Unit] if the profile creation succeeded,
     * or an error if the operation failed.
     */
    suspend fun createProfile(
        name: String,
        username: String,
        photo: Uri?
    ): Result<Unit>

    /**
     * Checks if the given username is available for registration.
     *
     * @param username The username to check for availability.
     * @return A [Result] containing a [CheckUsernameResponse] that indicates
     * whether the username is available or already taken.
     */
    suspend fun checkUsername(
        username: String
    ): Result<CheckUsernameResponse>

    /**
     * Fetches a user profile from the remote source and saves it locally.
     *
     * This is typically used for refreshing the profile data stored on the device.
     *
     * @param userId The unique identifier of the user.
     * @return A [Result] containing [Unit] if the operation succeeded,
     * or an error if the fetch or save process failed.
     */
    suspend fun fetchAndSaveProfile(
        userId: UUID
    ): Result<Unit>

    /**
     * Retrieves the user profile for the specified [userId].
     *
     * This method is typically used for one-time data access.
     *
     * @param userId The unique identifier of the user.
     * @return The [UserProfile] associated with the given [userId].
     * @throws Exception if the profile could not be retrieved.
     */
    suspend fun getProfile(
        userId: UUID
    ): UserProfile

    /**
     * Observes changes to a user's profile as a [Flow].
     *
     * This stream emits updates whenever the profile data changes locally.
     *
     * @param userId The unique identifier of the user.
     * @return A [Flow] emitting [UserProfile] updates, or `null` if not found.
     */
    fun getProfileFlow(
        userId: UUID
    ): Flow<UserProfile?>

    /**
     * Searches for user profiles that match the given query string.
     *
     * The query can represent a partial match for a username or display name.
     *
     * @param query The search term used to filter profiles.
     * @return A [Result] containing a list of [UserProfile] objects matching the query,
     * or an error if the search failed.
     */
    suspend fun searchProfiles(
        query: String
    ): Result<List<UserProfileSearchItem>>

    /**
     * Updates the display name of the current user.
     *
     * @param name The new name to be set for the user's profile.
     * @return A [Result] containing [Unit] if the update succeeded,
     * or an error if the operation failed.
     */
    suspend fun updateName(
        name: String
    ): Result<Unit>

    /**
     * Updates the username of the current user.
     *
     * @param username The new username to be set for the user's profile.
     * @return A [Result] containing [Unit] if the update succeeded,
     * or an error if the operation failed.
     */
    suspend fun updateUsername(
        username: String
    ): Result<Unit>

    /**
     * Updates the user's profile photo.
     *
     * @param photo A [Uri] representing the new profile picture.
     * @return A [Result] containing [Unit] if the update succeeded,
     * or an error if the operation failed.
     */
    suspend fun updateProfilePhoto(
        photo: Uri
    ): Result<Unit>

    /**
     * Removes the user's current profile photo.
     *
     * @return A [Result] containing [Unit] if the photo was successfully removed,
     * or an error if the operation failed.
     */
    suspend fun removeProfilePhoto(): Result<Unit>
}