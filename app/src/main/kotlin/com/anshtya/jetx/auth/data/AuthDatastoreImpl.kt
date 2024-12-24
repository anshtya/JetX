package com.anshtya.jetx.auth.data

import androidx.datastore.preferences.core.Preferences
import com.anshtya.jetx.auth.data.model.AuthStatus
import com.anshtya.jetx.preferences.PreferencesMap
import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.values.AuthValues
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthDatastoreImpl @Inject constructor(
    private val preferencesStore: PreferencesStore
) : AuthDatastore {
    override val authStatus: Flow<AuthStatus> =
        preferencesStore.data.map {
            AuthStatus(
                authCompleted = it[getPreferenceKey(AuthValues.AUTH_COMPLETED)] ?: false,
                profileCreated = it[getPreferenceKey(AuthValues.PROFILE_CREATED)] ?: false
            )
        }

    override suspend fun setAuthCompleted(value: Boolean) {
        preferencesStore.set(AuthValues.AUTH_COMPLETED, value)
    }

    override suspend fun setProfileCreated(value: Boolean) {
        preferencesStore.set(AuthValues.PROFILE_CREATED, value)
    }

    override suspend fun onSignIn() {
        setAuthCompleted(true)
        setProfileCreated(true)
    }

    override suspend fun onSignOut() {
        setAuthCompleted(false)
        setProfileCreated(false)
    }

    private fun <T> getPreferenceKey(key: String): Preferences.Key<T> =
        PreferencesMap.getPreferenceKey(key)
}