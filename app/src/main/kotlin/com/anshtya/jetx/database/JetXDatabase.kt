package com.anshtya.jetx.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.dao.UserProfileDao
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.UserProfileEntity
import com.anshtya.jetx.database.util.LocalDateTimeConverter

@Database(
    entities = [
        ChatEntity::class,
        MessageEntity::class,
        UserProfileEntity::class
    ],
    version = 1
)
@TypeConverters(
    LocalDateTimeConverter::class
)
abstract class JetXDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun userProfileDao(): UserProfileDao
}