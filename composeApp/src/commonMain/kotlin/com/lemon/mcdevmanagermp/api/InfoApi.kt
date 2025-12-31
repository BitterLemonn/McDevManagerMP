package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.resource.ResourceResponseBean
import com.lemon.mcdevmanagermp.data.netease.user.LevelInfoBean
import com.lemon.mcdevmanagermp.data.netease.user.OverviewBean
import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface InfoApi {

    @GET("/users/me")
    suspend fun getUserInfo(): ResponseData<UserInfoBean>

    @GET("/data_analysis/overview")
    suspend fun getOverview(): ResponseData<OverviewBean>

    @GET("/new_level")
    suspend fun getLevelInfo(): ResponseData<LevelInfoBean>

    @GET("/items/categories/{platform}")
    suspend fun getResInfoList(
        @Path("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<ResourceResponseBean>

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideKtorfit(NETEASE_MC_DEV_LINK).createInfoApi()
        }
    }
}