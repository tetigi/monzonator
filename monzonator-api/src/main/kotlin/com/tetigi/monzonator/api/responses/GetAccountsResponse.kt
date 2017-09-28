package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.Account

data class GetAccountsResponse(
        val accounts: List<Account>
)
