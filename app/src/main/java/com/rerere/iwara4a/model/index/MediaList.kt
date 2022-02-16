package com.rerere.iwara4a.model.index

data class MediaList(
    val currentPage: Int,
    val hasNext: Boolean,
    val mediaList: List<MediaPreview>
)