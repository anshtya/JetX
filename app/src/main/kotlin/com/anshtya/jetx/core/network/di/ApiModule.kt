package com.anshtya.jetx.core.network.di

import com.anshtya.jetx.core.network.api.AuthApi
import com.anshtya.jetx.core.network.api.UserProfileApi
import com.anshtya.jetx.core.network.di.qualifiers.Authenticated
import com.anshtya.jetx.core.network.di.qualifiers.Base
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        @Base retrofit: Retrofit
    ): AuthApi {
        return retrofit.create<AuthApi>()
    }

    @Provides
    @Singleton
    fun provideUserApi(
        @Authenticated retrofit: Retrofit
    ): UserProfileApi {
        return retrofit.create<UserProfileApi>()
    }
}
