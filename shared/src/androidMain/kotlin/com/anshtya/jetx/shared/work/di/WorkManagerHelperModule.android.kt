package com.anshtya.jetx.shared.work.di

import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.work.CancelWork
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class CancelWorkModule {
    @Single
    actual fun provideCancelWork(ctx: ContextWrapper): CancelWork {
        return CancelWork(ctx.context.applicationContext)
    }
}