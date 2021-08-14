package com.rerere.iwara4a.ui.screen.playlist

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.IntRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.model.playlist.PlaylistAction
import com.rerere.iwara4a.model.playlist.PlaylistDetail
import com.rerere.iwara4a.model.playlist.PlaylistOverview
import com.rerere.iwara4a.model.playlist.PlaylistPreview
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

private const val TAG = "PlaylistViewModel"

@HiltViewModel
class PlaylistViewModel @Inject constructor(
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager
) : ViewModel() {

    val overview = MutableStateFlow<DataState<List<PlaylistOverview>>>(DataState.Empty)
    fun loadOverview(){
        viewModelScope.launch {
            overview.value = DataState.Loading
            delay(100)
            val result = mediaRepo.getPlaylistOverview(sessionManager.session)
            if(result.isSuccess()){
                overview.value = DataState.Success(result.read())
            } else {
                overview.value = DataState.Error(result.errorMessage())
            }
        }
    }

    val playlistDetail = MutableStateFlow<DataState<PlaylistDetail>>(DataState.Empty)
    fun loadDetail(playlistId: String) {
        viewModelScope.launch {
            playlistDetail.value = DataState.Loading
            delay(100)
            val result = mediaRepo.getPlaylistDetail(sessionManager.session, playlistId)
            if(result.isSuccess()){
                playlistDetail.value = DataState.Success(result.read())
            } else {
                playlistDetail.value =  DataState.Error(result.errorMessage())
            }
        }
    }

    var modifyPlaylist by mutableStateOf(emptyList<PlaylistPreview.PlaylistPreviewItem>())
    var modifyPlaylistLoading by mutableStateOf(false)
    var modifyPlaylistError by mutableStateOf(false)

    fun loadPlaylist(@IntRange(from = 1) nid: Int) {
        Log.i(TAG, "loadPlaylist: Loading playlist for nid = $nid")
        viewModelScope.launch {
            modifyPlaylistLoading = true
            modifyPlaylistError = false
            val result = mediaRepo.getPlaylistPreview(sessionManager.session, nid)
            Log.i(TAG, "loadPlaylist: Load result = ${result.isSuccess()}")
            if (result.isSuccess()) {
                modifyPlaylist = emptyList()
                modifyPlaylist = result.read()
                println(modifyPlaylist.joinToString { it.title })
                Log.i(TAG, "loadPlaylist: Loaded ${result.read().size} playlist")
            } else {
                modifyPlaylistError = true
            }
            modifyPlaylistLoading = false
        }
    }

    var modifyLoading by mutableStateOf(false)

    fun modify(context: Context, playlist: Int, nid: Int, current: Boolean) {
        viewModelScope.launch {
            modifyLoading = true
            val result = mediaRepo.modifyPlaylist(
                session = sessionManager.session,
                playlist = playlist,
                nid = nid,
                action = if (current) PlaylistAction.DELETE else PlaylistAction.PUT
            )
            if(result.isSuccess() && result.read() == 1){
                // Success
                val refresh = mediaRepo.getPlaylistPreview(sessionManager.session, nid)
                if (refresh.isSuccess()) {
                    modifyPlaylist = emptyList()
                    modifyPlaylist = refresh.read()
                }
            } else {
                Toast.makeText(context, "编辑播单失败，请稍后重试", Toast.LENGTH_SHORT).show()
            }
            modifyLoading = false
        }
    }
}