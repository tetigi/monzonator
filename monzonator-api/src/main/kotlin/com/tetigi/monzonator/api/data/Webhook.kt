package com.tetigi.monzonator.api.data

import java.net.URL

data class Webhook(
        val accountId: String,
        val id: String,
        val url: URL
)