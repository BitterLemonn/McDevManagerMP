package com.lemon.mcdevmanagermp

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@OptIn(ExperimentalForeignApi::class)
actual fun getLogDirectory(): String {
    val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
    val documentsDirectory = paths.first() as String
    val logsDirectory = "$documentsDirectory/logs"
    val fileManager = NSFileManager.defaultManager
    if (!fileManager.fileExistsAtPath(logsDirectory)) {
        fileManager.createDirectoryAtPath(logsDirectory, true, null, null)
    }
    return logsDirectory
}
