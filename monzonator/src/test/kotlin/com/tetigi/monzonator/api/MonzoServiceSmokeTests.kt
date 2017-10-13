package com.tetigi.monzonator.api

import com.palantir.remoting.api.config.ssl.SslConfiguration
import com.palantir.remoting3.clients.ClientConfigurations
import com.palantir.remoting3.config.ssl.SslSocketFactories
import com.palantir.remoting3.retrofit2.KotlinRetrofit2Client
import com.palantir.remoting3.retrofit2.call
import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature
import com.palantir.tokens.auth.AuthHeader
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.requests.CreateFeedItemRequest
import com.tetigi.monzonator.api.requests.RegisterWebhookRequest
import com.tetigi.monzonator.resources.MonzoRefreshingTokenResource
import io.dropwizard.Application
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment
import org.junit.Test
import java.net.URI
import java.nio.file.Paths

class MonzoServiceSmokeTests: Application<Configuration>() {

    override fun run(configuration: Configuration, env: Environment) {
        env.jersey().register(HttpRemotingJerseyFeature.INSTANCE)
        env.jersey().register(tokenService)
    }

    companion object {
        private val TEST_URI: URI = URI("http://www.nyan.cat/cats/original.gif")
        private val monzoService: MonzoService by lazy { monzoService() }
        private val authService: MonzoAuthService by lazy { authService() }
        private val tokenService: MonzoRefreshingTokenResource by lazy {
            MonzoRefreshingTokenResource(
                    System.getProperty("clientId"),
                    System.getProperty("clientSecret"),
                    authService,
                    URI("https://google.com"),
                    URI("http://localhost:8080")
            )
        }
        private fun monzoService(): MonzoService {
            val sslConfig = SslConfiguration.of(Paths.get("var/security/truststore.jks"))
            val config = ClientConfigurations.of(
                    listOf(MonzoService.DEFAULT_MONZO_URI),
                    SslSocketFactories.createSslSocketFactory(sslConfig),
                    SslSocketFactories.createX509TrustManager(sslConfig)
            )

            return KotlinRetrofit2Client.create(MonzoService::class.java, "monzo", config)
        }

        private fun authService(): MonzoAuthService {
            val sslConfig = SslConfiguration.of(Paths.get("var/security/truststore.jks"))
            val config = ClientConfigurations.of(
                    listOf(MonzoService.DEFAULT_MONZO_URI),
                    SslSocketFactories.createSslSocketFactory(sslConfig),
                    SslSocketFactories.createX509TrustManager(sslConfig)
            )

            return KotlinRetrofit2Client.create(MonzoAuthService::class.java, "auth", config)
        }

        private val token: AuthHeader by lazy {
            MonzoServiceSmokeTests().run("server")
            tokenService.startBlockingAuthTokenRequest()
            tokenService.getRefreshingToken()
        }
    }

    private fun anAccountId(): String {
        val response = monzoService.getAccounts(token).call()
        if (response.accounts.isEmpty()) {
            error("Could not find any accounts to test with :(")
        } else {
            return response.accounts.first().id
        }
    }

    private fun aTransactionId(): String {
        val response = monzoService.getTransactions(token, anAccountId()).call()
        if (response.transactions.isEmpty()) {
            error("Could not find any transactions to test with :(")
        } else {
            return response.transactions.first().id
        }
    }

    private fun aWebhookId(): String {
        val response = monzoService.getWebhooks(token, anAccountId()).call()
        return if (response.webhooks.isEmpty()) {
            monzoService.registerWebhook(token, RegisterWebhookRequest(anAccountId(), TEST_URI))
            val newResponse = monzoService.getWebhooks(token, anAccountId()).call()
            if (newResponse.webhooks.isEmpty()) {
                error("Could not create a new webhook to test with :(")
            } else {
                newResponse.webhooks.first().id
            }
        } else {
            response.webhooks.first().id
        }
    }

    @Test
    fun testGetAccounts() {
        monzoService.getAccounts(token).call()
    }

    @Test
    fun testGetBalance() {
        monzoService.getBalance(token, anAccountId()).call()
    }

    @Test
    fun testGetTransaction() {
        monzoService.getTransaction(token, aTransactionId()).call()
    }

    @Test
    fun testGetTransactions() {
        monzoService.getTransactions(token, anAccountId()).call()
    }

    @Test
    fun testAnnotateTransaction() {
        // TODO()
        error("Not implemented")
    }

    @Test
    fun testCreateFeedItem() {
        monzoService.createFeedItem(
                token,
                CreateFeedItemRequest.fromFeedItem(
                        anAccountId(),
                        FeedItem.BasicFeedItem(
                                "Hullo from monzonator",
                                TEST_URI,
                                "THIS IS REAL LIFE",
                                null,
                                null,
                                null
                        ),
                        URI("http://www.nyan.cat/")
                )
        ).call()
    }

    @Test
    fun testRegisterWebhook() {
        monzoService.registerWebhook(
                token,
                RegisterWebhookRequest(
                        anAccountId(),
                        TEST_URI
                )
        ).call()
    }

    @Test
    fun testGetWebhooks() {
        monzoService.getWebhooks(token, anAccountId()).call()
    }

    @Test
    fun testDeleteWebhook() {
        monzoService.deleteWebhook(token, aWebhookId()).call()
    }

    @Test
    fun testUploadAttachment() {
        // TODO
        error("Not implemented")
    }

    @Test
    fun testRegisterAttachment() {
        // TODO
        error("Not implemented")
    }

    @Test
    fun testDeregisterAttachment() {
        // TODO
        error("Not implemented")
    }
}