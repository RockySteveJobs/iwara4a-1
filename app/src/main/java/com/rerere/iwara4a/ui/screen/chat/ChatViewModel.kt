package com.rerere.iwara4a.ui.screen.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

private const val TAG = "ChatViewModel"

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
) : ViewModel() {
    val gson = Gson()
    var chats by mutableStateOf(emptyList<ChatMessage>())
    var connectionOpened by mutableStateOf(false)
    val userData = MutableStateFlow<DataState<Self>>(DataState.Empty)
    private val websocket = object : WebSocketClient(
        URI("ws://iwara.quasar.ac:2333")
    ) {
        override fun onOpen(handshakedata: ServerHandshake) {
            connectionOpened = true
            Log.i(TAG, "onOpen: 连接已开启")
        }

        override fun onMessage(message: String) {
            val chatMessage = gson.fromJson(message, ChatMessage::class.java)
            val chatList = chats + chatMessage
            chats = chatList
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
            connectionOpened = false
            Log.w(TAG, "onClose: ws closed: $code")
        }

        override fun onError(ex: Exception) {
            ex.printStackTrace()
        }
    }

    init {
        fetchUserData()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i(TAG, "ws: 开始尝试连接")
                websocket.connect()
                Log.i(TAG, "ws: 连接完成")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserData(){
        viewModelScope.launch {
            userData.value = DataState.Loading
            val self = userRepo.getSelf(sessionManager.session)
            if (self.isSuccess()) {
                userData.value = DataState.Success(self.read())
            } else {
                userData.value = DataState.Error(self.errorMessage())
            }
        }
    }

    fun send(message: String, result: (Boolean) -> Unit) {
        if(userData.value !is DataState.Success){
            result(false)
            return
        }
        if(!connectionOpened){
            result(false)
            return
        }
        val user = userData.value.read()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    websocket.send(
                        gson.toJson(
                            ChatMessage(
                                message = message,
                                username = user.nickname,
                                userId = user.id,
                                avatar = user.profilePic,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    )
                }
                result(true)
            } catch (e: Exception) {
                e.printStackTrace()
                result(false)
            }
        }
    }

    fun reconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            websocket.reconnect()
        }
    }

    override fun onCleared() {
        try {
            websocket.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}

data class ChatMessage(
    val username: String,
    val userId: String,
    val avatar: String,
    val message: String,
    val timestamp: Long
){
    val developer: Boolean
        get() = userId == "%E3%81%93%E3%81%93%E3%82%8D%E3%81%AA%E3%81%97RE"
}