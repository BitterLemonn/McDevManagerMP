package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.InfoApi
import com.lemon.mcdevmanagermp.api.LoginApi
import com.lemon.mcdevmanagermp.data.netease.rankList.CommonRankListResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.HotSearchResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.PeHotResponseBean
import com.lemon.mcdevmanagermp.data.netease.rankList.RankListResponseBean
import com.lemon.mcdevmanagermp.data.netease.user.LevelInfoBean
import com.lemon.mcdevmanagermp.data.netease.user.OverviewBean
import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler

class MainRepository {
    companion object {
        val INSTANCE by lazy { MainRepository() }
        private val infoApi = InfoApi.INSTANCE
    }

    suspend fun getUserInfo(): NetworkState<UserInfoBean> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getUserInfo()
        }
    }

    suspend fun getOverview(): NetworkState<OverviewBean> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getOverview()
        }
    }

    suspend fun getLevelInfo(): NetworkState<LevelInfoBean> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getLevelInfo()
        }
    }

    suspend fun getPeHotRankList(
        firstType: Int = 2
    ): NetworkState<RankListResponseBean<PeHotResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getPeHotRankList(firstType = firstType)
        }
    }

    suspend fun getHotSearchRankList(
        firstType: Int = 0
    ): NetworkState<RankListResponseBean<HotSearchResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getHotSearchRankList(firstType = firstType)
        }
    }

    suspend fun getPeDownloadRankList(
        firstType: Int = 2
    ): NetworkState<RankListResponseBean<CommonRankListResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getPeDownloadRankList(firstType = firstType)
        }
    }

    suspend fun getPeSellRankList(
        firstType: Int = 2
    ): NetworkState<RankListResponseBean<CommonRankListResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getPeSellRankList(firstType = firstType)
        }
    }

    suspend fun getPcDownloadRankList(
        firstType: Int = 2
    ): NetworkState<RankListResponseBean<CommonRankListResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getPcDownloadRankList(firstType = firstType)
        }
    }

    suspend fun getPcLikeRankList(
        firstType: Int = 2
    ): NetworkState<RankListResponseBean<CommonRankListResponseBean>> {
        return UnifiedExceptionHandler.handleRequest {
            infoApi.getPcLikeRankList(firstType = firstType)
        }
    }

}