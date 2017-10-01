package com.palantir.remoting3.retrofit2

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import com.google.common.net.HttpHeaders
import com.palantir.remoting3.clients.CipherSuites
import com.palantir.remoting3.clients.ClientConfiguration
import com.palantir.remoting3.ext.jackson.ObjectMappers
import com.palantir.remoting3.tracing.Tracers
import com.palantir.remoting3.tracing.okhttp3.OkhttpTraceInterceptor
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.ConnectionSpec
import okhttp3.Credentials
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

// Stolen from palantir/http-remoting to support kotlin
internal class KotlinRetrofit2ClientBuilder(private val config: ClientConfiguration) {

    init {
        Preconditions.checkArgument(!config.uris().isEmpty(), "Cannot construct retrofit client with empty URI list")
    }

    fun <T> build(serviceClass: Class<T>, userAgent: String): T {
        val sanitizedUris = addTrailingSlashes(config.uris())
        val client = createOkHttpClient(userAgent, sanitizedUris)
        val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(sanitizedUris[0])
                .callFactory(AsyncCallTagCallFactory(client))
                .addConverterFactory(CborConverterFactory(
                        JacksonConverterFactory.create(OBJECT_MAPPER),
                        CBOR_OBJECT_MAPPER))
                .addConverterFactory(OptionalObjectToStringConverterFactory.INSTANCE)
                .addCallAdapterFactory(AsyncSerializableErrorCallAdapterFactory.INSTANCE)
                .build()
        return retrofit.create(serviceClass)
    }

    private fun createOkHttpClient(userAgent: String, uris: List<String>): OkHttpClient {
        val client = OkHttpClient.Builder()

        // SSL
        client.sslSocketFactory(config.sslSocketFactory(), config.trustManager())

        // proxy
        client.proxySelector(config.proxy())
        if (config.proxyCredentials().isPresent) {
            val basicCreds = config.proxyCredentials().get()
            val credentials = Credentials.basic(basicCreds.username(), basicCreds.password())
            client.proxyAuthenticator { _, response ->
                response.request().newBuilder()
                        .header(HttpHeaders.PROXY_AUTHORIZATION, credentials)
                        .build()
            }
        }

        // tracing
        client.interceptors().add(OkhttpTraceInterceptor.INSTANCE)

        // timeouts
        client.connectTimeout(config.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
        client.readTimeout(config.readTimeout().toMillis(), TimeUnit.MILLISECONDS)
        client.writeTimeout(config.writeTimeout().toMillis(), TimeUnit.MILLISECONDS)

        // retry configuration
        if (config.maxNumRetries() > 1) {
            client.addInterceptor(RetryInterceptor(config.maxNumRetries().toLong()))
        }

        client.addInterceptor(MultiServerRetryInterceptor.create(uris))
        client.addInterceptor(UserAgentInterceptor.of(userAgent))
        client.addInterceptor(SerializableErrorInterceptor.INSTANCE)

        // cipher setup
        client.connectionSpecs(createConnectionSpecs(config.enableGcmCipherSuites()))

        // increase default connection pool from 5 @ 5 minutes to 100 @ 10 minutes
        client.connectionPool(ConnectionPool(100, 10, TimeUnit.MINUTES))

        client.dispatcher(createDispatcher())

        return client.build()
    }

    companion object {
        private val executorService = Tracers.wrap(Dispatcher().executorService())

        private val CBOR_OBJECT_MAPPER = ObjectMappers
                .newCborClientObjectMapper()
                .registerKotlinModule()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(JodaModule())
        private val OBJECT_MAPPER = ObjectMappers
                .newClientObjectMapper()
                .registerKotlinModule()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(JodaModule())

        private fun addTrailingSlashes(uris: List<String>): List<String> {
            return Lists.transform(uris) { input -> if (input?.get(input.length - 1) == '/') input else input + "/" }
        }

        private fun createConnectionSpecs(enableGcmCipherSuites: Boolean): ImmutableList<ConnectionSpec> {
            return ImmutableList.of(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .cipherSuites(*if (enableGcmCipherSuites)
                                CipherSuites.allCipherSuites()
                            else
                                CipherSuites.fastCipherSuites())
                            .build(),
                    ConnectionSpec.CLEARTEXT)
        }

        private fun createDispatcher(): Dispatcher {
            val dispatcher = Dispatcher(executorService)

            dispatcher.maxRequests = 256
            dispatcher.maxRequestsPerHost = 256

            return dispatcher
        }
    }
}