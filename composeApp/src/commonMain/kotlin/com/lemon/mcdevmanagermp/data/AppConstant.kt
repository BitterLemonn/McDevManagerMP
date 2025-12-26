package com.lemon.mcdevmanagermp.data

import com.lemon.mcdevmanagermp.data.database.AppDatabase

object AppConstant {
    lateinit var database: AppDatabase
    val userInfoMap = mutableMapOf<String, String>()
}