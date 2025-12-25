package com.lemon.mcdevmanagermp

import androidx.compose.ui.window.ComposeUIViewController
import com.lemon.mcdevmanagermp.data.database.DriverFactory
import com.lemon.mcdevmanagermp.di.AppDependencies

fun MainViewController() = ComposeUIViewController {
    // 初始化数据库依赖
    val driverFactory = DriverFactory()
    AppDependencies.initialize(driverFactory)

    App()
}
