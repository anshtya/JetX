package com.anshtya.jetx.core.preferences.di

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.anshtya.jetx.core.preferences.PreferencesStore
import com.anshtya.jetx.core.preferences.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {
    @Provides
    @Singleton
    fun providePreferencesStore(
        dataStore: DataStore<Preferences>
    ): PreferencesStore {
        return PreferencesStore(
            dataStore = dataStore
        )
    }

    @Provides
    @Singleton
    fun provideTokenStore(
        encryptedSharedPreferences: SharedPreferences
    ): TokenStore {
        return TokenStore(
            encryptedSharedPreferences = encryptedSharedPreferences
        )
    }
}