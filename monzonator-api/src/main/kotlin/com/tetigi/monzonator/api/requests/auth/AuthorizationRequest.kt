package com.tetigi.monzonator.api.requests.auth

import com.tetigi.monzonator.api.data.auth.GrantType
import com.tetigi.monzonator.utils.FieldMappable
import java.net.URI

data class AuthorizationRequest(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: URI,
        val code: String
): FieldMappable(
        "grant_type" to GrantType.authorization_code,
        "client_id" to clientId,
        "client_secret" to clientSecret,
        "redirect_uri" to redirectUri,
        "code" to code
)