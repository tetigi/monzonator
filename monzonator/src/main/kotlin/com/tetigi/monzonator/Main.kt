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
import com.tetigi.monzonator.resources.MonzoRefreshingTokenResource
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import java.net.URL
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
            val sslConfig = SslConfiguration.of(Paths.get("monzonator/var/security/truststore.jks"))
            val config = ClientConfigurations.of(
                    listOf(MonzoService.DEFAULT_MONZO_URL),
                    SslSocketFactories.createSslSocketFactory(sslConfig),
                    SslSocketFactories.createX509TrustManager(sslConfig)
            )

            val authService = KotlinRetrofit2Client.create(MonzoAuthService::class.java, "auth", config)

            val tokenService = MonzoRefreshingTokenResource(
                    "oauthclient_00009PQ9yINphwvcOOzTGL",
                    args[1],
                    authService,
                    URL("http://localhost:8080")
            )

            Main(tokenService).run(*args.take(1).toTypedArray())

            tokenService.startAuthTokenRequest()

            val monzo = KotlinRetrofit2Client.create(MonzoService::class.java, "monzo", config)

            println(monzo.getAccounts(tokenService.getRefreshingToken()).call())
        }
    }
}
