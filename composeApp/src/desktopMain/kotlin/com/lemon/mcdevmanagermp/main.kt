package com.lemon.mcdevmanagermp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.ui.PlatformTitleBar
import com.lemon.mcdevmanagermp.ui.scaffold.AppScaffold
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
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
        AppScaffold()
    }
}