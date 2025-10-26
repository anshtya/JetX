package com.anshtya.jetx.core.network.di

import com.anshtya.jetx.core.network.api.AttachmentApi
import com.anshtya.jetx.core.network.api.AuthApi
import com.anshtya.jetx.core.network.api.MessageApi
import com.anshtya.jetx.core.network.api.StorageApi
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

    @Provides
    @Singleton
    fun provideStorageApi(
        @Authenticated retrofit: Retrofit
    ): StorageApi {
        return retrofit.create<StorageApi>()
    }

    @Provides
    @Singleton
    fun provideAttachmentApi(
        @Authenticated retrofit: Retrofit
    ): AttachmentApi {
        return retrofit.create<AttachmentApi>()
    }

    @Provides
    @Singleton
    fun provideMessageApi(
        @Authenticated retrofit: Retrofit
    ): MessageApi {
        return retrofit.create<MessageApi>()
    }
}
