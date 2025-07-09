package com.anshtya.jetx.shared.coroutine.di

import com.anshtya.jetx.shared.coroutine.DefaultDispatcher
import com.anshtya.jetx.shared.coroutine.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class CoroutineDispatcherModule {
    @DefaultDispatcher
    @Single
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Single
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
