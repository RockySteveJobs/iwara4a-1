package com.rerere.iwara4a.api.backend

import com.rerere.iwara4a.model.detail.video.VideoDetailFast
import retrofit2.http.GET
import retrofit2.http.Path

interface Iwara4aBackendAPI {
    @GET("/video/{id}")
    suspend fun fetchVideoDetail(@Path("id") id: String): VideoDetailFast
}