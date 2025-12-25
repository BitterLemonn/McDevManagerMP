package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.AppTheme
import com.lemon.mcdevmanagermp.MCDevManagerTheme
import com.lemon.mcdevmanagermp.ui.viewmodel.SplashViewModel
import com.lemon.mcdevmanagermp.widget.Box
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashPage(
    navController: NavController,
    showSnackBar: (String, String) -> Unit = { _, _ -> }
) {
    val viewModel: SplashViewModel = viewModel()
    val uiState = viewModel.uiState

    // 当初始化完成后，延迟一段时间再跳转
    LaunchedEffect(uiState.isInitialized) {
        if (uiState.isInitialized && !uiState.isLoading) {
            delay(1500L) // 显示1.5秒
            // navController.navigate("main") // 根据您的导航路由调整
        }
    }

    // 显示错误提示
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            showSnackBar("错误", error)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "开发者内容管理器",
                fontSize = 20.sp,
                color = AppTheme.colors.textColor
            )

            if (uiState.appVersion.isNotEmpty()) {
                Text(
                    text = "版本 ${uiState.appVersion}",
                    fontSize = 14.sp,
                    color = AppTheme.colors.textColor,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(top = 24.dp),
                        color = AppTheme.colors.textColor
                    )
                    Text(
                        text = "正在初始化数据库...",
                        fontSize = 12.sp,
                        color = AppTheme.colors.textColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error,
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Button(
                        onClick = { viewModel.retry() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("重试")
                    }
                }

                uiState.isInitialized -> {
                    Text(
                        text = "初始化完成",
                        fontSize = 12.sp,
                        color = Color.Green,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun SplashPagePreview() {
    MCDevManagerTheme {
        SplashPage(
            navController = rememberNavController()
        )
    }
}