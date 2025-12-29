package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.JSONConverter
import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.income.OneResRealtimeIncomeBean
import com.lemon.mcdevmanagermp.data.netease.resource.NewResDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResMonthDetailResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResourceResponseBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies

interface AnalyzeApi : BaseApi {
    @GET("/items/categories/{platform}/")
    suspend fun getAllResource(
        @Path("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<ResourceResponseBean>

    @GET("/data_analysis/day_detail/")
    suspend fun getDayDetail(
        @Query("platform") platform: String,
        @Query("category") category: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("item_list_str") itemListStr: String,
        @Query("sort") sort: String = "dateid",
        @Query("order") order: String = "ASC",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<ResDetailResponseBean>

    @GET("/data_analysis/day_detail/")
    suspend fun getNewDayDetail(
        @Query("platform") platform: String,
        @Query("category") category: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("item_list_str") itemListStr: String,
        @Query("sort") sort: String = "dateid",
        @Query("order") order: String = "ASC",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE,
        @Query("is_need_us_rank_data") isNeedUsRankData: Boolean = true
    ): ResponseData<NewResDetailResponseBean>

    @GET("/data_analysis/month_detail/")
    suspend fun getMonthDetail(
        @Query("platform") platform: String,
        @Query("category") category: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("sort") sort: String = "monthid",
        @Query("order") order: String = "DESC",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE,
        @Query("day_sort") daySort: String = "cnt_buy",
        @Query("day_span") daySpan: Int = Int.MAX_VALUE,
        @Query("day_dateid") dayDateId: String
    ): ResponseData<ResMonthDetailResponseBean>

    @GET("/items/categories/{platform}/{iid}/incomes/")
    suspend fun getOneResRealtimeIncome(
        @Path("platform") platform: String,
        @Path("iid") iid: String,
        @Query("begin_time") beginTime: String,
        @Query("end_time") endTime: String
    ): ResponseData<OneResRealtimeIncomeBean>
}