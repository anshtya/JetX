package com.anshtya.jetx.work.di

import android.content.Context
import com.anshtya.jetx.work.WorkManagerHelper
import com.anshtya.jetx.work.WorkScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkModule {
    @Provides
    @Singleton
    fun provideWorkManagerHelper(
        @ApplicationContext context: Context
    ) = WorkManagerHelper(context)

    @Provides
    @Singleton
    fun provideWorkScheduler(
        workManagerHelper: WorkManagerHelper
    ) = WorkScheduler(workManagerHelper)
}