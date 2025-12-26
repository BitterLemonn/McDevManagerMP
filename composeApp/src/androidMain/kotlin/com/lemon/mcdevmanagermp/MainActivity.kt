package com.lemon.mcdevmanagermp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DatabaseFactory
import com.lemon.mcdevmanagermp.utils.AndroidLogContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 初始化日志
        System.setProperty("kotlin-logging-to-android-native", "true")
        AndroidLogContext.setContext(this)

        // 初始化数据库依赖
        val databaseFactory = DatabaseFactory(this)
        AppConstant.database = databaseFactory.create().build()

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}