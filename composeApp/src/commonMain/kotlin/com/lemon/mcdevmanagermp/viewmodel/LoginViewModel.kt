package com.lemon.mcdevmanagermp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.common.CookiesStore
import com.lemon.mcdevmanagermp.data.common.NETEASE_USER_COOKIE
import com.lemon.mcdevmanagermp.data.database.entities.UserEntity
import com.lemon.mcdevmanagermp.data.netease.login.PVResultStrBean
import com.lemon.mcdevmanagermp.extension.IUiAction
import com.lemon.mcdevmanagermp.extension.IUiEffect
import com.lemon.mcdevmanagermp.extension.IUiState
import com.lemon.mcdevmanagermp.extension.createEffectFlow
import com.lemon.mcdevmanagermp.extension.sendEffect
import com.lemon.mcdevmanagermp.extension.setState
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.utils.NetworkState
import com.lemon.mcdevmanagermp.utils.dumpAndGetCookiesValue
import com.lemon.mcdevmanagermp.utils.isValidCookiesStr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {
    private val repository = LoginRepository.getInstance()

    private val _viewState = MutableStateFlow(LoginViewState())
    val viewState = _viewState.asStateFlow()
    private val _viewEffect = createEffectFlow<LoginViewEffect>()
    val viewEffect = _viewEffect.asSharedFlow()

    private lateinit var pvResultBean: PVResultStrBean
    private lateinit var tk: String

    private var retryCount = 0

    fun dispatch(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.UpdateUsername -> _viewState.setState { copy(username = action.username) }
            is LoginViewAction.UpdatePassword -> _viewState.setState { copy(password = action.password) }
            is LoginViewAction.UpdateCookies -> _viewState.setState { copy(cookies = action.cookies) }
            is LoginViewAction.Login -> login()
            is LoginViewAction.SetUser -> setUser(action.nickname)
        }
    }

    private fun login() {

        viewModelScope.launch {
            if (_viewState.value.cookies.isNotEmpty()) {
                val text = _viewState.value.cookies
                val cookie =
                    if (text.isValidCookiesStr()) text.dumpAndGetCookiesValue(NETEASE_USER_COOKIE) else text
                if (cookie == null) {
                    sendEffect(_viewEffect, LoginViewEffect.LoginFailed("无效的Cookie"))
                    return@launch
                }
                CookiesStore.addCookie(NETEASE_USER_COOKIE, cookie)
                _viewEffect.setEvent(LoginViewEffect.LoginSuccess("登录成功"))
            } else {
                flow<Unit> {
                    initLogic()
                }.catch {
                    _viewEffect.setEvent(LoginViewEffect.LoginFailed(it.message ?: "登录失败"))
                }.onStart {
                    _viewState.setState { copy(isStartLogin = true) }
                }.onCompletion {
                    _viewState.setState { copy(isStartLogin = false) }
                }.flowOn(Dispatchers.IO).collect()
            }
        }
    }

    private suspend fun initLogic() {
        Logger.d("开始初始化")
        val init = repository.init("https://mcdev.webapp.163.com/#/login")
        when (init) {
            is NetworkState.Success -> {
                getPowerLogic()
            }

            is NetworkState.Error -> {
                throw Exception("获取登录ticket失败")
            }
        }
    }

    private suspend fun getPowerLogic() {
        Logger.d("开始获取权限")
        val power = repository.getPower(
            _viewState.value.username, topUrl = "https://mcdev.webapp.163.com/#/login"
        )
        when (power) {
            is NetworkState.Success -> {
                power.data?.let {
                    val pvInfo = JSONConverter.decodeFromString<PVInfo>(it)
                    pvResultBean = vdfAsync(pvInfo)
                    Logger.d("pvResultBean: $pvResultBean")
                    getTicket()
                }
            }

            is NetworkState.Error -> {
                throw Exception("获取权限失败")
            }
        }
    }

    private fun getTicket() {
        viewModelScope.launch {
            flow {
                getTicketLogic()
                emit("")
            }.catch {
                _viewEffect.setEvent(LoginViewEffect.LoginFailed(it.message ?: "登录失败"))
            }.flowOn(Dispatchers.IO).collect()
        }
    }

    private suspend fun getTicketLogic() {
        Logger.d("开始获取ticket")
        when (val ticket = repository.getTicket(
            _viewState.value.username, "https://mcdev.webapp.163.com/#/login"
        )) {
            is NetworkState.Success -> {
                this.tk = ticket.data ?: ""
                safeLoginLogic()
            }

            is NetworkState.Error -> {
                throw Exception("获取ticket失败")
            }
        }
    }

    private suspend fun safeLoginLogic() {
        Logger.d("开始安全登录")
        when (val login = repository.loginWithTicket(
            _viewState.value.username, _viewState.value.password, tk, pvResultBean
        )) {
            is NetworkState.Success -> {
                sendEffect(_viewEffect, LoginViewEffect.ShowToast("登录成功", false))
            }

            is NetworkState.Error -> {
                val errorState = Array(4) { "80${it + 1}" }
                if (errorState.contains(login.msg) && retryCount < 3) {
                    getPowerLogic()
                    retryCount++
                } else {
                    retryCount = 0
                    when (login.msg) {
                        "413" -> throw Exception("邮箱或密码错误")
                        else -> throw Exception("登录失败, 请重试")
                    }
                }
            }
        }
    }

    private fun setUser(nickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var isExist = false
            withContext(Dispatchers.IO) {
                val user = AppConstant.database.userDao().getUserByNickname(nickname)
                if (user != null) {
                    sendEffect(_viewEffect, LoginViewEffect.LoginFailed("助记名称已存在"))
                    isExist = true
                }
            }
            if (!isExist) {
                flow<Unit> {
                    // room持久化
                    val cookies = CookiesStore.getCookie(NETEASE_USER_COOKIE)
                        ?: throw Exception("获取用户信息失败, 请重新登录")
                    val username = _viewState.value.username
                    val password = _viewState.value.password
                    val userInfo = UserEntity(
                        username = username,
                        password = password,
                        nickname = nickname,
                        cookies = cookies
                    )
                    // 持久化
                    AppConstant.database.userDao().updateUser(userInfo)
                    AppContext.cookiesStore[nickname] = cookies
                    AppContext.nowNickname = nickname
                    AppContext.accountList.add(nickname)
                }.onCompletion {
                    sendEffect(_viewEffect, LoginViewEffect.RouteToPath(Screen.MainPage, true))
                }.catch {
                    sendEffect(_viewEffect, LoginViewEffect.LoginFailed(it.message ?: "未知错误"))
                }.collect()
            }
        }
    }
}

data class LoginViewState(
    val isStartLogin: Boolean = false,
    val username: String = "",
    val password: String = "",
    val cookies: String = ""
) : IUiState

sealed class LoginViewEffect : IUiEffect {
    data class LoginSuccess(val username: String) : LoginViewEffect()
    data class LoginFailed(val message: String) : LoginViewEffect()
    data class RouteToPath(val path: Screen, val needPop: Boolean = false) : LoginViewEffect()
    data class ShowToast(val message: String, val isError: Boolean = true) : LoginViewEffect()
}

sealed class LoginViewAction : IUiAction {
    data class UpdateUsername(val username: String) : LoginViewAction()
    data class UpdatePassword(val password: String) : LoginViewAction()
    data class UpdateCookies(val cookies: String) : LoginViewAction()
    data object Login : LoginViewAction()
    data class SetUser(val nickname: String) : LoginViewAction()
}