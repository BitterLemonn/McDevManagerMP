package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.income.ApplyIncomeBean
import com.lemon.mcdevmanagermp.data.netease.income.ApplyIncomeDetailBean
import com.lemon.mcdevmanagermp.data.netease.income.IncentiveListBean
import com.lemon.mcdevmanagermp.data.netease.income.IncomeDetailBean
import com.lemon.mcdevmanagermp.utils.NoNeedData
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface IncomeApi {

    // 结算收益
    @PUT("/incomes/apply")
    suspend fun applyIncome(
        @Body request: ApplyIncomeBean
    ): ResponseData<NoNeedData>

    // 获取结算信息
    @GET("/incomes")
    suspend fun getIncome(
        @Query("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<IncomeDetailBean>

    // 获取结算详情
    @GET("/incomes/{id}")
    suspend fun getApplyDetail(
        @Path("id") id: String
    ): ResponseData<ApplyIncomeDetailBean>

    // 获取激励金
    @GET("/incentive_fund/detail")
    suspend fun getIncentiveFund(
        @Query("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<IncentiveListBean>

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideKtorfit(NETEASE_MC_DEV_LINK).createIncomeApi()
        }
    }
}