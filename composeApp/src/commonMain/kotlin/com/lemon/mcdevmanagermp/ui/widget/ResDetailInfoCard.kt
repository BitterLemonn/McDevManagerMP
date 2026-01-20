package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.data.netease.resource.ResDetailBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.Font

@Composable
fun ResDetailInfoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = AppTheme.colors.card,
    resBeans: List<ResDetailBean>,
    filterType: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val showIid = maxWidth > 320.dp

            Column(Modifier.fillMaxWidth()) {
                resBeans.forEach {
                    ResDetailInfoItem(
                        resName = it.resName,
                        iid = it.iid,
                        value = getValueByType(it, filterType),
                        showIid = showIid // 将判断结果传给子组件
                    )
                }
            }
        }
    }
}

// 辅助函数保持不变
private fun getValueByType(it: ResDetailBean, filterType: Int): Double {
    return when (filterType) {
        0 -> it.cntBuy.toDouble()
        1 -> it.downloadNum.toDouble()
        2 -> it.diamond.toDouble()
        3 -> it.points.toDouble()
        4 -> it.dau.toDouble()
        5 -> it.refundRate
        else -> 0.0
    }
}

@Composable
private fun ResDetailInfoItem(
    resName: String,
    iid: String,
    value: Double,
    showIid: Boolean // 接收布尔值，而不是宽度数值
) {
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = resName,
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Bottom), // 这里可以用 Baseline 对齐更好看
            color = AppTheme.colors.textColor,
            fontSize = 14.sp,
            maxLines = 1 // 建议加上，防止文字太长挤坏布局
        )

        if (showIid) {
            Text(
                text = iid,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Bottom),
                color = AppTheme.colors.hintColor,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString(),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Bottom),
            color = AppTheme.colors.textColor,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(Res.font.minecraft_ae))
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewResDetailInfoCard() {
    ResDetailInfoCard(
        resBeans = listOf(
            ResDetailBean(
                dau = 100,
                cntBuy = 10,
                dateId = "2021-10-01",
                diamond = 4000,
                downloadNum = 1000,
                iid = "4671862965461320892",
                platform = "pe",
                points = 200,
                refundRate = 0.1,
                resName = "神话之森",
                uploadTime = "2021-10-01"
            ),
            ResDetailBean(
                dau = 100,
                cntBuy = 10,
                dateId = "2021-10-01",
                diamond = 2000,
                downloadNum = 1000,
                iid = "4671862965412312312",
                platform = "pe",
                points = 300,
                refundRate = 0.1,
                resName = "苦柠",
                uploadTime = "2021-10-01"
            )
        )
    )
}