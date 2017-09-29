package com.tetigi.monzonator.api.requests

data class UploadAttachmentRequest(
        val fileName: String,
        val fileType: String
)