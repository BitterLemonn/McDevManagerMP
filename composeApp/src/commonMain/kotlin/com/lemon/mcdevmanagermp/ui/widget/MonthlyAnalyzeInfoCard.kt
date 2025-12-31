package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.data.netease.resource.ResMonthDetailBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lt.compose_views.other.HorizontalSpace
import com.lt.compose_views.other.VerticalSpace
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_buy
import mcdevmanagermp.composeapp.generated.resources.ic_dau
import mcdevmanagermp.composeapp.generated.resources.ic_diamond_line
import mcdevmanagermp.composeapp.generated.resources.ic_point
import mcdevmanagermp.composeapp.generated.resources.ic_sale
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MonthlyAnalyzeInfoCard(
    infoData: ResMonthDetailBean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = infoData.resName,
            fontSize = 16.sp,
            color = AppTheme.colors.textColor,
            fontFamily = FontFamily(Font(Res.font.minecraft_ae)),
            letterSpacing = 2.sp
        )
        Text(
            text = infoData.iid,
            fontSize = 12.sp,
            color = AppTheme.colors.hintColor,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Row {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(Res.drawable.ic_sale),
                    contentDescription = "sale count",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                )
                HorizontalSpace(dp = 4.dp)
                Text(
                    text = "销售总量",
                    fontSize = 14.sp,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = infoData.downloadNum.toString(),
                    fontSize = 16.sp,
                    color = AppTheme.colors.textColor,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae))
                )
            }
            HorizontalSpace(dp = 8.dp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(Res.drawable.ic_buy),
                    contentDescription = "buy count",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                )
                HorizontalSpace(dp = 4.dp)
                Text(
                    text = "日均购买",
                    fontSize = 14.sp,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = infoData.avgDayBuy.toString(),
                    fontSize = 16.sp,
                    color = AppTheme.colors.textColor,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae))
                )
            }
        }
        VerticalSpace(dp = 8.dp)
        Row {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(Res.drawable.ic_dau),
                    contentDescription = "dau count",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                )
                HorizontalSpace(dp = 4.dp)
                Text(
                    text = "日均活跃",
                    fontSize = 14.sp,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = infoData.avgDau.toString(),
                    fontSize = 16.sp,
                    color = AppTheme.colors.textColor,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae))
                )
            }
            HorizontalSpace(dp = 8.dp)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Image(
                    painter = painterResource(Res.drawable.ic_dau),
                    contentDescription = "mau count",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                )
                HorizontalSpace(dp = 4.dp)
                Text(
                    text = "月均活跃",
                    fontSize = 14.sp,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = infoData.mau.toString(),
                    fontSize = 16.sp,
                    color = AppTheme.colors.textColor,
                    fontFamily = FontFamily(Font(Res.font.minecraft_ae))
                )
            }
        }
        VerticalSpace(dp = 8.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.ic_diamond_line),
                contentDescription = "diamond count",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
            )
            HorizontalSpace(dp = 4.dp)
            Text(
                text = "钻石收益",
                fontSize = 14.sp,
                color = AppTheme.colors.hintColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = infoData.totalDiamond.toString(),
                fontSize = 16.sp,
                color = AppTheme.colors.textColor,
                fontFamily = FontFamily(Font(Res.font.minecraft_ae))
            )
        }
        VerticalSpace(dp = 8.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.ic_point),
                contentDescription = "points count",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
            )
            HorizontalSpace(dp = 4.dp)
            Text(
                text = "绿宝石收益",
                fontSize = 14.sp,
                color = AppTheme.colors.hintColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = infoData.totalPoints.toString(),
                fontSize = 16.sp,
                color = AppTheme.colors.textColor,
                fontFamily = FontFamily(Font(Res.font.minecraft_ae))
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MonthlyAnalyzeInfoCardPreview() {
    MCDevManagerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MonthlyAnalyzeInfoCard(
                ResMonthDetailBean(
                    avgDau = 1000,
                    avgDayBuy = 100,
                    downloadNum = 2000000,
                    iid = "12312312313131231321",
                    mau = 2000,
                    monthId = "2021-01",
                    resName = "test",
                    uploadTime = "2021-01-01",
                    platform = "PE",
                    totalDiamond = 1000,
                    totalPoints = 1000
                )
            )
        }
    }
}