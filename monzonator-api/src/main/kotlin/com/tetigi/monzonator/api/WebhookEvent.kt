package com.tetigi.monzonator.api

import com.fasterxml.jackson.annotation.JsonIgnore
import org.joda.time.DateTime
import java.util.*

sealed class WebhookEvent(
        @JsonIgnore val type: WebhookEventType
) {
    data class TransactionCreated(
            val accountId: String,
            val amount: Int,
            val created: DateTime,
            val currency: Currency,
            val description: String,
            val id: String,
            val category: String,
            val isLoad: Boolean,
            val settled: Boolean,
            val merchant: Merchant
    ): WebhookEvent(WebhookEventType.TRANSACTION_CREATED)
}