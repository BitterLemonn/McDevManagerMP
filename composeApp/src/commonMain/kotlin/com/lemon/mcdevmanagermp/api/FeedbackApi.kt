package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.netease.feedback.FeedbackResponseBean
import com.lemon.mcdevmanagermp.utils.NoNeedData
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import okhttp3.RequestBody

interface FeedbackApi {

    @GET("/items/feedback/pe/")
    suspend fun loadFeedback(
        @Query("start") from: Int,
        @Query("span") size: Int,
        @Query("sort") sort: String? = null,
        @Query("order") order: String? = null,
        @Query("type") status: String? = null,
        @Query("fuzzy_key") key: String? = null,
        @Query("reply_count") replyCount: Int? = null
    ): ResponseData<FeedbackResponseBean>

    @PUT("/items/feedback/pe/{id}/reply")
    suspend fun sendReply(
        @Path("id") feedbackId: String,
        @Body content: RequestBody
    ): ResponseData<NoNeedData>
}