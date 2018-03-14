package com.tetigi.monzonator.api

import com.tetigi.monzonator.api.requests.auth.AuthorizationRequest
import com.tetigi.monzonator.api.requests.auth.RefreshTokenRequest
import com.tetigi.monzonator.api.responses.TokenResponse
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

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
}
