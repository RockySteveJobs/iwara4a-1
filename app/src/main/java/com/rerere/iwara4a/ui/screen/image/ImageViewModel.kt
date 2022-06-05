package com.rerere.iwara4a.ui.screen.image

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.data.dao.AppDatabase
import com.rerere.iwara4a.data.dao.insertSmartly
import com.rerere.iwara4a.data.model.comment.Comment
import com.rerere.iwara4a.data.model.comment.CommentPostParam
import com.rerere.iwara4a.data.model.detail.image.ImageDetail
import com.rerere.iwara4a.data.model.history.HistoryData
import com.rerere.iwara4a.data.model.history.HistoryType
import com.rerere.iwara4a.data.model.index.MediaType
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.repo.MediaRepo
import com.rerere.iwara4a.ui.component.MediaQueryParam
import com.rerere.iwara4a.ui.component.PageListProvider
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val database: AppDatabase
) : ViewModel() {
    val imageId: String = checkNotNull(savedStateHandle["imageId"])
    var imageDetail = MutableStateFlow<DataState<ImageDetail>>(DataState.Empty)

    init {
        load()
    }

    fun load() = viewModelScope.launch {
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

    val commentPagerProvider = object : PageListProvider<Comment> {
        private var lastLoadingPage = -1
        private val data = MutableStateFlow<DataState<List<Comment>>>(DataState.Empty)
        private var hasNext by mutableStateOf(true)

        override fun refresh() {
            viewModelScope.launch {
                data.value = DataState.Loading
                try {
                    val response = mediaRepo.loadComment(
                        session = sessionManager.session,
                        mediaType = MediaType.IMAGE,
                        mediaId = imageId,
                        page = lastLoadingPage - 1
                    ).read()
                    data.value = DataState.Success(
                        response.comments
                    )
                    hasNext = response.hasNext
                } catch (e: Exception) {
                    e.printStackTrace()
                    data.value = DataState.Error(e.javaClass.name)
                }
            }
        }

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if (page == lastLoadingPage) return
            viewModelScope.launch {
                data.value = DataState.Loading
                try {
                    val response = mediaRepo.loadComment(
                        session = sessionManager.session,
                        mediaType = MediaType.VIDEO,
                        mediaId = imageId,
                        page = page - 1
                    ).read()
                    data.value = DataState.Success(
                        response.comments
                    )
                    hasNext = response.hasNext
                    lastLoadingPage = page
                } catch (e: Exception) {
                    e.printStackTrace()
                    data.value = DataState.Error(e.javaClass.name)
                }
            }
        }

        override fun getPage(): Flow<DataState<List<Comment>>> {
            return data
        }

        override fun hasNext(): Boolean {
            return hasNext
        }
    }

    fun postReply(
        content: String,
        nid: Int,
        commentId: Int?,
        commentPostParam: CommentPostParam,
        onFinished: () -> Unit
    ) {
        viewModelScope.launch {
            mediaRepo.postComment(
                session = sessionManager.session,
                nid = nid,
                commentId = commentId,
                commentPostParam = commentPostParam,
                content = content
            )
            onFinished()
        }
    }
}