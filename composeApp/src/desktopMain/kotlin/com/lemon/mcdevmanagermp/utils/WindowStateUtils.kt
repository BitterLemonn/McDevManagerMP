package com.lemon.mcdevmanagermp.utils

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.GraphicsEnvironment
import java.awt.Rectangle
import java.util.prefs.Preferences
import kotlin.math.min

object WindowStateUtils {
    private const val KEY_WIDTH = "window_width"
    private const val KEY_HEIGHT = "window_height"
    private const val KEY_X = "window_x"
    private const val KEY_Y = "window_y"
    private const val KEY_PLACEMENT = "window_placement"

    private val prefs = Preferences.userRoot().node("com.lemon.mcdevmanagermp.window")

    fun saveState(state: WindowState) {
        prefs.put(KEY_PLACEMENT, state.placement.name)
        // 仅在非最大化时保存尺寸和位置
        if (state.placement == WindowPlacement.Floating) {
            prefs.putFloat(KEY_WIDTH, state.size.width.value)
            prefs.putFloat(KEY_HEIGHT, state.size.height.value)
            if (state.position is WindowPosition.Absolute) {
                val pos = state.position as WindowPosition.Absolute
                prefs.putFloat(KEY_X, pos.x.value)
                prefs.putFloat(KEY_Y, pos.y.value)
            }
        }
    }

    fun loadState(): WindowState {
        val savedWidth = prefs.getFloat(KEY_WIDTH, 1024f)
        val savedHeight = prefs.getFloat(KEY_HEIGHT, 768f)
        val savedX = prefs.getFloat(KEY_X, Float.NaN)
        val savedY = prefs.getFloat(KEY_Y, Float.NaN)
        val placementName = prefs.get(KEY_PLACEMENT, WindowPlacement.Floating.name)

        // 1. 恢复 Placement
        val placement = try {
            WindowPlacement.valueOf(placementName)
        } catch (e: Exception) {
            WindowPlacement.Floating
        }

        // 2. 智能计算位置和尺寸
        // 如果没有保存过坐标，直接返回默认
        if (savedX.isNaN() || savedY.isNaN()) {
            return WindowState(
                placement = placement,
                size = DpSize(savedWidth.dp, savedHeight.dp),
                position = WindowPosition.PlatformDefault
            )
        }

        // --- 核心逻辑：多显示器校验 ---
        val validatedState = validateCoordinates(
            x = savedX,
            y = savedY,
            width = savedWidth,
            height = savedHeight
        )

        return WindowState(
            placement = placement,
            size = validatedState.size,
            position = validatedState.position
        )
    }

    /**
     * 校验坐标是否有效
     * 逻辑：检查窗口的中心点是否落在任何一个已连接的显示器区域内
     */
    private fun validateCoordinates(x: Float, y: Float, width: Float, height: Float): ValidatedResult {
        // 获取所有屏幕的物理边界 (AWT API)
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val screens = ge.screenDevices.map { it.defaultConfiguration.bounds }

        // 构造保存的窗口矩形
        // 注意：这里简单将 Dp 视为 Pixel 处理用于碰撞检测。
        // 虽然有 DPI 差异，但用于判断“是否在屏幕内”通常足够精确，
        // 除非窗口极其微小且正好在边缘。
        val windowRect = Rectangle(x.toInt(), y.toInt(), width.toInt(), height.toInt())

        // 计算窗口中心点
        val centerX = x + width / 2
        val centerY = y + height / 2

        // 判断 1: 窗口是否完全消失（中心点不在任何屏幕内）
        val isVisible = screens.any { screen ->
            screen.contains(centerX.toInt(), centerY.toInt())
        }

        if (!isVisible) {
            // 如果不可见，强制重置到主屏幕默认位置，尺寸保留
            // 也可以选择在这里将尺寸重置为 safe 尺寸
            return ValidatedResult(
                position = WindowPosition.PlatformDefault,
                size = DpSize(width.dp, height.dp)
            )
        }

        // 判断 2: 窗口尺寸是否比当前所在的屏幕还大 (例如从 4K 移到了 1080p)
        // 找到中心点所在的那个屏幕
        val targetScreen = screens.find { it.contains(centerX.toInt(), centerY.toInt()) }
            ?: screens.first() // fallback

        // 限制宽高不超过屏幕大小 (留出一点 margin，比如 taskbar)
        val safeWidth = min(width, targetScreen.width.toFloat() * 0.9f)
        val safeHeight = min(height, targetScreen.height.toFloat() * 0.9f)

        return ValidatedResult(
            position = WindowPosition.Absolute(x.dp, y.dp),
            size = DpSize(safeWidth.dp, safeHeight.dp)
        )
    }

    private data class ValidatedResult(
        val position: WindowPosition,
        val size: DpSize
    )
}