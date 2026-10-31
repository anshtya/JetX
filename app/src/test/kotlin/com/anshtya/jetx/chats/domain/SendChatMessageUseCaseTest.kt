package com.anshtya.jetx.chats.domain

import androidx.work.WorkManager
import com.anshtya.jetx.attachments.data.AttachmentFormat
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.auth.data.AuthManager
import com.anshtya.jetx.auth.data.model.AuthState
import com.anshtya.jetx.chats.data.ChatsRepository
import com.anshtya.jetx.chats.data.MessagesRepository
import com.anshtya.jetx.profile.data.ProfileRepository
import com.anshtya.jetx.work.worker.MessageSendWorker
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class SendChatMessageUseCaseTest {
    private val userId = UUID.randomUUID()
    private val recipientId = UUID.randomUUID()

    private val authState = MutableStateFlow<AuthState>(AuthState.Authenticated(userId, "token"))
    private val authManager: AuthManager = mockk {
        every { authState } returns this@SendChatMessageUseCaseTest.authState
    }
    private val messagesRepository: MessagesRepository = mockk {
        coEvery { insertMessage(any(), any(), any(), any(), any(), any()) } returns 42
    }
    private val chatsRepository: ChatsRepository = mockk {
        coEvery { getChatRecipientId(any()) } returns recipientId
    }
    private val profileRepository: ProfileRepository = mockk {
        coEvery { fetchAndSaveProfile(any()) } returns Result.success(Unit)
    }
    private val attachmentRepository: AttachmentRepository = mockk()
    private val workManager: WorkManager = mockk()

    private val useCase = SendChatMessageUseCase(
        messagesRepository = messagesRepository,
        chatsRepository = chatsRepository,
        profileRepository = profileRepository,
        attachmentRepository = attachmentRepository,
        authManager = authManager,
        workManager = workManager
    )

    @Before
    fun setUp() {
        mockkObject(MessageSendWorker.Companion)
        every { MessageSendWorker.scheduleWork(any(), any()) } just runs
    }

    @After
    fun tearDown() {
        unmockkObject(MessageSendWorker.Companion)
    }

    @Test
    fun `sends text message as the current user and schedules delivery`() = runTest {
        val result = useCase(recipientId = recipientId, text = "hi", attachmentUri = null)

        assertTrue(result.isSuccess)
        coVerify {
            messagesRepository.insertMessage(
                id = any(),
                senderId = userId,
                recipientId = recipientId,
                text = "hi",
                attachmentFormat = AttachmentFormat.None,
                currentUser = true
            )
        }
        verify { MessageSendWorker.scheduleWork(workManager, 42) }
    }

    @Test
    fun `resolves recipient from chat id`() = runTest {
        val result = useCase(chatId = 3, text = "hi", attachmentUri = null)

        assertTrue(result.isSuccess)
        coVerify { chatsRepository.getChatRecipientId(3) }
        coVerify { messagesRepository.insertMessage(any(), userId, recipientId, "hi", any(), true) }
    }

    @Test
    fun `fails without saving or scheduling when user is not authenticated`() = runTest {
        authState.value = AuthState.Unauthenticated

        val result = useCase(recipientId = recipientId, text = "hi", attachmentUri = null)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        coVerify(exactly = 0) {
            messagesRepository.insertMessage(any(), any(), any(), any(), any(), any())
        }
        verify(exactly = 0) { MessageSendWorker.scheduleWork(any(), any()) }
    }

    @Test
    fun `chat id overload returns failure when user is not authenticated`() = runTest {
        authState.value = AuthState.Initializing

        val result = useCase(chatId = 3, text = "hi", attachmentUri = null)

        assertTrue(result.isFailure)
        assertEquals(IllegalStateException::class, result.exceptionOrNull()!!::class)
    }
}
