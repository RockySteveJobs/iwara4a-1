package com.rerere.iwara4a.model.playlist


import com.google.gson.annotations.SerializedName

class PlaylistPreview : ArrayList<PlaylistPreview.PlaylistPreviewItem>(){
    data class PlaylistPreviewItem(
        @SerializedName("in_list")
        val inList: Int,
        @SerializedName("nid")
        val nid: String,
        @SerializedName("title")
        val title: String
    ){
        val inIt
            get() = inList != 0
    }
}