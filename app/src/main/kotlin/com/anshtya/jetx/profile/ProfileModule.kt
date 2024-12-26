package com.anshtya.jetx.profile

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {
    @Singleton
    @Binds
    abstract fun provideProfileRepository(
        impl: ProfileRepositoryImpl
    ) : ProfileRepository
}