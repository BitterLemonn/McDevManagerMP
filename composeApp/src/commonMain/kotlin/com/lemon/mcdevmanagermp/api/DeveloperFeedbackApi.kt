package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_MC_DEV_LINK
import com.lemon.mcdevmanagermp.data.netease.developerFeedback.DeveloperFeedbackBean
import com.lemon.mcdevmanagermp.data.netease.developerFeedback.DeveloperFeedbackResponseBean
import com.lemon.mcdevmanagermp.utils.ResponseData
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface DeveloperFeedbackApi {
    @POST("/developer/feedback/add_feedback")
    suspend fun seedFeedback(@Body feedbackBean: DeveloperFeedbackBean): ResponseData<DeveloperFeedbackResponseBean>

    companion object {
        val INSTANCE: DeveloperFeedbackApi by lazy {
            ApiFactory.provideKtorfit(NETEASE_MC_DEV_LINK).createDeveloperFeedbackApi()
        }
    }
}