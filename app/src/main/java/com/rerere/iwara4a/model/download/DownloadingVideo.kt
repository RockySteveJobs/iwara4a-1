package com.rerere.iwara4a.model.download

data class DownloadingVideo(
    val nid: Int,
    val fileName: String,
    val title: String,
    val downloadDate: Long,
    val preview: String,
    var progress: Float = 0f
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadingVideo

        if (nid != other.nid) return false

        return true
    }

    override fun hashCode(): Int {
        return nid
    }
}
