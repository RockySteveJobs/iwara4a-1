package com.rerere.iwara4a.data.repo

import androidx.annotation.IntRange
import com.rerere.iwara4a.data.api.IwaraApi
import com.rerere.iwara4a.data.api.Response
import com.rerere.iwara4a.data.api.backend.Iwara4aBackendAPI
import com.rerere.iwara4a.data.model.comment.CommentPostParam
import com.rerere.iwara4a.data.model.detail.video.VideoDetail
import com.rerere.iwara4a.data.model.index.MediaList
import com.rerere.iwara4a.data.model.index.MediaType
import com.rerere.iwara4a.data.model.index.SubscriptionList
import com.rerere.iwara4a.data.model.playlist.PlaylistAction
import com.rerere.iwara4a.data.model.playlist.PlaylistDetail
import com.rerere.iwara4a.data.model.playlist.PlaylistOverview
import com.rerere.iwara4a.data.model.playlist.PlaylistPreview
import com.rerere.iwara4a.data.model.session.Session
import com.rerere.iwara4a.ui.component.SortType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepo @Inject constructor(
    private val iwaraApi: IwaraApi,
    private val iwara4aBackendAPI: Iwara4aBackendAPI
) {
    suspend fun getSubscriptionList(
        session: Session,
        @IntRange(from = 0) page: Int
    ): Response<SubscriptionList> = iwaraApi.getSubscriptionList(session, page)

    suspend fun getMediaList(
        session: Session,
        mediaType: MediaType,
        page: Int,
        sortType: SortType = SortType.DATE,
        filters: Set<String> = hashSetOf()
    ) = iwaraApi.getMediaList(session, mediaType, page, sortType, filters)

    suspend fun getImageDetail(session: Session, imageId: String) =
        iwaraApi.getImagePageDetail(session, imageId)

    suspend fun getVideoDetail(session: Session, videoId: String) =
        iwaraApi.getVideoPageDetail(session, videoId)

    suspend fun getVideoDetailFast(videoId: String): VideoDetail? = try {
        val result = iwara4aBackendAPI.fetchVideoDetail(videoId)
        VideoDetail(
            id = result.id,
            title = result.title,
            description = result.description,
            nid = result.nid,
            authorId = result.authorId,
            authorName = result.authorName,
            authorPic = result.authorPic,
            likes = result.likes,
            watchs = result.watchs,
            comments = 0,
            follow = false,
            followLink = "",
            likeLink = "",
            isLike = false,
            preview = result.preview,
            postDate = result.postDate,
            commentPostParam = VideoDetail.PRIVATE.commentPostParam,
            moreVideo = emptyList(),
            recommendVideo = emptyList()
        )
    } catch (e: Exception){
        e.printStackTrace()
        null
    }

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
    ): Response<MediaList> = iwaraApi.getUserMediaList(
        session = session,
        userIdOnVideo = userIdOnVideo,
        mediaType = MediaType.VIDEO,
        page = page
    )

    suspend fun getUserImageList(
        session: Session,
        userIdOnVideo: String,
        @IntRange(from = 0) page: Int
    ): Response<MediaList> = iwaraApi.getUserMediaList(
        session = session,
        userIdOnVideo = userIdOnVideo,
        mediaType = MediaType.IMAGE,
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

    suspend fun getPlaylistOverview(session: Session): Response<List<PlaylistOverview>> =
        iwaraApi.getPlaylistOverview(session)

    suspend fun getPlaylistDetail(session: Session, playlistId: String): Response<PlaylistDetail> =
        iwaraApi.getPlaylistDetail(session, playlistId)

    suspend fun createPlaylist(session: Session, title: String) =
        iwaraApi.createPlaylist(session, title)

    suspend fun deletePlaylist(session: Session, id: Int): Response<Boolean> =
        iwaraApi.deletePlaylist(session, id)

    suspend fun changePlaylistName(session: Session, id: Int, name: String): Response<Boolean> =
        iwaraApi.changePlaylistName(session, id, name)
}