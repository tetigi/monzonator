package com.tetigi.monzonator.api.requests.auth

import com.tetigi.monzonator.api.data.auth.GrantType
import com.tetigi.monzonator.utils.FieldMappable

data class RefreshTokenRequest(
        val clientId: String,
        val clientSecret: String,
        val refreshToken: String
): FieldMappable(
        "grant_type" to GrantType.refresh_token,
        "client_id" to clientId,
        "client_secret" to clientSecret,
        "refresh_token" to refreshToken
)