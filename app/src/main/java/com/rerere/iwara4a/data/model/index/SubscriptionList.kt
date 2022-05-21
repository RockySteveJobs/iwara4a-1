package com.rerere.iwara4a.data.model.index

data class SubscriptionList(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val subscriptionList: List<MediaPreview>
)

