package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import com.anshtya.jetx.database.model.toExternalModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val messageUpdatesListener: MessageUpdatesListener,
    @DefaultScope private val coroutineScope: CoroutineScope
) : ChatsRepository {
    init {
        coroutineScope.launch {
            messageUpdatesListener.subscribe()
        }
    }

    override fun getChats(
        showFavoriteChats: Boolean,
        showUnreadChats: Boolean
    ): Flow<List<Chat>> {
        return chatDao.getChatsWithRecentMessage(
            showArchivedChats = false,
            showFavoriteChats = showFavoriteChats,
            showUnreadChats = showUnreadChats
        )
            .distinctUntilChanged()
            .map { chat ->
                chat.map(ChatWithRecentMessage::toExternalModel)
            }
    }

    override fun getArchivedChats(): Flow<List<Chat>> {
        return chatDao.getChatsWithRecentMessage(
            showArchivedChats = true,
            showFavoriteChats = false,
            showUnreadChats = false
        )
            .distinctUntilChanged()
            .map { chat ->
                chat.map(ChatWithRecentMessage::toExternalModel)
            }
    }

    override suspend fun getChatId(recipientId: UUID): Int? = chatDao.getChatId(recipientId)

    override suspend fun getChatRecipientId(chatId: Int): UUID = chatDao.getChatRecipientId(chatId)

    override suspend fun deleteChats(chatIds: List<Int>) {
        chatDao.deleteChats(chatIds)
    }

    override suspend fun archiveChats(chatIds: List<Int>) {
        chatDao.archiveChat(chatIds)
    }

    override suspend fun unarchiveChats(chatIds: List<Int>) {
        chatDao.unarchiveChat(chatIds)
    }
}