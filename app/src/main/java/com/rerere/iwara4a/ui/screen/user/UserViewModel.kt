package com.rerere.iwara4a.ui.screen.user

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.api.paging.UserPageCommentSource
import com.rerere.iwara4a.api.paging.UserVideoListSource
import com.rerere.iwara4a.dao.insertSmartly
import com.rerere.iwara4a.model.history.HistoryData
import com.rerere.iwara4a.model.history.HistoryType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.UserData
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.util.okhttp.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    private val mediaRepo: MediaRepo
) : ViewModel() {
    var loading by mutableStateOf(false)
    var error by mutableStateOf(false)
    var userData by mutableStateOf(UserData.LOADING)

    fun load(userId: String) {
        viewModelScope.launch {
            loading = true
            error = false

            val response = userRepo.getUser(sessionManager.session, userId)
            if (response.isSuccess()) {
                userData = response.read()

                // insert history
                AppContext.database.getHistoryDao().insertSmartly(
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


    val commentPager = Pager(
        PagingConfig(
            pageSize = 100,
            prefetchDistance = 10,
            initialLoadSize = 100
        )
    ) {
        UserPageCommentSource(
            sessionManager,
            userRepo,
            userId = userData.userId
        )
    }.flow.cachedIn(viewModelScope)

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
}