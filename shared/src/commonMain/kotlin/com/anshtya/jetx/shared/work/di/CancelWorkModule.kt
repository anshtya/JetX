package com.anshtya.jetx.shared.work.di

import com.anshtya.jetx.shared.ContextWrapper
import com.anshtya.jetx.shared.work.CancelWork
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class CancelWorkModule {
    @Single
    fun provideCancelWork(ctx: ContextWrapper): CancelWork
}