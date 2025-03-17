package com.anshtya.jetx.chats.di

import com.anshtya.jetx.chats.data.MessageUpdatesListener
import com.anshtya.jetx.common.coroutine.DefaultScope
import com.anshtya.jetx.database.dao.MessageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MessageUpdatesListenerModule {
    @Provides
    @Singleton
    fun provideMessageUpdatesListener(
        client: SupabaseClient,
        messageDao: MessageDao,
        @DefaultScope coroutineScope: CoroutineScope
    ) = MessageUpdatesListener(client, messageDao, coroutineScope)
}