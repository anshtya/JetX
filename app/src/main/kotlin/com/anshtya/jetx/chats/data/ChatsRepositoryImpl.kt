package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.NetworkMessage
import com.anshtya.jetx.chats.data.model.toIncomingMessage
import com.anshtya.jetx.chats.data.model.toNetworkMessage
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.common.model.IncomingMessage
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
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
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val profileRepository: ProfileRepository,
    private val messageListener: MessageListener,
    private val localMessagesDataSource: LocalMessagesDataSource,
    @DefaultScope private val coroutineScope: CoroutineScope
) : ChatsRepository {
    private val supabaseAuth = client.auth
    private val messagesTable = client.from(MESSAGE_TABLE)

    override suspend fun subscribeChanges() {
        messageListener.subscribe()
        listenMessageChanges()
    }

    override suspend fun unsubscribeChanges() {
        messageListener.unsubscribe()
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
        recipientId: UUID,
        text: String?,
        attachment: String?
    ) {
        val message = IncomingMessage(
            id = UUID.randomUUID(),
            senderId = UUID.fromString(supabaseAuth.currentUserOrNull()?.id),
            recipientId = recipientId,
            text = text,
            attachmentUri = attachment,
            status = MessageStatus.SENDING
        )
        localMessagesDataSource.insertMessage(message, isCurrentUser = true)
        messagesTable.insert(message.toNetworkMessage())
        localMessagesDataSource.updateMessageStatus(message.id, MessageStatus.SENT)
    }

    override suspend fun deleteChats(chatIds: List<Int>) {
        chatDao.deleteChats(chatIds)
    }

    override suspend fun markChatAsRead(recipientId: UUID) {
        localMessagesDataSource.markChatAsRead(recipientId)?.forEach { messageId ->
            messagesTable.update(
                update = { set("has_seen", true) },
                request = {
                    filter { eq("id", messageId) }
                }
            )
        }
    }

    override suspend fun markChatMessageAsSeen(messageId: UUID) {
        localMessagesDataSource.markMessageSeen(messageId)
        messagesTable.update(
            update = { set("has_seen", true) },
            request = {
                filter { eq("id", messageId) }
            }
        )
    }

    private fun listenMessageChanges() {
        val userId = UUID.fromString(supabaseAuth.currentUserOrNull()?.id)
        messageListener.changes.onEach { action ->
            when (action) {
                is PostgresAction.Insert -> {
                    val message = action.decodeRecord<NetworkMessage>()
                    val senderId = message.senderId
                    val profileExists = profileRepository.profileExists(senderId)
                    if (!profileExists) {
                        profileRepository.fetchAndSaveProfile(senderId.toString())
                    }
                    localMessagesDataSource.insertMessage(
                        incomingMessage = message.toIncomingMessage(),
                        isCurrentUser = false
                    )
                    messagesTable.update(
                        update = { set("has_received", true) },
                        request = {
                            filter { eq("id", message.id) }
                        }
                    )
                }

                is PostgresAction.Update -> {
                    val networkMessage = action.decodeRecord<NetworkMessage>()
                    val storedMessage = localMessagesDataSource.getChatMessage(networkMessage.id)
                    if (userId == networkMessage.recipientId) {
                        // Discard message status db updates because they have already been updated
                        // when seeing or receiving messages
                        if (networkMessage.text != storedMessage.text) {
                            localMessagesDataSource.updateMessage(
                                storedMessage.copy(text = networkMessage.text)
                            )
                        }
                    } else {
                        // Discard text message db updates because they have already been updated
                        // before updating messages on network
                        val hasSeen = storedMessage.status == MessageStatus.SEEN
                        val hasReceived = storedMessage.status == MessageStatus.RECEIVED
                        if (networkMessage.hasReceived != hasReceived || networkMessage.hasSeen != hasSeen) {
                            localMessagesDataSource.updateMessage(
                                storedMessage.copy(
                                    status = if (networkMessage.hasSeen) {
                                        MessageStatus.SEEN
                                    } else if (networkMessage.hasReceived) {
                                        MessageStatus.RECEIVED
                                    } else storedMessage.status
                                )
                            )
                        }
                    }
                }

                else -> {}
            }
        }.launchIn(coroutineScope)
    }
}