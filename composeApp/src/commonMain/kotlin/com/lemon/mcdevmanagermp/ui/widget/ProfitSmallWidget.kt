package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.down_image
import mcdevmanagermp.composeapp.generated.resources.ic_diamond
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import mcdevmanagermp.composeapp.generated.resources.normal_image
import mcdevmanagermp.composeapp.generated.resources.up_image
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfitSmallWidget(
    icon: DrawableResource,
    mainText: String,
    mainNum: Int,
    subText: String,
    subNum: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalPlatformContext.current

    BoxWithConstraints(
        modifier = Modifier.then(modifier)
    ) {
        val screenWidthDp = maxWidth
        val fontSizes = remember(screenWidthDp) {
            val standardWidth = 400.dp
            // 计算偏移量：每小 50dp，字体减小 2sp
            val offset = ((standardWidth - screenWidthDp) / 50.dp).toInt().coerceAtLeast(0)

            FontSizes(
                large = (24 - 2 * offset).sp,
                medium = (18 - 2 * offset).sp,
                small = (14 - 2 * offset).sp,
                smallest = (12 - 2 * offset).sp
            )
        }

        Box(modifier = Modifier.height(24.dp)) {
            Image(
                painter = painterResource(icon),
                contentDescription = "icon",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.lighting(
                    multiply = AppTheme.colors.imgTintColor,
                    add = Color.Transparent
                )
            )
        }
        Column(modifier = Modifier.padding(start = 20.dp)) {
            Text(
                text = mainText,
                fontSize = fontSizes.medium,
                color = AppTheme.colors.textColor,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Box(modifier = Modifier.heightIn(min = 24.dp)) {
                    Text(
                        text = mainNum.toString(),
                        fontFamily = Font(Res.font.minecraft_ae).toFontFamily(),
                        fontSize = if (mainNum.toString().length < 6) fontSizes.large else fontSizes.medium,
                        color = AppTheme.colors.textColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                AsyncImage(
                    model = ImageRequest.Builder(context).data(
                        when (if (mainNum > subNum) 1 else if (mainNum < subNum) -1 else 0) {
                            1 -> Res.drawable.up_image
                            -1 -> Res.drawable.down_image
                            else -> Res.drawable.normal_image
                        }
                    ).build(),
                    contentDescription = "indicator",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.lighting(
                        multiply = AppTheme.colors.imgTintColor,
                        add = Color.Transparent
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .heightIn(min = 18.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                if (subNum.toString().length + subText.length < 12)
                    Row(Modifier.align(Alignment.Center)) {
                        Text(
                            text = subText,
                            fontSize = fontSizes.small,
                            color = AppTheme.colors.textColor,
                            letterSpacing = 2.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = subNum.toString(),
                            fontSize = fontSizes.small,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                else
                    Column(Modifier.align(Alignment.Center)) {
                        Text(
                            text = subText,
                            fontSize = fontSizes.small,
                            color = AppTheme.colors.textColor,
                            letterSpacing = 1.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                        Text(
                            text = subNum.toString(),
                            fontSize = fontSizes.small,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
            }
        }
    }
}

data class FontSizes(
    val large: TextUnit,
    val medium: TextUnit,
    val small: TextUnit,
    val smallest: TextUnit
)

@Preview(showBackground = true)
@Composable
private fun ProfitSmallWidgetPreview() {
    MCDevManagerTheme {
        ProfitSmallWidget(
            icon = Res.drawable.ic_diamond,
            mainText = "本月钻石收益",
            mainNum = 3000,
            subText = "上月钻石收益",
            subNum = 15000000
        )
    }
}