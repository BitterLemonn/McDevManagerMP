package com.lemon.mcdevmanagermp.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSize {
    Compact, // 手机竖屏 (宽度 < 600dp)
    Medium,  // 平板竖屏 / 折叠屏 (600dp <= 宽度 < 840dp)
    Expanded // 平板横屏 / 桌面端 (宽度 >= 840dp)
}

// 根据宽度计算类型
fun getWindowSizeClass(width: Dp): WindowSize {
    return when {
        width < 600.dp -> WindowSize.Compact
        width < 840.dp -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}