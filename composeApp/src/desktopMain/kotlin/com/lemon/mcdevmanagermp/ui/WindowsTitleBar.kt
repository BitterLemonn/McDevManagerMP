package com.lemon.mcdevmanagermp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import java.awt.MouseInfo

@Composable
fun WindowsTitleBar(
    windowState: WindowState,
    isMaximized: Boolean,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startMouseX by remember { mutableStateOf(0) }
    var startMouseY by remember { mutableStateOf(0) }
    var startWinX by remember { mutableStateOf(0f) }
    var startWinY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(Color.Transparent)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        val mouse = MouseInfo.getPointerInfo().location
                        startMouseX = mouse.x
                        startMouseY = mouse.y
                        startWinX = windowState.position.x.value
                        startWinY = windowState.position.y.value
                    },
                    onDrag = { change, _ ->
                        if (!isMaximized) {
                            change.consume()
                            val mouse = MouseInfo.getPointerInfo().location
                            windowState.position = WindowPosition(
                                x = (startWinX + mouse.x - startMouseX).dp,
                                y = (startWinY + mouse.y - startMouseY).dp
                            )
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { onMaximize() })
            }
    ) {
        // Windows 风格按钮 - 右侧
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End
        ) {
            // 最小化
            WindowsButton(
                icon = "─",
                hoverBackground = Color.Gray.copy(alpha = 0.2f),
                onClick = onMinimize
            )
            // 最大化/还原
            WindowsButton(
                icon = if (isMaximized) "❐" else "□",
                hoverBackground = Color.Gray.copy(alpha = 0.2f),
                onClick = onMaximize
            )
            // 关闭
            WindowsButton(
                icon = "✕",
                hoverBackground = Color(0xFFE81123),
                hoverIconColor = Color.White,
                onClick = onClose
            )
        }
    }
}

@Composable
private fun WindowsButton(
    icon: String,
    hoverBackground: Color,
    hoverIconColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .width(46.dp)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .background(if (isHovered) hoverBackground else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            color = when {
                isHovered && hoverIconColor != Color.Unspecified -> hoverIconColor
                else -> Color.Gray
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}