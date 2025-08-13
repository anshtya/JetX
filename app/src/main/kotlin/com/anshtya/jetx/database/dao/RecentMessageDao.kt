package com.anshtya.jetx.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anshtya.jetx.database.entity.RecentMessageEntity

@Dao
interface RecentMessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentMessage(recentMessage: RecentMessageEntity)

    @Query("""
        UPDATE recent_message
        SET message_id = :messageId
        WHERE chat_id = :chatId
    """)
    suspend fun updateRecentMessageId(
        messageId: Int?,
        chatId: Int
    )
}