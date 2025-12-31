package com.lemon.mcdevmanagermp.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// 假设 publishTime 是秒 (Long 类型)
fun formatTime(publishTime: Long): String {
    // 1. 将秒转换为 Instant (时间戳)
    val instant = Instant.fromEpochSeconds(publishTime)

    // 2. 转换为当前系统的时区 (或者指定 TimeZone.of("Asia/Shanghai"))
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    // 3. 格式化
    // 方式 A: 直接使用 ISO 标准格式 (结果就是 yyyy-MM-dd)
    return localDate.toString()
}

fun getPreviousDay(dateStr: String): String {
    try {
        // 1. 解析: LocalDate.parse 默认支持 "yyyy-MM-dd" 格式
        // 如果 dateStr 是 "2024-05-20"，可以直接解析
        val currentDate = LocalDate.parse(dateStr)

        // 2. 计算: 减去 1 天
        // 这种方式是日历层面的减法，绝对准确
        val prevDate = currentDate.minus(1, DateTimeUnit.DAY)

        // 3. 格式化: toString() 默认输出 "yyyy-MM-dd"
        return prevDate.toString()
    } catch (e: Exception) {
        // 处理解析错误
        return ""
    }
}