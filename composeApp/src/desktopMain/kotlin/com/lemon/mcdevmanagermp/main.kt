package com.lemon.mcdevmanagermp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.scaffold.AppScaffold

fun main() = application {
    // 初始化数据库依赖
    val databaseFactory = DatabaseFactory()
    AppConstant.database = databaseFactory.create().build()

    Window(
        onCloseRequest = ::exitApplication,
        title = "MCDevManagerMP",
    ) {
        AppScaffold()
    }
}