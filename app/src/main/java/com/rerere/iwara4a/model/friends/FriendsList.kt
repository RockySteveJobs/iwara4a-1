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

    // 已通过
    ACCEPTED,

    // 未知
    UNKNOWN
}