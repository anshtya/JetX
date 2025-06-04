package com.anshtya.jetx.attachments

class AttachmentException(
    override val message: String = "Failed to process attachment"
): Exception()