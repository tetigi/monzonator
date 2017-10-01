package com.tetigi.monzonator.api.requests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.palantir.remoting3.ext.jackson.ObjectMappers
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.data.FeedItemType
import java.net.URL
import javax.ws.rs.core.Form
import javax.ws.rs.core.MultivaluedHashMap

data class CreateFeedItemRequest(
        val accountId: String,
        val type: FeedItemType,
        val url: URL?,
        val params: Map<String, Any>
): Form(
        MultivaluedHashMap<String, String>().apply {
            putAll(MAPPER.readValue(MAPPER.writeValueAsString(this)))
        }) {
    companion object {
        private val MAPPER: ObjectMapper = ObjectMappers.newServerObjectMapper().registerKotlinModule()
        fun fromFeedItem(accountId: String, feedItem: FeedItem, url: URL? = null): CreateFeedItemRequest =
                CreateFeedItemRequest(
                        accountId,
                        feedItem.type,
                        url,
                        MAPPER.readValue(MAPPER.writeValueAsString(feedItem)))
    }
}