package com.rerere.iwara4a.api.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rerere.iwara4a.api.oreno3d.Oreno3dApi
import com.rerere.iwara4a.api.oreno3d.OrenoSort
import com.rerere.iwara4a.model.oreno3d.OrenoPreview

private const val TAG = "OrenoSource"

class OrenoSource(
    private val oreno3dApi: Oreno3dApi,
    private val orenoSort: OrenoSort
) : PagingSource<Int, OrenoPreview>() {
    override fun getRefreshKey(state: PagingState<Int, OrenoPreview>): Int {
        return 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrenoPreview> {
        val page = params.key ?: 1

        Log.i(TAG, "load: load list (page: $page, sort: ${orenoSort.value})")

        val response = oreno3dApi.getVideoList(
            page = page,
            sort = orenoSort
        )

        return if(response.isSuccess()){
            LoadResult.Page(
                data = response.read().list,
                prevKey = if(page > 1) page - 1 else null,
                nextKey = if(response.read().hasNext) page + 1 else null
            )
        }else {
            LoadResult.Error(throwable = Throwable(response.errorMessage()))
        }
    }
}