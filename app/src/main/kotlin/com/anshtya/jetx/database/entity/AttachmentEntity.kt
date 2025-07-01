package com.anshtya.jetx.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anshtya.jetx.database.model.AttachmentTransferState
import com.anshtya.jetx.attachments.data.AttachmentType

@Entity(
    tableName = "attachment",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id", "message_id"])
    ],
)
data class AttachmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "message_id", index = true)
    val messageId: Int,
    @ColumnInfo(name = "file_name")
    val fileName: String?,
    @ColumnInfo(name = "storage_location")
    val storageLocation: String?,
    @ColumnInfo(name = "remote_location")
    val remoteLocation: String?,
    @ColumnInfo(name = "thumbnail_location")
    val thumbnailLocation: String?,
    val size: String? = null,
    val type: AttachmentType,
    @ColumnInfo(name = "transfer_state")
    val transferState: AttachmentTransferState? = null,
    @ColumnInfo(name = "download_progress")
    val downloadProgress: Float = 0f,
    val width: Int?,
    val height: Int?
)
