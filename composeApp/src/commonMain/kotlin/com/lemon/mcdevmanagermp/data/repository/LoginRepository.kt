package com.lemon.mcdevmanagermp.data.repository

import com.lemon.mcdevmanagermp.api.LoginApi
import com.lemon.mcdevmanagermp.data.common.RSAKey
import com.lemon.mcdevmanagermp.data.common.SM4Key
import com.lemon.mcdevmanagermp.data.netease.login.EncParams
import com.lemon.mcdevmanagermp.data.netease.login.GetCapIdRequestBean
import com.lemon.mcdevmanagermp.data.netease.login.GetPowerRequestBean
import com.lemon.mcdevmanagermp.data.netease.login.LoginRequestBean
import com.lemon.mcdevmanagermp.data.netease.login.PVResultStrBean
import com.lemon.mcdevmanagermp.data.netease.login.TicketRequestBean
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.UnifiedExceptionHandler
import com.lemon.mcdevmanagermp.utils.dataJsonToString
import com.lemon.mcdevmanagermp.utils.rsaEncrypt
import com.lemon.mcdevmanagermp.utils.sm4Encrypt

class LoginRepository {

    private constructor()

    companion object {
        val INSTANCE by lazy { LoginRepository() }
        private val loginApi = LoginApi.INSTANCE
    }

    suspend fun init(topUrl: String): NetworkState<String> {
        return UnifiedExceptionHandler.handleNeteaseRequest {
            val initRequest = GetCapIdRequestBean(topURL = topUrl)
            val encode = sm4Encrypt(dataJsonToString(initRequest), SM4Key)
            val encParams = EncParams(encode)
            loginApi.init(encParams)
        }
    }

    suspend fun getPower(username: String, topUrl: String): NetworkState<String> {
        return UnifiedExceptionHandler.handleNeteaseRequest {
            val powerRequest = GetPowerRequestBean(un = username, topURL = topUrl)
            val encode = sm4Encrypt(dataJsonToString(powerRequest), SM4Key)
            val encParams = EncParams(encode)
            loginApi.getPower(encParams)
        }
    }

    suspend fun getTicket(username: String, topUrl: String): NetworkState<String> {
        return UnifiedExceptionHandler.handleNeteaseRequest {
            val tkRequest = TicketRequestBean(username, topURL = topUrl)
            val encParams = EncParams(sm4Encrypt(dataJsonToString(tkRequest), SM4Key))
            loginApi.getTicket(encParams)
        }
    }

    suspend fun loginWithTicket(
        username: String,
        password: String,
        ticket: String,
        pvResultBean: PVResultStrBean
    ): NetworkState<String> {
        return UnifiedExceptionHandler.handleNeteaseRequest {
            val encodePw = rsaEncrypt(password, RSAKey)
            val loginRequest = LoginRequestBean(
                un = username,
                pw = encodePw,
                tk = ticket,
                topURL = "https://mcdev.webapp.163.com/#/login",
                pvParam = pvResultBean
            )
            val encode = sm4Encrypt(dataJsonToString(loginRequest), SM4Key)
            val encParams = EncParams(encode)
            loginApi.safeLogin(encParams)
        }
    }

}