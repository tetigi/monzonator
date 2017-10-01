package com.palantir.remoting3.retrofit2

import com.palantir.remoting3.clients.ClientConfiguration

object KotlinRetrofit2Client {
    fun<T> create(serviceClass: Class<T>, userAgent: String, config: ClientConfiguration): T =
            KotlinRetrofit2ClientBuilder(config).build(serviceClass, userAgent)
}
