package com.tetigi.monzonator.api.requests

import com.tetigi.monzonator.utils.FieldMappable
import java.net.URI

data class RegisterWebhookRequest(
        val accountId: String,
        val url: URI
): FieldMappable(
        "account_id" to accountId,
        "url" to url
)