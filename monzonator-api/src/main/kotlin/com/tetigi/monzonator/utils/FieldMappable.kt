package com.tetigi.monzonator.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.palantir.remoting3.ext.jackson.ObjectMappers

/**
 * Wrapper object for making a data class into something that retrofit will accept for the @FieldMap annotation.
 * Bit of a hack, we essentially have to do our own serialization of the parameters into something that is compliant
 * with form-url-encoding.
 */
abstract class FieldMappable(private val map: Map<String, Any?>): Map<String, String> by wrap(map) {

    constructor(vararg pairs: Pair<String, Any?>): this(pairs.toMap())

    companion object {
        val MAPPER: ObjectMapper =
                ObjectMappers.newServerObjectMapper()
                        .registerKotlinModule()
                        .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

        fun wrap(map: Map<String, Any?>): Map<String, String> {
            return map.flatMap { (k, v) ->
                when (v) {
                    null -> emptyList()
                    is Map<*, *> -> v.map { (innerK, innerV) ->
                        if (innerV == null) {
                            null
                        } else {
                            "$k[${MAPPER.writeValueAsString(innerK).trim('"')}]" to MAPPER.writeValueAsString(innerV).trim('"')
                        }
                    }.filterNotNull()
                    else -> listOf(MAPPER.writeValueAsString(k).trim('"') to MAPPER.writeValueAsString(v).trim('"'))
                }
            }.toMap()
        }
    }
}

