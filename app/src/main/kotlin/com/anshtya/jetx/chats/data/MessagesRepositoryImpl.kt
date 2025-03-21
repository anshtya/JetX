package com.anshtya.jetx.chats.data

import android.util.Log
import com.anshtya.jetx.chats.data.model.DateChatMessages
import com.anshtya.jetx.chats.data.model.toNetworkMessage
import com.anshtya.jetx.common.model.MessageStatus
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.datasource.LocalMessagesDataSource
import com.anshtya.jetx.database.entity.MessageEntity
import com.anshtya.jetx.database.entity.toExternalModel
import com.anshtya.jetx.profile.ProfileRepository
import com.anshtya.jetx.util.Constants.MESSAGE_TABLE
import com.anshtya.jetx.util.getDateOrTime
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val localMessagesDataSource: LocalMessagesDataSource,
    private val profileRepository: ProfileRepository
) : MessagesRepository, MessageReceiveRepository {
    private val supabaseAuth = client.auth
    private val networkMessagesTable = client.from(MESSAGE_TABLE)

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
                            datePattern = "d MMMM yyyy",
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
                filter { eq("id", message.uid) }
            }
        )
        return message.chatId
    }

    override suspend fun sendChatMessage(chatId: Int, text: String?) {
        val recipientId = chatDao.getChatIds(chatId)!!.recipientId
        sendChatMessage(recipientId, text)
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
        localMessagesDataSource.updateMessageStatus(message.uid, MessageStatus.SENT)
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
            profileRepository.saveProfile(profileId.toString())
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

    override suspend fun deleteMessages(ids: List<Int>) {
        localMessagesDataSource.deleteMessages(ids)
    }
}