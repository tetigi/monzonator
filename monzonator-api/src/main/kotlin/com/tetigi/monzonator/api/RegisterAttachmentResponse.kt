package com.tetigi.monzonator.api

import org.joda.time.DateTime
import java.net.URL

data class RegisterAttachmentResponse(
        val id: String,
        val userId: String,
        val externalId: String,
        val fileUrl: URL,
        val fileType: String,
        val created: DateTime
)