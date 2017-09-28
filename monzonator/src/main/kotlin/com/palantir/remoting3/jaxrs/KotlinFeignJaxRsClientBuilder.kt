package com.palantir.remoting3.jaxrs

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.palantir.remoting3.clients.ClientConfiguration
import com.palantir.remoting3.ext.jackson.ObjectMappers

internal class KotlinFeignJaxRsClientBuilder(config: ClientConfiguration)
    : AbstractFeignJaxRsClientBuilder(config) {

    private val JSON_OBJECT_MAPPER: ObjectMapper =
            ObjectMappers.newClientObjectMapper()
                    .registerKotlinModule()
                    .registerModule(JodaModule())
    private val CBOR_OBJECT_MAPPER: ObjectMapper =
            ObjectMappers.newCborClientObjectMapper()
                    .registerKotlinModule()
                    .registerModule(JodaModule())

    override fun getObjectMapper(): ObjectMapper = JSON_OBJECT_MAPPER

    override fun getCborObjectMapper(): ObjectMapper = CBOR_OBJECT_MAPPER
}
