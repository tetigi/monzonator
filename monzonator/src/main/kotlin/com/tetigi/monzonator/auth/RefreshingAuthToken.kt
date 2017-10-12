package com.tetigi.monzonator.auth

import com.palantir.remoting3.retrofit2.call
import com.palantir.tokens.auth.AuthHeader
import com.palantir.tokens.auth.BearerToken
import com.tetigi.monzonator.api.MonzoAuthService
import com.tetigi.monzonator.api.data.auth.TokenType
import com.tetigi.monzonator.api.requests.auth.RefreshTokenRequest
import com.tetigi.monzonator.api.responses.TokenResponse
import java.time.Clock
import java.time.Instant

/**
 * An automatically refreshing authorization token. May not be thread-safe (?)
 */
class RefreshingAuthToken(
        private val authService: MonzoAuthService,
        private val clientId: String,
        private val clientSecret: String,
        initialTokenResponse: TokenResponse
) : AuthHeader() {

    private var nextRefresh: Instant = Instant.MIN
    private var cachedBearerToken: BearerToken = createBearerToken(initialTokenResponse)
    private var refreshToken: String = initialTokenResponse.refreshToken

    private fun getClock(): Clock {
        return Clock.systemUTC()
    }

    private fun shouldRefresh(): Boolean {
        return getClock().instant().isAfter(nextRefresh)
    }

    private fun createBearerToken(tokenResponse: TokenResponse): BearerToken {
        if (tokenResponse.tokenType != TokenType.Bearer) {
            error("Cannot create AuthHeader from token not of BEARER type!")
        } else {
            return BearerToken.valueOf(tokenResponse.accessToken)
        }
    }

    @Synchronized
    override fun getBearerToken(): BearerToken {
        if (shouldRefresh()) {
            val tokenResponse = authService.refreshToken(
                    RefreshTokenRequest(clientId, clientSecret, refreshToken))
                    .call()

            cachedBearerToken = createBearerToken(tokenResponse)
            nextRefresh = getClock().instant().plusSeconds(tokenResponse.expiresIn.toLong() / 2)
            refreshToken = tokenResponse.refreshToken
        }

        return cachedBearerToken
    }
}