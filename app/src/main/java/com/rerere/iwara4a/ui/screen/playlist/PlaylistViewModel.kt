package com.rerere.iwara4a.ui.screen.playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val userRepo: UserRepo
) : ViewModel() {
    fun loadPlaylist() {
        viewModelScope.launch {

        }
    }
}