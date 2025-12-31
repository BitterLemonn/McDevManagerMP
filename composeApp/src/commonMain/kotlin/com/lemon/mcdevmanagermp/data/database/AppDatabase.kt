package com.lemon.mcdevmanagermp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lemon.mcdevmanagermp.data.database.dao.InfoDao
import com.lemon.mcdevmanagermp.data.database.dao.UserDao
import com.lemon.mcdevmanagermp.data.database.entities.AnalyzeEntity
import com.lemon.mcdevmanagermp.data.database.entities.OverviewEntity
import com.lemon.mcdevmanagermp.data.database.entities.UserEntity

@Database(entities = [UserEntity::class, OverviewEntity::class, AnalyzeEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    abstract fun infoDao(): InfoDao
}