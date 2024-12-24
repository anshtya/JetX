package com.anshtya.jetx.auth.data

import com.anshtya.jetx.auth.data.model.AuthStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val authDatastore: AuthDatastore
) : AuthRepository {
    private val auth = client.auth

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

    override suspend fun signOut(): Result<Unit> {
        return kotlin.runCatching {
            auth.signOut()
            authDatastore.onSignOut()
        }
    }
}