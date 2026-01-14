package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.common.MAX_COMPACT_WIDTH
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import com.lemon.mcdevmanagermp.ui.widget.AppLoadingWidget
import com.lemon.mcdevmanagermp.ui.widget.BottomNameInput
import com.lemon.mcdevmanagermp.ui.widget.LoginOutlineTextField
import com.lemon.mcdevmanagermp.ui.widget.SNACK_ERROR
import com.lemon.mcdevmanagermp.ui.widget.SNACK_INFO
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.viewmodel.LoginViewAction
import com.lemon.mcdevmanagermp.viewmodel.LoginViewEffect
import com.lemon.mcdevmanagermp.viewmodel.LoginViewModel
import com.lt.compose_views.util.rememberMutableStateOf
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_mc
import mcdevmanagermp.composeapp.generated.resources.ic_no_show
import mcdevmanagermp.composeapp.generated.resources.ic_show
import mcdevmanagermp.composeapp.generated.resources.img_login_bg
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginPageCompact(
    navController: NavController = rememberNavController(),
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val states by viewModel.viewState.collectAsState()

    var isUseCookies by remember { mutableStateOf(false) }
    var isShowTitle by remember { mutableStateOf(true) }
    var isShowPassword by remember { mutableStateOf(false) }

    val animatedUsername by animateDpAsState(
        targetValue = if (isUseCookies) 0.dp else 60.dp,
        animationSpec = tween(durationMillis = 150),
        label = ""
    )

    BasePage(
        viewEffect = viewModel.viewEffect, onEffect = { effect ->
            when (effect) {
                is LoginViewEffect.LoginFailed -> showToast(effect.message, SNACK_ERROR)
                is LoginViewEffect.RouteToPath -> navController.navigate(effect.path) {
                    popUpTo(Screen.LoginPage) { inclusive = true }
                    launchSingleTop = true
                }

                is LoginViewEffect.ShowToast -> showToast(
                    effect.message, if (effect.isError) SNACK_ERROR else SNACK_INFO
                )
            }
        }) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)
                    .imePadding()
            ) {
                if (isShowTitle) Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                        .padding(top = 30.dp)
                ) {
                    Text(
                        text = "登录",
                        fontSize = 40.sp,
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        color = AppTheme.colors.textColor,
                        letterSpacing = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    Modifier.fillMaxWidth().align(Alignment.Center)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_mc),
                        contentDescription = "",
                        modifier = Modifier.align(Alignment.CenterHorizontally).width(120.dp)
                            .aspectRatio(1f),
                        colorFilter = ColorFilter.lighting(
                            multiply = AppTheme.colors.imgTintColor, add = Color.Transparent
                        )
                    )
                    AnimatedVisibility(
                        visible = !isUseCookies,
                        enter = fadeIn(animationSpec = tween(durationMillis = 150)),
                        exit = fadeOut(animationSpec = tween(durationMillis = 150))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(20.dp))
                            LoginOutlineTextField(
                                value = states.username,
                                onValueChange = {
                                    viewModel.dispatch(LoginViewAction.UpdateUsername(it))
                                },
                                label = { Text("邮箱") },
                                modifier = Modifier.height(animatedUsername),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Ascii,
                                    imeAction = ImeAction.Next
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    // 密码 or Cookies
                    LoginOutlineTextField(
                        value = if (isUseCookies) states.cookies else states.password,
                        onValueChange = {
                            if (isUseCookies) viewModel.dispatch(
                                LoginViewAction.UpdateCookies(
                                    it
                                )
                            )
                            else viewModel.dispatch(LoginViewAction.UpdatePassword(it))
                        },
                        label = { Text(text = if (isUseCookies) "Cookies" else "密码") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            showKeyboardOnFocus = false,
                            keyboardType = KeyboardType.Ascii
                        ),
                        visualTransformation = if (!isUseCookies && !isShowPassword) PasswordVisualTransformation()
                        else VisualTransformation.None,
                        keyboardActions = KeyboardActions(onDone = {
                            viewModel.dispatch(LoginViewAction.Login)
                            keyboardController?.hide()
                        }),
                        singleLine = !isUseCookies,
                        trialingIcon = {
                            if (!isUseCookies) {
                                Box(
                                    modifier = Modifier.size(30.dp).clip(CircleShape).clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        isShowPassword = !isShowPassword
                                    }) {
                                    Image(
                                        painter = painterResource(
                                            if (isShowPassword) Res.drawable.ic_no_show
                                            else Res.drawable.ic_show
                                        ),
                                        contentDescription = "visibility",
                                        colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor),
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        })
                    // 切换登录方式
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier.align(Alignment.CenterEnd).clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                viewModel.dispatch(LoginViewAction.UpdateCookies(""))
                                isUseCookies = !isUseCookies
                                isShowPassword = false
                            }.padding(10.dp)
                        ) {
                            Text(
                                text = if (!isUseCookies) "使用Cookies登录 >" else "使用账号密码登录 >",
                                color = AppTheme.colors.primaryColor,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                    // 登录按钮
                    Button(
                        onClick = {
                            viewModel.dispatch(LoginViewAction.Login)
                            keyboardController?.hide()
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.primaryColor,
                            contentColor = TextWhite
                        )
                    ) {
                        Text(
                            text = "登录", fontSize = 16.sp, modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = states.isStartLogin,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AppLoadingWidget()
        }
    }
}

@Composable
fun LoginPageWide(
    navController: NavController = rememberNavController(),
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val states by viewModel.viewState.collectAsState()

    var isLoginSuccess by remember { mutableStateOf(false) }
    var isUseCookies by remember { mutableStateOf(false) }
    var isShowPassword by remember { mutableStateOf(false) }

    val animatedUsername by animateDpAsState(
        targetValue = if (isUseCookies) 0.dp else 60.dp,
        animationSpec = tween(durationMillis = 150),
        label = ""
    )

    BasePage(
        viewEffect = viewModel.viewEffect, onEffect = { effect ->
            when (effect) {
                is LoginViewEffect.LoginFailed -> showToast(effect.message, SNACK_ERROR)
                is LoginViewEffect.RouteToPath -> navController.navigate(effect.path) {
                    popUpTo(Screen.LoginPage) { inclusive = true }
                    launchSingleTop = true
                }

                is LoginViewEffect.ShowToast -> showToast(
                    effect.message, if (effect.isError) SNACK_ERROR else SNACK_INFO
                )
            }
        }) {
        Box(
            modifier = Modifier.fillMaxSize().background(AppTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.width(MAX_COMPACT_WIDTH)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_mc),
                    contentDescription = "",
                    modifier = Modifier.align(Alignment.CenterHorizontally).width(120.dp)
                        .aspectRatio(1f),
                    colorFilter = ColorFilter.lighting(
                        multiply = AppTheme.colors.imgTintColor, add = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    AnimatedVisibility(
                        visible = !isLoginSuccess,
                        exit = fadeOut(animationSpec = tween(durationMillis = 150))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AnimatedVisibility(
                                visible = !isUseCookies,
                                enter = fadeIn(animationSpec = tween(durationMillis = 150)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 150))
                            ) {
                                Column {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    LoginOutlineTextField(
                                        value = states.username,
                                        onValueChange = {
                                            viewModel.dispatch(LoginViewAction.UpdateUsername(it))
                                        },
                                        label = { Text("邮箱") },
                                        modifier = Modifier.height(animatedUsername),
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Ascii,
                                            imeAction = ImeAction.Next
                                        )
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                            // 密码 or Cookies
                            LoginOutlineTextField(
                                value = if (isUseCookies) states.cookies else states.password,
                                onValueChange = {
                                    if (isUseCookies) viewModel.dispatch(
                                        LoginViewAction.UpdateCookies(
                                            it
                                        )
                                    )
                                    else viewModel.dispatch(LoginViewAction.UpdatePassword(it))
                                },
                                label = { Text(text = if (isUseCookies) "Cookies" else "密码") },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    showKeyboardOnFocus = false,
                                    keyboardType = KeyboardType.Ascii
                                ),
                                visualTransformation = if (!isUseCookies && !isShowPassword) PasswordVisualTransformation()
                                else VisualTransformation.None,
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.dispatch(LoginViewAction.Login)
                                    keyboardController?.hide()
                                }),
                                singleLine = !isUseCookies,
                                trialingIcon = {
                                    if (!isUseCookies) {
                                        Box(
                                            modifier = Modifier.size(30.dp).clip(CircleShape)
                                                .clickable(
                                                    indication = null,
                                                    interactionSource = remember { MutableInteractionSource() }) {
                                                    isShowPassword = !isShowPassword
                                                }) {
                                            Image(
                                                painter = painterResource(
                                                    if (isShowPassword) Res.drawable.ic_no_show
                                                    else Res.drawable.ic_show
                                                ),
                                                contentDescription = "visibility",
                                                colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor),
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }
                                })
                            Spacer(modifier = Modifier.height(40.dp))
                            // 登录按钮
                            Button(
                                onClick = {
                                    viewModel.dispatch(LoginViewAction.Login)
                                    keyboardController?.hide()
                                },
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 2.dp,
                                    hoveredElevation = 6.dp,
                                    focusedElevation = 6.dp
                                ),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppTheme.colors.primaryColor,
                                    contentColor = TextWhite
                                )
                            ) {
                                Text(
                                    text = "登录",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "或",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.hintColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            // 切换登录方式
                            Button(
                                onClick = {
                                    isUseCookies = !isUseCookies
                                    isShowPassword = false
                                },
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 2.dp,
                                    hoveredElevation = 6.dp,
                                    focusedElevation = 6.dp
                                ),
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppTheme.colors.secondaryColor,
                                    contentColor = TextWhite
                                )
                            ) {
                                Text(
                                    text = if (!isUseCookies) "使用Cookies登录" else "使用账号密码登录",
                                    fontSize = 16.sp,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = states.isStartLogin,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AppLoadingWidget()
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun LoginPagePreview() {
    MCDevManagerTheme {
        Box(Modifier.fillMaxSize().background(AppTheme.colors.background)) {
            LoginPageCompact()
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 1024, heightDp = 768)
private fun LoginPageWidePreview() {
    MCDevManagerTheme {
        Box(Modifier.fillMaxSize().background(AppTheme.colors.background)) {
            LoginPageWide()
        }
    }
}