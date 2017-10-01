package com.tetigi.monzonator.api.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.palantir.remoting3.ext.jackson.ObjectMappers
import java.awt.Color
import java.net.URL

sealed class FeedItem(
        @JsonIgnore val type: FeedItemType
){
    private val MAPPER: ObjectMapper =
            ObjectMappers.newServerObjectMapper().registerKotlinModule()

    data class BasicFeedItem (
            val title: String,
            val imageUrl: URL,
            val body: String?,
            val backgroundColor: Color?,
            val titleColor: Color?,
            val bodyColor: Color?
    ): FeedItem(FeedItemType.basic)

    fun asParams(): Map<String, Any> =
            MAPPER.readValue(MAPPER.writeValueAsString(this))
}