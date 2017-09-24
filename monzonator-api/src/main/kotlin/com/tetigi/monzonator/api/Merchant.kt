package com.tetigi.monzonator.api

import org.joda.time.DateTime
import java.net.URL


data class Merchant(
        val address: Address,
        val created: DateTime,
        val groupId: String,
        val id: String,
        val logo: URL,
        val emoji: String,
        val name: String,
        val category: String
)