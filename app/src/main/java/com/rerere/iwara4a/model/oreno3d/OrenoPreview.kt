package com.rerere.iwara4a.model.oreno3d

data class OrenoPreview(
    val title: String,
    val author: String,
    val pic: String,
    val like: String,
    val watch: String,
    val id: Int
)

data class OrenoPreviewList(
    val list: List<OrenoPreview>,
    val currentPage: Int,
    val hasNext: Boolean
)