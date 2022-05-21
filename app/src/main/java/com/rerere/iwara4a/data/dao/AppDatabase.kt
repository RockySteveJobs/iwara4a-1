package com.rerere.iwara4a.data.dao

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rerere.iwara4a.data.model.download.DownloadedVideo
import com.rerere.iwara4a.data.model.follow.FollowUser
import com.rerere.iwara4a.data.model.history.HistoryData

@Database(
    entities = [DownloadedVideo::class, HistoryData::class, FollowUser::class],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDownloadedVideoDao(): DownloadedVideoDao

    abstract fun getHistoryDao(): HistoryDao

    abstract fun getFollowingDao(): FollowUserDao
}