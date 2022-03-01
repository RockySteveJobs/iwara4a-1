package com.rerere.iwara4a.ui.screen.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import com.rerere.iwara4a.ui.component.MediaQueryParam
import com.rerere.iwara4a.ui.component.PageListProvider
import com.rerere.iwara4a.ui.component.SortType
import com.rerere.iwara4a.util.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
) : ViewModel() {
    var query by mutableStateOf("")

    val provider = object : PageListProvider<MediaPreview> {
        private var lastSuccessPage = -1
        private var lastSuccessQueryParam: MediaQueryParam? = MediaQueryParam.Default
        private var lastQuery = ""
        private val data = MutableStateFlow<DataState<List<MediaPreview>>>(DataState.Empty)

        override fun load(page: Int, queryParam: MediaQueryParam?) {
            if(query.isBlank()) return
            if(page == lastSuccessPage && query == lastQuery && queryParam == lastSuccessQueryParam) return

            viewModelScope.launch {
                data.value = DataState.Loading
                val response = mediaRepo.search(
                    session = sessionManager.session,
                    query = query,
                    page = page - 1,
                    sort = queryParam?.sortType ?: SortType.DATE,
                    filter = queryParam?.filters ?: hashSetOf()
                )
                when(response){
                    is Response.Success -> {
                        data.value = DataState.Success(response.read().mediaList)

                        lastSuccessPage = page
                        lastQuery = query
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
}