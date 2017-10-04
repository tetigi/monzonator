package com.tetigi.monzonator.api.data.auth

enum class GrantType(private val repr: String) {

    AUTHORIZATION_CODE("authorization_code");

    override fun toString(): String = repr
}