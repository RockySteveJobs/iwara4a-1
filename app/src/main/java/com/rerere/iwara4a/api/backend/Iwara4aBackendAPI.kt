package com.rerere.iwara4a.api.backend

import com.rerere.iwara4a.model.detail.video.VideoDetailFast
import retrofit2.http.*

interface Iwara4aBackendAPI {
    /**
     * 加载视频信息缓存
     */
    @GET("/video/{id}")
    suspend fun fetchVideoDetail(@Path("id") id: String): VideoDetailFast
}