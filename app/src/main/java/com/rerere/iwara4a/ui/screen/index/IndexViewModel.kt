package com.rerere.iwara4a.ui.screen.index

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.GithubAPI
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.api.oreno3d.Oreno3dApi
import com.rerere.iwara4a.api.oreno3d.OrenoSort
import com.rerere.iwara4a.api.paging.OrenoSource
import com.rerere.iwara4a.model.github.GithubRelease
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.repo.UserRepo
import com.rerere.iwara4a.sharedPreferencesOf
import com.rerere.iwara4a.ui.public.MediaQueryParam
import com.rerere.iwara4a.ui.public.PageListProvider
import com.rerere.iwara4a.ui.public.SortType
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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
    val videoListPrvider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null
        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if(page == lastSuccessPage && queryParam == lastSuccessQueryParam) return

            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getMediaList(
                    session = sessionManager.session,
                    mediaType = MediaType.VIDEO,
                    page = page - 1,
                    sortType = queryParam?.sortType ?: SortType.DATE,
                    filters = queryParam?.filters ?: hashSetOf()
                )
                when(response){
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().mediaList)
                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
    }


    // Pager: 订阅列表
    val subscriptionsProvider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null
        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if(page == lastSuccessPage && queryParam == lastSuccessQueryParam) return
            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getSubscriptionList(
                    session = sessionManager.session,
                    page = page - 1
                )
                when(response){
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().subscriptionList)
                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
    }

    // 图片列表
    val imageListProvider = object : PageListProvider<MediaPreview>{
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = null

        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if(page == lastSuccessPage && queryParam == lastSuccessQueryParam) return
            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.getMediaList(
                    session = sessionManager.session,
                    mediaType = MediaType.IMAGE,
                    page = page - 1,
                    sortType = queryParam?.sortType ?: SortType.DATE,
                    filters = queryParam?.filters ?: hashSetOf()
                )
                when(response){
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().mediaList)

                        lastSuccessPage = page
                        lastSuccessQueryParam = queryParam
                    }
                    is Response.Failed -> {
                        data.value = DataState.Error(response.errorMessage())
                    }
                }
            }
        }

        override fun getPage(): Flow<DataState<List<MediaPreview>>> = data
    }

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