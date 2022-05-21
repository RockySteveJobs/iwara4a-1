package com.rerere.iwara4a.data.model.playlist


import com.google.gson.annotations.SerializedName

data class PlaylistModifyResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
)