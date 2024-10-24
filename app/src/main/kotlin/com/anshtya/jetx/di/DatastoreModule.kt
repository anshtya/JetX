package com.anshtya.jetx.di

import com.anshtya.jetx.data.datastore.TokenManager
import com.anshtya.jetx.data.datastore.TokenDatastore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatastoreModule {
    @Singleton
    @Binds
    abstract fun provideTokenManager(
        tokenDatastore: TokenDatastore
    ) : TokenManager
}