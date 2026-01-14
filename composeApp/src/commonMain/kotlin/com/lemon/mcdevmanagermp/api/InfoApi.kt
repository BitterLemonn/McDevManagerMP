package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.rankList.CommonRankListResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.HotSearchResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.PeHotResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.RankListResponseBean
import com.lemon.mcdevmanagermp.data.netease.resource.ResourceResponseBean
import com.lemon.mcdevmanagermp.data.netease.user.LevelInfoBean
import com.lemon.mcdevmanagermp.data.netease.user.OverviewBean
import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface InfoApi {

    @GET("users/me")
    suspend fun getUserInfo(): ResponseData<UserInfoBean>

    @GET("data_analysis/overview")
    suspend fun getOverview(): ResponseData<OverviewBean>

    @GET("new_level")
    suspend fun getLevelInfo(): ResponseData<LevelInfoBean>

    @GET("items/categories/{platform}")
    suspend fun getResInfoList(
        @Path("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<ResourceResponseBean>

    @GET("square/us_rank_list/?type=pe_hot")
    suspend fun getPeHotRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<PeHotResponseBean>>

    @GET("square/us_rank_list/?type=hot_search")
    suspend fun getHotSearchRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<HotSearchResponseBean>>

    @GET("square/rank_list/?type=pe_download")
    suspend fun getPeDownloadRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<CommonRankListResponseBean>>

    @GET("square/rank_list/?type=pe_sell")
    suspend fun getPeSellRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<CommonRankListResponseBean>>

    @GET("square/rank_list/?type=pc_download")
    suspend fun getPcDownloadRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<CommonRankListResponseBean>>

    @GET("square/rank_list/?type=pc_like")
    suspend fun getPcLikeRankList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 50,
        @Query("first_type") firstType: Int
    ): ResponseData<RankListResponseBean<CommonRankListResponseBean>>

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideKtorfit(NETEASE_MC_DEV_LINK).createInfoApi()
        }
    }
}