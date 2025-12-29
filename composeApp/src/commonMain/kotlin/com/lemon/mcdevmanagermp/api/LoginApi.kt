package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.JSONConverter
import com.lemon.mcdevmanagermp.data.common.NETEASE_LOGIN_LINK
import com.lemon.mcdevmanagermp.data.netease.login.BaseLoginBean
import com.lemon.mcdevmanagermp.data.netease.login.CapIdBean
import com.lemon.mcdevmanagermp.data.netease.login.EncParams
import com.lemon.mcdevmanagermp.data.netease.login.PowerBean
import com.lemon.mcdevmanagermp.data.netease.login.TicketBean
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies

interface LoginApi {

    @POST("/dl/zj/mail/ini")
    suspend fun init(@Body encParams: EncParams): CapIdBean

    @POST("/dl/zj/mail/powGetP")
    suspend fun getPower(@Body encParams: EncParams): PowerBean

    @POST("/dl/zj/mail/gt")
    suspend fun getTicket(@Body encParams: EncParams): TicketBean

    @POST("/dl/zj/mail/l")
    suspend fun safeLogin(@Body encParams: EncParams): BaseLoginBean

    companion object {
        /**
         * 获取接口实例用于调用对接方法
         * @return Ktorfit
         */
        fun create(): Ktorfit {
            val ktorClient = HttpClient {
                install(ContentNegotiation) { JSONConverter }
                install(HttpTimeout) {
                    connectTimeoutMillis = 15_000 // 15秒
                    requestTimeoutMillis = 15_000 // 请求总超时（通常涵盖读取时间）
                    socketTimeoutMillis = 15_000  // Socket 数据包读取间隔超时
                }
                install(HttpCookies) {
                    storage = AcceptAllCookiesStorage() // 自动内存存储 Cookie
                }
            }
            return Ktorfit.Builder()
                .baseUrl(NETEASE_LOGIN_LINK)
                .httpClient(ktorClient)
                .build()
        }
    }
}

