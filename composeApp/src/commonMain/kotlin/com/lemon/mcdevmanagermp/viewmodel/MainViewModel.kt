package com.lemon.mcdevmanagermp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.database.entities.OverviewEntity
import com.lemon.mcdevmanagermp.data.netease.rankList.CommonRankListResponseBean
import com.lemon.mcdevmanagermp.data.page.RankCategoryContent
import com.lemon.mcdevmanagermp.data.page.RankCategoryData
import com.lemon.mcdevmanagermp.data.page.RankCategoryTypeEnum
import com.lemon.mcdevmanagermp.data.page.RankListItemData
import com.lemon.mcdevmanagermp.data.netease.rankList.RankListResponseBean
import com.lemon.mcdevmanagermp.data.page.RankSubCategoryTypeEnum
import com.lemon.mcdevmanagermp.data.page.commonRankCategoryContent
import com.lemon.mcdevmanagermp.data.netease.resource.ResourceBean
import com.lemon.mcdevmanagermp.data.repository.DetailRepository
import com.lemon.mcdevmanagermp.data.repository.MainRepository
import com.lemon.mcdevmanagermp.data.repository.RealtimeProfitRepository
import com.lemon.mcdevmanagermp.extension.IUiAction
import com.lemon.mcdevmanagermp.extension.IUiEffect
import com.lemon.mcdevmanagermp.extension.IUiState
import com.lemon.mcdevmanagermp.extension.createEffectFlow
import com.lemon.mcdevmanagermp.extension.formatDecimal
import com.lemon.mcdevmanagermp.extension.sendEffect
import com.lemon.mcdevmanagermp.extension.setState
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler
import com.lemon.mcdevmanagermp.utils.logout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.math.min
import kotlin.time.Clock
import kotlin.time.Instant

class MainViewModel : ViewModel() {
    private val mainRepository = MainRepository.INSTANCE
    private val realtimeProfitRepository = RealtimeProfitRepository.INSTANCE
    private val overviewRepository = DetailRepository.INSTANCE

    private val _viewStates = MutableStateFlow(MainViewState())
    val viewStates = _viewStates.asStateFlow()
    private val _viewEffects = createEffectFlow<MainViewEffect>()
    val viewEffects = _viewEffects.asSharedFlow()

    fun dispatch(action: MainViewAction) {
        when (action) {
            is MainViewAction.LoadData -> loadData(action.forceReload)
            is MainViewAction.DeleteAccount -> deleteAccount(action.accountName)
            is MainViewAction.ChangeAccount -> changeAccount(action.accountName)
            is MainViewAction.GetRankData -> getRankData(action.category, action.subCategory)
        }
    }

    private fun loadData(forceReload: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            flow<Unit> {
                // 先获取用户信息
                getUserInfoLogic()

                // 再获取概览信息
                getOverviewLogic(forceReload)
            }.onStart {
                // 开始加载时，设置为加载状态
                _viewStates.setState { copy(isLoadingOverview = true) }
            }.onCompletion {
                // 完成后，取消加载状态
                _viewStates.setState { copy(isLoadingOverview = false) }
            }.catch {
                // 处理异常
                Logger.e("加载数据失败: ${it.message}")
                sendEffect(_viewEffects, MainViewEffect.ShowToast("加载数据失败: ${it.message}"))
            }.collect()
        }
    }

    private suspend fun getUserInfoLogic() {
        when (val result = mainRepository.getUserInfo()) {
            is NetworkState.Success -> {
                getLevelInfoLogic()
                result.data?.let { userInfo ->
                    AppContext.curUserInfo = userInfo
                    _viewStates.setState {
                        copy(
                            username = userInfo.nickname, avatarUrl = userInfo.headImg ?: avatarUrl
                        )
                    }
                } ?: run {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    sendEffect(
                        _viewEffects, MainViewEffect.ShowToast("无法获取用户信息, 请重新登录")
                    )
                    logout(AppContext.nowNickname)
                }
            }

            is NetworkState.Error -> {
                when (result.e) {
                    is UnifiedExceptionHandler.CookiesExpiredException -> {
                        sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                        sendEffect(_viewEffects, MainViewEffect.ShowToast("登录过期, 请重新登录"))
                        logout(AppContext.nowNickname)
                    }

                    else -> {
                        sendEffect(_viewEffects, MainViewEffect.ShowToast(result.msg))
                    }
                }
            }
        }
    }

    private suspend fun getLevelInfoLogic() {
        when (val levelInfo = mainRepository.getLevelInfo()) {
            is NetworkState.Success -> {
                levelInfo.data?.let {
                    val levelText = when (it.currentClass) {
                        1 -> "元气新星"
                        2 -> "巧手工匠"
                        3 -> "杰出精英"
                        4 -> "创造大师"
                        5 -> "传奇宗师"
                        else -> "元气新星"
                    }
                    _viewStates.setState {
                        copy(
                            mainLevel = it.currentClass,
                            subLevel = it.currentLevel,
                            levelText = "$levelText LV. ${it.currentLevel}",
                            maxLevelExp = it.expCeiling,
                            currentExp = it.totalExp,
                            canLevelUp = it.upgradeClassAchieve,
                            contributionMonth = it.contributionMonth,
                            netGameClass = it.contributionNetGameClass,
                            netGameRank = it.contributionNetGameRank,
                            netGameScore = it.contributionNetGameScore,
                            contributionClass = it.contributionClass,
                            contributionRank = it.contributionRank,
                            contributionScore = it.contributionScore
                        )
                    }
                } ?: throw Exception("获取等级信息失败")
            }

            is NetworkState.Error -> {
                when (levelInfo.e) {
                    is UnifiedExceptionHandler.CookiesExpiredException -> {
                        sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                        sendEffect(_viewEffects, MainViewEffect.ShowToast("登录过期, 请重新登录"))
                        logout(AppContext.nowNickname)
                    }

                    else -> {
                        sendEffect(_viewEffects, MainViewEffect.ShowToast(levelInfo.msg))
                    }
                }
            }
        }
    }

    private suspend fun getOverviewLogic(forceReload: Boolean) {
        val overviewEntity = withContext(Dispatchers.IO) {
            AppConstant.database.infoDao().getLatestOverviewByNickname(AppContext.nowNickname)
        }

        if (overviewEntity != null) {
            val instant = Instant.fromEpochMilliseconds(overviewEntity.timestamp)
            val chinaZoneId = TimeZone.of("Asia/Shanghai")
            val chinaTime = instant.toLocalDateTime(chinaZoneId)

            var isLoad = false
            if (overviewEntity.yesterdayDownload != 0) isLoad = true
            else if (chinaTime.hour > 11 || (chinaTime.hour == 11 && chinaTime.minute >= 30))
                isLoad = true
            if (isDifferentDay(overviewEntity.timestamp)) isLoad = false

            if (isLoad && !forceReload) {
                _viewStates.setState {
                    copy(
                        curMonthProfit = overviewEntity.thisMonthDiamond,
                        curMonthDl = overviewEntity.thisMonthDownload,
                        lastMonthProfit = overviewEntity.lastMonthDiamond,
                        lastMonthDl = overviewEntity.lastMonthDownload,
                        yesterdayProfit = overviewEntity.yesterdayDiamond,
                        halfAvgProfit = overviewEntity.days14AverageDiamond,
                        yesterdayDl = overviewEntity.yesterdayDownload,
                        halfAvgDl = overviewEntity.days14AverageDownload,
                        realMoney = overviewEntity.thisMonthProfit,
                        taxMoney = overviewEntity.thisMonthTax,
                        lastRealMoney = overviewEntity.lastMonthProfit,
                        lastTaxMoney = overviewEntity.lastMonthTax,
                        isLoadingProfit = false
                    )
                }
            } else getOverviewByServer()
        } else getOverviewByServer()

        val now = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Shanghai"))
        if (now.day <= 10) sendEffect(_viewEffects, MainViewEffect.ShowLastMonthProfit)
    }

    private suspend fun getOverviewByServer() {
        // 只有需要请求概览数据时才请求资源列表
        _viewStates.setState { copy(isLoadingProfit = true) }
        getResListLogic()
        when (val overview = mainRepository.getOverview()) {
            is NetworkState.Success -> {
                overview.data?.let {
                    _viewStates.setState {
                        copy(
                            curMonthProfit = it.thisMonthDiamond,
                            curMonthDl = it.thisMonthDownload,
                            lastMonthProfit = it.lastMonthDiamond,
                            lastMonthDl = it.lastMonthDownload,
                            yesterdayProfit = it.yesterdayDiamond,
                            halfAvgProfit = it.days14AverageDiamond,
                            yesterdayDl = it.yesterdayDownload,
                            halfAvgDl = it.days14AverageDownload
                        )
                    }
                    // 保存概览信息到数据库
                    withContext(Dispatchers.IO) {
                        val overviewEntity = OverviewEntity(
                            nickname = AppContext.nowNickname,
                            days14AverageDownload = it.days14AverageDownload,
                            days14AverageDiamond = it.days14AverageDiamond,
                            days14TotalDownload = it.days14TotalDownload,
                            days14TotalDiamond = it.days14TotalDiamond,
                            lastMonthDiamond = it.lastMonthDiamond,
                            lastMonthDownload = it.lastMonthDownload,
                            thisMonthDiamond = it.thisMonthDiamond,
                            thisMonthDownload = it.thisMonthDownload,
                            yesterdayDiamond = it.yesterdayDiamond,
                            yesterdayDownload = it.yesterdayDownload
                        )
                        AppConstant.database.infoDao().insertOverview(overviewEntity)
                    }
                    // 如果昨日数据为0, 且当前时间在11点之前, 则提示用户数据可能未刷新
                    if (it.yesterdayDiamond == 0 && it.yesterdayDownload == 0) {
                        val chinaZoneId = TimeZone.of("Asia/Shanghai")
                        val currentTimeInChina = Clock.System.now().toLocalDateTime(chinaZoneId)
                        val currentHourInChina = currentTimeInChina.hour

                        if (currentHourInChina < 11) {
                            sendEffect(_viewEffects, MainViewEffect.MaybeDataNoRefresh)
                        }
                    }
                } ?: throw Exception("获取概览信息失败")
            }

            is NetworkState.Error -> {
                when (overview.e) {
                    is UnifiedExceptionHandler.CookiesExpiredException -> {
                        sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                        sendEffect(_viewEffects, MainViewEffect.ShowToast("登录过期, 请重新登录"))
                        logout(AppContext.nowNickname)
                    }

                    else -> {
                        sendEffect(_viewEffects, MainViewEffect.ShowToast(overview.msg))
                    }
                }
            }
        }
    }

    private suspend fun getResListLogic() {
        when (val result = overviewRepository.getAllResource("pe")) {
            is NetworkState.Success -> {
                result.data?.let { _viewStates.setState { copy(resList = it.item.filter { it.onlineTime != "UNKNOWN" }) } }
                computeMoney()
            }

            is NetworkState.Error -> {
                Logger.e("获取资源列表失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取资源列表失败: ${result.msg}")
                }
            }
        }
    }

    private fun deleteAccount(accountName: String) {
        val isLogout = accountName == AppContext.nowNickname
        viewModelScope.launch {
            logout(accountName)
            if (isLogout) sendEffect(
                _viewEffects,
                MainViewEffect.RouteToPath(Screen.LoginPage, true)
            )
        }
    }

    private fun changeAccount(accountName: String) {
        viewModelScope.launch {
            delay(100)
            AppContext.nowNickname = accountName
            loadData(true)
        }
    }

    private fun computeMoney() {
        viewModelScope.launch {
            val timeZone = TimeZone.of("Asia/Shanghai") // 或 TimeZone.currentSystemDefault()
            val now = Clock.System.now().toLocalDateTime(timeZone)

            val thisMonthProfit = getOneMonthProfit(now.year, now.month.number) / 100.0

            val lastMonthDate = now.date.minus(1, DateTimeUnit.MONTH)
            val lastMonthProfit =
                getOneMonthProfit(lastMonthDate.year, lastMonthDate.month.number) / 100.0

            val realMoney = getRealMoney(thisMonthProfit)
            val taxMoney = getTaxMoney(realMoney)

            val lastRealMoney = getRealMoney(lastMonthProfit)
            val lastTaxMoney = getTaxMoney(lastRealMoney)

            val taxMoneyStr = taxMoney.formatDecimal(2)
            val realMoneyStr = (realMoney - taxMoney).formatDecimal(2)
            val lastTaxMoneyStr = lastTaxMoney.formatDecimal(2)
            val lastRealMoneyStr = (lastRealMoney - lastTaxMoney).formatDecimal(2)
            _viewStates.setState {
                copy(
                    isLoadingProfit = false,
                    realMoney = realMoneyStr,
                    taxMoney = taxMoneyStr,
                    lastRealMoney = lastRealMoneyStr,
                    lastTaxMoney = lastTaxMoneyStr
                )
            }

            withContext(Dispatchers.IO) {
                val dataBase = AppConstant.database.infoDao()
                val latestOverview = dataBase.getLatestOverviewByNickname(AppContext.nowNickname)
                if (latestOverview != null) {
                    val updatedOverview = latestOverview.copy(
                        thisMonthProfit = realMoneyStr,
                        thisMonthTax = taxMoneyStr,
                        lastMonthProfit = lastRealMoneyStr,
                        lastMonthTax = lastTaxMoneyStr,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    dataBase.insertOverview(updatedOverview)
                }
            }
        }
    }

    private suspend fun getOneMonthProfit(year: Int, month: Int): Double =
        withContext(Dispatchers.IO) {
            try {
                val deferreds = viewStates.value.resList.map { itemId ->
                    async {
                        when (val result =
                            realtimeProfitRepository.getOneMonthDetail(
                                "pe",
                                itemId.itemId,
                                year,
                                month
                            )) {
                            is NetworkState.Success -> {
                                result.data?.totalDiamonds ?: throw Exception("获取收益失败")
                            }

                            is NetworkState.Error -> {
                                Logger.e("获取收益失败: ${result.msg}")
                                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                                    sendEffect(
                                        _viewEffects,
                                        MainViewEffect.RouteToPath(Screen.LoginPage, true)
                                    )
                                    throw result.e
                                } else {
                                    throw Exception("获取收益失败: ${result.msg}")
                                }
                            }
                        }
                    }
                }
                deferreds.awaitAll().sumOf { (it as Number).toDouble() }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    sendEffect(_viewEffects, MainViewEffect.ShowToast(e.message ?: "未知错误"))
                    _viewStates.setState { copy(isLoadingProfit = false) }
                } else {
                    Logger.d(e.message ?: "未知错误")
                }
                0.0
            }
        }

    private fun isDifferentDay(timestamp: Long): Boolean {
        val timeZone = TimeZone.of("Asia/Shanghai")
        val inputDate = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(timeZone).date
        val currentDate = Clock.System.now().toLocalDateTime(timeZone).date
        return inputDate != currentDate
    }

    private fun getRealMoney(profit: Double): Double {
        // 渠道分成
        val channelMoney = profit * 0.35
        val lastMoney = profit - channelMoney

        // 网易阶段分成
        var level1 = if (profit > 1500) min(profit - 1500, 50000.0) * 0.3 else 0.0
        var level2 = if (profit > 50000) (profit - 50000) * 0.4 else 0.0
        val neteaseMoney = level1 + level2
        val neteasePercent = if (profit > 0) neteaseMoney / profit else 0.0

        // 技术服务费
        level1 = if (profit > 100000) min(profit - 100000, 1000000.0) * 0.1 else 0.0
        level2 = if (profit > 1000000) min(profit - 1000000, 3000000.0) * 0.15 else 0.0
        val level3 = if (profit > 3000000) min(profit - 3000000, 5000000.0) * 0.2 else 0.0
        val level4 = if (profit > 5000000) (profit - 5000000) * 0.25 else 0.0
        val serviceMoney = level1 + level2 + level3 + level4
        val servicePercent = if (profit > 0) serviceMoney / profit else 0.0

        return lastMoney - (lastMoney * neteasePercent) - (lastMoney * servicePercent)
    }

    private fun getTaxMoney(realMoney: Double): Double {
        return if (realMoney < 800) 0.0 else if (realMoney < 4000) (realMoney - 800) * 0.2 else (realMoney * 0.8) * 0.2
    }

    private fun getRankData(
        category: RankCategoryTypeEnum,
        subCategory: RankSubCategoryTypeEnum? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            flow<Unit> {
                val firstType = when (subCategory) {
                    RankSubCategoryTypeEnum.MOD -> 2
                    RankSubCategoryTypeEnum.MAP -> 1
                    RankSubCategoryTypeEnum.RESOURCE_PACK -> 3
                    RankSubCategoryTypeEnum.SERVER -> 6
                    else -> 0
                }

                val firstTypePc = when (subCategory) {
                    RankSubCategoryTypeEnum.MOD -> 3
                    RankSubCategoryTypeEnum.MAP -> 5
                    RankSubCategoryTypeEnum.RESOURCE_PACK -> 4
                    RankSubCategoryTypeEnum.SERVER -> 11
                    else -> 0
                }
                val subCategoryName = subCategory?.typeName
                when (category) {
                    RankCategoryTypeEnum.PE_HOT -> getPeHotRankListLogic(firstType, subCategoryName)
                    RankCategoryTypeEnum.HOT_SEARCH -> getHotSearchRankListLogic(firstType)
                    RankCategoryTypeEnum.PE_DOWNLOAD -> getPeDownloadRankListLogic(
                        firstType,
                        subCategoryName
                    )

                    RankCategoryTypeEnum.PE_SELL -> getPeSellRankListLogic(
                        firstType,
                        subCategoryName
                    )

                    RankCategoryTypeEnum.PC_DOWNLOAD -> getPcDownloadRankListLogic(
                        firstTypePc,
                        subCategoryName
                    )

                    RankCategoryTypeEnum.PC_LIKE -> getPcLikeRankListLogic(
                        firstTypePc,
                        subCategoryName
                    )
                }
            }.catch {
                if (it !is CancellationException) {
                    sendEffect(_viewEffects, MainViewEffect.ShowToast(it.message ?: "未知错误"))
                } else {
                    Logger.d(it.message ?: "未知错误")
                }
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private fun updateRankListState(
        category: String,
        subCategory: String? = null,
        items: List<RankListItemData>
    ) {
        _viewStates.setState {
            val newList = rankListData.map { item ->
                if (item.categoryTitle == category) {
                    val newContent = when (val content = item.content) {
                        is RankCategoryContent.Single -> content.copy(list = items)
                        is RankCategoryContent.Multi -> {
                            val newGroups = content.groups.map { group ->
                                if (group.categoryName == subCategory) group.copy(data = items)
                                else group
                            }
                            RankCategoryContent.Multi(newGroups)
                        }
                    }
                    item.copy(content = newContent)
                } else item
            }
            copy(rankListData = newList)
        }
    }

    private fun updateCommonRankListState(
        category: String,
        subCategory: String?,
        items: RankListResponseBean<CommonRankListResponseBean>
    ) {
        val rankItems = items.data.mapIndexed { index, bean ->
            RankListItemData(
                title = bean.itemName,
                rank = index + 1,
                rankChange = bean.rankChange,
                isNew = bean.isNew
            )
        }
        // 更新状态
        updateRankListState(category, subCategory, rankItems)
    }

    private suspend fun getPeHotRankListLogic(firstType: Int, subCategoryName: String? = null) {
        when (val result = mainRepository.getPeHotRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    val rankItems = rankListResponse.data.mapIndexed { index, bean ->
                        RankListItemData(
                            title = bean.resName,
                            rank = index + 1,
                            rankChange = bean.rankChange,
                            isNew = bean.isNew,
                            imgUrl = bean.iconUrl
                        )
                    }
                    // 更新状态
                    updateRankListState(
                        RankCategoryTypeEnum.PE_HOT.typeName,
                        subCategoryName,
                        rankItems
                    )
                } ?: throw Exception("获取手游热门飙升榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取手游热门飙升榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取手游热门飙升榜失败: ${result.msg}")
                }
            }
        }
    }

    private suspend fun getHotSearchRankListLogic(firstType: Int) {
        when (val result = mainRepository.getHotSearchRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    val rankItems = rankListResponse.data.mapIndexed { index, bean ->
                        RankListItemData(
                            title = bean.content,
                            rank = index + 1,
                            rankChange = bean.rankChange,
                            isNew = bean.isNew
                        )
                    }
                    // 更新状态
                    updateRankListState(RankCategoryTypeEnum.HOT_SEARCH.typeName, null, rankItems)
                } ?: throw Exception("获取热搜榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取热搜榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取热搜榜失败: ${result.msg}")
                }
            }
        }
    }

    private suspend fun getPeDownloadRankListLogic(
        firstType: Int,
        subCategoryName: String? = null
    ) {
        when (val result = mainRepository.getPeDownloadRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    updateCommonRankListState(
                        RankCategoryTypeEnum.PE_DOWNLOAD.typeName,
                        subCategoryName,
                        rankListResponse
                    )
                } ?: throw Exception("获取手游免费榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取手游免费榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取手游免费榜失败: ${result.msg}")
                }
            }
        }
    }

    private suspend fun getPeSellRankListLogic(firstType: Int, subCategoryName: String? = null) {
        when (val result = mainRepository.getPeSellRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    updateCommonRankListState(
                        RankCategoryTypeEnum.PE_SELL.typeName,
                        subCategoryName,
                        rankListResponse
                    )
                } ?: throw Exception("获取手游畅销榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取手游畅销榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取手游畅销榜失败: ${result.msg}")
                }
            }
        }
    }

    private suspend fun getPcDownloadRankListLogic(
        firstType: Int,
        subCategoryName: String? = null
    ) {
        when (val result = mainRepository.getPcDownloadRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    updateCommonRankListState(
                        RankCategoryTypeEnum.PC_DOWNLOAD.typeName,
                        subCategoryName,
                        rankListResponse
                    )
                } ?: throw Exception("获取端游下载榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取端游下载榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取端游下载榜失败: ${result.msg}")
                }
            }
        }
    }

    private suspend fun getPcLikeRankListLogic(firstType: Int, subCategoryName: String? = null) {
        when (val result = mainRepository.getPcLikeRankList(firstType)) {
            is NetworkState.Success -> {
                result.data?.let { rankListResponse ->
                    updateCommonRankListState(
                        RankCategoryTypeEnum.PC_LIKE.typeName,
                        subCategoryName,
                        rankListResponse
                    )
                } ?: throw Exception("获取端游点赞榜失败")
            }

            is NetworkState.Error -> {
                Logger.e("获取端游点赞榜失败: ${result.msg}")
                if (result.e is UnifiedExceptionHandler.CookiesExpiredException) {
                    sendEffect(_viewEffects, MainViewEffect.RouteToPath(Screen.LoginPage, true))
                    throw result.e
                } else {
                    throw Exception("获取端游点赞榜失败: ${result.msg}")
                }
            }
        }
    }
}

data class MainViewState(
    val curMonthProfit: Int = 0,
    val curMonthDl: Int = 0,
    val lastMonthProfit: Int = 0,
    val lastMonthDl: Int = 0,
    val yesterdayProfit: Int = 0,
    val halfAvgProfit: Int = 0,
    val yesterdayDl: Int = 0,
    val halfAvgDl: Int = 0,
    val isLoadingOverview: Boolean = false,

    val username: String = "开发者",
    val avatarUrl: String = "https://gss0.baidu.com/-fo3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/bd315c6034a85edf3b752e104b540923dd54750c.jpg",

    val mainLevel: Int = 0,
    val subLevel: Int = 0,
    val levelText: String = "",
    val maxLevelExp: Double = 1.0,
    val currentExp: Double = 1.0,
    val canLevelUp: Boolean = false,

    val contributionMonth: String = "",
    val netGameClass: Int = 0,
    val netGameRank: Int = 0,
    val netGameScore: String = "0",
    val contributionClass: Int = 0,
    val contributionRank: Int = 0,
    val contributionScore: String = "0",

    val resList: List<ResourceBean> = emptyList(),
    val rankListData: List<RankCategoryData> = listOf(
        RankCategoryData(RankCategoryTypeEnum.PE_HOT.typeName, commonRankCategoryContent),
        RankCategoryData(
            RankCategoryTypeEnum.HOT_SEARCH.typeName,
            RankCategoryContent.Single(emptyList())
        ),
        RankCategoryData(RankCategoryTypeEnum.PE_DOWNLOAD.typeName, commonRankCategoryContent),
        RankCategoryData(RankCategoryTypeEnum.PE_SELL.typeName, commonRankCategoryContent),
        RankCategoryData(RankCategoryTypeEnum.PC_DOWNLOAD.typeName, commonRankCategoryContent),
        RankCategoryData(RankCategoryTypeEnum.PC_LIKE.typeName, commonRankCategoryContent)
    ),

    val realMoney: String = "0.00",
    val taxMoney: String = "0.00",

    val lastRealMoney: String = "0.00",
    val lastTaxMoney: String = "0.00",

    val isLoadingProfit: Boolean = false
) : IUiState

sealed class MainViewEffect : IUiEffect {
    data class RouteToPath(val path: Screen, val needPop: Boolean = false) : MainViewEffect()
    data class ShowToast(val msg: String) : MainViewEffect()
    data object MaybeDataNoRefresh : MainViewEffect()
    data object ShowLastMonthProfit : MainViewEffect()
}

sealed class MainViewAction : IUiAction {
    data class LoadData(val forceReload: Boolean = false) : MainViewAction()
    data class DeleteAccount(val accountName: String) : MainViewAction()
    data class ChangeAccount(val accountName: String) : MainViewAction()
    data class GetRankData(
        val category: RankCategoryTypeEnum,
        val subCategory: RankSubCategoryTypeEnum? = null
    ) : MainViewAction()
}