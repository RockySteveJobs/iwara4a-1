package com.rerere.iwara4a.ui.screen.follow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.data.dao.AppDatabase
import com.rerere.iwara4a.data.model.follow.FollowUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowScreenViewModel @Inject constructor(
    private val database: AppDatabase
): ViewModel(){
    val allUsers = database.getFollowingDao().getAllFollowUsers()

    fun delete(user: FollowUser){
        viewModelScope.launch {
            database.getFollowingDao().removeUser(user)
        }
    }
}