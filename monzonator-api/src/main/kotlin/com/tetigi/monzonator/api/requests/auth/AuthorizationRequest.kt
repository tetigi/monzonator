package com.tetigi.monzonator.api.requests.auth

import com.tetigi.monzonator.api.data.auth.GrantType
import com.tetigi.monzonator.utils.FieldMappable
import java.net.URL

data class AuthorizationRequest(
        val grantType: GrantType,
        val clientId: String,
        val clientSecret: String,
        val redirectUri: URL,
        val code: String
): FieldMappable(
        "grant_type" to grantType,
        "client_id" to clientId,
        "client_secret" to clientSecret,
        "redirectUri" to redirectUri,
        "code" to code
)