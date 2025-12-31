package com.lemon.mcdevmanagermp.utils

import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.common.CookiesStore
import com.lemon.mcdevmanagermp.data.common.NETEASE_USER_COOKIE
import com.lemon.mcdevmanagermp.data.github.update.LatestReleaseBean
import com.lemon.mcdevmanagermp.data.netease.login.BaseLoginBean
import com.lemon.mcdevmanagermp.data.netease.login.CapIdBean
import com.lemon.mcdevmanagermp.data.netease.login.PowerBean
import com.lemon.mcdevmanagermp.data.netease.login.TicketBean
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

object UnifiedExceptionHandler {
    private const val TAG = "UnifiedException"

    suspend fun <T> handleRequest(
        block: suspend () -> ResponseData<T>
    ): NetworkState<T> {
        return try {
            val response = block.invoke()
            parseData(response)
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * Github 专用处理
     */
    suspend fun <T> handleGithubRequest(
        block: suspend () -> T
    ): NetworkState<T> {
        return try {
            val result = block.invoke()
            if (result is LatestReleaseBean) {
                val uniData = ResponseData("200", result)
                parseData(uniData)
            } else {
                NetworkState.Error("函数调用错误，非Github更新接口请勿调用此方法")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * 网易 API 专用处理
     */
    suspend fun <T> handleNeteaseRequest(
        block: suspend () -> T
    ): NetworkState<String> {
        return try {
            when (val result = block.invoke()) {
                is TicketBean -> {
                    val uniData = ResponseData(result.ret.toString(), result.tk)
                    parseData(uniData, noNeedRefreshCookies = true)
                }

                is BaseLoginBean -> {
                    val uniData = ResponseData(result.ret.toString(), null)
                    parseData(uniData, noNeedRefreshCookies = true)
                }

                is PowerBean -> {
                    val uniData =
                        ResponseData(result.ret.toString(), dataJsonToString(result.pVInfo))
                    parseData(uniData, noNeedRefreshCookies = true)
                }

                is CapIdBean -> {
                    val uniData = ResponseData(result.ret.toString(), result.capId)
                    parseData(uniData, noNeedRefreshCookies = true)
                }

                else -> NetworkState.Error("函数调用错误，非网易登录接口请勿调用此方法")
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    /**
     * 统一异常处理逻辑
     */
    private suspend fun <T> handleException(e: Exception): NetworkState<T> {
        if (e is CancellationException) {
            // 协程取消异常，直接抛出以便上层处理
            throw e
        }

        return when (e) {
            is io.ktor.client.network.sockets.ConnectTimeoutException,
            is io.ktor.client.network.sockets.SocketTimeoutException -> {
                Logger.e("$TAG:链接超时\n$e")
                NetworkState.Error("网络好像被末影人搬走了", e)
            }

            // Ktor 的 HTTP 错误状态码处理 (4xx, 5xx)
            is ResponseException -> {
                when (val statusCode = e.response.status.value) {
                    401 -> {
                        Logger.e("$TAG:Token失效\n$e")
                        NetworkState.Error("登录过期啦!", LoginException("登录过期啦!"))
                    }
                    403 -> {
                        Logger.e("$TAG:请求受限\n$e")
                        NetworkState.Error("请求过于频繁,请稍后再次尝试", e)
                    }
                    else -> {
                        Logger.e("$TAG:HTTP错误 $statusCode\n$e")
                        NetworkState.Error("服务器开小差了 ($statusCode)", e)
                    }
                }
            }

            // 通用 IO 异常 (断网等)
            is IOException -> {
                Logger.e("$TAG:网络错误\n$e")
                NetworkState.Error("服务器掉进深暗之域了", e)
            }

            else -> {
                e.message?.let { Logger.e("$TAG:$it") } ?: Logger.e(e::class.toString())
                NetworkState.Error("未知错误，请联系管理员", e)
            }
        }
    }

    private suspend fun <T> parseData(
        result: ResponseData<T>,
        noNeedRefreshCookies: Boolean = false
    ): NetworkState<T> {
        val returnCookies = CookiesStore.getCookie(NETEASE_USER_COOKIE)
        // 更新用户cookie
        if (returnCookies != null && returnCookies != AppContext.cookiesStore[AppContext.nowNickname] && !noNeedRefreshCookies) {
            AppContext.cookiesStore[AppContext.nowNickname] = returnCookies
            AppConstant.database.userDao().getUserByNickname(AppContext.nowNickname)?.let {
                val user = it.copy(cookies = returnCookies)
                AppConstant.database.userDao().updateUser(user)
                Logger.d("用户${AppContext.nowNickname}的cookie已更新: $returnCookies")
            } ?: run {
                Logger.e("用户${AppContext.nowNickname}不存在")
            }
        }

        return when (result.status) {
            "200", "ok", "OK", "Ok" -> result.data?.let { NetworkState.Success(result.data) }
                ?: NetworkState.Success(msg = result.msg ?: result.status)

            "201" -> result.data?.let { NetworkState.Success(result.data) }
                ?: NetworkState.Success(msg = result.msg ?: result.status)

            "401", "no_login" -> NetworkState.Error(
                "登录过期了，请重新登录",
                CookiesExpiredException()
            )

            else -> NetworkState.Error(result.msg ?: result.status)
        }
    }

    class LoginException(message: String? = null) : Exception(message)
    class CookiesExpiredException : Exception("Cookies 已过期")
}