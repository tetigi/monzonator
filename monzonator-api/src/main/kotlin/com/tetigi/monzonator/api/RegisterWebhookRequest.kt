package com.tetigi.monzonator.api

import java.net.URL

data class RegisterWebhookRequest(
        val accountId: String,
        val url: URL
)