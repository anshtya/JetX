package com.anshtya.jetx.shared

import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

expect class ContextWrapper

@Module
expect class ContextModule {
    @Single
    fun providesContextWrapper(scope: Scope): ContextWrapper
}