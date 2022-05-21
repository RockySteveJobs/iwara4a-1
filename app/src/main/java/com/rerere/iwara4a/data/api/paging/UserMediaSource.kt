package com.rerere.iwara4a.data.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rerere.iwara4a.data.model.index.MediaPreview
import com.rerere.iwara4a.data.model.session.SessionManager
import com.rerere.iwara4a.data.repo.MediaRepo

class UserVideoListSource(
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val userId: String
) : PagingSource<Int, MediaPreview>() {
    override fun getRefreshKey(state: PagingState<Int, MediaPreview>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPreview> {
        val page = params.key ?: 0
        val response = mediaRepo.getUserVideoList(sessionManager.session, userId, page)
        return if (response.isSuccess()) {
            val data = response.read()
            LoadResult.Page(
                data = data.mediaList,
                prevKey = if (page <= 0) null else page - 1,
                nextKey = if (data.hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}

class UserImageListSource(
    private val mediaRepo: MediaRepo,
    private val sessionManager: SessionManager,
    private val userId: String
) : PagingSource<Int, MediaPreview>() {
    override fun getRefreshKey(state: PagingState<Int, MediaPreview>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPreview> {
        val page = params.key ?: 0
        val response = mediaRepo.getUserImageList(sessionManager.session, userId, page)
        return if (response.isSuccess()) {
            val data = response.read()
            LoadResult.Page(
                data = data.mediaList,
                prevKey = if (page <= 0) null else page - 1,
                nextKey = if (data.hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}