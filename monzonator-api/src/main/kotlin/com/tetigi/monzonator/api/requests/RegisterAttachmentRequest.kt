package com.tetigi.monzonator.api.requests

import com.tetigi.monzonator.utils.FieldMappable
import java.net.URL

data class RegisterAttachmentRequest(
        val externalId: String,
        val fileUrl: URL,
        val fileType: String
): FieldMappable(
        "external_id" to externalId,
        "file_url" to fileUrl,
        "file_type" to fileType
)