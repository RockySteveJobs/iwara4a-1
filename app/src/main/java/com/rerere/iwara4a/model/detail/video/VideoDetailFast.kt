package com.rerere.iwara4a.model.detail.video

data class VideoDetailFast(
    // 视频信息
    val id: String,
    val nid: Int,
    val title: String,
    val likes: String,
    val watchs: String,
    val postDate: String,
    val description: String,
    val preview: String,

    // 视频作者信息
    val authorPic: String,
    val authorName: String,
    val authorId: String
)