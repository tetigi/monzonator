package com.tetigi.monzonator.api.requests

import com.tetigi.monzonator.utils.FieldMappable

data class UploadAttachmentRequest(
        val fileName: String,
        val fileType: String
): FieldMappable(
        "file_name" to fileName,
        "file_type" to fileType
)