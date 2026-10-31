package com.anshtya.jetx.chats.ui.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import androidx.work.WorkManager
import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.chats.domain.SendChatMessageUseCase
import com.anshtya.jetx.chats.ui.navigation.ChatsDestination
import com.anshtya.jetx.core.database.model.MessageWithAttachment
import com.anshtya.jetx.core.model.UserProfile
import com.anshtya.jetx.notifications.DefaultNotificationManager
import com.anshtya.jetx.profile.data.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val recipientId = UUID.randomUUID()
    private val profile = UserProfile(
        id = recipientId,
        name = "Name",
        username = "recipient",
        phoneNumber = "123",
        pictureUrl = "profile-pic"
    )

    private val chatsRepository: ChatsRepository = mockk {
        coEvery { getChatRecipientId(any()) } returns recipientId
        coEvery { getChatId(any()) } returns null
    }
    private val messagesRepository: MessagesRepository = mockk {
        every { getChatMessages(any()) } returns flowOf(emptyList())
    }
    private val sendChatMessageUseCase: SendChatMessageUseCase = mockk {
        coEvery { this@mockk(any<UUID>(), any(), any()) } returns Result.success(Unit)
    }
    private val profileRepository: ProfileRepository = mockk {
        coEvery { getProfile(recipientId) } returns profile
    }
    private val workManager: WorkManager = mockk()
    private val notificationManager: DefaultNotificationManager = mockk(relaxed = true)

    @Before
    fun setUp() {
        // toRoute needs an Android Bundle, so resolve the route straight from the arguments.
        mockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    @After
    fun tearDown() {
        unmockkStatic("androidx.navigation.SavedStateHandleKt")
    }

    private fun viewModel(route: ChatsDestination.Chat): ChatViewModel {
        val savedStateHandle: SavedStateHandle = mockk()
        every { savedStateHandle.toRoute<ChatsDestination.Chat>() } returns route

        return ChatViewModel(
            savedStateHandle = savedStateHandle,
            chatsRepository = chatsRepository,
            messagesRepository = messagesRepository,
            sendChatMessageUseCase = sendChatMessageUseCase,
            profileRepository = profileRepository,
            workManager = workManager,
            defaultNotificationManager = notificationManager
        )
    }

    private fun TestScope.collectMessages(viewModel: ChatViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.chatMessages.collect()
        }
    }

    @Test
    fun `existing chat loads the recipient from the profile and opens the chat`() = runTest {
        val messages = listOf<MessageWithAttachment>(mockk(), mockk())
        every { messagesRepository.getChatMessages(4) } returns flowOf(messages)

        val viewModel = viewModel(ChatsDestination.Chat(chatId = 4))
        collectMessages(viewModel)
        advanceUntilIdle()

        assertEquals(
            RecipientUser(recipientId, "recipient", "profile-pic"),
            viewModel.recipientUser.value
        )
        assertEquals(messages, viewModel.chatMessages.value)
        verify { notificationManager.setCurrentChat(4) }
    }

    @Test
    fun `new chat uses the recipient from the arguments`() = runTest {
        val viewModel = viewModel(
            ChatsDestination.Chat(
                recipientId = recipientId.toString(),
                username = "from-args",
                pictureUrl = "args-pic"
            )
        )
        collectMessages(viewModel)
        advanceUntilIdle()

        assertEquals(
            RecipientUser(recipientId, "from-args", "args-pic"),
            viewModel.recipientUser.value
        )
        assertEquals(emptyList<MessageWithAttachment>(), viewModel.chatMessages.value)
        verify(exactly = 0) { messagesRepository.getChatMessages(any()) }
        verify(exactly = 0) { notificationManager.setCurrentChat(any()) }
    }

    @Test
    fun `new chat with an existing conversation opens that conversation`() = runTest {
        coEvery { chatsRepository.getChatId(recipientId) } returns 9

        val viewModel = viewModel(
            ChatsDestination.Chat(recipientId = recipientId.toString(), username = "user")
        )
        collectMessages(viewModel)
        advanceUntilIdle()

        verify { messagesRepository.getChatMessages(9) }
        verify { notificationManager.setCurrentChat(9) }
    }

    @Test
    fun `arguments that do not identify a chat leave the screen empty`() = runTest {
        val viewModel = viewModel(ChatsDestination.Chat())
        collectMessages(viewModel)
        advanceUntilIdle()

        assertNull(viewModel.recipientUser.value)
        assertEquals(emptyList<MessageWithAttachment>(), viewModel.chatMessages.value)
        verify(exactly = 0) { notificationManager.setCurrentChat(any()) }
    }

    @Test
    fun `malformed recipient id leaves the screen empty`() = runTest {
        val viewModel = viewModel(
            ChatsDestination.Chat(recipientId = "not-a-uuid", username = "user")
        )
        advanceUntilIdle()

        assertNull(viewModel.recipientUser.value)
    }

    @Test
    fun `sendMessage without a recipient does nothing`() = runTest {
        val viewModel = viewModel(ChatsDestination.Chat())

        viewModel.sendMessage("hi")
        advanceUntilIdle()

        coVerify(exactly = 0) { sendChatMessageUseCase(any<UUID>(), any(), any()) }
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `sendMessage in a new chat sends to the recipient and opens the created chat`() = runTest {
        coEvery { chatsRepository.getChatId(recipientId) } returnsMany listOf(null, 11)
        val uri: Uri = mockk()
        val viewModel = viewModel(
            ChatsDestination.Chat(recipientId = recipientId.toString(), username = "user")
        )
        collectMessages(viewModel)
        advanceUntilIdle()

        viewModel.sendMessage("hi", uri)
        advanceUntilIdle()

        coVerify { sendChatMessageUseCase(recipientId, "hi", uri) }
        verify { messagesRepository.getChatMessages(11) }
        verify { notificationManager.setCurrentChat(11) }
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `sendMessage tolerates the chat not being found after sending`() = runTest {
        val viewModel = viewModel(
            ChatsDestination.Chat(recipientId = recipientId.toString(), username = "user")
        )
        advanceUntilIdle()

        viewModel.sendMessage("hi")
        advanceUntilIdle()

        coVerify { sendChatMessageUseCase(recipientId, "hi", null) }
        verify(exactly = 0) { messagesRepository.getChatMessages(any()) }
        verify(exactly = 0) { notificationManager.setCurrentChat(any()) }
    }

    @Test
    fun `sendMessage failure reports an error and does not look up the chat`() = runTest {
        coEvery { sendChatMessageUseCase(any<UUID>(), any(), any()) } returns
                Result.failure(Exception("fail"))
        val viewModel = viewModel(
            ChatsDestination.Chat(recipientId = recipientId.toString(), username = "user")
        )
        advanceUntilIdle()

        viewModel.sendMessage("hi")
        advanceUntilIdle()

        assertEquals("Error sending message", viewModel.errorMessage.value)
        coVerify(exactly = 1) { chatsRepository.getChatId(any()) } // only the initial lookup

        viewModel.errorShown()
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `sendMessage in an existing chat does not look the chat up again`() = runTest {
        val viewModel = viewModel(ChatsDestination.Chat(chatId = 4))
        advanceUntilIdle()

        viewModel.sendMessage("hi")
        advanceUntilIdle()

        coVerify { sendChatMessageUseCase(recipientId, "hi", null) }
        coVerify(exactly = 0) { chatsRepository.getChatId(any()) }
    }

    @Test
    fun `message selection can be toggled and cleared`() = runTest {
        val viewModel = viewModel(ChatsDestination.Chat())

        viewModel.selectMessage(1)
        viewModel.selectMessage(2)
        viewModel.unselectMessage(1)
        assertEquals(setOf(2), viewModel.selectedMessages.value)

        viewModel.clearSelectedMessages()
        assertEquals(emptySet<Int>(), viewModel.selectedMessages.value)
    }

    @Test
    fun `markChatMessagesAsSeen only acts once a chat id is known`() = runTest {
        coEvery { messagesRepository.markChatMessagesAsSeen(any()) } returns Unit

        val noChat = viewModel(ChatsDestination.Chat())
        noChat.markChatMessagesAsSeen()
        advanceUntilIdle()
        coVerify(exactly = 0) { messagesRepository.markChatMessagesAsSeen(any()) }

        val existing = viewModel(ChatsDestination.Chat(chatId = 4))
        advanceUntilIdle()
        existing.markChatMessagesAsSeen()
        advanceUntilIdle()
        coVerify(exactly = 1) { messagesRepository.markChatMessagesAsSeen(4) }
    }
}
