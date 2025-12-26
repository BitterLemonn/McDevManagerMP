package com.lemon.mcdevmanagermp.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = AppColors(
    textColor = TextNight,
    hintColor = Hint,
    dividerColor = DividerDark,
    imgTintColor = IconDark,
    card = CardDark,
    background = BackgroundDark,
    primaryColor = Purple40,
    primarySubColor = PurpleGrey40,
    secondaryColor = Pink40,
    info = InfoNight,
    warn = WarnNight,
    success = SuccessNight,
    error = ErrorNight,
    lineChartColors = LineChartColorsDark
)

private val LightColorPalette = AppColors(
    textColor = TextDay,
    hintColor = Hint,
    dividerColor = DividerLight,
    imgTintColor = IconLight,
    card = CardLight,
    background = BackgroundLight,
    primaryColor = Purple200,
    primarySubColor = PurpleGrey80,
    secondaryColor = Pink200,
    info = InfoLight,
    warn = WarnLight,
    success = SuccessLight,
    error = ErrorLight,
    lineChartColors = LineChartColorsLight
)

// 1. 修复 LocalAppColors 的初始化，默认给一个值即可
val LocalAppColors = staticCompositionLocalOf {
    LightColorPalette
}

@Stable
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current

    enum class Theme {
        Light, Dark
    }
}

@Composable
fun MCDevManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // KMP 中通常移除 dynamicColor，因为它是 Android 12+ 专有的
    content: @Composable () -> Unit
) {
    val targetColors = if (darkTheme) DarkColorPalette else LightColorPalette

    // 2. 颜色动画过渡 (保持你的逻辑)
    val textColor by animateColorAsState(targetColors.textColor, TweenSpec(600))
    val hintColor by animateColorAsState(targetColors.hintColor, TweenSpec(600))
    val dividerColor by animateColorAsState(targetColors.dividerColor, TweenSpec(600))
    val imgTintColor by animateColorAsState(targetColors.imgTintColor, TweenSpec(600))
    val card by animateColorAsState(targetColors.card, TweenSpec(600))
    val background by animateColorAsState(targetColors.background, TweenSpec(600))
    val primaryColor by animateColorAsState(targetColors.primaryColor, TweenSpec(600))
    val primarySubColor by animateColorAsState(targetColors.primarySubColor, TweenSpec(600))
    val secondaryColor by animateColorAsState(targetColors.secondaryColor, TweenSpec(600))
    val info by animateColorAsState(targetColors.info, TweenSpec(600))
    val warn by animateColorAsState(targetColors.warn, TweenSpec(600))
    val success by animateColorAsState(targetColors.success, TweenSpec(600))
    val error by animateColorAsState(targetColors.error, TweenSpec(600))

    // 构造当前状态的 appColors
    val appColors = remember(darkTheme) { // 使用 remember 优化性能
        AppColors(
            textColor = textColor,
            hintColor = hintColor,
            dividerColor = dividerColor,
            imgTintColor = imgTintColor,
            card = card,
            background = background,
            primaryColor = primaryColor,
            primarySubColor = primarySubColor,
            secondaryColor = secondaryColor,
            info = info,
            warn = warn,
            success = success,
            error = error,
            lineChartColors = targetColors.chartColors
        )
    }

    CompositionLocalProvider(LocalAppColors provides appColors, content = content)
}

class AppColors(
    textColor: Color,
    hintColor: Color,
    dividerColor: Color,
    imgTintColor: Color,
    card: Color,
    background: Color,
    primaryColor: Color,
    primarySubColor: Color,
    secondaryColor: Color,
    info: Color,
    warn: Color,
    success: Color,
    error: Color,
    lineChartColors: List<Color>
) {
    var textColor: Color by mutableStateOf(textColor)
        internal set
    var hintColor: Color by mutableStateOf(hintColor)
        internal set
    var dividerColor: Color by mutableStateOf(dividerColor)
        internal set
    var imgTintColor: Color by mutableStateOf(imgTintColor)
        internal set
    var card: Color by mutableStateOf(card)
        internal set
    var background: Color by mutableStateOf(background)
        internal set
    var primaryColor: Color by mutableStateOf(primaryColor)
        internal set
    var primarySubColor: Color by mutableStateOf(primarySubColor)
        internal set
    var secondaryColor: Color by mutableStateOf(secondaryColor)
        internal set
    var info: Color by mutableStateOf(info)
        internal set
    var warn: Color by mutableStateOf(warn)
        internal set
    var success: Color by mutableStateOf(success)
        internal set
    var error: Color by mutableStateOf(error)
        internal set
    var chartColors: List<Color> by mutableStateOf(lineChartColors)
        internal set
}
