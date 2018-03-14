package com.tetigi.monzonator.api.utils

import com.google.common.net.UrlEscapers
import java.net.URI


object Auth {
    /**
     * The default URI for making a request for a new auth token.
     * Note, this is NOT the URI to make refresh or authorization requests.
     * Please see [MonzoService.DEFAULT_MONZO_URI]
     */
    const val DEFAULT_MONZO_AUTH_REQUEST_URI: String = "https://auth.getmondo.co.uk"

    const val CALLBACK_URI: String = "oauth/callback"

    @JvmStatic
    fun getAuthLink(clientId: String, redirectUri: URI, state: String): String {
        val escapedRedirectUri = UrlEscapers.urlPathSegmentEscaper().escape(redirectUri.toString())
        return "$DEFAULT_MONZO_AUTH_REQUEST_URI/?client_id=$clientId&redirect_uri=$escapedRedirectUri&response_type=code&state=$state"
    }
}