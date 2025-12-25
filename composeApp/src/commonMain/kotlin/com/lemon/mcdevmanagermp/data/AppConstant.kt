package com.lemon.mcdevmanagermp.data

import com.lemon.mcdevmanagermp.data.database.Database

object AppConstant {
    const val DATABASE_VERSION = 1
    lateinit var database: Database

    val userInfoMap = mutableMapOf<String, String>()
}