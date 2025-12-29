package com.lemon.mcdevmanagermp.scaffold

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.AppTheme
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.ui.page.SplashPage
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.widget.AppSnackbar
import kotlinx.coroutines.launch

@Composable
fun AppScaffold() {

    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleScope = remember(lifecycleOwner) { lifecycleOwner.lifecycleScope }

    fun showToast(msg: String, flag: String) {
        lifecycleScope.launch {
            snackBarHostState.showSnackbar(msg, flag)
        }
    }

    MCDevManagerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    snackbar = { AppSnackbar(data = it) }
                )
            }
        ) {
            NavHost(
                navController = navController,
                modifier = Modifier
                    .background(color = AppTheme.colors.background)
                    .fillMaxSize(),
                startDestination = Screen.SplashPage
            ) {
                // 启动页
                composable<Screen.SplashPage> {
                    SplashPage(
                        navController = navController,
                        showSnackBar = { msg, flag -> showToast(msg, flag) }
                    )
                }
                // 登录页
                composable<Screen.LoginPage> {
//                LoginPage(
//                    navController = navController,
//                    showToast = { msg, flag -> showToast(msg, flag) })
                }
            }
        }
    }
}