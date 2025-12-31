package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DrawerValue
import androidx.compose.material.ModalDrawer
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.ui.widget.AccountManagerDrawer
import com.lemon.mcdevmanagermp.ui.widget.FunctionCard
import com.lemon.mcdevmanagermp.ui.widget.MainUserCard
import com.lemon.mcdevmanagermp.ui.widget.ProfitCard
import com.lemon.mcdevmanagermp.ui.widget.ProfitWidget
import com.lemon.mcdevmanagermp.ui.widget.TipsCard
import com.lemon.mcdevmanagermp.viewmodel.MainViewAction
import com.lemon.mcdevmanagermp.viewmodel.MainViewEffect
import com.lemon.mcdevmanagermp.viewmodel.MainViewModel
import com.lemon.mcdevmanagermp.widget.SNACK_ERROR
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_analyze
import mcdevmanagermp.composeapp.generated.resources.ic_comment_line
import mcdevmanagermp.composeapp.generated.resources.ic_feedback
import mcdevmanagermp.composeapp.generated.resources.ic_notice
import mcdevmanagermp.composeapp.generated.resources.ic_profit
import mcdevmanagermp.composeapp.generated.resources.ic_setting
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainPage(
    navController: NavController,
    viewModel: MainViewModel = viewModel { MainViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val states = viewModel.viewStates.collectAsState().value
    var isShowNotice by remember { mutableStateOf(false) }
    var isShowLastMonthProfit by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(MainViewAction.LoadData(AppContext.nowNickname))
    }
    BasePage(
        viewEffect = viewModel.viewEffects,
        onEffect = { effect ->
            when (effect) {
                is MainViewEffect.ShowToast -> showToast(effect.msg, SNACK_ERROR)
                is MainViewEffect.RouteToPath -> navController.navigate(effect.path) {
                    if (effect.needPop) popUpTo(Screen.MainPage) { inclusive = true }
                    launchSingleTop = true
                }

                is MainViewEffect.MaybeDataNoRefresh -> isShowNotice = true
                is MainViewEffect.ShowLastMonthProfit -> isShowLastMonthProfit = true
            }
        }
    ) {
        ModalDrawer(
            modifier = Modifier.fillMaxWidth(0.5f),
            drawerState = drawerState,
            drawerBackgroundColor = Color.Transparent,
            drawerElevation = 0.dp,
            drawerContent = {
                AccountManagerDrawer(
                    accountList = AppContext.accountList,
                    onClick = { viewModel.dispatch(MainViewAction.ChangeAccount(it)) },
                    onDismiss = { viewModel.dispatch(MainViewAction.DeleteAccount(it)) },
                    onLogout = { viewModel.dispatch(MainViewAction.DeleteAccount(AppContext.nowNickname)) },
                    onRightClick = { navController.navigate(Screen.LoginPage) })
            }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                ) {
                    MainUserCard(
                        username = states.username,
                        avatarUrl = states.avatarUrl,
                        mainLevel = states.mainLevel,
                        subLevel = states.subLevel,
                        levelText = states.levelText,
                        maxLevelExp = states.maxLevelExp,
                        currentExp = states.currentExp,
                        canLevelUp = states.canLevelUp,
                        contributeScore = states.contributionScore,
                        contributeRank = states.contributionRank,
                        contributeClass = states.contributionClass,
                        netGameScore = states.netGameScore,
                        netGameRank = states.netGameRank,
                        netGameClass = states.netGameClass,
                        dataDate = states.contributionMonth,
                        enableAvatarClick = false
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        ProfitWidget(
                            curMonthProfit = states.curMonthProfit,
                            curMonthDl = states.curMonthDl,
                            lastMonthProfit = states.lastMonthProfit,
                            lastMonthDl = states.lastMonthDl,
                            yesterdayDl = states.yesterdayDl,
                            yesterdayProfit = states.yesterdayProfit,
                            halfAvgProfit = states.halfAvgProfit,
                            halfAvgDl = states.halfAvgDl,
                            isLoading = states.isLoadingOverview
                        )
                        AnimatedVisibility(
                            visible = isShowNotice,
                            enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300)
                            ),
                            exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                                animationSpec = tween(300)
                            )
                        ) {
                            TipsCard(
                                headerIcon = Res.drawable.ic_notice,
                                content = "昨日数据可能未更新",
                                dismissText = "知道了"
                            ) { isShowNotice = false }
                        }
                        ProfitCard(
                            title = "本月收益速算",
                            realMoney = states.realMoney,
                            taxMoney = states.taxMoney,
                            isLoading = states.isLoadingProfit
                        )
                        AnimatedVisibility(
                            visible = isShowLastMonthProfit,
                            enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                animationSpec = tween(300)
                            ),
                            exit = fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                                animationSpec = tween(300)
                            )
                        ) {
                            ProfitCard(
                                title = "上月收益速算",
                                realMoney = states.lastRealMoney,
                                taxMoney = states.lastTaxMoney,
                                isLoading = states.isLoadingProfit
                            )
                        }
                        FunctionCard(icon = Res.drawable.ic_analyze, title = "数据分析") {
                            navController.navigate(Screen.AnalyzePage) {
                                launchSingleTop = true
                            }
                        }
                        FunctionCard(icon = Res.drawable.ic_feedback, title = "玩家反馈") {
                            navController.navigate(Screen.FeedbackPage) {
                                launchSingleTop = true
                            }
                        }
                        FunctionCard(icon = Res.drawable.ic_comment_line, title = "组件评论") {
                            navController.navigate(Screen.CommentPage) {
                                launchSingleTop = true
                            }
                        }
                        FunctionCard(icon = Res.drawable.ic_profit, title = "收益管理") {
                            navController.navigate(Screen.ProfitPage) {
                                launchSingleTop = true
                            }
                        }
                        FunctionCard(icon = Res.drawable.ic_setting, title = "设置") {
                            navController.navigate(Screen.SettingPage) {
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview() {
    val navController = rememberNavController()
    MainPage(navController)
}