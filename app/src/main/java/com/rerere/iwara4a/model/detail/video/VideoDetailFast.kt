package com.rerere.iwara4a.model.detail.video

import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType

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

fun VideoDetailFast.toMediaPreview() = MediaPreview(
    title = this.title,
    author = this.authorName,
    previewPic = this.preview,
    likes = this.likes,
    watchs = this.watchs,
    type = MediaType.VIDEO,
    mediaId = this.id,
    private = false
)