package com.anshtya.jetx.profile

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.AuthValues
import com.anshtya.jetx.profile.model.NetworkProfile
import com.anshtya.jetx.profile.model.Profile
import com.anshtya.jetx.profile.model.ProfileStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val preferencesStore: PreferencesStore
) : ProfileRepository {
    private val supabaseStorage = client.storage
    private val supabasePostgrest = client.postgrest
    private val supabaseAuth = client.auth

    override val profileStatus: Flow<ProfileStatus> = preferencesStore
        .getBooleanFlow(AuthValues.PROFILE_CREATED)
        .distinctUntilChanged()
        .map { status ->
            status?.let { created ->
                if (created) ProfileStatus.CREATED else ProfileStatus.NOT_CREATED
            } ?: ProfileStatus.NOT_CREATED
        }

    override suspend fun createProfile(profile: Profile): Result<Unit> {
        return kotlin.runCatching {
            var profilePicturePath: String? = null

            if (profile.profilePicture != null) {
                val imageByteArray = getByteArrayFromBitmap(
                    imageBitmap = profile.profilePicture.asAndroidBitmap()
                )
                val userId = supabaseAuth.retrieveUserForCurrentSession().id
                val mediaBucket = supabaseStorage.from("media")
                val path = "${userId}/profile-${profile.username}.png"
                mediaBucket.upload(
                    path = path,
                    data = imageByteArray
                )
                profilePicturePath = mediaBucket.publicUrl(path)
            }

            val profiles = supabasePostgrest.from("profile")
            profiles.insert(
                value = NetworkProfile(
                    name = profile.name,
                    username = profile.username,
                    profilePictureUrl = profilePicturePath
                )
            )
            preferencesStore.setBoolean(AuthValues.PROFILE_CREATED, true)
        }
    }

    override suspend fun getProfile(id: String): Result<Profile> {
        TODO("Not yet implemented")
    }

    private fun getByteArrayFromBitmap(imageBitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
}