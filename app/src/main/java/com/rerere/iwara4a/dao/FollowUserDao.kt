package com.rerere.iwara4a.dao

import androidx.room.*
import com.rerere.iwara4a.model.follow.FollowUser
import kotlinx.coroutines.flow.Flow

@Dao
interface FollowUserDao {
    @Query("SELECT * FROM following_user")
    fun getAllFollowUsers(): Flow<List<FollowUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: FollowUser)

    @Delete
    suspend fun removeUser(user: FollowUser)

    @Update
    suspend fun updateUser(user: FollowUser)

    @Query("SELECT * FROM following_user WHERE id=:id LIMIT 1")
    suspend fun getUserById(id: String): FollowUser?
}

suspend fun FollowUserDao.insertSmart(
    id: String,
    name: String,
    profilePic: String
){
    if(id.isBlank() || name.isBlank() || profilePic.isBlank()){
        return
    }
    val localUser = getUserById(id)
    localUser?.let {
        updateUser(it.apply {
            this.name = name
            this.profilePic = profilePic
        })
    } ?: run {
        addUser(FollowUser(
            id = id,
            name = name,
            profilePic = profilePic
        ))
    }
}