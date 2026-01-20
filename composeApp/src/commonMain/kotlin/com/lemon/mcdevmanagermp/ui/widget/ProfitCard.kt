package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.extension.formatDecimal
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_money
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfitCard(
    title: String,
    realMoney: String,
    taxMoney: String,
    elevation: Dp = 0.dp,
    isLoading: Boolean = true
) {
    if (!isLoading)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.card
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_money),
                    contentDescription = "money",
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    color = AppTheme.colors.textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Column {
                    Row {
                        Text(
                            text = "不含扣税",
                            fontSize = 12.sp,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "￥",
                            fontSize = 20.sp,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = (realMoney.toDouble() + taxMoney.toDouble()).formatDecimal(2),
                            fontFamily = Font(Res.font.minecraft_ae).toFontFamily(),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            color = AppTheme.colors.textColor
                        )
                    }
                    Row {
                        Text(
                            text = "　含扣税",
                            fontSize = 12.sp,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Text(
                            text = "￥",
                            fontSize = 20.sp,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = realMoney,
                            fontFamily = Font(Res.font.minecraft_ae).toFontFamily(),
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            color = AppTheme.colors.textColor
                        )
                    }
                }
            }
        }
    else
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.hintColor.copy(alpha = 0.45f))
                .shimmerLoadingAnimation(false, !isSystemInDarkTheme(), durationMillis = 2000)
        )
}

@Composable
@Preview(showBackground = true)
private fun PreviewProfitCard() {
    ProfitCard(
        title = "本月收益速算",
        realMoney = "100.0",
        taxMoney = "20.0"
    )
}