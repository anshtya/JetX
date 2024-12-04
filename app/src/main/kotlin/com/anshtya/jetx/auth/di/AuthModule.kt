package com.anshtya.jetx.auth.di

import com.anshtya.jetx.auth.data.AuthTokenManager
import com.anshtya.jetx.auth.data.AuthTokenStore
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
    abstract fun bindAuthTokenManager(
        authTokenStore: AuthTokenStore
    ) : AuthTokenManager
}