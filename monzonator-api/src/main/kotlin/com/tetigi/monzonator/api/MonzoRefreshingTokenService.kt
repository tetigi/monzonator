package com.tetigi.monzonator.api

import com.palantir.tokens.auth.AuthHeader
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Path("/oauth")
interface MonzoRefreshingTokenService {
    @GET
    @Path("/callback")
    fun authorizationCallback(
            @QueryParam("code") authorizationCode: String,
            @QueryParam("state") stateToken: String
    ): Response

    /**
     * Returns a valid token that is self refreshing.
     * If the token has yet to be created (because the auth flow has not been completed yet) it will throw
     * an [IllegalStateException].
     */
    fun getRefreshingToken(): AuthHeader

    companion object {
        const val CALLBACK_URI: String = "oauth/callback"
    }
}