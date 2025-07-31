package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.fcm.FcmTokenManager
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.work.WorkManagerHelper
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val fcmTokenManager: FcmTokenManager,
    private val profileRepository: ProfileRepository,
    private val preferencesStore: PreferencesStore,
    private val userProfileDao: UserProfileDao,
    private val messageUpdatesListener: MessageUpdatesListener,
    private val workManagerHelper: WorkManagerHelper
) : AuthRepository {
    private val supabaseAuth = client.auth

    override val authState: Flow<AuthState> = supabaseAuth.sessionStatus
        .map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthState.Initializing
                is SessionStatus.Authenticated -> AuthState.Authenticated
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                is SessionStatus.RefreshFailure -> {
                    val sessionExists = supabaseAuth.loadFromStorage(autoRefresh = false)
                    AuthState.RefreshError(sessionExists)
                }
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
            } else {
                preferencesStore.setProfileCreated(false)
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
            preferencesStore.setProfileCreated(false)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            messageUpdatesListener.unsubscribe()
            workManagerHelper.cancelAllWork()
            userProfileDao.deleteAllProfiles()
            preferencesStore.clearPreferences()
            fcmTokenManager.removeToken()
            supabaseAuth.signOut()
        }
    }
}