package com.tetigi.monzonator

import com.palantir.remoting.api.config.ssl.SslConfiguration
import com.palantir.remoting3.clients.ClientConfigurations
import com.palantir.remoting3.config.ssl.SslSocketFactories
import com.palantir.remoting3.jaxrs.JaxRsClient
import com.palantir.remoting3.jaxrs.KotlinJaxRsClient
import com.palantir.tokens.auth.AuthHeader
import com.palantir.tokens.auth.BearerToken
import com.tetigi.monzonator.api.MonzoService
import java.nio.file.Paths

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val sslConfig = SslConfiguration.of(Paths.get("/home/tetigi/git/monzonator/var/security/truststore.jks"))
        val config = ClientConfigurations.of(
                listOf("https://api.monzo.com"),
                SslSocketFactories.createSslSocketFactory(sslConfig),
                SslSocketFactories.createX509TrustManager(sslConfig)
        )


        val monzo = KotlinJaxRsClient.create(MonzoService::class.java, "monzo", config)

        println("Hello, world!")

        val token = AuthHeader.of(BearerToken.valueOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaSI6Im9hdXRoY2xpZW50XzAwMDA5NFB2SU5ER3pUM2s2dHo4anAiLCJleHAiOjE1MDY2NDIwOTEsImlhdCI6MTUwNjYyMDQ5MSwianRpIjoidG9rXzAwMDA5T3owNmk0RnF4bWpqVUlZZE4iLCJ1aSI6InVzZXJfMDAwMDlDbzBTVjNBRXhncDU0NDR1WCIsInYiOiIyIn0.QckDR5aLi4pBjRb6UWgm6L2_IeJMNrYV2eQ9eNqSWAU"))

        println(monzo.getAccounts(token))
    }
}
