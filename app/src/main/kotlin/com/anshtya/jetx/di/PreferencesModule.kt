package com.anshtya.jetx.di

import com.anshtya.jetx.data.preferences.PreferencesStore
import com.anshtya.jetx.data.preferences.PreferencesStoreImpl
import com.anshtya.jetx.data.preferences.auth.AuthTokenManager
import com.anshtya.jetx.data.preferences.auth.AuthTokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {
    @Singleton
    @Binds
    abstract fun bindPreferencesStore(
        preferencesStoreImpl: PreferencesStoreImpl
    ) : PreferencesStore

    @Singleton
    @Binds
    abstract fun bindAuthTokenManager(
        authTokenStore: AuthTokenStore
    ) : AuthTokenManager
}