package com.tetigi.monzonator.api.data

import org.joda.time.DateTime

data class Account(
        val id: String,
        val description: String,
        val created: DateTime)