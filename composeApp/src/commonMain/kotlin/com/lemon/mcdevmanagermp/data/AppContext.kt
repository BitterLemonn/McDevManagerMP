package com.lemon.mcdevmanagermp.data

import com.lemon.mcdevmanagermp.data.netease.user.UserInfoBean

object AppContext {
    var curUserInfo: UserInfoBean? = null

    val cookiesStore = HashMap<String, String>()
    var userName = "UNKNOWN"
    var avatarUrl: String? = null

    val accountList = mutableListOf<String>()
}