package com.rerere.iwara4a.ui.screen.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.AppContext
import com.rerere.iwara4a.model.follow.FollowUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowScreenViewModel @Inject constructor(): ViewModel(){
    val allUsers = AppContext.database.getFollowingDao().getAllFollowUsers()

    fun delete(user: FollowUser){
        viewModelScope.launch {
            AppContext.database.getFollowingDao().removeUser(user)
        }
    }
}