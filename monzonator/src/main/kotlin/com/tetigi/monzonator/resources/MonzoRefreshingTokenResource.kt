package com.tetigi.monzonator.resources

import com.palantir.remoting3.retrofit2.call
import com.palantir.tokens.auth.AuthHeader
import com.tetigi.monzonator.api.MonzoAuthService
import com.tetigi.monzonator.api.MonzoRefreshingTokenService
import com.tetigi.monzonator.api.data.auth.GrantType
import com.tetigi.monzonator.api.requests.auth.AuthorizationRequest
import com.tetigi.monzonator.auth.RefreshingAuthToken
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class MonzoRefreshingTokenResource(
        private val clientId: String,
        private val clientSecret: String,
        private val redirectUri: URL,
        private val authService: MonzoAuthService
): MonzoRefreshingTokenService {

    private val LOG: Logger = LoggerFactory.getLogger(this::class.java)

    private val state: AtomicReference<String?> = AtomicReference(null)
    private val authToken: AtomicReference<AuthHeader?> = AtomicReference(null)

    fun startAuthTokenRequest(serviceLocation: URL) {
        val state = UUID.randomUUID().toString()
        this.state.set(state)
        val authLink = MonzoAuthService.getAuthLink(
                clientId,
                URL("$serviceLocation/${MonzoRefreshingTokenService.CALLBACK_URL}"),
                state
        )

        // Right now this will just log to console, but should eventually do something useful I would imagine
        LOG.info("Auth URL -> $authLink")
    }

    override fun authorizationCallback(authorizationCode: String, stateToken: String) {
        if (stateToken != this.state.get()) {
            error("STATE TOKENS DON'T MATCH, ABORT!")
        }

        val tokenResponse = authService.authorizeToken(
                AuthorizationRequest(
                        GrantType.AUTHORIZATION_CODE,
                        clientId,
                        clientSecret,
                        redirectUri,
                        authorizationCode)
        ).call()

        authToken.set(RefreshingAuthToken(authService, clientId, clientSecret, tokenResponse))
    }

    override fun getRefreshingToken(): AuthHeader =
            checkNotNull(authToken.get(), { "Auth token has not been created yet! Has the auth flow been completed yet?" })
}