package com.lemon.mcdevmanagermp.utils

import coil3.ImageLoader
import coil3.PlatformContext

actual fun getImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .build()
}