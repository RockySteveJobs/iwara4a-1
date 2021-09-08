package com.rerere.iwara4a.ui.screen.index

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.GithubAPI
import com.rerere.iwara4a.api.oreno3d.Oreno3dApi
import com.rerere.iwara4a.api.oreno3d.OrenoSort
import com.rerere.iwara4a.api.paging.MediaSource
import com.rerere.iwara4a.api.paging.OrenoSource
import com.rerere.iwara4a.api.paging.SubscriptionsSource
import com.rerere.iwara4a.model.github.GithubRelease
import com.rerere.iwara4a.model.index.MediaQueryParam
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SortType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "IndexViewModel"

@HiltViewModel
class IndexViewModel @Inject constructor(
    private val userRepo: UserRepo,
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val githubAPI: GithubAPI,
    private val oreno3dApi: Oreno3dApi
) : ViewModel() {
    var self by mutableStateOf(Self.GUEST)
    var email by mutableStateOf("")
    var loadingSelf by mutableStateOf(false)

    var updateChecker = MutableStateFlow<DataState<GithubRelease>>(DataState.Empty)

    init {
        viewModelScope.launch {
            updateChecker.value = githubAPI.getLatestRelease().toDataState()
        }
        refreshSelf()
    }

    // Pager: 视频列表
    var videoQueryParam: MediaQueryParam by mutableStateOf(
        MediaQueryParam(
            SortType.DATE,
            hashSetOf()
        )
    )
    val videoPager = Pager(
        config = PagingConfig(pageSize = 32, initialLoadSize = 32, prefetchDistance = 8)
    ) {
        Log.i(TAG, "VidPager: Invoking Source Factory")
        MediaSource(
            MediaType.VIDEO,
            mediaRepo,
            sessionManager,
            videoQueryParam
        )
    }.flow.cachedIn(viewModelScope)


    // Pager: 订阅列表
    val subscriptionPager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32,
            prefetchDistance = 4
        )
    ) {
        Log.i(TAG, "SubPager: Invoking Source Factory")
        SubscriptionsSource(
            sessionManager,
            mediaRepo
        )
    }.flow.cachedIn(viewModelScope)


    // 图片列表
    var imageQueryParam: MediaQueryParam by mutableStateOf(
        MediaQueryParam(
            SortType.DATE,
            hashSetOf()
        )
    )

    val imagePager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32,
            prefetchDistance = 8
        )
    ) {
        Log.i(TAG, "ImagePager: Invoking Source Factory")
        MediaSource(
            MediaType.IMAGE,
            mediaRepo,
            sessionManager,
            imageQueryParam
        )
    }.flow.cachedIn(viewModelScope)

    // 推荐
    val orenoList = OrenoSort.values().map { sort ->
        sort to Pager(
            config = PagingConfig(
                pageSize = 36,
                prefetchDistance = 8,
                initialLoadSize = 36
            )
        ){
            OrenoSource(
                oreno3dApi = oreno3dApi,
                orenoSort = sort
            )
        }.flow.cachedIn(viewModelScope)
    }.toList()

    fun openOrenoVideo(id: Int, result: (String) -> Unit){
        viewModelScope.launch {
            val response = oreno3dApi.getVideoIwaraId(id)
            if(response.isSuccess()){
                result(response.read())
            } else {
                result("")
            }
        }
    }

    fun refreshSelf() = viewModelScope.launch {
        loadingSelf = true
        email = sharedPreferencesOf("session").getString("username", "请先登录你的账号吧")!!
        val response = userRepo.getSelf(sessionManager.session)
        if (response.isSuccess()) {
            self = response.read()
        }
        loadingSelf = false
    }
}