package com.lemon.mcdevmanagermp.data

import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean

object AppContext {
    var curUserInfo: UserInfoBean? = null

    val cookiesStore = HashMap<String, String>()
    var nowNickname = "UNKNOWN"

    val accountList = mutableListOf<String>()
}