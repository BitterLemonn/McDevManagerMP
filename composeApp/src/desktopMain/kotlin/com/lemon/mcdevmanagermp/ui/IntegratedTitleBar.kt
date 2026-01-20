package com.lemon.mcdevmanagermp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun IntegratedTitleBar(
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
            .height(40.dp)
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
        // macOS 风格按钮 - 左侧
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 关闭按钮 - 红色
            TrafficLightButton(
                normalColor = Color(0xFFFF5F57),
                hoverColor = Color(0xFFE0443E),
                icon = "✕",
                onClick = onClose
            )
            // 最小化按钮 - 黄色
            TrafficLightButton(
                normalColor = Color(0xFFFFBD2E),
                hoverColor = Color(0xFFDEA123),
                icon = "−",
                onClick = onMinimize
            )
            // 最大化按钮 - 绿色
            TrafficLightButton(
                normalColor = Color(0xFF28C840),
                hoverColor = Color(0xFF1AAB29),
                icon = if (isMaximized) "❐" else "＋",
                onClick = onMaximize
            )
        }
    }
}

@Composable
private fun TrafficLightButton(
    normalColor: Color,
    hoverColor: Color,
    icon: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(CircleShape)
            .background(if (isHovered) hoverColor else normalColor)
            .hoverable(interactionSource)
            .pointerInput(Unit) {
                detectTapGestures { onClick() }
            },
        contentAlignment = Alignment.Center
    ) {
        if (isHovered) {
            Text(
                text = icon,
                color = Color(0xFF4A0000).copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}