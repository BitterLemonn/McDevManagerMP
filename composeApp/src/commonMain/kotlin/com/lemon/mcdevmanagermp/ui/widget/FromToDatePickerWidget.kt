package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun FromToDatePickerWidget(
    modifier: Modifier = Modifier,
    timeZone: TimeZone = TimeZone.of("Asia/Shanghai"),
    startTime: Instant = Clock.System.now().minus(7, DateTimeUnit.DAY, timeZone),
    endTime: Instant = Clock.System.now(),
    onChangeFromDate: (String) -> Unit = {},
    onChangeToDate: (String) -> Unit = {},
    onChanging: (Boolean) -> Unit = {}
) {
    // 状态初始化
    var fromDate by remember { mutableStateOf(startTime.toLocalDateTime(timeZone).date) }
    var toDate by remember { mutableStateOf(endTime.toLocalDateTime(timeZone).date) }

    // 当前激活的选择器：None, From, To
    var activeState by remember { mutableStateOf(ActiveState.NONE) }

    // 权重动画
    val fromWeight by animateFloatAsState(
        targetValue = if (activeState == ActiveState.TO) 0.001f else 1f, // 0.001f 防止布局完全消失导致重组问题
        animationSpec = tween(300)
    )
    val toWeight by animateFloatAsState(
        targetValue = if (activeState == ActiveState.FROM) 0.001f else 1f,
        animationSpec = tween(300)
    )

    // 回调包装
    val handleActiveChange: (ActiveState) -> Unit = { newState ->
        activeState = newState
        onChanging(newState != ActiveState.NONE)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 开始时间部分 ---
        if (activeState != ActiveState.TO) {
            ExpandableDateItem(
                modifier = Modifier.weight(fromWeight),
                date = fromDate,
                isExpanded = activeState == ActiveState.FROM,
                onExpand = { handleActiveChange(ActiveState.FROM) },
                onConfirm = { newDate ->
                    fromDate = newDate
                    // 返回符合 ISO8601 格式的 UTC 时间字符串
                    val instantStr = LocalDateTime(
                        newDate.year,
                        newDate.month.number,
                        newDate.day,
                        0,
                        0,
                        0,
                        0
                    )
                        .toInstant(timeZone).toString()
                    onChangeFromDate(instantStr)
                    handleActiveChange(ActiveState.NONE)
                }
            )
        }

        // --- 中间分割字 "至" ---
        // 只有当两个都没展开时才显示
        AnimatedVisibility(
            visible = activeState == ActiveState.NONE,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "至",
                fontSize = 14.sp,
                color = AppTheme.colors.textColor,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // --- 结束时间部分 ---
        if (activeState != ActiveState.FROM) {
            ExpandableDateItem(
                modifier = Modifier.weight(toWeight),
                date = toDate,
                isExpanded = activeState == ActiveState.TO,
                onExpand = { handleActiveChange(ActiveState.TO) },
                onConfirm = { newDate ->
                    toDate = newDate
                    val instantStr = LocalDateTime(
                        newDate.year,
                        newDate.month.number,
                        newDate.day,
                        0,
                        0,
                        0,
                        0
                    )
                        .toInstant(timeZone).toString()
                    onChangeToDate(instantStr)
                    handleActiveChange(ActiveState.NONE)
                }
            )
        }
    }
}

/**
 * 可展开的日期条目组件
 * 包含：收起状态的文本框 + 展开状态的选择器面板
 */
@Composable
private fun ExpandableDateItem(
    modifier: Modifier,
    date: LocalDate,
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    Box(
        modifier = modifier
            .heightIn(min = 45.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = if (isExpanded) Color.Transparent else AppTheme.colors.primaryColor,
                shape = RoundedCornerShape(8.dp)
            )
            .background(if (isExpanded) Color.Transparent else AppTheme.colors.background)
    ) {
        // 使用 AnimatedContent 处理内容切换
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        ) { expanded ->
            if (!expanded) {
                // --- 收起状态：显示日期文本 ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp) // 固定高度保持对齐
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = onExpand
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.toString(), // 默认 yyyy-MM-dd
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = AppTheme.colors.textColor
                    )
                }
            } else {
                // --- 展开状态：显示选择器 ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column {
                        // 原生 CMP 日期滚轮选择器
                        NativeDatePicker(
                            initialDate = date,
                            onDateSelected = onConfirm
                        )
                    }
                }
            }
        }
    }
}

/**
 * 封装的日期选择面板
 * 包含：年、月、日三个 WheelPicker 和确定按钮
 */
@Composable
private fun NativeDatePicker(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // 临时状态，用于在点击确定前存储选择
    var selectedYear by remember { mutableStateOf(initialDate.year) }
    var selectedMonth by remember { mutableStateOf(initialDate.month.number) }
    var selectedDay by remember { mutableStateOf(initialDate.day) }

    // 数据源
    val years = remember { (2000..2050).toList() } // 范围可调
    val months = remember { (1..12).toList() }

    // 动态计算当月天数
    val days = remember(selectedYear, selectedMonth) {
        val daysInMonth = try {
            // 获取下个月第一天减去一天，得到本月天数
            val date = LocalDate(selectedYear, selectedMonth, 1)
            date.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
        } catch (e: Exception) {
            30 // 兜底
        }
        (1..daysInMonth).toList()
    }

    // 纠正天数溢出（例如从1月31日切到2月，天数应变为28/29）
    LaunchedEffect(days) {
        if (selectedDay > days.last()) {
            selectedDay = days.last()
        }
    }

    // 滚轮状态
    val yearState =
        rememberWheelPickerState(initialIndex = years.indexOf(selectedYear).coerceAtLeast(0))
    val monthState =
        rememberWheelPickerState(initialIndex = months.indexOf(selectedMonth).coerceAtLeast(0))
    // 注意：Day 的 index 需要动态计算，因为列表长度会变
    val dayState = rememberWheelPickerState(initialIndex = (selectedDay - 1).coerceAtLeast(0))

    // 监听滚轮变化
    LaunchedEffect(yearState.currentIndex) { selectedYear = years[yearState.currentIndex] }
    LaunchedEffect(monthState.currentIndex) { selectedMonth = months[monthState.currentIndex] }
    LaunchedEffect(dayState.currentIndex) {
        // 防止 index 越界 (因为 days 列表可能会变短)
        val index = dayState.currentIndex.coerceIn(0, days.lastIndex)
        selectedDay = days[index]
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 年
            WheelPicker(
                modifier = Modifier.weight(1.2f),
                count = years.size,
                state = yearState,
                itemHeight = 35.dp
            ) { i -> PickerText(years[i].toString() + "年", i == yearState.currentIndex) }

            // 月
            WheelPicker(
                modifier = Modifier.weight(1f),
                count = months.size,
                state = monthState,
                itemHeight = 35.dp
            ) { i ->
                PickerText(
                    months[i].toString().padStart(2, '0') + "月",
                    i == monthState.currentIndex
                )
            }

            // 日
            WheelPicker(
                modifier = Modifier.weight(1f),
                count = days.size,
                state = dayState,
                itemHeight = 35.dp,
                // 当天数列表变化时，强制重置 Key 以刷新 UI (可选，视 WheelPicker 实现而定)
                // key = days.size
            ) { i ->
                PickerText(
                    days[i].toString().padStart(2, '0') + "日",
                    i == dayState.currentIndex
                )
            }
        }

        // 确定按钮
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.primaryColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = {
                        onDateSelected(LocalDate(selectedYear, selectedMonth, selectedDay))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "确定",
                color = TextWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun PickerText(text: String, isSelected: Boolean) {
    Text(
        text = text,
        color = if (isSelected) AppTheme.colors.textColor else AppTheme.colors.hintColor,
        fontSize = if (isSelected) 16.sp else 14.sp,
        textAlign = TextAlign.Center
    )
}

// 状态枚举
private enum class ActiveState {
    NONE, FROM, TO
}