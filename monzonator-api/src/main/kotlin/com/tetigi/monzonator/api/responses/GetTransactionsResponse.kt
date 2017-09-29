package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.data.Transaction


data class GetTransactionsResponse(
        val transactions: List<Transaction>
)