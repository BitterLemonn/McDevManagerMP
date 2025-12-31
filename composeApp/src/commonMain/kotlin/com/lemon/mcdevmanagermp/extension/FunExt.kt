package com.lemon.mcdevmanagermp.extension

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

fun String.dumpAndGetCookiesValue(key: String): String? {
    val cookies = this.split(";")
    for (cookie in cookies) {
        val pair = cookie.split("=")
        if (pair.size == 2 && pair[0].trim() == key) {
            return pair[1].trim()
        }
    }
    return null
}

fun String.isValidCookiesStr(): Boolean {
    return this.isNotEmpty() && this.contains("=") && this.contains(";")
}

fun Double.formatDecimal(digits: Int = 1): String {
    if (digits < 0) return this.toString()
    if (digits == 0) return this.roundToLong().toString()

    // 1. 计算倍率，例如保留2位就是乘100
    // 使用 Long 避免大数精度丢失
    val factor = 10.0.pow(digits)

    // 2. 四舍五入
    val scaled = (this * factor).roundToLong()

    // 3. 分离整数和小数部分
    val intPart = scaled / factor.toLong()
    val fractionPart = abs(scaled % factor.toLong())

    // 4. 拼接字符串，小数部分需要补零 (例如 1.5 -> 1.50)
    // padStart 是 Kotlin 标准库函数，跨平台通用
    return "$intPart.${fractionPart.toString().padStart(digits, '0')}"
}

fun Float.formatDecimal(digits: Int = 1): String = this.toDouble().formatDecimal(digits)