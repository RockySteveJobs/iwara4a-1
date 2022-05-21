package com.rerere.iwara4a.data.model.detail.image

data class ImageDetail(
    val id: String,
    val title: String,
    val imageLinks: List<String>,
    val description: String,

    val authorId: String,
    val authorName: String,
    val authorProfilePic: String,

    val watchs: String
)