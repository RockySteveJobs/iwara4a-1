package com.rerere.iwara4a.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.google.TranslatorAPI
import com.rerere.iwara4a.api.paging.CommentSource
import com.rerere.iwara4a.dao.AppDatabase
import com.rerere.iwara4a.dao.insertSmart
import com.rerere.iwara4a.dao.insertSmartly
import com.rerere.iwara4a.model.comment.CommentPostParam
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.history.HistoryData
import com.rerere.iwara4a.model.history.HistoryType
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val translatorAPI: TranslatorAPI,
    val database: AppDatabase
) : ViewModel() {
    val videoDetailState = MutableStateFlow<DataState<VideoDetail>>(DataState.Empty)

    fun translate(){
        viewModelScope.launch {
            val title = async { translatorAPI.translate(videoDetailState.value.read().title) }
            val description = async {  translatorAPI.translate(videoDetailState.value.read().description) }
            videoDetailState.value = DataState.Success(
                videoDetailState.value.read().copy(
                    description = description.await() ?: "error",
                    title = title.await() ?: "error"
                )
            )
        }
    }

    val commentPager by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30
            )
        ) {
            CommentSource(
                sessionManager = sessionManager,
                mediaRepo = mediaRepo,
                mediaType = MediaType.VIDEO,
                mediaId = videoDetailState.value.read().id
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun postReply(
        content: String,
        nid: Int,
        commentId: Int?,
        commentPostParam: CommentPostParam,
        showTail: Boolean = true,
        onFinished: () -> Unit
    ) {
        viewModelScope.launch {
            mediaRepo.postComment(
                session = sessionManager.session,
                nid = nid,
                commentId = commentId,
                commentPostParam = commentPostParam,
                content = content + if(showTail) "\r\n (来自 [url=https://github.com/re-ovo/iwara4a]Iwara4A[/url] 安卓客户端)" else ""
            )
            onFinished()
        }
    }

    fun loadVideo(id: String) {
        viewModelScope.launch {
            videoDetailState.value = DataState.Loading
            val response = mediaRepo.getVideoDetail(sessionManager.session, id)
            if (response.isSuccess()) {
                videoDetailState.value = DataState.Success(response.read())

                // insert history
                database.getHistoryDao().insertSmartly(
                    HistoryData(
                        date = System.currentTimeMillis(),
                        title = response.read().title,
                        preview = response.read().preview,
                        route = "video/$id",
                        historyType = HistoryType.VIDEO
                    )
                )

                // insert following
                if(videoDetailState.value.read().follow) {
                    database.getFollowingDao().insertSmart(
                        id = videoDetailState.value.read().authorId,
                        name = videoDetailState.value.read().authorName,
                        profilePic = videoDetailState.value.read().authorPic
                    )
                }
            } else {
                videoDetailState.value = DataState.Error(response.errorMessage())
            }
        }
    }

    fun handleLike(result: (action: Boolean, success: Boolean) -> Unit) {
        val action = !videoDetailState.value.read().isLike
        viewModelScope.launch {
            val response = mediaRepo.like(
                sessionManager.session,
                action,
                videoDetailState.value.read().likeLink
            )
            if (response.isSuccess()) {
                videoDetailState.value = DataState.Success(
                    videoDetailState.value.read()
                        .copy(isLike = response.read().flagStatus == "flagged")
                )
            }
            result(action, response.isSuccess())
        }
    }

    fun handleFollow(result: (action: Boolean, success: Boolean) -> Unit) {
        val action = !videoDetailState.value.read().follow
        viewModelScope.launch {
            val response = mediaRepo.follow(
                sessionManager.session,
                action,
                videoDetailState.value.read().followLink
            )
            if (response.isSuccess()) {
                videoDetailState.value = DataState.Success(
                    videoDetailState.value.read()
                        .copy(follow = response.read().flagStatus == "flagged")
                )
            }
            result(action, response.isSuccess())
        }
    }
}