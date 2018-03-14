package com.tetigi.monzonator.resources

import com.palantir.remoting3.retrofit2.call
import com.palantir.tokens.auth.AuthHeader
import com.tetigi.monzonator.api.MonzoAuthService
import com.tetigi.monzonator.api.MonzoRefreshingTokenService
import com.tetigi.monzonator.api.requests.auth.AuthorizationRequest
import com.tetigi.monzonator.api.utils.Auth
import com.tetigi.monzonator.auth.RefreshingAuthToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.ws.rs.core.Response

class MonzoRefreshingTokenResource(
        private val clientId: String,
        private val clientSecret: String,
        private val authService: MonzoAuthService,
        private val postAuthRedirect: URI,
        serviceLocation: URI
): MonzoRefreshingTokenService {

    private val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    private val state: AtomicReference<String?> = AtomicReference(null)
    private val authToken: AtomicReference<AuthHeader?> = AtomicReference(null)
    private val redirectUri: URI = URI("$serviceLocation/${Auth.CALLBACK_URI}")

    /**
     * Initiates the auth token request workflow and blocks until it is completed
     */
    fun startBlockingAuthTokenRequest() {
        val state = UUID.randomUUID().toString()
        this.state.set(state)
        val authLink = Auth.getAuthLink(
                clientId,
                redirectUri,
                state
        )

        // Right now this will just log to console, but should eventually do something useful I would imagine
        LOG.info("Auth URI -> $authLink")

        while (authToken.get() == null) {
            LOG.info("No token yet - sleeping..")
            Thread.sleep(5000)
        }
    }

    override fun authorizationCallback(authorizationCode: String, stateToken: String): Response {
        if (stateToken != this.state.get()) {
            error("STATE TOKENS DON'T MATCH, ABORT! Expected ${this.state.get()} but got $stateToken")
        }

        LOG.info("Got auth callback with $authorizationCode and state $stateToken")

        val tokenResponse = authService.authorizeToken(
                AuthorizationRequest(
                        clientId,
                        clientSecret,
                        redirectUri,
                        authorizationCode)
        ).call()

        LOG.info("Got token response: $tokenResponse")

        authToken.set(RefreshingAuthToken(authService, clientId, clientSecret, tokenResponse))

        return Response.temporaryRedirect(postAuthRedirect).build()
    }

    override fun getRefreshingToken(): AuthHeader =
            checkNotNull(authToken.get(), { "Auth token has not been created yet! Has the auth flow been completed yet?" })
}