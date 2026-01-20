package com.lemon.mcdevmanagermp

import java.io.File

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}-desktop"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun getLogDirectory(): String {
    val userHome = System.getProperty("user.home")
    val appDir = File(userHome, ".MCDevManagerMP/logs")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    return appDir.absolutePath
}
