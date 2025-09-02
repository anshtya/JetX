package com.anshtya.jetx.core.database.di

import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.database.dao.AttachmentDao
import com.anshtya.jetx.core.database.dao.ChatDao
import com.anshtya.jetx.core.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.core.database.dao.MessageDao
import com.anshtya.jetx.core.database.dao.RecentMessageDao
import com.anshtya.jetx.core.database.datasource.LocalMessagesDataSource
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
        messageAttachmentsDao: MessageAttachmentsDao,
        recentMessageDao: RecentMessageDao
    ) = LocalMessagesDataSource(
        attachmentDao,
        chatDao,
        db,
        messageDao,
        messageAttachmentsDao,
        recentMessageDao
    )
}