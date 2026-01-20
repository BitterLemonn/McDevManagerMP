package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.extension.formatDecimal
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.utils.LevelResourceUtils
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.img_avatar
import mcdevmanagermp.composeapp.generated.resources.minecraft_ae
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource

@Composable
fun MainUserCard(
    username: String,
    avatarUrl: String,
    mainLevel: Int = 0,
    subLevel: Int = 0,
    levelText: String = "",
    maxLevelExp: Double = 1.0,
    currentExp: Double = 1.0,
    canLevelUp: Boolean = false,
    contributeScore: String = "0",
    contributeRank: Int = 0,
    contributeClass: Int = 0,
    netGameScore: String = "0",
    netGameRank: Int = 0,
    netGameClass: Int = 0,
    dataDate: String = "",
    enableAvatarClick: Boolean = true,
    onClickAvatar: () -> Unit = {}
) {
    var isShowLevelInfo by remember { mutableStateOf(false) }

    val levelIconResource: DrawableResource? = remember(mainLevel, subLevel) {
        LevelResourceUtils.getLevelIcon(mainLevel, subLevel)
    }

    val contributeClassStr = remember(contributeClass) {
        LevelResourceUtils.getRankTitle(contributeClass)
    }

    val netGameClassStr = remember(netGameClass) {
        LevelResourceUtils.getRankTitle(netGameClass)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(
                        enabled = enableAvatarClick,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onClickAvatar() }
            ) {
                CoilImage(
                    imageModel = { avatarUrl },
                    imageOptions = ImageOptions(
                        contentDescription = "avatar image",
                        contentScale = ContentScale.Crop,
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically),
                    loading = {
                        painterResource(Res.drawable.img_avatar)
                    }
                )
                Text(
                    text = username,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 8.dp),
                    fontSize = 16.sp,
                    color = AppTheme.colors.textColor
                )
            }
            if (mainLevel != 0 && levelIconResource != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { isShowLevelInfo = !isShowLevelInfo }
                ) {
                    Text(
                        text = levelText,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        fontSize = 16.sp,
                        color = AppTheme.colors.textColor
                    )
                    Image(
                        painter = painterResource(levelIconResource),
                        contentDescription = "level image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(28.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = isShowLevelInfo,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                        ) {
                            Text(
                                text = "${currentExp.formatDecimal()}/ ${maxLevelExp.formatDecimal()}",
                                fontSize = if ("$currentExp".length + "/$maxLevelExp".length > 15) 12.sp else 14.sp,
                                color = AppTheme.colors.textColor,
                                modifier = Modifier.align(Alignment.BottomStart),
                                fontFamily = Font(Res.font.minecraft_ae).toFontFamily()
                            )
                            Text(
                                text = "升阶任务 ${if (canLevelUp) "已完成" else "未完成"}",
                                fontSize = 14.sp,
                                color = AppTheme.colors.textColor,
                                modifier = Modifier.align(Alignment.BottomEnd)
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (currentExp / maxLevelExp).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape),
                            color = if (canLevelUp) AppTheme.colors.success else AppTheme.colors.primaryColor
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "月度贡献",
                        fontSize = 14.sp,
                        color = AppTheme.colors.textColor,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    Text(
                        text = "统计时间 $dataDate",
                        fontSize = 12.sp,
                        color = AppTheme.colors.hintColor,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    color = AppTheme.colors.dividerColor,
                    thickness = 0.5.dp
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Row {
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "组件贡献分数",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = contributeScore.toDouble().formatDecimal(),
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "组件贡献排名",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = "$contributeRank",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "组件贡献等级",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = contributeClassStr,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "网络游戏分数",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = netGameScore.toDouble().formatDecimal(),
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "网络游戏排名",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = "$netGameRank",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = "网络游戏等级",
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                                Text(
                                    text = netGameClassStr,
                                    fontSize = 14.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MainUserCardPreview() {
    MainUserCard(
        "苦柠",
        "https://x19.fp.ps.netease.com/file/65967c76a72130654744d818gJc5iJKi05",
        3,
        2,
        "杰出精英 LV.2",
        5610101.0,
        561010.0,
        true,
        dataDate = "2024-5"
    )
}