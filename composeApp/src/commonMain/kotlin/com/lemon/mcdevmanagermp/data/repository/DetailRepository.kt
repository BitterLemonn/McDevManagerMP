package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.AnalyzeApi
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.common.CookiesStore
import com.lemon.mcdevmanagermp.data.common.NETEASE_USER_COOKIE
import com.lemon.mcdevmanagermp.data.netease.resource.NewResDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResMonthDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResourceResponseBean
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler.handleRequest
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus

class DetailRepository {
    companion object {
        val INSTANCE by lazy { DetailRepository() }
        private val analyzeApi = AnalyzeApi.INSTANCE
        private val compactFormat = LocalDate.Format {
            year()
            monthNumber() // 两位数月份 (01-12)
            day()  // 两位数日期 (01-31)
        }
    }

    suspend fun getAllResource(platform: String): NetworkState<ResourceResponseBean> {
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)
            return handleRequest {
                analyzeApi.getAllResource(if (platform == "pe") "pe" else "comp")
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录", UnifiedExceptionHandler.CookiesExpiredException()
        )
    }

    suspend fun getDailyDetail(
        platform: String,
        startDate: String,
        endDate: String,
        itemList: List<String>,
        sort: String = "dateid",
        order: String = "ASC",
        start: Int = 0,
        span: Int = Int.MAX_VALUE
    ): NetworkState<ResDetailResponseBean> {
        val itemListStr = itemList.joinToString(",")
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)
            return handleRequest {
                analyzeApi.getDayDetail(
                    platform = platform,
                    category = if (platform == "pe") "pe" else "comp",
                    startDate = startDate,
                    endDate = endDate,
                    itemListStr = itemListStr,
                    sort = sort,
                    order = order,
                    start = start,
                    span = span
                )
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录",
            UnifiedExceptionHandler.CookiesExpiredException()
        )
    }

    suspend fun getNewDailyDetail(
        platform: String,
        startDate: String,
        endDate: String,
        item: String,
        sort: String = "dateid",
        order: String = "ASC",
        start: Int = 0,
        span: Int = Int.MAX_VALUE
    ): NetworkState<NewResDetailResponseBean> {
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)
            return handleRequest {
                analyzeApi.getNewDayDetail(
                    platform = platform,
                    category = if (platform == "pe") "pe" else "comp",
                    startDate = startDate,
                    endDate = endDate,
                    itemListStr = item,
                    sort = sort,
                    order = order,
                    start = start,
                    span = span
                )
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录",
            UnifiedExceptionHandler.CookiesExpiredException()
        )
    }

    suspend fun getMonthDetail(
        platform: String,
        startMonth: String,
        endMonth: String,
        sort: String = "monthid",
        order: String = "DESC",
        start: Int = 0,
        span: Int = Int.MAX_VALUE
    ): NetworkState<ResMonthDetailResponseBean> {
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)
            return handleRequest {
                analyzeApi.getMonthDetail(
                    platform = platform,
                    category = if (platform == "pe") "pe" else "comp",
                    startDate = startMonth,
                    endDate = endMonth,
                    sort = sort,
                    order = order,
                    start = start,
                    span = span,
                    dayDateId = calculatePreviousMonth(endMonth)
                )
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录",
            UnifiedExceptionHandler.CookiesExpiredException()
        )
    }

    private fun calculatePreviousMonth(endMonth: String): String {
        try {
            val localDate = LocalDate.parse(endMonth, compactFormat)
            val prevDate = localDate.minus(1, DateTimeUnit.MONTH)
            return compactFormat.format(prevDate)
        } catch (e: Exception) {
            // 处理解析错误
            return ""
        }
    }
}