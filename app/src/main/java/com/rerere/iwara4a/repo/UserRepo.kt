package com.rerere.iwara4a.repo

import androidx.annotation.IntRange
import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.comment.CommentList
import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.model.user.UserData
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun login(username: String, password: String): Response<Session> =
        iwaraApi.login(username, password)

    suspend fun getSelf(session: Session): Response<Self> = iwaraApi.getSelf(session)

    suspend fun getUser(session: Session, userId: String): Response<UserData> =
        iwaraApi.getUser(session, userId)

    suspend fun getUserPageComment(
        session: Session,
        userId: String,
        @IntRange(from = 0) page: Int
    ): Response<CommentList> = iwaraApi.getUserPageComment(session, userId, page)
}