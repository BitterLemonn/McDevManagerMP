package com.lemon.mcdevmanagermp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState

@Composable
fun PlatformTitleBar(
    windowState: WindowState,
    isMaximized: Boolean,
    onMinimize: () -> Unit,
    onMaximize: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMac = System.getProperty("os.name").lowercase().contains("mac")

    if (isMac) {
        IntegratedTitleBar(
            windowState = windowState,
            isMaximized = isMaximized,
            onMinimize = onMinimize,
            onMaximize = onMaximize,
            onClose = onClose,
            modifier = modifier
        )
    } else {
        WindowsTitleBar(
            windowState = windowState,
            isMaximized = isMaximized,
            onMinimize = onMinimize,
            onMaximize = onMaximize,
            onClose = onClose,
            modifier = modifier
        )
    }
}