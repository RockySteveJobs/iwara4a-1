package com.rerere.iwara4a.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.model.download.DownloadingVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadedVideoDao {
    @Query("SELECT * FROM downloadedvideo ORDER BY downloadDate DESC")
    fun getAllDownloadedVideos() : Flow<List<DownloadedVideo>>

    @Query("SELECT * FROM downloadedvideo WHERE nid=:nid")
    fun getVideo(nid: Int) : DownloadedVideo?

    @Insert
    fun insertVideo(video: DownloadedVideo)

    @Delete
    fun delete(video: DownloadedVideo)
}