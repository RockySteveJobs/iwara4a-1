package com.rerere.iwara4a.repo

import androidx.annotation.IntRange
import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.comment.CommentPostParam
import com.rerere.iwara4a.model.index.MediaList
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SortType
import com.rerere.iwara4a.model.index.SubscriptionList
import com.rerere.iwara4a.model.playlist.PlaylistAction
import com.rerere.iwara4a.model.playlist.PlaylistPreview
import com.rerere.iwara4a.model.session.Session
import javax.inject.Inject

class MediaRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun getSubscriptionList(
        session: Session,
        @IntRange(from = 0) page: Int
    ): Response<SubscriptionList> = iwaraApi.getSubscriptionList(session, page)

    suspend fun getMediaList(
        session: Session,
        mediaType: MediaType,
        page: Int,
        sortType: SortType,
        filters: Set<String>
    ) = iwaraApi.getMediaList(session, mediaType, page, sortType, filters)

    suspend fun getImageDetail(session: Session, imageId: String) =
        iwaraApi.getImagePageDetail(session, imageId)

    suspend fun getVideoDetail(session: Session, videoId: String) =
        iwaraApi.getVideoPageDetail(session, videoId)

    suspend fun like(session: Session, like: Boolean, link: String) =
        iwaraApi.like(session, like, link)

    suspend fun follow(session: Session, follow: Boolean, link: String) =
        iwaraApi.follow(session, follow, link)

    suspend fun loadComment(session: Session, mediaType: MediaType, mediaId: String, page: Int) =
        iwaraApi.getCommentList(session, mediaType, mediaId, page)

    suspend fun search(
        session: Session,
        query: String,
        page: Int,
        sort: SortType,
        filter: Set<String>
    ): Response<MediaList> = iwaraApi.search(
        session, query, page, sort, filter
    )

    suspend fun getLikePage(session: Session, page: Int) = iwaraApi.getLikePage(session, page)

    suspend fun getPlaylistPreview(session: Session, nid: Int): Response<PlaylistPreview> =
        iwaraApi.getPlaylistPreview(session, nid)

    suspend fun modifyPlaylist(
        session: Session,
        nid: Int,
        playlist: Int,
        action: PlaylistAction
    ): Response<Int> = iwaraApi.modifyPlaylist(session, nid, playlist, action)

    suspend fun getUserVideoList(
        session: Session,
        userIdOnVideo: String,
        @IntRange(from = 0) page: Int
    ): Response<MediaList> = iwaraApi.getUserVideoList(
        session = session,
        userIdOnVideo = userIdOnVideo,
        page = page
    )

    suspend fun postComment(
        session: Session,
        nid: Int,
        commentId: Int?,
        content: String,
        commentPostParam: CommentPostParam
    ) {
        iwaraApi.postComment(session, nid, commentId, content, commentPostParam)
    }
}