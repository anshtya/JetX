package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.profile.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val profileRepository: ProfileRepository
) : AuthRepository {
    private val auth = client.auth

    override val authStatus: Flow<AuthStatus> = auth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthStatus.INITIALIZING
                is SessionStatus.Authenticated -> AuthStatus.AUTHORIZED
                else -> AuthStatus.UNAUTHORIZED
            }
        }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Unit> {
        return kotlin.runCatching {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val authStatus = auth.sessionStatus.value
            if (authStatus is SessionStatus.Authenticated) {
                val userId = authStatus.session.user?.id
                    ?: throw IllegalStateException("User is not authenticated. Can't create profile")
                profileRepository.fetchAndSaveProfile(userId)
            }
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
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return kotlin.runCatching {
            profileRepository.deleteProfiles()
            auth.signOut()
        }
    }
}