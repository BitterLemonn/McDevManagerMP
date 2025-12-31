package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.AnalyzeApi
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.common.CookiesStore
import com.lemon.mcdevmanagermp.data.common.NETEASE_USER_COOKIE
import com.lemon.mcdevmanagermp.data.netease.income.OneResRealtimeIncomeBean
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler
import com.lemon.mcdevmanagermp.utils.getPreviousDay
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class RealtimeProfitRepository {
    companion object {
        val INSTANCE by lazy { RealtimeProfitRepository() }
        private val analyzeApi = AnalyzeApi.INSTANCE
    }

    suspend fun getOneDayDetail(
        platform: String,
        iid: String,
        date: String
    ): NetworkState<OneResRealtimeIncomeBean> {
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)

            return UnifiedExceptionHandler.handleRequest {
                analyzeApi.getOneResRealtimeIncome(
                    platform = if (platform == "pe") "pe" else "comp",
                    iid = iid,
                    beginTime = getPreviousDay(date) + "T16:00:00.000Z",
                    endTime = date + "T15:59:59.999Z"
                )
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录",
            UnifiedExceptionHandler.CookiesExpiredException()
        )
    }

    suspend fun getOneMonthDetail(
        platform: String,
        iid: String,
        year: Int,
        month: Int
    ): NetworkState<OneResRealtimeIncomeBean> {
        val cookie = AppContext.cookiesStore[AppContext.nowNickname]
        if (cookie != null) {
            CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)

            val currentMonthFirstDay = LocalDate(year, month, 1)

            // 这个月 -9 天
            val currentMonthResultDate = currentMonthFirstDay
                .plus(1, DateTimeUnit.MONTH) // 变成下个月1号
                .minus(10, DateTimeUnit.DAY) // 减10天

            // 上个月 -9 天
            val lastMonthResultDate = currentMonthFirstDay
                .minus(10, DateTimeUnit.DAY)

            return UnifiedExceptionHandler.handleRequest {
                analyzeApi.getOneResRealtimeIncome(
                    platform = if (platform == "pe") "pe" else "comp",
                    iid = iid,
                    beginTime = lastMonthResultDate.toString() + "T16:00:00.000Z",
                    endTime = currentMonthResultDate.toString() + "T15:59:59.999Z"
                )
            }
        }
        return NetworkState.Error(
            "无法获取用户cookie, 请重新登录",
            UnifiedExceptionHandler.CookiesExpiredException()
        )
    }
}