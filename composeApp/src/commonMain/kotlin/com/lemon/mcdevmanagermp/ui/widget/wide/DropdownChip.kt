package com.lemon.mcdevmanagermp.ui.widget.wide

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_correct
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectDropdownChip(
    label: String,
    icon: DrawableResource?,
    options: List<String>,
    selectedOption: String?,
    onSelectionChanged: (String?) -> Unit,
    optionDisplayText: (String) -> String = { it }
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasSelection = selectedOption != null

    val chipLabel = selectedOption?.let { optionDisplayText(it) } ?: label

    Box {
        FilterChip(
            selected = hasSelection,
            onClick = {
                if (options.isNotEmpty()) {
                    isExpanded = true
                }
            },
            label = {
                Text(
                    text = chipLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = if (icon != null) {
                {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else null,
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
                selected = hasSelection
            )
        )

        // 单选下拉菜单
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(AppTheme.colors.card)
                .widthIn(min = 180.dp, max = 300.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, AppTheme.colors.hintColor.copy(alpha = 0.2f))
        ) {
            // 全部选项（取消选择）
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = selectedOption == null,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AppTheme.colors.primaryColor,
                                unselectedColor = AppTheme.colors.hintColor
                            )
                        )
                        Text(
                            text = "全部",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedOption == null) AppTheme.colors.primaryColor
                            else AppTheme.colors.textColor
                        )
                    }
                },
                onClick = {
                    onSelectionChanged(null)
                    isExpanded = false
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            )

            if (options.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = AppTheme.colors.hintColor.copy(alpha = 0.2f)
                )
            }

            // 选项列表
            Column(
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    val isSelected = option == selectedOption

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = AppTheme.colors.primaryColor,
                                        unselectedColor = AppTheme.colors.hintColor
                                    )
                                )
                                Text(
                                    text = optionDisplayText(option),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) AppTheme.colors.primaryColor
                                    else AppTheme.colors.textColor
                                )
                            }
                        },
                        onClick = {
                            onSelectionChanged(option)
                            isExpanded = false
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectDropdownChip(
    label: String,
    icon: DrawableResource?,
    options: List<String>,
    selectedOptions: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    optionDisplayText: (String) -> String = { it }
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasSelection = selectedOptions.isNotEmpty()
    val selectedCount = selectedOptions.size

    val chipLabel = when {
        selectedOptions.isEmpty() -> label
        selectedCount == 1 -> optionDisplayText(selectedOptions.first())
        else -> "$label ($selectedCount)"
    }

    Box {
        FilterChip(
            selected = hasSelection,
            onClick = {
                if (options.isNotEmpty()) {
                    isExpanded = true
                }
            },
            label = {
                Text(
                    text = chipLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = when {
                icon != null -> {
                    {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                hasSelection -> {
                    {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(
                                    AppTheme.colors.primaryColor,
                                    RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedCount.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                else -> null
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
                selected = hasSelection
            )
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .background(AppTheme.colors.card)
                .widthIn(min = 200.dp, max = 320.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(
                1.dp,
                AppTheme.colors.hintColor.copy(alpha = 0.2f)
            )
        ) {
            // 头部
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "已选 $selectedCount / ${options.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppTheme.colors.hintColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Row() {
                    TextButton(
                        onClick = { onSelectionChanged(options.toSet()) },
                        enabled = selectedCount < options.size,
                    ) {
                        Text(
                            text = "全选",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selectedCount < options.size) AppTheme.colors.primaryColor
                            else AppTheme.colors.textColor.copy(alpha = 0.38f)
                        )
                    }

                    TextButton(
                        onClick = { onSelectionChanged(emptySet()) },
                        enabled = hasSelection,
                    ) {
                        Text(
                            text = "清空",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (hasSelection) AppTheme.colors.error
                            else AppTheme.colors.textColor.copy(alpha = 0.38f)
                        )
                    }
                }
            }

            HorizontalDivider(color = AppTheme.colors.hintColor.copy(alpha = 0.2f))

            Column(
                modifier = Modifier
                    .heightIn(max = 280.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    val isSelected = option in selectedOptions

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AppTheme.colors.primaryColor,
                                        uncheckedColor = AppTheme.colors.hintColor,
                                        checkmarkColor = Color.White
                                    )
                                )
                                Text(
                                    text = optionDisplayText(option),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) AppTheme.colors.primaryColor
                                    else AppTheme.colors.textColor
                                )
                            }
                        },
                        onClick = {
                            val newSelection = if (isSelected) {
                                selectedOptions - option
                            } else {
                                selectedOptions + option
                            }
                            onSelectionChanged(newSelection)
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    )
                }
            }

            HorizontalDivider(color = AppTheme.colors.hintColor.copy(alpha = 0.2f))

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                FilledTonalButton(
                    onClick = { isExpanded = false },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = AppTheme.colors.primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_correct),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("完成")
                }
            }
        }
    }
}
