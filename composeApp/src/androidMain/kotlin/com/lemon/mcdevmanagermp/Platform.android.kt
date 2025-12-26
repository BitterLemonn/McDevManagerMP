package com.lemon.mcdevmanagermp

import android.content.Context
import android.os.Build
import java.lang.ref.WeakReference

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

object AndroidLogContext {
    private var contextRef: WeakReference<Context>? = null

    fun setContext(context: Context) {
        contextRef = WeakReference(context)
    }

    fun getContext(): Context? = contextRef?.get()
}

actual fun getLogDirectory(): String {
    val context = AndroidLogContext.getContext()
        ?: throw IllegalStateException("Android Context not initialized for Logging. Call AndroidLogContext.setContext(context) in MainActivity.")
    val dir = context.getExternalFilesDir("logs") ?: context.filesDir
    return dir.absolutePath
}
