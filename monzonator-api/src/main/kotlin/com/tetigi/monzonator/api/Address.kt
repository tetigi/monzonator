package com.tetigi.monzonator.api

data class Address(
        val address: String,
        val city: String,
        val country: String,
        val latitude: Double,
        val longitude: Double,
        val postcode: String,
        val region: String
)