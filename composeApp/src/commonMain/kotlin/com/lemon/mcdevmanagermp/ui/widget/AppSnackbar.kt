package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.widget.SNACK_ERROR
import com.lemon.mcdevmanagermp.widget.SNACK_INFO
import com.lemon.mcdevmanagermp.widget.SNACK_SUCCESS
import com.lemon.mcdevmanagermp.widget.SNACK_WARN
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppSnackbar(
    data: SnackbarData
) {
    Snackbar(
        snackbarData = data,
        containerColor = when (data.visuals.actionLabel) {
            SNACK_INFO -> AppTheme.colors.info
            SNACK_WARN -> AppTheme.colors.warn
            SNACK_ERROR -> AppTheme.colors.error
            SNACK_SUCCESS -> AppTheme.colors.success
            else -> AppTheme.colors.info
        }
    )
}


fun popupSnackBar(
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    label: String,
    message: String,
    onDismissCallback: () -> Unit = {}
) {
    scope.launch {
        snackbarHostState.showSnackbar(
            actionLabel = label,
            message = message,
            duration = SnackbarDuration.Short
        )
        onDismissCallback.invoke()
    }
}