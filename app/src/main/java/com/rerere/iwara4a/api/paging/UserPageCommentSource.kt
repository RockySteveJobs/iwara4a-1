package com.rerere.iwara4a.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.session.SessionManager
import com.rerere.iwara4a.repo.UserRepo

class UserPageCommentSource(
    private val sessionManager: SessionManager,
    private val userRepo: UserRepo,
    private val userId: String
) : PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 0

        val response = userRepo.getUserPageComment(sessionManager.session, userId, page)

        return if (response.isSuccess()) {
            LoadResult.Page(
                data = response.read().comments,
                prevKey = if (page <= 0) null else page - 1,
                nextKey = if (response.read().hasNext) page + 1 else null
            )
        } else {
            LoadResult.Error(Exception(response.errorMessage()))
        }
    }
}