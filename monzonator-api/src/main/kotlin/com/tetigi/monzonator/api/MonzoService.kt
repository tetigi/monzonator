package com.tetigi.monzonator.api

import com.palantir.tokens.auth.AuthHeader
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface MonzoService {
    /**
     * Returns a list of accounts owned by the currently authorised user.
     */
    @GET
    @Path("/accounts")
    fun listAccounts(@HeaderParam("Authorization") authHeader: AuthHeader): List<Account>
}