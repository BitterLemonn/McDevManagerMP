package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.NavigationRail
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import com.lemon.mcdevmanagermp.ui.widget.AccountManagerDrawer
import com.lemon.mcdevmanagermp.ui.widget.ExpandableNavigateItem
import com.lemon.mcdevmanagermp.ui.widget.FunctionCard
import com.lemon.mcdevmanagermp.ui.widget.MainUserCard
import com.lemon.mcdevmanagermp.ui.widget.MultiLevelRankingCard
import com.lemon.mcdevmanagermp.ui.widget.ProfitCard
import com.lemon.mcdevmanagermp.ui.widget.ProfitSplitWidget
import com.lemon.mcdevmanagermp.ui.widget.ProfitWidget
import com.lemon.mcdevmanagermp.ui.widget.SNACK_ERROR
import com.lemon.mcdevmanagermp.ui.widget.SNACK_WARN
import com.lemon.mcdevmanagermp.ui.widget.TipsCard
import com.lemon.mcdevmanagermp.viewmodel.MainViewAction
import com.lemon.mcdevmanagermp.viewmodel.MainViewEffect
import com.lemon.mcdevmanagermp.viewmodel.MainViewModel
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_analyze
import mcdevmanagermp.composeapp.generated.resources.ic_comment_line
import mcdevmanagermp.composeapp.generated.resources.ic_dashboard
import mcdevmanagermp.composeapp.generated.resources.ic_feedback
import mcdevmanagermp.composeapp.generated.resources.ic_menu
import mcdevmanagermp.composeapp.generated.resources.ic_notice
import mcdevmanagermp.composeapp.generated.resources.ic_profit
import mcdevmanagermp.composeapp.generated.resources.ic_setting
import org.jetbrains.compose.ui.tooling.preview.Preview


// 定义常量
private val COLLAPSED_WIDTH = 80.dp
private val EXPANDED_WIDTH = 240.dp

@Composable
fun MainPageCompact(
    navController: NavController,
    viewModel: MainViewModel = viewModel { MainViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val states = viewModel.viewStates.collectAsState().value
    var isShowNotice by remember { mutableStateOf(false) }
    var isShowLastMonthProfit by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(MainViewAction.LoadData())
    }
    BasePage(
        viewEffect = viewModel.viewEffects, onEffect = { effect ->
            when (effect) {
                is MainViewEffect.ShowToast -> showToast(effect.msg, SNACK_ERROR)
                is MainViewEffect.RouteToPath -> navController.navigate(effect.path) {
                    if (effect.needPop) popUpTo(Screen.MainPage) { inclusive = true }
                    launchSingleTop = true
                }

                is MainViewEffect.MaybeDataNoRefresh -> isShowNotice = true
                is MainViewEffect.ShowLastMonthProfit -> isShowLastMonthProfit = true
            }
        }) {
        ModalNavigationDrawer(
            modifier = Modifier.fillMaxWidth(0.5f), drawerState = drawerState, drawerContent = {
                AccountManagerDrawer(
                    accountList = AppContext.accountList,
                    onClick = { viewModel.dispatch(MainViewAction.ChangeAccount(it)) },
                    onDismiss = { viewModel.dispatch(MainViewAction.DeleteAccount(it)) },
                    onLogout = { viewModel.dispatch(MainViewAction.DeleteAccount(AppContext.userName)) },
                    onRightClick = { navController.navigate(Screen.LoginPage) })
            }) {
            Box(
                modifier = Modifier.fillMaxSize().animateContentSize()
                    .padding(WindowInsets.systemBars.asPaddingValues())
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().animateContentSize()
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
                        modifier = Modifier.fillMaxSize().animateContentSize()
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

@Composable
fun MainPageMedium(
    navController: NavController,
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    // 控制展开/收起的状态
    var isExpanded by remember { mutableStateOf(false) }
    // 控制当前选中的菜单项
    var selectedItem by remember { mutableStateOf(0) }

    // 侧边栏宽度动画
    val sidebarWidth by animateDpAsState(
        targetValue = if (isExpanded) EXPANDED_WIDTH else COLLAPSED_WIDTH, animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow
        ), label = "SidebarWidthAnimation"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AppTheme.colors.card)
        ) {
            // 侧边栏
            NavigationRail(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight(),
                backgroundColor = AppTheme.colors.card,
                elevation = 0.dp
            )
            {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // 顶部：切换按钮 (汉堡菜单)
                    ExpandableNavigateItem(
                        title = "MCDEV",
                        titleWeight = FontWeight.ExtraBold,
                        titleColor = AppTheme.colors.secondaryColor,
                        icon = Res.drawable.ic_menu,
                        expanded = isExpanded
                    ) {
                        isExpanded = !isExpanded
                    }
                    Divider(color = AppTheme.colors.dividerColor, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    ExpandableNavigateItem(
                        title = "Dashboard",
                        icon = Res.drawable.ic_dashboard,
                        titleWeight = FontWeight.SemiBold,
                        iconModifier = Modifier.clip(CircleShape),
                        expanded = isExpanded,
                        selected = selectedItem == 0
                    ) {
                        selectedItem = 0
                    }
                    ExpandableNavigateItem(
                        title = "数据分析",
                        icon = Res.drawable.ic_analyze,
                        expanded = isExpanded,
                        selected = selectedItem == 1
                    ) {
                        selectedItem = 1
                    }
                    ExpandableNavigateItem(
                        title = "玩家反馈",
                        icon = Res.drawable.ic_feedback,
                        expanded = isExpanded,
                        selected = selectedItem == 2
                    ) {
                        selectedItem = 2
                    }
                    ExpandableNavigateItem(
                        title = "组件评论",
                        icon = Res.drawable.ic_comment_line,
                        expanded = isExpanded,
                        selected = selectedItem == 3
                    ) {
                        selectedItem = 3
                    }
                    ExpandableNavigateItem(
                        title = "收益管理",
                        icon = Res.drawable.ic_profit,
                        expanded = isExpanded,
                        selected = selectedItem == 4
                    ) {
                        selectedItem = 4
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    ExpandableNavigateItem(
                        title = AppContext.userName,
                        icon = AppContext.avatarUrl,
                        isTinted = false,
                        iconModifier = Modifier.clip(CircleShape),
                        expanded = isExpanded
                    ) {}
                    ExpandableNavigateItem(
                        title = "设置",
                        icon = Res.drawable.ic_setting,
                        expanded = isExpanded,
                        selected = selectedItem == 5
                    ) {
                        selectedItem = 5
                    }
                }
            }
            // 主内容区
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(bottomStart = 16.dp, topStart = 16.dp))
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .background(AppTheme.colors.background)
            ) {
                this@Row.AnimatedVisibility(
                    visible = selectedItem == 0,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) +
                            slideOutHorizontally(animationSpec = tween(300))
                ) {
                    MainPageContent(
                        navController = navController,
                        showToast = showToast
                    )
                }
                this@Row.AnimatedVisibility(
                    visible = 3 == selectedItem,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInHorizontally(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) +
                            slideOutHorizontally(animationSpec = tween(300))
                ) {
                    CommentPage(
                        navController = navController,
                        showToast = showToast
                    )
                }
            }
        }
    }
}

@Composable
private fun MainPageContent(
    navController: NavController,
    viewModel: MainViewModel = viewModel { MainViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.dispatch(MainViewAction.LoadData())
    }

    val viewStates by viewModel.viewStates.collectAsState()
    var isShowLastMonthProfit by remember { mutableStateOf(false) }

    BasePage(
        viewEffect = viewModel.viewEffects,
        onEffect = { effect ->
            when (effect) {
                is MainViewEffect.ShowToast -> showToast(effect.msg, SNACK_ERROR)
                is MainViewEffect.RouteToPath -> navController.navigate(effect.path) {
                    if (effect.needPop) popUpTo(Screen.MainPage) { inclusive = true }
                    launchSingleTop = true
                }

                is MainViewEffect.MaybeDataNoRefresh -> {
                    showToast("昨日数据可能未更新", SNACK_WARN)
                }

                is MainViewEffect.ShowLastMonthProfit -> isShowLastMonthProfit = true
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(160.dp)
                .background(AppTheme.colors.primaryColor)
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    modifier = Modifier.padding(end = 8.dp),
                    text = "Hi, ${viewStates.username}!",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    color = TextWhite
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ProfitSplitWidget(
                curMonthProfit = viewStates.curMonthProfit,
                curMonthDl = viewStates.curMonthDl,
                lastMonthProfit = viewStates.lastMonthProfit,
                lastMonthDl = viewStates.lastMonthDl,
                yesterdayDl = viewStates.yesterdayDl,
                yesterdayProfit = viewStates.yesterdayProfit,
                halfAvgProfit = viewStates.halfAvgProfit,
                halfAvgDl = viewStates.halfAvgDl,
                isLoading = viewStates.isLoadingOverview
            ) {
                viewModel.dispatch(MainViewAction.LoadData(true))
            }
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(1f)) {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(300)) +
                                slideInHorizontally(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300)) +
                                slideOutHorizontally(animationSpec = tween(300))
                    ) {
                        ProfitCard(
                            title = "本月收益速算",
                            realMoney = viewStates.realMoney,
                            taxMoney = viewStates.taxMoney,
                            elevation = 2.dp,
                            isLoading = viewStates.isLoadingProfit
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
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
                            realMoney = viewStates.lastRealMoney,
                            taxMoney = viewStates.lastTaxMoney,
                            elevation = 2.dp,
                            isLoading = viewStates.isLoadingProfit
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    MultiLevelRankingCard(
                        viewStates.rankListData
                    ) { categoryType, subCategoryType ->
                        viewModel.dispatch(
                            MainViewAction.GetRankData(
                                categoryType,
                                subCategoryType
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun MainPagePreview() {
    val navController = rememberNavController()
    MCDevManagerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.background).fillMaxSize()) {
            MainPageCompact(navController)
        }
    }
}

@Preview(showBackground = true, widthDp = 1024, heightDp = 768)
@Composable
fun MainPageWidePreview() {
    val navController = rememberNavController()
    MCDevManagerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.background).fillMaxSize()) {
            MainPageMedium(navController)
        }
    }
}