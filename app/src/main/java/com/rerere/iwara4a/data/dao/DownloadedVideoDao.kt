package com.rerere.iwara4a.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rerere.iwara4a.data.model.download.DownloadedVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedVideoDao {
    @Query("SELECT * FROM downloadedvideo ORDER BY downloadDate DESC")
    fun getAllDownloadedVideos(): Flow<List<DownloadedVideo>>

    @Query("SELECT * FROM downloadedvideo WHERE nid=:nid")
    suspend fun getVideo(nid: Int): DownloadedVideo?

    @Insert
    suspend fun insertVideo(video: DownloadedVideo)

    @Delete
    suspend fun delete(video: DownloadedVideo)
}