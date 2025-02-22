package com.anshtya.jetx.settings.di

import com.anshtya.jetx.settings.data.SettingsRepository
import com.anshtya.jetx.settings.data.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsRepositoryModule {
    @Binds
    @Singleton
    abstract fun provideSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}