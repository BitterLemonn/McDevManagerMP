package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.Database
import com.lemon.mcdevmanagermp.data.database.DriverFactory
import com.lemon.mcdevmanagermp.data.database.createDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DatabaseRepository(private val driverFactory: DriverFactory) {
    private val database: Database = createDatabase(driverFactory)

    //初始化数据库检查
    suspend fun initializeDatabase(): Boolean {
        val version = AppConstant.DATABASE_VERSION
        return withContext(Dispatchers.IO) {
            try {
                // 这里可以执行数据库初始化操作
                // TODO 比如检查表是否存在，执行迁移等
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
