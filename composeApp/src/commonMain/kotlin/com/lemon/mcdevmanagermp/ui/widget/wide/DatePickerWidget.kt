package com.lemon.mcdevmanagermp.ui.widget.wide

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.utils.formatDateFull
import com.lemon.mcdevmanagermp.utils.formatDateShort
import com.lemon.mcdevmanagermp.utils.getTodayStartMillis
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_calendar
import mcdevmanagermp.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeFilterChip(
    startTime: Long?,
    endTime: Long?,
    onTimeRangeChanged: (startTime: Long?, endTime: Long?) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val hasTimeFilter = startTime != null || endTime != null

    // 格式化显示
    val displayText = when {
        startTime != null && endTime != null -> {
            "${formatDateShort(startTime)} - ${formatDateShort(endTime)}"
        }

        startTime != null -> "从 ${formatDateShort(startTime)}"
        endTime != null -> "至 ${formatDateShort(endTime)}"
        else -> "时间范围"
    }

    Box {
        FilterChip(
            selected = hasTimeFilter,
            onClick = { isExpanded = true },
            label = {
                Text(
                    text = displayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_calendar),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = AppTheme.colors.background,
                selectedContainerColor = AppTheme.colors.primaryColor.copy(alpha = 0.12f),
                labelColor = AppTheme.colors.textColor,
                selectedLabelColor = AppTheme.colors.primaryColor,
                iconColor = AppTheme.colors.hintColor,
                selectedLeadingIconColor = AppTheme.colors.primaryColor,
                selectedTrailingIconColor = AppTheme.colors.primaryColor
            ),
            border = FilterChipDefaults.filterChipBorder(
                borderColor = AppTheme.colors.hintColor.copy(alpha = 0.3f),
                selectedBorderColor = AppTheme.colors.primaryColor.copy(alpha = 0.5f),
                enabled = true,
                selected = hasTimeFilter
            )
        )

        // 时间选择下拉菜单
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(AppTheme.colors.card)
                .widthIn(min = 280.dp),
            border = BorderStroke(1.dp, AppTheme.colors.hintColor.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "选择时间范围",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.textColor
                )

                // 开始时间
                DatePickerField(
                    label = "开始时间",
                    selectedTime = startTime,
                    onClear = { onTimeRangeChanged(null, endTime) },
                    onClick = { showStartDatePicker = true }
                )

                // 结束时间
                DatePickerField(
                    label = "结束时间",
                    selectedTime = endTime,
                    onClear = { onTimeRangeChanged(startTime, null) },
                    onClick = { showEndDatePicker = true }
                )

                // 快捷选项
                Text(
                    text = "快捷选择",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.hintColor
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickDateButton(
                        text = "今天",
                        onClick = {
                            val today = getTodayStartMillis()
                            val todayEnd = today + 24 * 60 * 60 * 1000 - 1
                            onTimeRangeChanged(today, todayEnd)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickDateButton(
                        text = "近7天",
                        onClick = {
                            val now = Clock.System.now().toEpochMilliseconds()
                            val weekAgo = now - 7 * 24 * 60 * 60 * 1000
                            onTimeRangeChanged(weekAgo, now)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    QuickDateButton(
                        text = "近30天",
                        onClick = {
                            val now = Clock.System.now().toEpochMilliseconds()
                            val monthAgo = now - 30L * 24 * 60 * 60 * 1000
                            onTimeRangeChanged(monthAgo, now)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = AppTheme.colors.hintColor.copy(alpha = 0.2f))

                // 底部按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                ) {
                    if (hasTimeFilter) {
                        OutlinedButton(
                            onClick = {
                                onTimeRangeChanged(null, null)
                            },
                            border = BorderStroke(1.dp, AppTheme.colors.error),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = AppTheme.colors.card,
                                contentColor = AppTheme.colors.textColor
                            )
                        ) { Text("清除") }
                    }

                    FilledTonalButton(
                        onClick = { isExpanded = false },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AppTheme.colors.primaryColor,
                            contentColor = Color.White
                        )
                    ) { Text("确定") }
                }
            }
        }
    }

    // 开始日期选择器
    if (showStartDatePicker) {
        DatePickerModal(
            initialDate = startTime,
            onDateSelected = { selectedMillis ->
                onTimeRangeChanged(selectedMillis, endTime)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false }
        )
    }

    // 结束日期选择器
    if (showEndDatePicker) {
        DatePickerModal(
            initialDate = endTime,
            onDateSelected = { selectedMillis ->
                onTimeRangeChanged(startTime, selectedMillis)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false }
        )
    }
}


@Composable
private fun DatePickerField(
    label: String,
    selectedTime: Long?,
    onClear: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = AppTheme.colors.background,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            AppTheme.colors.hintColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = AppTheme.colors.hintColor
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTheme.colors.hintColor
                )
                Text(
                    text = selectedTime?.let { formatDateFull(it) } ?: "请选择",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selectedTime != null) AppTheme.colors.textColor
                    else AppTheme.colors.hintColor
                )
            }

            if (selectedTime != null) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
                    contentDescription = "清除",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClear() },
                    tint = AppTheme.colors.hintColor
                )
            }
        }
    }
}

@Composable
private fun QuickDateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AppTheme.colors.primaryColor.copy(0.2f),
            contentColor = AppTheme.colors.textColor
        ),
        border = BorderStroke(1.dp, AppTheme.colors.primaryColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialDate: Long?,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate,
        initialDisplayMode = DisplayMode.Picker
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = AppTheme.colors.card,
            selectedDayContentColor = AppTheme.colors.textColor,
            todayDateBorderColor = AppTheme.colors.primaryColor,
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = AppTheme.colors.card,
                titleContentColor = AppTheme.colors.textColor,
                headlineContentColor = AppTheme.colors.textColor,
                selectedDayContainerColor = AppTheme.colors.primaryColor,
                todayDateBorderColor = AppTheme.colors.primaryColor,
                dayContentColor = AppTheme.colors.textColor,
                disabledDayContentColor = AppTheme.colors.hintColor.copy(alpha = 0.3f),
                yearContentColor = AppTheme.colors.textColor,
                weekdayContentColor = AppTheme.colors.textColor,
                navigationContentColor = AppTheme.colors.textColor
            )
        )
    }
}

@Preview
@Composable
private fun DateRangeFilterChipPreview() {
    MCDevManagerTheme {
        DatePickerModal(
            initialDate = null,
            onDateSelected = {},
            onDismiss = {}
        )
    }
}