package com.lemon.mcdevmanagermp.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Streaming
import de.jensklingenberg.ktorfit.http.Url
import io.ktor.client.statement.HttpStatement

interface DownloadApi {

    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): HttpStatement

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideDownloadKtorfit().createDownloadApi()
        }
    }
}