package com.rerere.iwara4a.ui.screen.dev

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.paging.SubscriptionsSource
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DevViewmodel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
) : ViewModel(){
    // Pager: 订阅列表
    val subscriptionPager = Pager(
        config = PagingConfig(
            pageSize = 32,
            initialLoadSize = 32,
            prefetchDistance = 8
        )
    ) {
        SubscriptionsSource(
            sessionManager,
            mediaRepo
        )
    }.flow.cachedIn(viewModelScope)
}