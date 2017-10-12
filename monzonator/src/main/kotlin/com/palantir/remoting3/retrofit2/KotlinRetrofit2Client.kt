package com.palantir.remoting3.retrofit2

import com.palantir.remoting3.clients.ClientConfiguration

/**
 * Kotlin version of the [Retrofit2Client] so that we can add the kotlin module to the object mapper.
 */
object KotlinRetrofit2Client {
    fun<T> create(serviceClass: Class<T>, userAgent: String, config: ClientConfiguration): T =
            KotlinRetrofit2ClientBuilder(config).build(serviceClass, userAgent)
}
