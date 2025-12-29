package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.GITHUB_RESTFUL_LINK
import com.lemon.mcdevmanagermp.data.common.JSONConverter
import com.lemon.mcdevmanagermp.data.github.update.LatestReleaseBean
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies

interface GithubUpdateApi {

    @GET("/repos/{author}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("author") author: String = "BitterLemonn",
        @Path("repo") repo: String = "MCDevManager"
    ): LatestReleaseBean

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
                .baseUrl(GITHUB_RESTFUL_LINK)
                .httpClient(ktorClient)
                .build()
        }
    }
}