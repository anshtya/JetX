package com.anshtya.jetx.core.database.di

import com.anshtya.jetx.core.database.JetXDatabase
import com.anshtya.jetx.core.database.dao.AttachmentDao
import com.anshtya.jetx.core.database.dao.ChatDao
import com.anshtya.jetx.core.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.core.database.dao.MessageDao
import com.anshtya.jetx.core.database.dao.RecentMessageDao
import com.anshtya.jetx.core.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
    @Singleton
    @Provides
    fun provideAttachmentDao(db: JetXDatabase): AttachmentDao {
        return db.attachmentDao()
    }

    @Singleton
    @Provides
    fun provideChatDao(db: JetXDatabase): ChatDao {
        return db.chatDao()
    }

    @Singleton
    @Provides
    fun provideMessageDao(db: JetXDatabase): MessageDao {
        return db.messageDao()
    }

    @Singleton
    @Provides
    fun provideMessageAttachmentsDao(db: JetXDatabase): MessageAttachmentsDao {
        return db.messageAttachmentsDao()
    }

    @Singleton
    @Provides
    fun provideRecentMessageDao(db: JetXDatabase): RecentMessageDao {
        return db.recentMessageDao()
    }

    @Singleton
    @Provides
    fun provideUserProfileDao(db: JetXDatabase): UserProfileDao {
        return db.userProfileDao()
    }
}