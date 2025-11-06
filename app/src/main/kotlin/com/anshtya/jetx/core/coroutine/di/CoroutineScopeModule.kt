package com.anshtya.jetx.core.coroutine.di

import com.anshtya.jetx.core.coroutine.DefaultDispatcher
import com.anshtya.jetx.core.coroutine.DefaultScope
import com.anshtya.jetx.core.coroutine.ExternalScope
import com.anshtya.jetx.core.coroutine.MainDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopeModule {
    @Singleton
    @ExternalScope
    @Provides
    fun providesExternalCoroutineScope(
        @MainDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @Singleton
    @DefaultScope
    @Provides
    fun providesDefaultCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}