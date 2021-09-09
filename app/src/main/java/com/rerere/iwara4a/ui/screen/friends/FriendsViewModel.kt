package com.rerere.iwara4a.ui.screen.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.friends.FriendList
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.util.DataState
import com.rerere.iwara4a.util.okhttp.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
) : ViewModel() {
    val friendList = MutableStateFlow<DataState<FriendList>>(DataState.Empty)

    init {
        loadFriendList()
    }

    fun loadFriendList() {
        viewModelScope.launch {
            friendList.value = DataState.Loading
            friendList.value = userRepo.getFriendList(sessionManager.session).toDataState()
        }
    }

    fun handleFriendRequest(id: Int, accept: Boolean, done: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/api/user/friends")
                    .method(if(accept) "PUT" else "DELETE", FormBody.Builder()
                        .add("frid", id.toString())
                        .build())
                    .build()
                okHttpClient.newCall(request).await()
            }
            done()
        }
    }
}