package com.lemon.mcdevmanagermp.ui.scaffold

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.common.LAYOUT_MODE_COMPACT
import com.lemon.mcdevmanagermp.data.common.LAYOUT_MODE_EXPANDED
import com.lemon.mcdevmanagermp.data.common.LAYOUT_MODE_MEDIUM
import com.lemon.mcdevmanagermp.data.common.MAX_COMPACT_WIDTH
import com.lemon.mcdevmanagermp.data.common.MAX_MEDIUM_WIDTH
import com.lemon.mcdevmanagermp.ui.page.LoginPageCompact
import com.lemon.mcdevmanagermp.ui.page.LoginPageWide
import com.lemon.mcdevmanagermp.ui.page.MainPageCompact
import com.lemon.mcdevmanagermp.ui.page.MainPageMedium
import com.lemon.mcdevmanagermp.ui.page.SplashPage
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.ui.widget.AppSnackbar
import com.lt.compose_views.util.rememberMutableStateOf
import kotlinx.coroutines.launch

@Composable
fun AppScaffold() {

    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }
    var windowSize by rememberMutableStateOf { LAYOUT_MODE_COMPACT }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleScope = remember(lifecycleOwner) { lifecycleOwner.lifecycleScope }

    fun showToast(msg: String, flag: String) {
        lifecycleScope.launch {
            snackBarHostState.showSnackbar(msg, flag)
        }
    }


    MCDevManagerTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(), snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState, snackbar = { AppSnackbar(data = it) })
            }) {
            BoxWithConstraints {
                windowSize = if (maxWidth <= MAX_COMPACT_WIDTH) {
                    LAYOUT_MODE_COMPACT
                } else if (maxWidth <= MAX_MEDIUM_WIDTH) {
                    LAYOUT_MODE_MEDIUM
                } else {
                    LAYOUT_MODE_EXPANDED
                }

                NavHost(
                    navController = navController,
                    modifier = Modifier.background(color = AppTheme.colors.background)
                        .fillMaxSize(),
                    startDestination = Screen.SplashPage
                ) {
                    // 启动页
                    composable<Screen.SplashPage> {
                        SplashPage(
                            navController = navController,
                            showSnackBar = { msg, flag -> showToast(msg, flag) })
                    }
                    // 登录页
                    composable<Screen.LoginPage> {
                        when (windowSize) {
                            LAYOUT_MODE_COMPACT -> {
                                LoginPageCompact(
                                    navController = navController,
                                    showToast = { msg, flag -> showToast(msg, flag) }
                                )
                            }

                            LAYOUT_MODE_MEDIUM,
                            LAYOUT_MODE_EXPANDED -> {
                                LoginPageWide(
                                    navController = navController,
                                    showToast = { msg, flag -> showToast(msg, flag) }
                                )
                            }
                        }
                    }
                    // 主页
                    composable<Screen.MainPage>(
                        exitTransition = {
                            slideOutHorizontally(
                                animationSpec = tween(200),
                                targetOffsetX = { -it }
                            )
                        }
                    ) {
                        when (windowSize) {
                            LAYOUT_MODE_COMPACT -> {
                                MainPageCompact(
                                    navController = navController,
                                    showToast = { msg, flag -> showToast(msg, flag) }
                                )
                            }

                            LAYOUT_MODE_MEDIUM,
                            LAYOUT_MODE_EXPANDED -> {
                                MainPageMedium(
                                    navController = navController,
                                    showToast = { msg, flag -> showToast(msg, flag) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}