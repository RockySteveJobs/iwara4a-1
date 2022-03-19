package com.rerere.iwara4a.ui.activity

import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
) : ViewModel(){
    var screenOrientation by mutableStateOf(Configuration.ORIENTATION_PORTRAIT)

    // 已完成检查
    var checked by mutableStateOf(false)
    // 是否正在检查
    var checkingCookkie by mutableStateOf(false)
    // Cookie无效
    var cookieValid by mutableStateOf(false)
    // 是否为首次登录
    var firstTime by mutableStateOf(false)

    init {
        fun isLogin() = sessionManager.session.key.isNotEmpty()

        viewModelScope.launch {
            checkingCookkie = true
            if (isLogin()) {
                val info = userRepo.getSelf(sessionManager.session)
                if (info.isSuccess()) {
                    cookieValid = true
                } else {
                    cookieValid = false
                    println(info.errorMessage())
                }
            } else {
                firstTime = true
                delay(500)
                cookieValid = false
            }

            checkingCookkie = false
            checked = true
        }
    }
}