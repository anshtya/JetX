package com.anshtya.jetx.chats.data

import com.anshtya.jetx.common.model.Chat
import com.anshtya.jetx.database.dao.ChatDao
import com.anshtya.jetx.database.model.LocalChat
import com.anshtya.jetx.database.model.toExternalModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatsRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val client: SupabaseClient
) : ChatsRepository {
    override fun getChats(
        showArchivedChats: Boolean
    ): Flow<List<Chat>> {
        return chatDao.getChats(
            showArchivedChats = showArchivedChats,
        ).map { chat -> chat.map(LocalChat::toExternalModel) }
    }

    override suspend fun deleteChats(chatIds: List<Int>) {
        chatDao.deleteChats(chatIds)
    }
}