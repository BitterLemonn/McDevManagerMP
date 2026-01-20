package com.lemon.mcdevmanagermp.ui.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.lemon.mcdevmanagermp.data.page.RankCategoryContent
import com.lemon.mcdevmanagermp.data.page.RankCategoryData
import com.lemon.mcdevmanagermp.data.page.RankCategoryTypeEnum
import com.lemon.mcdevmanagermp.data.page.RankGroupData
import com.lemon.mcdevmanagermp.data.page.RankListItemData
import com.lemon.mcdevmanagermp.data.page.RankSubCategoryTypeEnum
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.theme.MCDevManagerTheme
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.down_image
import mcdevmanagermp.composeapp.generated.resources.ic_refresh
import mcdevmanagermp.composeapp.generated.resources.normal_image
import mcdevmanagermp.composeapp.generated.resources.up_image
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs

// 模拟数据源
private val mockData = listOf(
    RankCategoryData(
        categoryTitle = "付费榜",
        content = RankCategoryContent.Multi(
            groups = listOf(
                RankGroupData(
                    categoryName = "模组",
                    data = listOf(
                        RankListItemData("魔法金属", 1, rankChange = 2, imgUrl = ""),
                        RankListItemData("暮色森林", 2, rankChange = -1, imgUrl = ""),
                        RankListItemData("工业时代2", 3, rankChange = 0, imgUrl = ""),
                        RankListItemData("神秘时代", 4, rankChange = 3, imgUrl = ""),
                        RankListItemData("末影工艺", 5, rankChange = -2, imgUrl = "")
                    )
                ),
                RankGroupData(
                    categoryName = "地图",
                    data = listOf(
                        RankListItemData("跑酷天堂", 1, 1),
                        RankListItemData("空岛生存", 2, 2)
                    )
                )
            )
        )
    ),
    RankCategoryData(
        categoryTitle = "热搜榜",
        content = RankCategoryContent.Single(
            listOf(
                RankListItemData("高清材质包", 1, 1),
                RankListItemData("光影核心", 2, 2),
                RankListItemData("解密：古堡", 3),
                RankListItemData("RPG：勇者", 4),
                RankListItemData("恐怖医院", 5)
            )
        )
    )
)

@Composable
fun MultiLevelRankingCard(
    data: List<RankCategoryData> = mockData,
    onChange: (RankCategoryTypeEnum, RankSubCategoryTypeEnum?) -> Unit = { _, _ -> }
) {
    if (data.isEmpty()) return

    // 选中的主分类标题
    var selectedCategoryTitle by remember { mutableStateOf(data.first().categoryTitle) }

    // 选中的主分类 (从当前 data 中获取最新的对象)
    val selectedCategory = remember(data, selectedCategoryTitle) {
        data.find { it.categoryTitle == selectedCategoryTitle } ?: data.first()
    }

    // 选中的二级分类名称
    // 初始值为空，会在 LaunchedEffect 中根据 selectedCategory 自动设置
    var selectedSubCategoryName by remember { mutableStateOf("") }

    // 是否展示全部
    var isShowAll by remember { mutableStateOf(false) }

    // 获取当前分类下的内容
    val currentContent = selectedCategory.content

    // 计算当前需要显示的二级菜单列表
    // 这里的 remember key 使用 selectedCategory，当主分类变化时重新计算
    val subTabTitles = remember(selectedCategory) {
        when (currentContent) {
            is RankCategoryContent.Multi -> currentContent.groups.map { it.categoryName }
            is RankCategoryContent.Single -> emptyList()
        }
    }

    // 计算当前需要显示的列表数据 (List<RankListItemData>)
    // key 加入 selectedSubCategoryName，当切换二级菜单时重新计算
    val currentList = remember(selectedCategory, selectedSubCategoryName) {
        when (currentContent) {
            is RankCategoryContent.Single -> currentContent.list
            is RankCategoryContent.Multi -> {
                // 在 groups 中找到名字匹配的那个 group，如果没找到默认取第一个
                val group =
                    currentContent.groups.find { it.categoryName == selectedSubCategoryName }
                        ?: currentContent.groups.firstOrNull()
                group?.data ?: emptyList()
            }
        }
    }

    // 当切换主分类时：重置二级菜单选中项、重置展开状态
    LaunchedEffect(selectedCategory.categoryTitle) {
        isShowAll = false
        selectedSubCategoryName = if (currentContent is RankCategoryContent.Multi)
            currentContent.groups.firstOrNull()?.categoryName ?: "" else ""
    }

    val triggerFetch = {
        onChange(
            when (selectedCategory.categoryTitle) {
                RankCategoryTypeEnum.PE_HOT.typeName -> RankCategoryTypeEnum.PE_HOT
                RankCategoryTypeEnum.HOT_SEARCH.typeName -> RankCategoryTypeEnum.HOT_SEARCH
                RankCategoryTypeEnum.PE_DOWNLOAD.typeName -> RankCategoryTypeEnum.PE_DOWNLOAD
                RankCategoryTypeEnum.PE_SELL.typeName -> RankCategoryTypeEnum.PE_SELL
                RankCategoryTypeEnum.PC_DOWNLOAD.typeName -> RankCategoryTypeEnum.PC_DOWNLOAD
                RankCategoryTypeEnum.PC_LIKE.typeName -> RankCategoryTypeEnum.PC_LIKE
                else -> RankCategoryTypeEnum.PE_HOT
            },
            if (selectedSubCategoryName.isNotEmpty()) {
                when (selectedSubCategoryName) {
                    RankSubCategoryTypeEnum.MOD.typeName -> RankSubCategoryTypeEnum.MOD
                    RankSubCategoryTypeEnum.MAP.typeName -> RankSubCategoryTypeEnum.MAP
                    RankSubCategoryTypeEnum.RESOURCE_PACK.typeName -> RankSubCategoryTypeEnum.RESOURCE_PACK
                    RankSubCategoryTypeEnum.SERVER.typeName -> RankSubCategoryTypeEnum.SERVER
                    else -> null
                }
            } else null
        )
    }

    // 切换分类时请求数据
    LaunchedEffect(selectedCategory.categoryTitle, selectedSubCategoryName) {
        if (currentList.isEmpty()) {
            triggerFetch()
        }
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = AppTheme.colors.card
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 1. 顶部一级菜单 (Main Tabs)
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val selectedIndex =
                    data.find { it.categoryTitle == selectedCategory.categoryTitle }?.let {
                        data.indexOf(it)
                    } ?: 0

                PrimaryScrollableTabRow(
                    modifier = Modifier.weight(1f),
                    edgePadding = 0.dp,
                    selectedTabIndex = if (selectedIndex >= 0) selectedIndex else 0,
                    containerColor = AppTheme.colors.card,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedIndex)
                        )
                    }
                ) {
                    data.forEach { item ->
                        Tab(
                            selected = selectedCategory == item,
                            onClick = { selectedCategoryTitle = item.categoryTitle },
                            text = {
                                Text(
                                    text = item.categoryTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 二级菜单
            AnimatedVisibility(
                visible = subTabTitles.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(subTabTitles) { title ->
                            FilterChip(
                                selected = selectedSubCategoryName == title,
                                onClick = { selectedSubCategoryName = title },
                                label = {
                                    Text(
                                        text = title,
                                        color = AppTheme.colors.textColor,
                                        modifier = Modifier.background(Color.Transparent)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = AppTheme.colors.card,
                                    selectedContainerColor = AppTheme.colors.primarySubColor.copy(
                                        alpha = 0.2f
                                    )
                                )
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(onClick = { triggerFetch() }) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_refresh),
                                    contentDescription = "Refresh",
                                    modifier = Modifier.size(24.dp),
                                    tint = AppTheme.colors.primaryColor
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            AnimatedContent(
                targetState = currentList,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                },
                label = "ListAnimation"
            ) { listData ->
                // 计算实际显示的列表（处理折叠逻辑）
                val displayList =
                    if (!isShowAll && listData.size > 3) listData.take(3) else listData

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .scrollable(
                            state = rememberScrollState(),
                            orientation = Orientation.Vertical
                        )
                        .animateContentSize(),
                ) {
                    if (listData.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("暂无数据", color = AppTheme.colors.textColor)
                            }
                        }
                    } else {
                        items(displayList) { item ->
                            RankingListItem(item) // 你的列表项组件

                            // 最后一项不显示分割线
                            if (item != displayList.last()) {
                                HorizontalDivider(
                                    color = AppTheme.colors.dividerColor.copy(alpha = 0.5f),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }

            // 查看全部按钮
            val showExpandButton = currentList.size > 3
            AnimatedVisibility(
                visible = !isShowAll && showExpandButton
            ) {
                TextButton(
                    onClick = { isShowAll = true },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text("查看全部榜单")
                }
            }
        }
    }
}

@Composable
private fun RankingListItem(item: RankListItemData) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 排名数字
        Text(
            text = "${item.rank}",
            style = MaterialTheme.typography.titleLarge,
            color = when (item.rank) {
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                3 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp)
        )

        // 模组图片
        if (item.imgUrl != null) {
            Spacer(modifier = Modifier.width(8.dp))
            // 如果有图片 URL，则显示图片
            Box(
                modifier = Modifier.size(40.dp).background(
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    RoundedCornerShape(4.dp)
                )
            ) {
                CoilImage(
                    imageModel = { item.imgUrl },
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp)),
                    imageOptions = ImageOptions(
                        requestSize = IntSize(80, 80)
                    )
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
        }

        // 内容信息
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.textColor
        )

        Spacer(Modifier.weight(1f))


        // 变化情况
        if (!item.isNew) {
            if (item.rankChange != 0) {
                Text(
                    text = "${abs(item.rankChange)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (item.rankChange > 0) AppTheme.colors.primaryColor
                    else AppTheme.colors.secondaryColor,
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // 尾部图标/数据
            Icon(
                painter = painterResource(
                    if (item.rankChange > 0) Res.drawable.up_image
                    else if (item.rankChange < 0) Res.drawable.down_image
                    else Res.drawable.normal_image
                ),
                contentDescription = null,
                tint = if (item.rankChange > 0) AppTheme.colors.primaryColor
                else if (item.rankChange < 0) AppTheme.colors.secondaryColor
                else AppTheme.colors.hintColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            // 新入榜
            Text(
                text = "NEW",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontStyle = FontStyle.Italic
                ),
                color = AppTheme.colors.primaryColor,
            )
        }
    }
}

@Preview
@Composable
private fun RankingCardPreview() {
    MCDevManagerTheme {
        Box(modifier = Modifier.padding(top = 20.dp)) {
            MultiLevelRankingCard(mockData)
        }
    }
}

@Preview
@Composable
private fun RankingListItemPreview() {
    MCDevManagerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.card).padding(16.dp)) {
            RankingListItem(
                RankListItemData(
                    title = "魔法金属", rank = 1, isNew = true
                )
            )
        }
    }
}