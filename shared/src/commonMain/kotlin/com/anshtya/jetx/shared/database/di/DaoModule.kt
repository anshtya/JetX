package com.anshtya.jetx.shared.database.di

import com.anshtya.jetx.shared.database.JetXDatabase
import com.anshtya.jetx.shared.database.dao.AttachmentDao
import com.anshtya.jetx.shared.database.dao.ChatDao
import com.anshtya.jetx.shared.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.shared.database.dao.MessageDao
import com.anshtya.jetx.shared.database.dao.UserProfileDao
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
object DaoModule {
    @Single
    fun provideAttachmentDao(db: JetXDatabase): AttachmentDao {
        return db.attachmentDao()
    }

    @Single
    fun provideChatDao(db: JetXDatabase): ChatDao {
        return db.chatDao()
    }

    @Single
    fun provideMessageDao(db: JetXDatabase): MessageDao {
        return db.messageDao()
    }

    @Single
    fun provideMessageAttachmentsDao(db: JetXDatabase): MessageAttachmentsDao {
        return db.messageAttachmentsDao()
    }

    @Single
    fun provideUserProfileDao(db: JetXDatabase): UserProfileDao {
        return db.userProfileDao()
    }
}