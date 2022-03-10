package com.rerere.iwara4a.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.dao.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val database: AppDatabase
) : ViewModel() {
    val historyList = database.getHistoryDao().getAllHistory()

    fun clearAll() {
        viewModelScope.launch {
            database.getHistoryDao().clearAll()
        }
    }
}