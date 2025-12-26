package com.lemon.mcdevmanagermp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lemon.mcdevmanagermp.data.database.entities.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM userEntity WHERE nickname = :nickname")
    suspend fun getUserByNickname(nickname: String): UserEntity?

    @Query("SELECT * FROM userEntity")
    suspend fun getAllUsers(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM userEntity WHERE nickname = :nickname")
    suspend fun deleteUserByNickname(nickname: String)
}