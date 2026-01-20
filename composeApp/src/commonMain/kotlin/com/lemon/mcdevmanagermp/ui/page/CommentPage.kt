package com.lemon.mcdevmanagermp.ui.page

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lemon.mcdevmanagermp.data.Screen
import com.lemon.mcdevmanagermp.data.netease.comment.CommentBean
import com.lemon.mcdevmanagermp.ui.theme.AppTheme
import com.lemon.mcdevmanagermp.ui.widget.LoginOutlineTextField
import com.lemon.mcdevmanagermp.ui.widget.SNACK_ERROR
import com.lemon.mcdevmanagermp.ui.widget.wide.DateRangeFilterChip
import com.lemon.mcdevmanagermp.ui.widget.wide.MultiSelectDropdownChip
import com.lemon.mcdevmanagermp.ui.widget.wide.SingleSelectDropdownChip
import com.lemon.mcdevmanagermp.utils.formatDateShort
import com.lemon.mcdevmanagermp.utils.formatTime
import com.lemon.mcdevmanagermp.viewmodel.CommentViewActions
import com.lemon.mcdevmanagermp.viewmodel.CommentViewEffects
import com.lemon.mcdevmanagermp.viewmodel.CommentViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import mcdevmanagermp.composeapp.generated.resources.Res
import mcdevmanagermp.composeapp.generated.resources.ic_back
import mcdevmanagermp.composeapp.generated.resources.ic_close
import mcdevmanagermp.composeapp.generated.resources.ic_comment
import mcdevmanagermp.composeapp.generated.resources.ic_filter
import mcdevmanagermp.composeapp.generated.resources.ic_mod
import mcdevmanagermp.composeapp.generated.resources.ic_star
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Clock

@Composable
fun CommentPage(
    navController: NavController = rememberNavController(),
    viewModel: CommentViewModel = viewModel { CommentViewModel() },
    showToast: (String, String) -> Unit = { _, _ -> },
) {
    val viewStates by viewModel.viewStates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.dispatch(CommentViewActions.LoadComments)
    }

    BasePage(
        viewEffect = viewModel.viewEffects,
        onEffect = { effect ->
            when (effect) {
                is CommentViewEffects.LoadingData -> {}
                is CommentViewEffects.LoadDataSuccess -> {}
                is CommentViewEffects.RouteToPath -> {
                    navController.navigate(effect.path) {
                        if (effect.needPop) popUpTo(Screen.CommentPage) { inclusive = true }
                        launchSingleTop = true
                    }
                }

                is CommentViewEffects.LoadDataFailed -> {
                    showToast(effect.errorMsg, SNACK_ERROR)
                }
            }
        }
    ) {
        CommentPageContain(
            comments = viewStates.commentList,
            onReplySubmit = { commentId, replyText ->

            },
            onNeedLoadMore = {
                if (!viewStates.isLoadOver) viewModel.dispatch(CommentViewActions.LoadComments)
            }
        )
    }
}

@Composable
private fun LazyStaggeredGridState.OnBottomReached(
    buffer: Int = 5,
    onLoadMore: () -> Unit
) {
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf false

            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .filter { it }
            .collect { onLoadMore() }
    }
}

// --- 主屏幕 ---
@Composable
private fun CommentPageContain(
    comments: List<CommentBean>,
    onReplySubmit: (String, String) -> Unit,
    onNeedLoadMore: () -> Unit = { },
    onFilterChange: (CommentFilter) -> Unit = { }  // 新增回调
) {
    var selectedCommentId by remember { mutableStateOf<String?>(null) }
    var currentFilter by remember { mutableStateOf(CommentFilter()) }

    val listState = rememberLazyStaggeredGridState()
    listState.OnBottomReached { onNeedLoadMore() }
    val coroutineScope = rememberCoroutineScope()

    var activeComment by remember { mutableStateOf<CommentBean?>(null) }

    val selectedComment = remember(selectedCommentId, comments) {
        comments.find { it.id == selectedCommentId }
    }

    LaunchedEffect(selectedComment) {
        if (selectedComment != null) {
            activeComment = selectedComment
        }
    }

    val isExpanded = selectedCommentId != null

    val transition = updateTransition(targetState = isExpanded, label = "panel")

    val listWidthFraction by transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "listWidth"
    ) { if (it) 0.4f else 1f }

    val detailOffsetX by transition.animateFloat(
        transitionSpec = { spring(stiffness = Spring.StiffnessLow) },
        label = "offset"
    ) { if (it) 0f else 1f }

    val detailAlpha by transition.animateFloat(
        transitionSpec = { tween(250) },
        label = "alpha"
    ) { if (it) 1f else 0f }

    val showPanel = transition.currentState || transition.targetState

    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.currentState && !transition.targetState) {
            activeComment = null
        }
    }

    var isPanelPreloaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        isPanelPreloaded = true
    }

    Scaffold(containerColor = AppTheme.colors.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 筛选栏
            CommentFilterBar(
                comments = comments,
                currentFilter = currentFilter,
                onFilterChange = { filter ->
                    currentFilter = filter
                    onFilterChange(filter)  // 回调给外部
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 原有内容
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                val containerWidth = maxWidth
                val detailWidth = containerWidth * 0.6f

                // 预渲染隐藏的面板
                if (isPanelPreloaded && activeComment == null && comments.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .width(detailWidth)
                            .fillMaxHeight()
                            .offset(x = containerWidth)
                            .alpha(0f)
                    ) {
                        CommentDetailPanel(
                            comment = comments.first(),
                            onClose = {},
                            onReplySubmit = {}
                        )
                    }
                }

                // 列表
                Box(
                    modifier = Modifier
                        .width(containerWidth * listWidthFraction)
                        .fillMaxHeight()
                ) {
                    CommentListArea(
                        comments = comments,
                        selectedId = selectedCommentId,
                        listState = listState,
                        onSelect = { commentId ->
                            val prev = selectedCommentId
                            selectedCommentId = commentId
                            if (prev != null) {
                                coroutineScope.launch {
                                    val idx = comments.indexOfFirst { it.id == commentId }
                                    if (idx >= 0) listState.animateScrollToItem(idx)
                                }
                            }
                        }
                    )
                }

                // 详情面板
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(detailWidth)
                        .fillMaxHeight()
                        .graphicsLayer {
                            translationX = detailWidth.toPx() * detailOffsetX
                            alpha = detailAlpha
                        }
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                        .border(
                            BorderStroke(1.dp, AppTheme.colors.hintColor.copy(0.2f)),
                            shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                        )
                ) {
                    if (showPanel && activeComment != null) {
                        key(activeComment!!.id) {
                            CommentDetailPanel(
                                comment = activeComment!!,
                                onClose = { selectedCommentId = null },
                                onReplySubmit = { text -> onReplySubmit(activeComment!!.id, text) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 左侧列表组件 ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CommentListArea(
    comments: List<CommentBean>,
    selectedId: String?,
    listState: LazyStaggeredGridState,
    onSelect: (String) -> Unit
) {
    val columns = if (selectedId == null) 2 else 1

    LazyVerticalStaggeredGrid(
        state = listState,
        columns = StaggeredGridCells.Fixed(columns),
        verticalItemSpacing = 12.dp,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = comments, key = { it.id }) { comment ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                CommentSummaryCard(
                    comment = comment,
                    isSelected = comment.id == selectedId,
                    isCompact = selectedId != null,
                    onClick = { onSelect(comment.id) }
                )
            }
        }
    }
}

// --- 列表卡片 (摘要模式) ---
@Composable
private fun CommentSummaryCard(
    comment: CommentBean, isSelected: Boolean, isCompact: Boolean, onClick: () -> Unit
) {
    val containerColor = if (isSelected) AppTheme.colors.primaryColor.copy(alpha = 0.2f)
    else AppTheme.colors.card

    Card(
        modifier = Modifier.fillMaxWidth().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.nickname,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppTheme.colors.textColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatTime(comment.publishTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppTheme.colors.hintColor
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = comment.userComment,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = AppTheme.colors.textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusChip(
                    label = comment.resName,
                    icon = Res.drawable.ic_mod,
                    isCompact = isCompact,
                    color = containerColor
                )
                StatusChip(
                    label = comment.commentTag,
                    icon = Res.drawable.ic_comment,
                    isCompact = isCompact,
                    color = containerColor
                )
            }
        }
    }
}

// --- 右侧详情与回复面板 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentDetailPanel(
    comment: CommentBean, onClose: () -> Unit, onReplySubmit: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "详情回复",
                            style = MaterialTheme.typography.titleMedium,
                            color = AppTheme.colors.textColor
                        )
                        Text(
                            "${comment.resName} ${comment.iid}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AppTheme.colors.hintColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_back),
                            contentDescription = "Close",
                            modifier = Modifier.size(32.dp),
                            tint = AppTheme.colors.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppTheme.colors.background)
            )
        }, bottomBar = {
            // 底部固定回复栏
            ReplyInputBar(onReplySubmit = onReplySubmit)
        }, containerColor = AppTheme.colors.card
    ) { padding ->
        // 详情内容可滚动
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 用户卡片
            DetailUserHeader(comment)

            Spacer(modifier = Modifier.height(24.dp))

            // 评论内容
            SelectionContainer {
                Text(
                    text = comment.userComment,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
                    color = AppTheme.colors.textColor
                )
            }

            // 底部留白，防止内容被输入框遮挡
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- 详情页头部组件 ---
@Composable
private fun DetailUserHeader(comment: CommentBean) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = comment.nickname,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = comment.uid,
                style = MaterialTheme.typography.titleMedium,
                color = AppTheme.colors.hintColor
            )
        }
        Text(
            text = formatTime(comment.publishTime),
            style = MaterialTheme.typography.bodySmall,
            color = AppTheme.colors.hintColor
        )
    }
}

// --- 底部回复输入栏 ---
@Composable
private fun ReplyInputBar(
    onReplySubmit: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
        color = AppTheme.colors.card
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LoginOutlineTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("回复") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                FilledTonalButton(
                    onClick = {
                        if (text.isNotBlank()) {
                            onReplySubmit(text)
                            text = ""
                        }
                    }, enabled = text.isNotBlank(), colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primaryColor, contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_comment),
                        contentDescription = "send_reply",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("发送")
                }
            }
        }
    }
}


/**
 * 筛选条件
 */
data class CommentFilter(
    val commentTags: Set<String> = emptySet(),
    val resName: String? = null,  // 单选
    val stars: Set<String> = emptySet(),
    val startTime: Long? = null,  // 开始时间戳（毫秒）
    val endTime: Long? = null     // 结束时间戳（毫秒）
) {
    fun isActive(): Boolean =
        commentTags.isNotEmpty() ||
                resName != null ||
                stars.isNotEmpty() ||
                startTime != null ||
                endTime != null

    fun totalSelectedCount(): Int {
        var count = commentTags.size + stars.size
        if (resName != null) count++
        if (startTime != null) count++
        if (endTime != null) count++
        return count
    }

    fun hasTimeFilter(): Boolean = startTime != null || endTime != null
}

// --- 筛选栏组件 ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentFilterBar(
    comments: List<CommentBean>,
    currentFilter: CommentFilter,
    onFilterChange: (CommentFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    // 从评论列表提取可选项
    val availableCommentTags = remember(comments) {
        comments.map { it.commentTag }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val availableResNames = remember(comments) {
        comments.map { it.resName }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val availableStars = remember(comments) {
        comments.map { it.stars }.filter { it.isNotBlank() }.distinct().sortedDescending()
    }

    Column(modifier = modifier) {
        // 主筛选栏
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = AppTheme.colors.card,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 筛选图标
                Icon(
                    painter = painterResource(Res.drawable.ic_filter),
                    contentDescription = "筛选",
                    modifier = Modifier.size(20.dp),
                    tint = if (currentFilter.isActive()) AppTheme.colors.primaryColor
                    else AppTheme.colors.hintColor
                )

                Text(
                    text = "筛选",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.colors.textColor
                )

                VerticalDivider(
                    modifier = Modifier.height(24.dp),
                    color = AppTheme.colors.hintColor.copy(alpha = 0.3f)
                )

                // 评论类型多选
                MultiSelectDropdownChip(
                    label = "评论类型",
                    icon = Res.drawable.ic_comment,
                    options = availableCommentTags,
                    selectedOptions = currentFilter.commentTags,
                    onSelectionChanged = { newSelection ->
                        onFilterChange(currentFilter.copy(commentTags = newSelection))
                    }
                )

                // 资源单选
                SingleSelectDropdownChip(
                    label = "资源",
                    icon = Res.drawable.ic_mod,
                    options = availableResNames,
                    selectedOption = currentFilter.resName,
                    onSelectionChanged = { newSelection ->
                        onFilterChange(currentFilter.copy(resName = newSelection))
                    }
                )

                // 评分多选
                MultiSelectDropdownChip(
                    label = "评分",
                    icon = Res.drawable.ic_star,
                    options = availableStars,
                    selectedOptions = currentFilter.stars,
                    onSelectionChanged = { newSelection ->
                        onFilterChange(currentFilter.copy(stars = newSelection))
                    },
                    optionDisplayText = { it }
                )

                VerticalDivider(
                    modifier = Modifier.height(24.dp),
                    color = AppTheme.colors.hintColor.copy(alpha = 0.3f)
                )

                // 时间范围筛选
                DateRangeFilterChip(
                    startTime = currentFilter.startTime,
                    endTime = currentFilter.endTime,
                    onTimeRangeChanged = { start, end ->
                        onFilterChange(currentFilter.copy(startTime = start, endTime = end))
                    }
                )

                // 清除按钮
                AnimatedVisibility(
                    visible = currentFilter.isActive(),
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        VerticalDivider(
                            modifier = Modifier.height(24.dp),
                            color = AppTheme.colors.hintColor.copy(alpha = 0.3f)
                        )
                        AssistChip(
                            onClick = { onFilterChange(CommentFilter()) },
                            label = { Text("清除全部") },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_close),
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = AppTheme.colors.card,
                                labelColor = AppTheme.colors.error,
                                leadingIconContentColor = AppTheme.colors.error
                            ),
                            border = BorderStroke(1.dp, AppTheme.colors.error)
                        )
                    }
                }
            }
        }

        // 已选标签快速移除
        AnimatedVisibility(
            visible = currentFilter.isActive(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SelectedFilterTags(
                filter = currentFilter,
                onRemoveTag = { filterType, value ->
                    val newFilter = when (filterType) {
                        FilterType.COMMENT_TAG -> currentFilter.copy(
                            commentTags = currentFilter.commentTags - value
                        )

                        FilterType.RES_NAME -> currentFilter.copy(resName = null)
                        FilterType.STARS -> currentFilter.copy(
                            stars = currentFilter.stars - value
                        )

                        FilterType.START_TIME -> currentFilter.copy(startTime = null)
                        FilterType.END_TIME -> currentFilter.copy(endTime = null)
                    }
                    onFilterChange(newFilter)
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private enum class FilterType {
    COMMENT_TAG, RES_NAME, STARS, START_TIME, END_TIME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedFilterTags(
    filter: CommentFilter,
    onRemoveTag: (FilterType, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "已筛选:",
            style = MaterialTheme.typography.labelMedium,
            color = AppTheme.colors.hintColor
        )

        // 评论类型标签
        filter.commentTags.forEach { tag ->
            RemovableTag(
                text = tag,
                containerColor = AppTheme.colors.primaryColor.copy(0.2f),
                contentColor = AppTheme.colors.primaryColor,
                onRemove = { onRemoveTag(FilterType.COMMENT_TAG, tag) }
            )
        }

        // 资源标签（单选）
        filter.resName?.let { name ->
            RemovableTag(
                text = name,
                containerColor = AppTheme.colors.primaryColor.copy(0.2f),
                contentColor = AppTheme.colors.primaryColor,
                onRemove = { onRemoveTag(FilterType.RES_NAME, name) }
            )
        }

        // 评分标签
        filter.stars.forEach { star ->
            RemovableTag(
                text = "⭐ $star",
                containerColor = AppTheme.colors.primaryColor.copy(0.2f),
                contentColor = AppTheme.colors.primaryColor,
                onRemove = { onRemoveTag(FilterType.STARS, star) }
            )
        }

        // 时间标签
        filter.startTime?.let { time ->
            RemovableTag(
                text = "从 ${formatDateShort(time)}",
                containerColor = AppTheme.colors.primaryColor.copy(0.2f),
                contentColor = AppTheme.colors.primaryColor,
                onRemove = { onRemoveTag(FilterType.START_TIME, "") }
            )
        }

        filter.endTime?.let { time ->
            RemovableTag(
                text = "至 ${formatDateShort(time)}",
                containerColor = AppTheme.colors.primaryColor.copy(0.2f),
                contentColor = AppTheme.colors.primaryColor,
                onRemove = { onRemoveTag(FilterType.END_TIME, "") }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RemovableTag(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onRemove: () -> Unit
) {
    InputChip(
        selected = true,
        onClick = onRemove,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        },
        trailingIcon = {
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = "移除",
                modifier = Modifier.size(14.dp)
            )
        },
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = containerColor,
            selectedLabelColor = contentColor,
            selectedTrailingIconColor = contentColor
        ),
        border = BorderStroke(1.dp, contentColor),
        modifier = Modifier.height(28.dp)
    )
}

// --- 标签 ---
@Composable
private fun StatusChip(
    label: String,
    icon: DrawableResource,
    isCompact: Boolean = false,
    color: Color = AppTheme.colors.card
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, AppTheme.colors.hintColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isCompact) 6.dp else 8.dp, vertical = if (isCompact) 2.dp else 4.dp
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(if (isCompact) 10.dp else 14.dp),
                tint = AppTheme.colors.textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                color = AppTheme.colors.textColor.copy(alpha = 0.7f)
            )
        }
    }
}

// --- 动画包装器，避免 RowScope 作用域冲突 ---
@Composable
private fun DetailPanelAnimationWrapper(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        content()
    }
}

@Preview(widthDp = 1200, heightDp = 800)
@Composable
private fun CommentReviewScreenPreview() {
    val sampleComments = List(20) { index -> // 增加数量以测试滚动
        CommentBean(
            id = "cmt_$index",
            nickname = "用户$index",
            resName = "模块${index % 3}",
            iid = "mod_${index % 3}",
            userComment = "这是用户$index 的评论内容。".repeat((index % 4) + 1),
            publishTime = (Clock.System.now().toEpochMilliseconds() - index * 86400000L) / 1000,
            commentTag = if (index % 2 == 0) "反馈" else "建议",
            stars = "5",
            uid = "user_$index"
        )
    }

    CommentPageContain(
        comments = sampleComments, onReplySubmit = { commentId, replyText ->
            println("回复评论 $commentId: $replyText")
        })
}