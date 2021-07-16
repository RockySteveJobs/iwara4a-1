package com.rerere.iwara4a.ui.screen.like

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rerere.iwara4a.api.paging.LikeSource
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LikedViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo
) : ViewModel() {
    val pager = Pager(
        config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 20,
            prefetchDistance = 4
        )
    ) {
        LikeSource(
            sessionManager = sessionManager,
            mediaRepo = mediaRepo
        )
    }.flow.cachedIn(viewModelScope)
}