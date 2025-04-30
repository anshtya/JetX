package com.anshtya.jetx.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.anshtya.jetx.database.entity.AttachmentEntity

data class MessageWithAttachment(
    @Embedded val messageInfo: MessageInfo,
    @Relation(
        entity = AttachmentEntity::class,
        parentColumn = "id",
        entityColumn = "message_id",
        projection = [
            "id",
            "storage_location",
            "thumbnail_location",
            "type",
            "download_progress",
            "transfer_state",
            "width",
            "height"
        ]
    )
    val attachment: AttachmentInfo?
)
