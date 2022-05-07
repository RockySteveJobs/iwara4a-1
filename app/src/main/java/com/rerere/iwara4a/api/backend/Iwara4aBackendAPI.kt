package com.rerere.iwara4a.api.backend

import com.rerere.iwara4a.model.detail.video.VideoDetailFast
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Iwara4aBackendAPI {
    @GET("/video/{id}")
    suspend fun fetchVideoDetail(@Path("id") id: String): VideoDetailFast

    @GET("/recommend_tags")
    suspend fun getAllRecommendTags(): List<String>

    @GET("/recommend")
    suspend fun recommend(
        @Query("tags") tags: String,
        @Query("limit") limit: Int
    ): List<VideoDetailFast>
}