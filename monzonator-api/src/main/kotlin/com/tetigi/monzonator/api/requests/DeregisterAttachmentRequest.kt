package com.tetigi.monzonator.api.requests

import com.tetigi.monzonator.utils.FieldMappable

data class DeregisterAttachmentRequest(
        val id: String
): FieldMappable(
        "id" to id
)