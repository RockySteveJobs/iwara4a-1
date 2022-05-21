package com.rerere.iwara4a.data.model.playlist

import com.rerere.iwara4a.data.model.index.MediaPreview

class PlaylistDetail(
    val title: String,
    val nid: Int,
    val videolist: List<MediaPreview>
)