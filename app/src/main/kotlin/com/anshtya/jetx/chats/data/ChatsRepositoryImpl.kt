package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.ChatInfo
import com.anshtya.jetx.chats.data.model.toChatInfo
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import com.anshtya.jetx.database.model.toExternalModel
import com.anshtya.jetx.profile.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val profileRepository: ProfileRepository
) : ChatsRepository {
    override fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>> {
        return chatDao.getChatsWithRecentMessage(
            showArchivedChats = false,
            showFavoriteChats = showFavoriteChats,
            showUnreadChats = showUnreadChats
        ).map { chat ->
            chat.map(ChatWithRecentMessage::toExternalModel)
        }
    }

    override fun getArchivedChats(): Flow<List<Chat>> {
        return chatDao.getChatsWithRecentMessage(
            showArchivedChats = true,
            showFavoriteChats = false,
            showUnreadChats = false
        ).map { chat ->
            chat.map(ChatWithRecentMessage::toExternalModel)
        }
    }

    override suspend fun getChatInfo(recipientId: UUID): ChatInfo? {
        return chatDao.getChat(recipientId)?.toChatInfo()
    }

    override suspend fun createChat(recipientId: UUID): Int {
        profileRepository.fetchAndSaveProfile(recipientId.toString())
        return chatDao.insertChat(ChatEntity(recipientId = recipientId)).toInt()
    }

    override suspend fun deleteChats(chatIds: List<Int>) {
        chatDao.deleteChats(chatIds)
    }
}