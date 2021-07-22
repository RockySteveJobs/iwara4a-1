package com.rerere.iwara4a.model.user

data class UserData(
    val userId: String,
    val username: String,
    val userIdMedia: String,

    val follow: Boolean,
    val followLink: String,

    val pic: String,
    val joinDate: String,
    val lastSeen: String,
    val about: String
){
    companion object{
        val LOADING = UserData(
            "",
            "",
            "",
            false,
            "",
            "",
            "",
            "",
            ""
        )
    }
}