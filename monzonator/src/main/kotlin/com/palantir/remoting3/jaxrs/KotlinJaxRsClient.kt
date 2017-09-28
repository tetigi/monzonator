package com.palantir.remoting3.jaxrs

import com.palantir.remoting3.clients.ClientConfiguration

object KotlinJaxRsClient {
    fun<T> create(serviceClass: Class<T>, userAgent: String, config: ClientConfiguration): T =
            KotlinFeignJaxRsClientBuilder(config).build(serviceClass, userAgent)
}
