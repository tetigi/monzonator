package com.tetigi.monzonator.api

import com.palantir.remoting.api.config.ssl.SslConfiguration
import com.palantir.remoting3.clients.ClientConfigurations
import com.palantir.remoting3.config.ssl.SslSocketFactories
import com.palantir.remoting3.retrofit2.KotlinRetrofit2Client
import com.palantir.remoting3.retrofit2.call
import com.palantir.tokens.auth.AuthHeader
import com.palantir.tokens.auth.BearerToken
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.requests.CreateFeedItemRequest
import com.tetigi.monzonator.api.requests.RegisterWebhookRequest
import org.junit.Test
import java.net.URL
import java.nio.file.Paths
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class MonzoServiceSmokeTests {

    companion object {
        private val TEST_URL: URL = URL("http://www.nyan.cat/cats/original.gif")
    }

    private fun monzoService(): MonzoService {
        val sslConfig = SslConfiguration.of(Paths.get("var/security/truststore.jks"))
        val config = ClientConfigurations.of(
                listOf(MonzoService.DEFAULT_MONZO_URL),
                //listOf("http://localhost:8080"),
                SslSocketFactories.createSslSocketFactory(sslConfig),
                SslSocketFactories.createX509TrustManager(sslConfig)
        )

        return KotlinRetrofit2Client.create(MonzoService::class.java, "monzo", config)

    }

    private fun token(): AuthHeader =
        AuthHeader.of(BearerToken.valueOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaSI6Im9hdXRoY2xpZW50XzAwMDA5NFB2SU5ER3pUM2s2dHo4anAiLCJleHAiOjE1MDcwNjU4MjIsImlhdCI6MTUwNzA0NDIyMiwianRpIjoidG9rXzAwMDA5UDlBTk5oSm1lQ3g2V3FuV1QiLCJ1aSI6InVzZXJfMDAwMDlDbzBTVjNBRXhncDU0NDR1WCIsInYiOiIyIn0.UzkJx3l3fZv9ocZP8uqbaJfG5aZv3puuOM0dOn39ezc"))

    private fun anAccountId(): String {
        val response = monzoService().getAccounts(token()).call()
        if (response.accounts.isEmpty()) {
            error("Could not find any accounts to test with :(")
        } else {
            return response.accounts.first().id
        }
    }

    private fun aTransactionId(): String {
        val response = monzoService().getTransactions(token(), anAccountId()).call()
        if (response.transactions.isEmpty()) {
            error("Could not find any transactions to test with :(")
        } else {
            return response.transactions.first().id
        }
    }

    private fun aWebhookId(): String {
        val response = monzoService().getWebhooks(token(), anAccountId()).call()
        return if (response.webhooks.isEmpty()) {
            monzoService().registerWebhook(token(), RegisterWebhookRequest(anAccountId(), TEST_URL))
            val newResponse = monzoService().getWebhooks(token(), anAccountId()).call()
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
        monzoService().getAccounts(token()).call()
    }

    @Test
    fun testGetBalance() {
        monzoService().getBalance(token(), anAccountId()).call()
    }

    @Test
    fun testGetTransaction() {
        monzoService().getTransaction(token(), aTransactionId()).call()
    }

    @Test
    fun testGetTransactions() {
        monzoService().getTransactions(token(), anAccountId()).call()
    }

    @Test
    fun testAnnotateTransaction() {
        // TODO()
        error("Not implemented")
    }

    @Test
    fun testCreateFeedItem() {
        monzoService().createFeedItem(
                token(),
                CreateFeedItemRequest.fromFeedItem(
                        anAccountId(),
                        FeedItem.BasicFeedItem(
                                "Hullo from monzonator",
                                TEST_URL,
                                "THIS IS REAL LIFE",
                                null,
                                null,
                                null
                        ),
                        URL("http://www.nyan.cat/")
                )
        ).call()
    }

    @Test
    fun testRegisterWebhook() {
        monzoService().registerWebhook(
                token(),
                RegisterWebhookRequest(
                        anAccountId(),
                        TEST_URL
                )
        ).call()
    }

    @Test
    fun testGetWebhooks() {
        monzoService().getWebhooks(token(), anAccountId()).call()
    }

    @Test
    fun testDeleteWebhook() {
        monzoService().deleteWebhook(token(), aWebhookId()).call()
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