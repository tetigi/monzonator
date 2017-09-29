package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.data.Transaction


data class GetTransactionResponse(
        val transaction: Transaction
)