package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.data.Account

data class GetAccountsResponse(
        val accounts: List<Account>
)
