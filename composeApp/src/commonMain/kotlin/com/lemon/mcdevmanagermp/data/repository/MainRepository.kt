package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.InfoApi
import com.lemon.mcdevmanagermp.api.LoginApi
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
}