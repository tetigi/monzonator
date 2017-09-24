package com.tetigi.monzonator.api

import org.joda.time.DateTime

data class Account(
        val id: String,
        val description: String,
        val created: DateTime)