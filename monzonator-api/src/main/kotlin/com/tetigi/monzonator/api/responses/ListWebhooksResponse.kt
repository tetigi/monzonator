package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.data.Webhook


data class ListWebhooksResponse(
        val webhooks: List<Webhook>
)