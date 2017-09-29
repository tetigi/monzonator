package com.tetigi.monzonator.api.requests

import java.net.URL

data class RegisterAttachmentRequest(
        val externalId: String,
        val fileUrl: URL,
        val fileType: String
)