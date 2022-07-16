package com.rerere.iwara4a.ui.activity

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.data.api.backend.Iwara4aBackendAPI
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.model.user.Self
import com.rerere.iwara4a.data.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RouterViewModel"

@HiltViewModel
class RouterViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo,
    private val backendAPI: Iwara4aBackendAPI,
    application: Application
) : AndroidViewModel(application) {

    init {
        viewModelScope.launch {
            kotlin.runCatching {
                val deviceUUID = (application as AppContext).deviceUUID.toString()
                Log.i(TAG, "stats: posting usage stats to backend: $deviceUUID")
                backendAPI.postStatusData(
                    uuid = deviceUUID
                )
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    // 用户数据
    var userData by mutableStateOf(Self.GUEST)
    var userDataFetched by mutableStateOf(false)

    private fun isLogin() = sessionManager.session.key.isNotEmpty()

    suspend fun prepareUserData() {
        if (isLogin()) {
            val response = userRepo.getSelf(sessionManager.session)
            userData = if (response.isSuccess()) {
                response.read()
            } else {
                if(response.errorMessage() == java.lang.IllegalStateException::class.java.name) {
                    Self.GUEST // 登录过期
                } else {
                    // 没有网络连接?
                    Self.GUEST.copy(
                        nickname = "???"
                    )
                }
            }
        }
        userDataFetched = true
    }

    init {
        viewModelScope.launch {
            prepareUserData()
        }
    }
}