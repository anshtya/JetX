package com.anshtya.jetx.database.di

import com.anshtya.jetx.database.JetXDatabase
import com.anshtya.jetx.database.dao.AttachmentDao
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun provideLocalMessagesDataSource(
        attachmentDao: AttachmentDao,
        chatDao: ChatDao,
        db: JetXDatabase,
        messageDao: MessageDao,
        messageAttachmentsDao: MessageAttachmentsDao
    ) = LocalMessagesDataSource(attachmentDao, chatDao, db, messageDao, messageAttachmentsDao)
}