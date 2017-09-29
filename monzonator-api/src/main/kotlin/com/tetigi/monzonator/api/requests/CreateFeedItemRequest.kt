package com.tetigi.monzonator.api.requests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.palantir.remoting3.ext.jackson.ObjectMappers
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.data.FeedItemType
import java.net.URL

data class CreateFeedItemRequest(
        val type: FeedItemType,
        val url: URL?,
        val params: Map<String, Any>
) {
    companion object {
        private val MAPPER: ObjectMapper = ObjectMappers.newServerObjectMapper().registerKotlinModule()
        fun fromFeedItem(feedItem: FeedItem, url: URL? = null): CreateFeedItemRequest =
                CreateFeedItemRequest(
                        feedItem.type,
                        url,
                        MAPPER.readValue(MAPPER.writeValueAsString(feedItem)))
    }
}