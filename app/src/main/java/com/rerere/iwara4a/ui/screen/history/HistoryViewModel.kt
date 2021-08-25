package com.rerere.iwara4a.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import kotlinx.coroutines.launch
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
) : ViewModel() {
    val historyList = AppContext.database.getHistoryDao().getAllHistory()

    fun clearAll() {
        viewModelScope.launch {
            AppContext.database.getHistoryDao().clearAll()
        }
    }
}