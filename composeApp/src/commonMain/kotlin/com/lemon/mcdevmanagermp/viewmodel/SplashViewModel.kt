package com.lemon.mcdevmanagermp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.common.CookiesStore
import com.lemon.mcdevmanagermp.data.common.NETEASE_USER_COOKIE
import com.lemon.mcdevmanagermp.extension.IUiEffect
import com.lemon.mcdevmanagermp.extension.createEffectFlow
import com.lemon.mcdevmanagermp.extension.sendEffect
import com.lemon.mcdevmanagermp.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _viewEffect = createEffectFlow<SplashViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    fun dispatch(action: SplashViewAction) {
        when (action) {
            is SplashViewAction.GetDatabase -> getDatabase()
        }
    }

    private fun getDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            flow<Unit> {
                val userInfoList = AppConstant.database.userDao().getAllUsers()
                Logger.d("获取到用户列表: $userInfoList")
                userInfoList.let {
                    if (userInfoList.isNotEmpty()) {
                        for (user in userInfoList) {
                            if (userInfoList.indexOf(user) == 0) {
                                AppContext.userName = user.nickname
                                AppContext.avatarUrl = user.avatarUrl
                                CookiesStore.addCookie(NETEASE_USER_COOKIE, user.cookies)
                            }
                            AppContext.cookiesStore[user.nickname] = user.cookies
                        }
                        AppContext.accountList.addAll(userInfoList.map { it.nickname })
                        sendEffect(_viewEffect, SplashViewEffect.RouteToPath(Screen.MainPage))
                    } else {
                        sendEffect(_viewEffect, SplashViewEffect.RouteToPath(Screen.LoginPage))
                    }
                }
            }.collect()
        }
    }
}

sealed class SplashViewAction {
    data object GetDatabase : SplashViewAction()
}

sealed class SplashViewEffect : IUiEffect {
    data class RouteToPath(val path: Screen) : SplashViewEffect()
}