package com.rerere.iwara4a.ui.screen.index

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.rerere.iwara4a.api.GithubAPI
import com.rerere.iwara4a.api.paging.MediaSource
import com.rerere.iwara4a.api.paging.SubscriptionsSource
import com.rerere.iwara4a.model.github.GithubRelease
import com.rerere.iwara4a.model.index.MediaQueryParam
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SortType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.screen.index.page.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject

private const val TAG = "IndexViewModel"

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val githubAPI: GithubAPI
) : ViewModel() {
    var self by mutableStateOf(Self.GUEST)
    var email by mutableStateOf("")
    var loadingSelf by mutableStateOf(false)

    var updateChecker = MutableLiveData<com.rerere.iwara4a.api.Response<GithubRelease>>()

    init {
        viewModelScope.launch {
            updateChecker.value = githubAPI.getLatestRelease()
        }
    }

    // Pager: 视频列表
    var videoQueryParam: MediaQueryParam by mutableStateOf(
        MediaQueryParam(
            SortType.DATE,
            emptyList()
        )
    )
    val videoPager = Pager(
        config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8)
    ) {
        Log.i(TAG, "VidPager: Invoking Source Factory")
        MediaSource(
            MediaType.VIDEO,
            mediaRepo,
            sessionManager,
            videoQueryParam
        )
    }.flow.cachedIn(viewModelScope)


    // Pager: 订阅列表
    val subscriptionPager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32,
            prefetchDistance = 8
        )
    ) {
        Log.i(TAG, "SubPager: Invoking Source Factory")
        SubscriptionsSource(
            sessionManager,
            mediaRepo
        )
    }.flow.cachedIn(viewModelScope)


    // 图片列表
    var imageQueryParam: MediaQueryParam by mutableStateOf(
        MediaQueryParam(
            SortType.DATE,
            emptyList()
        )
    )

    val imagePager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32,
            prefetchDistance = 8
        )
    ) {
        Log.i(TAG, "ImagePager: Invoking Source Factory")
        MediaSource(
            MediaType.IMAGE,
            mediaRepo,
            sessionManager,
            imageQueryParam
        )
    }.flow.cachedIn(viewModelScope)

    // 聊天
    val gson = Gson()
    var webSocketConnected by mutableStateOf(false)
    var webSocket: WebSocket? = null
    var chatHistory by mutableStateOf(mutableListOf<ChatMessage>())

    fun reconnect() {
        initIrc()
    }

    fun sendMessage(message: String, other : Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            webSocket?.send(gson.toJson(
                ChatMessage(
                    userId = if(other) self.id + "~" else self.id,
                    username = self.nickname,
                    avatar = self.profilePic,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )
            ))
        }
    }

    private fun initIrc() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val okHttpClient = OkHttpClient.Builder().build()
                webSocket = okHttpClient.newWebSocket(
                    request = Request.Builder()
                        .url("ws://iwara.quasar.ac:80/chat")
                        .build(),
                    listener =
                    object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            Log.i(TAG, "onOpen: Connected chat room")
                            webSocketConnected = true
                        }

                        override fun onFailure(
                            webSocket: WebSocket,
                            t: Throwable,
                            response: Response?
                        ) {
                            t.printStackTrace()
                        }

                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            Log.i(TAG, "onClosed: Closed~")
                            webSocketConnected = false
                        }

                        override fun onMessage(webSocket: WebSocket, text: String) {
                            try {
                                val chat = gson.fromJson(text, ChatMessage::class.java)
                                chatHistory.add(chat)
                                val his = chatHistory
                                chatHistory = arrayListOf()
                                chatHistory = his
                            } catch (e: Exception){
                                e.printStackTrace()
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        refreshSelf()
        initIrc()
    }

    override fun onCleared() {
        Log.i(TAG, "onCleared: Cleaned")
        try {
            webSocket?.close(1000, "Clear")
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        email = sharedPreferencesOf("session").getString("username", "请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            self = response.read()
        }
        loadingSelf = false
    }
}