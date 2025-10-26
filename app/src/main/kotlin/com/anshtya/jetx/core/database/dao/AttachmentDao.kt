package com.anshtya.jetx.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.anshtya.jetx.core.database.entity.AttachmentEntity
import com.anshtya.jetx.core.database.model.AttachmentTransferState

@Dao
interface AttachmentDao {
    @Query("SELECT storage_location FROM attachment WHERE message_id = :messageId")
    suspend fun getStorageLocationForAttachment(messageId: Int): String?

    @Query("SELECT remote_location FROM attachment WHERE id = :attachmentId")
    suspend fun getRemoteLocation(attachmentId: Int): String

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Query(
        """
        UPDATE attachment
        SET transfer_state = :transferState
        WHERE id = :attachmentId AND message_id = :messageId
    """
    )
    suspend fun updateAttachmentTransferState(
        attachmentId: Int,
        messageId: Int,
        transferState: AttachmentTransferState
    )

    @Query(
        """
        UPDATE attachment
        SET download_progress = :progress
        WHERE id = :attachmentId AND message_id = :messageId
    """
    )
    suspend fun updateAttachmentDownloadProgress(
        attachmentId: Int,
        messageId: Int,
        progress: Float
    )

    @Query(
        """
        UPDATE attachment
        SET transfer_state = :transferState, storage_location = :storageLocation
        WHERE id = :attachmentId AND message_id = :messageId
    """
    )
    suspend fun updateAttachmentDownloadAsFinished(
        attachmentId: Int,
        messageId: Int,
        storageLocation: String,
        transferState: AttachmentTransferState = AttachmentTransferState.FINISHED
    )
}