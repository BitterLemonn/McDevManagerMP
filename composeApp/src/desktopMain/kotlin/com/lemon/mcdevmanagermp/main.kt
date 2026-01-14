package com.lemon.mcdevmanagermp

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.ui.scaffold.AppScaffold
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.utils.WindowStateUtils
import java.io.PrintStream

fun main() = application {
    System.setOut(PrintStream(System.out, true, "UTF-8"))
    System.setErr(PrintStream(System.err, true, "UTF-8"))

    // 初始化数据库依赖
    val databaseFactory = DatabaseFactory()
    AppConstant.database = databaseFactory.create().build()

    // 删除过期日志文件
    Logger.autoDeleteOldLogs()

    val windowState = remember { WindowStateUtils.loadState() }

    Window(
        onCloseRequest = {
            WindowStateUtils.saveState(windowState)
            exitApplication()
        },
        state = windowState,
        title = "MCDevManagerMP"
    ) {
//        window.minimumSize = Dimension(800, 768)
        AppScaffold()
    }
}