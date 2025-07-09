package com.anshtya.jetx.shared.attachments

import androidx.room.ColumnInfo

data class AttachmentInfo(
    val id: Int,
    @ColumnInfo(name = "storage_location")
    val storageLocation: String?,
    @ColumnInfo(name = "thumbnail_location")
    val thumbnailLocation: String?,
    val type: AttachmentType,
    val size: String?,
    @ColumnInfo(name = "transfer_state")
    val transferState: AttachmentTransferState?,
    @ColumnInfo(name = "download_progress")
    val downloadProgress: Float,
    val height: Int?,
    val width: Int?
)
