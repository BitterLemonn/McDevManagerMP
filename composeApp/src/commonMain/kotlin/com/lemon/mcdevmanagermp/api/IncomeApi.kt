package com.lemon.mcdevmanager.api

import com.lemon.mcdevmanager.data.AddCookiesInterceptor
import com.lemon.mcdevmanager.data.CommonInterceptor
import com.lemon.mcdevmanager.data.common.JSONConverter
import com.lemon.mcdevmanager.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanager.data.netease.income.ApplyIncomeDetailBean
import com.lemon.mcdevmanager.data.netease.income.IncentiveBean
import com.lemon.mcdevmanager.data.netease.income.IncentiveListBean
import com.lemon.mcdevmanager.data.netease.income.IncomeDetailBean
import com.lemon.mcdevmanager.utils.NoNeedData
import com.lemon.mcdevmanager.utils.ResponseData
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface IncomeApi {

    // 结算收益
    @PUT("/incomes/apply")
    suspend fun applyIncome(
        @Body request: RequestBody
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
}