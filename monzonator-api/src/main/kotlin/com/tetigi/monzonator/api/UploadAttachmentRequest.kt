package com.tetigi.monzonator.api

data class UploadAttachmentRequest(
        val fileName: String,
        val fileType: String
)