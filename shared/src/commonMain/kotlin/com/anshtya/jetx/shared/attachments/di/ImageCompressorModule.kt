package com.anshtya.jetx.shared.attachments.di

import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.attachments.ImageCompressor
import com.anshtya.jetx.shared.coroutine.DefaultDispatcher
import com.anshtya.jetx.shared.coroutine.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class ImageCompressorModule {
    @Single
    fun provideImageCompressor(
        ctx: ContextWrapper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): ImageCompressor
}