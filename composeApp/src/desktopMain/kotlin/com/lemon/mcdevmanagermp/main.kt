package com.lemon.mcdevmanagermp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.ui.scaffold.AppScaffold
import com.lemon.mcdevmanagermp.utils.Logger

fun main() = application {
    // 初始化数据库依赖
    val databaseFactory = DatabaseFactory()
    AppConstant.database = databaseFactory.create().build()

    // 初始化日志
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", Logger.LOG_LEVEL)

    Window(
        onCloseRequest = ::exitApplication,
        title = "MCDevManagerMP",
    ) {
        AppScaffold()
    }
}