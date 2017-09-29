package com.tetigi.monzonator.api.data

import java.util.*

data class AccountBalance(
        val balance: Int,
        val currency: Currency,
        val spendToday: Int
        )