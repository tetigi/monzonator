package com.tetigi.monzonator.api

import com.fasterxml.jackson.annotation.JsonIgnore
import java.awt.Color
import java.net.URL

sealed class FeedItem(
        @JsonIgnore val type: FeedItemType
){

    data class BasicFeedItem (
            val title: String,
            val imageUrl: URL,
            val body: String?,
            val backgroundColor: Color,
            val titleColor: Color,
            val bodyColor: Color
    ): FeedItem(FeedItemType.BASIC)

}