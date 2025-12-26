package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lemon.mcdevmanagermp.extension.IUiEffect
import com.lemon.mcdevmanagermp.extension.collectEffect
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun <Effect : IUiEffect> BasePage(
    viewEffect: SharedFlow<Effect>? = null,
    onEffect: ((Effect) -> Unit)? = null,
    content: @Composable (Modifier) -> Unit
) {
    if (viewEffect != null && onEffect != null) {
        viewEffect.collectEffect { event ->
            onEffect(event)
        }
    }

    content(Modifier.padding(WindowInsets.navigationBars.asPaddingValues()))
}