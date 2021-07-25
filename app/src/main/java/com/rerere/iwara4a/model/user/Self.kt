package com.rerere.iwara4a.model.user

data class Self(
    val id: String,
    val nickname: String,
    val profilePic: String,
    val about: String? = null
){
    companion object {
        val GUEST = Self("","访客", "https://ecchi.iwara.tv/sites/all/themes/main/img/logo.png")
    }
}