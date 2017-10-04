package com.tetigi.monzonator.api

import com.palantir.tokens.auth.AuthHeader
import retrofit2.http.GET
import retrofit2.http.Query

interface MonzoRefreshingTokenService {
    @GET(CALLBACK_URL)
    fun authorizationCallback(
            @Query("code") authorizationCode: String,
            @Query("state") stateToken: String
    )

    /**
     * Returns a valid token that is self refreshing.
     * If the token has yet to be created (because the auth flow has not been completed yet) it will throw
     * an [IllegalStateException].
     */
    fun getRefreshingToken(): AuthHeader

    companion object {
        const val CALLBACK_URL: String = "oauth/callback"
    }
}