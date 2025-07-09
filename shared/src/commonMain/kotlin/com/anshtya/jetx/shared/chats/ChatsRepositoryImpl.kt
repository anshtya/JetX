package com.anshtya.jetx.shared.chats

import com.anshtya.jetx.shared.coroutine.DefaultScope
import com.anshtya.jetx.shared.database.dao.ChatDao
import com.anshtya.jetx.shared.database.model.ChatWithRecentMessage
import com.anshtya.jetx.shared.database.model.toExternalModel
import com.anshtya.jetx.shared.model.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single(binds = [ChatsRepository::class])
class ChatsRepositoryImpl(
    private val chatDao: ChatDao,
    private val messageUpdatesListener: MessageUpdatesListener,
    @DefaultScope coroutineScope: CoroutineScope
) : ChatsRepository {
    override var currentChatId: Int? = null
        private set

    init {
        coroutineScope.launch {
            messageUpdatesListener.subscribe()
        }
    }

    override fun setCurrentChatId(id: Int?) {
        currentChatId = id
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