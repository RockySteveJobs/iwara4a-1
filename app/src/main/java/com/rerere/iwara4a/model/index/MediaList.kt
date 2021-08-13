package com.rerere.iwara4a.model.index

import androidx.compose.runtime.Composable

data class MediaList(
    val currentPage: Int,
    val hasNext: Boolean,
    val mediaList: List<MediaPreview>
)

data class MediaQueryParam(
    var sortType: SortType,
    var filters: MutableSet<String>
)

enum class SortType(val value: String) {
    DATE("date"),
    VIEWS("views"),
    LIKES("likes")
}

data class MediaFilter(
    val type: String,
    val value: List<String>
){
    constructor(type: String, vararg values: String) : this(type, values.toList())
}

val MEDIA_FILTERS = listOf(
    MediaFilter("type", "video", "image"),
    MediaFilter("created", "2021","2020","2019","2018"),
    MediaFilter("field_categories", "6", "16190", "31264")
)

@Composable
fun Pair<String, String>.filterName() = when(this.first){
    "type" -> "类型: " + when(this.second){
        "video" -> "视频"
        "image" -> "图片"
        else -> this.second
    }
    "created" -> "上传日期: ${this.second}"
    "field_categories" -> "类型: " + when(this.second){
        "6" -> "Vocaloid"
        "16190" -> "虚拟主播"
        "31264" -> "原神"
        else -> "(${this.second})"
    }
    else -> "${this.first}: ${this.second}"
}

@Composable
fun SortType.displayName() = when (this) {
    SortType.DATE -> "日期"
    SortType.VIEWS -> "播放量"
    SortType.LIKES -> "喜欢"
}