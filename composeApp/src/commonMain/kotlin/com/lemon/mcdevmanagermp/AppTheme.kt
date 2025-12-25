package com.lemon.mcdevmanagermp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.lemon.mcdevmanagermp.AppColorToken.BackgroundDark
import com.lemon.mcdevmanagermp.AppColorToken.BackgroundLight
import com.lemon.mcdevmanagermp.AppColorToken.CardDark
import com.lemon.mcdevmanagermp.AppColorToken.CardLight
import com.lemon.mcdevmanagermp.AppColorToken.DividerDark
import com.lemon.mcdevmanagermp.AppColorToken.DividerLight
import com.lemon.mcdevmanagermp.AppColorToken.ErrorLight
import com.lemon.mcdevmanagermp.AppColorToken.ErrorNight
import com.lemon.mcdevmanagermp.AppColorToken.Hint
import com.lemon.mcdevmanagermp.AppColorToken.InfoLight
import com.lemon.mcdevmanagermp.AppColorToken.InfoNight
import com.lemon.mcdevmanagermp.AppColorToken.SuccessLight
import com.lemon.mcdevmanagermp.AppColorToken.SuccessNight
import com.lemon.mcdevmanagermp.AppColorToken.TextDay
import com.lemon.mcdevmanagermp.AppColorToken.TextNight
import com.lemon.mcdevmanagermp.AppColorToken.WarnLight
import com.lemon.mcdevmanagermp.AppColorToken.WarnNight

object AppColorToken {
    val TextDay = Color(0xFF121212)
    val TextNight = Color(0xFFCCCCCC)

    val Hint = Color(0xFF9E9E9E)

    val DividerLight = Color(0xFFE0E0E0)
    val DividerDark = Color(0xFF707070)

    val CardLight = Color(0xFFEEEDF3)
    val CardDark = Color(0xFF313131)

    val BackgroundLight = Color(0xFFFAF9FF)
    val BackgroundDark = Color(0xFF121212)

    val InfoLight = Color(0xFF2196F3)
    val InfoNight = Color(0xFF40739B)

    val WarnLight = Color(0xFFFFC107)
    val WarnNight = Color(0xFFD8A000)

    val SuccessLight = Color(0xFF4CAF50)
    val SuccessNight = Color(0xFF2E7D32)

    val ErrorLight = Color(0xFFFF5252)
    val ErrorNight = Color(0xFFC62828)
}

class AppColors(
    textColor: Color,
    hintColor: Color,
    dividerColor: Color,
    card: Color,
    background: Color,
    info: Color,
    warn: Color,
    success: Color,
    error: Color,
//    lineChartColors: List<Color> = LineChartColorsLight
) {
    var textColor: Color by mutableStateOf(textColor)
        internal set
    var hintColor: Color by mutableStateOf(hintColor)
        internal set
    var dividerColor: Color by mutableStateOf(dividerColor)
        internal set
    var card: Color by mutableStateOf(card)
        internal set
    var background: Color by mutableStateOf(background)
        internal set
    var info: Color by mutableStateOf(info)
        internal set
    var warn: Color by mutableStateOf(warn)
        internal set
    var success: Color by mutableStateOf(success)
        internal set
    var error: Color by mutableStateOf(error)
        internal set
//    var chartColors: List<Color> by mutableStateOf(lineChartColors)
//        internal set
}

var LocalAppColors = compositionLocalOf {
    LightColorPalette
    DarkColorPalette
}

private val DarkColorPalette = AppColors(
    textColor = TextNight,
    hintColor = Hint,
    dividerColor = DividerDark,
    card = CardDark,
    background = BackgroundDark,
    info = InfoNight,
    warn = WarnNight,
    success = SuccessNight,
    error = ErrorNight,
//    lineChartColors = LineChartColorsDark
)

private val LightColorPalette = AppColors(
    textColor = TextDay,
    hintColor = Hint,
    dividerColor = DividerLight,
    card = CardLight,
    background = BackgroundLight,
    info = InfoLight,
    warn = WarnLight,
    success = SuccessLight,
    error = ErrorLight,
//    lineChartColors = LineChartColorsLight
)


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
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val theme = if (isSystemInDarkTheme()) AppTheme.Theme.Dark
    else AppTheme.Theme.Light

    val targetColors = when (theme) {
        AppTheme.Theme.Light -> LightColorPalette
        AppTheme.Theme.Dark -> DarkColorPalette
    }

    val textColor by animateColorAsState(targetColors.textColor, TweenSpec(600))
    val hintColor by animateColorAsState(targetColors.hintColor, TweenSpec(600))
    val dividerColor by animateColorAsState(targetColors.dividerColor, TweenSpec(600))
    val card by animateColorAsState(targetColors.card, TweenSpec(600))
    val background by animateColorAsState(targetColors.background, TweenSpec(600))
    val info by animateColorAsState(targetColors.info, TweenSpec(600))
    val warn by animateColorAsState(targetColors.warn, TweenSpec(600))
    val success by animateColorAsState(targetColors.success, TweenSpec(600))
    val error by animateColorAsState(targetValue = targetColors.error, TweenSpec(600))
//    val lineChartColors = targetColors.chartColors

    val appColors = AppColors(
        textColor = textColor,
        hintColor = hintColor,
        dividerColor = dividerColor,
        card = card,
        background = background,
        info = info,
        warn = warn,
        success = success,
        error = error,
//        lineChartColors = lineChartColors
    )

    CompositionLocalProvider(LocalAppColors provides appColors, content = content)
}