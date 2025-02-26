package com.anshtya.jetx.chats.data

import android.util.Log
import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.NetworkMessage
import com.anshtya.jetx.chats.data.model.toNetworkMessage
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.toExternalModel
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import com.anshtya.jetx.database.model.toExternalModel
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.util.Constants.FULL_DATE
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import com.anshtya.jetx.util.getDateOrTime
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.decodeRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val messageListener: MessageListener,
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val profileRepository: ProfileRepository,
    @DefaultScope private val coroutineScope: CoroutineScope
) : ChatsRepository, MessageReceiveRepository {
    private val supabaseAuth = client.auth
    private val networkMessagesTable = client.from(MESSAGE_TABLE)

    init {
        coroutineScope.launch {
            messageListener.subscribe()
            listenMessageChanges()
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

    override suspend fun getChatId(recipientId: UUID): Int? =
        chatDao.getChat(recipientId)?.id

    override suspend fun getChatRecipientId(chatId: Int): UUID? =
        chatDao.getChat(chatId)?.recipientId


    override fun getChatMessages(chatId: Int): Flow<DateChatMessages> {
        return localMessagesDataSource.getChatMessages(chatId)
            .distinctUntilChanged()
            .map { messages ->
                DateChatMessages(
                    messages = messages.groupBy(
                        keySelector = { message -> message.createdAt.truncatedTo(ChronoUnit.DAYS) },
                        valueTransform = { message -> message.toExternalModel() }
                    ).mapKeys { (createdAt, _) ->
                        createdAt.getDateOrTime(
                            datePattern = FULL_DATE,
                            getToday = true,
                            getYesterday = true
                        )
                    }
                )
            }
    }

    override suspend fun insertChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentUri: String?
    ): Int {
        val message = saveChatMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentUri = null,
            currentUser = false
        )
        networkMessagesTable.update(
            update = { set("has_received", true) },
            request = {
                filter { eq("id", message.id) }
            }
        )
        return message.chatId
    }

    override suspend fun sendChatMessage(
        recipientId: UUID,
        text: String?
    ) {
        val message = saveChatMessage(
            id = UUID.randomUUID(),
            senderId = UUID.fromString(supabaseAuth.currentUserOrNull()?.id),
            recipientId = recipientId,
            text = text,
            attachmentUri = null,
            currentUser = true
        )
        networkMessagesTable.insert(message.toNetworkMessage())
        localMessagesDataSource.updateMessageStatus(message.id, MessageStatus.SENT)
    }

    override suspend fun deleteChats(chatIds: List<Int>) {
        chatDao.deleteChats(chatIds)
    }

    override suspend fun markChatMessagesAsSeen(chatId: Int) {
        val unreadMessageIds = localMessagesDataSource.markChatMessagesAsSeen(chatId)
        Log.i("message", "unread ids - $unreadMessageIds")
        unreadMessageIds.forEach { messageId ->
            networkMessagesTable.update(
                update = { set("has_seen", true) },
                request = {
                    filter { eq("id", messageId) }
                }
            )
        }
    }

    private fun listenMessageChanges() {
        messageListener.changes.onEach { action ->
            when (action) {
                is PostgresAction.Update -> {
                    val networkMessage = action.decodeRecord<NetworkMessage>()
                    val storedMessage = localMessagesDataSource.getChatMessage(networkMessage.id)
                    localMessagesDataSource.updateMessage(
                        storedMessage.copy(
                            text = networkMessage.text,
                            status = if (networkMessage.hasSeen) {
                                MessageStatus.SEEN
                            } else if (networkMessage.hasReceived) {
                                MessageStatus.RECEIVED
                            } else storedMessage.status
                        )
                    )
                }

                else -> {}
            }
        }.launchIn(coroutineScope)
    }

    private suspend fun saveChatMessage(
        id: UUID,
        senderId: UUID,
        recipientId: UUID,
        text: String?,
        attachmentUri: String?,
        currentUser: Boolean
    ): MessageEntity {
        val profileId = if (currentUser) recipientId else senderId
        val profileExists = profileRepository.getProfile(profileId)
        if (profileExists == null) {
            profileRepository.fetchAndSaveProfile(profileId.toString())
        }
        return localMessagesDataSource.insertMessage(
            id = id,
            senderId = senderId,
            recipientId = recipientId,
            text = text,
            attachmentUri = attachmentUri,
            currentUser = currentUser
        )
    }
}