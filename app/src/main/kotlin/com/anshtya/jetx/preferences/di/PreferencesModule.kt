package com.anshtya.jetx.preferences.di

import com.anshtya.jetx.preferences.PreferencesStore
import com.anshtya.jetx.preferences.PreferencesStoreImpl
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
}