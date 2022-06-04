package com.rerere.iwara4a.ui.screen.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.data.api.google.TranslatorAPI
import com.rerere.iwara4a.data.api.paging.UserImageListSource
import com.rerere.iwara4a.data.api.paging.UserVideoListSource
import com.rerere.iwara4a.data.dao.AppDatabase
import com.rerere.iwara4a.data.dao.insertSmartly
import com.rerere.iwara4a.data.model.comment.Comment
import com.rerere.iwara4a.data.model.comment.CommentPostParam
import com.rerere.iwara4a.data.model.history.HistoryData
import com.rerere.iwara4a.data.model.history.HistoryType
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.model.user.UserData
import com.rerere.iwara4a.data.repo.MediaRepo
import com.rerere.iwara4a.data.repo.UserRepo
import com.rerere.iwara4a.ui.component.MediaQueryParam
import com.rerere.iwara4a.ui.component.PageListProvider
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.okhttp.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val TAG = "UserViewModel"

@HiltViewModel
class UserViewModel @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val database: AppDatabase,
    private val translatorAPI: TranslatorAPI
) : ViewModel() {
    var loading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var userData by mutableStateOf(UserData.LOADING)

    suspend fun translate(text: String) = translatorAPI.translate(text) ?: text

    fun load(userId: String) {
        viewModelScope.launch {
            loading = true
            error = false

            val response = userRepo.getUser(sessionManager.session, userId)
            if (response.isSuccess()) {
                userData = response.read()

                // insert history
                database.getHistoryDao().insertSmartly(
                    HistoryData(
                        date = System.currentTimeMillis(),
                        title = response.read().username,
                        preview = response.read().pic,
                        route = "user/$userId",
                        historyType = HistoryType.USER
                    )
                )
            } else {
                error = true
            }

            loading = false
        }
    }

    fun isLoaded() = userData != UserData.LOADING

    fun handleFollow(result: (action: Boolean, success: Boolean) -> Unit) {
        val action = !userData.follow
        viewModelScope.launch {
            val response = mediaRepo.follow(sessionManager.session, action, userData.followLink)
            if (response.isSuccess()) {
                userData = userData.copy(follow = response.read().flagStatus == "flagged")
            }
            result(action, response.isSuccess())
        }
    }

    fun handleFriendRequest(result: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/api/user/friends/request")
                    .post(
                        FormBody.Builder()
                            .add("uid", userData.id.toString())
                            .build()
                    )
                    .build()
                okHttpClient.newCall(request).await()
            }
            result()
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

    val commentPagerProvider = object : PageListProvider<Comment> {
        private var lastLoadingPage = -1
        private val data = MutableStateFlow<DataState<List<Comment>>>(DataState.Empty)
        private var hasNext by mutableStateOf(true)

        override fun refresh() {
            viewModelScope.launch {
                data.value = DataState.Loading
                try {
                    val response = userRepo.getUserPageComment(
                        session = sessionManager.session,
                        userId = userData.userId,
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
                    val response = userRepo.getUserPageComment(
                        session = sessionManager.session,
                        userId = userData.userId,
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

    val videoPager = Pager(
        PagingConfig(
            pageSize = 40,
            prefetchDistance = 8,
            initialLoadSize = 40
        )
    ) {
        UserVideoListSource(
            mediaRepo = mediaRepo,
            sessionManager = sessionManager,
            userId = userData.userIdMedia
        )
    }.flow.cachedIn(viewModelScope)

    val imagePager = Pager(
        PagingConfig(
            pageSize = 40,
            prefetchDistance = 8,
            initialLoadSize = 40
        )
    ) {
        UserImageListSource(
            mediaRepo = mediaRepo,
            sessionManager = sessionManager,
            userId = userData.userIdMedia
        )
    }.flow.cachedIn(viewModelScope)
}