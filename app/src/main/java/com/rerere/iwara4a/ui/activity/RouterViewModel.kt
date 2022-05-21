package com.rerere.iwara4a.ui.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.model.user.Self
import com.rerere.iwara4a.data.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouterViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
) : ViewModel() {
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