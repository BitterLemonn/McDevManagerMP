package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.ui.theme.TextBlack
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomHintDialog(
    hint: String,
    isShow: Boolean,
    canTouchOutside: Boolean = false,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    if (isShow)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = TextBlack.copy(alpha = 0.4f))
                .clickable(
                    enabled = canTouchOutside,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onCancel.invoke() }
        )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isShow,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideIn { fullSize -> IntOffset(0, fullSize.height) },
            exit = slideOut { fullSize -> IntOffset(0, fullSize.height) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .background(color = AppTheme.colors.card)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .heightIn(min = 120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hint,
                        color = AppTheme.colors.textColor,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 25.dp),
                        textAlign = TextAlign.Center
                    )
                }
                HorizontalDivider(
                    color = AppTheme.colors.dividerColor,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onCancel
                            )
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "取消",
                            fontSize = 14.sp,
                            color = AppTheme.colors.textColor
                        )
                    }
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        thickness = 1.dp,
                        color = AppTheme.colors.dividerColor
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = onConfirm
                            )
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "确定",
                            fontSize = 14.sp,
                            color = AppTheme.colors.primaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun BottomDialogPreview() {
    MCDevManagerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.background)) {
            var isShow by remember { mutableStateOf(true) }
            BottomHintDialog(
                hint = "应用向你请求权限",
                isShow = isShow,
                onConfirm = { isShow = false },
                onCancel = { isShow = false }
            )
        }
    }
}