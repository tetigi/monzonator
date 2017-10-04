package com.tetigi.monzonator.api.responses

import com.tetigi.monzonator.api.data.auth.TokenType

data class TokenResponse(
        val accessToken: String,
        val clientId: String,
        val refreshToken: String,
        val tokenType: TokenType,
        val userId: String,
        val expiresIn: Int
)
