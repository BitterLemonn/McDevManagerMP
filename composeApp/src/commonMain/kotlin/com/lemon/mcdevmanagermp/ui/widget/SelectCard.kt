package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.TextWhite
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_bar_chart
import mcdevmanagermp.composeapp.generated.resources.ic_line_chart
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SelectTextCard(
    modifier: Modifier = Modifier,
    isSelectLeft: Boolean = true,
    leftName: String,
    rightName: String,
    onSelectChange: (Boolean) -> Unit = {}
) {
    val isSelectLeft by rememberUpdatedState(isSelectLeft)
    SelectCard(
        modifier = modifier,
        isSelectLeft = isSelectLeft,
        leftContain = {
            Text(
                text = leftName,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center),
                color = if (isSelectLeft) TextWhite else AppTheme.colors.textColor
            )
        },
        rightContain = {
            Text(
                text = rightName,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Center),
                color = if (isSelectLeft) AppTheme.colors.textColor else TextWhite
            )
        },
        onSelectChange = onSelectChange
    )
}


@Composable
fun SelectCard(
    modifier: Modifier = Modifier,
    isSelectLeft: Boolean = true,
    leftContain: @Composable BoxScope.() -> Unit,
    rightContain: @Composable BoxScope.() -> Unit,
    onSelectChange: (Boolean) -> Unit = {}
) {
    // 动画：计算滑块的水平偏移量 (Bias)
    val alignmentBias by animateFloatAsState(
        targetValue = if (isSelectLeft) -1f else 1f,
        label = "SelectionAnimation"
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .then(modifier),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.card
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .align(BiasAlignment(horizontalBias = alignmentBias, verticalBias = 0f))
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppTheme.colors.primaryColor)
            )

            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = !isSelectLeft
                        ) { onSelectChange(true) },
                    contentAlignment = Alignment.Center
                ) {
                    leftContain()
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = isSelectLeft
                        ) { onSelectChange(false) },
                    contentAlignment = Alignment.Center
                ) {
                    rightContain()
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SelectCardPreview() {
    val isSelectLeft by rememberUpdatedState(false)
    SelectCard(
        modifier = Modifier.width(120.dp),
        isSelectLeft = isSelectLeft,
        leftContain = {
            Image(
                painter = painterResource(Res.drawable.ic_line_chart),
                contentDescription = "line chart",
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        },
        rightContain = {
            Image(
                painter = painterResource(Res.drawable.ic_bar_chart),
                contentDescription = "bar chart",
                modifier = Modifier
                    .size(36.dp)
                    .padding(4.dp)
                    .align(Alignment.Center)
            )
        },
        onSelectChange = {
            isSelectLeft != it
        }
    )
}