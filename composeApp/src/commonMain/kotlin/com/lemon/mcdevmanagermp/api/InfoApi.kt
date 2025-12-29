package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.netease.resource.ResourceResponseBean
import com.lemon.mcdevmanagermp.data.netease.user.LevelInfoBean
import com.lemon.mcdevmanagermp.data.netease.user.OverviewBean
import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import retrofit2.Call

interface InfoApi {

    @GET("/users/me")
    fun getUserInfo(): Call<ResponseData<UserInfoBean>>

    @GET("/data_analysis/overview")
    fun getOverview(): Call<ResponseData<OverviewBean>>

    @GET("/new_level")
    fun getLevelInfo(): Call<ResponseData<LevelInfoBean>>

    @GET("/items/categories/{platform}")
    suspend fun getResInfoList(
        @Path("platform") platform: String = "pe",
        @Query("start") start: Int = 0,
        @Query("span") span: Int = Int.MAX_VALUE
    ): ResponseData<ResourceResponseBean>
}