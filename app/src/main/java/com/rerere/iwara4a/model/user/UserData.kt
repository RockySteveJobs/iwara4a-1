package com.rerere.iwara4a.model.user

data class UserData(
    val userId: String,
    val username: String,
    val userIdMedia: String,

    val follow: Boolean,
    val followLink: String,

    val friend: UserFriendState,
    val id: Int,

    val pic: String,
    val joinDate: String,
    val lastSeen: String,
    val about: String
) {
    companion object {
        val LOADING = UserData(
            "",
            "",
            "",
            false,
            "",
            UserFriendState.NOT,
            0,
            "",
            "",
            "",
            ""
        )
    }
}

enum class UserFriendState {
    NOT,
    PENDING,
    ALREADY
}