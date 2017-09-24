package com.tetigi.monzonator.api

import org.joda.time.DateTime
import java.util.*

data class Transaction(
        val accountBalance: Int,
        val amount: Int,
        val created: DateTime,
        val currency: Currency,
        val description: String,
        val id: String,
        val merchant: Merchant,
        val metadata: Map<String, Any>,
        val notes: String,
        val isLoad: Boolean,
        val settled: DateTime
        )