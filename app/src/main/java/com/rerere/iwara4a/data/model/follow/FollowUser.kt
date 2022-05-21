package com.rerere.iwara4a.data.model.follow

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "following_user")
data class FollowUser(
    @PrimaryKey(autoGenerate = true)
    val idKey: Int = 0,
    val id: String,
    var name: String,
    var profilePic: String
)