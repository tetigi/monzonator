package com.tetigi.monzonator.api.requests

import com.fasterxml.jackson.module.kotlin.readValue
import com.tetigi.monzonator.api.data.FeedItem
import com.tetigi.monzonator.api.data.FeedItemType
import com.tetigi.monzonator.utils.FieldMappable
import java.net.URL

data class CreateFeedItemRequest(
        val accountId: String,
        val type: FeedItemType,
        val url: URL?,
        val params: Map<String, String?>
): FieldMappable(
        "account_id" to accountId,
        "type" to type,
        "url" to url,
        "params" to params
) {
    companion object {
        fun fromFeedItem(accountId: String, feedItem: FeedItem, url: URL? = null): CreateFeedItemRequest =
                CreateFeedItemRequest(
                        accountId,
                        feedItem.type,
                        url,
                        MAPPER.readValue(MAPPER.writeValueAsString(feedItem)))
    }
}
