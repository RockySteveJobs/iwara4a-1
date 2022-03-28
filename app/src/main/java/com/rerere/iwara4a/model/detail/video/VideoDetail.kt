package com.rerere.iwara4a.model.detail.video

import com.rerere.iwara4a.model.comment.CommentPostParam

data class VideoDetail(
    // 视频信息
    val id: String,
    val nid: Int,
    val title: String,
    var videoLinks: VideoLink,
    val likes: String,
    val watchs: String,
    val postDate: String,
    val description: String,
    val preview: String,

    val comments: Int,
    val commentPostParam: CommentPostParam,

    // 视频作者信息
    val authorPic: String,
    val authorName: String,
    val authorId: String,

    // 作者的更多视频
    val moreVideo: List<MoreVideo>,
    // 相似视频
    val recommendVideo: List<MoreVideo>,

    // 是否关注
    val follow: Boolean,
    // 关注链接
    val followLink: String,

    // 是否喜欢
    var isLike: Boolean,
    val likeLink: String
) {
    companion object {
        val DELETED = VideoDetail(
            "",
            0,
            "视频不存在",
            VideoLink(),
            "",
            "",
            "",
            "",
            "",
            0,
            CommentPostParam.Default,
            "",
            "",
            "",
            emptyList(),
            emptyList(),
            true,
            "",
            true,
            ""
        )

        val PRIVATE = VideoDetail(
            "",
            0,
            "私人视频",
            VideoLink(),
            "",
            "",
            "",
            "",
            "",
            0,
            CommentPostParam.Default,
            "",
            "",
            "",
            emptyList(),
            emptyList(),
            true,
            "",
            true,
            ""
        )
    }
}

data class MoreVideo(
    val id: String,
    val title: String,
    val pic: String,
    val watchs: String,
    val likes: String
)