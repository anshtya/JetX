package com.anshtya.jetx.profile.di

import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.profile.ProfileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileRepositoryModule {
    @Singleton
    @Binds
    abstract fun provideProfileRepository(
        impl: ProfileRepositoryImpl
    ) : ProfileRepository
}