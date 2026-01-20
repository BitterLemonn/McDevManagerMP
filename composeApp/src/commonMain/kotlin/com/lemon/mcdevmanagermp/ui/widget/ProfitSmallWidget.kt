package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun ProfitSmallWidget(
    icon: DrawableResource,
    mainText: String,
    mainNum: Int,
    subText: String,
    subNum: Int,
    modifier: Modifier = Modifier
) {
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
                small = (16 - 2 * offset).sp,
                smallest = (14 - 2 * offset).sp
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
                Image(
                    painter = painterResource(
                        when (if (mainNum > subNum) 1 else if (mainNum < subNum) -1 else 0) {
                            1 -> Res.drawable.up_image
                            -1 -> Res.drawable.down_image
                            else -> Res.drawable.normal_image
                        }
                    ),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.lighting(
                        multiply = AppTheme.colors.imgTintColor,
                        add = Color.Transparent
                    ),
                    contentDescription = "indicator",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically),

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
                            fontSize = fontSizes.medium,
                            color = AppTheme.colors.textColor,
                            letterSpacing = 2.sp,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = subNum.toString(),
                            fontSize = fontSizes.medium,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                else
                    Column(Modifier.align(Alignment.Center)) {
                        Text(
                            text = subText,
                            fontSize = fontSizes.medium,
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

@Composable
fun ProfitSingleWidget(
    icon: DrawableResource,
    mainText: String,
    mainNum: Int,
    subText: String,
    subNum: Int,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .widthIn(min = 280.dp)
            .height(140.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = !isLoading,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .then(modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 4.dp
        )
    ) {
        if (!isLoading) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            ) {
                Text(
                    text = mainText,
                    fontSize = 18.sp,
                    color = AppTheme.colors.textColor,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Image(
                        painter = painterResource(icon),
                        contentDescription = "icon",
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.lighting(
                            multiply = AppTheme.colors.imgTintColor,
                            add = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = mainNum.toString(),
                        fontSize = 28.sp,
                        color = AppTheme.colors.textColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(
                            when (if (mainNum > subNum) 1 else if (mainNum < subNum) -1 else 0) {
                                1 -> Res.drawable.up_image
                                -1 -> Res.drawable.down_image
                                else -> Res.drawable.normal_image
                            }
                        ),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.lighting(
                            multiply = AppTheme.colors.imgTintColor,
                            add = Color.Transparent
                        ),
                        contentDescription = "indicator",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = subText,
                        fontSize = 16.sp,
                        color = AppTheme.colors.textColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = subNum.toString(),
                        fontSize = 16.sp,
                        color = AppTheme.colors.textColor,
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.hintColor.copy(alpha = 0.45f))
                    .shimmerLoadingAnimation(false, !isSystemInDarkTheme(), durationMillis = 2000)
            )
        }
    }
}

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

@Preview(showBackground = true)
@Composable
private fun ProfitSingleWidgetPreview() {
    MCDevManagerTheme {
        ProfitSingleWidget(
            icon = Res.drawable.ic_diamond,
            mainText = "本月钻石收益",
            mainNum = 2147483647,
            subText = "上月钻石收益",
            subNum = 15000000
        )
    }
}