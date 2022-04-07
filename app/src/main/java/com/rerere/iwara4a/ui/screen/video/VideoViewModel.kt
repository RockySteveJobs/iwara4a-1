package com.rerere.iwara4a.ui.screen.video

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.google.TranslatorAPI
import com.rerere.iwara4a.api.paging.CommentSource
import com.rerere.iwara4a.api.service.IwaraService
import com.rerere.iwara4a.dao.AppDatabase
import com.rerere.iwara4a.dao.insertSmart
import com.rerere.iwara4a.dao.insertSmartly
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.comment.CommentPostParam
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.detail.video.VideoLink
import com.rerere.iwara4a.model.history.HistoryData
import com.rerere.iwara4a.model.history.HistoryType
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.ui.component.MediaQueryParam
import com.rerere.iwara4a.ui.component.PageListProvider
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val translatorAPI: TranslatorAPI,
    val database: AppDatabase,
    private val iwaraService: IwaraService
) : ViewModel() {
    val videoLink = MutableStateFlow<DataState<VideoLink>>(DataState.Empty)
    val videoDetailState = MutableStateFlow<DataState<VideoDetail>>(DataState.Empty)

    fun translate() {
        viewModelScope.launch {
            val title = async { translatorAPI.translate(videoDetailState.value.read().title) }
            val description =
                async { translatorAPI.translate(videoDetailState.value.read().description) }
            videoDetailState.value = DataState.Success(
                videoDetailState.value.read().copy(
                    description = description.await() ?: "error",
                    title = title.await() ?: "error"
                )
            )
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
                        mediaType = MediaType.VIDEO,
                        mediaId = videoDetailState.value.readSafely()?.id ?: "",
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
                        mediaId = videoDetailState.value.readSafely()?.id ?: "",
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

    fun loadVideo(id: String) {
        viewModelScope.launch {
            videoDetailState.value = DataState.Loading

            launch {
                // Load video detail fast
                mediaRepo.getVideoDetailFast(id)?.let {
                    if (videoDetailState.value is DataState.Loading) {
                        videoDetailState.value = DataState.Success(it)
                        println("Loaded video from backend api")
                    }
                }
            }

            // Load video link
            try {
                videoLink.value = DataState.Loading
                videoLink.value = DataState.Success(iwaraService.getVideoInfo(id))
            } catch (e: Exception) {
                e.printStackTrace()
                videoLink.value = DataState.Error(e.javaClass.name)
            }

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
                if (videoDetailState.value.read().follow) {
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