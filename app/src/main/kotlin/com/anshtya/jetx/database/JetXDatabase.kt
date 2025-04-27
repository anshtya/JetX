package com.anshtya.jetx.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anshtya.jetx.database.dao.AttachmentDao
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageAttachmentsDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.AttachmentEntity
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.database.util.UUIDConverter
import com.anshtya.jetx.database.util.ZonedDateTimeConverter

@Database(
    entities = [
        AttachmentEntity::class,
        ChatEntity::class,
        MessageEntity::class,
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
    abstract fun userProfileDao(): UserProfileDao
}