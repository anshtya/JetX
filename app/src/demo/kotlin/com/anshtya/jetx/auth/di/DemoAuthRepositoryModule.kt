package com.anshtya.jetx.auth.di

import com.anshtya.jetx.auth.data.AuthRepository
import com.anshtya.jetx.auth.data.fake.FakeAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DemoAuthRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        impl: FakeAuthRepository
    ): AuthRepository
}