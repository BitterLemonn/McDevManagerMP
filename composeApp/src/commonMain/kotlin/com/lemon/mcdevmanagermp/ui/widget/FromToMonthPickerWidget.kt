package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import com.lemon.mcdevmanagermp.widget.SNACK_ERROR
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun FromToMonthPickerWidget(
    modifier: Modifier = Modifier,
    fromMonthStr: String,
    toMonthStr: String,
    onFromMonthChange: (String) -> Unit = {},
    onToMonthChange: (String) -> Unit = {},
    onConfirm: () -> Unit = {},
    showToast: (String, String) -> Unit = { _, _ -> }
) {
    // 1. 获取当前时间 (KMP 方式)
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val currentYear = now.year

    // 2. 状态管理
    var activePicker by remember { mutableStateOf<PickerType?>(null) } // 使用枚举管理当前激活的 Picker

    // 3. 数据源准备 (只创建一次)
    val yearList = remember(currentYear) {
        (2010..currentYear).map { it.toString() }
    }
    val monthList = remember {
        (1..12).map { it.toString().padStart(2, '0') }
    }

    // 辅助函数：解析 "yyyy-MM"
    fun parseDate(dateStr: String): Pair<String, String> {
        val parts = dateStr.split("-")
        return if (parts.size == 2) parts[0] to parts[1] else yearList.last() to monthList.first()
    }

    Card(
        modifier = modifier.padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column {
            // 顶部显示区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 开始月份显示框
                DateDisplayBox(
                    text = fromMonthStr,
                    modifier = Modifier.weight(1f),
                    onClick = { activePicker = PickerType.FROM }
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "至",
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = AppTheme.colors.textColor
                    )
                }

                // 结束月份显示框
                DateDisplayBox(
                    text = toMonthStr,
                    modifier = Modifier.weight(1f),
                    onClick = { activePicker = PickerType.TO }
                )
            }

            // 底部确认按钮（整个范围选择的确认）
            ConfirmButton(
                text = "确定",
                onClick = onConfirm,
                modifier = Modifier.padding(8.dp)
            )
        }
    }

    // 遮罩层
    ModalBackgroundWidget(visibility = activePicker != null)

    // 提取出的通用选择器抽屉
    // 开始时间选择器
    val (fromYear, fromMonth) = parseDate(fromMonthStr)
    MonthPickerDrawer(
        isVisible = activePicker == PickerType.FROM,
        slideFromLeft = true, // 从左侧滑入
        initialYear = fromYear,
        initialMonth = fromMonth,
        yearList = yearList,
        monthList = monthList,
        onConfirm = { y, m ->
            onFromMonthChange("$y-$m")
            activePicker = null
        }
    )

    // 结束时间选择器
    val (toYear, toMonth) = parseDate(toMonthStr)
    MonthPickerDrawer(
        isVisible = activePicker == PickerType.TO,
        slideFromLeft = false, // 从右侧滑入
        initialYear = toYear,
        initialMonth = toMonth,
        yearList = yearList,
        monthList = monthList,
        onConfirm = { y, m ->
            // 校验逻辑
            if (y < fromYear || (y == fromYear && m < fromMonth)) {
                showToast("结束月份不能小于开始月份", SNACK_ERROR)
                // 校验失败不关闭弹窗，或者看你需求
                return@MonthPickerDrawer
            }
            onToMonthChange("$y-$m")
            activePicker = null
        }
    )
}

/**
 * 提取出的独立月份选择器抽屉组件
 * 负责动画、滚轮选择和内部布局
 */
@Composable
private fun MonthPickerDrawer(
    isVisible: Boolean,
    slideFromLeft: Boolean,
    initialYear: String,
    initialMonth: String,
    yearList: List<String>,
    monthList: List<String>,
    onConfirm: (year: String, month: String) -> Unit
) {
    val yearState =
        rememberWheelPickerState(initialIndex = yearList.indexOf(initialYear).coerceAtLeast(0))
    val monthState =
        rememberWheelPickerState(initialIndex = monthList.indexOf(initialMonth).coerceAtLeast(0))

    // 当抽屉再次打开时，重置滚轮位置到当前传入的值
    LaunchedEffect(isVisible) {
        if (isVisible) {
            val yIndex = yearList.indexOf(initialYear).coerceAtLeast(0)
            val mIndex = monthList.indexOf(initialMonth).coerceAtLeast(0)
            if (yearState.currentIndex != yIndex) yearState.scrollToIndex(yIndex)
            if (monthState.currentIndex != mIndex) monthState.scrollToIndex(mIndex)
        }
    }

    val slideOffsetMultiplier = if (slideFromLeft) -1 else 1

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { it * slideOffsetMultiplier },
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { it * slideOffsetMultiplier },
            animationSpec = spring(stiffness = Spring.StiffnessLow)
        ) + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .border(1.dp, AppTheme.colors.primaryColor, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.background)
                .padding(16.dp), // 内部增加 padding 让视觉更舒适
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelColumn(modifier = Modifier.weight(1f), state = yearState, list = yearList)
                Text(
                    text = "年",
                    color = AppTheme.colors.hintColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                // 使用新的 WheelColumn
                WheelColumn(modifier = Modifier.weight(1f), state = monthState, list = monthList)
                Text(
                    text = "月",
                    color = AppTheme.colors.hintColor,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ConfirmButton(
                text = "确定",
                onClick = {
                    val year = yearList.getOrElse(yearState.currentIndex) { yearList.last() }
                    val month = monthList.getOrElse(monthState.currentIndex) { monthList.first() }
                    onConfirm(year, month)
                }
            )
        }
    }
}

/**
 * 单个滚轮列封装
 */
@Composable
private fun WheelColumn(
    modifier: Modifier,
    state: WheelPickerState,
    list: List<String>
) {
    WheelPicker(
        modifier = modifier,
        state = state,
        count = list.size,
        itemHeight = 40.dp,
        visibleCount = 5 // 可以调整可见数量，3 或 5 比较好
    ) { index ->
        // 判断是否选中，改变颜色
        val isSelected = state.currentIndex == index
        Text(
            text = list[index],
            color = if (isSelected) AppTheme.colors.textColor else AppTheme.colors.hintColor,
            fontSize = 16.sp
        )
    }
}

/**
 * 显示日期的边框盒子
 */
@Composable
private fun DateDisplayBox(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .border(1.dp, AppTheme.colors.primaryColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)) // 确保点击水波纹不溢出
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = AppTheme.colors.textColor,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

/**
 * 通用确认按钮
 */
@Composable
private fun ConfirmButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.primaryColor),
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = TextWhite,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
    }
}

// 辅助枚举，标记当前是哪个 Picker 打开
private enum class PickerType {
    FROM, TO
}

// 假设的占位符，如果你的项目中没有这个，请保留你原来的 ModalBackgroundWidget
@Composable
fun ModalBackgroundWidget(visibility: Boolean) {
    // 你的遮罩层逻辑
    AnimatedVisibility(visible = visibility, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) {} // 拦截点击
        )
    }
}