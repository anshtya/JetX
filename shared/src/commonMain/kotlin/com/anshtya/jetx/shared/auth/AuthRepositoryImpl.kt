package com.anshtya.jetx.shared.auth

import com.anshtya.jetx.shared.chats.MessageUpdatesListener
import com.anshtya.jetx.shared.database.dao.UserProfileDao
import com.anshtya.jetx.shared.fcm.FcmTokenManager
import com.anshtya.jetx.shared.preferences.PreferencesStore
import com.anshtya.jetx.shared.profile.ProfileRepository
import com.anshtya.jetx.shared.work.CancelWork
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single(binds = [AuthRepository::class])
class AuthRepositoryImpl(
    client: SupabaseClient,
    private val fcmTokenManager: FcmTokenManager,
    private val profileRepository: ProfileRepository,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val messageUpdatesListener: MessageUpdatesListener,
    private val cancelWork: CancelWork
) : AuthRepository {
    private val supabaseAuth = client.auth

    override val authStatus: Flow<AuthStatus> = supabaseAuth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthStatus.Loading
                else -> AuthStatus.Success(
                    authenticated = status is SessionStatus.Authenticated,
                    profileCreated = preferencesStore.getProfileCreated()
                )
            }
        }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<Boolean> {
        return runCatching {
            supabaseAuth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = supabaseAuth.currentUserOrNull()?.id
                ?: throw IllegalStateException("User is not authenticated. Can't create profile")
            val profileSaved = profileRepository.saveProfile(userId)
            if (profileSaved) {
                fcmTokenManager.addToken()
                preferencesStore.setProfileCreated(true)
            }

            profileSaved
        }
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<Unit> {
        return runCatching {
            supabaseAuth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            messageUpdatesListener.unsubscribe()
            fcmTokenManager.removeToken()
            cancelWork.cancelAllWork()
            userProfileDao.deleteAllProfiles()
            preferencesStore.clearPreferences()
            supabaseAuth.signOut()
        }
    }
}