package com.rerere.iwara4a.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rerere.iwara4a.model.history.HistoryData
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import kotlin.math.abs

@Dao
interface HistoryDao {
    @Query("SELECT * FROM historydata ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryData>>

    @Query("SELECT * FROM historydata ORDER BY date DESC LIMIT 1")
    suspend fun getLatestHistory() : HistoryData

    @Insert
    suspend fun insert(historyData: HistoryData)

    @Query("DELETE FROM historydata")
    suspend fun clearAll()
}

/**
 * 只在与上一个data不同时或者间隔超过1分钟才插入该数据
 */
suspend fun HistoryDao.insertSmartly(historyData: HistoryData){
    val latest = getLatestHistory()
    if(latest.route != historyData.route || abs(latest.date - historyData.date) >= 60000){
        insert(historyData)
    }
}