package com.tetigi.monzonator.api

import com.palantir.remoting.api.config.ssl.SslConfiguration
import com.palantir.remoting3.clients.ClientConfigurations
import com.palantir.remoting3.config.ssl.SslSocketFactories
import com.palantir.remoting3.jaxrs.KotlinJaxRsClient
import com.palantir.tokens.auth.AuthHeader
import com.palantir.tokens.auth.BearerToken
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.requests.CreateFeedItemRequest
import com.tetigi.monzonator.api.requests.RegisterWebhookRequest
import com.tetigi.monzonator.api.requests.UploadAttachmentRequest
import java.net.URL
import java.nio.file.Paths


class MonzoServiceSmokeTests {

    private val TEST_URL: URL = URL("http://google.com")

    private fun monzoService(): MonzoService {
        val sslConfig = SslConfiguration.of(Paths.get("var/security/truststore.jks"))
        val config = ClientConfigurations.of(
                listOf("https://api.monzo.com"),
                SslSocketFactories.createSslSocketFactory(sslConfig),
                SslSocketFactories.createX509TrustManager(sslConfig)
        )

        return KotlinJaxRsClient.create(MonzoService::class.java, "monzo", config)

    }

    private fun token(): AuthHeader =
        AuthHeader.of(BearerToken.valueOf("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaSI6Im9hdXRoY2xpZW50XzAwMDA5NFB2SU5ER3pUM2s2dHo4anAiLCJleHAiOjE1MDY2NDIwOTEsImlhdCI6MTUwNjYyMDQ5MSwianRpIjoidG9rXzAwMDA5T3owNmk0RnF4bWpqVUlZZE4iLCJ1aSI6InVzZXJfMDAwMDlDbzBTVjNBRXhncDU0NDR1WCIsInYiOiIyIn0.QckDR5aLi4pBjRb6UWgm6L2_IeJMNrYV2eQ9eNqSWAU"))

    private fun anAccountId(): String {
        val response = monzoService().getAccounts(token())
        if (response.accounts.isEmpty()) {
            error("Could not find any accounts to test with :(")
        } else {
            return response.accounts.first().id
        }
    }

    private fun aTransactionId(): String {
        val response = monzoService().getTransactions(token(), anAccountId())
        if (response.transactions.isEmpty()) {
            error("Could not find any transactions to test with :(")
        } else {
            return response.transactions.first().id
        }
    }

    private fun aWebhookId(): String {
        val response = monzoService().getWebhooks(token(), anAccountId())
        if (response.webhooks.isEmpty()) {
            monzoService().registerWebhook(token(), RegisterWebhookRequest(anAccountId(), TEST_URL))
            val newResponse = monzoService().getWebhooks(token(), anAccountId())
            if (newResponse.webhooks.isEmpty()) {
                error("Could not create a new webhook to test with :(")
            } else {
                return newResponse.webhooks.first().id
            }
        } else {
            return response.webhooks.first().id
        }
    }

    fun testGetAccounts() {
        monzoService().getAccounts(token())
    }

    fun testGetBalance() {
        monzoService().getBalance(token(), anAccountId())
    }

    fun testGetTransaction() {
        monzoService().getTransaction(token(), aTransactionId())
    }

    fun testGetTransactions() {
        monzoService().getTransactions(token(), anAccountId())
    }

    fun testAnnotateTransaction() {
        // TODO()
        error("Not implemented")
    }

    fun testCreateFeedItem() {
        monzoService().createFeedItem(
                token(),
                anAccountId(),
                CreateFeedItemRequest.fromFeedItem(
                        FeedItem.BasicFeedItem(
                                "test",
                                TEST_URL,
                                null,
                                null,
                                null,
                                null
                        ),
                        null
                )
        )
    }

    fun testRegisterWebhook() {
        monzoService().registerWebhook(
                token(),
                RegisterWebhookRequest(
                        anAccountId(),
                        TEST_URL
                )
        )
    }

    fun testGetWebhooks() {
        monzoService().getWebhooks(token(), anAccountId())
    }

    fun testDeleteWebhook() {
        monzoService().deleteWebhook(token(), aWebhookId())
    }

    fun testUploadAttachment() {
        // TODO
        error("Not implemented")
    }

    fun testRegisterAttachment() {
        // TODO
        error("Not implemented")
    }

    fun testDeregisterAttachment() {
        // TODO
        error("Not implemented")
    }
}