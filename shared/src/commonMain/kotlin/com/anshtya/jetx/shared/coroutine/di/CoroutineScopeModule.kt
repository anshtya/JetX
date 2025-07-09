package com.anshtya.jetx.shared.coroutine.di

import com.anshtya.jetx.shared.coroutine.DefaultDispatcher
import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.shared.coroutine.IoDispatcher
import com.anshtya.jetx.shared.coroutine.IoScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class CoroutineScopeModule {
    @Single
    @DefaultScope
    fun providesDefaultCoroutineScope(
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }

    @Single
    @IoScope
    fun providesIoCoroutineScope(
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): CoroutineScope {
        return CoroutineScope(SupervisorJob() + dispatcher)
    }
}