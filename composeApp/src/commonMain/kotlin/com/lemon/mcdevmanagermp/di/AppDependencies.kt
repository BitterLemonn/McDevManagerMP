package com.lemon.mcdevmanagermp.di

import com.lemon.mcdevmanagermp.data.AppConstant
import com.lemon.mcdevmanagermp.data.database.DriverFactory
import com.lemon.mcdevmanagermp.data.database.createDatabase
import com.lemon.mcdevmanagermp.data.repository.DatabaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

object AppDependencies {
    private var _driverFactory: DriverFactory? = null
    private var _databaseRepository: DatabaseRepository? = null

    fun initialize(driverFactory: DriverFactory) {
        _driverFactory = driverFactory
        _databaseRepository = DatabaseRepository(driverFactory)
        CoroutineScope(Dispatchers.IO).launch {
            if (_databaseRepository!!.initializeDatabase()) {
                AppConstant.database = createDatabase(_driverFactory!!)
            }
        }
    }


}
