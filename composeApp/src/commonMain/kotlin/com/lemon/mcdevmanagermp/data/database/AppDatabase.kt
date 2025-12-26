package com.lemon.mcdevmanagermp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lemon.mcdevmanagermp.data.database.dao.UserDao
import com.lemon.mcdevmanagermp.data.database.entities.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}