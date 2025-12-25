package com.lemon.mcdevmanagermp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lemon.mcdevmanagermp.data.database.DriverFactory
import com.lemon.mcdevmanagermp.di.AppDependencies

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 初始化数据库依赖
        val driverFactory = DriverFactory(this)
        AppDependencies.initialize(driverFactory)

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