package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.comment.CommentListBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CommentApi {

    @GET("/items/comment/pe/")
    suspend fun getCommentList(
        @Query("start") start: Int = 0,
        @Query("span") span: Int = 20,
        @Query("fuzzy_key") key: String? = null,
        @Query("comment_tag") tag: String? = null,
        @Query("comment_state") state: Int? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): ResponseData<CommentListBean>

    companion object {
        val INSTANCE: CommentApi by lazy {
            ApiFactory.provideKtorfit(NETEASE_MC_DEV_LINK).createCommentApi()
        }
    }
}