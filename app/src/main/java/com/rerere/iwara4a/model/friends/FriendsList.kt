package com.rerere.iwara4a.model.friends

typealias FriendList = List<Friend>

data class Friend(
    val username: String,
    val userId: String,
    val frId: Int,
    val date: String,
    val friendStatus: FriendStatus
)

enum class FriendStatus {
    // 等待通过
    PENDING,

    // 自己发的好友请求, 等待对方通过
    PENDING_REQUEST,

    // 已通过
    ACCEPTED,

    // 未知
    UNKNOWN
}