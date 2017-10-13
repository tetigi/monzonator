package com.tetigi.monzonator.api

import com.google.common.net.UrlEscapers
import com.tetigi.monzonator.api.requests.auth.AuthorizationRequest
import com.tetigi.monzonator.api.requests.auth.RefreshTokenRequest
import com.tetigi.monzonator.api.responses.TokenResponse
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.net.URI

interface MonzoAuthService {

    /**
     * When you receive an authorization code, exchange it for an access token.
     * The resulting access token is tied to both your client and an individual Monzo user,
     * and is valid for several hours.
     */
    @POST("oauth2/token")
    @FormUrlEncoded
    fun authorizeToken(@FieldMap request: AuthorizationRequest): Call<TokenResponse>

    /**
     * Refreshing an access token will invalidate the previous token, if it is still valid.
     * Refreshing is a one-time operation.
     */
    @POST("oauth2/token")
    @FormUrlEncoded
    fun refreshToken(@FieldMap request: RefreshTokenRequest): Call<TokenResponse>

    companion object {
        /**
         * The default URI for making a request for a new auth token.
         * Note, this is NOT the URI to make refresh or authorization requests.
         * Please see [MonzoService.DEFAULT_MONZO_URI]
         */
        val DEFAULT_MONZO_AUTH_REQUEST_URI: String = "https://auth.getmondo.co.uk"

        fun getAuthLink(clientId: String, redirectUri: URI, state: String): String {
            val escapedRedirectUri = UrlEscapers.urlPathSegmentEscaper().escape(redirectUri.toString())
            return "$DEFAULT_MONZO_AUTH_REQUEST_URI/?client_id=$clientId&redirect_uri=$escapedRedirectUri&response_type=code&state=$state"
        }
    }
}
