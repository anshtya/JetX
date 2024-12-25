package com.anshtya.jetx.auth.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.auth.data.model.NetworkProfile
import com.anshtya.jetx.auth.data.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val authDatastore: AuthDatastore
) : AuthRepository {
    private val auth = client.auth
    private val postgrest = client.postgrest
    private val storage = client.storage

    override val authStatus: Flow<AuthStatus> = authDatastore.authStatus

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> {
        return kotlin.runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            authDatastore.onSignIn()
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit> {
        return kotlin.runCatching {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            authDatastore.setAuthCompleted(true)
        }
    }

    override suspend fun createProfile(profile: Profile): Result<Unit> {
        return kotlin.runCatching {
            var profilePicturePath: String? = null

            if (profile.profilePicture != null) {
                val mediaBucket = storage.from("media")
                val imageBitmap = profile.profilePicture.asAndroidBitmap()
                val outputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val imageByteArray = outputStream.toByteArray()

                val uploadResponse = mediaBucket.upload(
                    path = "profile-${profile.username}.png",
                    data = imageByteArray
                ) {
                    upsert = true
                }
                profilePicturePath = uploadResponse.key
            }

            val profiles = postgrest.from("profile")
            profiles.insert(
                value = NetworkProfile(
                    name = profile.name,
                    username = profile.username,
                    profilePictureUrl = profilePicturePath
                )
            )

            authDatastore.setProfileCreated(true)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return kotlin.runCatching {
            auth.signOut()
            authDatastore.onSignOut()
        }
    }
}