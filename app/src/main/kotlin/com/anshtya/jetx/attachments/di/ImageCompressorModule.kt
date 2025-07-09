package com.anshtya.jetx.attachments.di

import android.content.Context
import com.anshtya.jetx.shared.attachments.ImageCompressor
import com.anshtya.jetx.shared.coroutine.DefaultDispatcher
import com.anshtya.jetx.shared.coroutine.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class ImageCompressorModule {
    @Single
    fun provideImageCompressor(
        context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): ImageCompressor = ImageCompressor(
        context = context,
        ioDispatcher = ioDispatcher,
        defaultDispatcher = defaultDispatcher
    )
}