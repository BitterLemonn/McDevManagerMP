package com.lemon.mcdevmanagermp.utils


fun getFileSizeFormat(size: Long): String {
    val kb = size / 1024
    return if (kb < 1024) {
        "$kb KB"
    } else if (kb < 1024 * 1024) {
        val mb = kb / 1024
        "$mb MB"
    } else {
        val gb = kb / 1024
        "$gb GB"
    }
}