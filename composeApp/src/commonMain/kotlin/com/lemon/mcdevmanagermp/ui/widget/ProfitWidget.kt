package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_diamond
import mcdevmanagermp.composeapp.generated.resources.ic_download
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ProfitWidget(
    curMonthProfit: Int,
    curMonthDl: Int,
    lastMonthProfit: Int,
    lastMonthDl: Int,
    yesterdayProfit: Int,
    halfAvgProfit: Int,
    yesterdayDl: Int,
    halfAvgDl: Int,
    isLoading: Boolean = true
) {
    if (!isLoading)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                        ProfitSmallWidget(
                            icon = Res.drawable.ic_diamond,
                            mainText = "本月钻石收益",
                            mainNum = curMonthProfit,
                            subText = "上月钻石收益",
                            subNum = lastMonthProfit,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ProfitSmallWidget(
                            icon = Res.drawable.ic_diamond,
                            mainText = "昨日钻石收益",
                            mainNum = yesterdayProfit,
                            subText = "14日均钻石收益",
                            subNum = halfAvgProfit,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(1f)) {
                    Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                        ProfitSmallWidget(
                            icon = Res.drawable.ic_download,
                            mainText = "本月下载量",
                            mainNum = curMonthDl,
                            subText = "上月下载量",
                            subNum = lastMonthDl,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        ProfitSmallWidget(
                            icon = Res.drawable.ic_download,
                            mainText = "昨日下载量",
                            mainNum = yesterdayDl,
                            subText = "14日均下载量",
                            subNum = halfAvgDl,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    else
        Box(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.hintColor.copy(alpha = 0.45f))
                .shimmerLoadingAnimation(
                    isLoadingCompleted = false,
                    isLightModeActive = !isSystemInDarkTheme(),
                    durationMillis = 2000
                )
        )

}

@Composable
fun ProfitSplitWidget(
    curMonthProfit: Int,
    curMonthDl: Int,
    lastMonthProfit: Int,
    lastMonthDl: Int,
    yesterdayProfit: Int,
    halfAvgProfit: Int,
    yesterdayDl: Int,
    halfAvgDl: Int,
    isLoading: Boolean = true,
    onClick: (() -> Unit) = {}
) {
    Column(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)){
        Row(modifier = Modifier.fillMaxWidth()){
            ProfitSingleWidget(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.ic_diamond,
                mainText = "本月收益",
                mainNum = curMonthProfit,
                subText = "上月收益",
                subNum = lastMonthProfit,
                isLoading = isLoading,
                onClick = onClick
            )
            ProfitSingleWidget(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.ic_download,
                mainText = "本月下载",
                mainNum = curMonthDl,
                subText = "上月下载",
                subNum = lastMonthDl,
                isLoading = isLoading,
                onClick = onClick
            )
        }
        Row(modifier = Modifier.fillMaxWidth()){
            ProfitSingleWidget(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.ic_diamond,
                mainText = "昨日收益",
                mainNum = yesterdayProfit,
                subText = "14日均收益",
                subNum = halfAvgProfit,
                isLoading = isLoading,
                onClick = onClick
            )
            ProfitSingleWidget(
                modifier = Modifier.weight(1f),
                icon = Res.drawable.ic_download,
                mainText = "昨日下载",
                mainNum = yesterdayDl,
                subText = "14日均下载",
                subNum = halfAvgDl,
                isLoading = isLoading,
                onClick = onClick
            )
        }
    }

//    LazyVerticalGrid(
//        columns = GridCells.Fixed(2),
//        horizontalArrangement = Arrangement.spacedBy(spacing),
//        verticalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterVertically),
//        modifier = Modifier
//            .fillMaxWidth()
//            .animateContentSize()
//    ) {
//        items(indexList, key = { it }) { index ->
//            Box(
//                modifier = Modifier
//                    .height(140.dp)
//            ) {
//                val item = items[index]
//                ProfitSingleWidget(
//                    modifier = Modifier.fillMaxWidth(),
//                    icon = item.icon,
//                    mainText = item.mainText,
//                    mainNum = item.mainNum,
//                    subText = item.subText,
//                    subNum = item.subNum,
//                    isLoading = isLoading,
//                    onClick = onClick
//                )
//            }
//        }
//    }
}

private data class ProfitItem(
    val icon: DrawableResource,
    val mainText: String,
    val mainNum: Int,
    val subText: String,
    val subNum: Int
)

@Preview(showBackground = true)
@Composable
private fun ProfitWidgetPreview() {
    ProfitWidget(
        curMonthProfit = 30000000,
        curMonthDl = 10,
        yesterdayProfit = 30000000,
        yesterdayDl = 1,
        lastMonthProfit = 1500,
        lastMonthDl = 5,
        halfAvgProfit = 200000000,
        halfAvgDl = 8,
        isLoading = false
    )
}

@Preview(showBackground = true, widthDp = 800)
@Composable
private fun ProfitSplitWidgetPreview() {
    ProfitSplitWidget(
        curMonthProfit = 0,
        curMonthDl = 0,
        yesterdayProfit = 0,
        yesterdayDl = 0,
        lastMonthProfit = 0,
        lastMonthDl = 0,
        halfAvgProfit = 0,
        halfAvgDl = 0,
        isLoading = false
    )
}