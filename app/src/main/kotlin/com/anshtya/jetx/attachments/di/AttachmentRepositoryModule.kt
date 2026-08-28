package com.anshtya.jetx.attachments.di

import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.attachments.data.AttachmentRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AttachmentRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAttachmentRepository(
        impl: AttachmentRepositoryImpl
    ): AttachmentRepository
}
