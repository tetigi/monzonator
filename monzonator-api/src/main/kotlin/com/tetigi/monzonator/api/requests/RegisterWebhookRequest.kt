package com.tetigi.monzonator.api.requests

import java.net.URL

data class RegisterWebhookRequest(
        val accountId: String,
        val url: URL
)