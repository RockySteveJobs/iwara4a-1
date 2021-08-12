package com.rerere.iwara4a.dao

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.rerere.iwara4a.model.download.DownloadedVideo

@Database(
    entities = [DownloadedVideo::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDownloadedVideoDao(): DownloadedVideoDao
}