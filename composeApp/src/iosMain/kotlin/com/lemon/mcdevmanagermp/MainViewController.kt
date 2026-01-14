package com.lemon.mcdevmanagermp

import androidx.compose.ui.window.ComposeUIViewController
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.ui.scaffold.AppScaffold
import com.lemon.mcdevmanagermp.utils.Logger

fun MainViewController() = ComposeUIViewController {
    // 初始化数据库依赖
    val databaseFactory = DatabaseFactory()
    AppConstant.database = databaseFactory.create().build()

    // 删除过期日志文件
    Logger.autoDeleteOldLogs()

    AppScaffold()
}
