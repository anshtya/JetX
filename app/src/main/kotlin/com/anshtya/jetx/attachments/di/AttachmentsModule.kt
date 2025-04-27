package com.anshtya.jetx.attachments.di

import android.content.Context
import com.anshtya.jetx.attachments.ImageCompressor
import com.anshtya.jetx.common.coroutine.DefaultDispatcher
import com.anshtya.jetx.common.coroutine.IoDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttachmentsModule {
    @Provides
    @Singleton
    fun provideImageCompressor(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ) = ImageCompressor(context, ioDispatcher, defaultDispatcher)
}