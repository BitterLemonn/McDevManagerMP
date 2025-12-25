package com.lemon.mcdevmanagermp.utils

import com.lemon.mcdevmanagermp.data.AppContext
import com.lemon.mcdevmanagermp.data.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

suspend fun logout(accountName: String) {
    AppContext.accountList.remove(accountName)
    AppContext.cookiesStore.remove(accountName)
    withContext<Any>(Dispatchers.IO) {
        // TODO: Create Info.sq and uncomment the following line
        // AppConstant.database.infoQueries.deleteOverviewByNickname(accountName)
        AppConstant.database.userInfoQueries.deleteUserByName(accountName)
    }
}