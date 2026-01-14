package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.lt.compose_views.util.rememberMutableStateOf
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_dashboard
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExpandableNavigateItem(
    title: String,
    titleColor: Color = AppTheme.colors.textColor,
    titleWeight: FontWeight = FontWeight.Normal,
    icon: Any,
    iconModifier: Modifier = Modifier,
    isTinted: Boolean = true,
    expanded: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected && expanded) AppTheme.colors.primarySubColor.copy(alpha = 0.12f)
                else Color.Transparent
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (icon is DrawableResource) {
                Icon(
                    painterResource(icon),
                    contentDescription = title,
                    tint = if (isTinted) {
                        if (selected) AppTheme.colors.primaryColor
                        else AppTheme.colors.hintColor
                    } else Color.Transparent,
                    modifier = iconModifier.then(
                        Modifier.requiredSize(28.dp)
                    )
                )
            } else {
                CoilImage(
                    imageModel = { icon },
                    modifier = iconModifier.then(
                        Modifier.requiredSize(28.dp)
                    ),
                    imageOptions = if (isTinted) ImageOptions(
                        colorFilter = if (selected) ColorFilter.tint(AppTheme.colors.primaryColor)
                        else ColorFilter.tint(AppTheme.colors.hintColor),
                        requestSize = IntSize(100, 100)
                    ) else ImageOptions()
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally(),
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    color = if (selected) AppTheme.colors.primaryColor else titleColor,
                    maxLines = 1,
                    softWrap = false,
                    fontSize = 20.sp,
                    fontWeight = titleWeight
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun ExpandableNavigateItemPreview() {
    MCDevManagerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.background)) {
            var isExpanded by rememberMutableStateOf { false }
            ExpandableNavigateItem(
                title = "Expandable Item",
                icon = Res.drawable.ic_dashboard,
                selected = true,
                expanded = isExpanded,
                onClick = { isExpanded = !isExpanded })
        }
    }
}