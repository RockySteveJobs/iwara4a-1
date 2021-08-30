package com.rerere.iwara4a.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rerere.iwara4a.model.history.HistoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM historydata ORDER BY date DESC")
    fun getAllHistory(): Flow<List<HistoryData>>

    @Insert
    suspend fun insert(historyData: HistoryData)

    @Query("DELETE FROM historydata")
    suspend fun clearAll()
}