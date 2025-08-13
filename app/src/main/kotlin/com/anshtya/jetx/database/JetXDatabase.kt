package com.anshtya.jetx.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anshtya.jetx.database.converter.UUIDConverter
import com.anshtya.jetx.database.converter.ZonedDateTimeConverter
import com.anshtya.jetx.database.dao.AttachmentDao
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.dao.RecentMessageDao
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.AttachmentEntity
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.RecentMessageEntity
import com.anshtya.jetx.database.entity.UserProfileEntity

@Database(
    entities = [
        AttachmentEntity::class,
        ChatEntity::class,
        MessageEntity::class,
        RecentMessageEntity::class,
        UserProfileEntity::class
    ],
    version = 1
)
@TypeConverters(
    ZonedDateTimeConverter::class,
    UUIDConverter::class
)
abstract class JetXDatabase : RoomDatabase() {
    abstract fun attachmentDao(): AttachmentDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun messageAttachmentsDao(): MessageAttachmentsDao
    abstract fun recentMessageDao(): RecentMessageDao
    abstract fun userProfileDao(): UserProfileDao
}