package com.anshtya.jetx.camera.ui

import android.graphics.Bitmap
import android.net.Uri
import com.anshtya.jetx.MainDispatcherRule
import com.anshtya.jetx.attachments.data.AttachmentRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val savedUri: Uri = mockk()
    private val bitmap: Bitmap = mockk()
    private val attachmentRepository: AttachmentRepository = mockk()
    private val viewModel = CameraViewModel(attachmentRepository)

    @Test
    fun `initial state has nothing to preview and no error`() {
        assertNull(viewModel.previewUri.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `captured image is exposed for preview once saved`() = runTest {
        coEvery { attachmentRepository.saveBitmapImageBeforeUpload(bitmap) } returns
                Result.success(savedUri)

        viewModel.onImageCapture(bitmap)

        assertEquals(savedUri, viewModel.previewUri.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `failing to save an image reports an error and does not navigate`() = runTest {
        coEvery { attachmentRepository.saveBitmapImageBeforeUpload(bitmap) } returns
                Result.failure(Exception("disk full"))

        viewModel.onImageCapture(bitmap)

        assertNull(viewModel.previewUri.value)
        assertEquals("Failed to save image", viewModel.errorMessage.value)
    }

    @Test
    fun `recorded video is exposed for preview using the saved copy`() = runTest {
        val recordedUri: Uri = mockk()
        coEvery { attachmentRepository.saveAttachmentBeforeUpload(recordedUri) } returns
                Result.success(savedUri)

        viewModel.onVideoCapture(recordedUri)

        assertEquals(savedUri, viewModel.previewUri.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun `failing to save a video reports an error and does not navigate`() = runTest {
        val recordedUri: Uri = mockk()
        coEvery { attachmentRepository.saveAttachmentBeforeUpload(recordedUri) } returns
                Result.failure(Exception("disk full"))

        viewModel.onVideoCapture(recordedUri)

        assertNull(viewModel.previewUri.value)
        assertEquals("Failed to save video", viewModel.errorMessage.value)
    }

    @Test
    fun `onNavigate clears the pending preview`() = runTest {
        coEvery { attachmentRepository.saveBitmapImageBeforeUpload(bitmap) } returns
                Result.success(savedUri)
        viewModel.onImageCapture(bitmap)

        viewModel.onNavigate()

        assertNull(viewModel.previewUri.value)
    }

    @Test
    fun `onErrorShown clears the error`() = runTest {
        coEvery { attachmentRepository.saveBitmapImageBeforeUpload(bitmap) } returns
                Result.failure(Exception())
        viewModel.onImageCapture(bitmap)

        viewModel.onErrorShown()

        assertNull(viewModel.errorMessage.value)
    }
}
