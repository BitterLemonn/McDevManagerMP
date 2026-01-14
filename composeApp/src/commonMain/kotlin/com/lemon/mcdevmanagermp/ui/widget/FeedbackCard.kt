package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import com.lemon.mcdevmanagermp.data.common.JSONConverter
import com.lemon.mcdevmanagermp.data.netease.feedback.ConflictModsBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.utils.Logger
import com.lemon.mcdevmanagermp.utils.formatTime
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_mod
import mcdevmanagermp.composeapp.generated.resources.ic_no_reply
import mcdevmanagermp.composeapp.generated.resources.ic_replied
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackCard(
    modifier: Modifier = Modifier,
    modName: String,
    modUid: String,
    createTime: Long,
    type: String,
    nickname: String,
    content: String,
    picList: List<String> = emptyList(),
    reply: String? = null,
    onClickImg: (String) -> Unit = {},
    isShowReply: Boolean = false,
    extraContent: @Composable () -> Unit = {}
) {
    var contentStr by remember { mutableStateOf(content) }

    LaunchedEffect(key1 = content) {
        if (content.startsWith("{\"item_list\"")) {
            try {
                val mods = JSONConverter.decodeFromString<ConflictModsBean>(content)
                contentStr = "冲突mod:\n${mods.itemList.joinToString { it.name }}"
                if (!mods.detail.isNullOrBlank()) {
                    contentStr += "\n\n冲突详情: ${mods.detail}"
                }
            } catch (e: Exception) {
                Logger.e("FeedbackCard: 解析冲突mod失败", e)
                contentStr = "解析冲突mod失败"
            }
        } else {
            contentStr = content
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).animateContentSize().then(modifier),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.card),
        shape = RoundedCornerShape(8.dp)
    ) {
        // 工具头
        Row(
            Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_mod),
                contentDescription = "mod icon",
                modifier = Modifier.padding(start = 16.dp).size(24.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.lighting(
                    add = Color.Transparent, multiply = AppTheme.colors.primaryColor
                )
            )
            Column(
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                BoxWithConstraints {
                    val fontScale = LocalDensity.current.fontScale
                    val isSpacious = (maxWidth.value / fontScale) > 300

                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = modName,
                            fontSize = 12.sp,
                            color = AppTheme.colors.textColor,
                            modifier = Modifier
                                .then(
                                    if (isSpacious) Modifier.width(Dp.Unspecified)
                                    else Modifier.width(100.dp)
                                ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )

                        if (isSpacious) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = modUid,
                                fontSize = 12.sp,
                                color = AppTheme.colors.hintColor
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                Modifier.align(Alignment.CenterVertically).padding(end = 8.dp)
            ) {
                Text(
                    text = formatTime(createTime),
                    fontSize = 12.sp,
                    color = AppTheme.colors.hintColor,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
                        .align(Alignment.End)
                )
                Text(
                    text = type,
                    fontSize = 12.sp,
                    color = AppTheme.colors.hintColor,
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp)
                        .align(Alignment.End)
                )
            }
            Image(
                painter = painterResource(if (!reply.isNullOrBlank()) Res.drawable.ic_no_reply else Res.drawable.ic_replied),
                contentDescription = "reply state",
                modifier = Modifier.padding(end = 16.dp).size(24.dp)
                    .align(Alignment.CenterVertically),
            )
        }

        Text(
            text = nickname,
            fontSize = 14.sp,
            color = AppTheme.colors.textColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Text(
            text = contentStr,
            fontSize = 16.sp,
            letterSpacing = 1.sp,
            color = AppTheme.colors.textColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        if (picList.isNotEmpty()) FlowRow(
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            picList.forEach {
                CoilImage(
                    imageModel = { it },
                    modifier = Modifier.padding(8.dp).wrapContentHeight().heightIn(max = 160.dp)
                        .wrapContentWidth().clip(RoundedCornerShape(8.dp)).clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {
                            onClickImg(it)
                        },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Fit
                    )
                )
            }
        }
        if (isShowReply) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                color = AppTheme.colors.dividerColor,
                thickness = 0.5.dp
            )
            if (!reply.isNullOrBlank()) Text(
                text = reply,
                fontSize = 16.sp,
                color = AppTheme.colors.textColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                letterSpacing = 1.sp
            )
            extraContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFeedbackCard() {
    MCDevManagerTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)
        ) {
            FeedbackCard(
                modName = "苦柠的奇异饰品",
                modUid = "4668241759157945097",
                createTime = 1716015309,
                nickname = "苦柠",
                type = "故障问题反馈",
                content = "123123123123123",
                reply = "2222222",
                isShowReply = true,
                extraContent = {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, AppTheme.colors.hintColor, RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) {}) {
                        Row(Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                                    .padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞赞",
                                    fontSize = 16.sp,
                                    color = AppTheme.colors.textColor,
                                    modifier = Modifier.padding(8.dp).align(Alignment.CenterStart)
                                )
                            }
                            Box(
                                modifier = Modifier.width(80.dp).padding(8.dp)
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(8.dp)).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple()
                                    ) { }) {
                                Text(
                                    text = "回复",
                                    fontSize = 16.sp,
                                    color = AppTheme.colors.primaryColor,
                                    modifier = Modifier.padding(8.dp).align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                })
        }
    }
}