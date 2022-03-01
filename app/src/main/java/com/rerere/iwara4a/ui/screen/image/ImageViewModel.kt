package com.rerere.iwara4a.ui.screen.image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.dao.AppDatabase
import com.rerere.iwara4a.dao.insertSmartly
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.model.history.HistoryData
import com.rerere.iwara4a.model.history.HistoryType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val database: AppDatabase
) : ViewModel() {
    var imageDetail = MutableStateFlow<DataState<ImageDetail>>(DataState.Empty)

    fun load(imageId: String) = viewModelScope.launch {
        imageDetail.value = DataState.Loading
        val response = mediaRepo.getImageDetail(sessionManager.session, imageId)
        if (response.isSuccess()) {
            imageDetail.value = DataState.Success(response.read())

            // insert history
            database.getHistoryDao().insertSmartly(
                HistoryData(
                    date = System.currentTimeMillis(),
                    title = response.read().title,
                    preview = response.read().imageLinks.getOrNull(0) ?: "",
                    route = "image/$imageId",
                    historyType = HistoryType.IMAGE
                )
            )
        } else {
            imageDetail.value = DataState.Error(response.errorMessage())
        }
    }
}