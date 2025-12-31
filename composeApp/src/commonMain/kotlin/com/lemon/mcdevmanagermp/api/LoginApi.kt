package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.NETEASE_LOGIN_LINK
import com.lemon.mcdevmanagermp.data.netease.login.BaseLoginBean
import com.lemon.mcdevmanagermp.data.netease.login.CapIdBean
import com.lemon.mcdevmanagermp.data.netease.login.EncParams
import com.lemon.mcdevmanagermp.data.netease.login.PowerBean
import com.lemon.mcdevmanagermp.data.netease.login.TicketBean
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface LoginApi {

    @Headers("Content-Type: application/json")
    @POST("/dl/zj/mail/ini")
    suspend fun init(@Body encParams: EncParams): CapIdBean

    @Headers("Content-Type: application/json")
    @POST("/dl/zj/mail/powGetP")
    suspend fun getPower(@Body encParams: EncParams): PowerBean

    @Headers("Content-Type: application/json")
    @POST("/dl/zj/mail/gt")
    suspend fun getTicket(@Body encParams: EncParams): TicketBean

    @Headers("Content-Type: application/json")
    @POST("/dl/zj/mail/l")
    suspend fun safeLogin(@Body encParams: EncParams): BaseLoginBean

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideKtorfit(NETEASE_LOGIN_LINK).createLoginApi()
        }
    }
}

