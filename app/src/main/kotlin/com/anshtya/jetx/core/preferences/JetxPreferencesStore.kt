package com.anshtya.jetx.core.preferences

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anshtya.jetx.core.preferences.store.AccountStore
import com.anshtya.jetx.core.preferences.store.TokenStore
import com.anshtya.jetx.core.preferences.store.UserStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JetxPreferencesStore @Inject constructor(
    preferencesDataStore: DataStore<Preferences>,
    encryptedSharedPreferences: SharedPreferences
) {
    val account = AccountStore(preferencesDataStore)
    val token = TokenStore(encryptedSharedPreferences)
    val user = UserStore(preferencesDataStore)
}