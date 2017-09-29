package com.tetigi.monzonator.api.responses

import java.net.URL

data class UploadAttachmentResponse(
        val fileUrl: URL,
        val uploadUrl: URL
)