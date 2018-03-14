package com.tetigi.monzonator

import com.palantir.remoting.api.config.ssl.SslConfiguration
import com.palantir.remoting3.clients.ClientConfigurations
import com.palantir.remoting3.config.ssl.SslSocketFactories
import com.palantir.remoting3.retrofit2.KotlinRetrofit2Client
import com.palantir.remoting3.retrofit2.call
import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature
import com.tetigi.monzonator.api.MonzoAuthService
import com.tetigi.monzonator.api.MonzoRefreshingTokenService
import com.tetigi.monzonator.api.MonzoService
import com.tetigi.monzonator.api.utils.Monzo
import com.tetigi.monzonator.resources.MonzoRefreshingTokenResource
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import java.net.URI
import java.nio.file.Paths

class Main(
        private val tokenService: MonzoRefreshingTokenService
): Application<Configuration>() {

    override fun run(configuration: Configuration, env: Environment) {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE)
        env.jersey().register(tokenService)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            // Create ssl and client configurations
            val sslConfig = SslConfiguration.of(Paths.get("monzonator/var/security/truststore.jks"))
            val config = ClientConfigurations.of(
                    listOf(Monzo.DEFAULT_MONZO_URI),
                    SslSocketFactories.createSslSocketFactory(sslConfig),
                    SslSocketFactories.createX509TrustManager(sslConfig)
            )

            // Create the auth client
            val authService = KotlinRetrofit2Client.create(MonzoAuthService::class.java, "auth", config)

            // Create the monzo client
            val monzo = KotlinRetrofit2Client.create(MonzoService::class.java, "monzo", config)

            // The token service handles redirects and authorization workflow
            val tokenService = MonzoRefreshingTokenResource(
                    args[1],
                    args[2],
                    authService,
                    URI("https://google.com"),
                    URI("http://localhost:8080")
            )

            // Args: 0: server, 1: <client_id>, 2: <client_secret>
            Main(tokenService).run(*args.take(1).toTypedArray())

            // Start the auth token workflow
            tokenService.startBlockingAuthTokenRequest()

            // Example call
            println(monzo.getTransactions(tokenService.getRefreshingToken(), "acc_00009OgKrWja7BhHGyzXOL").call().transactions.reversed().take(10))
        }
    }
}
