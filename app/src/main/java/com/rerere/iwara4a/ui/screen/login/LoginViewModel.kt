package com.rerere.iwara4a.ui.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.activity.RouterViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val sessionManager: SessionManager
) : ViewModel() {
    var userName by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoginState by mutableStateOf(false)
    var errorContent by mutableStateOf("")

    init {
        val sharedPreferences = sharedPreferencesOf("session")
        userName = sharedPreferences.getString("username", "")!!
        password = sharedPreferences.getString("password", "")!!
    }

    fun login(viewModel: RouterViewModel, result: (success: Boolean) -> Unit) {
        viewModelScope.launch {
            isLoginState = true
            // save
            val sharedPreferences = sharedPreferencesOf("session")
            sharedPreferences.edit {
                putString("username", userName)
                putString("password", password)
            }

            val response = userRepo.login(userName, password)

            // call event
            if (response.isSuccess()) {
                val session = response.read()
                sessionManager.update(session.key, session.value)
                viewModel.prepareUserData()
            } else {
                errorContent = response.errorMessage()
            }

            delay(50)
            // call back
            result(response.isSuccess())

            isLoginState = false
        }
    }
}