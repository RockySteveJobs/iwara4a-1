package com.rerere.iwara4a.dao

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rerere.iwara4a.model.download.DownloadedVideo
import com.rerere.iwara4a.model.history.HistoryData

@Database(
    entities = [DownloadedVideo::class, HistoryData::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDownloadedVideoDao(): DownloadedVideoDao

    abstract fun getHistoryDao() : HistoryDao
}