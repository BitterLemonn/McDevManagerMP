package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import com.lemon.mcdevmanagermp.ui.viewmodel.SplashViewAction
import com.lemon.mcdevmanagermp.ui.viewmodel.SplashViewEffect
import com.lemon.mcdevmanagermp.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_mc
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SplashPage(
    navController: NavController,
    showSnackBar: (String, String) -> Unit = { _, _ -> },
    viewmodel: SplashViewModel = viewModel()
) {
    var waitingLast = 0
    LaunchedEffect(key1 = Unit) {
        this.launch(Dispatchers.IO) {
            while (waitingLast < 2) {
                waitingLast++
                delay(1000)
            }
            withContext(Dispatchers.Main) {
                navController.navigate(Screen.MainPage) {
                    launchSingleTop = true
                    popUpTo(Screen.SplashPage) { inclusive = true }
                }
            }
        }
        viewmodel.dispatch(SplashViewAction.GetDatabase)
    }

    BasePage(
        viewEffect = viewmodel.viewEffect,
        onEffect = { effect ->
            when (effect) {
                is SplashViewEffect.RouteToPath -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        while (waitingLast < 2) {
                            delay(100)
                        }
                        withContext(Dispatchers.Main) {
                            navController.navigate(effect.path) {
                                launchSingleTop = true
                                popUpTo(Screen.SplashPage) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isSystemInDarkTheme()) Color(0xFF417C54)
                    else Color(0xFF50C878)
                )
        ) {
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_mc),
                    contentDescription = "icon",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(200.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "开发者管理器",
                    color = TextWhite,
                    fontSize = 26.sp,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae)),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SplashPagePreview() {
    SplashPage(rememberNavController())
}