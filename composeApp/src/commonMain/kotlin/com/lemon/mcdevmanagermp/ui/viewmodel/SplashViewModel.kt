package com.lemon.mcdevmanagermp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.LOGIN_PAGE
import com.lemon.mcdevmanagermp.data.MAIN_PAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _viewEvents = MutableSharedFlow<SplashViewEvent>()
    val viewEvents = _viewEvents.asSharedFlow()

    fun dispatch(action: SplashViewAction) {
        when (action) {
            is SplashViewAction.GetDatabase -> getDatabase()
        }
    }

    private fun getDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            flow<Unit> {
                val userInfoList = AppConstant.database.userInfoQueries.getAllUsers().executeAsList()
                userInfoList.let {
                    if (userInfoList.isNotEmpty()) {
                        for (user in userInfoList) {
                            if (userInfoList.indexOf(user) == 0)
                                AppContext.nowNickname = user.name
                            user.cookies?.let {
                                AppContext.cookiesStore[user.name] = it
                            }
                        }
                        AppContext.accountList.addAll(userInfoList.map { it.name })
                        _viewEvents.emit(SplashViewEvent.RouteToPath(MAIN_PAGE))
                    } else {
                        _viewEvents.emit(SplashViewEvent.RouteToPath(LOGIN_PAGE))
                    }
                }
            }.collect()
        }
    }
}

sealed class SplashViewAction {
    data object GetDatabase : SplashViewAction()
}

sealed class SplashViewEvent {
    data class RouteToPath(val path: String) : SplashViewEvent()
}