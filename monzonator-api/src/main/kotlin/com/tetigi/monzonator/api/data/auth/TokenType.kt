package com.tetigi.monzonator.api.data.auth

enum class TokenType(private val repr: String) {
    BEARER("Bearer");

    override fun toString(): String = repr
}