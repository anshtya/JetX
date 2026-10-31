package com.anshtya.jetx.attachments.ui.preview

import android.net.Uri
import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.attachments.data.AttachmentRepository
import com.anshtya.jetx.chats.domain.SendChatMessageUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class MediaPreviewViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val dispatcherRule = MainDispatcherRule(testDispatcher)

    private val uri1: Uri = mockk()
    private val uri2: Uri = mockk()
    private val recipientId = UUID.randomUUID()

    private val attachmentRepository: AttachmentRepository = mockk {
        // Saving an attachment before upload returns the same uri.
        coEvery { saveAttachmentBeforeUpload(any()) } answers { Result.success(firstArg()) }
    }
    private val sendChatMessageUseCase: SendChatMessageUseCase = mockk {
        coEvery { this@mockk(any<Int>(), any(), any()) } returns Result.success(Unit)
        coEvery { this@mockk(any<UUID>(), any(), any()) } returns Result.success(Unit)
    }
    private lateinit var viewModel: MediaPreviewViewModel

    @Before
    fun setUp() {
        viewModel = MediaPreviewViewModel(
            attachmentRepository = attachmentRepository,
            sendChatMessageUseCase = sendChatMessageUseCase,
            ioDispatcher = testDispatcher
        )
    }

    private fun kotlinx.coroutines.test.TestScope.collectState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.mediaPreviewUiState.collect()
        }
    }

    private fun sendItems(): List<SendItem> {
        val state = viewModel.mediaPreviewUiState.value
        assertTrue(state is MediaPreviewUiState.DataLoaded)
        return (state as MediaPreviewUiState.DataLoaded).data
    }

    @Test
    fun `processIncomingData exposes the saved uris as send items`() = runTest {
        collectState()

        viewModel.processIncomingData(SendTarget.Chats(listOf(1)), listOf(uri1, uri2))
        advanceUntilIdle()

        assertEquals(setOf(uri1, uri2), sendItems().map { it.uri }.toSet())
        assertTrue(sendItems().all { it.caption.isEmpty() })
    }

    @Test
    fun `processIncomingData reports an error for attachments that fail to save`() = runTest {
        coEvery { attachmentRepository.saveAttachmentBeforeUpload(uri2) } returns
                Result.failure(Exception("fail"))
        collectState()

        viewModel.processIncomingData(SendTarget.Chats(listOf(1)), listOf(uri1, uri2))
        advanceUntilIdle()

        assertEquals(listOf(uri1), sendItems().map { it.uri })
        assertEquals("Error processing attachment", viewModel.errorMessage.value)
    }

    @Test
    fun `onSendClick sends every item to every chat`() = runTest {
        collectState()
        viewModel.processIncomingData(SendTarget.Chats(listOf(1, 2)), listOf(uri1, uri2))
        advanceUntilIdle()

        viewModel.onSendClick()
        advanceUntilIdle()

        for (chatId in listOf(1, 2)) {
            for (uri in listOf(uri1, uri2)) {
                coVerify(exactly = 1) { sendChatMessageUseCase(chatId, null, uri) }
            }
        }
        coVerify(exactly = 0) { sendChatMessageUseCase(any<UUID>(), any(), any()) }
        assertTrue(viewModel.navigateToChat.value)
    }

    @Test
    fun `onSendClick to a recipient sends every item to that recipient`() = runTest {
        collectState()
        viewModel.processIncomingData(SendTarget.Recipient(recipientId), listOf(uri1, uri2))
        advanceUntilIdle()

        viewModel.onSendClick()
        advanceUntilIdle()

        coVerify(exactly = 1) { sendChatMessageUseCase(recipientId, null, uri1) }
        coVerify(exactly = 1) { sendChatMessageUseCase(recipientId, null, uri2) }
        coVerify(exactly = 0) { sendChatMessageUseCase(any<Int>(), any(), any()) }
        assertTrue(viewModel.navigateToChat.value)
    }

    @Test
    fun `captions are sent as text and blank captions as null`() = runTest {
        collectState()
        viewModel.processIncomingData(SendTarget.Recipient(recipientId), listOf(uri1))
        advanceUntilIdle()
        viewModel.onCaptionChange(0, "hello")

        viewModel.onSendClick()
        advanceUntilIdle()
        coVerify { sendChatMessageUseCase(recipientId, "hello", uri1) }

        viewModel.onCaptionChange(0, "   ")
        viewModel.onSendClick()
        advanceUntilIdle()
        coVerify { sendChatMessageUseCase(recipientId, null, uri1) }
    }

    @Test
    fun `failure sending to a chat reports an error`() = runTest {
        coEvery { sendChatMessageUseCase(any<Int>(), any(), any()) } returns
                Result.failure(Exception("fail"))
        collectState()
        viewModel.processIncomingData(SendTarget.Chats(listOf(1)), listOf(uri1))
        advanceUntilIdle()

        viewModel.onSendClick()
        advanceUntilIdle()

        assertEquals("Error sending message", viewModel.errorMessage.value)
        viewModel.onErrorShown()
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `onSendClick before any target is set does nothing`() = runTest {
        collectState()

        viewModel.onSendClick()
        advanceUntilIdle()

        coVerify(exactly = 0) { sendChatMessageUseCase(any<Int>(), any(), any()) }
        coVerify(exactly = 0) { sendChatMessageUseCase(any<UUID>(), any(), any()) }
        assertFalse(viewModel.navigateToChat.value)
    }

    @Test
    fun `onSelectItem updates the current index`() {
        viewModel.onSelectItem(2)

        assertEquals(2, viewModel.currentItemIndex.value)
    }
}
