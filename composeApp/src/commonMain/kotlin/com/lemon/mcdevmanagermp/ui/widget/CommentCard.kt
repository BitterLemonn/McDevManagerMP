package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lemon.mcdevmanagermp.utils.formatTime
import com.lt.compose_views.other.HorizontalSpace
import com.lt.compose_views.other.VerticalSpace
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_comment
import mcdevmanagermp.composeapp.generated.resources.ic_mod
import mcdevmanagermp.composeapp.generated.resources.ic_star
import mcdevmanagermp.composeapp.generated.resources.ic_user
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock

@Composable
fun CommentCard(
    modifier: Modifier = Modifier,
    resName: String = "",
    iid: String = "",
    tag: String = "",
    comment: String = "",
    nickname: String = "",
    stars: Int = 0,
    publishTime: Long = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.ic_mod),
                    contentDescription = "",
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                )
                HorizontalSpace(dp = 8.dp)
                Column {
                    Text(
                        text = resName, color = AppTheme.colors.textColor, fontSize = 14.sp
                    )
                    HorizontalSpace(dp = 8.dp)

                    Text(
                        text = iid, color = AppTheme.colors.hintColor, fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = tag, color = AppTheme.colors.hintColor, fontSize = 12.sp
                    )
                    HorizontalSpace(dp = 8.dp)
                    Text(
                        text = formatTime(publishTime * 1000),
                        color = AppTheme.colors.hintColor,
                        fontSize = 12.sp
                    )
                }
            }

            VerticalSpace(dp = 8.dp)
            if (comment.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.ic_comment),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                    )
                    HorizontalSpace(dp = 8.dp)
                    Text(
                        text = nickname, color = AppTheme.colors.textColor, fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    LazyRow {
                        items(stars) {
                            Image(
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = "",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                            )
                        }
                    }
                }
                VerticalSpace(dp = 8.dp)
                Text(
                    text = comment, color = AppTheme.colors.hintColor, fontSize = 14.sp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.ic_user),
                        contentDescription = "用户",
                        modifier = Modifier.size(18.dp),
                        colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                    )
                    HorizontalSpace(dp = 8.dp)
                    Text(
                        text = nickname, color = AppTheme.colors.textColor, fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    LazyRow {
                        items(stars) {
                            Image(
                                painter = painterResource(Res.drawable.ic_star),
                                contentDescription = "",
                                modifier = Modifier.size(18.dp),
                                colorFilter = ColorFilter.tint(AppTheme.colors.primaryColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CommentCardPreview() {
    MCDevManagerTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(AppTheme.colors.background)
        ) {
            CommentCard(
                resName = "苦柠的奇异饰品",
                iid = "4668241759157945097",
                tag = "默认类型",
                comment = "",
                nickname = "nickname",
                stars = 5,
                publishTime = (Clock.System.now().nanosecondsOfSecond / 1000).toLong()
            )
        }
    }
}