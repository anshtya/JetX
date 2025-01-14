package com.anshtya.jetx.chats.data

import com.anshtya.jetx.chats.data.model.ChatInfo
import com.anshtya.jetx.chats.data.model.NetworkMessage
import com.anshtya.jetx.chats.data.model.toChatInfo
import com.anshtya.jetx.chats.data.model.toEntity
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.dao.MessageDao
import com.anshtya.jetx.database.entity.ChatEntity
import com.anshtya.jetx.database.model.ChatWithRecentMessage
import com.anshtya.jetx.database.model.toExternalModel
import com.anshtya.jetx.profile.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    client: SupabaseClient,
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val profileRepository: ProfileRepository,
    @DefaultScope private val coroutineScope: CoroutineScope
) : ChatsRepository {
    private val json = Json
    private val auth = client.auth
    private val userInfo: UserInfo = auth.currentSessionOrNull()?.user
        ?: throw IllegalStateException("User should be logged in to access chats")

    private val messagesChannel = client.channel("messages-db-changes")
    private val messagesChanges = messagesChannel.postgresChangeFlow<PostgresAction>(
        schema = "public",
        filter = {
            table = "messages"
            filter(
                filter = FilterOperation(
                    column = "recipient_id",
                    operator = FilterOperator.EQ,
                    value = userInfo.id
                )
            )
        }
    )

    init {
        coroutineScope.launch {
            messagesChannel.subscribe()
            messagesChanges.collect { action ->
                when (action) {
                    is PostgresAction.Insert -> {
                        val message = json.decodeFromString<NetworkMessage>(
                            string = action.record.toString()
                        )
                        upsertMessage(message)
                    }
                    is PostgresAction.Update -> {
                        val message = json.decodeFromString<NetworkMessage>(
                            string = action.record.toString()
                        )
                        upsertMessage(message)
                    }
                    else -> {}
                }
            }
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

    private suspend fun upsertMessage(networkMessage: NetworkMessage) {
        val senderId = networkMessage.senderId
        val chat = chatDao.getChat(senderId)
        val chatId = if (chat != null) {
            chat.id
        } else {
            profileRepository.fetchAndSaveProfile(senderId.toString())

            // Logged-in user will chat to sender, hence sender will be recipient.
            chatDao.insertChat(ChatEntity(recipientId = senderId)).toInt()
        }
        messageDao.upsertMessage(networkMessage.toEntity(chatId))
    }
}