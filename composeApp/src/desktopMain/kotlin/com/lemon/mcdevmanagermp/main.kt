package com.lemon.mcdevmanagermp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.lemon.mcdevmanagermp.data.database.DriverFactory
import com.lemon.mcdevmanagermp.di.AppDependencies

fun main() = application {
    // 初始化数据库依赖
    val driverFactory = DriverFactory()
    AppDependencies.initialize(driverFactory)

    Window(
        onCloseRequest = ::exitApplication,
        title = "MCDevManagerMP",
    ) {
        App()
    }
}