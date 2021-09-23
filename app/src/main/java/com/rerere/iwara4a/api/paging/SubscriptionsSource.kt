package com.rerere.iwara4a.api.paging

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.MediaRepo

private const val TAG = "SubscriptionsSource"

class SubscriptionsSource(
    private val sessionManager: SessionManager,
    private val mediaRepo: MediaRepo,
    private val loadPage: MutableState<Int> = mutableStateOf(0)
) : PagingSource<Int, MediaPreview>() {
    override fun getRefreshKey(state: PagingState<Int, MediaPreview>): Int {
        return loadPage.value.coerceAtLeast(0)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaPreview> {
        val page = params.key ?: loadPage.value.coerceAtLeast(0)

        Log.i(TAG, "load: trying to load page: $page")

        val response = mediaRepo.getSubscriptionList(sessionManager.session, page)
        return if (response.isSuccess()) {
            val data = response.read()
            Log.i(
                TAG,
                "load: success load sub list (datasize=${data.subscriptionList.size}, hasNext=${data.hasNextPage})"
            )
            LoadResult.Page(
                data = data.subscriptionList,
                prevKey = null,// if (page <= 0) null else page - 1,
                nextKey = if (data.hasNextPage) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}