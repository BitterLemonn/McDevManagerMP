package com.lemon.mcdevmanagermp.api

import com.lemon.mcdevmanagermp.data.common.GITHUB_RESTFUL_LINK
import com.lemon.mcdevmanagermp.data.github.update.LatestReleaseBean
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

interface GithubUpdateApi {

    @GET("/repos/{author}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("author") author: String = "BitterLemonn",
        @Path("repo") repo: String = "MCDevManager"
    ): LatestReleaseBean

    companion object {
        val INSTANCE by lazy {
            ApiFactory.provideKtorfit(GITHUB_RESTFUL_LINK).createGithubUpdateApi()
        }
    }
}