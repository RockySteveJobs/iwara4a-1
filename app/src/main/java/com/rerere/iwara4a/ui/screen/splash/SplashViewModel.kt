package com.rerere.iwara4a.ui.screen.splash

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
class SplashViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo
) : ViewModel() {
    private fun isLogin() = sessionManager.session.key.isNotEmpty()

    var checked by mutableStateOf(false)
    var checkingCookkie by mutableStateOf(false)
    var cookieValid by mutableStateOf(false)
    var startTime by mutableStateOf(0L)
    var firstTime by mutableStateOf(false)

    init {
        viewModelScope.launch {
            delay(50)
            checkingCookkie = true
            startTime = System.currentTimeMillis()
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
                delay(1000)
                cookieValid = false
                println("First Time")
            }

            checkingCookkie = false
            checked = true
        }

    }
}