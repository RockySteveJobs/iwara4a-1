package com.rerere.iwara4a.ui.screen.playlist.new

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.rerere.iwara4a.data.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val playlistId: String? = savedStateHandle["playlistId"]
}