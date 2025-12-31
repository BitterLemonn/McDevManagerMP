package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.loading
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun AppLoadingWidget(showBackground: Boolean = true) {

    var imageLoaded by remember { mutableStateOf(false) }
    val context = LocalPlatformContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (showBackground) Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
        Box {
            AsyncImage(
                model = Res.drawable.loading,
                contentDescription = "loading",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Center),
                colorFilter = ColorFilter.lighting(
                    multiply = AppTheme.colors.imgTintColor,
                    add = Color.Transparent
                ),
                onSuccess = {
                    imageLoaded = true
                }
            )
            if (imageLoaded)
                Text(
                    text = "Loading...",
                    color = Color.White,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp),
                    fontSize = 16.sp,
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppLoadingWidgetPreview() {
    AppLoadingWidget()
}