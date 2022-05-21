package com.rerere.iwara4a.data.repo

import androidx.annotation.IntRange
import com.rerere.iwara4a.data.api.IwaraApi
import com.rerere.iwara4a.data.api.Response
import com.rerere.iwara4a.data.model.comment.CommentList
import com.rerere.iwara4a.data.model.friends.FriendList
import com.rerere.iwara4a.data.model.session.Session
import com.rerere.iwara4a.data.model.user.Self
import com.rerere.iwara4a.data.model.user.UserData
import com.rerere.iwara4a.util.autoRetry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun login(username: String, password: String): Response<Session> =
        iwaraApi.login(username, password)

    suspend fun getSelf(session: Session): Response<Self> = autoRetry(2) { iwaraApi.getSelf(session) }

    suspend fun getUser(session: Session, userId: String): Response<UserData> =
        iwaraApi.getUser(session, userId)

    suspend fun getUserPageComment(
        session: Session,
        userId: String,
        @IntRange(from = 0) page: Int
    ): Response<CommentList> = iwaraApi.getUserPageComment(session, userId, page)

    suspend fun getFriendList(session: Session): Response<FriendList> =
        iwaraApi.getFriendList(session)
}