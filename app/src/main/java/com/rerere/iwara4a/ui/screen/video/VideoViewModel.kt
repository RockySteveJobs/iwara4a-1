package com.rerere.iwara4a.ui.screen.video

import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.api.paging.CommentSource
import com.rerere.iwara4a.model.comment.CommentPostParam
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
): ViewModel() {
    var videoId by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var videoDetail by mutableStateOf(VideoDetail.LOADING)

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
                mediaId = videoDetail.id
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun postReply(content: String, nid: Int, commentId: Int?, commentPostParam: CommentPostParam, onFinished: () -> Unit) {
        viewModelScope.launch {
            mediaRepo.postComment(
                session = sessionManager.session,
                nid = nid,
                commentId = commentId,
                commentPostParam = commentPostParam,
                content = "$content\r\n (来自 [url=https://github.com/jiangdashao/iwara4a]Iwara4A[/url] 安卓客户端)"
            )
            onFinished()
        }
    }

    fun loadVideo(id: String){
        if(videoDetail != VideoDetail.LOADING){
            return
        }

        viewModelScope.launch {
            videoId = id
            isLoading = true
            error = false

            val response = mediaRepo.getVideoDetail(sessionManager.session, id)
            if(response.isSuccess()){
                videoDetail = response.read()
            }else {
                error = true
            }

            isLoading = false
        }
    }

    fun handleLike(result: (action: Boolean, success: Boolean) -> Unit){
        val action = !videoDetail.isLike
        viewModelScope.launch {
            val response = mediaRepo.like(sessionManager.session, action, videoDetail.likeLink)
            if(response.isSuccess()){
                videoDetail = videoDetail.copy(isLike = response.read().flagStatus == "flagged")
            }
            result(action, response.isSuccess())
        }
    }

    fun handleFollow(result: (action: Boolean, success: Boolean) -> Unit){
        val action = !videoDetail.follow
        viewModelScope.launch {
            val response = mediaRepo.follow(sessionManager.session, action, videoDetail.followLink)
            if(response.isSuccess()){
                videoDetail = videoDetail.copy(follow = response.read().flagStatus == "flagged")
            }
            result(action, response.isSuccess())
        }
    }

    fun download() {
        val resolver = AppContext.instance.contentResolver
        val videoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        }
        val videoDetails = ContentValues().apply {
            put(MediaStore.Video.Media.RELATIVE_PATH,"")
        }
    }
}